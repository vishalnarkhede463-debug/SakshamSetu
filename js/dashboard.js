/**
 * GovSkill Connect - Dashboard & Analytics Controller
 * Powers Student & Employer cockpits, application pipelines, and notifications
 */

document.addEventListener('DOMContentLoaded', () => {
  const path = window.location.pathname.split('/').pop();

  if (path === 'student-dashboard.html') {
    initStudentDashboard();
  } else if (path === 'employer-dashboard.html') {
    initEmployerDashboard();
  } else if (path === 'applications.html') {
    initStudentApplications();
  } else if (path === 'employer-applications.html') {
    initEmployerApplications();
  } else if (path === 'candidate-search.html') {
    initCandidateSearch();
  } else if (path === 'notifications.html') {
    initNotifications();
  }
});

// ----------------------------------------------------------------------------
// 1. Student Dashboard
// ----------------------------------------------------------------------------
async function initStudentDashboard() {
  const user = requireAuth('STUDENT');
  if (!user) return;

  // Set greeting
  const greetingEl = document.getElementById('user-greeting');
  if (greetingEl) {
    const hours = new Date().getHours();
    let timeGreeting = 'Good morning';
    if (hours >= 12 && hours < 17) timeGreeting = 'Good afternoon';
    else if (hours >= 17) timeGreeting = 'Good evening';
    greetingEl.innerText = `${timeGreeting}, ${user.name}`;
  }

  // Load profile & metrics
  try {
    const res = await API.get('/student/profile');
    const profile = res.profile;

    if (profile) {
      // Progress Bar
      const perc = profile.profileCompletion || 50;
      const fill = document.getElementById('dash-profile-progress-fill');
      const val = document.getElementById('dash-profile-progress-val');
      if (fill) fill.style.width = `${perc}%`;
      if (val) val.innerText = `${perc}%`;

      // Verified skills count
      const skills = profile.skillsList || [];
      const verifiedCount = skills.filter(s => s.verified).length;
      setElText('dash-skills-count', verifiedCount);

      // Certificates count
      const certs = profile.certificatesList || [];
      setElText('dash-certs-count', certs.length);
    }

    // Load applications count & recent applications
    const appRes = await API.get('/applications');
    const apps = appRes.applications || [];
    setElText('dash-apps-count', apps.length);
    renderRecentApplications(apps.slice(0, 5));

    // Load saved count
    const savedRes = await API.get('/saved-opportunities');
    const saved = savedRes.saved || [];
    setElText('dash-saved-count', saved.length);

    // Load recommended opportunities
    const oppRes = await API.get('/opportunities');
    const opps = oppRes.opportunities || [];
    renderRecommendedOpportunities(opps.slice(0, 4));

  } catch (err) {
    console.error('Error loading student dashboard:', err);
  }
}

function renderRecommendedOpportunities(opps) {
  const container = document.getElementById('recommended-opps-container');
  if (!container) return;

  if (opps.length === 0) {
    container.innerHTML = '<div class="alert alert-info">No opportunities found matching your profile yet.</div>';
    return;
  }

  let html = '';
  opps.forEach(o => {
    const score = o.matchScore || 75;
    html += `
      <div class="card" style="margin-bottom: 16px;">
        <div class="card-body">
          <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom: 8px;">
            <div>
              <h4 style="font-size:17px; color:var(--gov-navy); margin-bottom:4px;">
                <a href="opportunity-details.html?id=${o.id}">${escapeHtml(o.title)}</a>
              </h4>
              <div style="font-size:13px; color:var(--text-muted); font-weight:600;">
                🏢 ${escapeHtml(o.organization)} • 📍 ${escapeHtml(o.location)}, ${escapeHtml(o.state)}
              </div>
            </div>
            <div class="match-pill" title="Computed compatibility based on your branch and skills">
              🎯 ${score}% Match
            </div>
          </div>
          <div style="font-size:13px; color:var(--text-main); margin-bottom: 12px; line-height:1.5;">
            ${escapeHtml(o.description ? (o.description.length > 140 ? o.description.substring(0, 140) + '...' : o.description) : '')}
          </div>
          <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:8px;">
            <div style="display:flex; gap:6px; flex-wrap:wrap;">
              <span class="badge badge-govt">${o.type.replace('_', ' ')}</span>
              <span class="badge badge-blue">💰 ${escapeHtml(o.stipendOrSalary)}</span>
            </div>
            <a href="opportunity-details.html?id=${o.id}" class="btn btn-outline-primary btn-sm">View & Apply →</a>
          </div>
        </div>
      </div>
    `;
  });
  container.innerHTML = html;
}

