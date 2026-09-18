/**
 * GovSkill Connect - Authentication & Session Script
 * Handles Registration, Login, Role Routing, and Route Guards
 */

document.addEventListener('DOMContentLoaded', () => {
  updateHeaderAuthState();
  initLoginForm();
  initRegisterForm();
});

function updateHeaderAuthState() {
  const user = API.getUser();
  const authContainer = document.getElementById('header-auth-container');
  if (!authContainer) return;

  if (user) {
    let dashboardLink = 'student-dashboard.html';
    if (user.role === 'EMPLOYER') dashboardLink = 'employer-dashboard.html';
    else if (user.role === 'ADMIN') dashboardLink = 'admin-dashboard.html';

    authContainer.innerHTML = `
      <div style="display:flex; align-items:center; gap:10px;">
        <a href="${dashboardLink}" class="btn btn-outline-primary btn-sm">
          <span>🏛️</span> <strong>${escapeHtml(user.name.split(' ')[0])}</strong> (${user.role})
        </a>
        <button onclick="handleLogout()" class="btn btn-secondary btn-sm" title="Sign Out">Logout</button>
      </div>
    `;
  } else {
    authContainer.innerHTML = `
      <a href="login.html" class="btn btn-primary btn-sm">Login</a>
      <a href="register.html" class="btn btn-saffron btn-sm">Register</a>
    `;
  }
}

function initLoginForm() {
  const form = document.getElementById('login-form');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertBox = document.getElementById('login-alert');
    const submitBtn = form.querySelector('button[type="submit"]');

    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value.trim();

    if (!email || !password) {
      showAlert(alertBox, 'danger', 'Please enter your email and password.');
      return;
    }

    try {
      submitBtn.disabled = true;
      submitBtn.innerText = 'Authenticating...';

      const res = await API.post('/auth/login', { email, password });
      if (res && res.success) {
        API.setToken(res.token);
        API.setUser(res.user);
        if (res.profile) {
          localStorage.setItem('govskill_profile', JSON.stringify(res.profile));
        }

        showAlert(alertBox, 'success', 'Login successful! Redirecting...');

        // Role-based routing
        setTimeout(() => {
          if (res.user.role === 'EMPLOYER') {
            window.location.href = 'employer-dashboard.html';
          } else if (res.user.role === 'ADMIN') {
            window.location.href = 'admin-dashboard.html';
          } else {
            window.location.href = 'student-dashboard.html';
          }
        }, 600);
      } else {
        showAlert(alertBox, 'danger', res.error || 'Invalid credentials. Please verify and try again.');
        submitBtn.disabled = false;
        submitBtn.innerText = 'Sign In';
      }
    } catch (err) {
      showAlert(alertBox, 'danger', err.message || 'Server connection failed. Try again.');
      submitBtn.disabled = false;
      submitBtn.innerText = 'Sign In';
    }
  });
}

function initRegisterForm() {
  const form = document.getElementById('register-form');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertBox = document.getElementById('register-alert');
    const submitBtn = form.querySelector('button[type="submit"]');

    const role = document.querySelector('input[name="role"]:checked')?.value || 'STUDENT';
    const email = document.getElementById('reg-email').value.trim();
    const phone = document.getElementById('reg-phone').value.trim();
    const password = document.getElementById('reg-password').value;
    const confirmPassword = document.getElementById('reg-confirm-password').value;

    if (password !== confirmPassword) {
      showAlert(alertBox, 'danger', 'Passwords do not match. Please re-enter.');
      return;
    }

    if (password.length < 6) {
      showAlert(alertBox, 'danger', 'Password must be at least 6 characters long.');
      return;
    }

    let payload = { role, email, phone, password };

    if (role === 'STUDENT') {
      const name = document.getElementById('reg-name').value.trim();
      const state = document.getElementById('reg-state').value;
      const city = document.getElementById('reg-city').value.trim();
      if (!name || !city) {
        showAlert(alertBox, 'danger', 'Please fill in all mandatory student fields.');
        return;
      }
      payload = { ...payload, name, state, city };
    } else {
      const orgName = document.getElementById('reg-org-name').value.trim();
      const orgType = document.getElementById('reg-org-type').value;
      const state = document.getElementById('reg-org-state')?.value || 'Delhi';
      const city = document.getElementById('reg-org-city')?.value || 'New Delhi';
      if (!orgName) {
        showAlert(alertBox, 'danger', 'Please enter your organization name.');
        return;
      }
      payload = { ...payload, organizationName: orgName, organizationType: orgType, state, city };
    }

    try {
      submitBtn.disabled = true;
      submitBtn.innerText = 'Registering Account...';

      const res = await API.post('/auth/register', payload);
      if (res && res.success) {
        API.setToken(res.token);
        API.setUser(res.user);
        if (res.profile) {
          localStorage.setItem('govskill_profile', JSON.stringify(res.profile));
        }

        showAlert(alertBox, 'success', 'Registration successful! Directing to your workspace...');
        setTimeout(() => {
          if (role === 'EMPLOYER') {
            window.location.href = 'employer-dashboard.html';
          } else {
            window.location.href = 'student-profile.html'; // Direct to profile onboarding
          }
        }, 800);
      } else {
        showAlert(alertBox, 'danger', res.error || 'Registration failed.');
        submitBtn.disabled = false;
        submitBtn.innerText = 'Create Account';
      }
    } catch (err) {
      showAlert(alertBox, 'danger', err.message || 'Server error during registration.');
      submitBtn.disabled = false;
      submitBtn.innerText = 'Create Account';
    }
  });
}

function handleLogout() {
  API.post('/auth/logout', {}).finally(() => {
    API.clearSession();
    window.location.href = 'index.html';
  });
}

function requireAuth(allowedRoles) {
  const user = API.getUser();
  if (!user) {
    window.location.href = 'login.html';
    return null;
  }

  if (allowedRoles) {
    const roles = Array.isArray(allowedRoles) ? allowedRoles : [allowedRoles];
    if (!roles.includes(user.role)) {
      alert('Access Denied: You do not have permissions to access this government workspace.');
      if (user.role === 'STUDENT') window.location.href = 'student-dashboard.html';
      else if (user.role === 'EMPLOYER') window.location.href = 'employer-dashboard.html';
      else window.location.href = 'index.html';
      return null;
    }
  }

  return user;
}

function showAlert(alertBox, type, message) {
  if (!alertBox) return;
  alertBox.className = `alert alert-${type}`;
  alertBox.innerText = message;
  alertBox.style.display = 'block';
}

function escapeHtml(str) {
  if (!str) return '';
  const div = document.createElement('div');
  div.innerText = str;
  return div.innerHTML;
}
