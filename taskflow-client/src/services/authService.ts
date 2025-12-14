import api from "../lib/axios";
import { useAuthStore } from "../store/authStore";

// Define the shape of the token (matches the backend DTO)
interface LoginResponse {
  token: string;
  tokenType: string;
};

// Define the shape of the login data (matches the backend DTO)
interface LoginData {
  email: string;
  password: string;
};

// Define the interface for the register data (matches de backend DTO)
interface RegisterData {
  fullName: string;
  email: string;
  password: string;
};

// Async login function
export const login = async (loginData: LoginData): Promise<string> => {
  try {
    // Call the endpoint /auth/login
    const response = await api.post<LoginResponse>('/auth/login', loginData);

    // Extract the token
    const token = response.data.token;

    // Store the token with Zustand globaly
    useAuthStore.getState().setToken(token);

    return token;
  } catch (error) {
    // Exception management
    console.error("Error en el login: ", error);
    throw new Error("Email o contraseña incorrectos.")
  }
};

// Logout service
export const logout = () => {
  // Delete the token from the storage
  useAuthStore.getState().logout();
};

// Register service
export const register = async (registerData: RegisterData): Promise<void> => {
  try {
    const payload = {
      name: registerData.fullName.split(" ")[0],
      lastName: registerData.fullName.split(" ").slice(1).join(" ") || "",
      email: registerData.email,
      password: registerData.password
    }
    console.log(payload);
    // call the endpoint
    await api.post('/auth/register', payload);
    console.log("✅ Registro satisfactorio")
  } catch (error) {
    console.error("Error al registrarse: ", error);
    throw new Error("No se pudo crear la cuenta, inténtalo más tarde.");
  }
};