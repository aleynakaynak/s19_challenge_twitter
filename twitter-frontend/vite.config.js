import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// README: React uygulaması 3200 portundan ayağa kalkmalı
export default defineConfig({
  plugins: [react()],
  server: { port: 3200, strictPort: true },
  preview: { port: 3200, strictPort: true },
})
