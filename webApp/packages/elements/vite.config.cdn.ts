import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
    plugins: [react()],
    build: {
        outDir: 'dist/cdn',
        emptyOutDir: true,
        lib: {
            entry: path.resolve(__dirname, 'src/entries/cdn/orchestrator.ts'),
            name: 'ConektaElements',
            formats: ['iife'],
            fileName: () => 'orchestrator.iife.js',
        },
        rollupOptions: {
            output: {
                banner: `var process = (typeof process !== 'undefined' ? process : { env: {} });`,
            },
        },
    },
});
