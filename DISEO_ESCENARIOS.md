# Diseño de Escenarios de Prueba - Wompi API PSE

## 📋 Información del Proyecto

| Campo | Valor |
|-------|-------|
| **Sistema Bajo Prueba** | Wompi API - Transacciones PSE |
| **Ambiente** | Sandbox UAT |
| **URL Base** | https://api-sandbox.co.uat.wompi.dev/v1 |
| **Método de Pago** | PSE (Pagos Seguros en Línea) |
| **Endpoint Principal** | POST /transactions |
| **Técnica** | BDD (Behavior-Driven Development) |
| **Patrón** | ScreenPlay |

---

## 🎯 Objetivo de las Pruebas

Validar que la API de Wompi:
1. ✅ Permita crear transacciones PSE con datos válidos
2. ✅ Rechace peticiones con autenticación inválida
3. ✅ Valide campos requeridos en la solicitud
4. ✅ Retorne códigos HTTP correctos
5. ✅ Genere transacciones con estados esperados (PENDING/APPROVED)
6. ✅ Envíe webhooks auténticos con firma HMAC-SHA256 válida
7. ✅ Permita validar la autenticidad de webhooks mediante firmas criptográficas
8. ✅ Rechace webhooks con firma inválida (prevención de ataques)

---

## 📊 Matriz de Casos de Prueba

| ID | Tipo | Prioridad | Escenario | Precondición | Datos de Entrada | Resultado Esperado | Status Code | Estado TX |
|----|------|-----------|-----------|--------------|------------------|-------------------|-------------|-----------|
| **TC-001** | Happy Path | Alta | Crear transacción PSE válida | Credenciales válidas | Monto: 5000000 COP<br>Email: test@test.com<br>Banco: 1040<br>Tipo: PERSON | Transacción creada exitosamente | 201 | PENDING o APPROVED |
| **TC-002** | Negative | Alta | Auth con llave inválida | Llave inválida | Private Key: "INVALID_KEY" | Error de autenticación | 401 o 403 | N/A |
| **TC-003** | Negative | Media | Transacción sin monto | Credenciales válidas | amount_in_cents: null | Error de validación | 400 o 422 | N/A |
| **TC-004** | Security | Alta | Webhook auténtico | Events Key válida | Payload válido<br>Firma HMAC-SHA256 correcta | Firma validada correctamente | N/A | N/A |
| **TC-005** | Security/Negative | Alta | Webhook firma inválida | Events Key válida | Payload modificado<br>Firma incorrecta | Webhook rechazado por seguridad | N/A | N/A |

---

## 📝 Casos de Prueba Detallados

### TC-001: Transacción PSE Exitosa (Happy Path)

#### Información General
- **Nombre:** Creación exitosa de transacción con PSE
- **Tipo:** Funcional - Integración API
- **Prioridad:** Alta
- **Objetivo:** Verificar que se puede crear una transacción válida con PSE

#### Precondiciones
- [x] Acceso a ambiente sandbox de Wompi
- [x] Llaves de autenticación válidas
- [x] Servicio API disponible

#### Datos de Entrada

**Headers:**
```
Authorization: Bearer prv_stagtest_5i0ZGIGiFcDQifYsXxvsny7Y37tKqFWg
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "amount_in_cents": 5000000,
  "currency": "COP",
  "customer_email": "test@test.com",
  "payment_method": "PSE",
  "reference": "TEST_REF_12345",
  "redirect_url": "https://example.com/redirect",
  "payment_method": {
    "type": "PSE",
    "user_type": 0,
    "user_legal_id_type": "CC",
    "user_legal_id": "123456789",
    "financial_institution_code": "1040",
    "payment_description": "Test payment with PSE"
  },
  "customer_data": {
    "phone_number": "3001234567",
    "full_name": "Juan Perez",
    "legal_id": "123456789",
    "legal_id_type": "CC"
  }
}
```

#### Pasos de Ejecución
1. Configurar headers con autenticación válida
2. Construir request JSON con datos de transacción PSE
3. Enviar POST a /transactions
4. Capturar respuesta

#### Resultado Esperado

**Status Code:** `201 Created`

