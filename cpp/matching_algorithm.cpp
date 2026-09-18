/**
 * ============================================================================
 * GovSkill Connect - Opportunity Matching Engine (C++)
 * High-Performance Weighted Recommendation Algorithm
 * ============================================================================
 * 
 * Computes a compatibility percentage score [0 - 100%] between a student's
 * profile attributes (Education, Branch, Skills, Category, Location) and an
 * opportunity's eligibility criteria.
 * 
 * Weights:
 * - Skill Overlap:       40%
 * - Education Level:     25%
 * - Branch Alignment:    20%
 * - Location/Category:   15%
 * 
 * Can be compiled as a standalone CLI tool or integrated with the Java backend.
 * Compilation: g++ -O3 -std=c++17 matching_algorithm.cpp -o matching_engine
 * Usage: ./matching_engine --student "<json/csv>" --opportunity "<json/csv>"
 */

#include <iostream>
#include <string>
#include <vector>
#include <sstream>
#include <algorithm>
#include <cctype>
#include <cmath>

// Helper to convert string to lowercase and trim
static std::string normalize(const std::string& str) {
    std::string s = str;
    // Trim
    size_t start = s.find_first_not_of(" \t\r\n");
    if (start == std::string::npos) return "";
    size_t end = s.find_last_not_of(" \t\r\n");
    s = s.substr(start, end - start + 1);
    
    // Lowercase
    std::transform(s.begin(), s.end(), s.begin(), [](unsigned char c) {
        return std::tolower(c);
    });
    return s;
}

// Split string by delimiter
static std::vector<std::string> split(const std::string& text, char delimiter) {
    std::vector<std::string> tokens;
    std::string token;
    std::istringstream tokenStream(text);
    while (std::getline(tokenStream, token, delimiter)) {
        std::string n = normalize(token);
        if (!n.empty()) {
            tokens.push_back(n);
        }
    }
    return tokens;
}

// Check if string contains substring (normalized)
static bool containsIgnoreCase(const std::string& haystack, const std::string& needle) {
    if (needle.empty()) return true;
    std::string h = normalize(haystack);
    std::string n = normalize(needle);
    return h.find(n) != std::string::npos || n.find(h) != std::string::npos;
}

struct StudentProfileData {
    std::string educationLevel;
    std::string branch;
    std::vector<std::string> skills;
    std::string category;
    std::string state;
    std::string city;
};

struct OpportunityData {
    std::string educationRequirement;
    std::string branch;
    std::vector<std::string> requiredSkills;
    std::string category;
    std::string state;
    std::string city;
};

class MatchingEngine {
public:
    static double calculateMatchScore(const StudentProfileData& student, const OpportunityData& opp) {
        double skillScore = calculateSkillScore(student.skills, opp.requiredSkills);
        double educationScore = calculateEducationScore(student.educationLevel, opp.educationRequirement);
        double branchScore = calculateBranchScore(student.branch, opp.branch);
        double locationCategoryScore = calculateLocationAndCategoryScore(student, opp);

        // Weighted total: Skill(40%) + Education(25%) + Branch(20%) + Location/Category(15%)
        double totalScore = (skillScore * 0.40) +
                            (educationScore * 0.25) +
                            (branchScore * 0.20) +
                            (locationCategoryScore * 0.15);

        // Clamp between 15% (baseline discovery) and 99%
        totalScore = std::max(15.0, std::min(99.0, std::round(totalScore)));
        return totalScore;
    }

private:
    static double calculateSkillScore(const std::vector<std::string>& studentSkills,
                                     const std::vector<std::string>& requiredSkills) {
        if (requiredSkills.empty()) return 85.0; // No strict skill restriction
        if (studentSkills.empty()) return 20.0;

        int matchCount = 0;
        for (const auto& req : requiredSkills) {
            for (const auto& stu : studentSkills) {
                if (req == stu || containsIgnoreCase(req, stu) || containsIgnoreCase(stu, req)) {
                    matchCount++;
                    break;
                }
            }
        }

        double ratio = static_cast<double>(matchCount) / requiredSkills.size();
        return std::min(100.0, ratio * 100.0);
    }

    static double calculateEducationScore(const std::string& studentEdu, const std::string& oppEdu) {
        if (oppEdu.empty() || normalize(oppEdu) == "any" || containsIgnoreCase(oppEdu, "all")) {
            return 100.0;
        }

        std::string sEdu = normalize(studentEdu);
        std::string oEdu = normalize(oppEdu);

        if (sEdu == oEdu || containsIgnoreCase(oEdu, sEdu) || containsIgnoreCase(sEdu, oEdu)) {
            return 100.0;
        }

        // Hierarchy comparison: Postgraduate > Undergraduate > Diploma > 12th > 10th
        auto getLevelRank = [](const std::string& edu) -> int {
            if (containsIgnoreCase(edu, "postgraduate") || containsIgnoreCase(edu, "m.tech") || containsIgnoreCase(edu, "mca")) return 5;
            if (containsIgnoreCase(edu, "undergraduate") || containsIgnoreCase(edu, "b.tech") || containsIgnoreCase(edu, "b.e") || containsIgnoreCase(edu, "bca")) return 4;
            if (containsIgnoreCase(edu, "diploma") || containsIgnoreCase(edu, "iti")) return 3;
            if (containsIgnoreCase(edu, "12th") || containsIgnoreCase(edu, "hsc")) return 2;
            if (containsIgnoreCase(edu, "10th") || containsIgnoreCase(edu, "ssc")) return 1;
            return 2;
        };

        int sRank = getLevelRank(sEdu);
        int oRank = getLevelRank(oEdu);

        if (sRank >= oRank) return 90.0; // Meets or exceeds minimum requirement
        if (sRank == oRank - 1) return 50.0; // Slightly below (e.g. diploma for degree)
        return 20.0;
    }

