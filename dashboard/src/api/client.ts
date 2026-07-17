import axios from "axios";

let inMemoryToken = "";

export function setAuthToken(token: string) {
  inMemoryToken = token;
}

export function clearAuthToken() {
  inMemoryToken = "";
}

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8082/api/v1"
});

api.interceptors.request.use((config) => {
  if (inMemoryToken) {
    config.headers.Authorization = `Bearer ${inMemoryToken}`;
  }
  return config;
});
