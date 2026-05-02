# Documentación del Proyecto - Wompi Automation con ScreenPlay & BDD

## 📖 Resumen Ejecutivo

Este proyecto implementa pruebas automatizadas para la API de Wompi (plataforma de pagos) utilizando:
- **Patrón de Diseño:** ScreenPlay
- **Metodología:** BDD (Behavior-Driven Development)
- **Framework:** Serenity BDD + Cucumber
- **Lenguaje:** Java 11
- **Método de Pago:** PSE (Pagos Seguros en Línea)

---

## 🎭 ¿Qué es el Patrón ScreenPlay?

### Definición
ScreenPlay es un patrón de diseño para pruebas automatizadas creado por **Antony Marcano** y popularizado por **John Ferguson Smart** (creador de Serenity BDD).

### Filosofía
> **"Los tests deben describir QUÉ hace el sistema, no CÓMO lo hace"**

### Componentes Principales

#### 1. **Actors (Actores)** 🎭
- **¿Qué son?** Representan a los usuarios o sistemas que interactúan con la aplicación
- **En este proyecto:**
  ```java
  Actor merchant = Actor.named("Comercio Wompi")
                       .whoCan(CallAnApi.at(WompiConfig.BASE_URL));
  ```
- **Analogía:** Como un actor de teatro que tiene un rol específico

#### 2. **Abilities (Habilidades)** 💪
- **¿Qué son?** Capacidades que tiene un actor para interactuar con el sistema
- **En este proyecto:**
  ```java
  .whoCan(CallAnApi.at(WompiConfig.BASE_URL))  // Habilidad de llamar APIs
  ```
- **Otros ejemplos:** BrowseTheWeb, TakeNotes, etc.

#### 3. **Tasks (Tareas)** ✅
- **¿Qué son?** Acciones de alto nivel que un actor puede realizar
- **Características:**
  - Implementan la interfaz `Task`
  - Método `performAs(Actor)`
  - Describen acciones de negocio

- **En este proyecto:**
  ```java
  // Archivo: tasks/CreateTransaction.java
  public class CreateTransaction implements Task {
      
      public static CreateTransaction withDefaultPSEData() {
          return instrumented(CreateTransaction.class, ...);
      }
      
      @Override
      public <T extends Actor> void performAs(T actor) {
          // Construir request
          // Hacer POST a API
      }
  }
  ```

- **Uso:**
  ```java
  merchant.attemptsTo(CreateTransaction.withDefaultPSEData());
  ```

#### 4. **Interactions (Interacciones)** 🔄
- **¿Qué son?** Acciones de bajo nivel con el sistema
- **En este proyecto:**
  ```java
  Post.to("/transactions")            // Interacción POST
  Get.resource("/transactions/123")   // Interacción GET
  ```
- **Diferencia con Tasks:** Interactions son atómicas, Tasks combinan varias interactions

#### 5. **Questions (Preguntas)** ❓
- **¿Qué son?** Consultas para obtener información del sistema y validar
- **Características:**
  - Implementan `Question<T>`
  - Retornan valores que se pueden validar
  - No modifican el estado

- **En este proyecto:**
  ```java
  // Archivo: questions/ValidateResponse.java
  public class ValidateResponse {
      
      public static Question<Integer> statusCode() {
          return actor -> {
              Response response = SerenityRest.lastResponse();
              return response.getStatusCode();
          };
      }
      
      public static Question<String> transactionStatus() {
          return actor -> {
              Response response = SerenityRest.lastResponse();
              return response.jsonPath().getString("data.status");
          };
      }
  }
  ```

- **Uso:**
  ```java
  merchant.should(
      seeThat("El código de estado", 
              ValidateResponse.statusCode(), 
              is(201))
  );
  ```

### Ventajas de ScreenPlay

| Aspecto | Page Object Pattern | ScreenPlay Pattern |
|---------|--------------------|--------------------|
| **Enfoque** | Orientado a páginas/componentes | Orientado a comportamiento del usuario |
| **Legibilidad** | Técnica | Natural, como lenguaje humano |
| **Reutilización** | Limitada | Alta (Tasks composables) |
| **Mantenibilidad** | Dependiente de la UI | Independiente de implementación |
| **Reporting** | Básico | Rico en contexto |

**Ejemplo comparativo:**

```java
// Page Object
loginPage.enterUsername("user");
loginPage.enterPassword("pass");
loginPage.clickSubmit();

// ScreenPlay
actor.attemptsTo(
    Login.withCredentials("user", "pass")
);
```

---

## 🥒 ¿Qué es BDD (Behavior-Driven Development)?

### Definición
BDD es una metodología de desarrollo que extiende TDD (Test-Driven Development) enfocándose en el **comportamiento del sistema desde la perspectiva del negocio**.

### Principios
1. **Lenguaje Ubicuo:** Tests escritos en lenguaje natural (Gherkin)
2. **Colaboración:** Negocio, QA y Desarrollo trabajan juntos
3. **Ejemplos Vivos:** Los tests son documentación ejecutable

