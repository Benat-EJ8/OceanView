import React, { useState, useEffect, useCallback } from 'react';
import { Box, Typography, Grid, Card, CardContent, Chip, Button, Tab, Tabs, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Dialog, DialogTitle, DialogContent, DialogActions, TextField, Select, MenuItem, FormControl, InputLabel, AppBar, Toolbar, IconButton, Avatar } from '@mui/material';
import { Functions, Logout, Hotel, Receipt, FlightTakeoff, FlightLand, MeetingRoom, CheckCircle, Cancel, Add } from '@mui/icons-material';
import api from '../api';

const StatCard = ({ icon, label, value, color }) => (
    <Card className="stat-card"><CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2, p: 2.5, '&:last-child': { pb: 2.5 } }}>
        <Avatar sx={{ bgcolor: `${color}22`, color, width: 52, height: 52 }}>{icon}</Avatar>
        <Box><Typography variant="body2" color="text.secondary">{label}</Typography><Typography variant="h4" fontWeight={800}>{value}</Typography></Box>
    </CardContent></Card>
);

export default function ReceptionistDashboard({ user, onLogout, showSnack }) {
    const [tab, setTab] = useState(0);
    const [reservations, setReservations] = useState([]);
    const [pending, setPending] = useState([]);
    const [rooms, setRooms] = useState([]);
    const [categories, setCategories] = useState([]);
    const [bookDlg, setBookDlg] = useState(false);
    const [bookForm, setBookForm] = useState({ guestId: '', roomId: '', checkInDate: '', checkOutDate: '', adults: 1, children: 0 });
    const [invoiceDlg, setInvoiceDlg] = useState(false);
    const [invoice, setInvoice] = useState(null);

    const load = useCallback(async () => {
        try {
            const [res, p, rm, cat] = await Promise.all([api.getReservations(), api.getPendingReservations(), api.getRooms(), api.getRoomCategories()]);
            setReservations(res.data || []);
            setPending(p.data || []);
            setRooms(rm.data || []);
            setCategories(cat.data || []);
        } catch (e) { console.error(e); }
    }, []);

    useEffect(() => { load(); }, [load]);

    const handleApprove = async (id) => {
        try { await api.approveReservation(id); showSnack('Reservation approved!'); load(); } catch (e) { showSnack(e.message, 'error'); }
    };

    const handleCheckIn = async (id) => {
        try { await api.checkIn(id); showSnack('Guest checked in successfully!'); load(); } catch (e) { showSnack(e.message, 'error'); }
    };

    const handleCheckOut = async (id) => {
        try {
            await api.checkOut(id);
            // Generate invoice on checkout
            try {
                const inv = await api.generateInvoice(id);
                setInvoice(inv.data);
                setInvoiceDlg(true);
            } catch (e) { /* invoice generation might fail if no room, that's ok */ }
            showSnack('Guest checked out!');
            load();
        } catch (e) { showSnack(e.message, 'error'); }
    };

    const handleInstantBook = async () => {
        try {
            await api.instantBook(bookForm);
            showSnack('Room booked and guest checked in!');
            setBookDlg(false);
            setBookForm({ guestId: '', roomId: '', checkInDate: '', checkOutDate: '', adults: 1, children: 0 });
            load();
        } catch (e) { showSnack(e.message, 'error'); }
    };

    const handleReservation = async () => {
        try {
            await api.createReservation(bookForm);
            showSnack('Reservation created (pending approval)');
            setBookDlg(false);
            setBookForm({ guestId: '', roomId: '', checkInDate: '', checkOutDate: '', adults: 1, children: 0 });
            load();
        } catch (e) { showSnack(e.message, 'error'); }
    };

    const statusColor = (s) => ({ AVAILABLE: 'success', OCCUPIED: 'warning', CONFIRMED: 'info', CHECKED_IN: 'success', CANCELLED: 'error', PENDING_APPROVAL: 'warning' }[s] || 'default');
    const availRooms = rooms.filter(r => r.status === 'AVAILABLE');

    return (
        <Box sx={{ minHeight: '100vh' }}>
            <AppBar position="sticky" sx={{ background: 'linear-gradient(90deg, rgba(10,22,40,0.98), rgba(17,34,64,0.98))', backdropFilter: 'blur(20px)', borderBottom: '1px solid rgba(0,188,212,0.1)' }}>
                <Toolbar>
                    <Functions sx={{ mr: 1.5, color: 'primary.main' }} />
                    <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>OceanView <Chip label="RECEPTIONIST" size="small" sx={{ ml: 1, bgcolor: '#4caf5022', color: '#4caf50' }} /></Typography>
                    <Button variant="contained" startIcon={<Add />} onClick={() => setBookDlg(true)} sx={{ mr: 2 }}>New Booking</Button>
                    <Typography variant="body2" color="text.secondary" sx={{ mr: 2 }}>{user.firstName}</Typography>
                    <IconButton onClick={onLogout} color="inherit"><Logout /></IconButton>
                </Toolbar>
            </AppBar>

            <Box sx={{ p: 3 }}>
                <Grid container spacing={3} sx={{ mb: 3 }}>
                    <Grid item xs={12} sm={3}><StatCard icon={<Hotel />} label="All Reservations" value={reservations.length} color="#00bcd4" /></Grid>
                    <Grid item xs={12} sm={3}><StatCard icon={<Receipt />} label="Pending" value={pending.length} color="#ffc107" /></Grid>
                    <Grid item xs={12} sm={3}><StatCard icon={<FlightLand />} label="Checked In" value={reservations.filter(r => r.status === 'CHECKED_IN').length} color="#4caf50" /></Grid>
                    <Grid item xs={12} sm={3}><StatCard icon={<MeetingRoom />} label="Available Rooms" value={availRooms.length} color="#03a9f4" /></Grid>
                </Grid>

                <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 3, '& .MuiTab-root': { fontWeight: 600, textTransform: 'none' } }}>
                    <Tab label="All Reservations" />
                    <Tab label={`Pending (${pending.length})`} />
                    <Tab label="Rooms" />
                    <Tab label="Help & Guidelines" />
                </Tabs>

                {tab === 0 && (
                    <Card><CardContent>
                        <TableContainer><Table>
                            <TableHead><TableRow>
                                <TableCell sx={{ fontWeight: 700 }}>ID</TableCell><TableCell sx={{ fontWeight: 700 }}>Guest</TableCell>
                                <TableCell sx={{ fontWeight: 700 }}>Room</TableCell><TableCell sx={{ fontWeight: 700 }}>Check-in</TableCell>
                                <TableCell sx={{ fontWeight: 700 }}>Check-out</TableCell><TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                                <TableCell sx={{ fontWeight: 700 }}>Actions</TableCell>
                            </TableRow></TableHead>
                            <TableBody>
                                {reservations.map(r => (
                                    <TableRow key={r.id} sx={{ '&:hover': { bgcolor: 'rgba(0,188,212,0.04)' } }}>
                                        <TableCell>#{r.id}</TableCell>
                                        <TableCell>{r.guestName || `Guest #${r.guestId}`}</TableCell>
                                        <TableCell>{r.roomNumber || '-'}</TableCell>
                                        <TableCell>{r.checkInDate}</TableCell>
                                        <TableCell>{r.checkOutDate}</TableCell>
                                        <TableCell><Chip label={r.status?.replace('_', ' ')} size="small" color={statusColor(r.status)} /></TableCell>
                                        <TableCell>
                                            {r.status === 'PENDING_APPROVAL' && <Button size="small" color="success" onClick={() => handleApprove(r.id)}>Approve</Button>}
                                            {r.status === 'CONFIRMED' && <Button size="small" variant="contained" color="success" startIcon={<FlightLand />} onClick={() => handleCheckIn(r.id)}>Check In</Button>}
                                            {r.status === 'CHECKED_IN' && <Button size="small" variant="contained" color="warning" startIcon={<FlightTakeoff />} onClick={() => handleCheckOut(r.id)}>Check Out</Button>}
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table></TableContainer>
                    </CardContent></Card>
                )}

                {tab === 1 && (
                    <Card><CardContent>
                        <Typography variant="h6" gutterBottom color="primary">Pending Approval</Typography>
                        {pending.length === 0 ? <Typography color="text.secondary">No pending reservations</Typography> : (
                            <TableContainer><Table>
                                <TableHead><TableRow>
                                    <TableCell sx={{ fontWeight: 700 }}>Guest</TableCell><TableCell sx={{ fontWeight: 700 }}>Room</TableCell>
                                    <TableCell sx={{ fontWeight: 700 }}>Dates</TableCell><TableCell sx={{ fontWeight: 700 }}>Actions</TableCell>
                                </TableRow></TableHead>
                                <TableBody>
                                    {pending.map(r => (
                                        <TableRow key={r.id}>
                                            <TableCell>{r.guestName || `Guest #${r.guestId}`}</TableCell>
                                            <TableCell>{r.roomNumber || '-'}</TableCell>
                                            <TableCell>{r.checkInDate} → {r.checkOutDate}</TableCell>
                                            <TableCell>
                                                <Button size="small" variant="contained" color="success" startIcon={<CheckCircle />} onClick={() => handleApprove(r.id)} sx={{ mr: 1 }}>Approve</Button>
                                                <Button size="small" variant="outlined" color="error" startIcon={<Cancel />} onClick={() => api.cancelReservation(r.id, 'Rejected').then(load)}>Reject</Button>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table></TableContainer>
                        )}
                    </CardContent></Card>
                )}

                {tab === 2 && (
                    <Grid container spacing={2}>
                        {rooms.map(r => (
                            <Grid item xs={12} sm={6} md={4} lg={3} key={r.id}>
                                <Card sx={{ opacity: r.status === 'AVAILABLE' ? 1 : 0.7 }}>
                                    <CardContent>
                                        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                                            <Typography variant="h5" fontWeight={800} color="primary">Room {r.roomNumber}</Typography>
                                            <Chip label={r.status} size="small" color={statusColor(r.status)} />
                                        </Box>
                                        <Typography variant="body2" color="text.secondary">Floor {r.floor} • {r.viewType} view</Typography>
                                        {r.categoryName && <Typography variant="body2" color="text.secondary">{r.categoryName}</Typography>}
                                    </CardContent>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                )}

                {tab === 3 && (
                    <Card>
                        <CardContent>
                            <Typography variant="h5" gutterBottom color="primary" fontWeight={700}>
                                Receptionist Help & Onboarding Guide
                            </Typography>
                            <Typography variant="body1" sx={{ mb: 2 }}>
                                This guide is for new reception staff to quickly learn how to handle day-to-day reservations and check-in/check-out
                                using the OceanView system.
                            </Typography>

                            <Typography variant="subtitle1" fontWeight={700} sx={{ mt: 1 }}>
                                1. Dashboard overview
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                                • <strong>Stats cards</strong> at the top show today&apos;s overall activity: total reservations, pending approvals,
                                checked-in guests, and currently available rooms.
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                                • Use the <strong>tabs</strong> below to switch between all reservations, pending approvals, room list, and this help
                                section.
                            </Typography>

                            <Typography variant="subtitle1" fontWeight={700} sx={{ mt: 1 }}>
                                2. Creating a new booking
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                                • Click the <strong>"New Booking"</strong> button in the top bar to open the booking dialog. Fill in the guest ID,
                                room, stay dates, and number of guests. Use <strong>"Create Reservation"</strong> when you want a manager to approve
                                first, or <strong>"Instant Book &amp; Check In"</strong> for confirmed walk‑in guests where a room is immediately
                                assigned and marked as occupied.
                            </Typography>

                            <Typography variant="subtitle1" fontWeight={700} sx={{ mt: 1 }}>
                                3. Approving and rejecting reservations
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                                • The <strong>"Pending"</strong> tab lists all reservations waiting for approval. Use
                                <strong> Approve</strong> to confirm a booking, or <strong>Reject</strong> if the request cannot be accepted.
                            </Typography>

                            <Typography variant="subtitle1" fontWeight={700} sx={{ mt: 1 }}>
                                4. Check‑in and check‑out
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                                • In the <strong>"All Reservations"</strong> tab, look for the action buttons per reservation row:
                                <strong> Approve</strong> (for pending), <strong>Check In</strong> (for confirmed), and
                                <strong> Check Out</strong> (for checked‑in guests). After checkout the system will free the room and can generate
                                an invoice for the guest.
                            </Typography>

                            <Typography variant="subtitle1" fontWeight={700} sx={{ mt: 1 }}>
                                5. Monitoring room availability
                            </Typography>
                            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                                • The <strong>"Rooms"</strong> tab shows all rooms with their current status (Available, Occupied, Maintenance, etc.).
                                Always confirm that a room is <strong>AVAILABLE</strong> before assigning it to a reservation or walk‑in guest.
                            </Typography>

                            <Typography variant="subtitle1" fontWeight={700} sx={{ mt: 1 }}>
                                6. Best practices
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                • Double‑check dates and room numbers before saving, add clear notes for special requests, and keep pending
                                reservations up‑to‑date so managers see an accurate queue. When in doubt, consult your manager before confirming or
                                cancelling a high‑value booking.
                            </Typography>
                        </CardContent>
                    </Card>
                )}
            </Box>

            {/* Booking Dialog */}
            <Dialog open={bookDlg} onClose={() => setBookDlg(false)} maxWidth="sm" fullWidth>
                <DialogTitle sx={{ fontWeight: 700 }}>New Booking / Reservation</DialogTitle>
                <DialogContent>
                    <Grid container spacing={2} sx={{ mt: 0.5 }}>
                        <Grid item xs={12}>
                            <TextField fullWidth label="Guest ID" type="number" value={bookForm.guestId} onChange={e => setBookForm(f => ({ ...f, guestId: parseInt(e.target.value) || '' }))} />
                        </Grid>
                        <Grid item xs={12}>
                            <FormControl fullWidth>
                                <InputLabel>Room</InputLabel>
                                <Select value={bookForm.roomId} label="Room" onChange={e => setBookForm(f => ({ ...f, roomId: e.target.value }))}>
                                    {availRooms.map(r => <MenuItem key={r.id} value={r.id}>Room {r.roomNumber} - Floor {r.floor} ({r.categoryName || 'Standard'})</MenuItem>)}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={6}>
                            <TextField fullWidth type="date" label="Check-in" value={bookForm.checkInDate} onChange={e => setBookForm(f => ({ ...f, checkInDate: e.target.value }))} InputLabelProps={{ shrink: true }} />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField fullWidth type="date" label="Check-out" value={bookForm.checkOutDate} onChange={e => setBookForm(f => ({ ...f, checkOutDate: e.target.value }))} InputLabelProps={{ shrink: true }} />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField fullWidth type="number" label="Adults" value={bookForm.adults} onChange={e => setBookForm(f => ({ ...f, adults: parseInt(e.target.value) || 1 }))} />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField fullWidth type="number" label="Children" value={bookForm.children} onChange={e => setBookForm(f => ({ ...f, children: parseInt(e.target.value) || 0 }))} />
                        </Grid>
                    </Grid>
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 2 }}>
                    <Button onClick={() => setBookDlg(false)}>Cancel</Button>
                    <Button variant="outlined" color="primary" onClick={handleReservation}>Create Reservation</Button>
                    <Button variant="contained" color="secondary" onClick={handleInstantBook} startIcon={<FlightLand />}>Instant Book & Check In</Button>
                </DialogActions>
            </Dialog>

            {/* Invoice Dialog */}
            <Dialog open={invoiceDlg} onClose={() => setInvoiceDlg(false)} maxWidth="sm" fullWidth>
                <DialogTitle sx={{ fontWeight: 700, color: 'primary.main' }}>Checkout Bill</DialogTitle>
                <DialogContent>
                    {invoice && (
                        <Box>
                            <Box sx={{ mb: 2, p: 2, bgcolor: 'rgba(0,188,212,0.05)', borderRadius: 2 }}>
                                <Typography variant="body2" color="text.secondary">Invoice #{invoice.invoiceNumber}</Typography>
                                <Typography variant="body1" fontWeight={600}>{invoice.guestName}</Typography>
                                <Typography variant="body2" color="text.secondary">Room {invoice.roomNumber} • {invoice.checkInDate} to {invoice.checkOutDate}</Typography>
                            </Box>
                            <TableContainer>
                                <Table size="small">
                                    <TableHead><TableRow><TableCell sx={{ fontWeight: 700 }}>Description</TableCell><TableCell align="right" sx={{ fontWeight: 700 }}>Qty</TableCell><TableCell align="right" sx={{ fontWeight: 700 }}>Price</TableCell><TableCell align="right" sx={{ fontWeight: 700 }}>Amount</TableCell></TableRow></TableHead>
                                    <TableBody>
                                        {(invoice.lineItems || []).map((item, i) => (
                                            <TableRow key={i}><TableCell>{item.description}</TableCell><TableCell align="right">{item.quantity}</TableCell><TableCell align="right">LKR {Number(item.unitPrice).toLocaleString()}</TableCell><TableCell align="right">LKR {Number(item.amount).toLocaleString()}</TableCell></TableRow>
                                        ))}
                                        <TableRow><TableCell colSpan={3} align="right" sx={{ fontWeight: 600 }}>Subtotal</TableCell><TableCell align="right">LKR {Number(invoice.subtotal).toLocaleString()}</TableCell></TableRow>
                                        <TableRow><TableCell colSpan={3} align="right" sx={{ fontWeight: 600 }}>Tax ({invoice.taxRate}%)</TableCell><TableCell align="right">LKR {Number(invoice.taxAmount).toLocaleString()}</TableCell></TableRow>
                                        <TableRow><TableCell colSpan={3} align="right" sx={{ fontWeight: 700, color: 'primary.main', fontSize: '1.1em' }}>Total</TableCell><TableCell align="right" sx={{ fontWeight: 700, color: 'primary.main', fontSize: '1.1em' }}>LKR {Number(invoice.totalAmount).toLocaleString()}</TableCell></TableRow>
                                    </TableBody>
                                </Table>
                            </TableContainer>
                        </Box>
                    )}
                </DialogContent>
                <DialogActions><Button onClick={() => setInvoiceDlg(false)} variant="contained">Close</Button></DialogActions>
            </Dialog>
        </Box>
    );
}
