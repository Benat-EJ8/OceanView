import { useState, useEffect } from 'react';
import {
    Box, Typography, Card, CardContent, Table, TableBody, TableCell,
    TableContainer, TableHead, TableRow, Paper, Button, Dialog,
    DialogTitle, DialogContent, DialogActions, TextField, Select,
    MenuItem, FormControl, InputLabel, IconButton, Alert, Chip,
    CircularProgress, Tooltip,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { getRooms, getRoomCategories, createRoom, updateRoom, deleteRoom } from '../api';

const VIEW_TYPES = ['OCEAN', 'GARDEN', 'POOL', 'CITY'];
const STATUS_LIST = ['AVAILABLE', 'OCCUPIED', 'MAINTENANCE', 'CLEANING', 'OUT_OF_ORDER'];

const statusColor = {
    AVAILABLE: 'success',
    OCCUPIED: 'info',
    MAINTENANCE: 'warning',
    CLEANING: 'secondary',
    OUT_OF_ORDER: 'error',
};

export function RoomManagement() {
    const [rooms, setRooms] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [dialogOpen, setDialogOpen] = useState(false);
    const [editing, setEditing] = useState(null);
    const [form, setForm] = useState({ roomNumber: '', floor: 1, categoryId: '', status: 'AVAILABLE', viewType: 'OCEAN', branchId: 1 });

    const load = () => {
        setLoading(true);
        Promise.all([getRooms(), getRoomCategories()])
            .then(([r, c]) => { setRooms(r); setCategories(c); })
            .catch((e) => setError(e.message))
            .finally(() => setLoading(false));
    };

    useEffect(() => { load(); }, []);

    const openAdd = () => {
        setEditing(null);
        setForm({ roomNumber: '', floor: 1, categoryId: categories[0]?.id || '', status: 'AVAILABLE', viewType: 'OCEAN', branchId: 1 });
        setDialogOpen(true);
    };

    const openEdit = (room) => {
        setEditing(room);
        setForm({
            roomNumber: room.roomNumber,
            floor: room.floor,
            categoryId: room.categoryId,
            status: room.status,
            viewType: room.viewType || 'OCEAN',
            branchId: room.branchId || 1,
        });
        setDialogOpen(true);
    };

    const handleSave = async () => {
        try {
            if (editing) {
                await updateRoom(editing.id, form);
            } else {
                await createRoom(form);
            }
            setDialogOpen(false);
            load();
        } catch (e) {
            setError(e.message);
        }
    };

    const handleDelete = async (id) => {
        if (!confirm('Are you sure you want to delete this room?')) return;
        try {
            await deleteRoom(id);
            load();
        } catch (e) {
            setError(e.message);
        }
    };

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4" sx={{ fontWeight: 700 }}>Room Management</Typography>
                <Button variant="contained" startIcon={<AddIcon />} onClick={openAdd}>Add Room</Button>
            </Box>

            {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
            {loading && <CircularProgress />}

            {!loading && (
                <TableContainer component={Paper} elevation={2} sx={{ borderRadius: 2 }}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ bgcolor: 'primary.main' }}>
                                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Room #</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Floor</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Category</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Status</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 600 }}>View</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 600 }}>Price</TableCell>
                                <TableCell sx={{ color: 'white', fontWeight: 600 }} align="right">Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rooms.map((room) => (
                                <TableRow key={room.id} hover>
                                    <TableCell sx={{ fontWeight: 600 }}>{room.roomNumber}</TableCell>
                                    <TableCell>{room.floor}</TableCell>
                                    <TableCell>{room.categoryName || room.categoryId}</TableCell>
                                    <TableCell>
                                        <Chip label={room.status} color={statusColor[room.status] || 'default'} size="small" />
                                    </TableCell>
                                    <TableCell>{room.viewType}</TableCell>
                                    <TableCell>{room.basePrice ? `LKR ${Number(room.basePrice).toLocaleString()}` : '—'}</TableCell>
                                    <TableCell align="right">
                                        <Tooltip title="Edit">
                                            <IconButton size="small" onClick={() => openEdit(room)}><EditIcon fontSize="small" /></IconButton>
                                        </Tooltip>
                                        <Tooltip title="Delete">
                                            <IconButton size="small" color="error" onClick={() => handleDelete(room.id)}><DeleteIcon fontSize="small" /></IconButton>
                                        </Tooltip>
                                    </TableCell>
                                </TableRow>
                            ))}
                            {rooms.length === 0 && (
                                <TableRow><TableCell colSpan={7} align="center">No rooms found</TableCell></TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
                <DialogTitle sx={{ fontWeight: 600 }}>{editing ? 'Edit Room' : 'Add New Room'}</DialogTitle>
                <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: '16px !important' }}>
                    <TextField label="Room Number" value={form.roomNumber} onChange={(e) => setForm({ ...form, roomNumber: e.target.value })} fullWidth />
                    <TextField label="Floor" type="number" value={form.floor} onChange={(e) => setForm({ ...form, floor: parseInt(e.target.value) || 1 })} fullWidth />
                    <FormControl fullWidth>
                        <InputLabel>Category</InputLabel>
                        <Select value={form.categoryId} label="Category" onChange={(e) => setForm({ ...form, categoryId: e.target.value })}>
                            {categories.map((c) => <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>)}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth>
                        <InputLabel>Status</InputLabel>
                        <Select value={form.status} label="Status" onChange={(e) => setForm({ ...form, status: e.target.value })}>
                            {STATUS_LIST.map((s) => <MenuItem key={s} value={s}>{s}</MenuItem>)}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth>
                        <InputLabel>View Type</InputLabel>
                        <Select value={form.viewType} label="View Type" onChange={(e) => setForm({ ...form, viewType: e.target.value })}>
                            {VIEW_TYPES.map((v) => <MenuItem key={v} value={v}>{v}</MenuItem>)}
                        </Select>
                    </FormControl>
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 2 }}>
                    <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
                    <Button variant="contained" onClick={handleSave}>{editing ? 'Update' : 'Create'}</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
}