function renderRecentApplications(apps) {
  const container = document.getElementById('recent-apps-container');
  if (!container) return;

  if (apps.length === 0) {
    container.innerHTML = '<div class="alert alert-info">You have not applied to any opportunities yet. Browse matching opportunities above!</div>';
    return;
  }

  let html = `
    <table class="gov-table">
      <thead>
        <tr>
          <th>Opportunity</th>
          <th>Organization</th>
          <th>Applied Date</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
  `;

  apps.forEach(a => {
    let statusClass = 'badge-pending';
    if (a.status === 'SHORTLISTED') statusClass = 'badge-govt';
    if (a.status === 'SELECTED') statusClass = 'badge-verified';
    if (a.status === 'REJECTED') statusClass = 'badge-required';

    html += `
      <tr>
        <td><strong><a href="opportunity-details.html?id=${a.opportunityId}">${escapeHtml(a.opportunityTitle || 'Opportunity')}</a></strong></td>
        <td>${escapeHtml(a.organization || '—')}</td>
        <td>${a.appliedAt || 'Recent'}</td>
        <td><span class="badge ${statusClass}">${a.status.replace('_', ' ')}</span></td>
      </tr>
    `;
  });

  html += '</tbody></table>';
  container.innerHTML = html;
}

// ----------------------------------------------------------------------------
// 2. Employer Dashboard
// ----------------------------------------------------------------------------
async function initEmployerDashboard() {
  const user = requireAuth('EMPLOYER');
  if (!user) return;

  try {
    const statsRes = await API.get('/employer/stats');
    if (statsRes) {
      setElText('emp-active-opps', statsRes.activeOpportunities || 0);
      setElText('emp-total-apps', statsRes.totalApplications || 0);
      setElText('emp-shortlisted', statsRes.shortlisted || 0);
      setElText('emp-hired', statsRes.hired || 0);

      const statusBadge = document.getElementById('emp-verified-badge');
      if (statusBadge) {
        if (statsRes.verificationStatus === 'VERIFIED') {
          statusBadge.className = 'badge badge-verified';
          statusBadge.innerText = '✓ Verified Organization';
        } else {
          statusBadge.className = 'badge badge-pending';
          statusBadge.innerText = '⏳ Verification Pending';
        }
      }
    }

    // Load recent employer applications
    const appRes = await API.get('/applications');
    const apps = appRes.applications || [];
    renderEmployerApplicantPipeline(apps.slice(0, 6));

  } catch (err) {
    console.error('Error loading employer dashboard:', err);
  }
}

