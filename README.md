# AI-Powered Job Portal with Resume Matching & Candidate Ranking

A production-quality, microservices-style job portal that uses AI/NLP to match resumes against job descriptions, compute match scores, and rank candidates.

## System Architecture

```
Frontend (HTML/CSS/JS)  ──►  Java Backend (Spring Boot :8080)  ──►  Python AI (FastAPI :5000)
                                      │
                                      ▼
                                MySQL Database (:3306)
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17+, Spring Boot 3.2, Spring Security, JPA/Hibernate |
| AI Service | Python 3.9+, FastAPI, scikit-learn, PyPDF2, python-docx |
| Frontend | HTML5, CSS3 (dark glassmorphism), Vanilla JavaScript |
| Database | MySQL 8.x |
| Auth | JWT (JJWT), BCrypt password hashing |

---

## Setup Instructions

### 1. Database Setup

```bash
# Login to MySQL
mysql -u root -proot123

# Run the init script
source C:/Users/User/CODING/job/database/init.sql
```

This creates the `job_portal` database with 4 tables and seed data (admin + sample jobs).

**Seed Users:**
| Email | Password | Role |
|-------|----------|------|
| admin@jobportal.com | admin123 | ADMIN |
| john@example.com | user123 | USER |

### 2. Python AI Service

```bash
cd ai-service

# Create virtual environment (recommended)
python -m venv venv
venv\Scripts\activate    # Windows

# Install dependencies
pip install -r requirements.txt

# Download spaCy model (optional, for enhanced NLP)
python -m spacy download en_core_web_sm

# Start the service
python main.py
```

The AI service runs on **http://localhost:5000**. Test it: `GET http://localhost:5000/api/health`

### 3. Java Backend

```bash
cd backend

# Build and run
mvn clean install -DskipTests
mvn spring-boot:run
```

The backend runs on **http://localhost:8080**.

### 4. Frontend

Open `frontend/index.html` in a browser, or serve via any static server:

```bash
cd frontend
# Option 1: Python simple server
python -m http.server 5500

# Option 2: VS Code Live Server extension
# Option 3: Open index.html directly in browser
```

---

## API Reference

### Authentication
```
POST /api/auth/register
Body: { "fullName": "Test User", "email": "test@test.com", "password": "pass123" }

POST /api/auth/login
Body: { "email": "admin@jobportal.com", "password": "admin123" }
Response: { "token": "eyJ...", "role": "ROLE_ADMIN", ... }
```

### Jobs
```
GET    /api/jobs                    — List all jobs
GET    /api/jobs?search=python      — Search jobs
GET    /api/jobs/{id}               — Get job by ID
POST   /api/jobs                    — Create job (ADMIN, requires JWT)
PUT    /api/jobs/{id}               — Update job (ADMIN)
DELETE /api/jobs/{id}               — Delete job (ADMIN)
```

### Resume
```
POST /api/resumes/upload           — Upload PDF/DOCX (multipart, requires JWT)
GET  /api/resumes/my-resume        — Get current resume info
```

### Applications
```
POST /api/applications/apply              — Apply to job (triggers AI matching)
Body: { "jobId": 1 }

GET  /api/applications/my-applications    — User's applications
GET  /api/applications/job/{jobId}        — Ranked applicants (ADMIN)
GET  /api/applications/{id}/match-result  — Match details
PUT  /api/applications/{id}/status        — Update status (ADMIN)
Body: { "status": "ACCEPTED" }
```

### AI Service (Internal)
```
POST /api/match
Body: { "resume_text": "...", "job_description": "...", "required_skills": "Java, Spring" }
Response: { "match_score": 72.5, "matched_skills": [...], "missing_skills": [...], "recommendations": [...] }

POST /api/extract
Body: multipart file upload
Response: { "extracted_text": "...", "success": true }
```

---

## AI Matching Algorithm

1. **Text Preprocessing**: Clean → normalize → remove stop words
2. **Skill Extraction**: Pattern matching against 200+ technology skills taxonomy
3. **TF-IDF Vectorization**: Convert texts to TF-IDF vectors with bigrams
4. **Cosine Similarity**: Compute document similarity
5. **Weighted Score**: `60% × cosine_similarity + 40% × skill_match_ratio`
6. **Recommendations**: Generated based on missing skills and score thresholds

---

## Project Structure

```
job/
├── database/init.sql           — Schema + seed data
├── ai-service/                 — Python FastAPI
│   ├── main.py                 — FastAPI app (3 endpoints)
│   ├── requirements.txt
│   ├── models/schemas.py       — Pydantic models
│   └── services/
│       ├── text_extractor.py   — PDF/DOCX parsing
│       ├── preprocessor.py     — NLP preprocessing
│       ├── skill_extractor.py  — 200+ skills taxonomy
│       └── matcher.py          — TF-IDF + cosine similarity
├── backend/                    — Java Spring Boot
│   ├── pom.xml
│   └── src/main/java/com/jobportal/
│       ├── config/             — Security, CORS, App config
│       ├── controller/         — REST controllers (4)
│       ├── dto/                — Request/Response DTOs (8)
│       ├── entity/             — JPA entities (4)
│       ├── exception/          — Global error handling
│       ├── filter/             — JWT auth filter
│       ├── repository/         — Data repositories (4)
│       ├── security/           — JWT token provider
│       └── service/            — Business logic (6)
└── frontend/                   — HTML/CSS/JS
    ├── index.html              — Login
    ├── register.html           — Registration
    ├── dashboard.html          — User dashboard
    ├── jobs.html               — Job listings
    ├── job-detail.html         — Job details
    ├── resume.html             — Resume upload
    ├── match-results.html      — AI match results
    ├── admin-dashboard.html    — Admin overview
    ├── admin-jobs.html         — Job CRUD
    ├── admin-applicants.html   — Ranked candidates
    ├── css/styles.css          — Design system
    └── js/                     — Page scripts
```