### Gherkin
Lenguaje DSL (Domain-Specific Language) para escribir escenarios

**Estructura:**
```gherkin
Feature: Descripción de alto nivel
  
  Scenario: Caso específico
    Given [Contexto inicial]
    When [Acción]
    Then [Resultado esperado]
    And [Paso adicional]
```

**En este proyecto:**
```gherkin
# Archivo: payment.feature
Característica: Integración con API de Wompi para transacciones PSE
  
  Escenario: Creación exitosa de transacción con PSE
    Dado que tengo credenciales válidas de Wompi
    Cuando creo una transacción con el medio de pago PSE
    Entonces la respuesta debe ser exitosa
    Y el estado de la transacción debe ser "PENDING" o "APPROVED"
```

### Cucumber
Framework que ejecuta escenarios Gherkin vinculándolos con código Java

**Step Definitions:**
```java
// Archivo: stepdefinitions/PaymentStepDefinitions.java

@Given("que tengo credenciales válidas de Wompi")
public void validCredentials() {
    // Setup
}

@When("creo una transacción con el medio de pago PSE")
public void createTransaction() {
    merchant.attemptsTo(CreateTransaction.withDefaultPSEData());
}

@Then("la respuesta debe ser exitosa")
public void validateResponse() {
    merchant.should(seeThat(ValidateResponse.statusCode(), is(201)));
}
```

---

## 🏗️ Arquitectura del Proyecto

### Estructura de Directorios

```
wompi-automation/
├── pom.xml                          # Dependencias Maven
├── serenity.properties              # Configuración Serenity
├── GUIA_PRESENTACION.md             # Guía para la presentación
├── DOCUMENTACION_PROYECTO.md        # Este archivo
│
└── src/
    └── test/
        ├── java/
        ├── java/
        │   ├── models/                    # 📦 Modelos de datos
        │   │   ├── TransactionRequest.java
        │   │   ├── PaymentMethod.java
        │   │   └── CustomerData.java
        │   │
        │   ├── tasks/                     # ✅ Tareas (ScreenPlay)
        │   │   ├── CreateTransaction.java
        │   │   ├── CreateTransactionWithInvalidCredentials.java
        │   │   ├── CreateTransactionWithoutAmount.java
        │   │   └── GetAcceptanceToken.java
        │   │
        │   ├── questions/                 # ❓ Preguntas (ScreenPlay)
        │   │   └── ValidateResponse.java
        │   │
        │   ├── utils/                     # 🔧 Utilidades
        │   │   └── WompiConfig.java
        │   │
        │   ├── stepdefinitions/           # 🥒 Steps Cucumber
        │   │   └── PaymentStepDefinitions.java
        │   │
        │   └── runners/                   # 🏃 Ejecutores
        │       └── PaymentRunner.java
        │
        └── resources/
            └── features/                  # 📝 Escenarios BDD
                └── payment.feature
```

### Flujo de Ejecución

```
┌─────────────────────────────────────────────────────────────┐
│ 1. FEATURE FILE (Gherkin)                                   │
│    payment.feature                                          │
│    "Cuando creo una transacción con el medio de pago PSE"  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. STEP DEFINITION (Cucumber)                               │
│    PaymentStepDefinitions.java                              │
│    @When("creo una transacción...")                         │
│    merchant.attemptsTo(CreateTransaction...)                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. ACTOR (ScreenPlay)                                       │
│    Actor merchant = Actor.named("Comercio Wompi")           │
│    .whoCan(CallAnApi...)                                    │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. TASK (ScreenPlay)                                        │
│    CreateTransaction.java                                   │
│    - Obtiene acceptance_token                               │
│    - Calcula signature                                      │
│    - Construye TransactionRequest                           │
│    - Usa PaymentMethod (PSE)                                │
│    - Hace POST a /transactions con SerenityRest             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. INTERACTION (Serenity REST)                              │
│    Post.to("/transactions")                                 │
│    .with(request -> request.header(...).body(...))          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. WOMPI API                                                │
│    POST https://api-sandbox.co.uat.wompi.dev/v1/transactions│
│    Response: { "data": { "status": "PENDING", ... }}        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 7. QUESTION (ScreenPlay)                                    │
│    ValidateResponse.statusCode()                            │
│    Returns: 201                                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 8. ASSERTION                                                │
│    merchant.should(seeThat(..., is(201)))                   │
│    ✓ PASS                                                   │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 9. SERENITY REPORT                                          │
│    HTML report with detailed steps                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Implementación Detallada

### 1. Configuración (WompiConfig.java)

**Ubicación:** `src/test/java/utils/WompiConfig.java`

**Propósito:** Centralizar todas las configuraciones de la API de Wompi

```java
public class WompiConfig {
    // URLs
    public static final String BASE_URL = "https://api-sandbox.co.uat.wompi.dev/v1";
    
