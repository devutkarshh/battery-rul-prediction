document.addEventListener('DOMContentLoaded', async () => {
  if (!api.requireAuth()) return;
  renderNavbar('dashboard');
  const user = api.getUser();
  document.getElementById('welcomeMsg').textContent = `Welcome, ${user.fullName}`;

  try {
    const [apps, jobs] = await Promise.all([
      api.get('/applications/my-applications'),
      api.get('/jobs')
    ]);

    const avgScore = apps.length ? Math.round(apps.reduce((s, a) => s + (a.matchScore || 0), 0) / apps.length) : 0;

    document.getElementById('statsGrid').innerHTML = `
      <div class="stat-card"><div class="stat-icon blue">📋</div>
        <div class="stat-info"><h3>${apps.length}</h3><p>Applications</p></div></div>
      <div class="stat-card"><div class="stat-icon green">✅</div>
        <div class="stat-info"><h3>${apps.filter(a => a.status === 'ACCEPTED').length}</h3><p>Accepted</p></div></div>
      <div class="stat-card"><div class="stat-icon purple">📊</div>
        <div class="stat-info"><h3>${avgScore}%</h3><p>Avg Match Score</p></div></div>
      <div class="stat-card"><div class="stat-icon cyan">💼</div>
        <div class="stat-info"><h3>${jobs.length}</h3><p>Available Jobs</p></div></div>
    `;

    const container = document.getElementById('recentApps');
    if (!apps.length) {
      container.innerHTML = `<div class="empty-state"><h3>No applications yet</h3><p>Start applying to jobs to see them here</p></div>`;
      return;
    }

    container.innerHTML = apps.slice(0, 5).map(a => `
      <div class="card" style="margin-bottom:10px;padding:1rem;cursor:pointer"
           onclick="window.location.href='match-results.html?id=${a.id}'">
        <div style="display:flex;justify-content:space-between;align-items:center">
          <div>
            <strong>${a.jobTitle}</strong>
            <div style="color:var(--text-muted);font-size:0.85rem">${a.company}</div>
          </div>
          <div style="text-align:right">
            <div style="font-size:1.2rem;font-weight:700;color:${a.matchScore >= 70 ? 'var(--accent-green)' : a.matchScore >= 40 ? 'var(--accent-orange)' : 'var(--accent-red)'}">${Math.round(a.matchScore || 0)}%</div>
            <span class="badge badge-${a.status === 'ACCEPTED' ? 'green' : a.status === 'REJECTED' ? 'red' : 'blue'}">${a.status}</span>
          </div>
        </div>
      </div>
    `).join('');
  } catch (err) { showToast(err.message, 'error'); }
});
