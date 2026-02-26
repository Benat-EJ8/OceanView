import { useState, useEffect } from 'react';
import {
    Box, Typography, Card, CardContent, TextField, Button, Alert,
    Select, MenuItem, FormControl, InputLabel, Rating, List, ListItem,
    ListItemText, Chip, CircularProgress, Divider,
} from '@mui/material';
import FeedbackIcon from '@mui/icons-material/Feedback';
import StarIcon from '@mui/icons-material/Star';
import { getFeedback, createFeedback, getGuestMe } from '../api';
import { useAuth } from '../context/AuthContext';

const CATEGORIES = ['COMPLAINT', 'SUGGESTION', 'COMPLIMENT'];
const catColor = { COMPLAINT: 'error', SUGGESTION: 'info', COMPLIMENT: 'success' };

export function FeedbackPage() {
    const { user } = useAuth();
    const [feedbackList, setFeedbackList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [guestId, setGuestId] = useState(null);
    const [form, setForm] = useState({ rating: 4, category: 'COMPLIMENT', comment: '' });

    const load = async () => {
        setLoading(true);
        try {
            const guest = await getGuestMe();
            setGuestId(guest.id);
            const list = await getFeedback({ guestId: guest.id });
            setFeedbackList(list);
        } catch {
            setError('Failed to load feedback');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { if (user?.id) load(); }, [user?.id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(''); setSuccess('');
        if (!form.rating) { setError('Rating is required'); return; }
        try {
            await createFeedback({ ...form, guestId });
            setSuccess('Thank you for your feedback!');
            setForm({ rating: 4, category: 'COMPLIMENT', comment: '' });
            load();
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <Box>
            <Typography variant="h4" sx={{ mb: 3, fontWeight: 700, display: 'flex', alignItems: 'center', gap: 1 }}>
                <FeedbackIcon color="primary" /> Feedback & Complaints
            </Typography>

            {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
            {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}

            <Card elevation={2} sx={{ mb: 3, borderRadius: 2 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>Share Your Experience</Typography>
                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <Box>
                            <Typography variant="body2" sx={{ mb: 0.5, fontWeight: 500 }}>Your Rating</Typography>
                            <Rating
                                value={form.rating}
                                onChange={(_, v) => setForm({ ...form, rating: v })}
                                size="large"
                                emptyIcon={<StarIcon style={{ opacity: 0.3 }} fontSize="inherit" />}
                            />
                        </Box>
                        <FormControl fullWidth>
                            <InputLabel>Category</InputLabel>
                            <Select value={form.category} label="Category" onChange={(e) => setForm({ ...form, category: e.target.value })}>
                                {CATEGORIES.map((c) => <MenuItem key={c} value={c}>{c}</MenuItem>)}
                            </Select>
                        </FormControl>
                        <TextField
                            label="Your Comments"
                            value={form.comment}
                            onChange={(e) => setForm({ ...form, comment: e.target.value })}
                            fullWidth multiline rows={4}
                            placeholder="Tell us about your experience..."
                        />
                        <Button variant="contained" type="submit" sx={{ alignSelf: 'flex-start' }}>Submit Feedback</Button>
                    </Box>
                </CardContent>
            </Card>

            <Card elevation={2} sx={{ borderRadius: 2 }}>
                <CardContent>
                    <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>My Feedback History</Typography>
                    {loading && <CircularProgress />}
                    {!loading && feedbackList.length === 0 && (
                        <Typography color="text.secondary">You haven't submitted any feedback yet.</Typography>
                    )}
                    {!loading && feedbackList.length > 0 && (
                        <List>
                            {feedbackList.map((f, i) => (
                                <Box key={f.id}>
                                    {i > 0 && <Divider />}
                                    <ListItem sx={{ py: 1.5 }}>
                                        <ListItemText
                                            primary={
                                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                                    <Rating value={f.rating} readOnly size="small" />
                                                    <Chip label={f.category} color={catColor[f.category] || 'default'} size="small" />
                                                </Box>
                                            }
                                            secondary={`${f.comment || 'No comment'} · ${f.createdAt ? new Date(f.createdAt).toLocaleDateString() : ''}`}
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
