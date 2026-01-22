# Conekta Elements - Web Application

> **Part of the monorepo:** Conekta Elements includes both:
> - **`webApp/`** (You are here) - React + TypeScript for web
> - **`shared/`** - Kotlin Multiplatform SDK with Compose UI for Android/iOS
> 
> Both share the same features (Express Checkout, Tokenizer, Component) adapted to their platforms.

## 🎯 Overview

React + TypeScript library for payment processing with a focus on **Express Checkout** (Apple Pay / Google Pay auto-detection).

**Phase 1 (Current):** Express Checkout only  
**Future:** Tokenizer and Payment Component

### Why Express Checkout?

**Problem:**
- Merchants spend 2-3 weeks integrating each wallet (Apple Pay, Google Pay) separately
- Manual implementations often violate brand guidelines
- Inconsistent UX across merchants

**Solution:**
- Auto-detects device/browser and shows the right button (Apple Pay on iOS, Google Pay on Android)
- Guarantees compliance with Apple/Google brand guidelines
- Single integration for multiple wallets

## 🚀 Quick Start

```bash
cd webApp
npm install
cp env.example .env.local
# Add your Conekta public key to .env.local
npm run dev
```

### Basic Usage

```tsx
import { MantineProvider } from '@mantine/core';
import { ConektaProvider, ExpressCheckout } from '@conekta/elements';

function App() {
  return (
    <MantineProvider>
      <ConektaProvider publicKey="key_xxx" environment="sandbox">
        <ExpressCheckout
          amount={10000}              // Amount in cents
          currency="MXN"
          onPaymentCompleted={(result) => {
            console.log('Payment completed:', result);
          }}
        />
      </ConektaProvider>
    </MantineProvider>
  );
}
```

## 📁 Project Structure

```
src/
├── features/
│   └── express-checkout/        # Main feature (Phase 1)
│       ├── components/          # ExpressCheckout, ApplePayButton, GooglePayButton
│       ├── hooks/              # usePaymentMethod (auto-detection)
│       ├── store/              # Zustand state (if needed)
│       ├── utils/              # detectPaymentMethod
│       └── index.ts            # Public API
├── lib/                        # Formatters, constants
├── api/                        # HTTP client, endpoints
├── providers/                  # ConektaProvider
└── styles/                     # Mantine theme
```

## 🏗️ Architecture

**Feature-Based:** Everything related to Express Checkout lives in `/features/express-checkout/`.

**Why?**
- Easy to find code
- Easy to scale (add new features = new folders)
- Changes stay localized

**Tech Stack:**
- React 18 + TypeScript 5
- Mantine (Design System)
- Zustand (State Management)
- Vite (Build Tool)
- Vitest (Testing)

## 💻 Development

### Available Commands

```bash
npm run dev          # Start dev server
npm run build        # Build for production
npm test             # Run tests
npm run lint         # Lint code
npm run format       # Format code
```

### Feature Structure

Each feature exports only its public API:

```typescript
// features/express-checkout/index.ts
export { ExpressCheckout } from './components/ExpressCheckout';
export type { ExpressCheckoutProps } from './components/ExpressCheckout';
// Internal components, hooks, utils stay private
```

### Code Guidelines

- Use **Mantine** components as base
- Use **Zustand** for complex state (if needed)
- Keep TypeScript strict
- Export only public API via `index.ts`
- Write tests for components and logic

## 📚 Configuration

### Express Checkout Props

```typescript
interface ExpressCheckoutProps {
  // Required
  publicKey: string;
  amount: number;          // Cents
  currency: string;        // 'MXN', 'USD', etc.
  
  // Optional
  appearance?: {
    theme?: 'light' | 'dark' | 'auto';
    borderRadius?: string;
    height?: number;
    width?: number | string;
  };
  layout?: {
    type?: 'horizontal' | 'vertical';
    spacing?: number;
  };
  
  // Callbacks
  onPaymentMethodSelected?: (method: PaymentMethod) => void;
  onPaymentCompleted?: (result: PaymentResult) => void;
  onError?: (error: PaymentError) => void;
}
```

## 📖 Documentation

- **[.cursorrules](./.cursorrules)** - Coding guidelines

## 🔗 Resources

### Internal
- [Root README](../README.md) - Monorepo overview
- [Kotlin SDK](../shared/) - Mobile SDK with Compose UI

### External
- [Mantine](https://mantine.dev/) - Design system
- [Zustand](https://github.com/pmndrs/zustand) - State management
- [Conekta API](https://developers.conekta.com) - API documentation
- [Apple Pay Guidelines](https://developer.apple.com/design/human-interface-guidelines/apple-pay)
- [Google Pay Guidelines](https://developers.google.com/pay/api/web/guides/brand-guidelines)

## 📄 License

See [LICENSE](../LICENSE) in the root directory.

---

**Built with ❤️ by the Conekta Team**
