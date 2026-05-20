import { createTask, fetchNotifications, fetchTasks } from "./services/api.js";
import { getApiUrl, resetLegacyApiUrls, saveApiUrl } from "./state/config.js";
import { elements } from "./ui/dom.js";
import { render, setMessage } from "./ui/render.js";

const state = {
  tasks: [],
  notifications: []
};

async function loadTasks() {
  state.tasks = await fetchTasks(getApiUrl("taskApiUrl"));
}

async function loadNotifications() {
  state.notifications = await fetchNotifications(getApiUrl("notificationApiUrl"));
}

async function refreshData() {
  elements.refreshButton.disabled = true;

  const results = await Promise.allSettled([loadTasks(), loadNotifications()]);
  render(state);

  const failed = results
    .map((result, index) => ({ result, label: index === 0 ? "tasks" : "notifications" }))
    .filter(({ result }) => result.status === "rejected");

  if (failed.length === 0) {
    setMessage("Data refreshed.", "success");
  } else {
    setMessage(`Could not load ${failed.map(({ label }) => label).join(" and ")}.`, "error");
  }

  elements.refreshButton.disabled = false;
}

async function handleCreateTask(event) {
  event.preventDefault();

  const pizzaName = elements.pizzaName.value.trim();
  if (!pizzaName) {
    return;
  }

  try {
    await createTask(getApiUrl("taskApiUrl"), pizzaName);
    elements.pizzaName.value = "";
    setMessage("Pizza task created.", "success");
    await refreshData();
  } catch (error) {
    setMessage(`Could not create task: ${error.message}`, "error");
  }
}

function bindSettings() {
  elements.taskApiUrl.value = getApiUrl("taskApiUrl");
  elements.notificationApiUrl.value = getApiUrl("notificationApiUrl");

  elements.taskApiUrl.addEventListener("change", () => {
    saveApiUrl("taskApiUrl", elements.taskApiUrl.value);
    refreshData();
  });

  elements.notificationApiUrl.addEventListener("change", () => {
    saveApiUrl("notificationApiUrl", elements.notificationApiUrl.value);
    refreshData();
  });
}

function startApp() {
  resetLegacyApiUrls();
  bindSettings();

  elements.form.addEventListener("submit", handleCreateTask);
  elements.refreshButton.addEventListener("click", refreshData);

  refreshData();
  setInterval(refreshData, 10000);
}

try {
  startApp();
} catch (error) {
  setMessage(`Frontend startup failed: ${error.message}`, "error");
  throw error;
}