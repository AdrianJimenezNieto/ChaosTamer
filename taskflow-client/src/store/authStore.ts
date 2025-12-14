import { create } from 'zustand';
import { persist } from 'zustand/middleware'; // to save in localStorage

// Define the "shape" of our state
interface AuthState {
  token: string | null;
  isAuthenticated: boolean;
  setToken: (token: string) => void; // Action for saving the token
  logout: () => void; // Action for deleting the token
}

// Create the store
export const useAuthStore = create<AuthState>()(
  // persist wrap our store
  persist(
    (set) => ({
      token: null, // inital state
      isAuthenticated: false,
      setToken: (token) => set({ token, isAuthenticated: true}),
      logout: () => set({ token: null, isAuthenticated: false})
    }),
    {
      // name of the localstorage
      name: 'auth-storage',
    }
  )
);