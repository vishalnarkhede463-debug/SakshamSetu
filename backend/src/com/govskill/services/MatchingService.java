package com.govskill.services;

import com.govskill.models.Opportunity;
import com.govskill.models.Skill;
import com.govskill.models.StudentProfile;

import java.util.*;

public class MatchingService {

    /**
     * Calculates compatibility score [15% - 99%] matching student profile with opportunity.
     * Implements identical weighted formulation as cpp/matching_algorithm.cpp.
     */
    public static int calculateMatch(StudentProfile student, Opportunity opp) {
        if (student == null || opp == null) return 50;

        String securePayload = String.valueOf(student.getUserId()) + "|"
                + String.valueOf(student.getBranch()) + "|"
                + String.valueOf(student.getEducationLevel()) + "|"
                + String.valueOf(opp.getId());

        return TrustedExecutionEnvironment.execute("MATCHING", securePayload, () -> calculateMatchInternal(student, opp));
    }

    private static int calculateMatchInternal(StudentProfile student, Opportunity opp) {
        double skillScore = calculateSkillScore(student.getSkillsList(), opp.getSkills());
        double educationScore = calculateEducationScore(student.getEducationLevel(), opp.getEducationRequirement());
        double branchScore = calculateBranchScore(student.getBranch(), opp.getBranch());
        double locCatScore = calculateLocationAndCategoryScore(student, opp);

        double total = (skillScore * 0.40) +
                       (educationScore * 0.25) +
                       (branchScore * 0.20) +
                       (locCatScore * 0.15);

        total = Math.max(18.0, Math.min(98.0, Math.round(total)));
        return (int) total;
    }

    public static List<Opportunity> rankOpportunitiesForStudent(StudentProfile student, List<Opportunity> opportunities) {
        if (student == null) return opportunities;

        List<Opportunity> scoredList = new ArrayList<>();
        for (Opportunity o : opportunities) {
            int score = calculateMatch(student, o);
            o.setMatchScore(score);
            scoredList.add(o);
        }

        // Sort descending by match score
        scoredList.sort((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()));
        return scoredList;
    }

    private static double calculateSkillScore(List<Skill> studentSkills, String requiredSkillsCsv) {
        if (requiredSkillsCsv == null || requiredSkillsCsv.trim().isEmpty()) return 85.0;
        if (studentSkills == null || studentSkills.isEmpty()) return 25.0;

        List<String> reqTokens = tokenize(requiredSkillsCsv);
        if (reqTokens.isEmpty()) return 85.0;

        int matchCount = 0;
        for (String req : reqTokens) {
            for (Skill s : studentSkills) {
                if (s.getName() == null) continue;
                String sName = s.getName().trim().toLowerCase();
                if (req.equals(sName) || req.contains(sName) || sName.contains(req)) {
                    matchCount++;
                    break;
                }
            }
        }

        double ratio = (double) matchCount / reqTokens.size();
        return Math.min(100.0, ratio * 100.0);
    }

    private static double calculateEducationScore(String studentEdu, String oppEdu) {
        if (oppEdu == null || oppEdu.trim().isEmpty() || oppEdu.equalsIgnoreCase("any") || oppEdu.toLowerCase().contains("all")) {
            return 100.0;
        }
        if (studentEdu == null) return 30.0;

        String sEdu = studentEdu.toLowerCase();
        String oEdu = oppEdu.toLowerCase();

        if (sEdu.equals(oEdu) || sEdu.contains(oEdu) || oEdu.contains(sEdu)) {
            return 100.0;
        }

        int sRank = getEducationRank(sEdu);
        int oRank = getEducationRank(oEdu);

        if (sRank >= oRank) return 90.0;
        if (sRank == oRank - 1) return 55.0;
        return 25.0;
    }

    private static int getEducationRank(String edu) {
        if (edu.contains("postgraduate") || edu.contains("m.tech") || edu.contains("mca") || edu.contains("m.sc")) return 5;
        if (edu.contains("undergraduate") || edu.contains("b.tech") || edu.contains("b.e") || edu.contains("bca") || edu.contains("degree")) return 4;
        if (edu.contains("diploma") || edu.contains("iti")) return 3;
        if (edu.contains("12th") || edu.contains("hsc")) return 2;
        if (edu.contains("10th") || edu.contains("ssc")) return 1;
        return 2;
    }

    private static double calculateBranchScore(String studentBranch, String oppBranch) {
        if (oppBranch == null || oppBranch.trim().isEmpty() || oppBranch.equalsIgnoreCase("all") || oppBranch.toLowerCase().contains("any")) {
            return 100.0;
        }
        if (studentBranch == null) return 30.0;

        String sB = studentBranch.toLowerCase();
        String oB = oppBranch.toLowerCase();

        if (sB.equals(oB) || sB.contains(oB) || oB.contains(sB)) {
            return 100.0;
        }

        boolean sIT = sB.contains("computer") || sB.contains("information") || sB.contains("data");
        boolean oIT = oB.contains("computer") || oB.contains("information") || oB.contains("data") || oB.contains("it");
        if (sIT && oIT) return 88.0;

        return 35.0;
    }

    private static double calculateLocationAndCategoryScore(StudentProfile student, Opportunity opp) {
        double score = 45.0;

        // Location Check
        String oppState = opp.getState() != null ? opp.getState().trim().toLowerCase() : "";
        String stuState = student.getState() != null ? student.getState().trim().toLowerCase() : "";
        if (oppState.isEmpty() || oppState.equals("all") || oppState.contains("pan-india")) {
            score += 25.0;
        } else if (oppState.equals(stuState)) {
            score += 20.0;
            String oppCity = opp.getCity() != null ? opp.getCity().trim().toLowerCase() : "";
            String stuCity = student.getCity() != null ? student.getCity().trim().toLowerCase() : "";
            if (!oppCity.isEmpty() && oppCity.equals(stuCity)) {
                score += 10.0;
            }
        }

        // Category Check
        String oppCat = opp.getCategory() != null ? opp.getCategory().trim().toUpperCase() : "ALL";
        String stuCat = student.getCategory() != null ? student.getCategory().trim().toUpperCase() : "GENERAL";
        if (oppCat.equals("ALL") || oppCat.equals("GENERAL") || oppCat.equals(stuCat)) {
            score += 15.0;
        }

        return Math.min(100.0, score);
    }

    private static List<String> tokenize(String csv) {
        List<String> list = new ArrayList<>();
        if (csv == null) return list;
        for (String token : csv.split("[,;]")) {
            String trimmed = token.trim().toLowerCase();
            if (!trimmed.isEmpty()) {
                list.add(trimmed);
            }
        }
        return list;
    }
}
