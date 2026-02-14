import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Box, Typography, Card, CardContent, List, ListItem, ListItemText, Alert, Button, CircularProgress } from '@mui/material';
import { getGuestMe, getReservations } from '../api';
import { useAuth } from '../context/AuthContext';

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
      <Typography variant="h4" sx={{ mb: 2 }}>My Dashboard</Typography>
      <Typography color="text.secondary" sx={{ mb: 2 }}>Welcome, {user.firstName}. <Button component={Link} to="/booking" size="small">Book a room</Button></Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
      {loading && <CircularProgress />}
      {!loading && (
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>My Bookings</Typography>
            {reservations.length === 0 ? (
              <Typography color="text.secondary">No bookings yet. <Button component={Link} to="/booking">Make your first booking</Button></Typography>
            ) : (
              <List dense>
                {reservations.map((r) => (
                  <ListItem key={r.id}>
                    <ListItemText
                      primary={`#${r.id} · ${r.checkInDate} – ${r.checkOutDate} · ${r.roomNumber || 'Room TBA'}`}
                      secondary={r.status}
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