-- ============================================================
-- AI-Powered Job Portal — Database Initialization Script
-- MySQL 8.x compatible
-- ============================================================

CREATE DATABASE IF NOT EXISTS job_portal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE job_portal;

-- ============================================================
-- 1. USERS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,
    role        ENUM('ROLE_USER','ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
) ENGINE=InnoDB;

-- ============================================================
-- 2. JOBS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS jobs (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    company         VARCHAR(200) NOT NULL,
    location        VARCHAR(150),
    description     TEXT         NOT NULL,
    required_skills TEXT         NOT NULL,
    salary_range    VARCHAR(100),
    job_type        ENUM('FULL_TIME','PART_TIME','INTERNSHIP','CONTRACT') NOT NULL DEFAULT 'FULL_TIME',
    posted_by       BIGINT       NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (posted_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_jobs_title (title),
    INDEX idx_jobs_posted_by (posted_by)
) ENGINE=InnoDB;

-- ============================================================
-- 3. RESUMES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS resumes (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    file_name       VARCHAR(255) NOT NULL,
    file_path       VARCHAR(500) NOT NULL,
    file_type       VARCHAR(10)  NOT NULL,
    extracted_text  LONGTEXT,
    uploaded_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_resumes_user (user_id)
) ENGINE=InnoDB;

-- ============================================================
-- 4. APPLICATIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS applications (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    job_id          BIGINT NOT NULL,
    resume_id       BIGINT NOT NULL,
    match_score     DOUBLE,
    matched_skills  TEXT,
    missing_skills  TEXT,
    recommendations TEXT,
    status          ENUM('PENDING','REVIEWED','ACCEPTED','REJECTED') NOT NULL DEFAULT 'PENDING',
    applied_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)   REFERENCES users(id)    ON DELETE CASCADE,
    FOREIGN KEY (job_id)    REFERENCES jobs(id)     ON DELETE CASCADE,
    FOREIGN KEY (resume_id) REFERENCES resumes(id)  ON DELETE CASCADE,
    UNIQUE KEY uk_user_job (user_id, job_id),
    INDEX idx_app_job (job_id),
    INDEX idx_app_score (match_score DESC)
) ENGINE=InnoDB;

-- ============================================================
-- SEED DATA
-- ============================================================

-- Admin user  (password: admin123 — BCrypt hashed)
INSERT INTO users (full_name, email, password, role) VALUES
('Admin User', 'admin@jobportal.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_ADMIN');

-- Regular user (password: user123 — BCrypt hashed)
INSERT INTO users (full_name, email, password, role) VALUES
('John Doe', 'john@example.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'ROLE_USER');

-- Sample Jobs
INSERT INTO jobs (title, company, location, description, required_skills, salary_range, job_type, posted_by) VALUES
(
    'Senior Java Developer',
    'TechCorp Solutions',
    'Bangalore, India',
    'We are looking for an experienced Java Developer to join our enterprise applications team. You will design and develop high-volume, low-latency applications using Java, Spring Boot, and microservices architecture. Responsibilities include writing well-designed, testable code, conducting code reviews, and mentoring junior developers.',
    'Java, Spring Boot, Microservices, REST API, Hibernate, JPA, MySQL, Docker, Kubernetes, Git, CI/CD, JUnit, Design Patterns',
    '₹18,00,000 - ₹30,00,000',
    'FULL_TIME',
    1
),
(
    'Full Stack Python Developer',
    'DataMinds Inc.',
    'Hyderabad, India',
    'Join our product engineering team to build scalable web applications. You will work across the full stack using Python/Django for backend and React for frontend. Experience with cloud deployment and DevOps practices is a plus.',
    'Python, Django, Flask, React, JavaScript, HTML, CSS, PostgreSQL, MongoDB, Redis, AWS, Docker, REST API, Git',
    '₹15,00,000 - ₹25,00,000',
    'FULL_TIME',
    1
),
(
    'Machine Learning Engineer',
    'AI Dynamics',
    'Remote',
    'We are seeking a Machine Learning Engineer to develop and deploy ML models for our recommendation engine. You will work with large datasets, build training pipelines, and optimize model performance. Strong understanding of NLP and deep learning frameworks is required.',
    'Python, TensorFlow, PyTorch, scikit-learn, NLP, Deep Learning, Pandas, NumPy, SQL, AWS SageMaker, MLflow, Docker, Git, Statistics',
    '₹20,00,000 - ₹35,00,000',
    'FULL_TIME',
    1
),
(
    'Frontend Developer Intern',
    'StartupHub',
    'Mumbai, India',
    'Exciting internship opportunity for aspiring frontend developers! You will work on building responsive user interfaces for our SaaS platform. You will learn from experienced developers and gain hands-on experience with modern web technologies.',
    'HTML, CSS, JavaScript, React, TypeScript, Responsive Design, Git, Figma',
    '₹15,000 - ₹25,000 per month',
    'INTERNSHIP',
    1
),
(
    'DevOps Engineer',
    'CloudNine Technologies',
    'Pune, India',
    'We need a DevOps Engineer to manage our cloud infrastructure and CI/CD pipelines. You will automate deployment processes, monitor system performance, and ensure high availability of our services. Experience with container orchestration is essential.',
    'AWS, Azure, Docker, Kubernetes, Terraform, Jenkins, GitHub Actions, Linux, Bash, Python, Monitoring, Prometheus, Grafana, Nginx',
    '₹16,00,000 - ₹28,00,000',
    'FULL_TIME',
    1
);
