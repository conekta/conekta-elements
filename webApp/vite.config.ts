import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

export default defineConfig({
  root: '.',
  plugins: [react()],
  build: {
    outDir: 'dist',
    emptyOutDir: true,
  },
  server: { port: 8080 },
  test: {
    globals: true,
    environment: 'happy-dom',
    setupFiles: './vitest.setup.ts',
    css: true,
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'test/',
        '.storybook/',
        '**/dist/**',
        '**/*.d.ts',
        '**/*.config.*',
        '**/*.stories.*',
        'src/**/index.ts',
        'src/main.tsx',
        'src/vite-env.d.ts',
      ],
    },
  },
});