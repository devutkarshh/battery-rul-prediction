"""
Text preprocessing pipeline for NLP analysis.
Handles cleaning, tokenization, and normalization.
"""
import re
import string


# Common English stop words (avoids spaCy dependency for preprocessing)
STOP_WORDS = {
    'i', 'me', 'my', 'myself', 'we', 'our', 'ours', 'ourselves', 'you', 'your',
    'yours', 'yourself', 'yourselves', 'he', 'him', 'his', 'himself', 'she', 'her',
    'hers', 'herself', 'it', 'its', 'itself', 'they', 'them', 'their', 'theirs',
    'themselves', 'what', 'which', 'who', 'whom', 'this', 'that', 'these', 'those',
    'am', 'is', 'are', 'was', 'were', 'be', 'been', 'being', 'have', 'has', 'had',
    'having', 'do', 'does', 'did', 'doing', 'a', 'an', 'the', 'and', 'but', 'if',
    'or', 'because', 'as', 'until', 'while', 'of', 'at', 'by', 'for', 'with',
    'about', 'against', 'between', 'through', 'during', 'before', 'after', 'above',
    'below', 'to', 'from', 'up', 'down', 'in', 'out', 'on', 'off', 'over', 'under',
    'again', 'further', 'then', 'once', 'here', 'there', 'when', 'where', 'why',
    'how', 'all', 'both', 'each', 'few', 'more', 'most', 'other', 'some', 'such',
    'no', 'nor', 'not', 'only', 'own', 'same', 'so', 'than', 'too', 'very', 's',
    't', 'can', 'will', 'just', 'don', 'should', 'now', 'd', 'll', 'm', 'o', 're',
    've', 'y', 'ain', 'aren', 'couldn', 'didn', 'doesn', 'hadn', 'hasn', 'haven',
    'isn', 'ma', 'mightn', 'mustn', 'needn', 'shan', 'shouldn', 'wasn', 'weren',
    'won', 'wouldn', 'also', 'would', 'could', 'may', 'might', 'shall', 'must',
    'need', 'etc', 'e.g', 'i.e', 'including', 'using', 'used', 'work', 'working',
    'experience', 'required', 'requirements', 'ability', 'strong', 'good',
    'excellent', 'knowledge', 'understanding', 'familiar', 'proficient'
}


def clean_text(text: str) -> str:
    """
    Clean raw text by removing noise while preserving meaningful content.
    
    Args:
        text: Raw input text.
        
    Returns:
        Cleaned text string.
    """
    if not text:
        return ""
    
    # Normalize unicode and whitespace
    text = text.strip()
    
    # Remove URLs
    text = re.sub(r'https?://\S+|www\.\S+', '', text)
    
    # Remove email addresses
    text = re.sub(r'\S+@\S+\.\S+', '', text)
    
    # Remove phone numbers
    text = re.sub(r'[\+]?[\d\s\-\(\)]{10,}', '', text)
    
    # Keep alphanumeric, spaces, dots, hyphens, slashes, plus signs, hashes
    # (important for tech terms like C++, C#, .NET, Node.js)
    text = re.sub(r'[^\w\s\.\-\/\+\#]', ' ', text)
    
    # Collapse multiple spaces
    text = re.sub(r'\s+', ' ', text)
    
    return text.strip()


def normalize_text(text: str) -> str:
    """
    Normalize text to lowercase and remove extra whitespace.
    Preserves technology names that are case-sensitive.
    
    Args:
        text: Cleaned text.
        
    Returns:
        Normalized text.
    """
    return text.lower().strip()


def remove_stop_words(text: str) -> str:
    """
    Remove common stop words from text.
    
    Args:
        text: Normalized text.
        
    Returns:
        Text with stop words removed.
    """
    words = text.split()
    filtered = [w for w in words if w.lower() not in STOP_WORDS]
    return " ".join(filtered)


def preprocess(text: str) -> str:
    """
    Full preprocessing pipeline: clean → normalize → remove stop words.
    
    Args:
        text: Raw input text.
        
    Returns:
        Fully preprocessed text ready for NLP analysis.
    """
    text = clean_text(text)
    text = normalize_text(text)
    text = remove_stop_words(text)
    return text