function renderEmployerApplicantPipeline(apps) {
  const container = document.getElementById('employer-recent-apps-container');
  if (!container) return;

  if (apps.length === 0) {
    container.innerHTML = '<div class="alert alert-info">No candidate applications received yet. Post new opportunities to attract eligible students!</div>';
    return;
  }

  let html = `
    <table class="gov-table">
      <thead>
        <tr>
          <th>Candidate</th>
          <th>Opportunity</th>
          <th>Education / Branch</th>
          <th>Applied Date</th>
          <th>Status</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
  `;

  apps.forEach(a => {
    let statusClass = 'badge-pending';
    if (a.status === 'SHORTLISTED') statusClass = 'badge-govt';
    if (a.status === 'SELECTED') statusClass = 'badge-verified';
    if (a.status === 'REJECTED') statusClass = 'badge-required';

    html += `
      <tr>
        <td><strong>${escapeHtml(a.studentName || 'Candidate')}</strong><br><small style="color:var(--text-muted)">${escapeHtml(a.studentEmail || '')}</small></td>
        <td>${escapeHtml(a.opportunityTitle || '')}</td>
        <td>${escapeHtml(a.studentEducationLevel || '')} • ${escapeHtml(a.studentBranch || '')}</td>
        <td>${a.appliedAt || 'Recent'}</td>
        <td><span class="badge ${statusClass}">${a.status.replace('_', ' ')}</span></td>
        <td>
          <a href="employer-applications.html" class="btn btn-secondary btn-sm">Review & Update</a>
        </td>
      </tr>
    `;
  });

  html += '</tbody></table>';
  container.innerHTML = html;
}

