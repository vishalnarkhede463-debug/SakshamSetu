/* ==========================================================================
   INTERVIEW PRACTICE MODULE
   - Step 1: user picks Course/Field + Specialization/Sub-field
   - Step 2: shows 15 common HR questions + 15 field-specific questions (=30)
   ========================================================================== */

const InterviewPractice = (function () {

  const COMMON_QUESTIONS = [
    "Tell me about yourself.",
    "Why do you want this job / internship / opportunity?",
    "What are your greatest strengths and weaknesses?",
    "Where do you see yourself in the next 5 years?",
    "Why should we select you over other candidates?",
    "Describe a challenge you faced and how you solved it.",
    "How do you handle work pressure and tight deadlines?",
    "What motivates you to do your best work?",
    "Describe a time you worked effectively in a team.",
    "How do you handle criticism or negative feedback?",
    "What are your salary or stipend expectations?",
    "Why did you choose this field of study?",
    "What do you know about our organization or this scheme?",
    "How do you keep yourself updated with industry trends?",
    "Do you have any questions for us?"
  ];

  const FIELDS = {
    cse: {
      label: "Computer Science / IT",
      subfields: {
        "software-dev": {
          label: "Software Development / Programming",
          questions: [
            "Explain the core concepts of Object-Oriented Programming with examples.",
            "What is the difference between an array and a linked list?",
            "What is time complexity? Explain Big-O notation.",
            "Explain the difference between a stack and a queue.",
            "What is the difference between a compiler and an interpreter?",
            "How do you handle exceptions/errors in your preferred language?",
            "What is version control? Have you used Git/GitHub?",
            "Explain the phases of the Software Development Life Cycle (SDLC).",
            "What is the difference between a process and a thread?",
            "How do you approach debugging a program that isn't working?",
            "Explain recursion with a simple example.",
            "What is the difference between SQL and NoSQL databases?",
            "Explain what a REST API is and how it works.",
            "What design patterns have you used or studied?",
            "Explain a sorting algorithm you know well."
          ]
        },
        "web-dev": {
          label: "Web Development",
          questions: [
            "What are the roles of HTML, CSS, and JavaScript in a webpage?",
            "Explain the CSS box model.",
            "What is responsive web design and how do you achieve it?",
            "What is the difference between GET and POST requests?",
            "What is the DOM and how do you manipulate it with JavaScript?",
            "Explain client-side rendering vs server-side rendering.",
            "How does a frontend application consume a REST API?",
            "What is the difference between == and === in JavaScript?",
            "What is CORS and why does it matter?",
            "Explain cookies vs local storage vs session storage.",
            "What is the difference between a framework and a library? Give examples.",
            "How do you optimize a website's loading speed?",
            "Explain the MVC architecture.",
            "Explain callbacks, promises, and async/await in JavaScript.",
            "How do you ensure your website works across different browsers/devices?"
          ]
        },
        "data-science": {
          label: "Data Science / AI-ML",
          questions: [
            "What is the difference between supervised and unsupervised learning?",
            "Explain overfitting and underfitting.",
            "What is a confusion matrix used for?",
            "Explain the difference between classification and regression.",
            "What is feature engineering?",
            "Explain the bias-variance tradeoff.",
            "Which libraries have you used for data analysis (e.g. Pandas, NumPy)?",
            "What is data cleaning and why is it important?",
            "Explain the difference between correlation and causation.",
            "Explain a neural network in simple terms.",
            "What evaluation metrics do you use for a classification model?",
            "What is cross-validation and why do we use it?",
            "What is the difference between AI, Machine Learning, and Deep Learning?",
            "How do you handle missing data in a dataset?",
            "What tools have you used for data visualization?"
          ]
        },
        "networking-security": {
          label: "Networking / Cyber Security",
          questions: [
            "What is the OSI model? Briefly explain its layers.",
            "What is the difference between TCP and UDP?",
            "What is an IP address, and what is subnetting?",
            "What is the difference between a firewall and antivirus software?",
            "What is DNS and how does it work?",
            "What is a VPN and why is it used?",
            "Explain the difference between symmetric and asymmetric encryption.",
            "What is phishing and how can it be prevented?",
            "What is a DDoS attack?",
            "Explain the difference between HTTP and HTTPS.",
            "What is two-factor authentication and why does it help?",
            "What is the difference between a router and a switch?",
            "What is malware, and what are its common types?",
            "Explain the CIA triad in cybersecurity (Confidentiality, Integrity, Availability).",
            "How would you secure a home or office wireless network?"
          ]
        }
      }
    },

    mechanical: {
      label: "Mechanical Engineering",
      subfields: {
        "mech-design": {
          label: "Mechanical Design / Production",
          questions: [
            "What is the difference between stress and strain?",
            "Explain the working of an internal combustion engine.",
            "What is the difference between ductile and brittle materials?",
            "Explain the concept of factor of safety in design.",
            "What is CAD/CAM, and which software have you used?",
            "What is the difference between welding and brazing?",
            "Why is heat treatment done on metal components?",
            "Explain the working principle of a lathe machine.",
            "What are tolerances and fits in manufacturing?",
            "Explain the difference between casting and forging.",
            "What are the common types of gears and their applications?",
            "Explain the working of a hydraulic press.",
            "What is preventive maintenance and why is it important?",
            "What is the difference between a 2-stroke and a 4-stroke engine?",
            "What safety precautions do you follow in a mechanical workshop?"
          ]
        },
        "automobile": {
          label: "Automobile Engineering",
          questions: [
            "Explain the working of a clutch system.",
            "What is the difference between manual and automatic transmission?",
            "Explain the function of a differential in a vehicle.",
            "What is the purpose of the radiator/cooling system?",
            "Explain the vehicle braking system and its types.",
            "What role does the suspension system play?",
            "What is the difference between petrol and diesel engines?",
            "What is an alternator, and what is its function?",
            "Explain the working of a fuel injection system.",
            "What are BS6/emission norms and why do they matter?",
            "What is the function of a vehicle battery?",
            "What is wheel alignment and wheel balancing?",
            "Explain the basic working of an EV (electric vehicle) powertrain.",
            "What routine maintenance checks do you perform on a vehicle?",
            "What is the purpose of the exhaust system?"
          ]
        }
      }
    },

    electrical: {
      label: "Electrical & Electronics",
      subfields: {
        "electrical": {
          label: "Electrical Engineering",
          questions: [
            "State and explain Ohm's law.",
            "What is the difference between AC and DC current?",
            "Explain the difference between series and parallel circuits.",
            "How does a transformer work?",
            "Explain the working of a three-phase induction motor.",
            "What is earthing/grounding, and why is it important?",
            "What is the difference between an MCB and a fuse?",
            "What is power factor, and why is it corrected?",
            "Explain the working of a generator.",
            "What safety precautions do you take while working with electricity?",
            "What causes a short circuit, and how is overload protection provided?",
            "Explain the difference between star and delta connections.",
            "What is the function of a relay?",
            "Explain the difference between single-phase and three-phase supply.",
            "What instruments do you use to test electrical circuits (e.g. multimeter, megger)?"
          ]
        },
        "electronics-comm": {
          label: "Electronics & Communication",
          questions: [
            "What is the difference between analog and digital signals?",
            "Explain the working of a diode and a transistor.",
            "What is the difference between a microcontroller and a microprocessor?",
            "Explain the concept of modulation in communication systems.",
            "What is the difference between an amplifier and an oscillator?",
            "Explain the basic logic gates: AND, OR, NOT.",
            "What is a PCB, and how is it designed?",
            "What is the difference between LED and LCD displays?",
            "Explain the function of a rectifier circuit.",
            "Briefly explain the basics of mobile/wireless communication.",
            "What is an integrated circuit (IC)?",
            "What is the difference between wired and wireless networks?",
            "What is signal-to-noise ratio?",
            "Explain the working of any sensor you have studied.",
            "What software/tools have you used for circuit simulation?"
          ]
        }
      }
    },

    civil: {
      label: "Civil Engineering",
      subfields: {
        "civil-general": {
          label: "Civil Engineering (General)",
          questions: [
            "What is the difference between load-bearing and framed structures?",
            "Explain the significance of the water-cement ratio in concrete.",
            "What is the purpose of reinforcement in RCC?",
            "Explain the difference between a beam and a column.",
            "Why is soil testing important before construction?",
            "What are the common types of foundations used in buildings?",
            "What is surveying, and which instruments have you used?",
            "What is the difference between bitumen roads and concrete roads?",
            "Why are building codes/bye-laws important?",
            "Explain the curing process of concrete and why it matters.",
            "What safety measures are followed at a construction site?",
            "What is the difference between a truss and a frame structure?",
            "What is estimation and costing in civil engineering?",
            "Explain the function of a retaining wall.",
            "Why is quality control important during construction?"
          ]
        }
      }
    },

    commerce: {
      label: "Commerce & Business",
      subfields: {
        "accounting-finance": {
          label: "Accounting & Finance",
          questions: [
            "What is the difference between accounting and finance?",
            "What is the basic accounting equation?",
            "Explain the difference between debit and credit.",
            "What are the main financial statements of a business?",
            "What is the difference between profit and cash flow?",
            "What is depreciation, and what are its common methods?",
            "What is the difference between direct and indirect tax?",
            "What is GST, and how does it work in India?",
            "What is the difference between capital and revenue expenditure?",
            "What is a balance sheet used for?",
            "Explain the concept of break-even analysis.",
            "What software have you used for accounting (e.g. Tally, Excel)?",
            "What is the difference between gross profit and net profit?",
            "What is bank reconciliation?",
            "Why is budgeting important for a business?"
          ]
        },
        "marketing": {
          label: "Marketing",
          questions: [
            "Explain the 4 Ps of marketing.",
            "What is the difference between marketing and sales?",
            "Explain the concept of target audience and market segmentation.",
            "What is digital marketing, and what are its key channels?",
            "What is the difference between B2B and B2C marketing?",
            "What is a brand, and why is branding important?",
            "Explain the concept of SWOT analysis.",
            "What is Customer Relationship Management (CRM)?",
            "What is the difference between above-the-line and below-the-line marketing?",
            "What is SEO, and why does it matter for a business?",
            "Explain the stages of the product life cycle.",
            "What role does social media play in modern marketing?",
            "What is market research, and why is it done?",
            "What is a Unique Selling Proposition (USP)?",
            "How do you measure the success of a marketing campaign?"
          ]
        },
        "hr": {
          label: "Human Resources",
          questions: [
            "What is the role of the HR department in an organization?",
            "What is the difference between recruitment and selection?",
            "What is employee onboarding, and why does it matter?",
            "Explain the concept of performance appraisal.",
            "What is the difference between training and development?",
            "What labour laws are you aware of (e.g. minimum wages, PF, ESI)?",
            "What is employee engagement, and why does it matter?",
            "Explain the concept of organizational culture.",
            "What is the difference between HRM and HRD?",
            "How would you handle a conflict between two employees?",
            "What is payroll management?",
            "Explain the concept of attrition and how to reduce it.",
            "What is the difference between a job description and a job specification?",
            "How would you conduct an effective interview?",
            "Why is diversity and inclusion important in the workplace?"
          ]
        }
      }
    },

    science: {
      label: "Science",
      subfields: {
        "physics": {
          label: "Physics",
          questions: [
            "State and explain Newton's laws of motion.",
            "What is the difference between speed and velocity?",
            "Explain the concepts of work, energy, and power.",
            "Explain the laws of thermodynamics briefly.",
            "What is the photoelectric effect?",
            "Explain the concept of electric and magnetic fields.",
            "What is the difference between reflection and refraction?",
            "Explain the concept of wave-particle duality.",
            "What is the significance of Einstein's theory of relativity?",
            "Explain the difference between potential and kinetic energy.",
            "What is the difference between scalar and vector quantities?",
            "Explain the working of a simple pendulum.",
            "Explain the concept of resistance and conductivity.",
            "Explain the basics of nuclear fission and fusion.",
            "How would you explain a physics concept to someone with no background in it?"
          ]
        },
        "chemistry": {
          label: "Chemistry",
          questions: [
            "Explain the periodic table and periodic trends.",
            "What is the difference between a physical and a chemical change?",
            "Explain acids, bases, and the pH scale.",
            "What is a chemical bond? Explain its types.",
            "Explain the concept of oxidation and reduction.",
            "What is the mole concept in chemistry?",
            "What is the difference between organic and inorganic chemistry?",
            "What is a catalyst, and how does it work?",
            "Explain the concept of chemical equilibrium.",
            "What are polymers? Give some examples.",
            "What is the difference between exothermic and endothermic reactions?",
            "What safety precautions are followed in a chemistry lab?",
            "Explain the concepts of molarity and normality.",
            "What is electrochemistry?",
            "Explain the concept of isotopes."
          ]
        },
        "biology": {
          label: "Biology",
          questions: [
            "Explain the structure and function of a cell.",
            "What is the difference between DNA and RNA?",
            "Explain the process of photosynthesis.",
            "What is the difference between mitosis and meiosis?",
            "Briefly explain the human digestive system.",
            "What is the difference between prokaryotic and eukaryotic cells?",
            "Explain the basics of genetics and heredity.",
            "What is the immune system, and how does it work?",
            "Explain the process of respiration in humans.",
            "What is an ecosystem, and what are its components?",
            "Explain the theory of evolution briefly.",
            "What are enzymes, and what role do they play in the body?",
            "Briefly explain the human circulatory system.",
            "What is the difference between vaccination and natural immunity?",
            "Explain the concept of biodiversity and its importance."
          ]
        }
      }
    },

    arts: {
      label: "Arts & Humanities",
      subfields: {
        "english-literature": {
          label: "English / Literature",
          questions: [
            "What inspired you to study literature or English?",
            "What is the difference between prose and poetry?",
            "Name a favourite literary work and explain why you like it.",
            "Explain any figures of speech with examples.",
            "What is the difference between fiction and non-fiction?",
            "How do you approach analyzing a poem or a text?",
            "Why is grammar important in effective communication?",
            "What is the difference between formal and informal writing?",
            "How would you help someone improve their spoken English?",
            "How do you apply critical thinking to literary analysis?",
            "What role does literature play in society?",
            "What is the difference between a novel and a short story?",
            "How do you approach translation or comprehension tasks?",
            "What is your approach to public speaking?",
            "How do you stay updated with contemporary literature?"
          ]
        },
        "history-polsci": {
          label: "History / Political Science",
          questions: [
            "Why did you choose to study history or political science?",
            "Explain the significance of the Indian Constitution.",
            "What is the difference between a democracy and a monarchy?",
            "Discuss a major historical event and its impact.",
            "What is the role of the judiciary in a democracy?",
            "Explain the concept of federalism.",
            "What is the difference between the Lok Sabha and the Rajya Sabha?",
            "Explain the significance of fundamental rights and duties.",
            "What is the role of the Election Commission of India?",
            "Explain the concept of secularism in the Indian context.",
            "Why is local self-governance (Panchayati Raj) important?",
            "Discuss a major freedom movement and its significance.",
            "What is the difference between domestic and foreign policy?",
            "How do current affairs relate to your field of study?",
            "What civic responsibilities do you believe every citizen has?"
          ]
        }
      }
    },

    medical: {
      label: "Medical & Healthcare",
      subfields: {
        "nursing": {
          label: "Nursing",
          questions: [
            "Why did you choose nursing as a profession?",
            "Explain the basic steps of first aid for a bleeding wound.",
            "Why is hygiene and sterilization important in patient care?",
            "How would you take a patient's vital signs?",
            "What is the role of a nurse within a hospital team?",
            "How do you stay calm while handling a medical emergency?",
            "Why is patient confidentiality important?",
            "What precautions do you take while administering medication?",
            "How would you communicate with a frightened or anxious patient?",
            "Why is accurate record-keeping important in nursing?",
            "What is the difference between an acute and a chronic illness?",
            "How would you handle a difficult or non-cooperative patient?",
            "What infection control practices do you follow?",
            "What kind of continuing education do nurses need?",
            "How do you manage stress in a high-pressure ward environment?"
          ]
        },
        "pharmacy": {
          label: "Pharmacy",
          questions: [
            "Why did you choose pharmacy as a career?",
            "What is the difference between generic and branded medicines?",
            "What is the role of a pharmacist in patient care?",
            "Why is checking for drug interactions important?",
            "What is the difference between OTC and prescription drugs?",
            "What are the proper storage conditions for medicines?",
            "What precautions do you take while dispensing medication?",
            "How should expired medicines be disposed of?",
            "What is the difference between working in a hospital pharmacy vs a retail store?",
            "Why is patient counselling an important part of a pharmacist's job?",
            "What is the difference between tablets, capsules, and syrups?",
            "How would you handle a prescription that seems incorrect?",
            "Why is maintaining pharmacy records important?",
            "What government regulations govern pharmacy practice in India?",
            "How do you stay updated with new drugs and guidelines?"
          ]
        }
      }
    },

    iti: {
      label: "ITI / Vocational Trades",
      subfields: {
        "electrician": {
          label: "Electrician",
          questions: [
            "What is the difference between AC and DC supply?",
            "What safety precautions do you take before starting electrical work?",
            "Explain the function of a fuse and an MCB.",
            "What tools does an electrician commonly use?",
            "How would you wire a simple household circuit?",
            "What is earthing, and why is it necessary?",
            "What is the difference between a series and a parallel circuit connection?",
            "How do you troubleshoot a short circuit?",
            "What is the function of a switchboard/distribution board?",
            "Explain the working of a ceiling fan motor.",
            "What precautions are needed while handling high-voltage lines?",
            "How do you use a multimeter?",
            "What is the purpose of a voltage stabilizer?",
            "How do you maintain and service electrical appliances?",
            "What government safety codes apply to electrical wiring in India?"
          ]
        },
        "fitter": {
          label: "Fitter",
          questions: [
            "What is the role of a fitter in a workshop?",
            "Explain the use of common fitting tools like files, hacksaws, and chisels.",
            "What is the difference between marking tools and measuring tools?",
            "Explain the process of filing and its types.",
            "What is the purpose of a vice in fitting work?",
            "How would you check the flatness of a surface?",
            "What safety gear do you use while fitting or machining?",
            "What is the difference between drilling and reaming?",
            "What is the purpose of tapping and dieing in fitting work?",
            "Explain the concept of tolerance in fitting work.",
            "What is a surface plate used for?",
            "Explain the process of assembling machine parts.",
            "What precautions do you take while using power tools?",
            "Why is it important to maintain your tools and equipment?",
            "How do you read engineering drawings on the job?"
          ]
        },
        "welding": {
          label: "Welding",
          questions: [
            "What is the difference between arc welding and gas welding?",
            "What safety equipment is required for welding work?",
            "What is the purpose of a welding electrode?",
            "What are some common welding defects and their causes?",
            "What is the difference between MIG and TIG welding?",
            "How do you check the quality of a weld joint?",
            "What precautions help avoid exposure to welding fumes?",
            "Explain the concept of welding current and polarity.",
            "Why is pre-heating sometimes needed before welding?",
            "What is the difference between a butt joint and a lap joint?",
            "What is the role of flux in welding?",
            "How do you prevent warping during welding?",
            "What is spot welding, and where is it used?",
            "What fire safety precautions apply in a welding workshop?",
            "How do you inspect and test a finished weld?"
          ]
        }
      }
    },

    management: {
      label: "Management (MBA / BBA)",
      subfields: {
        "management-general": {
          label: "General Management",
          questions: [
            "Why did you choose to pursue management studies?",
            "What is the difference between leadership and management?",
            "What is a SWOT analysis, and how is it used?",
            "Explain the concept of organizational behavior.",
            "What is the difference between strategic and operational planning?",
            "Why is time management important in business?",
            "What is supply chain management?",
            "What is the difference between a manager and an entrepreneur?",
            "What is stakeholder management?",
            "Explain the concept of change management.",
            "Why are business ethics important?",
            "Explain the concept of competitive advantage.",
            "What role does data/analytics play in decision making?",
            "How would you motivate an underperforming team member?",
            "What is your approach to problem-solving in a business scenario?"
          ]
        }
      }
    },

    law: {
      label: "Law",
      subfields: {
        "law-general": {
          label: "Law (General)",
          questions: [
            "Why did you choose law as a career?",
            "What is the difference between civil law and criminal law?",
            "What is the significance of the Indian Constitution?",
            "Explain the concept of fundamental rights.",
            "What is the difference between a bailable and a non-bailable offence?",
            "What is the difference between the role of a judge and a lawyer?",
            "Explain the concept of natural justice.",
            "What is the process of filing an FIR?",
            "What is the difference between a contract and an agreement?",
            "Explain the jurisdiction of courts in India.",
            "What is a Public Interest Litigation (PIL)?",
            "Why is legal aid important for the underprivileged?",
            "What is the difference between arbitration and litigation?",
            "What is your understanding of consumer protection laws?",
            "How do you stay updated with changes in the law?"
          ]
        }
      }
    },

    education: {
      label: "Education / Teaching (B.Ed)",
      subfields: {
        "education-general": {
          label: "Teaching (General)",
          questions: [
            "Why did you choose teaching as a profession?",
            "What is the difference between teaching and learning?",
            "What teaching methods do you find most effective?",
            "How would you handle a classroom with students of mixed ability?",
            "Why is lesson planning important?",
            "How do you assess student understanding beyond exams?",
            "What is the role of a teacher in a child's overall development?",
            "How would you handle discipline issues in a classroom?",
            "Why is inclusive education important?",
            "What role does technology play in modern teaching?",
            "How do you keep students engaged during a lesson?",
            "Explain the concept of Continuous and Comprehensive Evaluation (CCE).",
            "How would you handle a parent's complaint about their child?",
            "Why are extracurricular activities important in education?",
            "How do you approach your own professional development as a teacher?"
          ]
        }
      }
    }
  };

  function populateFieldDropdown() {
    const fieldSelect = document.getElementById('ip-field');
    fieldSelect.innerHTML = '';
    Object.keys(FIELDS).forEach(fieldKey => {
      const opt = document.createElement('option');
      opt.value = fieldKey;
      opt.textContent = FIELDS[fieldKey].label;
      fieldSelect.appendChild(opt);
    });
    populateSubfieldDropdown(fieldSelect.value);
  }

  function populateSubfieldDropdown(fieldKey) {
    const subfieldSelect = document.getElementById('ip-subfield');
    subfieldSelect.innerHTML = '';
    const subfields = FIELDS[fieldKey].subfields;
    Object.keys(subfields).forEach(subKey => {
      const opt = document.createElement('option');
      opt.value = subKey;
      opt.textContent = subfields[subKey].label;
      subfieldSelect.appendChild(opt);
    });
  }

  function renderQuestions(fieldKey, subKey) {
    const field = FIELDS[fieldKey];
    const sub = field.subfields[subKey];

    document.getElementById('ip-results-title').textContent =
      `Your 30 Interview Questions — ${field.label} → ${sub.label}`;
    document.getElementById('ip-specific-title').textContent =
      `🎯 ${sub.label} Questions (16–30)`;

    const commonList = document.getElementById('ip-common-list');
    commonList.innerHTML = COMMON_QUESTIONS.map(q => `<li>${escapeHtml(q)}</li>`).join('');

    const specificList = document.getElementById('ip-specific-list');
    specificList.innerHTML = sub.questions.map(q => `<li>${escapeHtml(q)}</li>`).join('');

    document.getElementById('ip-results-wrapper').style.display = 'block';
    document.getElementById('ip-results-wrapper').scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }

  function init() {
    populateFieldDropdown();

    document.getElementById('ip-field').addEventListener('change', (e) => {
      populateSubfieldDropdown(e.target.value);
    });

    document.getElementById('ip-select-form').addEventListener('submit', (e) => {
      e.preventDefault();
      const fieldKey = document.getElementById('ip-field').value;
      const subKey = document.getElementById('ip-subfield').value;
      renderQuestions(fieldKey, subKey);
    });
  }

  return { init };
})();
