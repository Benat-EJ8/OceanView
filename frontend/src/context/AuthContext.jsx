import { createContext, useContext, useState, useEffect } from 'react';
import { me, logout as apiLogout } from '../api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    me()
      .then((res) => setUser(res.data))
      .catch(() => setUser(null))
      .finally(() => setLoading(false));
  }, []);

  const login = (userData) => setUser(userData);
  const logout = async () => {
    await apiLogout();
    setUser(null);
  };

  const role = user?.roleCode || null;
  const isAdmin = role === 'ADMIN';
  const isManager = role === 'MANAGER' || isAdmin;
  const isReceptionist = role === 'RECEPTIONIST' || isManager;
  const isCustomer = role === 'CUSTOMER';

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, role, isAdmin, isManager, isReceptionist, isCustomer }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