    // Endpoints
    public static final String TRANSACTIONS_ENDPOINT = "/transactions";
    
    // Llaves de autenticación
    public static final String PRIVATE_KEY = "prv_stagtest_5i0ZGIGiFcDQifYsXxvsny7Y37tKqFWg";
    
    // Datos de prueba PSE
    public static final String PSE_PAYMENT_METHOD = "PSE";
    public static final String PSE_USER_TYPE = "PERSON";
    public static final String PSE_FINANCIAL_INSTITUTION_CODE = "1040"; // Banco Agrario
}
```

**Implementación en el proyecto:** ✅ Completado

---

### 1b. WebhookValidator (utils/)

**Ubicación:** `src/test/java/utils/WebhookValidator.java`

**Propósito:** Validar la autenticidad e integridad de webhooks enviados por Wompi

**¿Qué son los Webhooks de Wompi?**

Los webhooks son notificaciones HTTP POST que Wompi envía automáticamente cuando:
- 📊 Una transacción cambia de estado (PENDING → APPROVED)
- ✅ Un pago es aprobado
- ❌ Un pago es rechazado
- 🔄 Ocurren eventos importantes en transacciones

**¿Por qué validar la firma?**

Cada webhook incluye una firma HMAC-SHA256 que permite:
1. **Autenticidad:** Verificar que el webhook proviene realmente de Wompi
2. **Integridad:** Confirmar que el payload no fue modificado en tránsito
3. **Seguridad:** Prevenir ataques de spoofing o replay

**Implementación:**

```java
public class WebhookValidator {
    
    // Valida la firma del webhook
    public static boolean validateSignature(String payload, String receivedSignature) {
        String calculatedSignature = calculateSignature(payload);
        return calculatedSignature.equals(receivedSignature);
    }
    
    // Calcula HMAC-SHA256 usando EVENTS_KEY
    public static String calculateSignature(String payload) {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
            WompiConfig.EVENTS_KEY.getBytes(),
            "HmacSHA256"
        );
        mac.init(secretKey);
        byte[] hash = mac.doFinal(payload.getBytes());
        return bytesToHex(hash);
    }
    
    // Extrae el tipo de evento
    public static String extractEventType(String payload) {
        // Parse JSON y retorna "event" field
    }
}
```

**Ejemplo de uso:**

```java
// Recibir webhook de Wompi
String webhookPayload = request.getBody(); // JSON completo
String signature = request.getHeader("X-Signature");

// Validar autenticidad
if (WebhookValidator.validateSignature(webhookPayload, signature)) {
    // ✓ Webhook legítimo de Wompi
    String eventType = WebhookValidator.extractEventType(webhookPayload);
    processEvent(eventType, webhookPayload);
} else {
    // ✗ Posible ataque - rechazar webhook
    logSecurityIncident();
}
```

**Tipos de eventos soportados:**

| Evento | Constante | Cuándo ocurre |
|--------|-----------|---------------|
| `transaction.updated` | `EVENT_TRANSACTION_UPDATED` | Transacción cambia de estado |
| `payment.approved` | `EVENT_PAYMENT_APPROVED` | Pago aprobado exitosamente |
| `payment.declined` | `EVENT_PAYMENT_DECLINED` | Pago rechazado |

**Estructura típica de un webhook:**

```json
{
  "event": "transaction.updated",
  "data": {
    "transaction": {
      "id": "123-uuid",
      "status": "APPROVED",
      "amount_in_cents": 5000000,
      ...
    }
  },
  "sent_at": "2026-05-02T10:30:00Z",
  "timestamp": 1714650600,
  "signature": {
    "checksum": "abc123..."
  }
}
```

**Configuración:**

La llave de eventos está en `WompiConfig.java`:
```java
public static final String EVENTS_KEY = "stagtest_events_2PDUmhMywUkvb1LvxYnayFbmofT7w39N";
```

**Casos de uso en producción:**

1. **Actualización de base de datos:** Al recibir `transaction.updated`, actualizar estado en BD
2. **Notificaciones al usuario:** Enviar email/SMS cuando `payment.approved`
3. **Reversiones:** Manejar `payment.declined` para revertir inventario
4. **Auditoría:** Registrar todos los eventos para trazabilidad

---

### 2. Modelos de Datos (models/)

**Propósito:** Representar estructuras de datos JSON como POJOs

#### TransactionRequest.java
```java
public class TransactionRequest {
    private int amount_in_cents;
    private String currency;
    private String customer_email;
    private String payment_method;
    private PaymentMethod payment_method;  // Objeto con datos PSE
    
    // Builder pattern para facilitar construcción
    public static class Builder {
        public Builder withAmount(int amount) { ... }
        public Builder withCurrency(String currency) { ... }
        public TransactionRequest build() { ... }
    }
}
```

**Utilizados por:** Tasks para construir requests

**Implementación en el proyecto:** ✅ Completado
- ✅ TransactionRequest.java
- ✅ PaymentMethod.java (específico para PSE y otros métodos)
- ✅ CustomerData.java

---

### 3. Tasks (tasks/)

**Propósito:** Encapsular acciones de alto nivel que realiza el actor

#### CreateTransaction.java

```java
public class CreateTransaction implements Task {
    
