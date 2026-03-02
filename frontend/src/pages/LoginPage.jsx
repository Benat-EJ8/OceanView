import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Box, Card, CardContent, TextField, Button, Typography, InputAdornment, IconButton } from '@mui/material';
import { Visibility, VisibilityOff, Functions, Email, Lock } from '@mui/icons-material';

export default function LoginPage({ onLogin }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPass, setShowPass] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        await onLogin(email, password);
        setLoading(false);
    };

    return (
        <Box sx={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'linear-gradient(135deg, #0a1628 0%, #112240 50%, #0d1f3c 100%)', position: 'relative', overflow: 'hidden' }}>
            {/* Decorative elements */}
            <Box sx={{ position: 'absolute', top: -100, right: -100, width: 400, height: 400, borderRadius: '50%', background: 'radial-gradient(circle, rgba(0,188,212,0.08) 0%, transparent 70%)' }} />
            <Box sx={{ position: 'absolute', bottom: -150, left: -150, width: 500, height: 500, borderRadius: '50%', background: 'radial-gradient(circle, rgba(255,111,0,0.06) 0%, transparent 70%)' }} />

            <Card sx={{ width: 440, p: 1, position: 'relative', zIndex: 1 }}>
                <CardContent sx={{ p: 4 }}>
                    <Box sx={{ textAlign: 'center', mb: 4 }}>
                        <Functions sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
                        <Typography variant="h4" fontWeight={800} sx={{ background: 'linear-gradient(135deg, #00bcd4, #4dd0e1)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
                            OceanView
                        </Typography>
                        <Typography variant="body2" color="text.secondary" mt={0.5}>Resort Management System</Typography>
                    </Box>

                    <form onSubmit={handleSubmit}>
                        <TextField fullWidth label="Email" value={email} onChange={e => setEmail(e.target.value)}
                            sx={{ mb: 2.5 }} InputProps={{ startAdornment: <InputAdornment position="start"><Email sx={{ color: 'text.secondary' }} /></InputAdornment> }} />
                        <TextField fullWidth label="Password" type={showPass ? 'text' : 'password'} value={password} onChange={e => setPassword(e.target.value)}
                            sx={{ mb: 3 }}
                            InputProps={{
                                startAdornment: <InputAdornment position="start"><Lock sx={{ color: 'text.secondary' }} /></InputAdornment>,
                                endAdornment: <InputAdornment position="end"><IconButton onClick={() => setShowPass(!showPass)} edge="end" size="small">{showPass ? <VisibilityOff /> : <Visibility />}</IconButton></InputAdornment>,
                            }} />
                        <Button fullWidth variant="contained" size="large" type="submit" disabled={loading}
                            sx={{ py: 1.5, fontSize: '1rem', boxShadow: '0 8px 32px rgba(0,188,212,0.3)' }}>
                            {loading ? 'Signing in...' : 'Sign In'}
                        </Button>
                    </form>

                    <Typography variant="body2" color="text.secondary" textAlign="center" mt={3}>
                        Don't have an account? <Link to="/register" style={{ color: '#00bcd4', textDecoration: 'none', fontWeight: 600 }}>Register</Link>
                    </Typography>
                </CardContent>
            </Card>
        </Box>
    );
}
