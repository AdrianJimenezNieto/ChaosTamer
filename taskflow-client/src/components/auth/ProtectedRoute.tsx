import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "../../store/authStore";

export default function ProtectedRoute() {
  // Obtain the token
  const { isAuth } = useAuthStore();

  // If token exists, render the component
  if (isAuth) {
    return <Outlet />;
  }

  // If not token, redirect the user to /login
  return <Navigate to="/login" replace />
}