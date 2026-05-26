async function requestJson(url, options = {}) {
  const response = await fetch(url, {
    headers: {
      "Content-Type": "application/json",
      ...options.headers
    },
    ...options
  });

  if (!response.ok) {
    throw new Error(`${response.status} ${response.statusText}`);
  }

  return response.status === 204 ? null : response.json();
}

export function fetchTasks(taskApiUrl) {
  return requestJson(`${taskApiUrl}/api/tasks`);
}

export function fetchNotifications(notificationApiUrl) {
  return requestJson(`${notificationApiUrl}/api/notifications`);
}

export function createTask(taskApiUrl, pizzaName) {
  return requestJson(`${taskApiUrl}/api/tasks`, {
    method: "POST",
    body: JSON.stringify({ name: pizzaName })
  });
}

export function updateTask(taskApiUrl, taskId, updates) {
  return requestJson(${taskApiUrl}/api/tasks/${taskId}, {
    method: "PATCH",
    body: JSON.stringify(updates)
  });
}

export function deleteTask(taskApiUrl, taskId) {
  return requestJson(${taskApiUrl}/api/tasks/${taskId}, {
    method: "DELETE"
  });
}