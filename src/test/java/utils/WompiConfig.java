package utils;

/**
 * Configuración centralizada para la API de Wompi
 * Contiene URLs, endpoints y llaves de prueba
 */
public class WompiConfig {

    // URLs Base
    public static final String BASE_URL = "https://api-sandbox.co.uat.wompi.dev/v1";

    // Endpoints
    public static final String TRANSACTIONS_ENDPOINT = "/transactions";
    public static final String ACCEPTANCE_TOKEN_ENDPOINT = "/merchants/pub_stagtest_g2u0HQd3ZMh05hsSgTS2lUV8t3s4mOt7";

    // Llaves de prueba - CORREGIDAS
    // El carácter Ǫ del documento era en realidad HQ (H mayúscula + Q mayúscula)
    // Verificado con: curl https://api-sandbox.co.uat.wompi.dev/v1/merchants/pub_stagtest_g2u0HQd3ZMh05hsSgTS2lUV8t3s4mOt7
    public static final String PUBLIC_KEY = "pub_stagtest_g2u0HQd3ZMh05hsSgTS2lUV8t3s4mOt7";
    public static final String PRIVATE_KEY = "prv_stagtest_5i0ZGIGiFcDQifYsXxvsny7Y37tKqFWg";
    public static final String INTEGRITY_KEY = "stagtest_integrity_nAIBuqayW70XpUqJS4qf4STYiISd89Fp";

    // Headers
    public static final String BEARER_PREFIX = "Bearer ";

    // Constantes para pruebas con PSE
    public static final String PSE_PAYMENT_METHOD = "PSE";
    public static final int PSE_USER_TYPE = 0; // 0 = PERSON, 1 = BUSINESS
    public static final String PSE_USER_LEGAL_ID_TYPE = "CC"; // CC, CE, NIT
    public static final String PSE_USER_LEGAL_ID = "123456789";
    public static final String PSE_FINANCIAL_INSTITUTION_CODE = "1040"; // Banco Agrario
    public static final String PSE_PAYMENT_DESCRIPTION = "Test payment with PSE";

    private WompiConfig() {
        // Constructor privado para evitar instanciación
    }
}
