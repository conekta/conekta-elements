Contexto del proyecto – Payments SDK / Conekta Elements
Objetivo

Construir una plataforma de pagos embebible tipo Stripe Elements, pero más flexible, que permita:

Montar métodos de pago independientes

Componer experiencias de pago personalizadas

Cumplir PCI (el merchant nunca toca datos sensibles)

Soportar Web (React/Vue/Vanilla/CDN) y Mobile (Android/iOS)

Arquitectura general (decisiones finales)
Separación por repositorios

Hay dos repos principales:

Elements Core (este repo)

Headless, sin UI

Compartido entre Web y Mobile

Contiene:

Domain (state machines, reglas, validaciones)

Application pura (reducers + efectos declarativos)

Contratos (models, payloads)

Cliente de red compartido (KMP + Ktor)

SDK Mobile (Android/iOS)

Checkout-FE (repo separado)

UI web con SSR

Define rutas por método:

/method/cash

/method/bank-transfer

/method/bnpl

etc.

Esas rutas se cargan en iframes

Consume elements-core como librería instalable

Web runtime
Piezas

Merchant App (React/Vue/Vanilla)

Orchestrator SDK (host runtime)

Method Iframes (SSR Checkout-FE)

Principios

Cada método vive en su propio iframe

Todos los iframes son mismo dominio

Los métodos:

ejecutan su lógica

llaman al backend

devuelven resultados

El orquestador:

monta iframes

activa/desactiva métodos

controla el estado global

decide qué vista mostrar

emite analytics

comunica vía postMessage (RPC + events)

Modelo de control (clave)

Modelo B (decisión final)

El método decide el resultado (succeeded, failed, requires_action)

El orquestador decide el estado global y la UI

form

shipping

submitting

success

error

Beneficios:

UX consistente

Analytics centralizados

Compatible con lazy iframes + keep-alive

Alineado con mobile

Preparado para restore futuro (F5)

Comunicación Host ↔ Iframes

RPC (host → método)

submit()

resume()

Eventos (método → host)

READY

STATE

ACTION_REQUIRED

RESULT { status, payload }

Tree-shaking (requisito explícito)

El core debe ser tree-shakeable por método

El orquestador también

Cada iframe importa solo lo de su método

Diseño:

Nada de “god modules”

Entry points explícitos:

@conekta/elements-core/cash

@conekta/elements-core/bank-transfer

@conekta/elements-orchestrator

Clean-ish architecture (muy importante)
Domain (compartido)

modelos

state machines

validaciones

reglas deterministas

normalización de errores

Application (compartido parcialmente)

⚠️ NO use cases

Solo:

reducers / state machines puras

definición declarativa de efectos (qué hacer, no cómo)

Ejemplo de effect:

CREATE_PAYMENT_SOURCE

CREATE_ORDER

No ejecutan IO, no conocen UI ni plataforma.

Infrastructure (por plataforma)

implementación real de networking

lifecycle

adapters

Presentation

Solo en Checkout-FE (web UI) y mobile UI

Qué NO se comparte (muy claro)

UI

DOM / WebView

postMessage / RPC runtime

analytics sender

Orquestador runtime

Use cases

El orquestador no se comparte porque es platform-specific
(iframes/DOM en web, pantallas nativas en mobile)

Shared Networking (decisión importante)

Se comparte cliente de red usando Kotlin Multiplatform + Ktor

Exportado a JS vía @JsExport

Usado por:

SDK Mobile

SSR web (iframes)

Ejemplo conceptual:

ConektaJsClient

headers

auth

versioning

endpoints (getOrder, createOrder, etc.)

⚠️ No se comparten use cases ni lógica de flujo.

Analytics

Un solo emisor: el orquestador

Shared define:

nombres de eventos

códigos de error

helpers de payload

Web/Mobile implementan envío real (GTM, Firebase, etc.)

Métodos iniciales: Cash + BankTransfer

Comparten:

misma state machine

mismas validaciones

mismo flujo base

Diferencian:

payload de success

UI/copy

Shipping gate (shared)

Tras submit de buyer:

si needsShippingContact = true → pedir shipping

si no → continuar flujo

Lógica vive en domain compartido

Estado actual

Arquitectura definida

Diagramas claros (C4, capas, secuencia)

Restore/F5 y flows complejos no implementados aún (intencional)

Regla de oro final

