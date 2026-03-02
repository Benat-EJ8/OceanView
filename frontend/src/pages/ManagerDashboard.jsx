import React, { useState, useEffect, useCallback } from 'react';
import { Box, Typography, Grid, Card, CardContent, Chip, Button, Tab, Tabs, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, AppBar, Toolbar, IconButton, Avatar, Dialog, DialogTitle, DialogContent, DialogActions, FormControl, InputLabel, Select, MenuItem } from '@mui/material';
import { Functions, Logout, Hotel, Receipt, Assessment, MeetingRoom, LocalActivity, CheckCircle, Cancel } from '@mui/icons-material';
import api from '../api';
import { OccupancyChart, CategoryOccupancyChart, RevenueChart, BookingStatusChart, StaffPerformanceChart, ServiceStatsChart } from '../components/ReportCharts';

const StatCard = ({ icon, label, value, color }) => (
    <Card className="stat-card"><CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2, p: 2.5, '&:last-child': { pb: 2.5 } }}>
        <Avatar sx={{ bgcolor: `${color}22`, color, width: 52, height: 52 }}>{icon}</Avatar>
        <Box><Typography variant="body2" color="text.secondary">{label}</Typography><Typography variant="h4" fontWeight={800}>{value}</Typography></Box>
    </CardContent></Card>
);

