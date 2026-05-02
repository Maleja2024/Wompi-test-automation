package questions;

import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

/**
 * Question (Pregunta) del patrón ScreenPlay
 * Representa una validación o consulta que el actor puede hacer
 *
 * En ScreenPlay:
 * - Questions: Obtienen información del sistema para validar
 * - Retornan valores que se pueden verificar con assertions
 * - No modifican el estado del sistema
 */
public class ValidateResponse {

    /**
     * Pregunta: ¿Cuál es el código de estado HTTP?
     */
    public static Question<Integer> statusCode() {
        return actor -> {
            Response response = SerenityRest.lastResponse();
            return response.getStatusCode();
        };
    }

    /**
     * Pregunta: ¿Cuál es el estado de la transacción?
     */
    public static Question<String> transactionStatus() {
        return actor -> {
            Response response = SerenityRest.lastResponse();
            return response.jsonPath().getString("data.status");
        };
    }

    /**
     * Pregunta: ¿La respuesta contiene un error?
     */
    public static Question<Boolean> hasError() {
        return actor -> {
            Response response = SerenityRest.lastResponse();
            return response.jsonPath().get("error") != null;
        };
    }
}