    // Factory method - naming convention: verbo que describe acción
    public static CreateTransaction withDefaultPSEData() {
        return new CreateTransaction(5000000, "test@test.com", "TEST_REF_" + System.currentTimeMillis());
    }
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        // 1. Obtener acceptance token
        GetAcceptanceToken.fromWompi().performAs(actor);
        String acceptanceToken = actor.recall("acceptanceToken");
        
        // 2. Calcular firma de integridad
        String signature = IntegritySignature.calculate(reference, amount, "COP");
        
        // 3. Construir datos PSE
        PaymentMethod pseData = new PaymentMethod.Builder()
            .withType("PSE")
            .withUserType(0)  // 0 = PERSON
            .withFinancialInstitutionCode("1040")
            .build();
        
        // 4. Construir transacción
        TransactionRequest transaction = new TransactionRequest.Builder()
            .withAmount(5000000)
            .withPaymentMethod(pseData)
            .withAcceptanceToken(acceptanceToken)
            .withSignature(signature)
            .build();
        
        // 5. Hacer POST usando SerenityRest
        SerenityRest.given()
            .baseUri(WompiConfig.BASE_URL)
            .header("Authorization", "Bearer " + WompiConfig.PRIVATE_KEY)
            .contentType(ContentType.JSON)
            .body(new Gson().toJson(transaction))
            .when()
            .post("/transactions");
    }
}
```

**Implementación en el proyecto:** ✅ Completado
- ✅ CreateTransaction.java (escenario exitoso)
- ✅ CreateTransactionWithInvalidCredentials.java (escenario negativo)
- ✅ CreateTransactionWithoutAmount.java (validación de campos)
- ✅ GetAcceptanceToken.java (obtener token de aceptación)

**Dónde se usa:** En Step Definitions
```java
@When("creo una transacción con el medio de pago PSE")
public void createTransaction() {
    merchant.attemptsTo(CreateTransaction.withDefaultPSEData());
}
```

---

#### GetAcceptanceToken.java - Servicio de Token de Aceptación

**Propósito:** Obtener el `acceptance_token` requerido por Wompi para crear transacciones.

**¿Qué es el Acceptance Token?**

El `acceptance_token` es un token JWT que:
- Representa la aceptación de términos y condiciones del usuario
- Es **obligatorio** para crear transacciones en Wompi
- Se obtiene consultando la información del comercio con la llave pública
- Contiene referencia al contrato de políticas de usuario

**Endpoint utilizado:**
```
GET /v1/merchants/{public_key}
```

**Implementación:**

```java
public class GetAcceptanceToken implements Task {
    
    public static GetAcceptanceToken fromWompi() {
        return new GetAcceptanceToken();
    }
    
    @Override
    @Step("{0} obtiene el acceptance token")
    public <T extends Actor> void performAs(T actor) {
        // 1. Hacer GET al endpoint del merchant
        Response response = SerenityRest.given()
                .baseUri(WompiConfig.BASE_URL)
                .when()
                .get(WompiConfig.ACCEPTANCE_TOKEN_ENDPOINT);
        
        // 2. Extraer el acceptance_token del response
        String acceptanceToken = response.jsonPath()
                .getString("data.presigned_acceptance.acceptance_token");
        
        // 3. Almacenar en memoria del actor para uso posterior
        actor.remember("acceptanceToken", acceptanceToken);
        
        System.out.println("✓ Acceptance token obtenido: " + acceptanceToken);
    }
}
```

**Ejemplo de Request:**
```http
GET https://api-sandbox.co.uat.wompi.dev/v1/merchants/pub_stagtest_g2u0HQd3ZMh05hsSgTS2lUV8t3s4mOt7
```

**Ejemplo de Response (campos relevantes):**
```json
{
  "data": {
    "id": 5113,
    "name": "Alejandra Pruebas Sandbox UAT",
    "email": "pruebasensandbox@yopmail.com",
    "public_key": "pub_stagtest_g2u0HQd3ZMh05hsSgTS2lUV8t3s4mOt7",
    "accepted_payment_methods": ["NEQUI", "PSE", "CARD", ...],
    "presigned_acceptance": {
      "acceptance_token": "eyJhbGciOiJIUzI1NiJ9...",
      "permalink": "https://wompi.com/assets/downloadble/reglamento-Usuarios-Colombia.pdf",
      "type": "END_USER_POLICY"
    },
    "presigned_personal_data_auth": {
      "acceptance_token": "eyJhbGciOiJIUzI1NiJ9...",
      "permalink": "https://wompi.com/assets/downloadble/autorizacion-tratamiento-datos-personales.pdf",
      "type": "PERSONAL_DATA_AUTH"
    }
  }
}
```

**Campos importantes del response:**

| Campo | Descripción | Uso |
|-------|-------------|-----|
| `data.presigned_acceptance.acceptance_token` | Token JWT de aceptación de términos | **Se usa en transacciones** |
| `data.presigned_acceptance.permalink` | URL del documento de términos | Referencia legal |
| `data.accepted_payment_methods` | Métodos de pago habilitados | Validar que PSE esté disponible |
| `data.public_key` | Llave pública del comercio | Confirmar identidad |

**Uso en CreateTransaction:**

```java
// Paso 1: Obtener el token
GetAcceptanceToken.fromWompi().performAs(actor);

