import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function Layout({ children }) {
  const { user, logout, isManager, isReceptionist, isCustomer } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="layout">
      <header className="header">
        <Link to="/" className="logo">Ocean View Resort</Link>
        <nav className="nav">
          {user && (
            <>
              {isCustomer && (
                <>
                  <Link to="/dashboard">Dashboard</Link>
                  <Link to="/booking">Book a Room</Link>
                </>
              )}
              {(isReceptionist || isManager) && (
                <>
                  <Link to="/reception">Reception</Link>
                  <Link to="/reservations">Reservations</Link>
                </>
              )}
              {isManager && (
                <>
                  <Link to="/reports">Reports</Link>
                  <Link to="/staff">Staff</Link>
                </>
              )}
              <span className="user-name">{user.firstName} ({user.roleCode})</span>
              <button type="button" className="btn btn-ghost" onClick={handleLogout}>Logout</button>
            </>
          )}
          {!user && (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
            </>
          )}
        </nav>
      </header>
      <main className="main">{children}</main>
    </div>
  );
}
