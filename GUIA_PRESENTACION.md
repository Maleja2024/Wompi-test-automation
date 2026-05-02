# Guía para la Presentación Review - Punto 3

## 📋 Objetivo
Crear una presentación de máximo 5 minutos donde se explique de manera simplificada:
1. La arquitectura del proyecto
2. El diseño de los escenarios de prueba

---

## 🎯 ¿Qué debes aprender/preparar?

### 1. **Estructura de la Presentación (5 minutos)**

#### Slide 1: Introducción (30 segundos)
- **Título:** "Automatización de Pruebas API - Wompi PSE"
- **Contenido:**
  - Objetivo: Validar transacciones PSE en Wompi
  - Patrón de diseño: ScreenPlay
  - Framework: Serenity BDD + Cucumber
  - Lenguaje: Java 11

#### Slide 2: Arquitectura del Proyecto (1.5 minutos)
- **Diagrama de capas:**
  ```
  ┌─────────────────────────────────────┐
  │   BDD Layer (Cucumber/Gherkin)      │  ← Escenarios en lenguaje natural
  ├─────────────────────────────────────┤
  │   ScreenPlay Pattern                │
  │   - Actors (Actores)                │  ← Quien ejecuta las acciones
  │   - Tasks (Tareas) - 5 tasks        │  ← Qué hace el actor
  │   - Questions (Preguntas) - 2       │  ← Qué valida el actor
  │   - Abilities (Habilidades)         │  ← Cómo interactúa (API calls)
  ├─────────────────────────────────────┤
  │   Models (POJOs) - 4 modelos        │  ← Datos de request/response
  ├─────────────────────────────────────┤
  │   Utils (Seguridad) 🔒              │  ← Validación criptográfica
  │   - IntegritySignature (SHA-256)    │  ← Firma de transacciones
  │   - WebhookValidator (HMAC-SHA256)  │  ← Validación de webhooks
  │   - GetAcceptanceToken              │  ← Token de aceptación
  ├─────────────────────────────────────┤
  │   Wompi API (REST)                  │  ← Sistema bajo prueba
  └─────────────────────────────────────┘
  ```

**Explica:**
- **BDD:** Escenarios escritos en Gherkin (Given-When-Then)
- **ScreenPlay:** Patrón que separa QUÉ hace el test de CÓMO lo hace
- **Actors:** Representan usuarios/sistemas (ej: "Comercio Wompi")
- **Tasks:** Acciones de alto nivel (ej: "Crear transacción PSE", "Validar webhook")
- **Questions:** Validaciones (ej: "¿El status code es 201?", "¿Webhook auténtico?")
- **Abilities:** Capacidades del actor (ej: "CallAnApi")
- **Utils:** Capa de seguridad con validación criptográfica (SHA-256, HMAC-SHA256)

#### Slide 3: Diseño de Escenarios - 5 Casos de Prueba (1.5 minutos)

**1. Happy Path (20%):**
  - ✅ TC-001: Crear transacción PSE con datos válidos
    - Validar status code 201
    - Validar estado PENDING/APPROVED
    - Obtención automática de acceptance token
    - Cálculo automático de firma SHA-256

**2. Negative Testing (40%):**
  - ❌ TC-002: Credenciales inválidas → Error 401/403
  - ❌ TC-003: Datos incompletos (sin monto) → Error 400/422

**3. Security Testing (40%):**
  - 🔒 TC-004: Validar webhook auténtico de Wompi
    - Validación HMAC-SHA256 de firma
    - Verificar tipo de evento "transaction.updated"
  - 🔒 TC-005: Rechazar webhook con firma inválida
    - Detectar ataques de spoofing
    - Lanzar SecurityException

**Técnica de diseño:**
- Basado en documentación oficial de Wompi
- Cobertura completa: Happy + Negative + Security
- Validación criptográfica (SHA-256 + HMAC-SHA256)
- Prevención de ataques de manipulación y spoofing