// Paso 2: Recuperar del actor
String acceptanceToken = actor.recall("acceptanceToken");

// Paso 3: Incluir en la transacción
TransactionRequest transaction = new TransactionRequest.Builder()
    .withAmount(5000000)
    .withAcceptanceToken(acceptanceToken)  // ← Token obtenido
    .withSignature(signature)
    .build();
```

**Patrón de Memoria del Actor:**

Serenity ScreenPlay permite que los actores "recuerden" información usando:
- `actor.remember("key", value)` - Almacenar
- `actor.recall("key")` - Recuperar

Esto evita usar variables globales y mantiene el estado aislado por actor.

**Validaciones importantes:**

1. ✅ El endpoint NO requiere autenticación (usa public key en la URL)
2. ✅ El token tiene expiración (validez limitada en el tiempo)
3. ✅ Cada transacción debe usar un token válido
4. ✅ El token es específico del comercio (ligado a la public key)

**¿Por qué es necesario?**

Según la API de Wompi:
- Cumplimiento legal: registra que el usuario aceptó términos
- Trazabilidad: cada transacción vinculada a un contrato
- Seguridad: validación de que el comercio es legítimo

**Notas técnicas:**

- El token es un JWT (JSON Web Token) firmado por Wompi
- Contiene: contract_id, permalink, file_hash, timestamp
- No necesita decodificarse, se envía completo en las transacciones
- En sandbox siempre retorna el mismo token (válido indefinidamente)
- En producción, los tokens expiran y deben renovarse

---

#### ValidateWebhookSignature.java - Validación de Webhooks

**Propósito:** Validar la autenticidad e integridad de webhooks enviados por Wompi

**¿Qué valida?**

Cuando Wompi envía un webhook (notificación de evento), esta Task verifica:
- ✅ Que el webhook proviene realmente de Wompi (autenticidad)
- ✅ Que el payload no fue modificado (integridad)
- ✅ Que la firma HMAC-SHA256 es correcta

**Implementación:**

```java
public class ValidateWebhookSignature implements Task {
    
    public static ValidateWebhookSignature of(String payload, String signature) {
        return new ValidateWebhookSignature(payload, signature);
    }
    
    @Override
    @Step("{0} valida la firma del webhook de Wompi")
    public <T extends Actor> void performAs(T actor) {
        // 1. Validar firma
        boolean isValid = WebhookValidator.validateSignature(payload, receivedSignature);
        
        // 2. Almacenar resultado
        actor.remember("webhookValid", isValid);
        
        // 3. Extraer tipo de evento
        String eventType = WebhookValidator.extractEventType(payload);
        actor.remember("webhookEventType", eventType);
        
        // 4. Si inválido, lanzar excepción de seguridad
        if (!isValid) {
            throw new SecurityException("Firma del webhook inválida - posible ataque");
        }
    }
}
```

**Uso en escenarios:**

```java
// Simular recepción de webhook de Wompi
String webhookPayload = "{\"event\":\"transaction.updated\",\"data\":{...}}";
String signature = "abc123..."; // Firma enviada por Wompi

// El actor valida el webhook
merchant.attemptsTo(
    ValidateWebhookSignature.of(webhookPayload, signature)
);

// Verificar que es auténtico
merchant.should(
    seeThat(ValidateWebhookEvent.isAuthentic(), is(true)),
    seeThat(ValidateWebhookEvent.eventType(), equalTo("transaction.updated"))
);
```

**Escenario de prueba típico:**

```gherkin
Escenario: Validar webhook de transacción actualizada
  Dado que he creado una transacción con PSE
  Cuando Wompi envía un webhook de actualización de estado
  Entonces debo validar la firma del webhook
  Y el tipo de evento debe ser "transaction.updated"
  Y el webhook debe ser marcado como auténtico
```

**Seguridad:**

- Si la firma no coincide → `SecurityException`
- Previene ataques de spoofing (alguien suplantando a Wompi)
- Detecta modificaciones en el payload durante transmisión

---

### 4. Questions (questions/)

**Propósito:** Obtener información del sistema para validar

#### ValidateResponse.java

```java
public class ValidateResponse {
    
    // Question que retorna el status code
    public static Question<Integer> statusCode() {
        return actor -> {
            Response response = SerenityRest.lastResponse();
            return response.getStatusCode();
        };
    }
    
