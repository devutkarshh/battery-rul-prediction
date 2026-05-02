// Auth page logic (login + register)
document.addEventListener('DOMContentLoaded', () => {
  // Redirect if already logged in
  if (api.isAuthenticated()) {
    window.location.href = api.isAdmin() ? 'admin-dashboard.html' : 'dashboard.html';
    return;
  }

  const loginForm = document.getElementById('loginForm');
  const registerForm = document.getElementById('registerForm');

  if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const btn = document.getElementById('loginBtn');
      btn.disabled = true; btn.textContent = 'Signing in...';
      try {
        const data = await api.post('/auth/login', {
          email: document.getElementById('email').value,
          password: document.getElementById('password').value
        });
        api.saveAuth(data);
        showToast('Welcome back!', 'success');
        setTimeout(() => {
          window.location.href = data.role === 'ROLE_ADMIN' ? 'admin-dashboard.html' : 'dashboard.html';
        }, 500);
      } catch (err) {
        showToast(err.message, 'error');
        btn.disabled = false; btn.textContent = 'Sign In';
      }
    });
  }

  if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const pw = document.getElementById('password').value;
      const cpw = document.getElementById('confirmPassword').value;
      if (pw !== cpw) { showToast('Passwords do not match', 'error'); return; }

      const btn = document.getElementById('registerBtn');
      btn.disabled = true; btn.textContent = 'Creating account...';
      try {
        const data = await api.post('/auth/register', {
          fullName: document.getElementById('fullName').value,
          email: document.getElementById('email').value,
          password: pw
        });
        api.saveAuth(data);
        showToast('Account created successfully!', 'success');
        setTimeout(() => { window.location.href = 'dashboard.html'; }, 500);
      } catch (err) {
        showToast(err.message, 'error');
        btn.disabled = false; btn.textContent = 'Create Account';
      }
    });
  }
});