#### Slide 4: Flujo de Ejecución (1 minuto)
```
Feature File (Gherkin)
    ↓
Step Definitions
    ↓
Actor → GetAcceptanceToken → Wompi API
    ↓
Actor → Task (CreateTransaction) → IntegritySignature (SHA-256)
    ↓
API Call → Response
    ↓
Actor → Question → Assertion
    ↓
Wehbook Received → WebhookValidator (HMAC-SHA256)
    ↓
Serenity Report
```

**Ejemplo práctico - Transacción PSE:**
1. **Given** credenciales válidas → Actor configurado con API key
2. **Task interna:** Obtener acceptance_token de Wompi
3. **Task interna:** Calcular firma de integridad (SHA-256)
4. **When** creo transacción PSE → Actor ejecuta Task "CreateTransaction"
5. **Then** respuesta exitosa → Actor pregunta "¿statusCode == 201?"

**Ejemplo práctico - Webhook:**
1. **Given** llave de eventos configurada
2. **When** recibo webhook → Actor ejecuta "ValidateWebhookSignature"
3. **Task interna:** Calcular HMAC-SHA256 y comparar firmas
4. **Then** webhook auténtico → Question "¿isAuthentic() == true?"

#### Slide 5: Demo en Vivo / Resultados (30 segundos)
- Mostrar reporte de Serenity BDD
- **Resaltar métricas: 5 tests ejecutados, 5 pasaron ✅**
  - 1 Happy Path (transacción PSE)
  - 2 Negative (auth error + validation error)
  - 2 Security (webhook válido + webhook inválido)
- Mostrar detalle de un test (transacción PSE con firma SHA-256)
- **Destacar validación de webhooks con HMAC-SHA256**

#### Slide 6: Conclusión (30 segundos)
- **Ventajas del enfoque:**
  - Tests legibles (BDD con Gherkin en español)
  - Mantenibles (ScreenPlay Pattern)
  - Reportes detallados (Serenity BDD)
  - Reutilizable y escalable
  - **🔒 Seguridad robusta (SHA-256 + HMAC-SHA256)**
  - **🛡️ Prevención de ataques (spoofing, manipulación)**
  - Cobertura completa (Happy + Negative + Security)

---

## 📚 Conceptos Clave a Dominar

### **BDD (Behavior-Driven Development)**
- **¿Qué es?** Desarrollo guiado por comportamiento
- **Beneficio:** Tests entendibles por no técnicos
- **Herramienta:** Cucumber con Gherkin (Given-When-Then)
- **Ejemplo:**
  ```gherkin
  Scenario: Pago exitoso
    Given credenciales válidas
    When creo transacción PSE
    Then respuesta exitosa
  ```

### **ScreenPlay Pattern**
- **¿Qué es?** Patrón de diseño para tests automatizados
- **Creadores:** John Ferguson Smart (Serenity BDD)
- **Principio:** "Actores realizan Tareas usando Habilidades y hacen Preguntas"
- **Ventaja sobre Page Object:**
  - Más expresivo
  - Mejor separación de responsabilidades
  - Facilita reutilización

### **Serenity BDD**
- **¿Qué es?** Framework de automatización y reporting
- **Características:**
  - Integración con Cucumber
  - Reportes HTML automatizados
  - Screenshots y logs detallados
  - Living Documentation

### **Seguridad Criptográfica** 🔒
- **IntegritySignature (SHA-256):**
  - Calcula firma de integridad para cada transacción
  - Fórmula: `SHA-256(reference + amount + currency + integrity_key)`
  - Previene manipulación de montos o referencias
  - Validación automática por Wompi

- **WebhookValidator (HMAC-SHA256):**
  - Valida autenticidad de webhooks de Wompi
  - Algoritmo: `HMAC-SHA256(payload, events_key)`
  - Detecta webhooks falsificados (spoofing attacks)
  - Lanza `SecurityException` si firma inválida

- **Acceptance Token:**
  - Token JWT requerido por Wompi
  - Representa aceptación de términos y condiciones
  - Obtenido automáticamente con `GetAcceptanceToken`
  - Cumplimiento legal y trazabilidad

