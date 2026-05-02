package models;

import java.util.Map;

/**
 * Modelo para representar un webhook/evento enviado por Wompi
 *
 * Wompi envía webhooks cuando:
 * - Una transacción cambia de estado
 * - Un pago es aprobado/rechazado
 * - Ocurren eventos importantes en transacciones
 *
 * Estructura típica:
 * {
 *   "event": "transaction.updated",
 *   "data": { ... },
 *   "sent_at": "2026-05-02T10:30:00Z",
 *   "timestamp": 1714650600,
 *   "signature": { "checksum": "abc123..." }
 * }
 */
public class WebhookEvent {

    private String event;
    private Map<String, Object> data;
    private String sent_at;
    private long timestamp;
    private WebhookSignature signature;

    // Constructor vacío
    public WebhookEvent() {
    }

    // Getters y Setters
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getSent_at() {
        return sent_at;
    }

    public void setSent_at(String sent_at) {
        this.sent_at = sent_at;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public WebhookSignature getSignature() {
        return signature;
    }

    public void setSignature(WebhookSignature signature) {
        this.signature = signature;
    }

    /**
     * Clase interna para representar la firma del webhook
     */
    public static class WebhookSignature {
        private String[] properties;
        private String checksum;

        public String[] getProperties() {
            return properties;
        }

        public void setProperties(String[] properties) {
            this.properties = properties;
        }

        public String getChecksum() {
            return checksum;
        }

        public void setChecksum(String checksum) {
            this.checksum = checksum;
        }
    }

    /**
     * Builder para construcción fluida de eventos (útil en tests)
     */
    public static class Builder {
        private final WebhookEvent event;

        public Builder() {
            event = new WebhookEvent();
        }

        public Builder withEventType(String eventType) {
            event.event = eventType;
            return this;
        }

        public Builder withData(Map<String, Object> data) {
            event.data = data;
            return this;
        }

        public Builder withSentAt(String sentAt) {
            event.sent_at = sentAt;
            return this;
        }

        public Builder withTimestamp(long timestamp) {
            event.timestamp = timestamp;
            return this;
        }

        public Builder withSignature(WebhookSignature signature) {
            event.signature = signature;
            return this;
        }

        public WebhookEvent build() {
            return event;
        }
    }
}

