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

---

## 📊 Matriz de Casos de Prueba

| ID | Tipo | Prioridad | Escenario | Precondición | Datos de Entrada | Resultado Esperado | Status Code | Estado TX |
|----|------|-----------|-----------|--------------|------------------|-------------------|-------------|-----------|
| **TC-001** | Happy Path | Alta | Crear transacción PSE válida | Credenciales válidas | Monto: 5000000 COP<br>Email: test@test.com<br>Banco: 1040<br>Tipo: PERSON | Transacción creada exitosamente | 201 | PENDING o APPROVED |
| **TC-002** | Negative | Alta | Auth con llave inválida | Llave inválida | Private Key: "INVALID_KEY" | Error de autenticación | 401 o 403 | N/A |
| **TC-003** | Negative | Media | Transacción sin monto | Credenciales válidas | amount_in_cents: null | Error de validación | 400 o 422 | N/A |

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

## 🔄 Escenarios Adicionales (Futuros)

### Escenarios Happy Path
| ID | Escenario | Prioridad |
|----|-----------|-----------|
| TC-004 | Transacción PSE con banco diferente (1007 - Bancolombia) | Media |
| TC-005 | Transacción PSE para usuario tipo BUSINESS | Media |
| TC-006 | Transacción con monto mínimo (50000 centavos) | Baja |
| TC-007 | Transacción con monto máximo permitido | Baja |
| TC-008 | Consultar estado de transacción (GET /transactions/:id) | Alta |

### Escenarios Negative Path
| ID | Escenario | Prioridad |
|----|-----------|-----------|
| TC-009 | Transacción con email inválido | Media |
| TC-010 | Transacción con código de banco inexistente | Media |
| TC-011 | Transacción con moneda diferente a COP | Baja |
| TC-012 | Transacción duplicada (misma referencia) | Alta |
| TC-013 | Transacción con monto negativo | Media |
| TC-014 | Transacción sin método de pago | Alta |

---

## 🧪 Estrategia de Pruebas

### Cobertura

**Funcional:**
- ✅ Flujo feliz (Happy Path)
- ✅ Manejo de errores (Negative Path)
- ✅ Validación de datos

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

---

## 📈 Métricas de Éxito

| Métrica | Objetivo | Actual |
|---------|----------|--------|
| **Cobertura de Funcionalidad** | > 80% | 60% (3/5 flujos principales) |
| **Tests Automatizados** | 100% de casos críticos | ✅ 5/5 |
| **Tasa de Éxito** | > 95% | Pendiente de ejecución |
| **Tiempo de Ejecución** | < 2 min | Pendiente de medición |

---

## 🗺️ Roadmap de Testing

### Fase 1: MVP (Actual) ✅
- [x] TC-001: Happy path PSE
- [x] TC-002: Auth error
- [x] TC-003: Validation error
- [x] Framework setup con ScreenPlay + BDD

### Fase 2: Extensión 📅
- [ ] TC-004 a TC-008: Más casos happy
- [ ] TC-009 a TC-014: Más casos negative
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

## ✅ Checklist de Ejecución

Antes de ejecutar:
- [ ] Verificar conectividad a API sandbox
- [ ] Validar llaves de autenticación
- [ ] Tener Java 11+ y Maven instalados
- [ ] Dependencias descargadas (`mvn clean install`)

Durante ejecución:
- [ ] Logs habilitados
- [ ] Captura de requests/responses
- [ ] Tiempo de ejecución medido

Después de ejecutar:
- [ ] Revisar reporte Serenity
- [ ] Verificar todos los tests pasaron
- [ ] Documentar cualquier bug encontrado
- [ ] Actualizar matriz de casos de prueba

---

## 📝 Notas

### Limitaciones Conocidas
- ❗ Ambiente sandbox puede tener latencia variable
- ❗ Algunas transacciones PSE quedan en PENDING indefinidamente en sandbox
- ❗ No se realizan cargos reales

### Consideraciones
- ✅ Tests idempotentes (cada ejecución genera nueva referencia)
- ✅ No requiere limpieza de datos (sandbox)
- ✅ Pueden ejecutarse en paralelo con precaución

---

**Fecha de Creación:** Mayo 2026  
**Última Actualización:** Mayo 2026  
**Versión:** 1.0  
**Estado:** Activo ✅

