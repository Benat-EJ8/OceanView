import { useState, useEffect } from 'react';
import {
    Box, Typography, Card, CardContent, List, ListItem, ListItemText,
    ListItemIcon, IconButton, Chip, CircularProgress, Alert, Button, Divider,
} from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import MarkEmailReadIcon from '@mui/icons-material/MarkEmailRead';
import BuildIcon from '@mui/icons-material/Build';
import RoomServiceIcon from '@mui/icons-material/RoomService';
import InfoIcon from '@mui/icons-material/Info';
import { getNotifications, markNotificationRead } from '../api';
import { useAuth } from '../context/AuthContext';

const typeIcon = {
    MAINTENANCE: <BuildIcon color="warning" />,
    SERVICE_REQUEST: <RoomServiceIcon color="primary" />,
};

export function NotificationsPage() {
    const { user } = useAuth();
    const [notifications, setNotifications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const load = () => {
        setLoading(true);
        getNotifications()
            .then(setNotifications)
            .catch(() => setError('Failed to load notifications'))
            .finally(() => setLoading(false));
    };

    useEffect(() => { if (user?.id) load(); }, [user?.id]);

    const handleMarkRead = async (id) => {
        try {
            await markNotificationRead(id);
            load();
        } catch (err) {
            setError(err.message);
        }
    };

    const unread = notifications.filter((n) => !n.read);
    const read = notifications.filter((n) => n.read);

    return (
        <Box>
            <Typography variant="h4" sx={{ mb: 3, fontWeight: 700, display: 'flex', alignItems: 'center', gap: 1 }}>
                <NotificationsIcon color="primary" /> Notifications
                {unread.length > 0 && <Chip label={`${unread.length} new`} color="error" size="small" />}
            </Typography>

            {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
            {loading && <CircularProgress />}

            {!loading && notifications.length === 0 && (
                <Card elevation={2} sx={{ borderRadius: 2 }}>
                    <CardContent>
                        <Typography color="text.secondary" sx={{ textAlign: 'center', py: 3 }}>
                            No notifications yet.
                        </Typography>
                    </CardContent>
                </Card>
            )}

            {!loading && unread.length > 0 && (
                <Card elevation={2} sx={{ mb: 3, borderRadius: 2, border: '1px solid', borderColor: 'primary.light' }}>
                    <CardContent>
                        <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>New</Typography>
                        <List>
                            {unread.map((n, i) => (
                                <Box key={n.id}>
                                    {i > 0 && <Divider />}
                                    <ListItem
                                        sx={{ bgcolor: 'action.hover', borderRadius: 1, mb: 0.5 }}
                                        secondaryAction={
                                            <IconButton edge="end" onClick={() => handleMarkRead(n.id)} title="Mark as read">
                                                <MarkEmailReadIcon />
                                            </IconButton>
                                        }
                                    >
                                        <ListItemIcon>{typeIcon[n.type] || <InfoIcon color="action" />}</ListItemIcon>
                                        <ListItemText
                                            primary={<Typography variant="subtitle1" sx={{ fontWeight: 600 }}>{n.title}</Typography>}
                                            secondary={`${n.body || ''} · ${n.createdAt ? new Date(n.createdAt).toLocaleString() : ''}`}
                                        />
                                    </ListItem>
                                </Box>
                            ))}
                        </List>
                    </CardContent>
                </Card>
            )}

            {!loading && read.length > 0 && (
                <Card elevation={1} sx={{ borderRadius: 2 }}>
                    <CardContent>
                        <Typography variant="h6" gutterBottom sx={{ fontWeight: 600, color: 'text.secondary' }}>Earlier</Typography>
                        <List>
                            {read.map((n, i) => (
                                <Box key={n.id}>
                                    {i > 0 && <Divider />}
                                    <ListItem>
                                        <ListItemIcon>{typeIcon[n.type] || <InfoIcon color="disabled" />}</ListItemIcon>
                                        <ListItemText
                                            primary={n.title}
                                            secondary={`${n.body || ''} · ${n.createdAt ? new Date(n.createdAt).toLocaleString() : ''}`}
                                            sx={{ opacity: 0.7 }}
                                        />
                                    </ListItem>
                                </Box>
                            ))}
                        </List>
                    </CardContent>
                </Card>
            )}
        </Box>
    );
}
