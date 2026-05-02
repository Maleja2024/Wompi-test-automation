package stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import questions.ValidateResponse;
import tasks.CreateTransaction;
import tasks.CreateTransactionWithInvalidCredentials;
import tasks.CreateTransactionWithoutAmount;
import utils.WompiConfig;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.*;

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
}
