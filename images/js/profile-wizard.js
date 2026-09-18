/**
 * GovSkill Connect - 10-Step Student Profile Onboarding & Management Wizard
 * Implements real step-by-step database saving, consent controls, education/skills CRUD,
 * and certificate document upload.
 */

let currentStep = 1;
const totalSteps = 10;
let studentProfile = null;

document.addEventListener('DOMContentLoaded', async () => {
  const user = requireAuth('STUDENT');
  if (!user) return;

  await loadStudentProfile();
  initWizardNav();
  renderStep(currentStep);
});

async function loadStudentProfile() {
  try {
    const res = await API.get('/student/profile');
    if (res && res.profile) {
      studentProfile = res.profile;
      updateUIWithProfileData();
    }
  } catch (err) {
    console.error('Failed to load student profile:', err);
  }
}

function initWizardNav() {
  const indicators = document.querySelectorAll('.step-indicator');
  indicators.forEach(ind => {
    ind.addEventListener('click', () => {
      const step = parseInt(ind.getAttribute('data-step'));
      if (step && step <= totalSteps) {
        saveCurrentStepData().then(() => {
          renderStep(step);
        });
      }
    });
  });

  const btnNext = document.getElementById('wizard-next-btn');
  const btnPrev = document.getElementById('wizard-prev-btn');

  if (btnNext) {
    btnNext.addEventListener('click', async () => {
      btnNext.disabled = true;
      btnNext.innerText = 'Saving...';
      const saved = await saveCurrentStepData();
      btnNext.disabled = false;
      btnNext.innerText = 'Next Step →';

      if (saved && currentStep < totalSteps) {
        renderStep(currentStep + 1);
      } else if (currentStep === totalSteps) {
        alert('Your National Profile is updated and synchronized with the Government Portal.');
        window.location.href = 'student-dashboard.html';
      }
    });
  }

  if (btnPrev) {
    btnPrev.addEventListener('click', () => {
      if (currentStep > 1) {
        renderStep(currentStep - 1);
      }
    });
  }
}

function renderStep(step) {
  currentStep = step;

  // Hide all step panels
  for (let i = 1; i <= totalSteps; i++) {
    const panel = document.getElementById(`step-panel-${i}`);
    if (panel) panel.style.display = i === step ? 'block' : 'none';
  }

  // Update Stepper Navigation
  const indicators = document.querySelectorAll('.step-indicator');
  indicators.forEach(ind => {
    const s = parseInt(ind.getAttribute('data-step'));
    ind.classList.remove('active');
    if (s === step) {
      ind.classList.add('active');
      ind.scrollIntoView({ behavior: 'smooth', inline: 'nearest' });
    }
  });

  // Buttons state
  const btnPrev = document.getElementById('wizard-prev-btn');
  const btnNext = document.getElementById('wizard-next-btn');
  if (btnPrev) btnPrev.style.visibility = step === 1 ? 'hidden' : 'visible';
  if (btnNext) btnNext.innerText = step === totalSteps ? 'Finish & View Dashboard ✓' : 'Save & Next →';

  // Step specific renders
  if (step === 4) loadEducationTable();
  if (step === 6) loadSkillsTable();
  if (step === 7) loadCertificatesTable();
  if (step === 10) renderSummary();
}

function updateUIWithProfileData() {
  if (!studentProfile) return;

  const user = API.getUser();

  // Step 1
  setVal('acc-name', user ? user.name : '');
  setVal('acc-email', user ? user.email : '');
  setVal('acc-phone', user ? user.phone : '');
  setVal('acc-state', user ? user.state : '');
  setVal('acc-city', user ? user.city : '');

  // Step 2 (Consent)
  const consentToggle = document.getElementById('consent-toggle');
  if (consentToggle) consentToggle.checked = studentProfile.consentGiven;

  const visibilityToggle = document.getElementById('visibility-toggle');
  if (visibilityToggle) visibilityToggle.checked = studentProfile.profileVisibility;

  // Step 3 (Personal)
  setVal('pers-dob', studentProfile.dateOfBirth || '');
  setVal('pers-gender', studentProfile.gender || 'PREFER_NOT_TO_SAY');
  setVal('pers-category', studentProfile.category || 'GENERAL');
  setVal('pers-bio', studentProfile.bio || '');

  // Step 5 (Branch & College)
  setVal('edu-level', studentProfile.educationLevel || 'Undergraduate');
  setVal('edu-branch', studentProfile.branch || 'Computer Science & Engineering');
  setVal('edu-college', studentProfile.college || '');
  setVal('edu-grad-year', studentProfile.graduationYear || 2025);
}

