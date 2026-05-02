/**
 * API Client — Centralized fetch wrapper with JWT token management.
 */
const API_BASE = 'http://localhost:8080/api';

const api = {
  /** Get stored JWT token */
  getToken() { return localStorage.getItem('jwt_token'); },

  /** Get stored user info */
  getUser() {
    const u = localStorage.getItem('user');
    return u ? JSON.parse(u) : null;
  },

  /** Save auth data after login/register */
  saveAuth(data) {
    localStorage.setItem('jwt_token', data.token);
    localStorage.setItem('user', JSON.stringify({
      id: data.userId, fullName: data.fullName,
      email: data.email, role: data.role
    }));
  },

  /** Clear auth data (logout) */
  logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
  },

  /** Check if user is authenticated */
  isAuthenticated() { return !!this.getToken(); },

  /** Check if user is admin */
  isAdmin() {
    const u = this.getUser();
    return u && u.role === 'ROLE_ADMIN';
  },

  /** Redirect to login if not authenticated */
  requireAuth() {
    if (!this.isAuthenticated()) { window.location.href = 'index.html'; return false; }
    return true;
  },

  /** Core fetch wrapper */
  async request(endpoint, options = {}) {
    const url = `${API_BASE}${endpoint}`;
    const headers = options.headers || {};
    const token = this.getToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;
    if (!(options.body instanceof FormData)) {
      headers['Content-Type'] = 'application/json';
    }

    try {
      const res = await fetch(url, { ...options, headers });
      if (res.status === 401) { this.logout(); return; }
      if (res.status === 204) return null;

      const data = await res.json().catch(() => null);
      if (!res.ok) throw new Error(data?.message || `Request failed (${res.status})`);
      return data;
    } catch (err) {
      if (err.message.includes('Failed to fetch'))
        throw new Error('Cannot connect to server. Is the backend running?');
      throw err;
    }
  },

  get(endpoint) { return this.request(endpoint); },
  post(endpoint, body) {
    return this.request(endpoint, { method: 'POST', body: JSON.stringify(body) });
  },
  put(endpoint, body) {
    return this.request(endpoint, { method: 'PUT', body: JSON.stringify(body) });
  },
  delete(endpoint) { return this.request(endpoint, { method: 'DELETE' }); },
  upload(endpoint, formData) {
    return this.request(endpoint, { method: 'POST', body: formData, headers: {} });
  }
};

/* ── Toast Notifications ── */
function showToast(message, type = 'info') {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  container.appendChild(toast);
  setTimeout(() => { toast.remove(); }, 4000);
}

/* ── Navbar Renderer ── */
function renderNavbar(activePage = '') {
  const user = api.getUser();
  if (!user) return;

  const isAdmin = user.role === 'ROLE_ADMIN';
  const initials = user.fullName.split(' ').map(n => n[0]).join('').toUpperCase();

  const navLinks = isAdmin ? `
    <a href="admin-dashboard.html" class="${activePage === 'admin-dashboard' ? 'active' : ''}">Dashboard</a>
    <a href="admin-jobs.html" class="${activePage === 'admin-jobs' ? 'active' : ''}">Manage Jobs</a>
    <a href="admin-applicants.html" class="${activePage === 'admin-applicants' ? 'active' : ''}">Applicants</a>
  ` : `
    <a href="dashboard.html" class="${activePage === 'dashboard' ? 'active' : ''}">Dashboard</a>
    <a href="jobs.html" class="${activePage === 'jobs' ? 'active' : ''}">Jobs</a>
    <a href="resume.html" class="${activePage === 'resume' ? 'active' : ''}">Resume</a>
  `;

  document.getElementById('navbar').innerHTML = `
    <a href="${isAdmin ? 'admin-dashboard.html' : 'dashboard.html'}" class="navbar-brand">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M20 7H4a2 2 0 00-2 2v6a2 2 0 002 2h16a2 2 0 002-2V9a2 2 0 00-2-2z"/>
        <path d="M16 21V5a2 2 0 00-2-2h-4a2 2 0 00-2 2v16"/>
      </svg>
      JobPortal AI
    </a>
    <nav class="navbar-nav">
      ${navLinks}
      <div class="nav-user">
        <div class="nav-user-avatar">${initials}</div>
        <span class="nav-user-name">${user.fullName}</span>
      </div>
      <button onclick="api.logout()">Logout</button>
    </nav>
  `;
}
