import { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Button,
  Alert,
  CircularProgress,
  Paper,
} from '@mui/material';
import { getReports } from '../api';

export function Reports() {
  const [type, setType] = useState('occupancy');
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const run = () => {
    setLoading(true);
    setError('');
    getReports(type, from || undefined, to || undefined)
      .then(setData)
      .catch((e) => {
        setError(e.message || 'Failed to load report');
        setData(null);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => { run(); }, [type]);

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 2 }}>Reports</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, alignItems: 'center' }}>
            <FormControl size="small" sx={{ minWidth: 160 }}>
              <InputLabel>Report type</InputLabel>
              <Select value={type} label="Report type" onChange={(e) => setType(e.target.value)}>
                <MenuItem value="occupancy">Occupancy</MenuItem>
                <MenuItem value="revenue">Revenue</MenuItem>
                <MenuItem value="bookings">Booking stats</MenuItem>
                <MenuItem value="staff">Staff performance</MenuItem>
              </Select>
            </FormControl>
            <TextField type="date" label="From" value={from} onChange={(e) => setFrom(e.target.value)} InputLabelProps={{ shrink: true }} size="small" />
            <TextField type="date" label="To" value={to} onChange={(e) => setTo(e.target.value)} InputLabelProps={{ shrink: true }} size="small" />
            <Button variant="contained" onClick={run} disabled={loading}>{loading ? 'Loading...' : 'Run'}</Button>
          </Box>
        </CardContent>
      </Card>
      {data && (
        <Paper variant="outlined" sx={{ p: 2 }}>
          <Typography variant="h6" gutterBottom>Result</Typography>
          <Box component="pre" sx={{ overflow: 'auto', fontSize: '0.85rem', m: 0 }}>{JSON.stringify(data, null, 2)}</Box>
        </Paper>
      )}
    </Box>
  );
}