// ----------------------------------------------------------------------------
// 3. Student Applications Tracking Page
// ----------------------------------------------------------------------------
async function initStudentApplications() {
  const user = requireAuth('STUDENT');
  if (!user) return;

  const container = document.getElementById('student-applications-list');
  if (!container) return;

  try {
    const res = await API.get('/applications');
    const list = res.applications || [];

    if (list.length === 0) {
      container.innerHTML = `
        <div class="alert alert-info">
          You have not submitted any applications yet. <a href="opportunities.html">Browse and Apply to Opportunities</a>.
        </div>
      `;
      return;
    }

    let html = '';
    list.forEach(a => {
      let statusStep = 1;
      if (a.status === 'UNDER_REVIEW') statusStep = 2;
      if (a.status === 'SHORTLISTED') statusStep = 3;
      if (a.status === 'SELECTED') statusStep = 4;

      html += `
        <div class="card" style="margin-bottom: 20px;">
          <div class="card-header">
            <div>
              <h3 style="font-size:18px; color:var(--gov-navy); margin-bottom:2px;">${escapeHtml(a.opportunityTitle)}</h3>
              <div style="font-size:13px; color:var(--text-muted);">
                🏢 ${escapeHtml(a.organization)} • 📍 ${escapeHtml(a.location)} • Applied on: ${a.appliedAt}
              </div>
            </div>
            <div>
              <span class="badge ${a.status === 'SELECTED' ? 'badge-verified' : a.status === 'REJECTED' ? 'badge-required' : 'badge-govt'}">
                ${a.status.replace('_', ' ')}
              </span>
            </div>
          </div>
          <div class="card-body">
            <div class="timeline-stepper">
              <div class="timeline-step ${statusStep >= 1 ? 'completed' : ''}">
                <div class="timeline-circle">1</div>
                <div class="timeline-label">Applied</div>
              </div>
              <div class="timeline-step ${statusStep >= 2 ? 'completed' : statusStep === 1 ? 'active' : ''}">
                <div class="timeline-circle">2</div>
                <div class="timeline-label">Under Review</div>
              </div>
              <div class="timeline-step ${statusStep >= 3 ? 'completed' : statusStep === 2 ? 'active' : ''}">
                <div class="timeline-circle">3</div>
                <div class="timeline-label">Shortlisted</div>
              </div>
              <div class="timeline-step ${statusStep >= 4 ? 'completed' : statusStep === 3 ? 'active' : ''}">
                <div class="timeline-circle">4</div>
                <div class="timeline-label">${a.status === 'REJECTED' ? 'Rejected' : 'Selected'}</div>
              </div>
            </div>

            <!-- Interview Invitation Card for Candidate -->
            ${a.interview ? `
              <div style="margin-top:16px; background:${a.interview.type === 'ONLINE' ? '#f0f9ff' : '#fffbeb'}; border:1px solid ${a.interview.type === 'ONLINE' ? '#bae6fd' : '#fde68a'}; border-radius:6px; padding:16px;">
                <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:8px; margin-bottom:8px;">
                  <span class="badge" style="background:${a.interview.type === 'ONLINE' ? '#0284c7' : '#d97706'}; color:#fff; font-weight:800;">
                    ${a.interview.type === 'ONLINE' ? '📹 ONLINE VIDEO INTERVIEW SCHEDULED' : '🏛️ OFFLINE IN-PERSON INTERVIEW SCHEDULED'}
                  </span>
                  <span style="font-size:12px; font-weight:700; color:${a.interview.status === 'ACCEPTED' ? '#15803d' : '#b45309'};">
                    Response Status: ${a.interview.status}
                  </span>
                </div>

                <div style="font-size:15px; font-weight:800; color:var(--gov-navy); margin-bottom:6px;">
                  🗓️ ${a.interview.date} at ${a.interview.time || '10:30 AM'}
                </div>

                ${a.interview.type === 'ONLINE' ? `
                  <div style="font-size:13px; color:#0369a1; margin-bottom:10px;">
                    Meeting Platform: <strong>Google Meet / Virtual Panel</strong><br>
                    <a href="${a.interview.link || 'https://meet.google.com/saksham-setu-interview'}" target="_blank" class="btn btn-primary btn-sm" style="margin-top:6px; display:inline-flex; align-items:center; gap:6px;">
                      Join Video Meeting 📹
                    </a>
                  </div>
                ` : `
                  <div style="font-size:13px; color:#92400e; margin-bottom:10px;">
                    Reporting Venue: <strong>${escapeHtml(a.interview.venue || 'Corporate Office')}</strong>
                  </div>
                `}

                ${a.interview.notes ? `
                  <div style="font-size:12px; color:#475569; background:#ffffff; padding:8px 10px; border-radius:4px; border:1px dashed var(--border-light); margin-bottom:10px;">
                    <strong>Instructions:</strong> ${escapeHtml(a.interview.notes)}
                  </div>
                ` : ''}

                <!-- Candidate Response Buttons -->
                ${a.interview.status === 'INVITED' ? `
                  <div style="display:flex; gap:8px; align-items:center; margin-top:8px;">
                    <button class="btn btn-primary btn-sm" onclick="handleInterviewResponse(${a.id}, 'ACCEPTED')">
                      ✓ Confirm Attendance
                    </button>
                    <button class="btn btn-secondary btn-sm" onclick="handleInterviewResponse(${a.id}, 'DECLINED')">
                      ✕ Decline / Request Reschedule
                    </button>
                  </div>
                ` : a.interview.status === 'ACCEPTED' ? `
                  <div style="font-size:12px; color:#15803d; font-weight:700;">
                    ✓ You have confirmed attendance for this interview session.
                  </div>
                ` : `
                  <div style="font-size:12px; color:#b91c1c; font-weight:700;">
                    ✕ You declined this interview slot.
                  </div>
                `}
              </div>
            ` : ''}

            ${a.coverNote ? `<div style="font-size:13px; background:#f8fafc; padding:10px; border-radius:4px; margin-top:10px;"><strong>Cover Note:</strong> ${escapeHtml(a.coverNote)}</div>` : ''}
          </div>
        </div>
      `;
    });

    container.innerHTML = html;
  } catch (err) {
    container.innerHTML = `<div class="alert alert-danger">Error fetching applications.</div>`;
  }
}

async function handleInterviewResponse(appId, resp) {
  try {
    const res = await API.post(`/applications/${appId}/interview-response`, { response: resp });
    if (res && res.success) {
      alert(`Interview attendance marked as ${resp}. The recruiter has been notified.`);
      initStudentApplications();
    }
  } catch (err) {
    alert('Failed to submit response: ' + err.message);
  }
}