    static double calculateBranchScore(const std::string& studentBranch, const std::string& oppBranch) {
        if (oppBranch.empty() || normalize(oppBranch) == "all" || containsIgnoreCase(oppBranch, "any")) {
            return 100.0;
        }

        std::string sBranch = normalize(studentBranch);
        std::string oBranch = normalize(oppBranch);

        if (sBranch == oBranch || containsIgnoreCase(oBranch, sBranch) || containsIgnoreCase(sBranch, oBranch)) {
            return 100.0;
        }

        // Cross-disciplinary alignment for Computer Science / IT
        bool sIsIT = containsIgnoreCase(sBranch, "computer") || containsIgnoreCase(sBranch, "information") || containsIgnoreCase(sBranch, "data");
        bool oIsIT = containsIgnoreCase(oBranch, "computer") || containsIgnoreCase(oBranch, "information") || containsIgnoreCase(oBranch, "data") || containsIgnoreCase(oBranch, "it");
        if (sIsIT && oIsIT) return 90.0;

        return 30.0;
    }

    static double calculateLocationAndCategoryScore(const StudentProfileData& student, const OpportunityData& opp) {
        double score = 50.0; // Baseline

        // Location Check
        if (normalize(opp.state) == "all" || containsIgnoreCase(opp.state, "pan-india")) {
            score += 25.0;
        } else if (normalize(student.state) == normalize(opp.state)) {
            score += 20.0;
            if (normalize(student.city) == normalize(opp.city)) {
                score += 10.0;
            }
        }

        // Category Check
        std::string oCat = normalize(opp.category);
        if (oCat.empty() || oCat == "all" || oCat == "general" || normalize(student.category) == oCat) {
            score += 15.0;
        }

        return std::min(100.0, score);
    }
};

int main(int argc, char* argv[]) {
    // Demonstration / CLI mode
    // Input format: student_skills;student_edu;student_branch;student_state | opp_skills;opp_edu;opp_branch;opp_state
    if (argc > 1) {
        std::string arg = argv[1];
        size_t splitPos = arg.find('|');
        if (splitPos != std::string::npos) {
            std::string studentPart = arg.substr(0, splitPos);
            std::string oppPart = arg.substr(splitPos + 1);

            auto sTokens = split(studentPart, ';');
            auto oTokens = split(oppPart, ';');

            StudentProfileData student;
            if (sTokens.size() > 0) student.skills = split(sTokens[0], ',');
            if (sTokens.size() > 1) student.educationLevel = sTokens[1];
            if (sTokens.size() > 2) student.branch = sTokens[2];
            if (sTokens.size() > 3) student.state = sTokens[3];

            OpportunityData opp;
            if (oTokens.size() > 0) opp.requiredSkills = split(oTokens[0], ',');
            if (oTokens.size() > 1) opp.educationRequirement = oTokens[1];
            if (oTokens.size() > 2) opp.branch = oTokens[2];
            if (oTokens.size() > 3) opp.state = oTokens[3];

            double score = MatchingEngine::calculateMatchScore(student, opp);
            std::cout << static_cast<int>(score) << std::endl;
            return 0;
        }
    }

    // Default self-test demo
    StudentProfileData sampleStudent;
    sampleStudent.educationLevel = "Undergraduate";
    sampleStudent.branch = "Computer Science & Engineering";
    sampleStudent.skills = {"java", "c++", "sql", "linux"};
    sampleStudent.category = "OBC";
    sampleStudent.state = "Maharashtra";
    sampleStudent.city = "Pune";

    OpportunityData sampleOpp;
    sampleOpp.educationRequirement = "B.Tech / B.E.";
    sampleOpp.branch = "Computer Science & Engineering";
    sampleOpp.requiredSkills = {"java", "c++", "algorithms", "linux"};
    sampleOpp.category = "ALL";
    sampleOpp.state = "Karnataka";
    sampleOpp.city = "Bengaluru";

    double matchScore = MatchingEngine::calculateMatchScore(sampleStudent, sampleOpp);
    std::cout << "GovSkill Connect Matching Engine (C++17)" << std::endl;
    std::cout << "Student: " << sampleStudent.branch << ", Skills: Java, C++, SQL, Linux" << std::endl;
    std::cout << "Opportunity: " << sampleOpp.branch << ", Skills: Java, C++, Algorithms, Linux" << std::endl;
    std::cout << "Computed Match Score: " << static_cast<int>(matchScore) << "% Match" << std::endl;

    return 0;
}
