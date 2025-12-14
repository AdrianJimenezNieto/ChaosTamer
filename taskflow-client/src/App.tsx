import { Routes, Route, Navigate } from 'react-router-dom';
import { useEffect } from 'react';
import LoginPage from './components/pages/LoginPage.tsx';
import DashboardPage from './components/pages/DashboardPage.tsx';
import ProtectedRoute from './components/auth/ProtectedRoute.tsx';
import BoardDetailPage from './components/pages/BoardDetailPage.tsx';
import { PublicRoute } from './components/auth/PublicRoute.tsx';
import { RegisterPage } from './components/pages/RegisterPage.tsx';
import { useAuthStore } from './store/authStore.ts';


function App() {
  // Extract the session of the global store
  const checkAuth = useAuthStore((state) => state.checkAuth);

  // Effect to check the session
  useEffect(() => {
    checkAuth();
  }, [checkAuth])

  return(
    // Dark background for all the app
    <div className='min-h-screen w-full bg-gray-900'>
      <Routes>
        {/* Public routes */}
        <Route element={<PublicRoute />}>
          <Route path='/login' element={<LoginPage />} />
          <Route path='/register' element={<RegisterPage />} />
        </Route>

        {/* Protected Route */}
        <Route element={<ProtectedRoute />}>
          {/* Dashboard */}
          <Route path='/dashboard' element={<DashboardPage />} />
          {/* Board Detail */}
          <Route path='/board/:boardId' element={<BoardDetailPage />} />
        </Route>

        {/* Default redirect */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />

        {/* Unkown routes to dashboard */}
        {/* TODO: Cutom 404 */}
        <Route path='*' element={<Navigate to="/dashboard" replace />}/>
      </Routes>
    </div>
  )
}

export default App;
