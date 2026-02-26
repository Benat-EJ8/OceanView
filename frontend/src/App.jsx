import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { DashboardLayout } from './components/DashboardLayout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Landing } from './pages/Landing';
import { Login } from './pages/Login';
import { Register } from './pages/Register';
import { CustomerDashboard } from './pages/CustomerDashboard';
import { Booking } from './pages/Booking';
import { ReceptionistDashboard } from './pages/ReceptionistDashboard';
import { ReservationsPage } from './pages/ReservationsPage';
import { Reports } from './pages/Reports';
import { StaffManagement } from './pages/StaffManagement';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<DashboardLayout><Landing /></DashboardLayout>} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/booking" element={<DashboardLayout><Booking /></DashboardLayout>} />
          <Route path="/dashboard" element={<DashboardLayout><ProtectedRoute allowedRoles={['CUSTOMER']}><CustomerDashboard /></ProtectedRoute></DashboardLayout>} />
          <Route path="/reception" element={<DashboardLayout><ProtectedRoute allowedRoles={['RECEPTIONIST', 'MANAGER', 'ADMIN']}><ReceptionistDashboard /></ProtectedRoute></DashboardLayout>} />
          <Route path="/reservations" element={<DashboardLayout><ProtectedRoute allowedRoles={['RECEPTIONIST', 'MANAGER', 'ADMIN']}><ReservationsPage /></ProtectedRoute></DashboardLayout>} />
          <Route path="/reports" element={<DashboardLayout><ProtectedRoute allowedRoles={['MANAGER', 'ADMIN']}><Reports /></ProtectedRoute></DashboardLayout>} />
          <Route path="/staff" element={<DashboardLayout><ProtectedRoute allowedRoles={['MANAGER', 'ADMIN']}><StaffManagement /></ProtectedRoute></DashboardLayout>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
