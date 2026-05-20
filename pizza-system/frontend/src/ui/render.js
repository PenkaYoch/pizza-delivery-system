import { elements } from "./dom.js";
import { escapeHtml, formatDate, statusLabel } from "./format.js";

export function setMessage(text, tone = "info") {
  elements.message.textContent = text;
  elements.message.dataset.tone = tone;
}

export function render(state) {
  renderMetrics(state);
  renderTasks(state.tasks);
  renderNotifications(state.notifications);
  elements.lastUpdated.textContent = `Updated ${new Date().toLocaleTimeString()}`;
}

function renderMetrics({ tasks, notifications }) {
  const preparing = tasks.filter((task) => task.status === "PREPARING").length;
  const ready = tasks.filter((task) => task.status === "READY").length;

  elements.totalTasks.textContent = tasks.length;
  elements.preparingTasks.textContent = preparing;
  elements.readyTasks.textContent = ready;
  elements.notificationCount.textContent = notifications.length;
}

function renderTasks(tasks) {
  if (tasks.length === 0) {
    elements.tasksTable.innerHTML = `
      <tr>
        <td class="empty-state" colspan="4">No tasks yet.</td>
      </tr>
    `;
    return;
  }

  elements.tasksTable.innerHTML = tasks
    .slice()
    .sort((a, b) => Number(b.id || 0) - Number(a.id || 0))
    .map(taskTemplate)
    .join("");
}

function renderNotifications(notifications) {
  if (notifications.length === 0) {
    elements.notificationsList.innerHTML = `<li class="empty-state">No notifications yet.</li>`;
    return;
  }

  elements.notificationsList.innerHTML = notifications
    .slice()
    .sort((a, b) => new Date(b.timestamp || 0) - new Date(a.timestamp || 0))
    .slice(0, 12)
    .map(notificationTemplate)
    .join("");
}

function taskTemplate(task) {
  return `
    <tr>
      <td>#${task.id}</td>
      <td>${escapeHtml(task.pizzaName || "-")}</td>
      <td><span class="status-pill" data-status="${escapeHtml(task.status)}">${escapeHtml(statusLabel(task.status))}</span></td>
      <td>${formatDate(task.createdAt)}</td>
    </tr>
  `;
}

function notificationTemplate(notification) {
  return `
    <li>
      <div>
        <strong>${escapeHtml(notification.pizzaName || "Pizza")}</strong>
        <span>${escapeHtml(statusLabel(notification.status))}</span>
      </div>
      <time>${formatDate(notification.timestamp)}</time>
    </li>
  `;
 }