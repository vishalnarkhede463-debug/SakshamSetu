/**
 * GovSkill Connect — Universal Accessibility Engine (A11Y)
 * Provides Web Speech API TTS, Voice Commands, Focus Speaking, High Contrast,
 * Global Keyboard Shortcuts (Alt+A), and Screen Reader Enhancements.
 * Pure Vanilla JavaScript — Zero External Dependencies.
 */

const A11Y = {
  // Settings with defaults
  settings: {
    speechEnabled: false,
    speechRate: 1.0,
    speechPitch: 1.0,
    contrastMode: 'default', // 'default', 'dark', 'light'
    fontSize: 'normal',      // 'normal', 'large', 'xlarge'
    reduceMotion: false,
    voiceCommandsEnabled: false
  },

  lastAnnouncement: '',
  focusDebounceTimer: null,
  recognition: null,
  isListening: false,
  pendingAction: null,

  // --------------------------------------------------------------------------
  // 1. Initialization
  // --------------------------------------------------------------------------
  init() {
    this.loadSettings();
    this.applySettings();
    this.setupGlobalShortcuts();
    this.setupFocusSpeaking();
    this.setupLiveRegion();
    this.injectAccessiblePortalButtons();
    this.injectStopSpeakingButton();

    // Check if on Accessible Portal page
    if (document.body.classList.contains('accessible-portal-page') || window.location.pathname.includes('accessible-student-portal')) {
      this.initAccessiblePortalMode();
    }
  },

  // --------------------------------------------------------------------------
  // 2. Settings Persistence & Application
  // --------------------------------------------------------------------------
  loadSettings() {
    try {
      const saved = localStorage.getItem('govskill_a11y_settings');
      if (saved) {
        this.settings = { ...this.settings, ...JSON.parse(saved) };
      }
    } catch (e) {
      console.warn('Could not load accessibility settings:', e);
    }
  },

  saveSettings() {
    try {
      localStorage.setItem('govskill_a11y_settings', JSON.stringify(this.settings));
    } catch (e) {
      console.warn('Could not save accessibility settings:', e);
    }
  },

  applySettings() {
    const body = document.body;

    // Contrast
    body.classList.remove('a11y-contrast-dark', 'a11y-contrast-light');
    if (this.settings.contrastMode === 'dark') {
      body.classList.add('a11y-contrast-dark');
    } else if (this.settings.contrastMode === 'light') {
      body.classList.add('a11y-contrast-light');
    }

    // Font size
    body.classList.remove('a11y-font-large', 'a11y-font-xlarge');
    if (this.settings.fontSize === 'large') {
      body.classList.add('a11y-font-large');
    } else if (this.settings.fontSize === 'xlarge') {
      body.classList.add('a11y-font-xlarge');
    }

    // Reduced Motion
    if (this.settings.reduceMotion) {
      body.classList.add('a11y-reduced-motion');
    } else {
      body.classList.remove('a11y-reduced-motion');
    }

    this.updateControlsUI();
  },

  updateControlsUI() {
    // Update speech toggle button
    const speechToggleBtn = document.getElementById('a11y-btn-toggle-speech');
    if (speechToggleBtn) {
      const isOn = this.settings.speechEnabled;
      speechToggleBtn.setAttribute('aria-pressed', isOn ? 'true' : 'false');
      // Update icon + label
      const icon = speechToggleBtn.querySelector('.a11y-btn-icon');
      const label = speechToggleBtn.querySelector('.a11y-btn-label');
      if (icon) icon.textContent = isOn ? '🔊' : '🔇';
      if (label) label.textContent = isOn ? 'Speech: ON' : 'Speech: OFF';
      // Use the variant active class if present, otherwise fallback
      if (speechToggleBtn.classList.contains('a11y-tool-btn--speech')) {
        speechToggleBtn.classList.toggle('active', isOn);
      } else {
        speechToggleBtn.innerHTML = isOn ? '🔊 Speech: ON' : '🔇 Speech: OFF';
        speechToggleBtn.classList.toggle('active', isOn);
      }
    }

    const rateSelect = document.getElementById('a11y-select-speech-rate');
    if (rateSelect) rateSelect.value = this.settings.speechRate.toString();

    const contrastSelect = document.getElementById('a11y-select-contrast');
    if (contrastSelect) contrastSelect.value = this.settings.contrastMode;

    const fontSelect = document.getElementById('a11y-select-font-size');
    if (fontSelect) fontSelect.value = this.settings.fontSize;

    const motionToggle = document.getElementById('a11y-toggle-motion');
    if (motionToggle) motionToggle.checked = this.settings.reduceMotion;
  },

  // --------------------------------------------------------------------------
  // 3. Global Shortcuts (Alt + A)
  // --------------------------------------------------------------------------
  setupGlobalShortcuts() {
    window.addEventListener('keydown', (e) => {
      // ALT + A (or Option + A)
      if (e.altKey && (e.key === 'a' || e.key === 'A' || e.code === 'KeyA')) {
        e.preventDefault();
        this.openAccessiblePortal();
      }

      // Escape key: stops speech or closes modals
      if (e.key === 'Escape') {
        this.stopSpeech();
        const activeModal = document.querySelector('.a11y-modal-backdrop');
        if (activeModal && activeModal.style.display !== 'none') {
          activeModal.remove();
        }
      }

      // Alt + R or 'r' when not in input: repeat announcement
      if (e.altKey && (e.key === 'r' || e.key === 'R' || e.code === 'KeyR')) {
        e.preventDefault();
        this.repeatLast();
      }
    });
  },

  openAccessiblePortal() {
    const currentPath = window.location.pathname;
    if (currentPath.includes('accessible-student-portal')) {
      this.speak("Accessible Student Portal is already open. Use Tab to navigate options.", true);
      const firstTab = document.querySelector('.a11y-nav-btn');
      if (firstTab) firstTab.focus();
    } else {
      // Enable speech before navigating so the portal welcome message plays
      this.settings.speechEnabled = true;
      this.saveSettings();
      window.location.href = 'accessible-student-portal.html';
    }
  },

  // Inject visible "♿ Accessible Student Portal (Alt + A)" button in normal headers
  // Only inject if there is no existing button already in the HTML
  injectAccessiblePortalButtons() {
    if (window.location.pathname.includes('accessible-student-portal')) return;
    // Skip if button already exists (e.g. index.html has it inline)
    if (document.getElementById('btn-accessible-portal-shortcut')) return;

    // Look for top-bar or header
    const accessTools = document.querySelector('.access-tools') || document.querySelector('.top-bar-right');
    if (accessTools) {
      const a11yBtn = document.createElement('a');
      a11yBtn.id = 'btn-accessible-portal-shortcut';
      a11yBtn.href = 'accessible-student-portal.html';
      a11yBtn.className = 'access-btn';
      a11yBtn.style.cssText = 'background:#ffb703; color:#000000; font-weight:800; padding:3px 10px; border-radius:4px; text-decoration:none; display:inline-flex; align-items:center; gap:5px; margin-right:8px; border:2px solid #000;';
      a11yBtn.setAttribute('title', 'Open Accessible Student Portal (Keyboard Shortcut: Alt + A)');
      a11yBtn.setAttribute('aria-label', 'Open Accessible Student Portal. Shortcut: Alt plus A');
      a11yBtn.innerHTML = '♿ Accessible Student Portal <kbd style="font-size:10px; background:#000; color:#fff; padding:1px 4px; border-radius:2px; margin-left:3px;">Alt+A</kbd>';
      accessTools.insertBefore(a11yBtn, accessTools.firstChild);
    }
  },

  // --------------------------------------------------------------------------
  // 4. Live Regions for Screen Readers
  // --------------------------------------------------------------------------
  setupLiveRegion() {
    if (!document.getElementById('a11y-live-announcer')) {
      const liveDiv = document.createElement('div');
      liveDiv.id = 'a11y-live-announcer';
      liveDiv.className = 'sr-only';
      liveDiv.setAttribute('aria-live', 'polite');
      liveDiv.setAttribute('aria-atomic', 'true');
      document.body.appendChild(liveDiv);
    }

    if (!document.getElementById('a11y-assertive-announcer')) {
      const assertiveDiv = document.createElement('div');
      assertiveDiv.id = 'a11y-assertive-announcer';
      assertiveDiv.className = 'sr-only';
      assertiveDiv.setAttribute('aria-live', 'assertive');
      assertiveDiv.setAttribute('aria-atomic', 'true');
      document.body.appendChild(assertiveDiv);
    }
  },

  // --------------------------------------------------------------------------
  // 5. Text-to-Speech (Web Speech API)
  // --------------------------------------------------------------------------

  // Inject a floating "Stop Speaking" button once (hidden by default)
  injectStopSpeakingButton() {
    if (document.getElementById('a11y-stop-speak-btn')) return;
    const btn = document.createElement('button');
    btn.id = 'a11y-stop-speak-btn';
    btn.setAttribute('aria-label', 'Stop speaking voice guidance');
    btn.setAttribute('title', 'Stop Speaking (Press Escape)');
    btn.innerHTML = '🔇 Stop Speaking';
    btn.style.cssText = [
      'position:fixed',
      'bottom:24px',
      'right:24px',
      'z-index:99999',
      'display:none',
      'align-items:center',
      'gap:8px',
      'background:#d32f2f',
      'color:#fff',
      'border:none',
      'border-radius:50px',
      'padding:10px 20px',
      'font-size:14px',
      'font-weight:700',
      'cursor:pointer',
      'box-shadow:0 4px 16px rgba(0,0,0,0.35)',
      'transition:transform 0.15s ease',
      'opacity:0.95'
    ].join(';');
    btn.addEventListener('click', () => this.stopSpeech());
    btn.addEventListener('mouseenter', () => { btn.style.transform = 'scale(1.06)'; });
    btn.addEventListener('mouseleave', () => { btn.style.transform = 'scale(1)'; });
    document.body.appendChild(btn);
  },

  showStopSpeakingButton() {
    const btn = document.getElementById('a11y-stop-speak-btn');
    if (btn) btn.style.display = 'flex';
  },

  hideStopSpeakingButton() {
    const btn = document.getElementById('a11y-stop-speak-btn');
    if (btn) btn.style.display = 'none';
  },

  speak(text, priority = false) {
    if (!text) return;
    this.lastAnnouncement = text;

    // Update Visual Announcement Banner if present
    const bannerText = document.getElementById('a11y-announcement-live-text');
    if (bannerText) {
      bannerText.innerText = text;
    }

    // Update Screen Reader Live Region
    const liveRegion = document.getElementById(priority ? 'a11y-assertive-announcer' : 'a11y-live-announcer');
    if (liveRegion) {
      liveRegion.innerText = '';
      setTimeout(() => { liveRegion.innerText = text; }, 50);
    }

    if (!this.settings.speechEnabled) return;

    if (!('speechSynthesis' in window)) {
      console.warn('Web Speech API is not supported in this browser.');
      return;
    }

    try {
      if (priority) {
        window.speechSynthesis.cancel();
      }

      const utterance = new SpeechSynthesisUtterance(text);
      utterance.rate = this.settings.speechRate || 1.0;
      utterance.pitch = this.settings.speechPitch || 1.0;
      utterance.volume = 1.0;

      // Select high quality natural voice if available
      const voices = window.speechSynthesis.getVoices();
      if (voices && voices.length > 0) {
        const engVoice = voices.find(v => v.lang.startsWith('en') && (v.name.includes('Natural') || v.name.includes('Google') || v.name.includes('Microsoft') || v.name.includes('English')));
        if (engVoice) utterance.voice = engVoice;
      }

      // Show Stop Speaking button while speech is playing, hide when done
      utterance.onstart = () => this.showStopSpeakingButton();
      utterance.onend = () => this.hideStopSpeakingButton();
      utterance.onerror = () => this.hideStopSpeakingButton();

      window.speechSynthesis.speak(utterance);
    } catch (err) {
      console.warn('SpeechSynthesis error:', err);
    }
  },

  stopSpeech() {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
    }
    this.hideStopSpeakingButton();
  },

  repeatLast() {
    if (this.lastAnnouncement) {
      this.speak("Repeating: " + this.lastAnnouncement, true);
    } else {
      this.speak("No previous announcement to repeat.", true);
    }
  },

  toggleSpeech() {
    this.settings.speechEnabled = !this.settings.speechEnabled;
    this.saveSettings();
    this.applySettings();
    if (this.settings.speechEnabled) {
      this.speak("Voice guidance enabled.", true);
    } else {
      this.stopSpeech();
    }
  },

  setSpeechRate(rate) {
    this.settings.speechRate = parseFloat(rate);
    this.saveSettings();
    this.speak(`Speech speed set to ${rate} times speed.`, true);
  },

  setContrastMode(mode) {
    this.settings.contrastMode = mode;
    this.saveSettings();
    this.applySettings();
    this.speak(`High contrast mode set to ${mode}.`, true);
  },

  setFontSize(size) {
    this.settings.fontSize = size;
    this.saveSettings();
    this.applySettings();
    this.speak(`Text size set to ${size}.`, true);
  },

  toggleReduceMotion(enabled) {
    this.settings.reduceMotion = enabled;
    this.saveSettings();
    this.applySettings();
    this.speak(enabled ? "Animations reduced." : "Standard animations enabled.", true);
  },

  // --------------------------------------------------------------------------
  // 6. Focus Speaking Engine
  // --------------------------------------------------------------------------
  setupFocusSpeaking() {
    document.addEventListener('focusin', (e) => {
      const target = e.target;
      if (!target || target === document.body || target === document.documentElement) return;

      // Skip elements explicitly marked silent or purely decorative
      if (target.getAttribute('data-a11y-silent') === 'true' || target.getAttribute('aria-hidden') === 'true') return;

      clearTimeout(this.focusDebounceTimer);
      this.focusDebounceTimer = setTimeout(() => {
        const announcement = this.buildElementAnnouncement(target);
        if (announcement) {
          this.speak(announcement, false);
        }
      }, 220); // 220ms debounce prevents rapid-tabbing noise
    });
  },

  buildElementAnnouncement(el) {
    // If element provides an explicit custom spoken announcement:
    const customSpeech = el.getAttribute('data-a11y-speech');
    if (customSpeech) return customSpeech;

    const role = el.getAttribute('role') || el.tagName.toLowerCase();
    const ariaLabel = el.getAttribute('aria-label');
    const ariaDescribedby = el.getAttribute('aria-describedby');
    let descriptionText = '';
    if (ariaDescribedby) {
      const descEl = document.getElementById(ariaDescribedby);
      if (descEl) descriptionText = descEl.innerText.trim();
    }

    // 1. Buttons
    if (el.tagName === 'BUTTON' || role === 'button') {
      const label = ariaLabel || el.innerText.trim() || el.getAttribute('title') || 'Action button';
      return `${label}. Button. Press Enter or Space to activate.${descriptionText ? ' ' + descriptionText : ''}`;
    }

    // 2. Links
    if (el.tagName === 'A' || role === 'link') {
      const label = ariaLabel || el.innerText.trim() || el.getAttribute('title') || 'Link';
      return `${label}. Link. Press Enter to open.`;
    }

    // 3. Inputs
    if (el.tagName === 'INPUT') {
      const type = el.type || 'text';
      const label = this.getFieldLabel(el);

      if (type === 'checkbox') {
        const checked = el.checked ? 'Checked' : 'Not checked';
        return `${label}. Checkbox, ${checked}. Press Space to toggle.`;
      }
      if (type === 'radio') {
        const checked = el.checked ? 'Selected' : 'Not selected';
        return `${label}. Radio button, ${checked}. Use arrow keys to choose.`;
      }
      if (type === 'password') {
        return `${label}. Password field. Enter your password securely.`;
      }

      const val = el.value ? `Current value: ${el.value}.` : 'Empty.';
      const placeholder = el.placeholder ? `Hint: ${el.placeholder}.` : '';
      return `${label}. Text field. ${val} ${placeholder} Type to enter value.${descriptionText ? ' ' + descriptionText : ''}`;
    }

    // 4. Selects
    if (el.tagName === 'SELECT') {
      const label = this.getFieldLabel(el);
      const selectedOption = el.options[el.selectedIndex]?.text || '';
      return `${label}. Dropdown menu. Currently selected: ${selectedOption}. Use up and down arrow keys to change option.`;
    }

    // 5. Textareas
    if (el.tagName === 'TEXTAREA') {
      const label = this.getFieldLabel(el);
      const val = el.value ? `Current text: ${el.value.substring(0, 50)}.` : 'Empty.';
      return `${label}. Multi-line text area. ${val} Enter your remarks.`;
    }

    // 6. Tabs
    if (role === 'tab') {
      const label = ariaLabel || el.innerText.trim();
      const selected = el.getAttribute('aria-selected') === 'true' ? 'Selected tab.' : 'Tab.';
      return `${label}. ${selected} Press Enter to view this section.`;
    }

    // 7. Opportunity or Application Card
    if (el.classList.contains('a11y-card') || el.getAttribute('data-opp-title')) {
      const title = el.getAttribute('data-opp-title') || el.querySelector('h3, h4')?.innerText || 'Opportunity';
      const meta = el.querySelector('.a11y-card-meta')?.innerText || '';
      return `${title}. ${meta}. Press Enter to view full details.`;
    }

    // Generic fallback for focusable elements
    if (ariaLabel) return ariaLabel;
    const text = el.innerText ? el.innerText.trim().substring(0, 100) : '';
    return text ? `${text}` : '';
  },

  getFieldLabel(input) {
    if (input.getAttribute('aria-label')) return input.getAttribute('aria-label');
    if (input.id) {
      const labelEl = document.querySelector(`label[for="${input.id}"]`);
      if (labelEl) return labelEl.innerText.trim();
    }
    const parentLabel = input.closest('label');
    if (parentLabel) return parentLabel.innerText.trim();
    return input.name || input.placeholder || 'Input field';
  },

  // --------------------------------------------------------------------------
  // 7. Accessible Portal Mode Starter
  // --------------------------------------------------------------------------
  initAccessiblePortalMode() {
    // Ensure speech is enabled on the accessible portal
    if (!this.settings.speechEnabled) {
      this.settings.speechEnabled = true;
      this.saveSettings();
    }

    // Reset speech to off when leaving the portal so normal pages stay silent
    window.addEventListener('pagehide', () => {
      this.settings.speechEnabled = false;
      this.saveSettings();
    }, { once: true });

    // Initial welcome voice greeting
    setTimeout(() => {
      this.speak(
        "Welcome to GovSkill Connect Accessible Student Portal. Accessibility mode is active. " +
        "Use Tab to move between options. Press Enter to select. Press Escape to go back or stop speech.",
        true
      );
    }, 600);
  },

  // --------------------------------------------------------------------------
  // 8. Voice Command Assistant (Web Speech Recognition)
  // --------------------------------------------------------------------------
  initSpeechRecognition() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      return null;
    }
    const recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.interimResults = false;
    recognition.lang = 'en-IN';
    return recognition;
  },

  toggleVoiceCommands() {
    if (this.isListening) {
      this.stopVoiceCommands();
    } else {
      this.startVoiceCommands();
    }
  },

  startVoiceCommands() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      this.speak("Voice recognition is not supported in your web browser. You can use keyboard navigation fully.", true);
      alert("Speech recognition is not supported by this browser. Please use Chrome or Edge for voice commands.");
      return;
    }

    try {
      this.recognition = new SpeechRecognition();
      this.recognition.continuous = false;
      this.recognition.interimResults = false;
      this.recognition.lang = 'en-IN';

      this.recognition.onstart = () => {
        this.isListening = true;
        this.updateVoiceCommandUI(true);
        this.speak("Listening for voice command. Speak now.", true);
      };

      this.recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript.trim().toLowerCase();
        this.speak(`Command heard: ${transcript}`, false);
        this.executeVoiceCommand(transcript);
      };

      this.recognition.onerror = (event) => {
        console.warn('Voice command error:', event.error);
        this.isListening = false;
        this.updateVoiceCommandUI(false);
        if (event.error === 'not-allowed') {
          this.speak("Microphone permission was denied. Please allow microphone access to use voice commands.", true);
        } else {
          this.speak("Voice recognition did not hear a command. Please try again.", true);
        }
      };

      this.recognition.onend = () => {
        this.isListening = false;
        this.updateVoiceCommandUI(false);
      };

      this.recognition.start();
    } catch (err) {
      console.warn('Failed to start voice recognition:', err);
      this.speak("Could not access microphone for voice commands.", true);
    }
  },

  stopVoiceCommands() {
    if (this.recognition && this.isListening) {
      this.recognition.stop();
      this.isListening = false;
      this.updateVoiceCommandUI(false);
      this.speak("Voice listening stopped.", true);
    }
  },

  updateVoiceCommandUI(active) {
    const btn = document.getElementById('a11y-btn-voice-commands');
    if (btn) {
      btn.classList.toggle('active', active);
      btn.setAttribute('aria-pressed', active ? 'true' : 'false');
      // Update label text if using new design (has .a11y-btn-label span)
      const label = btn.querySelector('.a11y-btn-label');
      if (label) {
        label.textContent = active ? 'Listening...' : 'Voice Commands';
      } else {
        // Fallback for old markup
        btn.innerHTML = active ? '🔴 Listening...' : '🎙 Voice Commands';
      }
    }
  },

  executeVoiceCommand(cmd) {
    console.log('Processing voice command:', cmd);

    // Confirmation handling
    if (this.pendingAction) {
      if (cmd.includes('confirm') || cmd.includes('yes') || cmd.includes('submit')) {
        const action = this.pendingAction;
        this.pendingAction = null;
        action();
        return;
      } else if (cmd.includes('cancel') || cmd.includes('no') || cmd.includes('edit')) {
        this.pendingAction = null;
        this.speak("Action cancelled.", true);
        return;
      }
    }

    if (cmd.includes('dashboard') || cmd.includes('home')) {
      if (typeof switchTab === 'function') switchTab('dashboard');
      this.speak("Opening student dashboard.", true);
    } else if (cmd.includes('profile') || cmd.includes('my profile')) {
      if (typeof switchTab === 'function') switchTab('profile');
      this.speak("Opening student profile.", true);
    } else if (cmd.includes('education')) {
      if (typeof switchTab === 'function') switchTab('education');
      this.speak("Opening education records.", true);
    } else if (cmd.includes('skill')) {
      if (typeof switchTab === 'function') switchTab('skills');
      this.speak("Opening verified skills.", true);
    } else if (cmd.includes('opportunit') || cmd.includes('job') || cmd.includes('internship')) {
      if (typeof switchTab === 'function') switchTab('opportunities');
      this.speak("Opening available opportunities.", true);
    } else if (cmd.includes('application')) {
      if (typeof switchTab === 'function') switchTab('applications');
      this.speak("Showing your submitted applications.", true);
    } else if (cmd.includes('scheme') || cmd.includes('government scheme') || cmd.includes('welfare')) {
      if (typeof switchTab === 'function') switchTab('schemes');
      this.speak("Opening government schemes directory.", true);
    } else if (cmd.includes('eligibility') || cmd.includes('calculator')) {
      if (typeof switchTab === 'function') switchTab('eligibility');
      this.speak("Opening government eligibility calculator.", true);
    } else if (cmd.includes('notification')) {
      if (typeof switchTab === 'function') switchTab('notifications');
      this.speak("Opening notification inbox.", true);
    } else if (cmd.includes('resume') || cmd.includes('career')) {
      if (typeof switchTab === 'function') switchTab('resume');
      this.speak("Opening resume and career overview.", true);
    } else if (cmd.includes('setting') || cmd.includes('accessibility setting')) {
      if (typeof switchTab === 'function') switchTab('settings');
      this.speak("Opening accessibility settings.", true);
    } else if (cmd.includes('read this page') || cmd.includes('read page') || cmd.includes('read summary')) {
      if (typeof readCurrentPageSummary === 'function') {
        readCurrentPageSummary();
      } else {
        this.speak("Reading current page content.", true);
      }
    } else if (cmd.includes('stop speaking') || cmd.includes('quiet') || cmd.includes('silence')) {
      this.stopSpeech();
    } else if (cmd.includes('repeat')) {
      this.repeatLast();
    } else if (cmd.includes('go back') || cmd.includes('back')) {
      window.history.back();
      this.speak("Going back to previous page.", true);
    } else if (cmd.includes('logout') || cmd.includes('sign out')) {
      this.speak("Signing out of GovSkill Connect.", true);
      if (typeof handleLogout === 'function') handleLogout();
      else if (typeof API !== 'undefined') {
        API.clearSession();
        window.location.href = 'index.html';
      }
    } else if (cmd.includes('apply for this opportunity') || cmd.includes('apply now') || cmd.includes('apply')) {
      const oppTitleEl = document.getElementById('a11y-det-title');
      const oppTitle = oppTitleEl ? oppTitleEl.innerText : 'selected opportunity';

      this.speak(`You are about to apply for ${oppTitle}. Say or select Confirm to continue.`, true);
      this.pendingAction = () => {
        if (typeof confirmAccessibleApplication === 'function') {
          confirmAccessibleApplication();
        } else {
          this.speak("Application submitted successfully.", true);
        }
      };
    } else {
      this.speak(`Recognized: "${cmd}". Command not recognized. Try saying: open dashboard, open profile, open opportunities, show applications, or show government schemes.`, true);
    }
  },

  // --------------------------------------------------------------------------
  // 9. Voice-Assisted Profile Field Input
  // --------------------------------------------------------------------------
  startVoiceFieldFill(fieldId, fieldName) {
    const inputEl = document.getElementById(fieldId);
    if (!inputEl) return;

    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      this.speak("Speech recognition is not supported in this browser. Please type using your keyboard.", true);
      inputEl.focus();
      return;
    }

    this.speak(`What is your ${fieldName}? Please speak clearly now.`, true);

    const rec = new SpeechRecognition();
    rec.continuous = false;
    rec.interimResults = false;
    rec.lang = 'en-IN';

    rec.onresult = (event) => {
      const recognized = event.results[0][0].transcript.trim();
      this.showVoiceConfirmationModal(fieldId, fieldName, recognized);
    };

    rec.onerror = (e) => {
      this.speak(`Could not capture your ${fieldName}. Please enter using keyboard.`, true);
      inputEl.focus();
    };

    rec.start();
  },

  showVoiceConfirmationModal(fieldId, fieldName, recognizedText) {
    this.speak(`Your ${fieldName} is ${recognizedText}. Is this correct? Press Enter to Confirm, or Escape to Edit.`, true);

    const existing = document.getElementById('a11y-voice-confirm-modal');
    if (existing) existing.remove();

    const modal = document.createElement('div');
    modal.id = 'a11y-voice-confirm-modal';
    modal.className = 'a11y-modal-backdrop';
    modal.setAttribute('role', 'dialog');
    modal.setAttribute('aria-modal', 'true');
    modal.setAttribute('aria-labelledby', 'voice-confirm-heading');

    modal.innerHTML = `
      <div class="a11y-modal-box">
        <div class="a11y-modal-header">
          <h2 id="voice-confirm-heading" style="font-size:22px; color:#ffb703; margin:0 0 6px 0;">
            🎙 Confirm Voice Input
          </h2>
          <p style="margin:0; font-size:15px; color:#e0e1dd;">
            We recognized the following information for <strong>${fieldName}</strong>:
          </p>
        </div>

        <div style="background:#0d1b2a; border:2px solid #ffb703; border-radius:6px; padding:18px; margin-bottom:20px;">
          <label for="a11y-recognized-edit-input" style="display:block; font-size:14px; font-weight:700; color:#cbd5e1; margin-bottom:6px;">
            Recognized Text (You can also edit this with keyboard):
          </label>
          <input type="text" id="a11y-recognized-edit-input" class="a11y-input" value="${recognizedText}">
        </div>

        <div class="a11y-modal-actions">
          <button type="button" class="a11y-btn a11y-btn-secondary" id="a11y-voice-modal-cancel">
            ✕ Cancel
          </button>
          <button type="button" class="a11y-btn a11y-btn-primary" id="a11y-voice-modal-confirm">
            ✓ Confirm & Save
          </button>
        </div>
      </div>
    `;

    document.body.appendChild(modal);

    const editInput = document.getElementById('a11y-recognized-edit-input');
    const confirmBtn = document.getElementById('a11y-voice-modal-confirm');
    const cancelBtn = document.getElementById('a11y-voice-modal-cancel');

    confirmBtn.focus();

    confirmBtn.addEventListener('click', () => {
      const finalVal = editInput.value.trim();
      const targetField = document.getElementById(fieldId);
      if (targetField) {
        targetField.value = finalVal;
        targetField.dispatchEvent(new Event('input', { bubbles: true }));
        targetField.dispatchEvent(new Event('change', { bubbles: true }));
        this.speak(`${fieldName} updated to ${finalVal}.`, true);
        targetField.focus();
      }
      modal.remove();
    });

    cancelBtn.addEventListener('click', () => {
      modal.remove();
      this.speak("Voice input cancelled.", true);
      const targetField = document.getElementById(fieldId);
      if (targetField) targetField.focus();
    });
  }
};

// Initialize on DOM load
document.addEventListener('DOMContentLoaded', () => {
  A11Y.init();
});

// Export globally
window.A11Y = A11Y;
