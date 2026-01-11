import { create } from 'zustand';
import { persist } from 'zustand/middleware'; // to save in localStorage
import { getMe } from '../services/authService';
import type { User } from '../types/auth.types';

// Define the "shape" of our state
interface AuthState {
  token: string | null;
  user: User | null;
  isAuth: boolean;
  setToken: (token: string) => void; // Action for saving the token
  checkAuth: () => Promise<void>;
  logout: () => void; // Action for deleting the token
}

// Create the store
export const useAuthStore = create<AuthState>()(
  // persist wrap our store
  persist(
    (set, get) => ({
      token: null, // inital state
      user: null,
      isAuth: false,

      setToken: (token) => set({ token, isAuth: true}),
      logout: () => set({ token: null, user: null, isAuth: false}),

      checkAuth: async () => {
        const token = get().token;

        if(!token) {
          set({ user: null, isAuth: false});
          return;
        }

        try {
          const user = await getMe();
          set({ user, isAuth: true});
        } catch (error) {
          console.log("Sesión expirada o inválida");
          set({ token: null, user: null, isAuth: false});
        }
      }
    }),
    {
      // name of the localstorage
      name: 'auth-storage',
    }
  )
);