export default function ManagerDashboard({ user, onLogout, showSnack }) {
    const [tab, setTab] = useState(0);
    const [reservations, setReservations] = useState([]);
    const [pending, setPending] = useState([]);
    const [rooms, setRooms] = useState([]);
    const [categories, setCategories] = useState([]);
    const [reports, setReports] = useState({});
    const [dateRange, setDateRange] = useState({ from: new Date(Date.now() - 30 * 86400000).toISOString().split('T')[0], to: new Date().toISOString().split('T')[0] });
    const [roomDialogOpen, setRoomDialogOpen] = useState(false);
    const [editingRoom, setEditingRoom] = useState(null);
    const [roomForm, setRoomForm] = useState({ roomNumber: '', floor: 1, categoryId: '', viewType: '', branchId: 1, status: 'AVAILABLE' });

    const load = useCallback(async () => {
        try {
            const [res, p, rm, cats] = await Promise.all([
                api.getReservations(),
                api.getPendingReservations(),
                api.getRooms(),
                api.getRoomCategories(),
            ]);
            setReservations(res.data || []);
            setPending(p.data || []);
            setRooms(rm.data || []);
            setCategories(cats.data || []);
        } catch (e) { console.error(e); }
    }, []);

    const loadReports = useCallback(async () => {
        try {
            const [occ, rev, bk, staff, svc] = await Promise.all([
                api.getReport('occupancy', dateRange.from, dateRange.to),
                api.getReport('revenue', dateRange.from, dateRange.to),
                api.getReport('bookings', dateRange.from, dateRange.to),
                api.getReport('staff', dateRange.from, dateRange.to),
                api.getReport('services', dateRange.from, dateRange.to),
            ]);
            setReports({ occupancy: occ.data, revenue: rev.data, bookings: bk.data, staff: staff.data, services: svc.data });
        } catch (e) { console.error(e); }
    }, [dateRange]);

    useEffect(() => { load(); }, [load]);
    useEffect(() => { if (tab === 2) loadReports(); }, [tab, loadReports]);

    const handleApprove = async (id) => {
        try { await api.approveReservation(id); showSnack('Reservation approved!'); load(); } catch (e) { showSnack(e.message, 'error'); }
    };
    const handleCancel = async (id) => {
        try { await api.cancelReservation(id, 'Cancelled by manager'); showSnack('Reservation cancelled'); load(); } catch (e) { showSnack(e.message, 'error'); }
    };

    const statusColor = (s) => ({ AVAILABLE: 'success', OCCUPIED: 'warning', CONFIRMED: 'info', CHECKED_IN: 'success', CANCELLED: 'error', PENDING_APPROVAL: 'warning', CHECKED_OUT: 'default' }[s] || 'default');

    return (
        <Box sx={{ minHeight: '100vh' }}>
            <AppBar position="sticky" sx={{ background: 'linear-gradient(90deg, rgba(10,22,40,0.98), rgba(17,34,64,0.98))', backdropFilter: 'blur(20px)', borderBottom: '1px solid rgba(0,188,212,0.1)' }}>
                <Toolbar>
                    <Functions sx={{ mr: 1.5, color: 'primary.main' }} />
                    <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>OceanView <Chip label="MANAGER" size="small" color="secondary" sx={{ ml: 1 }} /></Typography>
                    <Typography variant="body2" color="text.secondary" sx={{ mr: 2 }}>Welcome, {user.firstName}</Typography>
                    <IconButton onClick={onLogout} color="inherit"><Logout /></IconButton>
                </Toolbar>
            </AppBar>

            <Box sx={{ p: 3 }}>
                <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 3, '& .MuiTab-root': { fontWeight: 600, textTransform: 'none' } }}>
                    <Tab icon={<Hotel />} iconPosition="start" label="Reservations" />
                    <Tab icon={<MeetingRoom />} iconPosition="start" label="Rooms" />
                    <Tab icon={<Receipt />} iconPosition="start" label={`Pending Approval (${pending.length})`} />
                    <Tab icon={<Assessment />} iconPosition="start" label="Reports & Analytics" />
                </Tabs>

                {tab === 0 && (
                    <Box>
                        <Grid container spacing={3} sx={{ mb: 3 }}>
                            <Grid item xs={12} sm={4}><StatCard icon={<Hotel />} label="Total Reservations" value={reservations.length} color="#00bcd4" /></Grid>
                            <Grid item xs={12} sm={4}><StatCard icon={<MeetingRoom />} label="Available Rooms" value={rooms.filter(r => r.status === 'AVAILABLE').length} color="#4caf50" /></Grid>
                            <Grid item xs={12} sm={4}><StatCard icon={<Receipt />} label="Pending Approval" value={pending.length} color="#ffc107" /></Grid>
                        </Grid>
                        <Card>
                            <CardContent>
                                <Typography variant="h6" gutterBottom color="primary">All Reservations</Typography>
                                <TableContainer>
                                    <Table>
                                        <TableHead><TableRow>
                                            <TableCell sx={{ fontWeight: 700 }}>ID</TableCell><TableCell sx={{ fontWeight: 700 }}>Guest</TableCell><TableCell sx={{ fontWeight: 700 }}>Room</TableCell>
                                            <TableCell sx={{ fontWeight: 700 }}>Check-in</TableCell><TableCell sx={{ fontWeight: 700 }}>Check-out</TableCell><TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                                        </TableRow></TableHead>
                                        <TableBody>
                                            {reservations.map(r => (
                                                <TableRow key={r.id} sx={{ '&:hover': { bgcolor: 'rgba(0,188,212,0.04)' } }}>
                                                    <TableCell>#{r.id}</TableCell><TableCell>{r.guestName || `Guest #${r.guestId}`}</TableCell><TableCell>{r.roomNumber || '-'}</TableCell>
                                                    <TableCell>{r.checkInDate}</TableCell><TableCell>{r.checkOutDate}</TableCell>
                                                    <TableCell><Chip label={r.status?.replace('_', ' ')} size="small" color={statusColor(r.status)} /></TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            </CardContent>
                        </Card>
                    </Box>
                )}

                {tab === 1 && (
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                                <Typography variant="h6" gutterBottom color="primary">Room Overview</Typography>
                                <Button variant="contained" onClick={() => { setEditingRoom(null); setRoomForm({ roomNumber: '', floor: 1, categoryId: '', viewType: '', branchId: 1, status: 'AVAILABLE' }); setRoomDialogOpen(true); }}>
                                    Add Room
                                </Button>
                            </Box>
                            <TableContainer>
                                <Table>
                                    <TableHead><TableRow>
                                        <TableCell sx={{ fontWeight: 700 }}>Room</TableCell>
                                        <TableCell sx={{ fontWeight: 700 }}>Floor</TableCell>
                                        <TableCell sx={{ fontWeight: 700 }}>Category</TableCell>
                                        <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                                        <TableCell sx={{ fontWeight: 700 }}>View</TableCell>
                                        <TableCell sx={{ fontWeight: 700 }}>Actions</TableCell>
                                    </TableRow></TableHead>
                                    <TableBody>
                                        {rooms.map(r => (
                                            <TableRow key={r.id}>
                                                <TableCell>{r.roomNumber}</TableCell>
                                                <TableCell>{r.floor}</TableCell>
                                                <TableCell>{r.categoryName}</TableCell>
                                                <TableCell><Chip label={r.status} size="small" color={statusColor(r.status)} /></TableCell>
                                                <TableCell>{r.viewType || '-'}</TableCell>
                                                <TableCell>
                                                    <Button size="small" onClick={() => { setEditingRoom(r); setRoomForm({ roomNumber: r.roomNumber, floor: r.floor, categoryId: r.categoryId, viewType: r.viewType || '', branchId: r.branchId || 1, status: r.status }); setRoomDialogOpen(true); }}>Edit</Button>
                                                    <Button size="small" color="error" onClick={async () => {
                                                        if (!window.confirm('Delete this room?')) return;
                                                        try {
                                                            await api.deleteRoom(r.id);
                                                            showSnack('Room deleted');
                                                            load();
                                                        } catch (e) { showSnack(e.message, 'error'); }
                                                    }}>Delete</Button>
                                                </TableCell>
                                            </TableRow>
                                        ))}
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </CardContent>
                    </Card>
                )}

                {tab === 2 && (
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom color="primary">Pending Approval</Typography>
                            {pending.length === 0 ? <Typography color="text.secondary">No pending reservations</Typography> : (
                                <TableContainer>
                                    <Table>
                                        <TableHead><TableRow>
                                            <TableCell sx={{ fontWeight: 700 }}>Guest</TableCell><TableCell sx={{ fontWeight: 700 }}>Room</TableCell>
                                            <TableCell sx={{ fontWeight: 700 }}>Check-in</TableCell><TableCell sx={{ fontWeight: 700 }}>Check-out</TableCell><TableCell sx={{ fontWeight: 700 }}>Actions</TableCell>
                                        </TableRow></TableHead>
                                        <TableBody>
                                            {pending.map(r => (
                                                <TableRow key={r.id}>
                                                    <TableCell>{r.guestName || `Guest #${r.guestId}`}</TableCell><TableCell>{r.roomNumber || '-'}</TableCell>
                                                    <TableCell>{r.checkInDate}</TableCell><TableCell>{r.checkOutDate}</TableCell>
                                                    <TableCell>
                                                        <Button size="small" variant="contained" color="success" startIcon={<CheckCircle />} onClick={() => handleApprove(r.id)} sx={{ mr: 1 }}>Approve</Button>
                                                        <Button size="small" variant="outlined" color="error" startIcon={<Cancel />} onClick={() => handleCancel(r.id)}>Reject</Button>
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            )}
                        </CardContent>
                    </Card>
                )}

                {tab === 3 && (
                    <Box>
                        <Box sx={{ display: 'flex', gap: 2, mb: 3, alignItems: 'center' }}>
                            <TextField type="date" label="From" value={dateRange.from} onChange={e => setDateRange(d => ({ ...d, from: e.target.value }))} InputLabelProps={{ shrink: true }} size="small" />
                            <TextField type="date" label="To" value={dateRange.to} onChange={e => setDateRange(d => ({ ...d, to: e.target.value }))} InputLabelProps={{ shrink: true }} size="small" />
                            <Button variant="contained" onClick={loadReports}>Refresh</Button>
                        </Box>
                        <Grid container spacing={3}>
                            <Grid item xs={12} md={4}><OccupancyChart data={reports.occupancy} /></Grid>
                            <Grid item xs={12} md={4}><RevenueChart data={reports.revenue} /></Grid>
                            <Grid item xs={12} md={4}><BookingStatusChart data={reports.bookings} /></Grid>
                            <Grid item xs={12} md={6}><CategoryOccupancyChart data={reports.occupancy} /></Grid>
                            <Grid item xs={12} md={6}><StaffPerformanceChart data={reports.staff} /></Grid>
                            <Grid item xs={12}><ServiceStatsChart data={reports.services} /></Grid>
                        </Grid>
                    </Box>
                )}
            </Box>

            <Dialog open={roomDialogOpen} onClose={() => setRoomDialogOpen(false)} maxWidth="sm" fullWidth>
                <DialogTitle>{editingRoom ? 'Edit Room' : 'Add Room'}</DialogTitle>
                <DialogContent>
                    <Grid container spacing={2} sx={{ mt: 0.5 }}>
                        <Grid item xs={6}>
                            <TextField fullWidth label="Room Number" value={roomForm.roomNumber} onChange={e => setRoomForm(f => ({ ...f, roomNumber: e.target.value }))} />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField fullWidth type="number" label="Floor" value={roomForm.floor} onChange={e => setRoomForm(f => ({ ...f, floor: parseInt(e.target.value, 10) || 1 }))} />
                        </Grid>
                        <Grid item xs={12}>
                            <FormControl fullWidth>
                                <InputLabel>Category</InputLabel>
                                <Select
                                    label="Category"
                                    value={roomForm.categoryId || ''}
                                    onChange={e => setRoomForm(f => ({ ...f, categoryId: Number(e.target.value) }))}
                                >
                                    {categories.map(c => (
                                        <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12}>
                            <TextField fullWidth label="View Type" value={roomForm.viewType} onChange={e => setRoomForm(f => ({ ...f, viewType: e.target.value }))} />
                        </Grid>
                    </Grid>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setRoomDialogOpen(false)}>Cancel</Button>
                    <Button variant="contained" onClick={async () => {
                        try {
                            if (editingRoom) {
                                await api.updateRoom(editingRoom.id, roomForm);
                                showSnack('Room updated');
                            } else {
                                await api.createRoom(roomForm);
                                showSnack('Room created');
                            }
                            setRoomDialogOpen(false);
                            load();
                        } catch (e) {
                            showSnack(e.message, 'error');
                        }
                    }}>{editingRoom ? 'Save' : 'Create'}</Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
}
