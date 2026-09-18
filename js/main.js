/**
 * GovSkill Connect - Main UI & Global Portal Script
 * Multi-language simulation, Accessibility Tools, Global Search, and Mobile Nav
 */

document.addEventListener('DOMContentLoaded', () => {
  initAccessibility();
  initGlobalSearch();
  initLanguageSelector();
  highlightActiveNav();
});

function initAccessibility() {
  let currentFontSize = 15;

  const btnIncrease = document.getElementById('btn-font-increase');
  const btnDecrease = document.getElementById('btn-font-decrease');
  const btnReset = document.getElementById('btn-font-reset');

  if (btnIncrease) {
    btnIncrease.addEventListener('click', () => {
      if (currentFontSize < 19) {
        currentFontSize += 1;
        document.body.style.fontSize = `${currentFontSize}px`;
      }
    });
  }

  if (btnDecrease) {
    btnDecrease.addEventListener('click', () => {
      if (currentFontSize > 13) {
        currentFontSize -= 1;
        document.body.style.fontSize = `${currentFontSize}px`;
      }
    });
  }

  if (btnReset) {
    btnReset.addEventListener('click', () => {
      currentFontSize = 15;
      document.body.style.fontSize = '15px';
    });
  }
}

function initGlobalSearch() {
  const searchForm = document.getElementById('hero-search-form');
  if (searchForm) {
    searchForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const input = document.getElementById('hero-search-input');
      const query = input ? input.value.trim() : '';
      window.location.href = `opportunities.html?q=${encodeURIComponent(query)}`;
    });
  }
}

