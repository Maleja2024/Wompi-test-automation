# language: es
@Wompi @API @Regression
Característica: Integración con API de Wompi para transacciones PSE
  Como comercio afiliado a Wompi
  Quiero poder crear transacciones de pago mediante PSE
  Para que mis clientes puedan realizar pagos de manera segura

  Antecedentes:
    Dado que tengo acceso a la API de Wompi en ambiente sandbox

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

  @Negative @Validation
  Escenario: Error por datos incompletos en la solicitud
    Dado que tengo credenciales válidas de Wompi
    Cuando creo una transacción sin monto
    Entonces la API debe responder con error de validación