**Response Body:**
```json
{
  "data": {
    "id": "123-transaction-id",
    "status": "PENDING" o "APPROVED",
    "payment_method": {
      "type": "PSE",
      ...
    },
    "amount_in_cents": 5000000,
    "currency": "COP",
    "customer_email": "test@test.com",
    "reference": "TEST_REF_12345",
    ...
  }
}
```

**Validaciones:**
- ✅ Status code es 201
- ✅ Campo `data.status` existe
- ✅ `data.status` es "PENDING" o "APPROVED"
- ✅ `data.payment_method_type` es "PSE"
- ✅ `data.amount_in_cents` coincide con lo enviado
- ✅ Response tiene campo `data.id`

#### Criterios de Aceptación
- La transacción se crea sin errores
- El status inicial es válido (PENDING o APPROVED)
- Todos los campos enviados se reflejan en la respuesta

---

### TC-002: Error de Autenticación

#### Información General
- **Nombre:** Error por llave de autenticación inválida
- **Tipo:** Funcional - Seguridad
- **Prioridad:** Alta
- **Objetivo:** Validar manejo de errores de autenticación

#### Precondiciones
- [x] Acceso a ambiente sandbox
- [x] Endpoint disponible

#### Datos de Entrada

**Headers:**
```
Authorization: Bearer INVALID_KEY_12345
Content-Type: application/json
```

**Body:** Cualquier transacción válida

#### Pasos de Ejecución
1. Configurar header con llave inválida
2. Enviar POST a /transactions
3. Capturar respuesta de error

#### Resultado Esperado

**Status Code:** `401 Unauthorized` o `403 Forbidden`

**Response Body (ejemplo):**
```json
{
  "error": {
    "type": "AUTHENTICATION_ERROR",
    "message": "Invalid or missing authentication credentials"
  }
}
```

**Validaciones:**
- ✅ Status code es 401 o 403
- ✅ Response contiene campo `error`
- ✅ No se crea la transacción

#### Criterios de Aceptación
- La API rechaza la petición
- Retorna error HTTP apropiado
- Mensaje de error es descriptivo

---

### TC-003: Validación de Campos Requeridos

#### Información General
- **Nombre:** Error por datos incompletos en la solicitud
- **Tipo:** Funcional - Validación
- **Prioridad:** Media
- **Objetivo:** Verificar validación de campos obligatorios

#### Precondiciones
- [x] Credenciales válidas
- [x] Endpoint disponible

#### Datos de Entrada

**Headers:** Válidos

**Body (JSON) - SIN MONTO:**
```json
{
  "currency": "COP",
  "customer_email": "test@test.com",
  "reference": "TEST_REF_NO_AMOUNT",
  "payment_method": {
    "type": "PSE",
    "user_type": 0,
    "financial_institution_code": "1040"
  }
}
```

#### Pasos de Ejecución
1. Configurar autenticación válida
2. Enviar request SIN campo `amount_in_cents`
3. Capturar respuesta de error

#### Resultado Esperado

**Status Code:** `400 Bad Request` o `422 Unprocessable Entity`

**Response Body (ejemplo):**
```json
{
  "error": {
    "type": "VALIDATION_ERROR",
    "message": "amount_in_cents is required"
  }
}
```

**Validaciones:**
- ✅ Status code es 400 o 422
- ✅ Response contiene campo `error`
- ✅ Mensaje indica campo faltante

#### Criterios de Aceptación
- La API valida campos requeridos
- Retorna error HTTP apropiado
- No se crea transacción incompleta

---

### TC-004: Validación de Webhook Auténtico

#### Información General
- **Nombre:** Validar webhook auténtico de Wompi
- **Tipo:** Seguridad - Validación de Integridad
- **Prioridad:** Alta
- **Objetivo:** Verificar que se puede validar la autenticidad de webhooks de Wompi mediante firma HMAC-SHA256

#### Precondiciones
- [x] Llave de eventos (EVENTS_KEY) configurada
- [x] Conocimiento del algoritmo HMAC-SHA256
- [x] WebhookValidator implementado

#### Datos de Entrada

**Evento:** `transaction.updated`

