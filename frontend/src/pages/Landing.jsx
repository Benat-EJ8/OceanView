import { Link } from 'react-router-dom';
import { Box, Typography, Button, Card, CardContent, Grid, Container, CardMedia } from '@mui/material';
import BeachAccessIcon from '@mui/icons-material/BeachAccess';
import BedIcon from '@mui/icons-material/Bed';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

export function Landing() {
  return (
    <Box>
      {/* Hero Section */}
      <Box
        sx={{
          height: '80vh',
          backgroundImage: 'url("/images/hero.jpg")',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'white',
          position: 'relative',
          '&::after': {
            content: '""',
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.4)',
          },
        }}
      >
        <Box sx={{ position: 'relative', zIndex: 1, textAlign: 'center', p: 3 }}>
          <Typography variant="h2" component="h1" fontWeight="bold" gutterBottom>
            Ocean View Resort
          </Typography>
          <Typography variant="h5" sx={{ mb: 4, fontWeight: 300 }}>
            Luxury Beachside Stay in Galle
          </Typography>
          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
            <Button component={Link} to="/booking" variant="contained" size="large" sx={{ px: 4, py: 1.5, fontSize: '1.1rem' }}>
              View Rooms & Book
            </Button>
            <Button component={Link} to="/login" variant="outlined" color="inherit" size="large" sx={{ px: 4, py: 1.5, fontSize: '1.1rem' }}>
              Staff Login
            </Button>
          </Box>
        </Box>
      </Box>

      {/* Features Section */}
      <Container maxWidth="lg" sx={{ py: 8 }}>
        <Typography variant="h4" align="center" sx={{ mb: 6, fontWeight: 'bold', color: 'primary.main' }}>
          Why stay with us
        </Typography>
        <Grid container spacing={4}>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card sx={{ height: '100%', borderRadius: 4, boxShadow: 3 }}>
              <CardMedia component="img" height="200" image="/images/hero.jpg" alt="Beach" />
              <CardContent sx={{ textAlign: 'center', pt: 3 }}>
                <BeachAccessIcon sx={{ fontSize: 40, color: 'primary.main', mb: 2 }} />
                <Typography variant="h6" gutterBottom fontWeight="bold">Direct Beach Access</Typography>
                <Typography color="text.secondary">Step out of your room and onto the golden sands of Galle.</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card sx={{ height: '100%', borderRadius: 4, boxShadow: 3 }}>
              <CardMedia component="img" height="200" image="/images/deluxe.jpg" alt="Rooms" />
              <CardContent sx={{ textAlign: 'center', pt: 3 }}>
                <BedIcon sx={{ fontSize: 40, color: 'primary.main', mb: 2 }} />
                <Typography variant="h6" gutterBottom fontWeight="bold">Luxury Rooms</Typography>
                <Typography color="text.secondary">Spacious suites with private balconies and ocean views.</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card sx={{ height: '100%', borderRadius: 4, boxShadow: 3 }}>
              <CardMedia component="img" height="200" image="/images/suite.jpg" alt="Interior" />
              <CardContent sx={{ textAlign: 'center', pt: 3 }}>
                <CheckCircleIcon sx={{ fontSize: 40, color: 'primary.main', mb: 2 }} />
                <Typography variant="h6" gutterBottom fontWeight="bold">Easy Booking</Typography>
                <Typography color="text.secondary">Hassle-free online reservations confirmed instantly.</Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
}
