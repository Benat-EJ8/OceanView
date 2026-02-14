import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Button,
  Alert,
} from '@mui/material';
import { register } from '../api';
import { useAuth } from '../context/AuthContext';

export function Register() {
  const [form, setForm] = useState({ username: '', password: '', email: '', firstName: '', lastName: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login: setUser } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await register(form);
      setUser(res.data);
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', p: 2, bgcolor: 'background.default' }}>
      <Card sx={{ width: '100%', maxWidth: 420 }}>
        <CardContent sx={{ p: 4 }}>
          <Typography variant="h4" align="center" fontWeight="bold" color="primary.main" gutterBottom>Create account</Typography>
          <Typography align="center" color="text.secondary" sx={{ mb: 3 }}>Book and manage your stays</Typography>
          {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
          <form onSubmit={handleSubmit}>
            <TextField fullWidth label="First name" name="firstName" value={form.firstName} onChange={handleChange} required margin="normal" />
            <TextField fullWidth label="Last name" name="lastName" value={form.lastName} onChange={handleChange} required margin="normal" />
            <TextField fullWidth label="Email" name="email" type="email" value={form.email} onChange={handleChange} required margin="normal" />
            <TextField fullWidth label="Username" name="username" value={form.username} onChange={handleChange} required margin="normal" />
            <TextField fullWidth label="Password" name="password" type="password" value={form.password} onChange={handleChange} required margin="normal" />
            <Button type="submit" variant="contained" fullWidth size="large" disabled={loading} sx={{ mt: 2 }}>
              {loading ? 'Creating...' : 'Register'}
            </Button>
          </form>
          <Typography align="center" color="text.secondary" sx={{ mt: 2, fontSize: '0.9rem' }}>
            Already have an account? <Link to="/login">Login</Link>.
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
}
