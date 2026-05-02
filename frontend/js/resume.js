document.addEventListener('DOMContentLoaded', () => {
  if (!api.requireAuth()) return;
  renderNavbar('resume');
  loadResume();

  const dropZone = document.getElementById('dropZone');
  const fileInput = document.getElementById('fileInput');
  let selectedFile = null;

  dropZone.addEventListener('click', () => fileInput.click());
  dropZone.addEventListener('dragover', e => { e.preventDefault(); dropZone.classList.add('dragover'); });
  dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragover'));
  dropZone.addEventListener('drop', e => {
    e.preventDefault(); dropZone.classList.remove('dragover');
    if (e.dataTransfer.files.length) handleFile(e.dataTransfer.files[0]);
  });
  fileInput.addEventListener('change', e => { if (e.target.files.length) handleFile(e.target.files[0]); });

  function handleFile(file) {
    const ext = file.name.split('.').pop().toLowerCase();
    if (!['pdf', 'docx'].includes(ext)) { showToast('Only PDF and DOCX files are supported', 'error'); return; }
    if (file.size > 10 * 1024 * 1024) { showToast('File too large (max 10MB)', 'error'); return; }
    selectedFile = file;
    document.getElementById('fileName').textContent = file.name;
    document.getElementById('fileSize').textContent = `(${(file.size / 1024).toFixed(1)} KB)`;
    document.getElementById('fileInfo').style.display = 'block';
    document.getElementById('uploadBtn').disabled = false;
  }

  document.getElementById('uploadForm').addEventListener('submit', async e => {
    e.preventDefault();
    if (!selectedFile) return;
    const btn = document.getElementById('uploadBtn');
    btn.disabled = true; btn.textContent = 'Uploading...';
    try {
      const fd = new FormData();
      fd.append('file', selectedFile);
      await api.upload('/resumes/upload', fd);
      showToast('Resume uploaded successfully!', 'success');
      selectedFile = null;
      document.getElementById('fileInfo').style.display = 'none';
      btn.textContent = 'Upload Resume';
      loadResume();
    } catch (err) {
      showToast(err.message, 'error');
      btn.disabled = false; btn.textContent = 'Upload Resume';
    }
  });
});

async function loadResume() {
  const container = document.getElementById('resumeInfo');
  try {
    const r = await api.get('/resumes/my-resume');
    container.innerHTML = `
      <div style="padding:1rem 0">
        <div style="display:flex;align-items:center;gap:12px;margin-bottom:1rem">
          <div style="width:48px;height:48px;border-radius:var(--radius-sm);background:rgba(59,130,246,0.15);display:flex;align-items:center;justify-content:center;font-size:1.5rem">📄</div>
          <div>
            <div style="font-weight:600">${r.fileName}</div>
            <div style="color:var(--text-muted);font-size:0.85rem">${r.fileType.toUpperCase()} • Uploaded ${new Date(r.uploadedAt).toLocaleDateString()}</div>
          </div>
        </div>
        <div style="display:flex;align-items:center;gap:8px">
          <span class="badge ${r.hasExtractedText ? 'badge-green' : 'badge-red'}">${r.hasExtractedText ? '✓ Text Extracted' : '✗ No Text'}</span>
        </div>
        <p style="color:var(--text-secondary);font-size:0.85rem;margin-top:1rem">Your resume is ready for AI matching. Apply to jobs to see your match scores.</p>
      </div>`;
  } catch {
    container.innerHTML = `<div class="empty-state"><h3>No resume uploaded</h3><p>Upload your resume to start applying</p></div>`;
  }
}
