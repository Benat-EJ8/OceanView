import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Box, Typography, Card, CardContent, CardActionArea, Grid, List,
  ListItem, ListItemText, Alert, Button, CircularProgress, Chip,
} from '@mui/material';
import BuildIcon from '@mui/icons-material/Build';
import RoomServiceIcon from '@mui/icons-material/RoomService';
import FeedbackIcon from '@mui/icons-material/Feedback';
import BookOnlineIcon from '@mui/icons-material/BookOnline';
import { getGuestMe, getReservations } from '../api';
import { useAuth } from '../context/AuthContext';

const quickActions = [
  { to: '/maintenance', label: 'Maintenance', desc: 'Report an issue', icon: <BuildIcon sx={{ fontSize: 36 }} />, color: '#ed6c02' },
  { to: '/service-requests', label: 'Room Service', desc: 'Towels, meals & more', icon: <RoomServiceIcon sx={{ fontSize: 36 }} />, color: '#1976d2' },
  { to: '/feedback', label: 'Feedback', desc: 'Share your thoughts', icon: <FeedbackIcon sx={{ fontSize: 36 }} />, color: '#2e7d32' },
  { to: '/booking', label: 'Book a Room', desc: 'Make a reservation', icon: <BookOnlineIcon sx={{ fontSize: 36 }} />, color: '#9c27b0' },
];

const statusColor = {
  PENDING_APPROVAL: 'warning', CONFIRMED: 'success', CHECKED_IN: 'info',
  CHECKED_OUT: 'default', CANCELLED: 'error',
};

export function CustomerDashboard() {
  const { user } = useAuth();
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!user?.id) return;
    getGuestMe()
      .then((guest) => getReservations({ guestId: guest.id }))
      .then(setReservations)
      .catch(() => setError('Could not load bookings'))
      .finally(() => setLoading(false));
  }, [user?.id]);

  if (!user) return null;

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 1, fontWeight: 700 }}>My Dashboard</Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>Welcome back, {user.firstName}!</Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}

      <Grid container spacing={2} sx={{ mb: 3 }}>
        {quickActions.map((a) => (
          <Grid item xs={6} sm={3} key={a.to}>
            <Card elevation={2} sx={{ borderRadius: 2, transition: '0.2s', '&:hover': { transform: 'translateY(-4px)', boxShadow: 6 } }}>
              <CardActionArea component={Link} to={a.to} sx={{ p: 2.5, textAlign: 'center' }}>
                <Box sx={{ color: a.color, mb: 1 }}>{a.icon}</Box>
                <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>{a.label}</Typography>
                <Typography variant="caption" color="text.secondary">{a.desc}</Typography>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>

      {loading && <CircularProgress />}
      {!loading && (
        <Card elevation={2} sx={{ borderRadius: 2 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>My Bookings</Typography>
            {reservations.length === 0 ? (
              <Typography color="text.secondary">No bookings yet. <Button component={Link} to="/booking">Make your first booking</Button></Typography>
            ) : (
              <List dense>
                {reservations.map((r) => (
                  <ListItem key={r.id}>
                    <ListItemText
                      primary={
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <Typography variant="body1">#{r.id} · {r.checkInDate} – {r.checkOutDate} · {r.roomNumber || 'Room TBA'}</Typography>
                          <Chip label={r.status?.replace(/_/g, ' ')} color={statusColor[r.status] || 'default'} size="small" />
                        </Box>
                      }
                    />
                  </ListItem>
                ))}
              </List>
            )}
          </CardContent>
        </Card>
      )}
    </Box>
  );
}