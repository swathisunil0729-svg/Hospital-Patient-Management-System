(() => {
  "use strict";

  // In-memory data for this demo. Refreshing the page clears all records.
  const patients = [];
  const regularQueue = [];
  const emergencyQueue = [];
  let nextId = 1;

  const $ = (id) => document.getElementById(id);
  const form = $("patientForm");
  const nameInput = $("patientName");
  const ageInput = $("patientAge");
  const conditionInput = $("patientCondition");
  const searchInput = $("searchInput");
  const tableBody = $("patientTableBody");
  const regularList = $("regularQueueList");
  const emergencyList = $("emergencyQueueList");
  const editDialog = $("editDialog");
  const editForm = $("editForm");
  let toastTimeout;

  function escapeHTML(value) {
    return String(value).replace(/[&<>"']/g, (character) => ({
      "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    })[character]);
  }

  function findPatient(id) {
    return patients.find((patient) => patient.id === Number(id));
  }

  function isWaiting(patient) {
    return regularQueue.some((item) => item.id === patient.id) ||
      emergencyQueue.some((item) => item.id === patient.id);
  }

  function statusFor(patient) {
    if (emergencyQueue.some((item) => item.id === patient.id)) {
      return '<span class="badge emergency">Emergency · P' + patient.priority + "</span>";
    }
    if (regularQueue.some((item) => item.id === patient.id)) {
      return '<span class="badge waiting">Regular · Waiting</span>';
    }
    return '<span class="badge regular">Registered</span>';
  }

  function showToast(message) {
    const toast = $("toast");
    toast.textContent = message;
    toast.classList.add("show");
    clearTimeout(toastTimeout);
    toastTimeout = setTimeout(() => toast.classList.remove("show"), 2800);
  }

  function renderStats() {
    $("totalPatients").textContent = patients.length;
    $("regularCount").textContent = regularQueue.length;
    $("emergencyCount").textContent = emergencyQueue.length;
    $("regularPill").textContent = regularQueue.length + " waiting";
    $("emergencyPill").textContent = emergencyQueue.length + " waiting";
  }

  function renderPatients() {
    const query = searchInput.value.trim().toLowerCase();
    const filtered = patients.filter((patient) =>
      [patient.id, patient.name, patient.age, patient.condition]
        .some((value) => String(value).toLowerCase().includes(query))
    );

    if (filtered.length === 0) {
      tableBody.innerHTML = '<tr><td colspan="6" class="empty">' +
        (patients.length ? "No matching patient records." : "No patients registered yet.") +
        "</td></tr>";
      return;
    }

    tableBody.innerHTML = filtered.map((patient) => `
      <tr>
        <td>#${patient.id}</td>
        <td><span class="patient-name">${escapeHTML(patient.name)}</span></td>
        <td>${patient.age}</td>
        <td>${escapeHTML(patient.condition)}</td>
        <td>${statusFor(patient)}</td>
        <td><div class="action-row">
          <button class="secondary" type="button" data-action="edit" data-id="${patient.id}">Edit</button>
          <button class="danger" type="button" data-action="remove" data-id="${patient.id}">Remove</button>
          <button class="secondary" type="button" data-action="regular" data-id="${patient.id}" ${isWaiting(patient) ? "disabled" : ""}>Regular</button>
          <button class="danger" type="button" data-action="emergency" data-id="${patient.id}" ${isWaiting(patient) ? "disabled" : ""}>Emergency</button>
        </div></td>
      </tr>
    `).join("");
  }

  function renderQueueList(listElement, queue, isEmergency) {
    if (queue.length === 0) {
      listElement.innerHTML = '<li class="empty-list">No ' +
        (isEmergency ? "emergency" : "regular") + " patients waiting.</li>";
      return;
    }
    listElement.innerHTML = queue.map((patient, index) => `
      <li class="queue-item">
        <div><strong>${index + 1}. ${escapeHTML(patient.name)} <span style="color:var(--muted);font-weight:500">(#${patient.id})</span></strong>
        <small>Age ${patient.age} · ${escapeHTML(patient.condition)}</small></div>
        ${isEmergency ? '<span class="queue-priority">Priority ' + patient.priority + "</span>" : ""}
      </li>
    `).join("");
  }

  function render() {
    // Lower priority number is more urgent; ID gives FIFO order for equal priorities.
    emergencyQueue.sort((a, b) => a.priority - b.priority || a.id - b.id);
    renderStats();
    renderPatients();
    renderQueueList(regularList, regularQueue, false);
    renderQueueList(emergencyList, emergencyQueue, true);
  }

  form.addEventListener("submit", (event) => {
    event.preventDefault();
    const name = nameInput.value.trim();
    const age = Number(ageInput.value);
    const condition = conditionInput.value.trim();

    if (!name || !condition || !Number.isInteger(age) || age < 0 || age > 130) {
      $("formMessage").textContent = "Enter a valid name, age (0–130), and condition.";
      return;
    }

    patients.push({ id: nextId++, name, age, condition, priority: 0 });
    form.reset();
    $("formMessage").textContent = "Patient registered successfully.";
    render();
    showToast("Patient record created.");
    nameInput.focus();
  });

  tableBody.addEventListener("click", (event) => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;
    const patient = findPatient(button.dataset.id);
    if (!patient) return;

    switch (button.dataset.action) {
      case "edit":
        $("editId").value = patient.id;
        $("editName").value = patient.name;
        $("editAge").value = patient.age;
        $("editCondition").value = patient.condition;
        editDialog.showModal();
        break;
      case "remove":
        if (window.confirm("Remove patient #" + patient.id + " (" + patient.name + ")?")) {
          removePatient(patient.id);
          showToast("Patient record removed.");
        }
        break;
      case "regular":
        addToRegular(patient);
        break;
      case "emergency": {
        const input = window.prompt("Enter emergency priority (1 = highest, 5 = lowest):", "1");
        if (input === null) return;
        const priority = Number(input);
        if (!Number.isInteger(priority) || priority < 1 || priority > 5) {
          showToast("Priority must be a whole number from 1 to 5.");
          return;
        }
        addToEmergency(patient, priority);
        break;
      }
    }
  });

  function addToRegular(patient) {
    if (isWaiting(patient)) {
      showToast("This patient is already waiting in a queue.");
      return;
    }
    patient.priority = 0;
    regularQueue.push(patient);
    render();
    showToast(patient.name + " added to the regular queue.");
  }

  function addToEmergency(patient, priority) {
    if (isWaiting(patient)) {
      showToast("This patient is already waiting in a queue.");
      return;
    }
    patient.priority = priority;
    emergencyQueue.push(patient);
    render();
    showToast(patient.name + " added to emergency queue.");
  }

  function removePatient(id) {
    const index = patients.findIndex((patient) => patient.id === Number(id));
    if (index < 0) return;
    patients.splice(index, 1);
    removeFromQueue(regularQueue, id);
    removeFromQueue(emergencyQueue, id);
    render();
  }

  function removeFromQueue(queue, id) {
    const index = queue.findIndex((patient) => patient.id === Number(id));
    if (index >= 0) queue.splice(index, 1);
  }

  $("serveRegularBtn").addEventListener("click", () => {
    const patient = regularQueue.shift();
    if (!patient) {
      showToast("No regular patients are waiting.");
      return;
    }
    render();
    showToast("Now serving regular patient: " + patient.name + " (ID #" + patient.id + ").");
  });

  $("serveEmergencyBtn").addEventListener("click", () => {
    emergencyQueue.sort((a, b) => a.priority - b.priority || a.id - b.id);
    const patient = emergencyQueue.shift();
    if (!patient) {
      showToast("No emergency patients are waiting.");
      return;
    }
    patient.priority = 0;
    render();
    showToast("Now serving emergency patient: " + patient.name + " (ID #" + patient.id + ").");
  });

  editForm.addEventListener("submit", (event) => {
    event.preventDefault();
    const patient = findPatient($("editId").value);
    if (!patient) {
      editDialog.close();
      return;
    }
    const name = $("editName").value.trim();
    const age = Number($("editAge").value);
    const condition = $("editCondition").value.trim();
    if (!name || !condition || !Number.isInteger(age) || age < 0 || age > 130) {
      showToast("Enter valid patient details.");
      return;
    }
    patient.name = name;
    patient.age = age;
    patient.condition = condition;
    editDialog.close();
    render();
    showToast("Patient details updated.");
  });

  function closeEditDialog() {
    editDialog.close();
  }
  $("closeDialogBtn").addEventListener("click", closeEditDialog);
  $("cancelEditBtn").addEventListener("click", closeEditDialog);
  searchInput.addEventListener("input", renderPatients);

  render();
})();
