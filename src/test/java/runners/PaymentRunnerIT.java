package runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Runner para ejecutar los tests BDD con Serenity
 *
 * ¿Qué hace este archivo?
 * - Conecta Cucumber con Serenity BDD
 * - Define dónde están los features (archivos .feature)
 * - Define dónde están los step definitions
 * - Configura opciones de ejecución y reportes
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "stepdefinitions",
        plugin = {"pretty", "html:target/cucumber-reports.html"},
        tags = ""  // Dejar vacío para ejecutar todos, o usar "@Happy" para solo happy path
)
public class PaymentRunnerIT {
}
