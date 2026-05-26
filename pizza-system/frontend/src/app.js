import { createTask, deleteTask, fetchNotifications, fetchTasks, updateTask } from "./services/api.js?v=20260526-last";
import { getApiUrl } from "./state/config.js?v=20260526-last";
import { elements } from "./ui/dom.js?v=20260526-last";
import { render, setMessage } from "./ui/render.js?v=20260526-last";

const state = {
  tasks: [],
  notifications: []
};

async function loadTasks() {
  state.tasks = await fetchTasks(getApiUrl());
}

async function loadNotifications() {
  state.notifications = await fetchNotifications(getApiUrl());
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
    await createTask(getApiUrl(), pizzaName);
    elements.pizzaName.value = "";
    setMessage("Pizza task created.", "success");
    await refreshData();
  } catch (error) {
    setMessage(`Could not create task: ${error.message}`, "error");
  }
}

async function handleTaskAction(event) {
  const button = event.target.closest("[data-task-action]");
  if (!button) {
    return;
  }

  const taskId = button.dataset.taskId;
  const action = button.dataset.taskAction;
  const task = state.tasks.find((item) => String(item.id) === taskId);

  if (!task) {
    setMessage("Task not found. Refresh and try again.", "error");
    return;
  }

  try {
    if (action === "rename") {
      const pizzaName = window.prompt("Pizza name", task.pizzaName || "");
      if (pizzaName == null || pizzaName.trim() === "") {
        return;
      }

      await updateTask(getApiUrl(), taskId, { pizzaName: pizzaName.trim() });
      setMessage("Task renamed.", "success");
    }

    if (action === "status") {
      const nextStatus = task.status === "READY" ? "PREPARING" : "READY";
      await updateTask(getApiUrl(), taskId, { status: nextStatus });
      setMessage(`Task marked ${nextStatus.toLowerCase()}.`, "success");
    }

    if (action === "delete") {
      if (!window.confirm(`Delete ${task.pizzaName || "this task"}?`)) {
        return;
      }

      await deleteTask(getApiUrl(), taskId);
      setMessage("Task deleted.", "success");
    }

    await refreshData();
  } catch (error) {
    setMessage(`Task action failed: ${error.message}`, "error");
  }
}

function startApp() {
  elements.form.addEventListener("submit", handleCreateTask);
  elements.refreshButton.addEventListener("click", refreshData);
  elements.tasksTable.addEventListener("click", handleTaskAction);

  refreshData();
  setInterval(refreshData, 10000);
}

try {
  startApp();
} catch (error) {
  setMessage(`Frontend startup failed: ${error.message}`, "error");
  throw error;
}