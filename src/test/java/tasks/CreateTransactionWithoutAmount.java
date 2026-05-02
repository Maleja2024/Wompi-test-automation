package tasks;

import com.google.gson.Gson;
import io.restassured.http.ContentType;
import models.PaymentMethod;
import models.TransactionRequest;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Step;
import utils.WompiConfig;

/**
 * Task para crear transacciones sin monto (datos incompletos)
 * Escenario alterno: validar validación de campos requeridos
 */
public class CreateTransactionWithoutAmount implements Task {

    private CreateTransactionWithoutAmount() {
    }

    public static CreateTransactionWithoutAmount forTesting() {
        return new CreateTransactionWithoutAmount();
    }

    @Override
    @Step("{0} intenta crear una transacción sin monto")
    public <T extends Actor> void performAs(T actor) {
        // Crear payment_method simple
        PaymentMethod paymentMethod = new PaymentMethod.Builder()
                .withType("PSE")
                .build();

        // Transacción incompleta (sin amount_in_cents)
        TransactionRequest transaction = new TransactionRequest.Builder()
                // .withAmount() <- INTENCIONALMENTE OMITIDO
                .withCurrency("COP")
                .withEmail("test@test.com")
                .withPaymentMethod(paymentMethod)
                .withReference("NO_AMOUNT_TEST_" + System.currentTimeMillis())
                .build();

        String jsonBody = new Gson().toJson(transaction);

        SerenityRest.given()
                .baseUri(WompiConfig.BASE_URL)
                .header("Authorization", WompiConfig.BEARER_PREFIX + WompiConfig.PRIVATE_KEY)
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(WompiConfig.TRANSACTIONS_ENDPOINT);
    }
}

