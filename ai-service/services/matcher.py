"""
Resume-Job matching engine using TF-IDF vectorization and cosine similarity.
Combines text similarity with explicit skill matching for accurate scoring.
"""
from typing import Dict, List, Any
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from services.preprocessor import preprocess
from services.skill_extractor import extract_skills, extract_skills_from_csv


def compute_match(resume_text: str, job_description: str, required_skills_csv: str = "") -> Dict[str, Any]:
    """
    Compute the match between a resume and a job description.
    
    Algorithm:
    1. Preprocess both texts (clean, normalize, remove stop words)
    2. Extract skills from resume text
    3. Extract required skills from job description + explicit skills CSV
    4. Compute TF-IDF cosine similarity between preprocessed texts
    5. Compute skill overlap ratio
    6. Weighted combination: 60% cosine similarity + 40% skill match ratio
    7. Generate recommendations based on missing skills
    
    Args:
        resume_text: Full text extracted from the resume.
        job_description: Full job description text.
        required_skills_csv: Comma-separated required skills from the job posting.
        
    Returns:
        Dictionary with match_score, matched_skills, missing_skills,
        recommendations, cosine_similarity, and skill_match_ratio.
    """
    # ------------------------------------------------------------------
    # Step 1: Preprocess texts
    # ------------------------------------------------------------------
    processed_resume = preprocess(resume_text)
    processed_job = preprocess(job_description)
    
    if not processed_resume or not processed_job:
        return {
            "match_score": 0.0,
            "matched_skills": [],
            "missing_skills": [],
            "recommendations": ["Unable to analyze: insufficient text content."],
            "cosine_similarity": 0.0,
            "skill_match_ratio": 0.0,
        }
    
    # ------------------------------------------------------------------
    # Step 2: Extract skills
    # ------------------------------------------------------------------
    resume_skills = set(extract_skills(resume_text))
    
    # Combine skills from job description text + explicit CSV
    job_skills_from_text = set(extract_skills(job_description))
    job_skills_from_csv = set(extract_skills_from_csv(required_skills_csv))
    job_skills = job_skills_from_text | job_skills_from_csv
    
    # If no job skills detected, fall back to text-only matching
    if not job_skills:
        job_skills = job_skills_from_text
    
    # ------------------------------------------------------------------
    # Step 3: Compute TF-IDF cosine similarity
    # ------------------------------------------------------------------
    try:
        vectorizer = TfidfVectorizer(
            max_features=5000,
            ngram_range=(1, 2),  # Unigrams + bigrams
            sublinear_tf=True,   # Apply log normalization
            min_df=1,
            max_df=1.0,
        )
        tfidf_matrix = vectorizer.fit_transform([processed_resume, processed_job])
        cosine_sim = float(cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:2])[0][0])
    except Exception:
        cosine_sim = 0.0
    
    # ------------------------------------------------------------------
    # Step 4: Compute skill overlap
    # ------------------------------------------------------------------
    matched_skills = sorted(list(resume_skills & job_skills))
    missing_skills = sorted(list(job_skills - resume_skills))
    
    if job_skills:
        skill_match_ratio = len(matched_skills) / len(job_skills)
    else:
        skill_match_ratio = 0.0
    
    # ------------------------------------------------------------------
    # Step 5: Compute weighted match score
    # ------------------------------------------------------------------
    # 60% text similarity (captures context, phrasing, domain knowledge)
    # 40% skill match ratio (captures explicit skill requirements)
    raw_score = (0.6 * cosine_sim + 0.4 * skill_match_ratio) * 100
    
    # Clamp to 0-100 range
    match_score = round(max(0.0, min(100.0, raw_score)), 1)
    
    # ------------------------------------------------------------------
    # Step 6: Generate recommendations
    # ------------------------------------------------------------------
    recommendations = _generate_recommendations(
        match_score, matched_skills, missing_skills, skill_match_ratio
    )
    
    return {
        "match_score": match_score,
        "matched_skills": matched_skills,
        "missing_skills": missing_skills,
        "recommendations": recommendations,
        "cosine_similarity": round(cosine_sim * 100, 1),
        "skill_match_ratio": round(skill_match_ratio * 100, 1),
    }


def _generate_recommendations(
    match_score: float,
    matched_skills: List[str],
    missing_skills: List[str],
    skill_ratio: float,
) -> List[str]:
    """
    Generate actionable recommendations based on the match analysis.
    
    Args:
        match_score: Overall match score (0-100).
        matched_skills: List of matching skills.
        missing_skills: List of missing skills.
        skill_ratio: Ratio of matched to required skills (0-1).
        
    Returns:
        List of recommendation strings.
    """
    recommendations = []
    
    # Score-based general recommendations
    if match_score >= 80:
        recommendations.append(
            "Excellent match! Your profile strongly aligns with this position. "
            "Focus on highlighting your relevant project experience in the interview."
        )
    elif match_score >= 60:
        recommendations.append(
            "Good match! You have a solid foundation for this role. "
            "Consider strengthening the areas listed below to improve your candidacy."
        )
    elif match_score >= 40:
        recommendations.append(
            "Moderate match. While you have some relevant skills, there are "
            "significant gaps that should be addressed before applying."
        )
    else:
        recommendations.append(
            "Low match. This position requires substantially different skills "
            "from what your resume demonstrates. Consider upskilling or looking "
            "at more aligned positions."
        )
    
    # Skill-specific recommendations
    if missing_skills:
        # Group into priority categories
        high_priority = missing_skills[:5]
        
        if high_priority:
            skills_str = ", ".join(high_priority)
            recommendations.append(
                f"Priority skills to develop: {skills_str}. "
                "Consider online courses, certifications, or personal projects "
                "to demonstrate proficiency in these areas."
            )
        
        if len(missing_skills) > 5:
            additional = ", ".join(missing_skills[5:])
            recommendations.append(
                f"Additional skills to consider: {additional}."
            )
    
    if skill_ratio < 0.5 and missing_skills:
        recommendations.append(
            "Your skill match is below 50%. Consider tailoring your resume "
            "to better highlight relevant experience and transferable skills."
        )
    
    if matched_skills:
        top_matched = ", ".join(matched_skills[:5])
        recommendations.append(
            f"Leverage your strengths in: {top_matched}. "
            "Ensure these are prominently featured in your resume and cover letter."
        )
    
    return recommendations
