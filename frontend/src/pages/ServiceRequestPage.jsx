import { useState, useEffect } from 'react';
import {
    Box, Typography, Card, CardContent, CardActionArea, Grid,
    TextField, Button, Alert, Chip, List, ListItem, ListItemText,
    CircularProgress, Divider, Dialog, DialogTitle, DialogContent, DialogActions,
} from '@mui/material';
import RoomServiceIcon from '@mui/icons-material/RoomService';
import LocalLaundryServiceIcon from '@mui/icons-material/LocalLaundryService';
import AlarmIcon from '@mui/icons-material/Alarm';
import HotelIcon from '@mui/icons-material/Hotel';
import MoreHorizIcon from '@mui/icons-material/MoreHoriz';
import { getServiceRequests, createServiceRequest } from '../api';
import { useAuth } from '../context/AuthContext';

const QUICK_ACTIONS = [
    { type: 'ROOM_SERVICE', label: 'Room Service', icon: <RoomServiceIcon sx={{ fontSize: 40 }} />, color: '#1976d2', desc: 'Order food & beverages to your room' },
    { type: 'TOWELS', label: 'Fresh Towels', icon: <LocalLaundryServiceIcon sx={{ fontSize: 40 }} />, color: '#2e7d32', desc: 'Request fresh towels and linens' },
    { type: 'WAKE_UP_CALL', label: 'Wake-Up Call', icon: <AlarmIcon sx={{ fontSize: 40 }} />, color: '#ed6c02', desc: 'Schedule a wake-up call' },
    { type: 'EXTRA_BED', label: 'Extra Bed', icon: <HotelIcon sx={{ fontSize: 40 }} />, color: '#9c27b0', desc: 'Request an extra bed' },
    { type: 'OTHER', label: 'Other', icon: <MoreHorizIcon sx={{ fontSize: 40 }} />, color: '#757575', desc: 'Custom request' },
];

const statusColor = { PENDING: 'warning', IN_PROGRESS: 'info', COMPLETED: 'success', CANCELLED: 'error' };

export function ServiceRequestPage() {
    const { user } = useAuth();
    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedType, setSelectedType] = useState('');
    const [description, setDescription] = useState('');

    const load = () => {
        setLoading(true);
        getServiceRequests({ guestId: user?.id })
            .then(setRequests)
            .catch(() => setError('Failed to load requests'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { if (user?.id) load(); }, [user?.id]);

    const openDialog = (type) => {
        setSelectedType(type);
        setDescription('');
        setDialogOpen(true);
    };

    const handleSubmit = async () => {
        setError(''); setSuccess('');
        try {
            await createServiceRequest({ requestType: selectedType, description });
            setSuccess(`${selectedType.replace(/_/g, ' ')} request submitted!`);
            setDialogOpen(false);
            load();
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <Box>
            <Typography variant="h4" sx={{ mb: 3, fontWeight: 700, display: 'flex', alignItems: 'center', gap: 1 }}>
                <RoomServiceIcon color="primary" /> Service Requests
            </Typography>

            {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}

            <Grid container spacing={2} sx={{ mb: 3 }}>
                {QUICK_ACTIONS.map((action) => (
                    <Grid item xs={6} sm={4} md={2.4} key={action.type}>
                        <Card elevation={2} sx={{ borderRadius: 2, transition: '0.2s', '&:hover': { transform: 'translateY(-4px)', boxShadow: 6 } }}>
                            <CardActionArea onClick={() => openDialog(action.type)} sx={{ p: 2, textAlign: 'center' }}>
                                <Box sx={{ color: action.color, mb: 1 }}>{action.icon}</Box>
                                <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>{action.label}</Typography>
                                <Typography variant="caption" color="text.secondary">{action.desc}</Typography>
                            </CardActionArea>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            <Card elevation={2} sx={{ borderRadius: 2 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>My Requests</Typography>
                    {loading && <CircularProgress />}
                    {!loading && requests.length === 0 && (
                        <Typography color="text.secondary">No service requests yet. Use the cards above to make one!</Typography>
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
                                                    <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>{r.requestType?.replace(/_/g, ' ')}</Typography>
                                                    <Chip label={r.status} color={statusColor[r.status] || 'default'} size="small" />
                                                </Box>
                                            }
                                            secondary={`${r.description || 'No details'} · ${r.createdAt ? new Date(r.createdAt).toLocaleDateString() : ''}`}
                                        />
                                    </ListItem>
                                </Box>
                            ))}
                        </List>
                    )}
                </CardContent>
            </Card>

            <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)} maxWidth="sm" fullWidth>
                <DialogTitle sx={{ fontWeight: 600 }}>{selectedType?.replace(/_/g, ' ')} Request</DialogTitle>
                <DialogContent>
                    <TextField
                        label="Additional Details (optional)"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        fullWidth multiline rows={3}
                        sx={{ mt: 1 }}
                    />
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 2 }}>
                    <Button onClick={() => setDialogOpen(false)}>Cancel</Button>
                    <Button variant="contained" onClick={handleSubmit}>Submit</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
}
