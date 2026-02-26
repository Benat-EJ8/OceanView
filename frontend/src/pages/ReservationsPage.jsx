import { useState, useEffect } from 'react';
import {
  Box, Card, CardContent, Typography, Table, TableBody, TableCell,
  TableContainer, TableHead, TableRow, Paper, Alert, CircularProgress,
  Button, Chip, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, IconButton, Tooltip,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { getReservations, approveReservation, cancelReservation, updateReservation, deleteReservation } from '../api';
import { useAuth } from '../context/AuthContext';

const statusColor = {
  PENDING_APPROVAL: 'warning',
  CONFIRMED: 'success',
  CHECKED_IN: 'info',
  CHECKED_OUT: 'default',
  CANCELLED: 'error',
  NO_SHOW: 'error',
};

export function ReservationsPage() {
  const { isManager } = useAuth();
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editDialog, setEditDialog] = useState(false);
  const [editForm, setEditForm] = useState({});

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
    try { await approveReservation(id); load(); }
    catch (e) { setError(e.message); }
  };

  const handleCancel = async (id) => {
    const reason = window.prompt('Cancel reason?') || 'Cancelled';
    try { await cancelReservation(id, reason); load(); }
    catch (e) { setError(e.message); }
  };

  const openEdit = (r) => {
    setEditForm({
      id: r.id,
      checkInDate: r.checkInDate,
      checkOutDate: r.checkOutDate,
      adults: r.adults || 1,
      children: r.children || 0,
      specialRequests: r.specialRequests || '',
    });
    setEditDialog(true);
  };

  const handleUpdate = async () => {
    try {
      await updateReservation(editForm.id, editForm);
      setEditDialog(false);
      load();
    } catch (e) { setError(e.message); }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this reservation permanently?')) return;
    try { await deleteReservation(id); load(); }
    catch (e) { setError(e.message); }
  };

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 2, fontWeight: 700 }}>Reservations</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
      {loading && <CircularProgress sx={{ my: 2 }} />}
      {!loading && (
        <TableContainer component={Paper} elevation={2} sx={{ borderRadius: 2 }}>
          <Table size="small">
            <TableHead>
              <TableRow sx={{ bgcolor: 'primary.main' }}>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>ID</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Guest</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Check-in</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Check-out</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Room</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Status</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 600 }} align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {list.map((r) => (
                <TableRow key={r.id} hover>
                  <TableCell>{r.id}</TableCell>
                  <TableCell>{r.guestName || '—'}</TableCell>
                  <TableCell>{r.checkInDate}</TableCell>
                  <TableCell>{r.checkOutDate}</TableCell>
                  <TableCell>{r.roomNumber || '—'}</TableCell>
                  <TableCell>
                    <Chip label={r.status?.replace(/_/g, ' ')} color={statusColor[r.status] || 'default'} size="small" />
                  </TableCell>
                  <TableCell align="right">
                    {r.status === 'PENDING_APPROVAL' && (
                      <>
                        <Button size="small" color="primary" onClick={() => handleApprove(r.id)}>Approve</Button>
                        <Button size="small" onClick={() => handleCancel(r.id)}>Cancel</Button>
                      </>
                    )}
                    {isManager && (
                      <>
                        <Tooltip title="Edit">
                          <IconButton size="small" onClick={() => openEdit(r)}><EditIcon fontSize="small" /></IconButton>
                        </Tooltip>
                        <Tooltip title="Delete">
                          <IconButton size="small" color="error" onClick={() => handleDelete(r.id)}><DeleteIcon fontSize="small" /></IconButton>
                        </Tooltip>
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

      <Dialog open={editDialog} onClose={() => setEditDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 600 }}>Edit Reservation #{editForm.id}</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: '16px !important' }}>
          <TextField label="Check-in" type="date" value={editForm.checkInDate || ''} onChange={(e) => setEditForm({ ...editForm, checkInDate: e.target.value })} InputLabelProps={{ shrink: true }} fullWidth />
          <TextField label="Check-out" type="date" value={editForm.checkOutDate || ''} onChange={(e) => setEditForm({ ...editForm, checkOutDate: e.target.value })} InputLabelProps={{ shrink: true }} fullWidth />
          <TextField label="Adults" type="number" value={editForm.adults || 1} onChange={(e) => setEditForm({ ...editForm, adults: parseInt(e.target.value) || 1 })} fullWidth />
          <TextField label="Children" type="number" value={editForm.children || 0} onChange={(e) => setEditForm({ ...editForm, children: parseInt(e.target.value) || 0 })} fullWidth />
          <TextField label="Special Requests" value={editForm.specialRequests || ''} onChange={(e) => setEditForm({ ...editForm, specialRequests: e.target.value })} fullWidth multiline rows={2} />
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setEditDialog(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleUpdate}>Update</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
