import React from 'react';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, PointElement, LineElement } from 'chart.js';
import { Doughnut, Bar, Pie, Line } from 'react-chartjs-2';
import { Box, Typography, Grid, Card, CardContent } from '@mui/material';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, PointElement, LineElement);

const chartOptions = {
    responsive: true, maintainAspectRatio: false,
    plugins: {
        legend: { labels: { color: '#e0e6f0', font: { family: 'Inter', size: 12 }, padding: 16, usePointStyle: true } },
        tooltip: { backgroundColor: 'rgba(17,34,64,0.95)', titleColor: '#4dd0e1', bodyColor: '#e0e6f0', borderColor: 'rgba(0,188,212,0.3)', borderWidth: 1, padding: 12, cornerRadius: 10 },
    },
    scales: {
        x: { ticks: { color: '#8892b0', font: { family: 'Inter' } }, grid: { color: 'rgba(0,188,212,0.06)' } },
        y: { ticks: { color: '#8892b0', font: { family: 'Inter' } }, grid: { color: 'rgba(0,188,212,0.06)' } },
    },
};

const doughnutOptions = { ...chartOptions, scales: undefined, cutout: '65%' };
const pieOptions = { ...chartOptions, scales: undefined };

const COLORS = ['#00bcd4', '#ff9800', '#e91e63', '#4caf50', '#9c27b0', '#ff5722', '#03a9f4', '#8bc34a', '#ffc107', '#607d8b'];

