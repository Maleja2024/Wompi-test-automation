package questions;

import net.serenitybdd.screenplay.Question;
import utils.WompiConfig;

/**
 * Questions del patrón ScreenPlay para validar webhooks de Wompi
 *
 * Permite al actor verificar:
 * - Si un webhook es auténtico (firma válida)
 * - El tipo de evento recibido
 * - Si el evento es de un tipo conocido
 *
 * Uso en assertions:
 * <pre>
 * actor.should(
 *     seeThat(ValidateWebhookEvent.isAuthentic(), is(true)),
 *     seeThat(ValidateWebhookEvent.eventType(), equalTo("transaction.updated"))
 * );
 * </pre>
 */
public class ValidateWebhookEvent {

    /**
     * Question: ¿El webhook es auténtico (firma válida)?
     *
     * @return true si la firma fue validada correctamente
     */
    public static Question<Boolean> isAuthentic() {
        return actor -> {
            Boolean isValid = actor.recall("webhookValid");
            return isValid != null && isValid;
        };
    }

    /**
     * Question: ¿Cuál es el tipo de evento del webhook?
     *
     * @return Tipo de evento (ej: "transaction.updated")
     */
    public static Question<String> eventType() {
        return actor -> {
            String eventType = actor.recall("webhookEventType");
            return eventType != null ? eventType : "unknown";
        };
    }

    /**
     * Question: ¿El evento es de un tipo conocido/esperado?
     *
     * @return true si el evento es uno de los tipos configurados
     */
    public static Question<Boolean> isKnownEventType() {
        return actor -> {
            String eventType = actor.recall("webhookEventType");
            if (eventType == null) {
                return false;
            }

            return eventType.equals(WompiConfig.EVENT_TRANSACTION_UPDATED) ||
                   eventType.equals(WompiConfig.EVENT_PAYMENT_APPROVED) ||
                   eventType.equals(WompiConfig.EVENT_PAYMENT_DECLINED);
        };
    }

    /**
     * Question: ¿El webhook indica una transacción actualizada?
     *
     * @return true si el evento es transaction.updated
     */
    public static Question<Boolean> isTransactionUpdated() {
        return actor -> {
            String eventType = actor.recall("webhookEventType");
            return WompiConfig.EVENT_TRANSACTION_UPDATED.equals(eventType);
        };
    }

    /**
     * Question: ¿El webhook indica un pago aprobado?
     *
     * @return true si el evento es payment.approved
     */
    public static Question<Boolean> isPaymentApproved() {
        return actor -> {
            String eventType = actor.recall("webhookEventType");
            return WompiConfig.EVENT_PAYMENT_APPROVED.equals(eventType);
        };
    }

    /**
     * Question: ¿El webhook indica un pago rechazado?
     *
     * @return true si el evento es payment.declined
     */
    public static Question<Boolean> isPaymentDeclined() {
        return actor -> {
            String eventType = actor.recall("webhookEventType");
            return WompiConfig.EVENT_PAYMENT_DECLINED.equals(eventType);
        };
    }

    /**
     * Question: Obtiene el payload completo del webhook
     *
     * @return JSON del webhook
     */
    public static Question<String> payload() {
        return actor -> {
            String payload = actor.recall("webhookPayload");
            return payload != null ? payload : "";
        };
    }
}

