package tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.thucydides.core.annotations.Step;
import utils.WebhookValidator;

/**
 * Task del patrón ScreenPlay para validar la firma de un webhook de Wompi
 *
 * Los webhooks de Wompi incluyen una firma HMAC-SHA256 que debe validarse
 * para garantizar que:
 * - El webhook proviene realmente de Wompi (autenticidad)
 * - El payload no fue modificado en tránsito (integridad)
 *
 * Uso en un escenario:
 * <pre>
 * actor.attemptsTo(
 *     ValidateWebhookSignature.of(webhookPayload, receivedSignature)
 * );
 * </pre>
 */
public class ValidateWebhookSignature implements Task {

    private final String payload;
    private final String receivedSignature;

    private ValidateWebhookSignature(String payload, String receivedSignature) {
        this.payload = payload;
        this.receivedSignature = receivedSignature;
    }

    /**
     * Factory method para crear la tarea
     *
     * @param payload JSON completo del webhook
     * @param receivedSignature Firma recibida del webhook
     * @return Instancia de la tarea
     */
    public static ValidateWebhookSignature of(String payload, String receivedSignature) {
        return new ValidateWebhookSignature(payload, receivedSignature);
    }

    @Override
    @Step("{0} valida la firma del webhook de Wompi")
    public <T extends Actor> void performAs(T actor) {
        System.out.println("🔐 Validando firma del webhook...");

        // Validar la firma
        boolean isValid = WebhookValidator.validateSignature(payload, receivedSignature);

        // Almacenar resultado en memoria del actor
        actor.remember("webhookValid", isValid);
        actor.remember("webhookPayload", payload);

        // Extraer tipo de evento
        String eventType = WebhookValidator.extractEventType(payload);
        actor.remember("webhookEventType", eventType);

        if (isValid) {
            System.out.println("✓ Firma del webhook válida");
            System.out.println("  Tipo de evento: " + eventType);
        } else {
            System.out.println("✗ Firma del webhook INVÁLIDA - Posible ataque");
            throw new SecurityException(
                "La firma del webhook no es válida. " +
                "Esto podría indicar un intento de ataque o modificación del payload."
            );
        }
    }
}