async function saveCurrentStepData() {
  if (!studentProfile) return true;

  try {
    if (currentStep === 2) {
      const consentToggle = document.getElementById('consent-toggle');
      const visibilityToggle = document.getElementById('visibility-toggle');
      await API.put('/student/profile', {
        consentGiven: consentToggle ? consentToggle.checked : true,
        profileVisibility: visibilityToggle ? visibilityToggle.checked : true
      });
    } else if (currentStep === 3) {
      await API.put('/student/profile', {
        dateOfBirth: document.getElementById('pers-dob')?.value || '',
        gender: document.getElementById('pers-gender')?.value || 'PREFER_NOT_TO_SAY',
        category: document.getElementById('pers-category')?.value || 'GENERAL',
        bio: document.getElementById('pers-bio')?.value || ''
      });
    } else if (currentStep === 5) {
      await API.put('/student/profile', {
        educationLevel: document.getElementById('edu-level')?.value || '',
        branch: document.getElementById('edu-branch')?.value || '',
        college: document.getElementById('edu-college')?.value || '',
        graduationYear: parseInt(document.getElementById('edu-grad-year')?.value || '2025')
      });
    } else if (currentStep === 8) {
      const vis = document.getElementById('visibility-toggle-step8');
      if (vis) {
        await API.put('/student/profile', { profileVisibility: vis.checked });
      }
    }

    // Refresh profile state
    const refreshed = await API.get('/student/profile');
    if (refreshed && refreshed.profile) {
      studentProfile = refreshed.profile;
    }
    return true;
  } catch (err) {
    alert('Error saving step: ' + err.message);
    return false;
  }
}