    // Question que retorna el estado de la transacción
    public static Question<String> transactionStatus() {
        return actor -> {
            Response response = SerenityRest.lastResponse();
            return response.jsonPath().getString("data.status");
        };
    }
}
```

**Implementación en el proyecto:** ✅ Completado
- ✅ statusCode()
- ✅ transactionStatus()
- ✅ hasError()
- ✅ errorMessage()
- ✅ isSuccessful()

**Dónde se usa:** En Step Definitions para validaciones
```java
@Then("la respuesta debe ser exitosa")
public void validateResponse() {
    merchant.should(
        seeThat("El código de estado", 
                ValidateResponse.statusCode(), 
                is(201))
    );
}
```

---

#### ValidateWebhookEvent.java - Validaciones de Webhooks

**Propósito:** Questions para verificar webhooks recibidos de Wompi

**Questions disponibles:**

```java
public class ValidateWebhookEvent {
    
    // ¿El webhook es auténtico (firma válida)?
    public static Question<Boolean> isAuthentic() {
        return actor -> actor.recall("webhookValid");
    }
    
    // ¿Cuál es el tipo de evento?
    public static Question<String> eventType() {
        return actor -> actor.recall("webhookEventType");
    }
    
    // ¿Es un tipo de evento conocido?
    public static Question<Boolean> isKnownEventType() {
        return actor -> {
            String type = actor.recall("webhookEventType");
            return type.equals(WompiConfig.EVENT_TRANSACTION_UPDATED) ||
                   type.equals(WompiConfig.EVENT_PAYMENT_APPROVED) ||
                   type.equals(WompiConfig.EVENT_PAYMENT_DECLINED);
        };
    }
    
    // ¿Es una actualización de transacción?
    public static Question<Boolean> isTransactionUpdated();
    
    // ¿Es un pago aprobado?
    public static Question<Boolean> isPaymentApproved();
    
    // ¿Es un pago rechazado?
    public static Question<Boolean> isPaymentDeclined();
    
    // Obtener payload completo
    public static Question<String> payload();
}
```

**Ejemplo de uso en tests:**

```java
// Después de validar webhook
merchant.attemptsTo(
    ValidateWebhookSignature.of(payload, signature)
);

// Verificar con Questions
merchant.should(
    seeThat("Webhook auténtico", 
            ValidateWebhookEvent.isAuthentic(), 
            is(true)),
    seeThat("Tipo de evento", 
            ValidateWebhookEvent.eventType(), 
            equalTo("transaction.updated")),
    seeThat("Evento conocido", 
            ValidateWebhookEvent.isKnownEventType(), 
            is(true))
);

// Verificar tipo específico
if (ValidateWebhookEvent.isPaymentApproved().answeredBy(merchant)) {
    // Procesar pago aprobado
    notifyCustomer();
    updateInventory();
}
```

**Implementación en el proyecto:** ✅ Completado
- ✅ isAuthentic() - Valida autenticidad
- ✅ eventType() - Extrae tipo de evento
- ✅ isKnownEventType() - Verifica si es evento esperado
- ✅ isTransactionUpdated() - Evento de actualización
- ✅ isPaymentApproved() - Pago aprobado
- ✅ isPaymentDeclined() - Pago rechazado
- ✅ payload() - Obtiene JSON completo


### 5. Step Definitions (stepdefinitions/)

**Propósito:** Conectar escenarios Gherkin con código Java

#### PaymentStepDefinitions.java

**Setup:**
```java
@Before
public void setTheStage() {
    OnStage.setTheStage(new OnlineCast());
    merchant = Actor.named("Comercio Wompi");
}
```

**Steps:**
```java
@Given("que tengo credenciales válidas de Wompi")
public void validCredentials() { ... }

@When("creo una transacción con el medio de pago PSE")
public void createTransaction() {
    merchant.attemptsTo(CreateTransaction.withDefaultPSEData());
}

@Then("la respuesta debe ser exitosa")
public void validateResponse() {
    merchant.should(seeThat(ValidateResponse.statusCode(), is(201)));
}
```

**Implementación en el proyecto:** ✅ Completado
- ✅ Setup con @Before
- ✅ Steps para escenario exitoso
- ✅ Steps para escenarios alternativos (auth error, validation error)

---

### 6. Feature Files (features/)

**Propósito:** Definir escenarios de prueba en lenguaje Gherkin

#### payment.feature

```gherkin
# language: es
@Wompi @API @Regression
Característica: Integración con API de Wompi para transacciones PSE
  
  @Happy @PSE
  Escenario: Creación exitosa de transacción con PSE
    Dado que tengo credenciales válidas de Wompi
    Cuando creo una transacción con el medio de pago PSE
    Entonces la respuesta debe ser exitosa
    Y el estado de la transacción debe ser "PENDING" o "APPROVED"
  
  @Negative @Authentication
  Escenario: Error por llave de autenticación inválida
    Dado que tengo credenciales inválidas
    Cuando intento crear una transacción
    Entonces la API debe responder con error de autenticación