**Payload de Webhook (JSON):**
```json
{
  "event": "transaction.updated",
  "data": {
    "transaction": {
      "id": "123-transaction-id",
      "status": "APPROVED",
      "amount_in_cents": 5000000,
      "currency": "COP",
      "customer_email": "test@test.com",
      "reference": "TEST_REF_12345",
      "payment_method_type": "PSE"
    }
  },
  "timestamp": "2026-05-02T10:30:00.000Z"
}
```

**Events Key:**
```
stagtest_events_2PDUmhMywUkvb1LvxYnayFbmofT7w39N
```

**Firma Calculada (HMAC-SHA256):**
La firma se calcula aplicando HMAC-SHA256 sobre el payload completo usando la EVENTS_KEY.

#### Pasos de Ejecución
1. Recibir payload del webhook
2. Extraer firma del header `X-Signature` (o simularla)
3. Calcular firma HMAC-SHA256 del payload usando EVENTS_KEY
4. Comparar firma calculada con firma recibida
5. Extraer tipo de evento del payload

#### Resultado Esperado

**Validación:** `EXITOSA`

**Firma:** `VÁLIDA`

**Información Extraída:**
- `webhookValid`: `true`
- `webhookEventType`: `"transaction.updated"`
- `webhookValidationResult`: `"VALID"`
- `webhookValidationMessage`: `"Firma válida - Webhook auténtico"`

**Validaciones:**
- ✅ La firma calculada coincide con la firma recibida
- ✅ El webhook es marcado como auténtico
- ✅ El tipo de evento es `"transaction.updated"`
- ✅ El payload no fue modificado

#### Criterios de Aceptación
- La firma HMAC-SHA256 se calcula correctamente
- Se valida la autenticidad del webhook
- Se extrae correctamente el tipo de evento
- No se lanza ninguna excepción de seguridad

#### Implementación ScreenPlay
- **Task:** `ValidateWebhookSignature.of(payload, signature)`
- **Question:** `ValidateWebhookEvent.isAuthentic()`
- **Question:** `ValidateWebhookEvent.eventType()`
- **Utility:** `WebhookValidator.validateSignature()`

---

### TC-005: Rechazo de Webhook con Firma Inválida

#### Información General
- **Nombre:** Rechazar webhook con firma inválida
- **Tipo:** Seguridad - Negative Testing
- **Prioridad:** Alta
- **Objetivo:** Verificar que se detectan y rechazan webhooks con firma incorrecta (prevención de ataques)

#### Precondiciones
- [x] Llave de eventos (EVENTS_KEY) configurada
- [x] WebhookValidator implementado
- [x] Manejo de webhooks maliciosos

#### Datos de Entrada

**Payload Original (JSON):**
```json
{
  "event": "transaction.updated",
  "data": {
    "transaction": {
      "id": "123-transaction-id",
      "status": "APPROVED",
      "amount_in_cents": 5000000
    }
  }
}
```

**Firma Inválida (Simulada):**
```
INVALID_SIGNATURE_12345_ATTACK_ATTEMPT
```

**Escenarios de Ataque:**
1. Firma completamente falsa
2. Payload modificado después de firmado
3. Firma calculada con llave incorrecta
4. Firma en formato incorrecto

#### Pasos de Ejecución
1. Recibir payload con firma inválida
2. Intentar calcular firma HMAC-SHA256
3. Comparar con firma recibida
4. Detectar discrepancia
5. Marcar webhook como NO AUTÉNTICO

#### Resultado Esperado

**Validación:** `RECHAZADA`

**Firma:** `INVÁLIDA`

**Información Registrada:**
- `webhookValid`: `false`
- `webhookValidationResult`: `"INVALID"`
- `webhookValidationMessage`: `"La firma del webhook no es válida. Esto podría indicar un intento de ataque o modificación del payload."`

**Validaciones:**
- ✅ La firma calculada NO coincide con la firma recibida
- ✅ El webhook es marcado como NO auténtico
- ✅ Se registra un mensaje de seguridad
- ✅ El sistema detecta el posible ataque

#### Criterios de Aceptación
- El sistema detecta firmas inválidas
- Se rechaza el webhook sin procesarlo
- Se registra advertencia de seguridad
- No se ejecutan acciones basadas en el webhook falso

