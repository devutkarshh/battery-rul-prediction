document.addEventListener('DOMContentLoaded', async () => {
  if (!api.requireAuth()) return;
  renderNavbar('');
  const id = new URLSearchParams(window.location.search).get('id');
  if (!id) { window.location.href = 'dashboard.html'; return; }

  try {
    const a = await api.get(`/applications/${id}/match-result`);
    const score = Math.round(a.matchScore || 0);
    const circumference = 2 * Math.PI * 90;
    const offset = circumference - (score / 100) * circumference;
    const scoreColor = score >= 70 ? '#10b981' : score >= 40 ? '#f59e0b' : '#ef4444';
    const matched = a.matchedSkills ? a.matchedSkills.split(',').map(s => s.trim()).filter(Boolean) : [];
    const missing = a.missingSkills ? a.missingSkills.split(',').map(s => s.trim()).filter(Boolean) : [];
    const recs = a.recommendations ? a.recommendations.split('|').map(s => s.trim()).filter(Boolean) : [];

    document.getElementById('matchContent').innerHTML = `
      <a href="dashboard.html" style="color:var(--text-muted);font-size:0.9rem">← Back to Dashboard</a>
      <div class="card" style="margin-top:1rem;text-align:center;padding:2rem">
        <h2 style="margin-bottom:0.5rem">${a.jobTitle}</h2>
        <p style="color:var(--accent-cyan)">${a.company}</p>
        <span class="badge badge-${a.status==='ACCEPTED'?'green':a.status==='REJECTED'?'red':'blue'}" style="margin-top:8px">${a.status}</span>
      </div>
      <div class="grid-2" style="margin-top:1.5rem">
        <div class="card">
          <div class="score-container">
            <div class="score-circle">
              <svg width="200" height="200" viewBox="0 0 200 200">
                <defs><linearGradient id="scoreGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" style="stop-color:${scoreColor}"/><stop offset="100%" style="stop-color:#3b82f6"/>
                </linearGradient></defs>
                <circle class="score-circle-bg" cx="100" cy="100" r="90"/>
                <circle class="score-circle-progress" cx="100" cy="100" r="90" id="scoreProgress"
                  style="stroke-dasharray:${circumference};stroke-dashoffset:${circumference}"/>
              </svg>
              <div class="score-value">${score}%</div>
            </div>
            <p class="score-label">Overall Match Score</p>
          </div>
        </div>
        <div class="card">
          <div class="card-header"><span class="card-title">Skill Analysis</span></div>
          <h4 style="color:var(--accent-green);margin:1rem 0 0.5rem">✓ Matched Skills (${matched.length})</h4>
          <div class="job-skills">${matched.map(s=>`<span class="skill-tag matched">${s}</span>`).join('') || '<span style="color:var(--text-muted)">None</span>'}</div>
          <h4 style="color:var(--accent-red);margin:1rem 0 0.5rem">✗ Missing Skills (${missing.length})</h4>
          <div class="job-skills">${missing.map(s=>`<span class="skill-tag missing">${s}</span>`).join('') || '<span style="color:var(--text-muted)">None</span>'}</div>
        </div>
      </div>
      ${recs.length ? `<div class="card" style="margin-top:1.5rem">
        <div class="card-header"><span class="card-title">💡 AI Recommendations</span></div>
        ${recs.map(r=>`<div class="recommendation-card">${r}</div>`).join('')}
      </div>` : ''}
      <div style="margin-top:1.5rem;display:flex;gap:12px">
        <a href="jobs.html" class="btn btn-primary">Browse More Jobs</a>
        <a href="dashboard.html" class="btn btn-secondary">Back to Dashboard</a>
      </div>`;

    // Animate score circle
    setTimeout(() => {
      document.getElementById('scoreProgress').style.strokeDashoffset = offset;
    }, 300);
  } catch (err) { showToast(err.message, 'error'); }
});
