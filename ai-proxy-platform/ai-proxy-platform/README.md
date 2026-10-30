# Plataforma de consumo de IA con Proxy de rate limiting y cuotas

Taller Patrón Proxy. Backend en Spring Boot 3 (Java 17) y frontend en React + Vite.

## Cadena de proxies

```
POST /api/ai/generate
        │
        ▼
RateLimitProxyService   → 429 Too Many Requests + header Retry-After
        │
        ▼
QuotaProxyService       → 402 Payment Required (descuenta tokens y registra consumo)
        │
        ▼
MockAIGenerationService → Thread.sleep(1200) + texto aleatorio predefinido
```

Las tres clases implementan `AIGenerationService.generate(GenerationRequest): GenerationResponse`.
`RateLimitProxyService` está marcado como `@Primary`, así que el controlador siempre recibe la cadena completa.

## Planes

| Plan       | Requests / min | Tokens / mes |
|------------|----------------|--------------|
| FREE       | 10             | 50.000       |
| PRO        | 60             | 500.000      |
| ENTERPRISE | Ilimitado      | Ilimitado    |

Tokens ≈ caracteres / 4 (prompt + respuesta).

## Endpoints

El usuario se identifica con el header `X-User-Id` (se crea automáticamente en plan FREE).

| Método | Ruta                  | Descripción |
|--------|-----------------------|-------------|
| POST   | `/api/ai/generate`    | `{ "prompt": "..." }` pasa por toda la cadena de proxies |
| GET    | `/api/quota/status`   | tokens usados, restantes, resetDate, plan, requests del minuto y segundos para el reset |
| GET    | `/api/quota/history`  | uso diario de los últimos 7 días |
| POST   | `/api/quota/upgrade`  | `{ "targetPlan": "PRO", "cardHolder": "...", "cardLastFour": "4242" }` pago simulado |
| GET    | `/api/quota/plans`    | catálogo de planes (extra, lo usa el modal) |

## Tareas programadas

- `0 * * * * *` → reinicia el contador de requests por minuto de todos los usuarios.
- `0 0 0 1 * *` → reinicia la cuota mensual de tokens el día 1 de cada mes (zona America/Bogota).

## Correr en local

```bash
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

## Despliegue

### Backend en Railway
1. Sube el repositorio a GitHub.
2. En Railway: **New Project → Deploy from GitHub repo**.
3. En **Settings → Root Directory** pon `backend`. Railway detecta el `Dockerfile`.
4. Variables (opcional): `CORS_ALLOWED_ORIGINS=https://tu-app.vercel.app`.
5. En **Settings → Networking → Generate Domain** para obtener la URL pública.
6. Prueba: `https://tu-backend.up.railway.app/api/quota/status`.

### Frontend en Vercel
1. **Add New → Project** e importa el mismo repositorio.
2. **Root Directory**: `frontend`. Framework: Vite.
3. Variable de entorno: `VITE_API_URL=https://tu-backend.up.railway.app` (sin `/` al final).
4. Deploy. Si cambias la variable, vuelve a desplegar.
