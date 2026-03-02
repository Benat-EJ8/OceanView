import { createTheme } from '@mui/material';

const theme = createTheme({
    palette: {
        mode: 'dark',
        primary: { main: '#00bcd4', light: '#4dd0e1', dark: '#0097a7' },
        secondary: { main: '#ff6f00', light: '#ff9800', dark: '#e65100' },
        background: { default: '#0a1628', paper: '#112240' },
        success: { main: '#00e676' },
        warning: { main: '#ffc107' },
        error: { main: '#ff5252' },
        info: { main: '#29b6f6' },
        text: { primary: '#e0e6f0', secondary: '#8892b0' },
    },
    typography: {
        fontFamily: '"Inter", "Roboto", sans-serif',
        h3: { fontWeight: 700, letterSpacing: '-0.02em' },
        h4: { fontWeight: 700, letterSpacing: '-0.01em' },
        h5: { fontWeight: 600 },
        h6: { fontWeight: 600 },
    },
    shape: { borderRadius: 16 },
    components: {
        MuiCard: {
            styleOverrides: {
                root: {
                    backgroundImage: 'linear-gradient(135deg, rgba(17,34,64,0.95) 0%, rgba(10,22,40,0.98) 100%)',
                    border: '1px solid rgba(0,188,212,0.12)',
                    backdropFilter: 'blur(20px)',
                    transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                    '&:hover': { transform: 'translateY(-2px)', boxShadow: '0 12px 40px rgba(0,188,212,0.15)' },
                },
            },
        },
        MuiButton: {
            styleOverrides: {
                root: { textTransform: 'none', fontWeight: 600, borderRadius: 12, padding: '10px 24px' },
                containedPrimary: {
                    background: 'linear-gradient(135deg, #00bcd4 0%, #0097a7 100%)',
                    '&:hover': { background: 'linear-gradient(135deg, #4dd0e1 0%, #00bcd4 100%)' },
                },
                containedSecondary: {
                    background: 'linear-gradient(135deg, #ff9800 0%, #e65100 100%)',
                    '&:hover': { background: 'linear-gradient(135deg, #ffb74d 0%, #ff9800 100%)' },
                },
            },
        },
        MuiChip: { styleOverrides: { root: { fontWeight: 600, borderRadius: 8 } } },
        MuiPaper: {
            styleOverrides: {
                root: {
                    backgroundImage: 'linear-gradient(135deg, rgba(17,34,64,0.95) 0%, rgba(10,22,40,0.98) 100%)',
                    border: '1px solid rgba(0,188,212,0.08)',
                },
            },
        },
        MuiDialog: {
            styleOverrides: {
                paper: {
                    backgroundImage: 'linear-gradient(135deg, rgba(17,34,64,0.98) 0%, rgba(10,22,40,1) 100%)',
                    border: '1px solid rgba(0,188,212,0.2)',
                },
            },
        },
        MuiTextField: {
            styleOverrides: {
                root: {
                    '& .MuiOutlinedInput-root': {
                        borderRadius: 12,
                        '& fieldset': { borderColor: 'rgba(0,188,212,0.2)' },
                        '&:hover fieldset': { borderColor: 'rgba(0,188,212,0.4)' },
                    },
                },
            },
        },
    },
});

export default theme;
