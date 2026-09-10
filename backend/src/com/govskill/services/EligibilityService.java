package com.govskill.services;

import com.govskill.dao.ProgramDAO;
import com.govskill.models.GovernmentProgram;

import java.util.*;

public class EligibilityService {

    private final ProgramDAO programDAO = new ProgramDAO();

    public static class EligibilityResult {
        public String categoryName;
        public List<ProgramMatch> matches = new ArrayList<>();
    }

    public static class ProgramMatch {
        public GovernmentProgram program;
        public int eligibilityScore;
        public String matchReason;
        public boolean eligible;

        public ProgramMatch(GovernmentProgram program, int eligibilityScore, String matchReason, boolean eligible) {
            this.program = program;
            this.eligibilityScore = eligibilityScore;
            this.matchReason = matchReason;
            this.eligible = eligible;
        }
    }

    public Map<String, List<ProgramMatch>> evaluateEligibility(String state, String category, String education, String branch, String skills) {
        String securePayload = String.join("|",
                state == null ? "" : state,
                category == null ? "" : category,
                education == null ? "" : education,
                branch == null ? "" : branch,
                skills == null ? "" : skills);
        return TrustedExecutionEnvironment.execute("ELIGIBILITY", securePayload,
                () -> evaluateEligibilityInternal(state, category, education, branch, skills));
    }

    private Map<String, List<ProgramMatch>> evaluateEligibilityInternal(String state, String category, String education, String branch, String skills) {
        Map<String, List<ProgramMatch>> result = new LinkedHashMap<>();
        result.put("SCHOLARSHIP", new ArrayList<>());
        result.put("EMPLOYMENT_SCHEME", new ArrayList<>());
        result.put("SKILL_DEVELOPMENT", new ArrayList<>());
        result.put("APPRENTICESHIP", new ArrayList<>());

        List<GovernmentProgram> allPrograms = programDAO.findAll(null);

        String sState = state != null ? state.trim().toLowerCase() : "";
        String sCat = category != null ? category.trim().toUpperCase() : "GENERAL";
        String sEdu = education != null ? education.trim().toLowerCase() : "";
        String sBranch = branch != null ? branch.trim().toLowerCase() : "";
        String sSkills = skills != null ? skills.trim().toLowerCase() : "";

        for (GovernmentProgram p : allPrograms) {
            int score = 50;
            StringBuilder reasons = new StringBuilder();

            String descAndElig = (p.getDescription() + " " + p.getEligibility()).toLowerCase();

            // Category match
            if (descAndElig.contains("sc") && sCat.equals("SC")) {
                score += 30;
                reasons.append("Category reservation matched (SC). ");
            } else if (descAndElig.contains("obc") && sCat.equals("OBC")) {
                score += 30;
                reasons.append("Category reservation matched (OBC). ");
            } else if (descAndElig.contains("girl") || descAndElig.contains("women")) {
                reasons.append("Special affirmative action program. ");
                score += 20;
            } else {
                score += 15;
                reasons.append("Open across categories. ");
            }

            // Education match
            if (!sEdu.isEmpty()) {
                if (descAndElig.contains(sEdu) || descAndElig.contains("any") || descAndElig.contains("degree") || descAndElig.contains("graduate")) {
                    score += 20;
                    reasons.append("Education qualification requirement satisfied. ");
                }
            }

            // State match
            if (p.getName().toLowerCase().contains("mahadbt") || descAndElig.contains("maharashtra")) {
                if (sState.contains("maharashtra")) {
                    score += 20;
                    reasons.append("State domicile requirement verified. ");
                } else {
                    score -= 30;
                    reasons.append("Restricted to Maharashtra domicile residents. ");
                }
            } else {
                score += 15;
                reasons.append("Pan-India Central Government Scheme. ");
            }

            score = Math.min(99, Math.max(25, score));
            boolean eligible = score >= 60;

            String typeKey = p.getType();
            if (!result.containsKey(typeKey)) {
                result.put(typeKey, new ArrayList<>());
            }
            result.get(typeKey).add(new ProgramMatch(p, score, reasons.toString().trim(), eligible));
        }

        // Sort each category by eligibility score
        for (List<ProgramMatch> list : result.values()) {
            list.sort((a, b) -> Integer.compare(b.eligibilityScore, a.eligibilityScore));
        }

        return result;
    }
}
