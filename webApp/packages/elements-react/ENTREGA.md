# Entrega — `@conekta/elements-react` (`webApp/packages/elements-react`)

Este paquete es el **wrapper React** de `@conekta/elements`.

## Objetivo

Dar una integración “React‑first” para inicializar Conekta Elements sin tocar DOM manualmente:

- Maneja el `container` internamente con un `ref`
- Llama `ConektaElements.init(...)` en `useEffect`
- Hace cleanup (`destroy()`) al desmontar

## API

- **`<ElementsProvider />`**
  - Provee el singleton `ConektaElements` vía contexto.
  - Hook: `useElements()` (evita imports directos en cada componente).

- **`<ConektaElement />`**
  - Componente genérico para cualquier `molecule`.
  - Props = `InitArgs` de `@conekta/elements` (sin `container`) + `className/style`.

- **Componentes específicos**
  - **`<ExpressCheckout />`** → `molecule="expressCheckout"`
  - **`<ApplePayButton />`** → `molecule="applePay"`

Todos aceptan:
- `checkoutRequestId`
- `locale`, `theme`
- callbacks `onInit`, `onSuccess`, `onError`

## Uso (ejemplo mínimo)

```tsx
import { ElementsProvider, ExpressCheckout } from '@conekta/elements-react';

export function Checkout() {
  return (
    <ElementsProvider>
      <ExpressCheckout
        checkoutRequestId="chkreq_..."
        locale="es"
        onSuccess={({ paymentMethod, payload }) => {
          console.log('OK', paymentMethod, payload);
        }}
        onError={({ error }) => {
          console.error('Error', error);
        }}
      />
    </ElementsProvider>
  );
}
```

## Qué hice aquí

- Implementé el “thin wrapper” alrededor de `@conekta/elements`:
  - `ElementsProvider` + `useElements`
  - `ConektaElement` genérico con lifecycle correcto (init + destroy)
  - componentes conveniencia para Apple Pay y Express Checkout

