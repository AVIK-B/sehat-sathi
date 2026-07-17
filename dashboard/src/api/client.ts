import axios from "axios";

let inMemoryToken = "";

export function setAuthToken(token: string) {
  inMemoryToken = token;
}

export function clearAuthToken() {
  inMemoryToken = "";
}
fetch('https://sehat-sathi-back.onrender.com/api/v1/auth/login', {
  method: 'POST',
  credentials: 'include',   // ← REQUIRED
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(data)
})

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8082/api/v1"
});

api.interceptors.request.use((config) => {
  if (inMemoryToken) {
    config.headers.Authorization = `Bearer ${inMemoryToken}`;
  }
  return config;
});