---

## 💻 Ejemplos de Código para Mostrar

### Ejemplo 1: Gherkin (Feature File)
```gherkin
Escenario: Creación exitosa de transacción con PSE
  Dado que tengo credenciales válidas de Wompi
  Cuando creo una transacción con el medio de pago PSE
  Entonces la respuesta debe ser exitosa
  Y el estado de la transacción debe ser "PENDING" o "APPROVED"
```

### Ejemplo 2: Task (CreateTransaction)
```java
public class CreateTransaction implements Task {
    @Override
    public <T extends Actor> void performAs(T actor) {
        // 1. Obtener acceptance token
        GetAcceptanceToken.fromWompi().performAs(actor);
        
        // 2. Calcular firma de integridad
        String signature = IntegritySignature.calculate(
            reference, amountInCents, "COP"
        );
        
        // 3. Ejecutar POST a Wompi API
        SerenityRest.given()
            .header("Authorization", "Bearer " + PRIVATE_KEY)
            .body(transactionRequest)
            .post("/transactions");
    }
}
```

### Ejemplo 3: Question (ValidateResponse)
```java
public static Question<Integer> statusCode() {
    return actor -> {
        Response response = SerenityRest.lastResponse();
        return response.getStatusCode();
    };
}
```

### Ejemplo 4: Validación de Webhook
```java
public class ValidateWebhookSignature implements Task {
    @Override
    public <T extends Actor> void performAs(T actor) {
        boolean isValid = WebhookValidator.validateSignature(
            payload, receivedSignature
        );
        
        if (!isValid) {
            throw new SecurityException("Firma inválida!");
        }
    }
}
```

---

## 🎨 Tips para la Presentación

### 1. **Visualización**
- Usa diagramas simples (evita texto excesivo)
- Código solo como snippet pequeño
- Usa colores para diferenciar capas

### 2. **Narrativa**
- Cuenta una historia: "El comercio quiere cobrar con PSE..."
- Usa analogías: "El Actor es como un usuario, la Task es lo que hace..."
- Conecta con el negocio: "Esto valida que Wompi procese pagos correctamente"

### 3. **Demo**
- Ejecuta un test en vivo (o graba un video)
- Muestra el reporte de Serenity
- Resalta el detalle de pasos

### 4. **Timing**
- ⏱️ Practica con cronómetro
- Deja 30 segundos para preguntas
- Ten slide de backup con detalles técnicos

### 5. **Explicando Seguridad Criptográfica** 🔒
- **No entres en detalle matemático:** Di "usamos SHA-256 para firmar transacciones" no "hash unidireccional de 256 bits..."
- **Usa analogías:** "Como un sello de seguridad en un paquete - si alguien lo abre, se nota"
- **Enfócate en el valor:** "Previene que alguien cambie el monto de $50,000 a $500,000"
- **Para webhooks:** "Como validar que un email realmente viene de tu banco, no de un impostor"
- **Menciona los algoritmos:** SHA-256 (transacciones) vs HMAC-SHA256 (webhooks), pero no expliques diferencias a menos que pregunten

---

## 🎯 Qué Destacar de Cada Tipo de Escenario

### Happy Path (TC-001)
**Qué resaltar:**
- Flujo completo end-to-end
- Obtención automática del acceptance token
- Cálculo automático de firma SHA-256
- Request bien formado con todos los campos requeridos
- Validación de respuesta exitosa (201 Created)

**Frase clave:** *"Valida que un comercio puede crear una transacción PSE correctamente con todas las validaciones de seguridad automáticas"*

### Negative - Authentication (TC-002)
**Qué resaltar:**
- Manejo de errores de autenticación
- Validación de credenciales inválidas
- Respuesta esperada: 401/403 Unauthorized

**Frase clave:** *"Garantiza que solo comercios autorizados puedan crear transacciones"*

### Negative - Validation (TC-003)
**Qué resaltar:**
- Validación de campos requeridos
- Detección de datos incompletos
- Respuesta esperada: 400/422 Bad Request

