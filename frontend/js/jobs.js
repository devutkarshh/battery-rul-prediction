document.addEventListener('DOMContentLoaded', () => {
  if (!api.requireAuth()) return;
  renderNavbar('jobs');
  loadJobs();
  document.getElementById('searchInput').addEventListener('keypress', e => {
    if (e.key === 'Enter') loadJobs();
  });
});

async function loadJobs() {
  const search = document.getElementById('searchInput').value;
  const container = document.getElementById('jobsList');
  container.innerHTML = '<div class="spinner"></div>';
  try {
    const endpoint = search ? `/jobs?search=${encodeURIComponent(search)}` : '/jobs';
    const jobs = await api.get(endpoint);
    if (!jobs.length) {
      container.innerHTML = '<div class="empty-state"><h3>No jobs found</h3><p>Try a different search</p></div>';
      return;
    }
    container.innerHTML = jobs.map(j => {
      const skills = j.requiredSkills.split(',').slice(0, 5);
      const typeColors = { FULL_TIME: 'green', PART_TIME: 'blue', INTERNSHIP: 'purple', CONTRACT: 'orange' };
      return `
        <div class="job-card" onclick="viewJob(${j.id})">
          <div class="job-card-header">
            <div><h3>${j.title}</h3><div class="company">${j.company}</div><div class="location">📍 ${j.location || 'Remote'}</div></div>
            <span class="badge badge-${typeColors[j.jobType] || 'blue'}">${j.jobType.replace('_', ' ')}</span>
          </div>
          <p style="color:var(--text-secondary);font-size:0.85rem;margin:0.75rem 0;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden">${j.description}</p>
          <div class="job-skills">${skills.map(s => `<span class="skill-tag">${s.trim()}</span>`).join('')}</div>
          <div class="job-meta" style="margin-top:1rem;justify-content:space-between;align-items:center">
            <span style="color:var(--accent-green);font-weight:600;font-size:0.85rem">${j.salaryRange || ''}</span>
            <button class="btn btn-primary btn-sm" onclick="event.stopPropagation();applyToJob(${j.id})">Apply Now</button>
          </div>
        </div>`;
    }).join('');
  } catch (err) { showToast(err.message, 'error'); }
}

function viewJob(id) { window.location.href = `job-detail.html?id=${id}`; }

async function applyToJob(jobId) {
  if (!confirm('Apply to this job? Make sure you have uploaded your resume.')) return;
  try {
    const result = await api.post('/applications/apply', { jobId });
    showToast(`Applied! Match score: ${Math.round(result.matchScore)}%`, 'success');
    setTimeout(() => { window.location.href = `match-results.html?id=${result.id}`; }, 1000);
  } catch (err) { showToast(err.message, 'error'); }
}