```

**Implementación en el proyecto:** ✅ Completado
- ✅ Escenario exitoso (Happy Path)
- ✅ Escenario de error de autenticación
- ✅ Escenario de validación de datos
- ✅ Tags para organización (@Happy, @Negative, @PSE)

---

### 7. Runner (runners/)

**Propósito:** Ejecutar los tests de Cucumber con Serenity

#### PaymentRunnerIT.java

```java
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "stepdefinitions",
    plugin = {"pretty", "html:target/cucumber-reports.html"}
)
public class PaymentRunnerIT {
}
```

**Implementación en el proyecto:** ✅ Completado

**Nota:** El nombre termina en `IT` (Integration Test) para que Maven Failsafe lo ejecute correctamente.

---

## 🎯 Diseño de Escenarios de Prueba

### Metodología

1. **Análisis de Documentación Wompi:**
   - Revisar endpoints disponibles
   - Identificar métodos de pago (PSE seleccionado)
   - Entender estructura de requests/responses

2. **Identificación de Casos de Prueba:**
   
   **Happy Path:**
   - ✅ Transacción PSE exitosa
   
   **Negative Paths:**
   - ✅ Autenticación inválida
   - ✅ Datos incompletos (sin monto)
   - ⚠️ Posibles adicionales: límites de monto, emails inválidos, etc.

3. **Diseño en Gherkin:**
   - Usar lenguaje de negocio
   - Evitar detalles técnicos en feature files
   - Tags para organización

### Casos de Prueba Implementados

| # | Escenario | Tipo | Esperado | Status |
|---|-----------|------|----------|--------|
| 1 | Transacción PSE válida | Happy | 201 Created, Status PENDING/APPROVED | ✅ |
| 2 | Credenciales inválidas | Negative | 401/403 Unauthorized | ✅ |
| 3 | Datos incompletos | Negative | 400/422 Bad Request | ✅ |
| 4 | Webhook auténtico | Security | Firma válida, evento correcto | ✅ |
| 5 | Webhook inválido | Security/Negative | SecurityException, rechazado | ✅ |

---

## 🔧 Tecnologías Utilizadas

### Dependencias principales (pom.xml)

```xml
<!-- Serenity BDD - Framework de automatización -->
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-core</artifactId>
    <version>3.9.0</version>
</dependency>

<!-- Serenity ScreenPlay -->
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-screenplay</artifactId>
    <version>3.9.0</version>
</dependency>

<!-- Serenity REST Assured -->
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-rest-assured</artifactId>
    <version>3.9.0</version>
</dependency>

<!-- Serenity + Cucumber -->
<dependency>
    <groupId>net.serenity-bdd</groupId>
    <artifactId>serenity-cucumber</artifactId>
    <version>3.9.0</version>
</dependency>

<!-- Cucumber para BDD -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.14.0</version>
</dependency>

<!-- Gson para serialización JSON -->
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

---

## 🚀 Cómo Ejecutar

### Prerrequisitos
- Java 11 o superior
- Maven 3.6+

### Comandos

```bash
# 1. Limpiar y compilar
mvn clean compile

# 2. Ejecutar tests
mvn clean verify

# 3. Generar reportes Serenity
mvn serenity:aggregate

# 4. Ver reporte (abre en navegador)
open target/site/serenity/index.html
```

### Ejecutar escenarios específicos por tags

```bash
# Solo escenarios exitosos
mvn verify -Dcucumber.filter.tags="@Happy"

# Solo escenarios de PSE
mvn verify -Dcucumber.filter.tags="@PSE"

# Excluir escenarios negativos
mvn verify -Dcucumber.filter.tags="not @Negative"
```

---

## 📊 Reportes Serenity

### ¿Qué incluyen?

1. **Dashboard:**
   - Total de tests ejecutados
   - % de éxito
   - Duración
   - Gráficos

2. **Detalle de Escenarios:**
   - Steps ejecutados
   - Requests/Responses
   - Tiempos de ejecución
   - Stack traces en caso de fallo

3. **Living Documentation:**
   - Features organizadas por tags
   - Descripción de escenarios
   - Resultados históricos

### Ubicación
```
target/site/serenity/index.html
```

---

## 🎓 Conceptos Aprendidos

### 1. ScreenPlay Pattern
- ✅ Separación de responsabilidades
- ✅ Actors, Tasks, Questions, Abilities
- ✅ Código más expresivo y mantenible

### 2. BDD
- ✅ Gherkin syntax
- ✅ Given-When-Then
- ✅ Living Documentation

### 3. Serenity BDD
- ✅ Integración con Cucumber
- ✅ Reportes automáticos
- ✅ REST Assured integration

### 4. API Testing
- ✅ REST API calls (POST)
- ✅ Autenticación con Bearer token
- ✅ Validación de status codes
- ✅ Validación de JSON responses

