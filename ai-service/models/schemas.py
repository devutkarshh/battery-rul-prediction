"""
Pydantic schemas for the AI matching service.
"""
from pydantic import BaseModel, Field
from typing import List, Optional


class MatchRequest(BaseModel):
    """Request body for resume-job matching."""
    resume_text: str = Field(..., min_length=10, description="Extracted text from the resume")
    job_description: str = Field(..., min_length=10, description="Full job description text")
    required_skills: str = Field(default="", description="Comma-separated required skills from the job posting")


class MatchResponse(BaseModel):
    """Response body with matching results."""
    match_score: float = Field(..., ge=0, le=100, description="Overall match score (0-100)")
    matched_skills: List[str] = Field(default_factory=list, description="Skills found in both resume and job")
    missing_skills: List[str] = Field(default_factory=list, description="Skills required by job but missing from resume")
    recommendations: List[str] = Field(default_factory=list, description="Improvement recommendations")
    cosine_similarity: float = Field(default=0.0, description="Raw cosine similarity score")
    skill_match_ratio: float = Field(default=0.0, description="Ratio of matched to required skills")


class ExtractRequest(BaseModel):
    """Request metadata for text extraction."""
    file_type: str = Field(..., description="File type: pdf or docx")


class ExtractResponse(BaseModel):
    """Response body with extracted text."""
    extracted_text: str = Field(default="", description="Text extracted from the uploaded file")
    success: bool = Field(default=True)
    message: str = Field(default="Text extracted successfully")


class HealthResponse(BaseModel):
    """Health check response."""
    status: str = "healthy"
    service: str = "ai-matching-service"
    version: str = "1.0.0"
