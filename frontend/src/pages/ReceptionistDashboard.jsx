import { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  List,
  ListItem,
  ListItemText,
  Alert,
  CircularProgress,
} from '@mui/material';
import { getReservations, approveReservation, cancelReservation } from '../api';

export function ReceptionistDashboard() {
  const [reservations, setReservations] = useState([]);
  const [pending, setPending] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pendingLoading, setPendingLoading] = useState(true);
  const [error, setError] = useState('');
  const [pendingError, setPendingError] = useState('');

  const loadAll = () => {
    setError('');
    getReservations({ branchId: 1 })
      .then(setReservations)
      .catch((e) => setError(e.message || 'Failed to load reservations'));
  };

  const loadPending = () => {
    setPendingError('');
    setPendingLoading(true);
    getReservations({ pending: 'true' })
      .then(setPending)
      .catch((e) => setPendingError(e.message || 'Failed to load pending'))
      .finally(() => setPendingLoading(false));
  };

  useEffect(() => {
    loadAll();
  }, []);

  useEffect(() => {
    loadPending();
  }, []);

  const handleApprove = async (id) => {
    try {
      await approveReservation(id);
      loadPending();
      loadAll();
    } catch (e) {
      setPendingError(e.message);
    }
  };

  const handleCancel = async (id) => {
    const reason = window.prompt('Cancel reason?') || 'Cancelled';
    try {
      await cancelReservation(id, reason);
      loadPending();
      loadAll();
    } catch (e) {
      setPendingError(e.message);
    }
  };

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 2 }}>Reception</Typography>
      {(error || pendingError) && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => { setError(''); setPendingError(''); }}>
          {error || pendingError}
        </Alert>
      )}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>Pending approval</Typography>
          {pendingLoading && <CircularProgress size={24} />}
          {!pendingLoading && pendingError && <Typography color="error">{pendingError}</Typography>}
          {!pendingLoading && !pendingError && pending.length === 0 && <Typography color="text.secondary">No pending bookings.</Typography>}
          <List dense>
            {pending.map((r) => (
              <ListItem
                key={r.id}
                secondaryAction={
                  <>
                    <Button size="small" color="primary" onClick={() => handleApprove(r.id)} sx={{ mr: 1 }}>Approve</Button>
                    <Button size="small" onClick={() => handleCancel(r.id)}>Cancel</Button>
                  </>
                }
              >
                <ListItemText primary={`#${r.id} ${r.guestName || 'Guest'}`} secondary={`${r.checkInDate} – ${r.checkOutDate}`} />
              </ListItem>
            ))}
          </List>
        </CardContent>
      </Card>
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>Recent reservations</Typography>
          {error && <Typography color="error">{error}</Typography>}
          <List dense>
            {reservations.slice(0, 15).map((r) => (
              <ListItem key={r.id}>
                <ListItemText
                  primary={`#${r.id} ${r.guestName || 'Guest'} · ${r.checkInDate} – ${r.checkOutDate}`}
                  secondary={r.status}
                />
              </ListItem>
            ))}
          </List>
        </CardContent>
      </Card>
    </Box>
  );
}
