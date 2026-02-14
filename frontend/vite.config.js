import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        // If your WAR is deployed as root, use target: 'http://localhost:8080'
        target: 'http://localhost:8080/oceanview',
        changeOrigin: true,
      },
    },
  },
})