export function OccupancyChart({ data }) {
    if (!data) return null;
    const chartData = {
        labels: ['Occupied', 'Available'],
        datasets: [{ data: [data.occupiedRooms || 0, data.availableRooms || 0], backgroundColor: ['#00bcd4', 'rgba(0,188,212,0.15)'], borderColor: ['#00bcd4', 'rgba(0,188,212,0.3)'], borderWidth: 2 }],
    };
    return (
        <Card sx={{ height: '100%' }}>
            <CardContent>
                <Typography variant="h6" gutterBottom sx={{ color: 'primary.main' }}>Room Occupancy</Typography>
                <Box sx={{ height: 260, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Doughnut data={chartData} options={doughnutOptions} />
                </Box>
                <Box sx={{ textAlign: 'center', mt: 1 }}>
                    <Typography variant="h3" color="primary" fontWeight={800}>{Math.round((data.occupancyRate || 0) * 100)}%</Typography>
                    <Typography variant="body2" color="text.secondary">Current Occupancy Rate</Typography>
                </Box>
            </CardContent>
        </Card>
    );
}

export function CategoryOccupancyChart({ data }) {
    if (!data?.categoryBreakdown?.length) return null;
    const chartData = {
        labels: data.categoryBreakdown.map(c => c.category),
        datasets: [
            { label: 'Total Rooms', data: data.categoryBreakdown.map(c => c.total), backgroundColor: 'rgba(0,188,212,0.3)', borderColor: '#00bcd4', borderWidth: 2, borderRadius: 8 },
            { label: 'Occupied', data: data.categoryBreakdown.map(c => c.occupied), backgroundColor: 'rgba(255,152,0,0.5)', borderColor: '#ff9800', borderWidth: 2, borderRadius: 8 },
        ],
    };
    return (
        <Card sx={{ height: '100%' }}>
            <CardContent>
                <Typography variant="h6" gutterBottom sx={{ color: 'primary.main' }}>Occupancy by Category</Typography>
                <Box sx={{ height: 280 }}><Bar data={chartData} options={chartOptions} /></Box>
            </CardContent>
        </Card>
    );
}

export function RevenueChart({ data }) {
    if (!data) return null;
    const revenue = Number(data.revenue || 0);
    const serviceRevenue = Number(data.serviceRevenue || 0);
    const roomRevenue = revenue - serviceRevenue;
    const chartData = {
        labels: ['Room Revenue', 'Service Revenue'],
        datasets: [{ data: [roomRevenue, serviceRevenue], backgroundColor: ['#00bcd4', '#ff9800'], borderWidth: 0 }],
    };
    return (
        <Card sx={{ height: '100%' }}>
            <CardContent>
                <Typography variant="h6" gutterBottom sx={{ color: 'primary.main' }}>Revenue Breakdown</Typography>
                <Box sx={{ textAlign: 'center', mb: 2 }}>
                    <Typography variant="h4" fontWeight={800} sx={{ background: 'linear-gradient(135deg, #00e676, #4dd0e1)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
                        LKR {revenue.toLocaleString()}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">Total Revenue</Typography>
                </Box>
                <Box sx={{ height: 240, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Doughnut data={chartData} options={doughnutOptions} />
                </Box>
            </CardContent>
        </Card>
    );
}

export function RevenueByCategoryChart({ data }) {
    if (!data?.revenueByCategory?.length) return null;
    const chartData = {
        labels: data.revenueByCategory.map(c => c.category || 'Other'),
        datasets: [{ label: 'Revenue (LKR)', data: data.revenueByCategory.map(c => Number(c.revenue)), backgroundColor: COLORS.slice(0, data.revenueByCategory.length), borderWidth: 0, borderRadius: 8 }],
    };
    return (
        <Card sx={{ height: '100%' }}>
            <CardContent>
                <Typography variant="h6" gutterBottom sx={{ color: 'primary.main' }}>Revenue by Room Category</Typography>
                <Box sx={{ height: 280 }}><Bar data={chartData} options={chartOptions} /></Box>
            </CardContent>
        </Card>
    );
}

export function BookingStatusChart({ data }) {
    if (!data?.byStatus?.length) return null;
    const statusColors = { PENDING_APPROVAL: '#ffc107', CONFIRMED: '#03a9f4', CHECKED_IN: '#00e676', CHECKED_OUT: '#9c27b0', CANCELLED: '#ff5252', NO_SHOW: '#607d8b' };
    const chartData = {
        labels: data.byStatus.map(s => s.status.replace('_', ' ')),
        datasets: [{ data: data.byStatus.map(s => s.count), backgroundColor: data.byStatus.map(s => statusColors[s.status] || '#888'), borderWidth: 0 }],
    };
    return (
        <Card sx={{ height: '100%' }}>
            <CardContent>
                <Typography variant="h6" gutterBottom sx={{ color: 'primary.main' }}>Booking Status</Typography>
                <Grid container spacing={2} sx={{ mb: 2 }}>
                    <Grid item xs={6}><Box sx={{ textAlign: 'center' }}><Typography variant="h4" fontWeight={800} color="primary">{data.totalBookings || 0}</Typography><Typography variant="caption" color="text.secondary">Total Bookings</Typography></Box></Grid>
                    <Grid item xs={6}><Box sx={{ textAlign: 'center' }}><Typography variant="h4" fontWeight={800} color="secondary">{(data.averageStayDays || 0).toFixed(1)}</Typography><Typography variant="caption" color="text.secondary">Avg Stay (Days)</Typography></Box></Grid>
                </Grid>
                <Box sx={{ height: 240, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Pie data={chartData} options={pieOptions} />
                </Box>
            </CardContent>
        </Card>
    );
}

export function StaffPerformanceChart({ data }) {
    if (!data?.staff?.length) return null;
    const chartData = {
        labels: data.staff.map(s => s.name),
        datasets: [{ label: 'Bookings Approved', data: data.staff.map(s => s.bookingsApproved), backgroundColor: 'rgba(0,188,212,0.4)', borderColor: '#00bcd4', borderWidth: 2, borderRadius: 8 }],
    };
    const opts = { ...chartOptions, indexAxis: 'y' };
    return (
        <Card sx={{ height: '100%' }}>
            <CardContent>
                <Typography variant="h6" gutterBottom sx={{ color: 'primary.main' }}>Staff Performance</Typography>
                <Box sx={{ height: 280 }}><Bar data={chartData} options={opts} /></Box>
            </CardContent>
        </Card>
    );
}

export function ServiceStatsChart({ data }) {
    if (!data?.services?.length) return null;
    const chartData = {
        labels: data.services.map(s => s.name),
        datasets: [
            { label: 'Times Booked', data: data.services.map(s => s.timesBooked), backgroundColor: 'rgba(0,188,212,0.5)', borderColor: '#00bcd4', borderWidth: 2, borderRadius: 8, yAxisID: 'y' },
            { label: 'Revenue (LKR)', data: data.services.map(s => Number(s.revenue)), backgroundColor: 'rgba(255,152,0,0.4)', borderColor: '#ff9800', borderWidth: 2, borderRadius: 8, yAxisID: 'y1' },
        ],
    };
    const opts = {
        ...chartOptions,
        scales: {
            ...chartOptions.scales,
            y: { ...chartOptions.scales.y, position: 'left', title: { display: true, text: 'Times Booked', color: '#8892b0' } },
            y1: { ...chartOptions.scales.y, position: 'right', grid: { drawOnChartArea: false }, title: { display: true, text: 'Revenue (LKR)', color: '#8892b0' } },
        },
    };
    return (
        <Card sx={{ height: '100%' }}>
            <CardContent>
                <Typography variant="h6" gutterBottom sx={{ color: 'primary.main' }}>Service Popularity & Revenue</Typography>
                <Box sx={{ height: 300 }}><Bar data={chartData} options={opts} /></Box>
            </CardContent>
        </Card>
    );
}
