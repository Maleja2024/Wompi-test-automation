package stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import questions.ValidateResponse;
import questions.ValidateWebhookEvent;
import tasks.CreateTransaction;
import tasks.CreateTransactionWithInvalidCredentials;
import tasks.CreateTransactionWithoutAmount;
import tasks.ValidateWebhookSignature;
import utils.WebhookValidator;
import utils.WompiConfig;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Step Definitions - Integración entre Cucumber (BDD) y ScreenPlay
 *
 * Aquí conectamos:
 * - Gherkin (scenarios en .feature) con
 * - ScreenPlay (Tasks, Questions, Actors)
 */
public class PaymentStepDefinitions {

    private Actor merchant; // El actor que representa al comercio que hace transacciones

    /**
     * Setup inicial - Se ejecuta antes de cada escenario
     * Configura el escenario con actores
     */
    @Before
    public void setTheStage() {
        // OnStage maneja los actores en el escenario
        OnStage.setTheStage(new OnlineCast());

        // Crear el actor "comercio"
        merchant = Actor.named("Comercio Wompi");

        OnStage.theActorCalled("Comercio Wompi");
    }

    @Given("que tengo acceso a la API de Wompi en ambiente sandbox")
    public void accessToWompiAPI() {
        System.out.println("✓ Configuración de ambiente sandbox: " + WompiConfig.BASE_URL);
    }

    @Given("que tengo credenciales válidas de Wompi")
    public void validCredentials() {
        // El actor ya fue configurado en @Before con credenciales válidas
        // Este step es más declarativo, la configuración real está en WompiConfig
        System.out.println("✓ Credenciales válidas configuradas");
    }

    @When("creo una transacción con el medio de pago PSE")
    public void createTransaction() {
        // El actor ejecuta la TAREA de crear una transacción
        // Esto es ScreenPlay en acción: merchant.attemptsTo(Task)
        merchant.attemptsTo(
                CreateTransaction.withDefaultPSEData()
        );
    }

    @Then("la respuesta debe ser exitosa")
    public void validateResponse() {
        // El actor verifica usando una PREGUNTA (Question)
        // Esto es ScreenPlay: actor.should(seeThat(Question, Matcher))
        merchant.should(
                seeThat("El código de estado HTTP",
                        ValidateResponse.statusCode(),
                        is(201))
                        .orComplainWith(AssertionError.class, "La transacción no se creó correctamente")
        );
    }

    @Then("el estado de la transacción debe ser {string} o {string}")
    public void validateTransactionStatus(String estado1, String estado2) {
        merchant.should(
                seeThat("El estado de la transacción",
                        ValidateResponse.transactionStatus(),
                        anyOf(equalTo(estado1), equalTo(estado2)))
        );
    }

    @Given("que tengo credenciales inválidas")
    public void invalidCredentials() {
        // Reconfigurar el actor con credenciales inválidas
        merchant = Actor.named("Comercio con credenciales inválidas");
    }

    @When("intento crear una transacción")
    public void tryCreateTransaction() {
        CreateTransactionWithInvalidCredentials.usingKey("INVALID_KEY_12345").performAs(merchant);
    }

    @Then("la API debe responder con error de autenticación")
    public void authError() {
        merchant.should(
                seeThat("El código de estado",
                        ValidateResponse.statusCode(),
                        anyOf(is(401), is(403)))
        );
    }

    @When("creo una transacción sin monto")
    public void transactionWithoutAmount() {
        // Ejecutar directamente sin Task para evitar problemas de inyección
        CreateTransactionWithoutAmount.forTesting().performAs(merchant);
    }

    @Then("la API debe responder con error de validación")
    public void validationError() {
        merchant.should(
                seeThat("El código de estado",
                        ValidateResponse.statusCode(),
                        anyOf(is(400), is(422)))
        );

        merchant.should(
                seeThat("La respuesta tiene error",
                        ValidateResponse.hasError(),
                        is(true))
        );
    }

    // ============================================
    // STEP DEFINITIONS PARA WEBHOOKS
    // ============================================

