"""
Skill extraction service using a comprehensive technology skills taxonomy.
Identifies skills from text using pattern matching.
"""
import re
from typing import List, Set


# ============================================================
# COMPREHENSIVE SKILLS TAXONOMY
# Organized by category for maintainability
# ============================================================

SKILLS_DATABASE = {
    # Programming Languages
    "java", "python", "javascript", "typescript", "c++", "c#", "c",
    "ruby", "go", "golang", "rust", "swift", "kotlin", "scala", "php",
    "perl", "r", "matlab", "dart", "lua", "haskell", "erlang",
    "objective-c", "assembly", "groovy", "visual basic", "vb.net",
    "fortran", "cobol", "shell", "bash", "powershell",

    # Web Frontend
    "html", "html5", "css", "css3", "sass", "scss", "less",
    "react", "reactjs", "react.js", "angular", "angularjs", "vue",
    "vuejs", "vue.js", "svelte", "next.js", "nextjs", "nuxt.js",
    "gatsby", "ember", "backbone", "jquery", "bootstrap", "tailwind",
    "tailwindcss", "material ui", "chakra ui", "ant design",
    "webpack", "vite", "parcel", "rollup", "babel",
    "responsive design", "web components", "pwa",

    # Web Backend & Frameworks
    "node.js", "nodejs", "express", "express.js", "nestjs",
    "spring", "spring boot", "spring mvc", "spring security",
    "django", "flask", "fastapi", "tornado",
    "rails", "ruby on rails", "sinatra",
    "asp.net", ".net", ".net core", "blazor",
    "laravel", "symfony", "codeigniter",
    "gin", "echo", "fiber",
    "microservices", "rest api", "restful", "graphql", "grpc",
    "websocket", "soap",

    # Databases
    "mysql", "postgresql", "postgres", "oracle", "sql server", "mssql",
    "sqlite", "mariadb", "sql",
    "mongodb", "cassandra", "couchdb", "dynamodb", "firebase",
    "redis", "memcached", "elasticsearch", "neo4j",
    "hibernate", "jpa", "sequelize", "prisma", "typeorm",
    "mybatis", "sqlalchemy",

    # Cloud & DevOps
    "aws", "amazon web services", "azure", "gcp", "google cloud",
    "heroku", "digitalocean", "netlify", "vercel",
    "docker", "kubernetes", "k8s", "openshift",
    "terraform", "ansible", "puppet", "chef",
    "jenkins", "github actions", "gitlab ci", "circleci", "travis ci",
    "ci/cd", "devops", "sre",
    "nginx", "apache", "tomcat",
    "linux", "unix", "windows server", "ubuntu",
    "aws lambda", "serverless", "cloudformation",
    "ec2", "s3", "rds", "ecs", "eks", "fargate",
    "load balancing", "auto scaling",

    # AI / ML / Data Science
    "machine learning", "deep learning", "artificial intelligence", "ai",
    "neural networks", "nlp", "natural language processing",
    "computer vision", "reinforcement learning",
    "tensorflow", "pytorch", "keras", "scikit-learn", "sklearn",
    "pandas", "numpy", "scipy", "matplotlib", "seaborn", "plotly",
    "opencv", "spacy", "nltk", "hugging face", "transformers",
    "bert", "gpt", "llm", "large language models",
    "data science", "data analysis", "data engineering",
    "data mining", "feature engineering",
    "mlflow", "kubeflow", "mlops",
    "aws sagemaker", "azure ml",
    "statistics", "regression", "classification", "clustering",
    "random forest", "xgboost", "gradient boosting",
    "cnn", "rnn", "lstm", "gan",

    # Mobile Development
    "android", "ios", "react native", "flutter", "xamarin",
    "swiftui", "jetpack compose", "cordova", "ionic",
    "mobile development",

    # Version Control & Collaboration
    "git", "github", "gitlab", "bitbucket", "svn",
    "jira", "confluence", "trello", "agile", "scrum", "kanban",

    # Testing
    "junit", "testng", "mockito", "selenium", "cypress",
    "jest", "mocha", "chai", "pytest", "unittest",
    "tdd", "bdd", "unit testing", "integration testing",
    "e2e testing", "load testing", "performance testing",
    "postman", "swagger",

    # Security
    "oauth", "oauth2", "jwt", "ssl", "tls", "https",
    "encryption", "authentication", "authorization",
    "owasp", "penetration testing", "cybersecurity",
    "spring security", "keycloak",

    # Architecture & Design
    "design patterns", "solid", "clean architecture",
    "event-driven", "domain-driven design", "ddd",
    "cqrs", "event sourcing", "saga pattern",
    "api gateway", "service mesh", "message queue",
    "rabbitmq", "kafka", "apache kafka", "activemq",
    "distributed systems",

    # Data Formats & APIs
    "json", "xml", "yaml", "csv", "protobuf",
    "rest", "soap", "graphql", "websockets",

    # Monitoring & Logging
    "prometheus", "grafana", "elk stack", "splunk",
    "datadog", "new relic", "cloudwatch",
    "logging", "monitoring", "alerting",

    # Big Data
    "hadoop", "spark", "apache spark", "hive", "pig",
    "flink", "storm", "airflow", "apache airflow",
    "etl", "data warehouse", "data lake",
    "snowflake", "redshift", "bigquery",

    # Soft Skills (relevant for matching)
    "leadership", "communication", "teamwork", "problem solving",
    "critical thinking", "project management", "mentoring",
    "code review", "documentation", "presentation",
}

