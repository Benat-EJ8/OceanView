import { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardMedia,
  CardContent,
  Typography,
  Button,
  TextField,
  Grid,
  Alert,
  CircularProgress,
  Chip,
} from '@mui/material';
import { getRoomCategories, getRooms, createReservation, getGuestMe } from '../api';
import { useAuth } from '../context/AuthContext';

export function Booking() {
  const { user } = useAuth();
  const [categories, setCategories] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [checkIn, setCheckIn] = useState('');
  const [checkOut, setCheckOut] = useState('');
  const [loading, setLoading] = useState(false);
  const [categoriesLoading, setCategoriesLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    setCategoriesLoading(true);
    setError('');
    getRoomCategories()
      .then(setCategories)
      .catch((e) => setError(e.message || 'Could not load categories. Is the backend running at http://localhost:8080/oceanview?'))
      .finally(() => setCategoriesLoading(false));
  }, []);

  const searchRooms = () => {
    if (!checkIn || !checkOut) return;
    setError('');
    setSuccess('');
    setLoading(true);
    getRooms(1, true, checkIn, checkOut)
      .then(setRooms)
      .catch((e) => setError(e.message || 'Could not load rooms'))
      .finally(() => setLoading(false));
  };

  const bookRoom = async (roomId) => {
    if (!user) {
      setError('Please log in or register to book.');
      return;
    }
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      const guest = await getGuestMe();
      await createReservation({
        branchId: 1,
        guestId: guest.id,
        roomId,
        checkInDate: checkIn,
        checkOutDate: checkOut,
        adults: 1,
        children: 0,
      });
      setSuccess('Booking request sent. A manager or receptionist will approve it shortly.');
      setRooms((prev) => prev.filter((r) => r.id !== roomId));
    } catch (err) {
      setError(err.message || 'Booking failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 2 }}>Book a room</Typography>
      {!user && (
        <Alert severity="info" sx={{ mb: 2 }}>
          Please register or log in to submit a booking.
        </Alert>
      )}
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}

      {/* Room Categories */}
      {categoriesLoading && <CircularProgress sx={{ mb: 2 }} />}
      {!categoriesLoading && categories.length > 0 && (
        <Box sx={{ mb: 3 }}>
          <Typography variant="h5" sx={{ mb: 2 }}>Our Room Categories</Typography>
          <Grid container spacing={3}>
            {categories.map((cat) => {
              const imageMap = {
                'STD': '/images/standard.jpg',
                'DLX': '/images/deluxe.jpg',
                'STE': '/images/suite.jpg'
              };
              const image = imageMap[cat.code] || '/images/standard.jpg';

              return (
                <Grid size={{ xs: 12, sm: 6, md: 4 }} key={cat.id}>
                  <Card sx={{ height: '100%', transition: 'transform 0.2s', '&:hover': { transform: 'translateY(-4px)' } }}>
                    <CardMedia
                      component="img"
                      height="200"
                      image={image}
                      alt={cat.name}
                    />
                    <CardContent>
                      <Typography variant="h6" color="primary.main" gutterBottom>{cat.name}</Typography>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>{cat.description}</Typography>
                      <Chip label={`LKR ${Number(cat.basePrice).toLocaleString()} / night`} color="primary" variant="outlined" size="small" sx={{ mr: 1 }} />
                      <Chip label={`Max ${cat.maxOccupancy} guests`} variant="outlined" size="small" />
                      {cat.sizeSqm && (
                        <Typography variant="caption" display="block" sx={{ mt: 1 }} color="text.secondary">{cat.sizeSqm} sqm</Typography>
                      )}
                    </CardContent>
                  </Card>
                </Grid>
              );
            })}
          </Grid>
        </Box>
      )}

      {/* Search */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>Search available rooms</Typography>
          <Grid container spacing={2} sx={{ alignItems: 'center' }}>
            <Grid size="auto">
              <TextField type="date" label="Check-in" value={checkIn} onChange={(e) => setCheckIn(e.target.value)} InputLabelProps={{ shrink: true }} size="small" />
            </Grid>
            <Grid size="auto">
              <TextField type="date" label="Check-out" value={checkOut} onChange={(e) => setCheckOut(e.target.value)} InputLabelProps={{ shrink: true }} size="small" />
            </Grid>
            <Grid size="auto">
              <Button variant="contained" onClick={searchRooms} disabled={loading || !checkIn || !checkOut}>{loading ? 'Searching...' : 'Search'}</Button>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Available Rooms */}
      <Grid container spacing={2}>
        {rooms.map((room) => (
          <Grid size={{ xs: 12, sm: 6, md: 4 }} key={room.id}>
            <Card>
              <CardContent>
                <Typography variant="h6">{room.categoryName} – {room.roomNumber}</Typography>
                <Typography variant="body2" color="text.secondary">Floor {room.floor} · {room.status}</Typography>
                <Typography variant="body2">LKR {room.basePrice} / night</Typography>
                <Button variant="contained" fullWidth sx={{ mt: 1 }} onClick={() => bookRoom(room.id)} disabled={loading}>Request booking</Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
      {!loading && rooms.length === 0 && checkIn && checkOut && !error && <Typography color="text.secondary">No rooms available for these dates.</Typography>}
    </Box>
  );
}
