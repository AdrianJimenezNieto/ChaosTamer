import { Navigate, Outlet } from "react-router-dom";
import { useAuthStore } from "../../store/authStore";

export const PublicRoute = () => {
    const { isAuth } = useAuthStore();

    if (isAuth) {
        return <Navigate to="/dashboard" replace />
    }

    return <Outlet />;
};