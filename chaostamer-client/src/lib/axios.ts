import axios from "axios";
import { useAuthStore } from "../store/authStore";

// Vite env variable
// const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
const apiBaseUrl = 'http://localhost:8080/api/v1';

// Create the axios instance
export const api = axios.create({
  // We define the base URL of our SpringBoot API
  baseURL: apiBaseUrl
});

// Axios Interceptor
// Rquest interceptor
api.interceptors.request.use(
  (config) => {
    // Obtain the token from Zustand
    const token = useAuthStore.getState().token;

    // If the token exists, we add it to the header 'Authorization'
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    // return the config
    return config;
  },
  (error) => {
    // Error handling 
    return Promise.reject(error);
  }
)

// Response interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      // Clean the token from the global state
      useAuthStore.getState().logout();
    }

    return Promise.reject(error);
  }
);

export default api;