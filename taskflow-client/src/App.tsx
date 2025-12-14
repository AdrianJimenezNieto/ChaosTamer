import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage.tsx';
import DashboardPage from './pages/DashboardPage.tsx';
import ProtectedRoute from './components/auth/ProtectedRoute.tsx';
import BoardDetailPage from './pages/BoardDetailPage.tsx';
import { PublicRoute } from './components/auth/PublicRoute.tsx';

function App() {
  return(
    // Dark background for all the app
    <div className='min-h-screen w-full bg-gray-900'>
      <Routes>
        {/* Public routes */}
        <Route element={<PublicRoute />}>
          <Route path='/login' element={<LoginPage />} />
          <Route path='/register' element={<Navigate to="/dashboard" replace />} />
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
        <Route path='*' element={<Navigate to="/dashboard" replace />}/>
      </Routes>
    </div>
  )
}

export default App
