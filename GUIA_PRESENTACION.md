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
  │   - Tasks (Tareas)                  │  ← Qué hace el actor
  │   - Questions (Preguntas)           │  ← Qué valida el actor
  │   - Abilities (Habilidades)         │  ← Cómo interactúa (API calls)
  ├─────────────────────────────────────┤
  │   Models (POJOs)                    │  ← Datos de request/response
  ├─────────────────────────────────────┤
  │   Wompi API (REST)                  │  ← Sistema bajo prueba
  └─────────────────────────────────────┘
  ```

**Explica:**
- **BDD:** Escenarios escritos en Gherkin (Given-When-Then)
- **ScreenPlay:** Patrón que separa QUÉ hace el test de CÓMO lo hace
- **Actors:** Representan usuarios/sistemas (ej: "Comercio Wompi")
- **Tasks:** Acciones de alto nivel (ej: "Crear transacción PSE")
- **Questions:** Validaciones (ej: "¿El status code es 201?")
- **Abilities:** Capacidades del actor (ej: "CallAnApi")

#### Slide 3: Diseño de Escenarios (1.5 minutos)
- **Escenario Exitoso (Happy Path):**
  - Crear transacción PSE con datos válidos
  - Validar status code 201
  - Validar estado PENDING/APPROVED

- **Escenarios Alternos (Negative Testing):**
  - Credenciales inválidas → Error 401/403
  - Datos incompletos → Error 400/422

**Técnica de diseño:**
- Basado en documentación de Wompi
- Cobertura de casos positivos y negativos
- Validación de campos requeridos
- Validación de autenticación

#### Slide 4: Flujo de Ejecución (1 minuto)
```
Feature File (Gherkin)
    ↓
Step Definitions
    ↓
Actor → Task → API Call → Response
    ↓
Actor → Question → Assertion
    ↓
Serenity Report
```

**Ejemplo práctico:**
1. **Given** credenciales válidas → Actor configurado con API key
2. **When** creo transacción PSE → Actor ejecuta Task "CreateTransaction"
3. **Then** respuesta exitosa → Actor pregunta "¿statusCode == 201?"

#### Slide 5: Demo en Vivo / Resultados (30 segundos)
- Mostrar reporte de Serenity BDD
- Resaltar métricas: X tests passed
- Mostrar detalle de un test

#### Slide 6: Conclusión (30 segundos)
- **Ventajas del enfoque:**
  - Tests legibles (BDD)
  - Mantenibles (ScreenPlay)
  - Reportes detallados (Serenity)
  - Reutilizable

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
- ¿Por qué ScreenPlay y no Page Object? → Más expresivo, mejor para APIs
- ¿Cómo se escala esto? → Agregando más Tasks reutilizables
- ¿Cómo se integra en CI/CD? → Maven plugin + Jenkins/GitHub Actions

---

## ✅ Checklist Pre-Presentación

- [ ] Diagramas de arquitectura creados
- [ ] Ejemplos de código preparados
- [ ] Tests ejecutados exitosamente
- [ ] Reporte de Serenity generado
- [ ] Presentación probada en 5 minutos
- [ ] Diapositivas con diseño limpio
- [ ] Demo funcional (o video backup)
- [ ] Preparado para preguntas técnicas

---

## 💡 Mensaje Clave

> "Implementamos un framework de automatización para validar transacciones PSE en Wompi, usando BDD para escribir tests legibles y ScreenPlay para hacerlos mantenibles, todo generando reportes automáticos con Serenity BDD."

¡Éxito en tu presentación! 🚀

