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
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  IconButton,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { getUsers, updateUser, deleteUser, createUser } from '../api';

const ROLES = [
  { value: 3, label: 'Receptionist' },
  { value: 2, label: 'Manager' },
];

export function StaffManagement() {
  const [users, setUsers] = useState([]);
  const [role, setRole] = useState('RECEPTIONIST');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [openDialog, setOpenDialog] = useState(false);
  const [openAdd, setOpenAdd] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ username: '', email: '', firstName: '', lastName: '', phone: '', roleId: 3, branchId: 1 });
  const [newPassword, setNewPassword] = useState('');
  const [submitLoading, setSubmitLoading] = useState(false);

  const load = () => {
    setLoading(true);
    setError('');
    getUsers(role)
      .then(setUsers)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [role]);

  const handleEdit = (u) => {
    setEditing(u);
    setForm({ username: u.username, email: u.email, firstName: u.firstName, lastName: u.lastName, phone: u.phone || '', roleId: u.roleId, branchId: u.branchId || 1 });
    setOpenDialog(true);
  };

  const handleSaveEdit = async () => {
    setSubmitLoading(true);
    try {
      await updateUser(editing.id, { ...form, id: editing.id });
      setOpenDialog(false);
      setEditing(null);
      load();
    } catch (e) {
      setError(e.message);
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Deactivate this user?')) return;
    try {
      await deleteUser(id);
      load();
    } catch (e) {
      setError(e.message);
    }
  };

  const handleAdd = () => {
    setForm({ username: '', email: '', firstName: '', lastName: '', phone: '', roleId: 2, branchId: 1 });
    setNewPassword('');
    setOpenAdd(true);
  };

  const handleSaveAdd = async () => {
    if (!form.username || !form.email || !newPassword) {
      setError('Username, email and password required');
      return;
    }
    setSubmitLoading(true);
    setError('');
    try {
      await createUser({ ...form, branchId: 1 }, newPassword);
      setOpenAdd(false);
      load();
    } catch (e) {
      setError(e.message);
    } finally {
      setSubmitLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 2 }}>Staff management</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 2 }}>
            <FormControl size="small" sx={{ minWidth: 180 }}>
              <InputLabel>Role</InputLabel>
              <Select value={role} label="Role" onChange={(e) => setRole(e.target.value)}>
                <MenuItem value="RECEPTIONIST">Receptionists</MenuItem>
                <MenuItem value="MANAGER">Managers</MenuItem>
                <MenuItem value="">All staff</MenuItem>
              </Select>
            </FormControl>
            <Button variant="contained" startIcon={<AddIcon />} onClick={handleAdd}>Add staff</Button>
          </Box>
        </CardContent>
      </Card>
      {loading && <CircularProgress />}
      {!loading && (
        <TableContainer component={Paper}>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Name</TableCell>
                <TableCell>Username</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Role</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {users.map((u) => (
                <TableRow key={u.id}>
                  <TableCell>{u.firstName} {u.lastName}</TableCell>
                  <TableCell>{u.username}</TableCell>
                  <TableCell>{u.email}</TableCell>
                  <TableCell>{u.roleCode}</TableCell>
                  <TableCell align="right">
                    <IconButton size="small" onClick={() => handleEdit(u)}><EditIcon /></IconButton>
                    <IconButton size="small" color="error" onClick={() => handleDelete(u.id)}><DeleteIcon /></IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
      {!loading && users.length === 0 && <Typography color="text.secondary">No staff found.</Typography>}

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Edit user</DialogTitle>
        <DialogContent>
          <TextField fullWidth label="First name" value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} margin="dense" />
          <TextField fullWidth label="Last name" value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} margin="dense" />
          <TextField fullWidth label="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} margin="dense" />
          <TextField fullWidth label="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} margin="dense" />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleSaveEdit} disabled={submitLoading}>Save</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={openAdd} onClose={() => setOpenAdd(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add staff</DialogTitle>
        <DialogContent>
          <TextField fullWidth label="Username" value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} margin="dense" required />
          <TextField fullWidth label="Password" type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} margin="dense" required />
          <TextField fullWidth label="First name" value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} margin="dense" />
          <TextField fullWidth label="Last name" value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} margin="dense" />
          <TextField fullWidth label="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} margin="dense" required />
          <FormControl fullWidth margin="dense">
            <InputLabel>Role</InputLabel>
            <Select value={form.roleId} label="Role" onChange={(e) => setForm({ ...form, roleId: e.target.value })}>
              {ROLES.map((r) => (
                <MenuItem key={r.value} value={r.value}>{r.label}</MenuItem>
              ))}
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenAdd(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleSaveAdd} disabled={submitLoading || !form.username || !form.email || !newPassword}>Add</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}