// ----------------------------------------------------------------------------
// 4. Employer Applications Review & Status Update Page
// ----------------------------------------------------------------------------
async function initEmployerApplications() {
  const user = requireAuth('EMPLOYER');
  if (!user) return;

  const container = document.getElementById('employer-apps-table-container');
  if (!container) return;

  try {
    const res = await API.get('/applications');
    const list = res.applications || [];

    if (list.length === 0) {
      container.innerHTML = '<div class="alert alert-info">No applications received yet.</div>';
      return;
    }
 
    let html = `
      <table class="gov-table">
        <thead>
          <tr>
            <th>Candidate Name</th>
            <th>Opportunity</th>
            <th>Branch & College</th>
            <th>Status</th>
            <th>Interview Workflow</th>
            <th>Update Decision</th>
          </tr>
        </thead>
        <tbody>
    `;

    list.forEach(a => {
      const intv = a.interview;
      let interviewCol = '';

      if (intv) {
        const isOnline = intv.type === 'ONLINE';
        let respColor = intv.status === 'ACCEPTED' ? '#15803d' : (intv.status === 'DECLINED' ? '#b91c1c' : '#b45309');
        interviewCol = `
          <div style="font-size:12px;">
            <span class="badge" style="background:${isOnline ? '#e0f2fe' : '#fef3c7'}; color:${isOnline ? '#0369a1' : '#b45309'}; font-weight:700;">
              ${isOnline ? '📹 Online Video' : '🏛️ Offline In-Person'}
            </span>
            <div style="font-weight:700; color:var(--gov-navy); margin-top:2px;">
              ${intv.date} (${intv.time || '10:30 AM'})
            </div>
            <div style="font-size:11px; color:${respColor}; font-weight:700; margin-top:1px;">
              Status: ${intv.status}
            </div>
            <button class="btn btn-outline-primary btn-sm" style="font-size:10px; padding:2px 6px; margin-top:4px;" onclick="openScheduleModal(${a.id}, '${escapeHtml(a.studentName)}', '${escapeHtml(a.opportunityTitle)}')">
              Reschedule
            </button>
          </div>
        `;
      } else {
        interviewCol = `
          <button class="btn btn-primary btn-sm" style="font-size:11px; padding:5px 10px;" onclick="openScheduleModal(${a.id}, '${escapeHtml(a.studentName)}', '${escapeHtml(a.opportunityTitle)}')">
            🎙️ Invite for Interview
          </button>
        `;
      }

     
      html += `
        <tr>
          <td>
            <strong>${escapeHtml(a.studentName)}</strong><br>
            <small style="color:var(--text-muted)">${escapeHtml(a.studentEmail)} • ${escapeHtml(a.studentPhone || '')}</small>
          </td>
          <td>${escapeHtml(a.opportunityTitle)}</td>
          <td>${escapeHtml(a.studentBranch || '—')}<br><small style="color:var(--text-muted)">${escapeHtml(a.studentCollege || '')}</small></td>
          <td>
            <span class="badge ${a.status === 'SELECTED' ? 'badge-verified' : a.status === 'REJECTED' ? 'badge-required' : 'badge-govt'}" id="status-badge-${a.id}">
              ${a.status.replace('_', ' ')}
            </span>
          </td>
          <td>${interviewCol}</td>
          <td>
            <select class="form-select" style="font-size:12px; padding:4px 8px; width:auto;" onchange="handleUpdateAppStatus(${a.id}, this.value)">
              <option value="APPLIED" ${a.status === 'APPLIED' ? 'selected' : ''}>Applied</option>
              <option value="UNDER_REVIEW" ${a.status === 'UNDER_REVIEW' ? 'selected' : ''}>Under Review</option>
              <option value="SHORTLISTED" ${a.status === 'SHORTLISTED' ? 'selected' : ''}>Shortlisted</option>
              <option value="SELECTED" ${a.status === 'SELECTED' ? 'selected' : ''}>Selected / Hired</option>
              <option value="REJECTED" ${a.status === 'REJECTED' ? 'selected' : ''}>Rejected</option>
            </select>
          </td>
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;
  } catch (err) {
    container.innerHTML = '<div class="alert alert-danger">Failed to load candidate applications.</div>';
  }
}

function openScheduleModal(appId, name, title) {
  const modal = document.getElementById('schedule-interview-modal');
  if (!modal) return;

  document.getElementById('sched-app-id').value = appId;
  document.getElementById('sched-modal-title').innerText = `🎙️ Schedule Interview: ${name}`;
  document.getElementById('sched-candidate-info').innerText = `Sending interview invitation for "${title}".`;

  // Default to tomorrow's date
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  document.getElementById('sched-date').value = tomorrow.toISOString().split('T')[0];

  modal.classList.add('active');
}

function closeScheduleModal() {
  const modal = document.getElementById('schedule-interview-modal');
  if (modal) modal.classList.remove('active');
}

function toggleInterviewModeFields() {
  const type = document.getElementById('sched-type').value;
  const linkBox = document.getElementById('field-online-link');
  const venueBox = document.getElementById('field-offline-venue');

  if (type === 'ONLINE') {
    if (linkBox) linkBox.style.display = 'block';
    if (venueBox) venueBox.style.display = 'none';
  } else {
    if (linkBox) linkBox.style.display = 'none';
    if (venueBox) venueBox.style.display = 'block';
  }
}

async function submitInterviewSchedule() {
  const appId = document.getElementById('sched-app-id').value;
  const type = document.getElementById('sched-type').value;
  const date = document.getElementById('sched-date').value;
  const time = document.getElementById('sched-time').value;
  const link = document.getElementById('sched-link').value.trim();
  const venue = document.getElementById('sched-venue').value.trim();
  const notes = document.getElementById('sched-notes').value.trim();

  if (!date) {
    alert('Please select an interview date.');
    return;
  }

  const btn = document.getElementById('btn-confirm-schedule');
  if (btn) {
    btn.disabled = true;
    btn.innerText = 'Dispatching Invitation...';
  }

  try {
    const res = await API.post(`/applications/${appId}/interview`, {
      type,
      date,
      time,
      link,
      venue,
      notes
    });

    if (res && res.success) {
      alert(`Interview successfully scheduled! Invitation dispatched to candidate.`);
      closeScheduleModal();
      initEmployerApplications();
    } else {
      alert('Failed to schedule: ' + (res ? res.error : 'Unknown error'));
    }
  } catch (err) {
    alert('Error communicating with service: ' + err.message);
  } finally {
    if (btn) {
      btn.disabled = false;
      btn.innerText = 'Dispatch Interview Invitation 🚀';
    }
  }
}

async function handleUpdateAppStatus(appId, newStatus) {
  try {
    const res = await API.put(`/applications/${appId}`, { status: newStatus });
    if (res && res.success) {
      const badge = document.getElementById(`status-badge-${appId}`);
      if (badge) {
        badge.innerText = newStatus.replace('_', ' ');
        badge.className = `badge ${newStatus === 'SELECTED' ? 'badge-verified' : newStatus === 'REJECTED' ? 'badge-required' : 'badge-govt'}`;
      }
      alert(`Application updated to ${newStatus.replace('_', ' ')}. Automated notification dispatched to the candidate.`);
    }
  } catch (err) {
    alert('Failed to update status: ' + err.message);
  }
}

// ----------------------------------------------------------------------------
// 5. Candidate Search (Strict Privacy Filtering)
// ----------------------------------------------------------------------------
let CANDIDATE_CACHE = []; // holds the last-loaded candidate objects so the
                           // profile modal can look them up by index instead
                           // of round-tripping JSON through an onclick attribute

async function initCandidateSearch() {
  const user = requireAuth('EMPLOYER');
  if (!user) return;

  const form = document.getElementById('candidate-search-form');
  if (form) {
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      loadCandidates();
    });
  }

  loadCandidates();
}

async function loadCandidates() {
  const container = document.getElementById('candidates-results-container');
  if (!container) return;

  const skill = document.getElementById('cand-skill')?.value || '';
  const branch = document.getElementById('cand-branch')?.value || '';
  const edu = document.getElementById('cand-edu')?.value || '';
  const loc = document.getElementById('cand-loc')?.value || '';
  const cat = document.getElementById('cand-cat')?.value || '';

  container.innerHTML = '<div class="alert alert-info">Querying verified candidate registry...</div>';

  try {
    const res = await API.get('/employer/candidates', {
      skill, branch, education: edu, location: loc, category: cat
    });

    const candidates = res.candidates || [];
    CANDIDATE_CACHE = candidates; // cache for the "View Full Profile" modal lookup
    setElText('candidates-count-lbl', `${candidates.length} Profiles Found`);

    if (candidates.length === 0) {
      container.innerHTML = `
        <div class="alert alert-warning">
          No candidates found matching criteria. Note: Only students who have explicitly granted privacy consent (<strong>consent_given = true</strong> and <strong>profile_visibility = true</strong>) are accessible in candidate discovery.
        </div>
      `;
      return;
    }

    let html = '<div style="display:grid; grid-template-columns:repeat(auto-fill, minmax(320px, 1fr)); gap:20px;">';

    candidates.forEach((c, idx) => {
      
      const skills = c.skillsList || [];
      const verifiedSkills = skills.filter(s => s.verified);

      html += `
        <div class="card">
          <div class="card-body">
            <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:10px;">
              <div>
                <h4 style="font-size:18px; color:var(--gov-navy); margin-bottom:2px;">${escapeHtml(c.name)}</h4>
                <div style="font-size:13px; color:var(--text-muted)">
                  🎓 ${escapeHtml(c.educationLevel || 'Degree')} • ${escapeHtml(c.branch || '—')}
                </div>
              </div>
              <span class="badge badge-verified">${c.profileCompletion}% Complete</span>
            </div>

            <div style="font-size:13px; margin-bottom:12px;">
              📍 <strong>Location:</strong> ${escapeHtml(c.city || '')}, ${escapeHtml(c.state || '')}<br>
              🏛️ <strong>Institution:</strong> ${escapeHtml(c.college || 'Government Recognized College')}
            </div>

            <div style="margin-bottom:14px;">
              <div style="font-size:12px; font-weight:700; color:var(--text-muted); margin-bottom:6px;">VERIFIED SKILLS:</div>
              <div style="display:flex; gap:6px; flex-wrap:wrap;">
                ${verifiedSkills.length > 0 
                  ? verifiedSkills.map(s => `<span class="badge badge-verified">✓ ${escapeHtml(s.name)}</span>`).join('') 
                  : '<span style="font-size:12px; color:var(--text-muted);">No verified skill certificates yet</span>'}
              </div>
            </div>

            <div style="padding-top:12px; border-top:1px solid var(--border-light); display:flex; justify-content:space-between; align-items:center;">
              <span class="badge badge-govt">Consent Granted</span>
              <button class="btn btn-outline-primary btn-sm" onclick="viewCandidateModal(${idx})">View Full Profile</button>
            </div>
          </div>
        </div>
      `;
    });

    html += '</div>';
    container.innerHTML = html;
  } catch (err) {
    container.innerHTML = `<div class="alert alert-danger">Error querying candidates: ${err.message}</div>`;
  }
}

function viewCandidateModal(index) {
  try {
    const c = CANDIDATE_CACHE[index];
    if (!c) return;
    const modal = document.getElementById('candidate-modal');
    const content = document.getElementById('candidate-modal-content');
    if (!modal || !content) return;

    content.innerHTML = `
      <h3 style="color:var(--gov-navy); margin-bottom:12px;">${escapeHtml(c.name)}</h3>
      <p style="font-size:14px; margin-bottom:14px;">${escapeHtml(c.bio || 'Registered candidate seeking national opportunity placement.')}</p>
      
      <div style="background:#f8fafc; padding:14px; border-radius:4px; margin-bottom:16px; font-size:13px;">
        <div><strong>Email:</strong> ${escapeHtml(c.email)}</div>
        <div><strong>Phone:</strong> ${escapeHtml(c.phone)}</div>
        <div><strong>State & City:</strong> ${escapeHtml(c.city)}, ${escapeHtml(c.state)}</div>
        <div><strong>Branch:</strong> ${escapeHtml(c.branch || '—')}</div>
        <div><strong>College:</strong> ${escapeHtml(c.college || '—')}</div>
        <div><strong>Category:</strong> ${c.category}</div>
      </div>

      <h4 style="font-size:15px; color:var(--gov-navy); margin-bottom:8px;">Education History:</h4>
      ${(c.educationList || []).map(e => `
        <div style="font-size:13px; margin-bottom:6px; padding:6px; background:#fff; border:1px solid var(--border-light); border-radius:3px;">
          <strong>${escapeHtml(e.qualification)}</strong> - ${escapeHtml(e.institution)} (${e.percentage}%, Class of ${e.passingYear})
        </div>
      `).join('') || '<p style="font-size:13px; color:var(--text-muted);">No detailed education records.</p>'}

      <h4 style="font-size:15px; color:var(--gov-navy); margin:14px 0 8px;">Skills & Certifications:</h4>
      <div style="display:flex; gap:6px; flex-wrap:wrap;">
        ${(c.skillsList || []).map(s => `
          <span class="badge ${s.verified ? 'badge-verified' : 'badge-blue'}">${s.verified ? '✓ ' : ''}${escapeHtml(s.name)} (${s.level})</span>
        `).join('')}
      </div>
    `;

    modal.classList.add('active');
  } catch (err) {
    console.error(err);
  }
}

function closeCandidateModal() {
  const modal = document.getElementById('candidate-modal');
  if (modal) modal.classList.remove('active');
}

// ----------------------------------------------------------------------------
// 6. Notifications
// ----------------------------------------------------------------------------
async function initNotifications() {
  const user = requireAuth();
  if (!user) return;

  const container = document.getElementById('notifications-list-container');
  if (!container) return;

  try {
    const res = await API.get('/notifications');
    const list = res.notifications || [];

    if (list.length === 0) {
      container.innerHTML = '<div class="alert alert-info">Your notification inbox is clear.</div>';
      return;
    }

    let html = '';
    list.forEach(n => {
      html += `
        <div class="card" style="margin-bottom:12px; ${!n.read ? 'border-left:4px solid var(--gov-saffron);' : ''}">
          <div class="card-body" style="display:flex; justify-content:space-between; align-items:center;">
            <div>
              <div style="font-weight:700; color:var(--gov-navy); font-size:15px; margin-bottom:4px;">
                ${escapeHtml(n.title)} ${!n.read ? '<span class="badge badge-govt">NEW</span>' : ''}
              </div>
              <p style="font-size:13px; color:var(--text-main); margin-bottom:4px;">${escapeHtml(n.message)}</p>
              <span style="font-size:11px; color:var(--text-muted);">${n.createdAt}</span>
            </div>
            ${!n.read ? `<button class="btn btn-secondary btn-sm" onclick="markNotificationRead(${n.id})">Mark as Read</button>` : ''}
          </div>
        </div>
      `;
    });

    container.innerHTML = html;
  } catch (err) {
    container.innerHTML = '<div class="alert alert-danger">Error loading notifications.</div>';
  }
}

async function markNotificationRead(id) {
  await API.put(`/notifications/${id}`);
  initNotifications();
}

async function markAllNotificationsRead() {
  await API.put('/notifications/read-all');
  initNotifications();
}

function setElText(id, text) {
  const el = document.getElementById(id);
  if (el) el.innerText = text;
}