import { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Alert,
  CircularProgress,
  Button,
} from '@mui/material';
import { getReservations, approveReservation, cancelReservation } from '../api';

export function ReservationsPage() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = () => {
    setLoading(true);
    setError('');
    getReservations({ branchId: 1 })
      .then(setList)
      .catch((e) => setError(e.message || 'Failed to load reservations'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleApprove = async (id) => {
    try {
      await approveReservation(id);
      load();
    } catch (e) { setError(e.message); }
  };

  const handleCancel = async (id) => {
    const reason = window.prompt('Cancel reason?') || 'Cancelled';
    try {
      await cancelReservation(id, reason);
      load();
    } catch (e) { setError(e.message); }
  };

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 2 }}>Reservations</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
      {loading && <CircularProgress sx={{ my: 2 }} />}
      {!loading && (
        <TableContainer component={Paper}>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Guest</TableCell>
                <TableCell>Check-in</TableCell>
                <TableCell>Check-out</TableCell>
                <TableCell>Room</TableCell>
                <TableCell>Status</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {list.map((r) => (
                <TableRow key={r.id}>
                  <TableCell>{r.id}</TableCell>
                  <TableCell>{r.guestName || '—'}</TableCell>
                  <TableCell>{r.checkInDate}</TableCell>
                  <TableCell>{r.checkOutDate}</TableCell>
                  <TableCell>{r.roomNumber || '—'}</TableCell>
                  <TableCell>{r.status}</TableCell>
                  <TableCell align="right">
                    {r.status === 'PENDING_APPROVAL' && (
                      <>
                        <Button size="small" color="primary" onClick={() => handleApprove(r.id)}>Approve</Button>
                        <Button size="small" onClick={() => handleCancel(r.id)}>Cancel</Button>
                      </>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
      {!loading && list.length === 0 && !error && <Typography color="text.secondary">No reservations.</Typography>}
    </Box>
  );
}
