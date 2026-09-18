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
  if (langSelect) {
    langSelect.addEventListener('change', (e) => {
      const lang = e.target.value;
      if (lang === 'hi') {
        alert('भाषा हिंदी में चुनी गई है (Language switched to Hindi).');
      } else if (lang === 'mr') {
        alert('भाषा मराठी मध्ये निवडली आहे (Language switched to Marathi).');
      } else {
        // English
      }
    });
  }
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
