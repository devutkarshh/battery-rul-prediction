package com.jobportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MatchResponse {
    @JsonProperty("match_score") private Double matchScore;
    @JsonProperty("matched_skills") private List<String> matchedSkills;
    @JsonProperty("missing_skills") private List<String> missingSkills;
    private List<String> recommendations;
    @JsonProperty("cosine_similarity") private Double cosineSimilarity;
    @JsonProperty("skill_match_ratio") private Double skillMatchRatio;

    public MatchResponse() {}

    public Double getMatchScore() { return matchScore; } public void setMatchScore(Double s) { this.matchScore = s; }
    public List<String> getMatchedSkills() { return matchedSkills; } public void setMatchedSkills(List<String> s) { this.matchedSkills = s; }
    public List<String> getMissingSkills() { return missingSkills; } public void setMissingSkills(List<String> s) { this.missingSkills = s; }
    public List<String> getRecommendations() { return recommendations; } public void setRecommendations(List<String> s) { this.recommendations = s; }
    public Double getCosineSimilarity() { return cosineSimilarity; } public void setCosineSimilarity(Double s) { this.cosineSimilarity = s; }
    public Double getSkillMatchRatio() { return skillMatchRatio; } public void setSkillMatchRatio(Double s) { this.skillMatchRatio = s; }
}
