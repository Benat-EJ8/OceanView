import { useState, useEffect } from 'react';
import {
    Box, Typography, Card, CardContent, TextField, Button, Alert,
    Select, MenuItem, FormControl, InputLabel, List, ListItem,
    ListItemText, Chip, CircularProgress, Divider, Rating,
} from '@mui/material';
import BuildIcon from '@mui/icons-material/Build';
import { getMaintenanceRequests, createMaintenanceRequest, getRooms } from '../api';
import { useAuth } from '../context/AuthContext';

const PRIORITIES = ['LOW', 'NORMAL', 'HIGH', 'URGENT'];
const statusColor = { OPEN: 'error', IN_PROGRESS: 'warning', RESOLVED: 'success', CLOSED: 'default' };

export function MaintenanceRequestPage() {
    const { user } = useAuth();
    const [requests, setRequests] = useState([]);
    const [rooms, setRooms] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [form, setForm] = useState({ roomId: '', title: '', description: '', priority: 'NORMAL' });

    const load = () => {
        setLoading(true);
        Promise.all([
            getMaintenanceRequests({ reportedBy: user?.id }),
            getRooms(),
        ])
            .then(([reqs, rms]) => { setRequests(reqs); setRooms(rms); })
            .catch(() => setError('Failed to load data'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { if (user?.id) load(); }, [user?.id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(''); setSuccess('');
        if (!form.roomId || !form.title) { setError('Room and title are required'); return; }
        try {
            await createMaintenanceRequest(form);
            setSuccess('Maintenance request submitted successfully!');
            setForm({ roomId: '', title: '', description: '', priority: 'NORMAL' });
            load();
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <Box>
            <Typography variant="h4" sx={{ mb: 3, fontWeight: 700, display: 'flex', alignItems: 'center', gap: 1 }}>
                <BuildIcon color="primary" /> Maintenance Requests
            </Typography>

            {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}

            <Card elevation={2} sx={{ mb: 3, borderRadius: 2 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>Submit New Request</Typography>
                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <FormControl fullWidth>
                            <InputLabel>Room</InputLabel>
                            <Select value={form.roomId} label="Room" onChange={(e) => setForm({ ...form, roomId: e.target.value })}>
                                {rooms.map((r) => <MenuItem key={r.id} value={r.id}>{r.roomNumber} — {r.categoryName || 'Room'}</MenuItem>)}
                            </Select>
                        </FormControl>
                        <TextField label="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} fullWidth required />
                        <TextField label="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} fullWidth multiline rows={3} />
                        <FormControl fullWidth>
                            <InputLabel>Priority</InputLabel>
                            <Select value={form.priority} label="Priority" onChange={(e) => setForm({ ...form, priority: e.target.value })}>
                                {PRIORITIES.map((p) => <MenuItem key={p} value={p}>{p}</MenuItem>)}
                            </Select>
                        </FormControl>
                        <Button variant="contained" type="submit" sx={{ alignSelf: 'flex-start' }}>Submit Request</Button>
                    </Box>
                </CardContent>
            </Card>

            <Card elevation={2} sx={{ borderRadius: 2 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>My Requests</Typography>
                    {loading && <CircularProgress />}
                    {!loading && requests.length === 0 && (
                        <Typography color="text.secondary">No maintenance requests submitted yet.</Typography>
                    )}
                    {!loading && requests.length > 0 && (
                        <List>
                            {requests.map((r, i) => (
                                <Box key={r.id}>
                                    {i > 0 && <Divider />}
                                    <ListItem sx={{ py: 1.5 }}>
                                        <ListItemText
                                            primary={
                                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                                    <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>{r.title}</Typography>
                                                    <Chip label={r.status} color={statusColor[r.status] || 'default'} size="small" />
                                                    <Chip label={r.priority} variant="outlined" size="small" />
                                                </Box>
                                            }
                                            secondary={`Room ${r.roomId} · ${r.description || 'No description'} · ${r.createdAt ? new Date(r.createdAt).toLocaleDateString() : ''}`}
                                        />
                                    </ListItem>
                                </Box>
                            ))}
                        </List>
                    )}
                </CardContent>
            </Card>
        </Box>
    );
}