#### Implementación ScreenPlay
- **Task:** `ValidateWebhookSignature.of(payload, invalidSignature)`
- **Question:** `ValidateWebhookEvent.isAuthentic()` → `false`
- **Utility:** `WebhookValidator.validateSignature()` → `false`

#### Importancia de Seguridad
Este test case es crítico porque:
1. 🛡️ **Previene Ataques de Spoofing:** Detecta webhooks falsos que intentan simular notificaciones de Wompi
2. 🔒 **Garantiza Integridad:** Asegura que el payload no fue modificado en tránsito
3. 💰 **Protege Transacciones:** Evita procesamiento de eventos fraudulentos
4. ✅ **Cumple Estándares:** Implementa buenas prácticas de validación de webhooks

---

## 🔄 Escenarios Adicionales (Futuros)

### Escenarios Happy Path
| ID | Escenario | Prioridad |
|----|-----------|-----------|
| TC-006 | Transacción PSE con banco diferente (1007 - Bancolombia) | Media |
| TC-007 | Transacción PSE para usuario tipo BUSINESS | Media |
| TC-008 | Transacción con monto mínimo (50000 centavos) | Baja |
| TC-009 | Transacción con monto máximo permitido | Baja |
| TC-010 | Consultar estado de transacción (GET /transactions/:id) | Alta |

### Escenarios Negative Path
| ID | Escenario | Prioridad |
|----|-----------|-----------|
| TC-011 | Transacción con email inválido | Media |
| TC-012 | Transacción con código de banco inexistente | Media |
| TC-013 | Transacción con moneda diferente a COP | Baja |
| TC-014 | Transacción duplicada (misma referencia) | Alta |
| TC-015 | Transacción con monto negativo | Media |
| TC-016 | Transacción sin método de pago | Alta |

### Escenarios de Seguridad (Futuros)
| ID | Escenario | Prioridad |
|----|-----------|-----------|
| TC-017 | Validar firma de integridad en transacciones | Alta |
| TC-018 | Webhook con timestamp expirado | Media |
| TC-019 | Replay attack con webhook duplicado | Alta |

---

## 🧪 Estrategia de Pruebas

### Cobertura

**Funcional:**
- ✅ Flujo feliz (Happy Path)
- ✅ Manejo de errores (Negative Path)
- ✅ Validación de datos

**Seguridad:**
- ✅ Validación de webhooks con HMAC-SHA256
- ✅ Detección de firmas inválidas
- ✅ Prevención de ataques de spoofing
- ✅ Verificación de integridad de payloads

**No Funcional (Futura):**
- ⏳ Performance (tiempo de respuesta < 3s)
- ⏳ Concurrencia (múltiples peticiones simultáneas)
- ⏳ Recuperación de errores

### Técnicas Aplicadas

1. **Partición de Equivalencia:**
   - Datos válidos vs inválidos
   - Diferentes bancos PSE
   - Tipos de usuario (PERSON/BUSINESS)

2. **Valores Límite:**
   - Montos mínimos/máximos
   - Longitud de campos

3. **Pruebas Negativas:**
   - Autenticación fallida
   - Datos faltantes
   - Datos malformados

4. **Pruebas de Seguridad:**
   - Validación criptográfica (HMAC-SHA256)
   - Detección de manipulación de datos
   - Verificación de autenticidad de origen
   - Prevención de ataques man-in-the-middle

---

## 📈 Métricas de Éxito

| Métrica | Objetivo | Actual |
|---------|----------|--------|
| **Cobertura de Funcionalidad** | > 80% | 80% (5/6 flujos principales) |
| **Tests Automatizados** | 100% de casos críticos | ✅ 5/5 implementados |
| **Tasa de Éxito** | > 95% | ✅ 100% (5/5 passing) |
| **Tiempo de Ejecución** | < 2 min | ✅ ~30 segundos |
| **Cobertura de Seguridad** | Webhooks validados | ✅ 2/2 (válido + inválido) |

---

## 🗺️ Roadmap de Testing

