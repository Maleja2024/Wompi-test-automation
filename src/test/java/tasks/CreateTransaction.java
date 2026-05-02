package tasks;

import com.google.gson.Gson;
import io.restassured.http.ContentType;
import models.CustomerData;
import models.PaymentMethod;
import models.TransactionRequest;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Step;
import utils.IntegritySignature;
import utils.WompiConfig;

/**
 * Task (Tarea) del patrón ScreenPlay
 * Representa la acción de crear una transacción en Wompi
 *
 * En ScreenPlay:
 * - Tasks: Son acciones de alto nivel que un actor puede realizar
 * - Deben implementar la interfaz Task
 * - Usan Interactions (como Post, Get) para interactuar con APIs
 */
public class CreateTransaction implements Task {

    private final int amountInCents;
    private final String email;
    private final String reference;

    /**
     * Constructor privado - usar el método estático withDefaultPSEData()
     */
    private CreateTransaction(int amountInCents, String email, String reference) {
        this.amountInCents = amountInCents;
        this.email = email;
        this.reference = reference;
    }

    /**
     * Método factory con valores por defecto para PSE
     */
    public static CreateTransaction withDefaultPSEData() {
        return new CreateTransaction(5000000, "test@test.com", "TEST_REF_" + System.currentTimeMillis());
    }

    @Override
    @Step("{0} crea una transacción con PSE por #amountInCents centavos")
    public <T extends Actor> void performAs(T actor) {
        // Primero obtener el acceptance_token
        GetAcceptanceToken.fromWompi().performAs(actor);
        String acceptanceToken = actor.recall("acceptanceToken");

        // Calcular la firma de integridad
        String signature = IntegritySignature.calculate(reference, amountInCents, "COP");

        // Construir el payment_method completo con todos los datos de PSE
        PaymentMethod paymentMethod = new PaymentMethod.Builder()
                .withType(WompiConfig.PSE_PAYMENT_METHOD)
                .withUserType(WompiConfig.PSE_USER_TYPE)
                .withUserLegalIdType(WompiConfig.PSE_USER_LEGAL_ID_TYPE)
                .withUserLegalId(WompiConfig.PSE_USER_LEGAL_ID)
                .withFinancialInstitutionCode(WompiConfig.PSE_FINANCIAL_INSTITUTION_CODE)
                .withPaymentDescription(WompiConfig.PSE_PAYMENT_DESCRIPTION)
                .build();

        // Construir datos del cliente
        CustomerData customerData = new CustomerData.Builder()
                .withPhoneNumber("3001234567")
                .withFullName("Juan Perez")
                .withLegalId(WompiConfig.PSE_USER_LEGAL_ID)
                .withLegalIdType(WompiConfig.PSE_USER_LEGAL_ID_TYPE)
                .build();

        // Construir la transacción completa
        TransactionRequest transaction = new TransactionRequest.Builder()
                .withAmount(amountInCents)
                .withCurrency("COP")
                .withEmail(email)
                .withPaymentMethod(paymentMethod)
                .withReference(reference)
                .withRedirectUrl("https://example.com/redirect")
                .withCustomerData(customerData)
                .withAcceptanceToken(acceptanceToken)
                .withSignature(signature)
                .build();

        // Convertir a JSON
        String jsonBody = new Gson().toJson(transaction);
        
        System.out.println("=== DEBUG: Request PSE ===");
        System.out.println("JSON: " + jsonBody);

        // Realizar la llamada POST usando Serenity REST
        SerenityRest.given()
                .baseUri(WompiConfig.BASE_URL)
                .header("Authorization", WompiConfig.BEARER_PREFIX + WompiConfig.PRIVATE_KEY)
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(WompiConfig.TRANSACTIONS_ENDPOINT);

        System.out.println("Response Status: " + SerenityRest.lastResponse().getStatusCode());
        System.out.println("Response: " + SerenityRest.lastResponse().getBody().asString());
        System.out.println("=== FIN DEBUG ===");
    }
}