Compartir domain + contratos + networking
NO compartir UI ni use cases
Orquestador manda
Métodos ejecutan

Contexto de avance actual:

Estamos implementando el core de Conekta Elements según esta arquitectura, ya tenemos los dos repositorios, ya hemos implementado la parte de orchestrator y payment methods (de momento solo con apple pay), este es el resumen de lo implementado usando zoid:

Contexto resumido — Zoid + Orchestrator + Payment Methods
1. Regla clave de Zoid (la más importante)

Zoid requiere que la definición del componente (zoid.create) se cargue tanto en el padre como en el hijo, usando exactamente el mismo tag.

Si el child no carga la definición, window.xprops será undefined y no habrá comunicación.

No es un workaround: es requisito de Zoid.

El tag es el identificador del canal RPC.

Conclusión:

La definición del componente Zoid debe vivir en un módulo compartido (o duplicado idéntico) que se ejecute en ambos lados.

2. Separación correcta de responsabilidades
🧠 Orchestrator (HOST / PARENT)

Vive en una lib instalable (npm / CDN).

Crea y monta iframes vía Zoid.

Decide:

qué método está activo

cuándo hacer submit

qué viewState se muestra

Nunca vive en el SSR del child.

Usa factories por método (tree-shakable).

const orchestrator = createOrchestrator({ baseUrl });

orchestrator.mount('applePay', '#container', { locale: 'es' });
orchestrator.setActive('applePay');
orchestrator.submit();

💳 Payment Method (Apple Pay, Cash, etc.)

Cada método tiene 3 piezas:

1️⃣ Zoid Component Definition (COMPARTIDA)

Archivo puro que hace solo:

zoid.create({
  tag: 'conekta-apple-pay-button',
  props: {...},
  exports: {...}
});


No lógica de orquestación

No side effects

Se ejecuta:

en el host (para montar)

en el child (para recibir xprops)

2️⃣ Factory (HOST)

Envuelve el componente Zoid y lo conecta al orchestrator:

export const createApplePayFactory: MethodFactory = (ctx) => {
  const ApplePayComponent = createApplePayComponent(ctx.baseUrl);
  return (props) => ApplePayComponent(props);
};


El ctx incluye baseUrl

Se registra vía registerMethodFactory

Tree-shakable por método

3️⃣ Child implementation (SSR / iframe)

En la página del iframe:

Cargar la definición Zoid

<script src="apple-pay.min.js"></script>


Acceder a window.xprops

Exportar RPC al padre:

window.xprops.export({
  submit,
  reset,
  setViewState,
  setActive,
  destroy
});

3. Comunicación (RPC)

Data down: props (locale, theme, callbacks)

Actions up: onReady, onStateChange, onResult

Commands: via RPC (submit, setActive, etc.)

// Parent
instance.submit()

// Child
window.xprops.export({ submit: async () => {...} })

4. baseUrl (detalle crítico)

baseUrl no debe leerse en zoid.create directamente

Zoid evalúa url en tiempo de creación

Solución correcta:

export const createApplePayComponent = (baseUrl: string) =>
  zoid.create({
    url: `${baseUrl}/elements/apple-pay`,
    ...
  });


Y en el factory:

const ApplePayComponent = createApplePayComponent(ctx.baseUrl);

5. Problema process is not defined (runtime)

Causa:

Zoid (o deps) usan process.env

El bundle corre en browser (iframe / CDN)

Fix mínimo (correcto):

Inyectar process en el bundle que corre en browser:

define: {
  'process.env': {},
  'process.env.NODE_ENV': '"production"',
  process: { env: {} }
}


O como banner:

var process = { env: {} };


📌 Esto debe hacerse en el build del SSR child, no solo en la lib.

6. Qué NO hacer

❌ Importar el orchestrator dentro del child
❌ Definir zoid.create solo en un lado
❌ Compartir lógica de UI o estado entre host y child
❌ Hacer que el child decida success/error global
❌ Meter todo en un solo bundle gigante

7. Tree-shaking correcto

Cada método exporta su propio registerX

El orchestrator solo registra lo que se importa

Subpaths tipo:

import { registerApplePay } from '.../applePay/register';

8. Modelo mental correcto

Zoid = transporte RPC

Orchestrator = state machine + UX coordinator

Payment method = flujo aislado

Iframe = boundary de seguridad

SSR child = implementación visual + side effects