### 5. Java Patterns
- ✅ Builder pattern (para construcción de objetos)
- ✅ Factory method (para Tasks)
- ✅ Lambda expressions (para Questions)

---

## 🔍 Dónde se Implementó Cada Patrón

### ScreenPlay

| Componente | Archivo | Líneas Clave |
|------------|---------|--------------|
| **Actor** | PaymentStepDefinitions.java | `Actor.named("Comercio Wompi")` |
| **Ability** | Implícito en SerenityRest | `SerenityRest.given()...` |
| **Task** | CreateTransaction.java | `implements Task`, `performAs(Actor)` |
| **Question** | ValidateResponse.java | `Question<Integer>`, `Question<String>` |
| **Interaction** | CreateTransaction.java | `SerenityRest.given()...post(...)` |

### BDD

| Componente | Archivo | Descripción |
|------------|---------|-------------|
| **Feature** | payment.feature | `Característica: ...` |
| **Scenario** | payment.feature | `Escenario: Creación exitosa...` |
| **Steps** | payment.feature | `Dado...Cuando...Entonces` |
| **Step Definitions** | PaymentStepDefinitions.java | `@Given`, `@When`, `@Then` |
| **Runner** | PaymentRunner.java | `@RunWith(CucumberWithSerenity.class)` |

---

## 💡 Mejores Prácticas Aplicadas

### 1. Nomenclatura
- ✅ Tasks con verbos: `CreateTransaction.withPSE()`
- ✅ Questions descriptivas: `ValidateResponse.statusCode()`
- ✅ Actors con nombres de negocio: `"Comercio Wompi"`

### 2. Organización
- ✅ Separación por capas (models, tasks, questions)
- ✅ Configuración centralizada (WompiConfig)
- ✅ Builder pattern para construcción de objetos

### 3. Reusabilidad
- ✅ Tasks genéricas reutilizables
- ✅ Questions parametrizables
- ✅ Factory methods para facilitar uso

### 4. Mantenibilidad
- ✅ Código autodocumentado
- ✅ Single Responsibility Principle
- ✅ Evitar hardcoding (usar constantes)

---

## 📈 Próximos Pasos (Mejoras Futuras)

### Corto Plazo
- [ ] Agregar más escenarios de PSE (montos límite, bancos diferentes)
- [ ] Implementar validación de integridad con signature
- [ ] Agregar pruebas de consulta de transacciones (GET)

### Medio Plazo
- [ ] Integración con CI/CD (GitHub Actions / Jenkins)
- [ ] Data-driven testing con Examples en Cucumber
- [ ] Pruebas de otros métodos de pago (NEQUI, Bancolombia)

### Largo Plazo
- [ ] Performance testing (JMeter integration)
- [ ] Contract testing (Pact)
- [ ] Monitoreo en producción (sintéticos)

---

## 📚 Referencias

### Documentación Oficial
- [Wompi API Docs](https://docs.wompi.co/docs/colombia/inicio-rapido/)
- [Serenity BDD](https://serenity-bdd.github.io/)
- [Cucumber](https://cucumber.io/docs/cucumber/)

### Recursos de Aprendizaje
- [Serenity ScreenPlay Tutorial](https://serenity-bdd.github.io/docs/screenplay/screenplay_fundamentals)
- [BDD Best Practices](https://cucumber.io/docs/bdd/)
- [REST Assured](https://rest-assured.io/)

---

## ✅ Checklist de Implementación

### Estructura del Proyecto
- [x] pom.xml con dependencias correctas
- [x] Estructura de directorios organizada
- [x] serenity.properties configurado

### Modelos
- [x] TransactionRequest con Builder
- [x] PaymentMethod con Builder (para PSE y otros)
- [x] CustomerData con Builder

### ScreenPlay
- [x] Tasks para escenarios happy y negative
- [x] Questions para validaciones
- [x] Actors configurados en Step Definitions

### BDD
- [x] Feature file con escenarios en Gherkin
- [x] Step Definitions mapeados
- [x] Runner configurado

### Configuración
- [x] WompiConfig con URLs y llaves
- [x] Datos de prueba para PSE

### Documentación
- [x] GUIA_PRESENTACION.md
- [x] DOCUMENTACION_PROYECTO.md (este archivo)
- [x] Comentarios en código
- [x] README con instrucciones

---

## 🎉 Conclusión

Este proyecto demuestra una implementación profesional de:
- **ScreenPlay Pattern** para tests mantenibles y expresivos
- **BDD** para documentación viva y colaboración
- **Serenity BDD** para reporting completo
- **API Testing** con REST Assured

El código está listo para ejecutarse, extenderse y presentarse. ¡Éxito en tu prueba técnica! 🚀

---

**Autor:** Automatización Wompi  
**Fecha:** 2026  
**Stack:** Java 11 + Maven + Serenity BDD + Cucumber + ScreenPlay