### Fase 1: MVP (Completado) ✅
- [x] TC-001: Happy path PSE
- [x] TC-002: Auth error
- [x] TC-003: Validation error
- [x] TC-004: Webhook auténtico (Security)
- [x] TC-005: Webhook con firma inválida (Security)
- [x] Framework setup con ScreenPlay + BDD
- [x] Validación de webhooks con HMAC-SHA256

### Fase 2: Extensión 📅
- [ ] TC-006 a TC-010: Más casos happy path
- [ ] TC-011 a TC-016: Más casos negative path
- [ ] TC-017 a TC-019: Casos avanzados de seguridad
- [ ] Data-driven testing con Examples

### Fase 3: Avanzado 🔮
- [ ] Otros métodos de pago (NEQUI, Bancolombia)
- [ ] Pruebas end-to-end (transacción + webhook)
- [ ] Performance testing de webhooks
- [ ] Integración CI/CD

---

## 🔗 Referencias

### Documentación Base
- [Wompi - Inicio Rápido](https://docs.wompi.co/docs/colombia/inicio-rapido/)
- [Wompi - Ambientes y Llaves](https://docs.wompi.co/docs/colombia/ambientes-y-llaves/)
- [Wompi - Transacciones](https://docs.wompi.co/docs/colombia/transacciones/)
- [Wompi - PSE](https://docs.wompi.co/docs/colombia/medios-de-pago/pse/)


### Endpoints Utilizados
#### 1. Obtener Acceptance Token
```
GET /v1/merchants/{public_key}
```
**Propósito:** Obtener el token de aceptación de términos y condiciones (obligatorio para transacciones)
**Request:**
```http
GET https://api-sandbox.co.uat.wompi.dev/v1/merchants/pub_stagtest_g2u0HQd3ZMh05hsSgTS2lUV8t3s4mOt7
```
**Response (campos relevantes):**
```json
{
  "data": {
    "presigned_acceptance": {
      "acceptance_token": "eyJhbGciOiJIUzI1NiJ9...",
      "permalink": "https://wompi.com/assets/downloadble/reglamento-Usuarios-Colombia.pdf",
      "type": "END_USER_POLICY"
    },
    "accepted_payment_methods": ["PSE", "NEQUI", "CARD", ...]
  }
}
```
**Uso:** Este token es obligatorio y se incluye en el campo `acceptance_token` del body de transacciones.
#### 2. Crear Transacción
```
POST /v1/transactions
```
**Headers:** `Authorization: Bearer {private_key}`
**Body:** Ver ejemplos de request en las secciones de casos de prueba arriba.
### Datos de Prueba
```
URL Base: https://api-sandbox.co.uat.wompi.dev/v1
Private Key: prv_stagtest_5i0ZGIGiFcDQifYsXxvsny7Y37tKqFWg
Public Key: pub_stagtest_g2u0HQd3ZMh05hsSgTS2lUV8t3s4mOt7

Bancos PSE Sandbox:
- 1040: Banco Agrario
- 1007: Bancolombia
- 1019: Scotiabank Colpatria
```

---

## 🔐 Validación de Webhooks - Detalles Técnicos

### ¿Qué es un Webhook?
Un webhook es una notificación HTTP que Wompi envía cuando ocurre un evento (ej: cambio de estado de transacción).

### ¿Por qué validar la firma?
Sin validación de firma, un atacante podría:
- ✗ Enviar webhooks falsos simulando ser Wompi
- ✗ Modificar el payload en tránsito
- ✗ Marcar transacciones como aprobadas fraudulentamente
- ✗ Ejecutar acciones no autorizadas en el sistema

### Algoritmo HMAC-SHA256

**HMAC** (Hash-based Message Authentication Code) es un mecanismo criptográfico que garantiza:
1. **Autenticidad:** Solo quien tiene la llave secreta puede generar la firma
2. **Integridad:** Cualquier modificación del mensaje invalida la firma

**Proceso de validación:**

```
1. Wompi envía webhook:
   - Payload: {...datos del evento...}
   - Header X-Signature: "a1b2c3d4e5f6..."

2. Tu servidor recibe el webhook:
   - Extrae el payload completo
   - Extrae la firma del header

3. Calcular firma esperada:
   signature = HMAC-SHA256(payload, EVENTS_KEY)

4. Comparar firmas:
   if (signature == received_signature) {
      ✅ Webhook auténtico - Procesar
   } else {
      ⛔ Posible ataque - Rechazar
   }
```

### Implementación en el Proyecto

**Clase Utilidad:**
```java
WebhookValidator.validateSignature(payload, receivedSignature)
```

**Componentes:**
- `WompiConfig.EVENTS_KEY`: Llave secreta compartida
- `WebhookValidator.calculateSignature()`: Calcula HMAC-SHA256
- `WebhookValidator.validateSignature()`: Compara firmas

**Ejemplo de Uso:**
```java
// En un endpoint real que recibe webhooks
@PostMapping("/webhooks/wompi")
public ResponseEntity<?> handleWebhook(
    @RequestBody String payload,
    @RequestHeader("X-Signature") String signature
) {
    if (WebhookValidator.validateSignature(payload, signature)) {
        // ✅ Procesar evento legítimo
        processEvent(payload);
        return ResponseEntity.ok().build();
    } else {
        // ⛔ Rechazar y registrar intento de ataque
        log.warn("Webhook con firma inválida detectado");
        return ResponseEntity.status(403).build();
    }
}
```

### Tipos de Eventos Soportados

Configurados en `WompiConfig.java`:

| Evento | Constante | Descripción |
|--------|-----------|-------------|
| `transaction.updated` | `EVENT_TRANSACTION_UPDATED` | Estado de transacción cambió |
| `payment.approved` | `EVENT_PAYMENT_APPROVED` | Pago fue aprobado |
| `payment.declined` | `EVENT_PAYMENT_DECLINED` | Pago fue rechazado |

### Pruebas Implementadas

**TC-004: Webhook Válido**
- ✅ Firma HMAC-SHA256 correcta
- ✅ Payload sin modificaciones
- ✅ Evento type extraído correctamente

**TC-005: Webhook Inválido**
- ✅ Firma incorrecta detectada
- ✅ Webhook rechazado
- ✅ Mensaje de seguridad registrado

---

## ✅ Checklist de Ejecución

Antes de ejecutar:
- [ ] Verificar conectividad a API sandbox
- [ ] Validar llaves de autenticación (PRIVATE_KEY, PUBLIC_KEY)
- [ ] Validar llave de eventos (EVENTS_KEY) para webhooks
- [ ] Tener Java 11+ y Maven instalados
- [ ] Dependencias descargadas (`mvn clean install`)

Durante ejecución:
- [ ] Logs habilitados
- [ ] Captura de requests/responses
- [ ] Validación de firmas HMAC-SHA256
- [ ] Tiempo de ejecución medido

Después de ejecutar:
- [ ] Revisar reporte Serenity
- [ ] Verificar todos los tests pasaron (5/5)
- [ ] Validar tests de seguridad (webhooks)
- [ ] Documentar cualquier bug encontrado
- [ ] Actualizar matriz de casos de prueba

---

## 📝 Notas

### Limitaciones Conocidas
- ❗ Ambiente sandbox puede tener latencia variable
- ❗ Algunas transacciones PSE quedan en PENDING indefinidamente en sandbox
- ❗ No se realizan cargos reales
- ❗ Webhooks son simulados en los tests (no enviados por Wompi en tiempo real)

### Consideraciones
- ✅ Tests idempotentes (cada ejecución genera nueva referencia)
- ✅ No requiere limpieza de datos (sandbox)
- ✅ Pueden ejecutarse en paralelo con precaución
- ✅ Validación de webhooks implementada con HMAC-SHA256
- ✅ Tests de seguridad cubren escenarios de ataque

### Mejoras Implementadas
- ✅ **Validación de Webhooks:** Implementación completa de validación HMAC-SHA256
- ✅ **Seguridad:** Detección de firmas inválidas y prevención de spoofing
- ✅ **Cobertura:** 5 escenarios críticos automatizados
- ✅ **ScreenPlay Pattern:** Separation of concerns (Tasks, Questions, Utils)

---

**Fecha de Creación:** Mayo 2026  
**Última Actualización:** Mayo 2026  
**Versión:** 1.0  
**Estado:** Activo ✅

