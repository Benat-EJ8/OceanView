import { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import {
  Box, Drawer, AppBar, Toolbar, Typography, List, ListItemButton,
  ListItemIcon, ListItemText, IconButton, Button, Badge, Tooltip,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import DashboardIcon from '@mui/icons-material/Dashboard';
import MeetingRoomIcon from '@mui/icons-material/MeetingRoom';
import BookOnlineIcon from '@mui/icons-material/BookOnline';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import BarChartIcon from '@mui/icons-material/BarChart';
import PeopleIcon from '@mui/icons-material/People';
import LoginIcon from '@mui/icons-material/Login';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import LogoutIcon from '@mui/icons-material/Logout';
import NotificationsIcon from '@mui/icons-material/Notifications';
import BuildIcon from '@mui/icons-material/Build';
import RoomServiceIcon from '@mui/icons-material/RoomService';
import FeedbackIcon from '@mui/icons-material/Feedback';
import BedroomParentIcon from '@mui/icons-material/BedroomParent';
import { useAuth } from '../context/AuthContext';
import { getUnreadCount } from '../api';

const DRAWER_WIDTH = 260;

export function DashboardLayout({ children }) {
  const [drawerOpen, setDrawerOpen] = useState(true);
  const { user, logout, isManager, isReceptionist, isCustomer } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (!user || isCustomer) return;
    const fetchCount = () => getUnreadCount().then(setUnreadCount).catch(() => { });
    fetchCount();
    const interval = setInterval(fetchCount, 30000);
    return () => clearInterval(interval);
  }, [user, isCustomer]);

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const navItems = [];
  if (user) {
    if (isCustomer) {
      navItems.push({ to: '/dashboard', label: 'Dashboard', icon: <DashboardIcon /> });
      navItems.push({ to: '/booking', label: 'Book a Room', icon: <MeetingRoomIcon /> });
      navItems.push({ to: '/maintenance', label: 'Maintenance', icon: <BuildIcon /> });
      navItems.push({ to: '/service-requests', label: 'Service Requests', icon: <RoomServiceIcon /> });
      navItems.push({ to: '/feedback', label: 'Feedback', icon: <FeedbackIcon /> });
    }
    if (isReceptionist || isManager) {
      navItems.push({ to: '/reception', label: 'Reception', icon: <BookOnlineIcon /> });
      navItems.push({ to: '/reservations', label: 'Reservations', icon: <ReceiptLongIcon /> });
      navItems.push({ to: '/notifications', label: 'Notifications', icon: <NotificationsIcon /> });
    }
    if (isManager) {
      navItems.push({ to: '/rooms', label: 'Rooms', icon: <BedroomParentIcon /> });
      navItems.push({ to: '/reports', label: 'Reports', icon: <BarChartIcon /> });
      navItems.push({ to: '/staff', label: 'Staff', icon: <PeopleIcon /> });
    }
  } else {
    navItems.push({ to: '/login', label: 'Login', icon: <LoginIcon /> });
    navItems.push({ to: '/register', label: 'Register', icon: <PersonAddIcon /> });
  }

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'grey.100' }}>
      <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
        <Toolbar>
          <IconButton color="inherit" edge="start" onClick={() => setDrawerOpen(!drawerOpen)} sx={{ mr: 2 }}>
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" component={Link} to="/" sx={{ flexGrow: 1, textDecoration: 'none', color: 'inherit' }}>
            Ocean View Resort
          </Typography>
          {user && !isCustomer && (
            <Tooltip title="Notifications">
              <IconButton color="inherit" component={Link} to="/notifications" sx={{ mr: 1 }}>
                <Badge badgeContent={unreadCount} color="error">
                  <NotificationsIcon />
                </Badge>
              </IconButton>
            </Tooltip>
          )}
          {user && (
            <>
              <Typography variant="body2" sx={{ mr: 2 }}>{user.firstName} ({user.roleCode})</Typography>
              <Button color="inherit" startIcon={<LogoutIcon />} onClick={handleLogout}>Logout</Button>
            </>
          )}
        </Toolbar>
      </AppBar>
      <Drawer
        variant="persistent"
        open={drawerOpen}
        sx={{
          width: DRAWER_WIDTH,
          flexShrink: 0,
          '& .MuiDrawer-paper': { width: DRAWER_WIDTH, boxSizing: 'border-box', top: 64, height: 'calc(100% - 64px)' },
        }}
      >
        <Toolbar />
        <Box sx={{ overflow: 'auto', pt: 1 }}>
          <List>
            {navItems.map((item) => (
              <ListItemButton
                key={item.to}
                component={Link}
                to={item.to}
                selected={location.pathname === item.to}
                sx={{ py: 1.25 }}
              >
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.label} />
              </ListItemButton>
            ))}
          </List>
        </Box>
      </Drawer>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { xs: '100%', sm: `calc(100% - ${drawerOpen ? DRAWER_WIDTH : 0}px)` },
          ml: drawerOpen ? `${DRAWER_WIDTH}px` : 0,
          mt: 8,
          minHeight: 'calc(100vh - 64px)',
        }}
      >
        {children}
      </Box>
    </Box>
  );
}
