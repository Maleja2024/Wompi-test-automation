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
 * Task para crear transacciones con credenciales inválidas
 * Escenario alterno: validar manejo de errores
 */
public class CreateTransactionWithInvalidCredentials implements Task {
    
    private final String invalidKey;
    
    private CreateTransactionWithInvalidCredentials(String invalidKey) {
        this.invalidKey = invalidKey;
    }
    
    public static CreateTransactionWithInvalidCredentials usingKey(String invalidKey) {
        return new CreateTransactionWithInvalidCredentials(invalidKey);
    }
    
    @Override
    @Step("{0} intenta crear una transacción con credenciales inválidas")
    public <T extends Actor> void performAs(T actor) {
        // Crear payment_method simple para auth test
        PaymentMethod paymentMethod = new PaymentMethod.Builder()
                .withType("PSE")
                .build();

        // Transacción simple para probar autenticación
        TransactionRequest transaction = new TransactionRequest.Builder()
                .withAmount(5000000)
                .withCurrency("COP")
                .withEmail("test@test.com")
                .withPaymentMethod(paymentMethod)
                .withReference("INVALID_TEST_" + System.currentTimeMillis())
                .build();
        
        String jsonBody = new Gson().toJson(transaction);
        
        SerenityRest.given()
                .baseUri(WompiConfig.BASE_URL)
                .header("Authorization", WompiConfig.BEARER_PREFIX + invalidKey)
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(WompiConfig.TRANSACTIONS_ENDPOINT);
    }
}

