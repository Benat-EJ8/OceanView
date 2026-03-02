import React, { useEffect, useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import {
    Box,
    Typography,
    Button,
    Grid,
    Card,
    CardContent,
    Chip,
    Stack,
    Divider,
} from '@mui/material';
import { Functions, LocalOffer, ArrowForward, Login, PersonAdd } from '@mui/icons-material';
import api from '../api';

export default function WelcomePage({ user }) {
    const navigate = useNavigate();
    const [promotions, setPromotions] = useState([]);

    useEffect(() => {
        let mounted = true;
        api.getPromotions()
            .then(r => {
                if (mounted) setPromotions(r.data || []);
            })
            .catch(() => {
                if (mounted) setPromotions([]);
            });
        return () => { mounted = false; };
    }, []);

    const goToDashboard = () => navigate('/dashboard');

    return (
        <Box
            sx={{
                minHeight: '100vh',
                backgroundImage:
                    'linear-gradient(180deg, rgba(3,7,18,0.65), rgba(3,7,18,0.96)), url(/images/hero-4k.jpg)',
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat',
                color: '#fff',
                display: 'flex',
                flexDirection: 'column',
            }}
        >
            <Box
                component="header"
                sx={{
                    px: 4,
                    py: 2.5,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                }}
            >
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                    <Functions sx={{ fontSize: 32, color: 'primary.main' }} />
                    <Box>
                        <Typography
                            variant="h5"
                            fontWeight={800}
                            sx={{
                                letterSpacing: 1,
                                background: 'linear-gradient(135deg, #00bcd4, #4dd0e1)',
                                WebkitBackgroundClip: 'text',
                                WebkitTextFillColor: 'transparent',
                            }}
                        >
                            OceanView
                        </Typography>
                        <Typography variant="caption" sx={{ color: 'rgba(226,232,240,0.72)' }}>
                            Resort Management System
                        </Typography>
                    </Box>
                </Box>

                <Stack direction="row" spacing={1.5}>
                    {user ? (
                        <Button
                            variant="contained"
                            color="primary"
                            endIcon={<ArrowForward />}
                            onClick={goToDashboard}
                        >
                            Go to dashboard
                        </Button>
                    ) : (
                        <>
                            <Button
                                variant="outlined"
                                color="inherit"
                                startIcon={<Login />}
                                component={RouterLink}
                                to="/login"
                            >
                                Sign in
                            </Button>
                            <Button
                                variant="contained"
                                color="primary"
                                startIcon={<PersonAdd />}
                                component={RouterLink}
                                to="/register"
                                sx={{ boxShadow: '0 10px 40px rgba(0,188,212,0.4)' }}
                            >
                                Register
                            </Button>
                        </>
                    )}
                </Stack>
            </Box>

            <Box sx={{ flex: 1, px: 4, pb: 6, pt: 2 }}>
                <Grid container spacing={4} alignItems="center">
                    <Grid item xs={12} md={6}>
                        <Typography
                            variant="h3"
                            fontWeight={800}
                            sx={{
                                mb: 2,
                                lineHeight: 1.1,
                                maxWidth: 520,
                            }}
                        >
                            Welcome to your oceanfront
                            <Box component="span" sx={{ color: 'primary.main' }}> stay experience.</Box>
                        </Typography>
                        <Typography
                            variant="body1"
                            sx={{ mb: 3, maxWidth: 520, color: 'rgba(226,232,240,0.78)' }}
                        >
                            Discover curated offers and seasonal promotions created by OceanView managers to
                            make every guest&apos;s stay unforgettable.
                        </Typography>

                        <Stack direction="row" spacing={2} sx={{ mb: 4, flexWrap: 'wrap' }}>
                            {!user && (
                                <Button
                                    variant="contained"
                                    size="large"
                                    color="primary"
                                    endIcon={<ArrowForward />}
                                    onClick={() => navigate('/login')}
                                >
                                    Start booking
                                </Button>
                            )}
                            <Button
                                variant="outlined"
                                size="large"
                                color="inherit"
                                onClick={() => {
                                    const el = document.getElementById('offers-section');
                                    if (el) el.scrollIntoView({ behavior: 'smooth' });
                                }}
                            >
                                View current offers
                            </Button>
                        </Stack>

                        <Stack direction="row" spacing={2}>
                            <Chip label="Beachfront resort" color="primary" variant="outlined" />
                            <Chip label="Smart reservations" color="primary" variant="outlined" />
                            <Chip label="Manager-curated promotions" color="primary" variant="outlined" />
                        </Stack>
                    </Grid>

                    <Grid item xs={12} md={6}>
                        <Box
                            id="offers-section"
                            sx={{
                                background: 'rgba(15,23,42,0.9)',
                                borderRadius: 4,
                                border: '1px solid rgba(148,163,184,0.25)',
                                p: 3,
                                boxShadow: '0 22px 40px rgba(15,23,42,0.65)',
                            }}
                        >
                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                <LocalOffer sx={{ color: 'primary.main', mr: 1 }} />
                                <Typography variant="h6" fontWeight={700}>
                                    Featured promotions
                                </Typography>
                                <Chip
                                    label={promotions.length > 0 ? `${promotions.length} active` : 'No active offers'}
                                    size="small"
                                    sx={{ ml: 1.5, bgcolor: 'rgba(56,189,248,0.12)', color: '#7dd3fc' }}
                                />
                            </Box>

                            {promotions.length === 0 && (
                                <Typography variant="body2" sx={{ color: 'rgba(148,163,184,0.9)' }}>
                                    Managers have not published any public promotions yet. Please check back soon for
                                    seasonal packages and special discounts.
                                </Typography>
                            )}

                            <Stack spacing={2.5}>
                                {promotions.map(promo => (
                                    <Card
                                        key={promo.id}
                                        variant="outlined"
                                        sx={{
                                            background: 'linear-gradient(135deg, #020617, #020617)',
                                            borderRadius: 3,
                                            borderColor: 'rgba(148,163,184,0.3)',
                                        }}
                                    >
                                        <CardContent sx={{ pb: 2.5 }}>
                                            <Box
                                                sx={{
                                                    display: 'flex',
                                                    justifyContent: 'space-between',
                                                    alignItems: 'flex-start',
                                                    mb: 1,
                                                }}
                                            >
                                                <Box>
                                                    <Typography variant="subtitle2" sx={{ color: 'primary.main', mb: 0.5 }}>
                                                        {promo.code}
                                                    </Typography>
                                                    <Typography variant="h6" fontWeight={700}>
                                                        {promo.name}
                                                    </Typography>
                                                </Box>
                                                <Chip
                                                    label={
                                                        promo.discountType === 'PERCENT'
                                                            ? `${promo.discountValue}% off`
                                                            : `LKR ${Number(promo.discountValue).toLocaleString()} off`
                                                    }
                                                    color="primary"
                                                    size="small"
                                                />
                                            </Box>

                                            {promo.description && (
                                                <Typography
                                                    variant="body2"
                                                    sx={{ mb: 1, color: 'rgba(148,163,184,0.95)' }}
                                                >
                                                    {promo.description}
                                                </Typography>
                                            )}

                                            <Stack
                                                direction="row"
                                                spacing={2}
                                                sx={{ fontSize: 12, color: 'rgba(148,163,184,0.9)' }}
                                            >
                                                <Typography variant="caption">
                                                    Valid {promo.validFrom} – {promo.validTo}
                                                </Typography>
                                                {promo.minStayNights && (
                                                    <Typography variant="caption">
                                                        Min stay: {promo.minStayNights} night
                                                        {promo.minStayNights > 1 ? 's' : ''}
                                                    </Typography>
                                                )}
                                            </Stack>
                                        </CardContent>
                                    </Card>
                                ))}
                            </Stack>

                            <Divider sx={{ my: 2.5, borderColor: 'rgba(148,163,184,0.3)' }} />

                            <Typography variant="caption" sx={{ color: 'rgba(148,163,184,0.85)' }}>
                                These promotions are managed by hotel managers and may be subject to blackout dates and
                                availability at check‑in.
                            </Typography>
                        </Box>
                    </Grid>
                </Grid>
            </Box>
        </Box>
    );
}

