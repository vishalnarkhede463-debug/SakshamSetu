/**
 * Saksham Setu - Advanced ATS Resume Generator Controller
 * Auto-generates structured resumes from verified candidate profile & education data.
 */

let resumeData = {
  profile: null,
  template: 'govt',
  summary: '',
  sections: {
    education: true,
    skills: true,
    certificates: true,
    personal: true
  }
};

document.addEventListener('DOMContentLoaded', async () => {
  const user = requireAuth('STUDENT');
  if (!user) return;

  try {
    const res = await API.get('/student/profile');
    resumeData.profile = res.profile;

    if (resumeData.profile) {
      resumeData.summary = resumeData.profile.bio || `Motivated ${resumeData.profile.branch || 'Engineering'} student with a solid academic record and hands-on competence in verified technologies. Seeking government, public sector, or industry apprenticeship and technical placement opportunities to contribute to national growth.`;
      
      const summaryInput = document.getElementById('resume-summary-input');
      if (summaryInput) summaryInput.value = resumeData.summary;
    }

    renderResumePreview();
  } catch (err) {
    console.error('Failed to load profile for resume:', err);
  }
});

function setResumeTemplate(tpl, btn) {
  resumeData.template = tpl;
  document.querySelectorAll('.tpl-btn').forEach(b => {
    b.className = 'btn btn-outline-primary btn-sm tpl-btn';
  });
  if (btn) btn.className = 'btn btn-primary btn-sm tpl-btn active';

  const sheet = document.getElementById('resume-sheet');
  if (sheet) {
    sheet.className = `resume-preview-sheet template-${tpl}`;
  }
}

function updateResumeSummary(val) {
  resumeData.summary = val;
  const summaryEl = document.getElementById('resume-render-summary');
  if (summaryEl) summaryEl.innerText = val;
}

function toggleResumeSection(sec) {
  resumeData.sections[sec] = !resumeData.sections[sec];
  renderResumePreview();
}

function renderResumePreview() {
  const p = resumeData.profile;
  const sheet = document.getElementById('resume-sheet');
  if (!sheet || !p) return;

  const eduList = p.educationList || [];
  const skillList = p.skillsList || [];
  const certList = p.certificatesList || [];

  let html = `
    <!-- Resume Header -->
    <div class="resume-header">
      <div class="resume-name">${escapeHtml(p.name || 'Candidate Name')}</div>
      ${resumeData.template === 'tech' ? `<div class="resume-headline">${escapeHtml(p.branch || 'Software Engineering')} • ${escapeHtml(p.educationLevel || 'Undergraduate')}</div>` : ''}
      <div class="resume-contact-bar">
        <span>📧 ${escapeHtml(p.email || '')}</span>
        <span>📞 ${escapeHtml(p.phone || '')}</span>
        <span>📍 ${escapeHtml(p.city || '')}, ${escapeHtml(p.state || 'India')}</span>
        ${p.category ? `<span>🇮🇳 Category: ${p.category}</span>` : ''}
      </div>
    </div>

    <!-- Professional Summary / Career Objective -->
    <div class="resume-section">
      <div class="resume-section-title">Career Objective / Professional Summary</div>
      <p style="font-size:13px; color:#334155; margin:0;" id="resume-render-summary">${escapeHtml(resumeData.summary)}</p>
    </div>
  `;

  // Education Section
  if (resumeData.sections.education && eduList.length > 0) {
    html += `
      <div class="resume-section">
        <div class="resume-section-title">Academic Qualifications</div>
        ${eduList.map(e => `
          <div class="resume-item">
            <div class="resume-item-row">
              <span class="resume-item-title">${escapeHtml(e.qualification)} - ${escapeHtml(e.branch || 'General')}</span>
              <span class="resume-item-date">${e.passingYear}</span>
            </div>
            <div class="resume-item-row">
              <span class="resume-item-sub">${escapeHtml(e.institution)}</span>
              <span style="font-weight:700; color:#0f172a; font-size:12px;">Score: ${e.percentage}%</span>
            </div>
          </div>
        `).join('')}
      </div>
    `;
  }

  // Skills Section
  if (resumeData.sections.skills && skillList.length > 0) {
    html += `
      <div class="resume-section">
        <div class="resume-section-title">Technical Competencies & Skills</div>
        <div class="resume-skills-grid">
          ${skillList.map(s => `
            <span class="resume-skill-badge ${s.verified ? 'resume-verified-badge' : ''}">
              ${s.verified ? '✓ ' : ''}${escapeHtml(s.name)} (${s.level})
            </span>
          `).join('')}
        </div>
      </div>
    `;
  }

  // Certificates Section
  if (resumeData.sections.certificates && certList.length > 0) {
    html += `
      <div class="resume-section">
        <div class="resume-section-title">Verified Certifications & Credentials</div>
        ${certList.map(c => `
          <div class="resume-item" style="margin-bottom:6px;">
            <div class="resume-item-row">
              <span class="resume-item-title">🏆 ${escapeHtml(c.name)}</span>
              <span class="resume-item-date">${c.issueDate || 'Verified'}</span>
            </div>
            <div class="resume-item-sub">
              Issuing Organization: <strong>${escapeHtml(c.issuingOrganization)}</strong>
            </div>
          </div>
        `).join('')}
      </div>
    `;
  }

  // Personal Information
  if (resumeData.sections.personal) {
    html += `
      <div class="resume-section">
        <div class="resume-section-title">Declaration & Candidate Particulars</div>
        <div style="font-size:12px; color:#475569; display:grid; grid-template-columns:1fr 1fr; gap:6px; margin-bottom:10px;">
          <div><strong>Date of Birth:</strong> ${p.dateOfBirth || 'As per matriculation'}</div>
          <div><strong>Gender:</strong> ${p.gender || 'Not specified'}</div>
          <div><strong>Reservation Category:</strong> ${p.category || 'GENERAL'}</div>
          <div><strong>Government Portal Verification:</strong> Verified (Saksham Setu TEE)</div>
        </div>
        <p style="font-size:11px; color:#64748b; font-style:italic; margin:0;">
          I hereby declare that all information furnished above is true, complete, and correct to the best of my knowledge and belief.
        </p>
      </div>
    `;
  }

  sheet.innerHTML = html;
}

function printResume() {
  window.print();
}

function downloadATSPlainText() {
  const p = resumeData.profile;
  if (!p) return;

  const edu = (p.educationList || []).map(e => `* ${e.qualification} (${e.branch}) - ${e.institution} - ${e.percentage}% [${e.passingYear}]`).join('\n');
  const skills = (p.skillsList || []).map(s => `${s.name} (${s.level})`).join(', ');
  const certs = (p.certificatesList || []).map(c => `* ${c.name} - ${c.issuingOrganization} [${c.issueDate}]`).join('\n');

  const text = `
================================================================================
${(p.name || 'CANDIDATE').toUpperCase()}
Email: ${p.email} | Phone: ${p.phone} | Location: ${p.city}, ${p.state}
Category: ${p.category} | Branch: ${p.branch}
================================================================================

CAREER OBJECTIVE:
${resumeData.summary}

ACADEMIC QUALIFICATIONS:
${edu}

TECHNICAL SKILLS:
${skills}

CERTIFICATIONS & CREDENTIALS:
${certs}

DECLARATION:
All details furnished are authentic and verified via Saksham Setu National Gateway.
`.trim();

  const blob = new Blob([text], { type: 'text/plain' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `${(p.name || 'Resume').replace(/\s+/g, '_')}_GovSkill_ATS.txt`;
  a.click();
  URL.revokeObjectURL(url);
}
