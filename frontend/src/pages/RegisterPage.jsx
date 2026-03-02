import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Box, Card, CardContent, TextField, Button, Typography, InputAdornment, Grid } from '@mui/material';
import { Functions, Person, Email, Lock, Phone } from '@mui/icons-material';

export default function RegisterPage({ onRegister }) {
    const navigate = useNavigate();
    const [form, setForm] = useState({ firstName: '', lastName: '', email: '', phone: '', password: '', confirmPassword: '' });
    const [loading, setLoading] = useState(false);
    const set = (k) => (e) => setForm(f => ({ ...f, [k]: e.target.value }));

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (form.password !== form.confirmPassword) {
            // Simple client-side feedback for now
            window.alert('Passwords do not match');
            return;
        }
        setLoading(true);
        const ok = await onRegister({
            username: form.email,
            email: form.email,
            password: form.password,
            firstName: form.firstName,
            lastName: form.lastName,
            phone: form.phone,
        });
        setLoading(false);
        if (ok) navigate('/login');
    };

    return (
        <Box sx={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'linear-gradient(135deg, #0a1628 0%, #112240 50%, #0d1f3c 100%)', position: 'relative', overflow: 'hidden' }}>
            <Box sx={{ position: 'absolute', top: -100, left: -100, width: 400, height: 400, borderRadius: '50%', background: 'radial-gradient(circle, rgba(0,188,212,0.08) 0%, transparent 70%)' }} />
            <Card sx={{ width: 500, p: 1 }}>
                <CardContent sx={{ p: 4 }}>
                    <Box sx={{ textAlign: 'center', mb: 3 }}>
                        <Functions sx={{ fontSize: 40, color: 'primary.main', mb: 1 }} />
                        <Typography variant="h5" fontWeight={700} color="primary">Create Account</Typography>
                        <Typography variant="body2" color="text.secondary">Join OceanView Resort</Typography>
                    </Box>
                    <form onSubmit={handleSubmit}>
                        <Grid container spacing={2}>
                            <Grid item xs={6}><TextField fullWidth label="First Name" value={form.firstName} onChange={set('firstName')} InputProps={{ startAdornment: <InputAdornment position="start"><Person sx={{ color: 'text.secondary', fontSize: 20 }} /></InputAdornment> }} /></Grid>
                            <Grid item xs={6}><TextField fullWidth label="Last Name" value={form.lastName} onChange={set('lastName')} /></Grid>
                            <Grid item xs={12}><TextField fullWidth label="Email" type="email" value={form.email} onChange={set('email')} InputProps={{ startAdornment: <InputAdornment position="start"><Email sx={{ color: 'text.secondary', fontSize: 20 }} /></InputAdornment> }} /></Grid>
                            <Grid item xs={12}><TextField fullWidth label="Phone" value={form.phone} onChange={set('phone')} InputProps={{ startAdornment: <InputAdornment position="start"><Phone sx={{ color: 'text.secondary', fontSize: 20 }} /></InputAdornment> }} /></Grid>
                            <Grid item xs={6}><TextField fullWidth label="Password" type="password" value={form.password} onChange={set('password')} InputProps={{ startAdornment: <InputAdornment position="start"><Lock sx={{ color: 'text.secondary', fontSize: 20 }} /></InputAdornment> }} /></Grid>
                            <Grid item xs={6}><TextField fullWidth label="Confirm" type="password" value={form.confirmPassword} onChange={set('confirmPassword')} /></Grid>
                        </Grid>
                        <Button fullWidth variant="contained" size="large" type="submit" disabled={loading} sx={{ mt: 3, py: 1.5 }}>
                            {loading ? 'Creating...' : 'Register'}
                        </Button>
                    </form>
                    <Typography variant="body2" color="text.secondary" textAlign="center" mt={2}>
                        Already have an account? <Link to="/login" style={{ color: '#00bcd4', textDecoration: 'none', fontWeight: 600 }}>Sign In</Link>
                    </Typography>
                </CardContent>
            </Card>
        </Box>
    );
}