function initLanguageSelector() {
  const langSelect = document.getElementById('language-selector');
  if (!langSelect) return;

  // Translation dictionary
  const translations = {
    hi: {
      heroTitle: 'अपने कौशल बनाएं। अवसर खोजें। अपना भविष्य गढ़ें।',
      heroSubtitle: 'अपनी शिक्षा और प्रमाणित कौशल के आधार पर सत्यापित सरकारी नौकरियाँ, इंटर्नशिप, अप्रेंटिसशिप, छात्रवृत्ति और कौशल विकास कार्यक्रम खोजें।',
      searchPlaceholder: 'नौकरियाँ, इंटर्नशिप, छात्रवृत्ति खोजें...',
      searchBtn: 'अवसर खोजें',
      buildProfile: '🚀 प्रोफ़ाइल बनाएं और सत्यापित हों',
      navHome: 'होम',
      navOpportunities: 'अवसर',
      navEligibility: 'सरकारी पात्रता',
      navSchemes: 'सरकारी योजनाएं',
      navCandidates: 'उम्मीदवार रजिस्ट्री',
      navResources: 'संसाधन',
      navAbout: 'हमारे बारे में',
      navContact: 'संपर्क',
      loginBtn: 'लॉगिन',
      registerBtn: 'पंजीकरण',
      stat1Label: 'सक्रिय अवसर',
      stat2Label: 'प्रमाणित कौशल कार्यक्रम',
      stat3Label: 'सत्यापित नियोक्ता और PSU',
      stat4Label: 'केंद्रीय और राज्य योजनाएं',
      heroBadge: 'एकीकृत डिजिटल इंडिया पहल',
      checkEligibility: 'अभी पात्रता जांचें →',
      viewAll: 'सभी अवसर देखें (25+) →',
    },
    mr: {
      heroTitle: 'आपले कौशल्य विकसित करा. संधी शोधा. आपले भविष्य घडवा.',
      heroSubtitle: 'आपल्या शिक्षण आणि प्रमाणित कौशल्यांनुसार सत्यापित सरकारी नोकऱ्या, इंटर्नशिप, शिष्यवृत्ती आणि कौशल्य विकास कार्यक्रम शोधा.',
      searchPlaceholder: 'नोकऱ्या, इंटर्नशिप, शिष्यवृत्ती शोधा...',
      searchBtn: 'संधी शोधा',
      buildProfile: '🚀 प्रोफाइल तयार करा आणि सत्यापित व्हा',
      navHome: 'मुखपृष्ठ',
      navOpportunities: 'संधी',
      navEligibility: 'सरकारी पात्रता',
      navSchemes: 'सरकारी योजना',
      navCandidates: 'उमेदवार नोंदणी',
      navResources: 'संसाधने',
      navAbout: 'आमच्याबद्दल',
      navContact: 'संपर्क',
      loginBtn: 'लॉगिन',
      registerBtn: 'नोंदणी',
      stat1Label: 'सक्रिय संधी',
      stat2Label: 'प्रमाणित कौशल्य कार्यक्रम',
      stat3Label: 'सत्यापित नियोक्ते आणि PSU',
      stat4Label: 'केंद्रीय आणि राज्य योजना',
      heroBadge: 'एकात्मिक डिजिटल इंडिया उपक्रम',
      checkEligibility: 'आता पात्रता तपासा →',
      viewAll: 'सर्व संधी पाहा (25+) →',
    },
    en: {
      heroTitle: 'Build Your Skills. Find Opportunities. Shape Your Future.',
      heroSubtitle: 'Discover verified government jobs, internships, apprenticeships, scholarships, and skill development programs tailored to your education and certified skills.',
      searchPlaceholder: 'Search jobs, internships, scholarships, skill programs, or skills (e.g. Java, Python)...',
      searchBtn: 'Search Opportunities',
      buildProfile: '🚀 Build Your Profile & Get Verified',
      navHome: 'Home',
      navOpportunities: 'Opportunities',
      navEligibility: 'Government Eligibility',
      navSchemes: 'Government Schemes',
      navCandidates: 'Candidate Registry',
      navResources: 'Resources',
      navAbout: 'About',
      navContact: 'Contact',
      loginBtn: 'Login',
      registerBtn: 'Register',
      stat1Label: 'Active Opportunities',
      stat2Label: 'Certified Skill Programs',
      stat3Label: 'Verified Employers & PSUs',
      stat4Label: 'Central & State Schemes',
      heroBadge: 'Unified Digital India Initiative',
      checkEligibility: 'Check Eligibility Now →',
      viewAll: 'View All Opportunities (25+) →',
    }
  };

  function applyTranslation(lang) {
    const t = translations[lang] || translations['en'];

    // Hero section
    const heroTitle = document.querySelector('.hero-title');
    if (heroTitle) heroTitle.textContent = t.heroTitle;

    const heroSubtitle = document.querySelector('.hero-subtitle');
    if (heroSubtitle) heroSubtitle.textContent = t.heroSubtitle;

    const heroBadge = document.querySelector('.hero-badge-tag');
    if (heroBadge) heroBadge.textContent = t.heroBadge;

    const searchInput = document.getElementById('hero-search-input');
    if (searchInput) searchInput.placeholder = t.searchPlaceholder;

    const searchBtn = document.querySelector('.hero-search-btn');
    if (searchBtn) searchBtn.textContent = t.searchBtn;

    const buildProfileBtn = document.querySelector('.btn-primary[href="register.html"]');
    if (buildProfileBtn && buildProfileBtn.textContent.trim().includes('Build')) {
      buildProfileBtn.textContent = t.buildProfile;
    }

    // Nav links
    const navLinks = document.querySelectorAll('.nav-list a');
    const navKeys = ['navHome','navOpportunities','navEligibility','navSchemes','navCandidates','navResources','navAbout','navContact'];
    navLinks.forEach((link, i) => {
      if (navKeys[i] && t[navKeys[i]]) link.textContent = t[navKeys[i]];
    });

    // Login / Register buttons
    const loginBtn = document.querySelector('a[href="login.html"].btn');
    if (loginBtn) loginBtn.textContent = t.loginBtn;

    const regBtn = document.querySelector('a[href="register.html"].btn-saffron');
    if (regBtn && regBtn.textContent.trim().toLowerCase().includes('reg')) {
      regBtn.textContent = t.registerBtn;
    }

    // Stats labels
    const statLabels = document.querySelectorAll('.stat-label');
    const statKeys = ['stat1Label','stat2Label','stat3Label','stat4Label'];
    statLabels.forEach((el, i) => {
      if (statKeys[i] && t[statKeys[i]]) el.textContent = t[statKeys[i]];
    });

    // Check eligibility button
    const eligBtn = document.querySelector('a[href="eligibility.html"].btn-saffron');
    if (eligBtn) eligBtn.textContent = t.checkEligibility;

    // View all button
    const viewAllBtn = document.querySelector('a[href="opportunities.html"].btn-outline-primary');
    if (viewAllBtn) viewAllBtn.textContent = t.viewAll;

    // Persist selection
    try { localStorage.setItem('govskill_lang', lang); } catch(e) {}
  }

  langSelect.addEventListener('change', (e) => {
    applyTranslation(e.target.value);
  });

  // Restore saved language on page load
  try {
    const savedLang = localStorage.getItem('govskill_lang');
    if (savedLang && savedLang !== 'en') {
      langSelect.value = savedLang;
      applyTranslation(savedLang);
    }
  } catch(e) {}
}

function highlightActiveNav() {
  const currentPath = window.location.pathname.split('/').pop() || 'index.html';
  const navLinks = document.querySelectorAll('.nav-list a');
  navLinks.forEach(link => {
    const href = link.getAttribute('href');
    if (href === currentPath || (currentPath === '' && href === 'index.html')) {
      link.parentElement.classList.add('active');
    }
  });
}