**Frase clave:** *"Valida que la API rechaza transacciones mal formadas"*

### Security - Webhook Válido (TC-004)
**Qué resaltar:**
- Validación criptográfica con HMAC-SHA256
- Verificación de autenticidad del webhook
- Extracción del tipo de evento
- Prevención de ataques

**Frase clave:** *"Garantiza que solo procesamos notificaciones legítimas de Wompi"*

### Security - Webhook Inválido (TC-005)
**Qué resaltar:**
- Detección de firmas inválidas
- Lanzamiento de SecurityException
- Rechazo de webhooks potencialmente maliciosos

**Frase clave:** *"Protege contra ataques de spoofing y manipulación de webhooks"*

---

## 🔗 Recursos Adicionales

### Para estudiar rápido:
1. **ScreenPlay Pattern:**
   - [Serenity BDD Docs](https://serenity-bdd.github.io/docs/screenplay/screenplay_fundamentals)
   - Video: "ScreenPlay Pattern Explained" (YouTube)

2. **BDD:**
   - [Cucumber docs](https://cucumber.io/docs/gherkin/)
   - Artículo: "Writing Better Gherkin" (Cucumber blog)

3. **Wompi API:**
   - [Docs oficiales](https://docs.wompi.co/docs/colombia/inicio-rapido/)

### Preguntas que pueden hacerte:
- ¿Por qué ScreenPlay y no Page Object? → Más expresivo, mejor para APIs, separación de responsabilidades
- ¿Cómo se escala esto? → Agregando más Tasks reutilizables, cada task es independiente
- ¿Cómo se integra en CI/CD? → Maven plugin + Jenkins/GitHub Actions, reportes automáticos
- **¿Cómo validan la seguridad?** → Firmas criptográficas (SHA-256 para transacciones, HMAC-SHA256 para webhooks)
- **¿Qué pasa si reciben un webhook falso?** → WebhookValidator detecta firma inválida y lanza SecurityException
- **¿Por qué necesitan acceptance token?** → Requerimiento legal de Wompi, representa aceptación de términos
- **¿Cuántos escenarios tienen?** → 5 escenarios: 1 Happy, 2 Negative, 2 Security

---

## ✅ Checklist Pre-Presentación

- [ ] Diagramas de arquitectura creados (incluir capa Utils/Security)
- [ ] Ejemplos de código preparados (Tasks, Questions, validación de firmas)
- [ ] **Tests ejecutados exitosamente (5/5 escenarios PASS)**
- [ ] Reporte de Serenity generado y revisado
- [ ] **Validar que aparezcan los 5 escenarios en el reporte:**
  - [ ] TC-001: Happy Path (transacción PSE)
  - [ ] TC-002: Negative (auth error)
  - [ ] TC-003: Negative (validation error)
  - [ ] TC-004: Security (webhook válido)
  - [ ] TC-005: Security (webhook inválido)
- [ ] Presentación probada en 5 minutos
- [ ] Diapositivas con diseño limpio
- [ ] Demo funcional (o video backup)
- [ ] **Preparado para explicar:**
  - [ ] ScreenPlay Pattern y sus componentes
  - [ ] Diferencia entre SHA-256 y HMAC-SHA256
  - [ ] Por qué validan webhooks
  - [ ] Qué es el acceptance token
- [ ] Preparado para preguntas técnicas

---

## 💡 Mensaje Clave

> "Implementamos un framework de automatización para validar transacciones PSE en Wompi, usando **BDD** para escribir tests legibles, **ScreenPlay** para hacerlos mantenibles, con **validación criptográfica robusta** (SHA-256 + HMAC-SHA256) para garantizar seguridad, todo generando reportes automáticos con Serenity BDD. Cobertura completa: Happy Path, Negative Testing y Security Testing."

**En una frase:**
> "Tests automatizados de Wompi con BDD, ScreenPlay y seguridad criptográfica integrada."

¡Éxito en tu presentación! 🚀

