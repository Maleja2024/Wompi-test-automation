package utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Validador de webhooks/eventos de Wompi
 * <p>
 * Wompi envía notificaciones (webhooks) cuando las transacciones cambian de estado.
 * Cada webhook incluye una firma HMAC-SHA256 para verificar autenticidad.
 * <p>
 * Uso:
 * - Validar que el webhook proviene de Wompi (no es un ataque)
 * - Verificar que el payload no fue modificado en tránsito
 * <p>
 * Ejemplo:
 * <pre>
 * String payload = "{\"event\":\"transaction.updated\",\"data\":{...}}";
 * String signature = request.getHeader("X-Signature");
 *
 * if (WebhookValidator.validateSignature(payload, signature)) {
 *     // Webhook legítimo - procesar evento
 * }
 * </pre>
 */
public class WebhookValidator {

    private static final String ALGORITHM = "HmacSHA256";

    /**
     * Valida la firma HMAC-SHA256 de un webhook de Wompi
     *
     * @param payload           JSON completo recibido del webhook
     * @param receivedSignature Firma enviada por Wompi (típicamente en header X-Signature)
     * @return true si la firma es válida, false en caso contrario
     */
    public static boolean validateSignature(String payload, String receivedSignature) {
        if (payload == null || receivedSignature == null) {
            return false;
        }

        try {
            String calculatedSignature = calculateSignature(payload);
            return calculatedSignature.equals(receivedSignature);
        } catch (Exception e) {
            System.err.println("❌ Error validando firma del webhook: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calcula la firma HMAC-SHA256 del payload usando la llave de eventos
     *
     * @param payload JSON del webhook
     * @return Firma en formato hexadecimal
     * @throws NoSuchAlgorithmException Si el algoritmo no está disponible
     * @throws InvalidKeyException      Si la llave es inválida
     */
    public static String calculateSignature(String payload) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(
                WompiConfig.EVENTS_KEY.getBytes(StandardCharsets.UTF_8),
                ALGORITHM
        );
        mac.init(secretKey);

        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    /**
     * Convierte un array de bytes a representación hexadecimal
     *
     * @param bytes Array de bytes
     * @return String en formato hexadecimal
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Extrae el tipo de evento de un payload JSON de webhook
     *
     * @param payload JSON del webhook
     * @return Tipo de evento (ej: "transaction.updated")
     */
    public static String extractEventType(String payload) {
        // Implementación simple - en producción usar un parser JSON
        if (payload.contains("\"event\":\"")) {
            int start = payload.indexOf("\"event\":\"") + 9;
            int end = payload.indexOf("\"", start);
            if (end > start) {
                return payload.substring(start, end);
            }
        }
        return "unknown";
    }

    /**
     * Verifica si el tipo de evento es uno esperado
     *
     * @param eventType Tipo de evento a verificar
     * @return true si es un evento conocido
     */
    public static boolean isKnownEventType(String eventType) {
        return eventType.equals(WompiConfig.EVENT_TRANSACTION_UPDATED) ||
                eventType.equals(WompiConfig.EVENT_PAYMENT_APPROVED) ||
                eventType.equals(WompiConfig.EVENT_PAYMENT_DECLINED);
    }
}

