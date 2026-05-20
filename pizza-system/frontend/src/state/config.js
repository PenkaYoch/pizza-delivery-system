const defaults = {
  taskApiUrl: window.location.origin,
  notificationApiUrl: window.location.origin
};

const legacyUrls = new Set(["http://localhost:8080/", "http://localhost:8081"]);

export function getApiUrl(key) {
  return localStorage.getItem(key)  defaults[key];
}

export function saveApiUrl(key, value) {
  localStorage.setItem(key, value.trim()  defaults[key]);
}

export function resetLegacyApiUrls() {
  for (const key of Object.keys(defaults)) {
    if (legacyUrls.has(localStorage.getItem(key))) {
      localStorage.removeItem(key);
    }
  }
}