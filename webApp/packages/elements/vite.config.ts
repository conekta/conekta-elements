import { defineConfig } from 'vite';
import path from 'path';

export default defineConfig({
    build: {
        outDir: 'dist/npm',
        emptyOutDir: true,
        lib: {
            entry: {
                'npm/index': 'src/entries/npm/index.ts',
                'npm/orchestrator': 'src/entries/npm/orchestrator.ts',
                'npm/applePay': 'src/entries/npm/applePay.ts',
                'npm/googlePay': 'src/entries/npm/googlePay.ts',
                orchestrator: 'src/entries/compat/orchestrator.ts',
                applePay: 'src/entries/compat/applePay.ts',
                googlePay: 'src/entries/compat/googlePay.ts',
                index: 'src/entries/compat/index.ts',
            },
            formats: ['es', 'cjs'],
            fileName: (format, entryName) => `${entryName}.${format}.js`,
        },
        rollupOptions: {
            external: [],
        },
    },
});
