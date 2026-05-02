package tasks;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Step;
import utils.WompiConfig;

/**
 * Task para obtener el acceptance_token de Wompi
 * El acceptance_token es requerido para crear transacciones
 */
public class GetAcceptanceToken implements Task {

    private GetAcceptanceToken() {
    }

    public static GetAcceptanceToken fromWompi() {
        return new GetAcceptanceToken();
    }

    @Override
    @Step("{0} obtiene el acceptance token")
    public <T extends Actor> void performAs(T actor) {
        Response response = SerenityRest.given()
                .baseUri(WompiConfig.BASE_URL)
                .when()
                .get(WompiConfig.ACCEPTANCE_TOKEN_ENDPOINT);

        // Extraer el acceptance_token de la respuesta
        String acceptanceToken = response.jsonPath().getString("data.presigned_acceptance.acceptance_token");

        // Almacenar en el actor para uso posterior
        actor.remember("acceptanceToken", acceptanceToken);

        System.out.println("✓ Acceptance token obtenido: " + acceptanceToken);
    }
}

