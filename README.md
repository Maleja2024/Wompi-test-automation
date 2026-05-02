# 🚀 Wompi API Automation - ScreenPlay & BDD

[![Build](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Tests](https://img.shields.io/badge/tests-3%2F3-brightgreen)]()
[![Pattern](https://img.shields.io/badge/pattern-ScreenPlay-purple)]()
[![BDD](https://img.shields.io/badge/BDD-Cucumber-green)]()
[![Java](https://img.shields.io/badge/java-11-orange)]()

Pruebas automatizadas para la API de Wompi utilizando el **patrón ScreenPlay** y **BDD** con Serenity + Cucumber.

---

## 📚 Documentación del Proyecto

| 📄 Archivo | 🎯 Propósito | 👀 Cuándo Leer |
|-----------|-------------|----------------|
| **[README.md](README.md)** (este archivo) | Índice y visión general | ⭐ **Inicio aquí** |
| **[RESUMEN_PROYECTO.md](RESUMEN_PROYECTO.md)** | Resumen ejecutivo completo | 📊 Para ver qué se implementó |
| **[INSTRUCCIONES_EJECUCION.md](INSTRUCCIONES_EJECUCION.md)** | Cómo ejecutar paso a paso | ▶️ Para correr los tests |
| **[DOCUMENTACION_PROYECTO.md](DOCUMENTACION_PROYECTO.md)** | Explicación detallada técnica | 📖 Para entender ScreenPlay y BDD |
| **[GUIA_PRESENTACION.md](GUIA_PRESENTACION.md)** | Preparación para review (5 min) | 🎤 Para la presentación |
| **[DISEÑO_ESCENARIOS.md](DISEO_ESCENARIOS.md)** | Matriz de casos de prueba | 🧪 Para ver la estrategia de testing |

---

## ⚡ Quick Start (30 segundos)

```bash
# 1. Ir al proyecto
cd "/Users/alejandraramirez/Documents/intellij/Curso Java/wompi-automation"

# 2. Ejecutar tests
mvn clean verify

# 3. Ver reporte
open target/site/serenity/index.html
```

---

## 📋 Descripción

Este proyecto automatiza pruebas de integración para transacciones con **PSE (Pagos Seguros en Línea)** en la plataforma Wompi, implementando:

- ✅ **Patrón ScreenPlay** (Serenity BDD)
- ✅ **BDD** con Cucumber y Gherkin en español
- ✅ **API REST Testing** con REST Assured
- ✅ **Reportes detallados** con Serenity
- ✅ **Java 11** + Maven

---

## 🏗️ Arquitectura (Vista Rápida)

```
┌─────────────────────────────────┐
│   BDD Layer (Gherkin)           │ ← Escenarios en lenguaje natural
├─────────────────────────────────┤
│   ScreenPlay Pattern            │
│   • Actors (Comercio Wompi)     │
│   • Tasks (Crear transacción)   │
│   • Questions (Validar status)  │
│   • Abilities (Llamar API)      │
├─────────────────────────────────┤
│   Models (POJOs)                │ ← Request/Response
├─────────────────────────────────┤
│   Wompi API REST                │ ← Sistema bajo prueba
└─────────────────────────────────┘
```

---

## 📁 Estructura del Proyecto

```
wompi-automation/
├── src/test/
│   ├── java/
│   │   ├── models/              # Modelos de datos (POJOs)
│   │   ├── tasks/               # Tareas (ScreenPlay)
│   │   ├── questions/           # Validaciones (ScreenPlay)
│   │   ├── utils/               # Configuración
│   │   ├── stepdefinitions/     # Steps Cucumber
│   │   └── runners/             # Ejecutor de tests
│   └── resources/
│       └── features/            # Escenarios Gherkin
├── pom.xml
├── serenity.properties
├── README.md                    # Este archivo
├── DOCUMENTACION_PROYECTO.md    # Documentación detallada
└── GUIA_PRESENTACION.md         # Guía para presentación
```

---

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 11 | Lenguaje de programación |
| Maven | 3.6+ | Gestión de dependencias |
| Serenity BDD | 3.9.0 | Framework de automatización |
| Cucumber | 7.14.0 | BDD Framework |
| REST Assured | - | Testing de APIs REST |
| JUnit | 4.13.2 | Framework de testing |

---

## 🚀 Inicio Rápido

### Prerrequisitos

```bash
# Verificar Java
java -version  # Debe ser 11 o superior

# Verificar Maven
mvn -version   # Debe ser 3.6+
```

### Instalación

```bash
# 1. Clonar/acceder al proyecto
cd wompi-automation

# 2. Instalar dependencias
mvn clean install -DskipTests
```

### Ejecutar Tests

```bash
# Ejecutar todos los tests
mvn clean verify

# Generar reportes
mvn serenity:aggregate

# Abrir reporte en navegador
open target/site/serenity/index.html
```

### Ejecutar por Tags

```bash
# Solo tests exitosos
mvn verify -Dcucumber.filter.tags="@Happy"

# Solo tests de PSE
mvn verify -Dcucumber.filter.tags="@PSE"

# Tests negativos
mvn verify -Dcucumber.filter.tags="@Negative"
```

---

## 📝 Escenarios de Prueba

### ✅ Happy Path

#### Transacción PSE Exitosa
```gherkin
Escenario: Creación exitosa de transacción con PSE
  Dado que tengo credenciales válidas de Wompi
  Cuando creo una transacción con el medio de pago PSE
  Entonces la respuesta debe ser exitosa
  Y el estado de la transacción debe ser "PENDING" o "APPROVED"
```

### ⚠️ Negative Paths

#### Error de Autenticación
```gherkin
Escenario: Error por llave de autenticación inválida
  Dado que tengo credenciales inválidas
  Cuando intento crear una transacción
  Entonces la API debe responder con error de autenticación
```

#### Validación de Datos
```gherkin
Escenario: Error por datos incompletos en la solicitud
  Dado que tengo credenciales válidas de Wompi
  Cuando creo una transacción sin monto
  Entonces la API debe responder con error de validación
```

---

## 🎭 Patrón ScreenPlay

### Ejemplo de Uso

```java
// 1. Crear el Actor
Actor merchant = Actor.named("Comercio Wompi")
                     .whoCan(CallAnApi.at(BASE_URL));

// 2. El Actor ejecuta una Task
merchant.attemptsTo(
    CreateTransaction.withDefaultPSEData()
);

// 3. El Actor hace una Question para validar
merchant.should(
    seeThat("El código de estado", 
            ValidateResponse.statusCode(), 
            is(201))
);
```

### Componentes

- **Actors:** `"Comercio Wompi"` - Quien realiza las acciones
- **Tasks:** `CreateTransaction` - Qué hacer
- **Questions:** `ValidateResponse` - Qué validar
- **Abilities:** Implícitas en SerenityRest - Cómo interactuar

---

## 📊 Reportes Serenity

Los reportes incluyen:
- ✅ Dashboard con métricas
- ✅ Detalle de cada escenario
- ✅ Requests/Responses HTTP
- ✅ Tiempos de ejecución
- ✅ Screenshots de errores
- ✅ Living Documentation

**Ubicación:** `target/site/serenity/index.html`

---

## 🔧 Configuración

### Credenciales Wompi (Sandbox)

Configuradas en `src/test/java/utils/WompiConfig.java`:

```java
// Base URL
BASE_URL = "https://api-sandbox.co.uat.wompi.dev/v1"

// Llaves
PRIVATE_KEY = "prv_stagtest_5i0ZGIGiFcDQifYsXxvsny7Y37tKqFWg"
PUBLIC_KEY = "pub_stagtest_g2u0HQd3ZMh05hsSgTS2lUV8t3s4mOt7"
```

### PSE (Método de Pago)

```java
PSE_PAYMENT_METHOD = "PSE"
PSE_USER_TYPE = 0  // 0 = PERSON, 1 = BUSINESS
PSE_FINANCIAL_INSTITUTION_CODE = "1040" // Banco Agrario
```

---

## 📚 Documentación

| Archivo | Descripción |
|---------|-------------|
| `README.md` | Inicio rápido (este archivo) |
| `DOCUMENTACION_PROYECTO.md` | Explicación detallada de ScreenPlay, BDD y arquitectura |
| `GUIA_PRESENTACION.md` | Guía para la presentación técnica (5 min) |

---

## 🧪 Casos de Prueba

| ID | Escenario | Tipo | Status Code | Estado Transacción |
|----|-----------|------|-------------|-------------------|
| TC01 | Transacción PSE válida | Happy | 201 | PENDING/APPROVED |
| TC02 | Auth inválida | Negative | 401/403 | - |
| TC03 | Datos incompletos | Negative | 400/422 | - |

---

## 🎓 Aprende Más

### ScreenPlay Pattern
- [Serenity ScreenPlay Docs](https://serenity-bdd.github.io/docs/screenplay/screenplay_fundamentals)
- El patrón separa **QUÉ** hace el test de **CÓMO** lo hace
- Más mantenible que Page Object Model

### BDD
- [Cucumber Docs](https://cucumber.io/docs/gherkin/)
- Escenarios en lenguaje natural (Given-When-Then)
- Living Documentation

### Wompi API
- [Documentación Oficial](https://docs.wompi.co/docs/colombia/inicio-rapido/)
- [Ambientes y Llaves](https://docs.wompi.co/docs/colombia/ambientes-y-llaves/)

---

## 🤝 Contribución

### Agregar Nuevos Escenarios

1. **Escribir escenario Gherkin** en `payment.feature`
2. **Implementar Step Definition** en `PaymentStepDefinitions.java`
3. **Crear Task si es necesario** en `tasks/`
4. **Crear Question si es necesario** en `questions/`
5. **Ejecutar:** `mvn verify`

### Agregar Nuevo Método de Pago

1. Crear modelo de datos en `models/`
2. Crear Task específica (ej: `CreateTransactionWithNequi.java`)
3. Actualizar `WompiConfig.java` con constantes
4. Escribir escenario en nuevo feature file

---

## 🐛 Troubleshooting

### Error: "No se encuentra el runner"
```bash
# Asegurar que Maven compile
mvn clean compile
```

### Error: "401 Unauthorized"
```bash
# Verificar llaves en WompiConfig.java
# Asegurar que sean llaves de sandbox
```

### Reportes no se generan
```bash
# Ejecutar explícitamente
mvn serenity:aggregate
```

---

## 📞 Contacto

Para preguntas sobre el proyecto:
- 📧 Email: [tu-email@example.com]
- 📝 Documentación: Ver `DOCUMENTACION_PROYECTO.md`

---

## 📄 Licencia

Este proyecto es con fines educativos y de prueba técnica.

---

## ⭐ Características Destacadas

- ✅ **ScreenPlay Pattern** implementado correctamente
- ✅ **BDD** con escenarios en español
- ✅ **Código limpio** y autodocumentado
- ✅ **Reportes profesionales** con Serenity
- ✅ **Fácil de extender** y mantener
- ✅ **CI/CD ready** (Maven)

---

**Hecho con ❤️ usando ScreenPlay Pattern y BDD**

---

## 🚦 Status del Proyecto

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Tests](https://img.shields.io/badge/tests-3%2F3-brightgreen)
![Coverage](https://img.shields.io/badge/coverage-happy%20%2B%20negative-blue)
![Java](https://img.shields.io/badge/java-11-orange)
![Pattern](https://img.shields.io/badge/pattern-ScreenPlay-purple)