    @Given("que tengo configurada la llave de eventos de Wompi")
    public void configureEventsKey() {
        // Verificar que la llave de eventos está configurada
        String eventsKey = WompiConfig.EVENTS_KEY;
        System.out.println("✓ Llave de eventos configurada: " + eventsKey.substring(0, 20) + "...");

        // Almacenar en el actor para uso posterior
        merchant.remember("eventsKeyConfigured", true);
    }

    @When("recibo un webhook con firma válida")
    public void receiveValidWebhook() {
        // Simular un webhook real de Wompi
        String webhookPayload = "{"
                + "\"event\":\"transaction.updated\","
                + "\"data\":{"
                + "\"transaction\":{"
                + "\"id\":\"123-test-uuid\","
                + "\"status\":\"APPROVED\","
                + "\"amount_in_cents\":5000000"
                + "}"
                + "},"
                + "\"sent_at\":\"2026-05-02T10:30:00Z\","
                + "\"timestamp\":1714650600"
                + "}";

        // Calcular la firma correcta usando el WebhookValidator
        String validSignature;
        try {
            validSignature = WebhookValidator.calculateSignature(webhookPayload);
            System.out.println("✓ Firma calculada para webhook de prueba");
        } catch (Exception e) {
            throw new RuntimeException("Error calculando firma: " + e.getMessage());
        }

        // El actor valida el webhook con la firma correcta
        merchant.attemptsTo(
                ValidateWebhookSignature.of(webhookPayload, validSignature)
        );
    }

    @When("recibo un webhook con firma inválida")
    public void receiveInvalidWebhook() {
        // Simular un webhook con firma inválida (posible ataque)
        String webhookPayload = "{"
                + "\"event\":\"transaction.updated\","
                + "\"data\":{"
                + "\"transaction\":{"
                + "\"id\":\"456-fake-uuid\","
                + "\"status\":\"APPROVED\","
                + "\"amount_in_cents\":9999999"
                + "}"
                + "}"
                + "}";

        // Usar una firma completamente inválida
        String invalidSignature = "invalid_signature_attack_attempt_12345";

        // Intentar validar el webhook con firma inválida
        // Esto debería fallar y lanzar SecurityException
        try {
            merchant.attemptsTo(
                    ValidateWebhookSignature.of(webhookPayload, invalidSignature)
            );
            merchant.remember("webhookValidationFailed", false);
        } catch (SecurityException e) {
            // Se esperaba esta excepción - webhook rechazado correctamente
            merchant.remember("webhookValidationFailed", true);
            merchant.remember("securityExceptionMessage", e.getMessage());
            System.out.println("✓ Webhook rechazado correctamente: " + e.getMessage());
        }
    }

    @Then("debo poder validar la firma correctamente")
    public void validateSignatureCorrectly() {
        merchant.should(
                seeThat("El webhook es auténtico",
                        ValidateWebhookEvent.isAuthentic(),
                        is(true))
        );
    }

    @Then("el tipo de evento debe ser {string}")
    public void validateEventType(String expectedEventType) {
        merchant.should(
                seeThat("El tipo de evento del webhook",
                        ValidateWebhookEvent.eventType(),
                        equalTo(expectedEventType))
        );
    }

    @Then("el webhook debe ser rechazado por seguridad")
    public void webhookRejected() {
        Boolean wasRejected = merchant.recall("webhookValidationFailed");

        if (wasRejected == null || !wasRejected) {
            throw new AssertionError(
                    "El webhook con firma inválida NO fue rechazado - FALLO DE SEGURIDAD"
            );
        }

        System.out.println("✓ Webhook rechazado correctamente por seguridad");
    }

    @Then("debe indicar que la firma es inválida")
    public void validateInvalidSignatureMessage() {
        String errorMessage = merchant.recall("securityExceptionMessage");

        if (errorMessage == null ||
            !(errorMessage.toLowerCase().contains("inval") ||
              errorMessage.toLowerCase().contains("firma"))) {
            throw new AssertionError(
                    "El mensaje de error no indica que la firma es inválida. Mensaje recibido: " + errorMessage
            );
        }

        System.out.println("✓ Mensaje de error correcto: " + errorMessage);
    }
}
