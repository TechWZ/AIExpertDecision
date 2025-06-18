import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 5173,
    proxy: {
      // 统一通过server代理所有API请求
      '/server': {
        target: 'http://127.0.0.1:5002',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/server/, '/AIExpertDecisionServer')
      }
    }
  }
})
