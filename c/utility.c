/**
 * ============================================================================
 * GovSkill Connect - Core Utility & Tokenization Module (C99)
 * High-Speed String Normalization, Tokenization & Hash Helper
 * ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_TOKEN_LEN 128
#define MAX_TOKENS 64

typedef struct {
    char tokens[MAX_TOKENS][MAX_TOKEN_LEN];
    int count;
} TokenList;

/**
 * Trim leading and trailing whitespace in-place
 */
void trim_whitespace(char* str) {
    if (!str) return;
    char* start = str;
    while (*start && isspace((unsigned char)*start)) {
        start++;
    }
    char* end = start + strlen(start) - 1;
    while (end >= start && isspace((unsigned char)*end)) {
        *end = '\0';
        end--;
    }
    if (start != str) {
        memmove(str, start, strlen(start) + 1);
    }
}

/**
 * Convert string to lowercase in-place
 */
void to_lowercase(char* str) {
    if (!str) return;
    for (int i = 0; str[i]; i++) {
        str[i] = (char)tolower((unsigned char)str[i]);
    }
}

/**
 * Tokenize comma-separated skills list into normalized TokenList
 */
TokenList tokenize_skills(const char* input_csv) {
    TokenList list;
    list.count = 0;
    if (!input_csv) return list;

    char buffer[2048];
    strncpy(buffer, input_csv, sizeof(buffer) - 1);
    buffer[sizeof(buffer) - 1] = '\0';

    char* token = strtok(buffer, ",;");
    while (token != NULL && list.count < MAX_TOKENS) {
        trim_whitespace(token);
        to_lowercase(token);
        if (strlen(token) > 0) {
            strncpy(list.tokens[list.count], token, MAX_TOKEN_LEN - 1);
            list.tokens[list.count][MAX_TOKEN_LEN - 1] = '\0';
            list.count++;
        }
        token = strtok(NULL, ",;");
    }
    return list;
}

/**
 * Calculate Jaccard similarity between two token sets
 */
double calculate_skill_overlap(const TokenList* listA, const TokenList* listB) {
    if (listA->count == 0 || listB->count == 0) return 0.0;

    int intersection = 0;
    for (int i = 0; i < listA->count; i++) {
        for (int j = 0; j < listB->count; j++) {
            if (strcmp(listA->tokens[i], listB->tokens[j]) == 0 ||
                strstr(listA->tokens[i], listB->tokens[j]) != NULL ||
                strstr(listB->tokens[j], listA->tokens[i]) != NULL) {
                intersection++;
                break;
            }
        }
    }

    int total = listB->count; // measured against opportunity requirement
    if (total == 0) return 1.0;
    double ratio = (double)intersection / (double)total;
    return ratio > 1.0 ? 1.0 : ratio;
}

#ifdef UTILITY_MAIN
int main(void) {
    const char* student_skills = "Java, C++, SQL, Linux, Git";
    const char* opp_skills = "Java, C++, Algorithms, Linux";

    TokenList sList = tokenize_skills(student_skills);
    TokenList oList = tokenize_skills(opp_skills);

    double overlap = calculate_skill_overlap(&sList, &oList);
    printf("GovSkill Connect C Utility Tokenizer\n");
    printf("Skill overlap score: %.2f (%.0f%%)\n", overlap, overlap * 100.0);
    return 0;
}
#endif
