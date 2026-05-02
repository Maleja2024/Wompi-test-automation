package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para calcular la firma de integridad de Wompi
 * La firma se calcula como SHA-256 de: reference^amount_in_cents^currency^integrity_key
 */
public class IntegritySignature {

    /**
     * Calcula la firma de integridad para una transacción
     *
     * @param reference Referencia única de la transacción
     * @param amountInCents Monto en centavos
     * @param currency Moneda (ej: COP)
     * @return La firma en formato hexadecimal
     */
    public static String calculate(String reference, int amountInCents, String currency) {
        String concatenated = reference + amountInCents + currency + WompiConfig.INTEGRITY_KEY;
        return toSHA256(concatenated);
    }

    /**
     * Calcula el hash SHA-256 de un string
     */
    private static String toSHA256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al calcular SHA-256", e);
        }
    }

    /**
     * Convierte bytes a hexadecimal
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}