// --------------------------------------------------------------------------
// Step 4: Education CRUD
// --------------------------------------------------------------------------
async function loadEducationTable() {
  const container = document.getElementById('education-list-container');
  if (!container) return;

  try {
    const res = await API.get('/student/education');
    const list = res.education || [];

    if (list.length === 0) {
      container.innerHTML = `
        <div class="alert alert-info">No education records added yet. Add your 10th, 12th, Diploma, or Degree below.</div>
      `;
      return;
    }

    let html = `
      <table class="gov-table">
        <thead>
          <tr>
            <th>Qualification</th>
            <th>Institution</th>
            <th>Branch / Stream</th>
            <th>Percentage / CGPA</th>
            <th>Passing Year</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
    `;

    list.forEach(item => {
      html += `
        <tr>
          <td><strong>${escapeHtml(item.qualification)}</strong></td>
          <td>${escapeHtml(item.institution)}</td>
          <td>${escapeHtml(item.branch || '—')}</td>
          <td><span class="badge badge-blue">${item.percentage}%</span></td>
          <td>${item.passingYear}</td>
          <td>
            <button class="btn btn-secondary btn-sm" onclick="deleteEducationRecord(${item.id})">Delete</button>
          </td>
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;
  } catch (err) {
    container.innerHTML = `<div class="alert alert-danger">Error loading education records.</div>`;
  }
}

async function handleAddEducation(e) {
  e.preventDefault();
  const qual = document.getElementById('edu-new-qual').value;
  const inst = document.getElementById('edu-new-inst').value.trim();
  const branch = document.getElementById('edu-new-branch').value.trim();
  const perc = parseFloat(document.getElementById('edu-new-perc').value);
  const year = parseInt(document.getElementById('edu-new-year').value);

  if (!inst || isNaN(perc) || isNaN(year)) {
    alert('Please enter all education details.');
    return;
  }

  await API.post('/student/education', {
    qualification: qual,
    institution: inst,
    branch: branch,
    percentage: perc,
    passingYear: year
  });

  document.getElementById('edu-new-inst').value = '';
  document.getElementById('edu-new-branch').value = '';
  document.getElementById('edu-new-perc').value = '';
  loadEducationTable();
}

async function deleteEducationRecord(id) {
  if (!confirm('Are you sure you want to remove this academic record?')) return;
  await API.delete(`/student/education/${id}`);
  loadEducationTable();
}

// --------------------------------------------------------------------------
// Step 6: Skills CRUD
// --------------------------------------------------------------------------
async function loadSkillsTable() {
  const container = document.getElementById('skills-list-container');
  if (!container) return;

  try {
    const res = await API.get('/student/skills');
    const list = res.skills || [];

    // Also populate certificate skill dropdown in step 7
    populateSkillDropdownForCerts(list);

    if (list.length === 0) {
      container.innerHTML = `
        <div class="alert alert-info">No skills added yet. Add skills like Python, Java, Web Development, C++, etc.</div>
      `;
      return;
    }

    let html = `
      <div style="display:grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 14px;">
    `;

    list.forEach(s => {
      const isVerified = s.verified;
      html += `
        <div class="card" style="padding: 14px; display:flex; justify-content:space-between; align-items:center;">
          <div>
            <div style="font-weight:700; color:var(--gov-navy); font-size:15px;">${escapeHtml(s.name)}</div>
            <div style="font-size:12px; color:var(--text-muted); margin: 3px 0;">Level: ${s.level}</div>
            <div>
              ${isVerified 
                ? '<span class="badge badge-verified">✓ Verified Skill</span>' 
                : '<span class="badge badge-required">⚠️ Certificate Required</span>'}
            </div>
          </div>
          <button class="btn btn-secondary btn-sm" onclick="deleteSkillRecord(${s.id})" title="Remove Skill">✕</button>
        </div>
      `;
    });

    html += '</div>';
    container.innerHTML = html;
  } catch (err) {
    container.innerHTML = `<div class="alert alert-danger">Error loading skills.</div>`;
  }
}

async function handleAddSkill(e) {
  e.preventDefault();
  const name = document.getElementById('skill-new-name').value.trim();
  const level = document.getElementById('skill-new-level').value;

  if (!name) {
    alert('Please enter a skill name.');
    return;
  }

  await API.post('/student/skills', { name, level });
  document.getElementById('skill-new-name').value = '';
  loadSkillsTable();
}

async function deleteSkillRecord(id) {
  if (!confirm('Remove this skill?')) return;
  await API.delete(`/student/skills/${id}`);
  loadSkillsTable();
}

function populateSkillDropdownForCerts(skills) {
  const select = document.getElementById('cert-skill-select');
  if (!select) return;
  select.innerHTML = '<option value="">-- Associate with a Skill (To get Verified) --</option>';
  skills.forEach(s => {
    select.innerHTML += `<option value="${s.id}">${escapeHtml(s.name)} (${s.level})</option>`;
  });
}

// --------------------------------------------------------------------------
// Step 7: Certificates Management & Upload
// --------------------------------------------------------------------------
async function loadCertificatesTable() {
  const container = document.getElementById('certificates-list-container');
  if (!container) return;

  try {
    const res = await API.get('/student/certificates');
    const list = res.certificates || [];

    if (list.length === 0) {
      container.innerHTML = `
        <div class="alert alert-info">No certificates uploaded. Upload your certificates (PDF, JPG, PNG) to verify your skills.</div>
      `;
      return;
    }

    let html = `
      <table class="gov-table">
        <thead>
          <tr>
            <th>Certificate Name</th>
            <th>Associated Skill</th>
            <th>Issuing Authority</th>
            <th>Issue Date</th>
            <th>Document</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
    `;

    list.forEach(c => {
      html += `
        <tr>
          <td><strong>${escapeHtml(c.name)}</strong></td>
          <td><span class="badge badge-verified">${escapeHtml(c.skillName || 'General')}</span></td>
          <td>${escapeHtml(c.issuingOrganization)}</td>
          <td>${c.issueDate}</td>
          <td>
            <a href="${c.certificateUrl}" target="_blank" class="btn btn-secondary btn-sm">📄 View</a>
          </td>
          <td>
            <button class="btn btn-secondary btn-sm" onclick="deleteCertificateRecord(${c.id})">Delete</button>
          </td>
        </tr>
      `;
    });

    html += '</tbody></table>';
    container.innerHTML = html;
  } catch (err) {
    container.innerHTML = `<div class="alert alert-danger">Error loading certificates.</div>`;
  }
}

async function handleUploadCertificate(e) {
  e.preventDefault();
  const name = document.getElementById('cert-name').value.trim();
  const org = document.getElementById('cert-org').value.trim();
  const date = document.getElementById('cert-date').value;
  const skillId = document.getElementById('cert-skill-select').value;
  const fileInput = document.getElementById('cert-file');

  if (!name || !org || !date || !fileInput.files[0]) {
    alert('Please fill all certificate fields and attach a file (PDF/JPG/PNG).');
    return;
  }

  const file = fileInput.files[0];
  const reader = new FileReader();

  reader.onload = async () => {
    const base64Data = reader.result;

    const res = await API.post('/student/certificates', {
      name,
      issuingOrganization: org,
      issueDate: date,
      skillId: skillId ? parseInt(skillId) : 0,
      fileName: file.name,
      fileData: base64Data
    });

    if (res && res.success) {
      alert('Certificate uploaded! Associated skill has been VERIFIED.');
      document.getElementById('cert-name').value = '';
      document.getElementById('cert-org').value = '';
      fileInput.value = '';
      loadCertificatesTable();
      loadSkillsTable(); // Refresh verified state!
    }
  };

  reader.readAsDataURL(file);
}

async function deleteCertificateRecord(id) {
  if (!confirm('Are you sure you want to delete this certificate?')) return;
  await API.delete(`/student/certificates/${id}`);
  loadCertificatesTable();
  loadSkillsTable();
}

// --------------------------------------------------------------------------
// Step 10: Profile Completion Summary
// --------------------------------------------------------------------------
function renderSummary() {
  const container = document.getElementById('profile-summary-container');
  if (!container || !studentProfile) return;

  container.innerHTML = `
    <div class="card" style="padding: 24px; border-left: 5px solid var(--gov-green);">
      <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
        <h4 style="color:var(--gov-navy); font-size:20px;">Profile Readiness Score</h4>
        <div style="font-size:24px; font-weight:800; color:var(--gov-green);">${studentProfile.profileCompletion}% Complete</div>
      </div>
      <p style="color:var(--text-muted); margin-bottom:16px;">
        Your student profile is configured for national opportunity matching and verified employer discovery.
      </p>
      <div style="display:grid; grid-template-columns: 1fr 1fr; gap:14px; font-size:14px;">
        <div><strong>Branch:</strong> ${escapeHtml(studentProfile.branch || 'Not specified')}</div>
        <div><strong>Education:</strong> ${escapeHtml(studentProfile.educationLevel || 'Undergraduate')}</div>
        <div><strong>College:</strong> ${escapeHtml(studentProfile.college || '—')}</div>
        <div><strong>Category:</strong> ${studentProfile.category}</div>
        <div><strong>Employer Visibility:</strong> ${studentProfile.profileVisibility ? '<span class="badge badge-verified">Public to Employers</span>' : '<span class="badge badge-required">Private</span>'}</div>
        <div><strong>Consent Status:</strong> ${studentProfile.consentGiven ? 'Granted' : 'Revoked'}</div>
      </div>
    </div>
  `;
}

function setVal(id, val) {
  const el = document.getElementById(id);
  if (el) el.value = val;
}
