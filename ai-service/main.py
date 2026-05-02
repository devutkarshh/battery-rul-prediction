"""
AI Resume Matching Service — FastAPI Application

Provides endpoints for:
- Resume text extraction (PDF/DOCX)
- Resume-job description matching with TF-IDF and cosine similarity
- Health check

Runs on port 5000 by default.
"""
from fastapi import FastAPI, UploadFile, File, HTTPException, Form
from fastapi.middleware.cors import CORSMiddleware

from models.schemas import MatchRequest, MatchResponse, ExtractResponse, HealthResponse
from services.text_extractor import extract_text
from services.matcher import compute_match

# ============================================================
# FastAPI Application Setup
# ============================================================
app = FastAPI(
    title="AI Resume Matching Service",
    description="NLP-powered resume analysis and job matching API",
    version="1.0.0",
)

# CORS — allow the Java backend and frontend to access
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ============================================================
# Health Check
# ============================================================
@app.get("/api/health", response_model=HealthResponse, tags=["Health"])
async def health_check():
    """Check if the AI service is running."""
    return HealthResponse()


# ============================================================
# Resume-Job Matching
# ============================================================
@app.post("/api/match", response_model=MatchResponse, tags=["Matching"])
async def match_resume_to_job(request: MatchRequest):
    """
    Match a resume against a job description.
    
    Takes resume text and job description text, performs NLP analysis
    using TF-IDF vectorization and cosine similarity, and returns
    a match score with detailed skill analysis.
    
    - **resume_text**: Full text extracted from the candidate's resume
    - **job_description**: Full job description including requirements
    - **required_skills**: Comma-separated list of required skills (optional)
    """
    try:
        result = compute_match(
            resume_text=request.resume_text,
            job_description=request.job_description,
            required_skills_csv=request.required_skills,
        )
        return MatchResponse(**result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Matching failed: {str(e)}")


# ============================================================
# Text Extraction from Uploaded Files
# ============================================================
@app.post("/api/extract", response_model=ExtractResponse, tags=["Extraction"])
async def extract_text_from_file(
    file: UploadFile = File(..., description="Resume file (PDF or DOCX)"),
):
    """
    Extract text content from an uploaded resume file.
    
    Supports PDF and DOCX formats. Returns the extracted text
    which can be stored and used for matching.
    """
    # Validate file type
    filename = file.filename or ""
    file_ext = filename.rsplit(".", 1)[-1].lower() if "." in filename else ""
    
    if file_ext not in ("pdf", "docx"):
        raise HTTPException(
            status_code=400,
            detail=f"Unsupported file type: .{file_ext}. Supported: .pdf, .docx"
        )
    
    try:
        file_bytes = await file.read()
        
        if not file_bytes:
            raise HTTPException(status_code=400, detail="Empty file uploaded")
        
        extracted = extract_text(file_bytes, file_ext)
        
        if not extracted.strip():
            return ExtractResponse(
                extracted_text="",
                success=False,
                message="File processed but no text content was found. The file may contain only images or be corrupted."
            )
        
        return ExtractResponse(
            extracted_text=extracted,
            success=True,
            message=f"Successfully extracted {len(extracted)} characters from {filename}"
        )
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Text extraction failed: {str(e)}")


# ============================================================
# Entry Point
# ============================================================
if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=5000, reload=True)