# Build lookup structures for efficient matching
# Sort by length (longest first) so multi-word skills match before single words
SKILLS_LIST = sorted(SKILLS_DATABASE, key=len, reverse=True)

# Create compiled regex patterns for each skill
SKILL_PATTERNS = {}
for skill in SKILLS_LIST:
    # Escape special regex characters
    escaped = re.escape(skill)
    # Word boundary matching (case-insensitive)
    SKILL_PATTERNS[skill] = re.compile(r'\b' + escaped + r'\b', re.IGNORECASE)


def extract_skills(text: str) -> List[str]:
    """
    Extract recognized skills from text using pattern matching.
    
    Uses a comprehensive taxonomy of ~200+ technology skills and
    matches them against the input text with word boundary detection.
    
    Args:
        text: Input text (resume or job description).
        
    Returns:
        List of unique matched skills, sorted alphabetically.
    """
    if not text:
        return []
    
    found_skills: Set[str] = set()
    
    # Normalize text for matching
    normalized = text.lower()
    
    for skill, pattern in SKILL_PATTERNS.items():
        if pattern.search(normalized):
            # Store the canonical form (from the database)
            found_skills.add(skill)
    
    # Remove sub-skills if parent skill is matched
    # e.g., if "spring boot" matched, don't also count "spring" separately
    skills_to_remove = set()
    for skill in found_skills:
        for other_skill in found_skills:
            if skill != other_skill and skill in other_skill:
                skills_to_remove.add(skill)
    
    final_skills = found_skills - skills_to_remove
    
    return sorted(list(final_skills))


def extract_skills_from_csv(skills_csv: str) -> List[str]:
    """
    Extract skills from a comma-separated string (e.g., from job posting required_skills field).
    Normalizes and matches against the skills database.
    
    Args:
        skills_csv: Comma-separated skills string.
        
    Returns:
        List of normalized, recognized skills.
    """
    if not skills_csv:
        return []
    
    raw_skills = [s.strip().lower() for s in skills_csv.split(",") if s.strip()]
    recognized = []
    
    for raw in raw_skills:
        # Direct match
        if raw in SKILLS_DATABASE:
            recognized.append(raw)
        else:
            # Try to find in taxonomy via pattern matching
            for skill in SKILLS_LIST:
                if skill in raw or raw in skill:
                    recognized.append(skill)
                    break
            else:
                # Keep the original skill even if not in taxonomy
                recognized.append(raw)
    
    return sorted(list(set(recognized)))
