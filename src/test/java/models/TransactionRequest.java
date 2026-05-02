package models;

/**
 * Modelo para representar una solicitud de transacción en Wompi
 * Patrón ScreenPlay: Este es un modelo de datos (no un Task ni Question)
 * Usa el patrón Builder para construcción fluida
 */
public class TransactionRequest {
    
    private int amount_in_cents;
    private String currency;
    private String customer_email;
    private PaymentMethod payment_method;
    private String reference;
    private String redirect_url;
    private CustomerData customer_data;
    private String acceptance_token;
    private String signature;

    // Constructor privado - usar Builder
    private TransactionRequest() {
    }

    /**
     * Builder pattern para facilitar la creación de transacciones
     */
    public static class Builder {
        private final TransactionRequest request;

        public Builder() {
            request = new TransactionRequest();
        }
        
        public Builder withAmount(int amountInCents) {
            request.amount_in_cents = amountInCents;
            return this;
        }
        
        public Builder withCurrency(String currency) {
            request.currency = currency;
            return this;
        }
        
        public Builder withEmail(String email) {
            request.customer_email = email;
            return this;
        }
        
        public Builder withPaymentMethod(PaymentMethod paymentMethod) {
            request.payment_method = paymentMethod;
            return this;
        }
        
        public Builder withReference(String reference) {
            request.reference = reference;
            return this;
        }
        
        public Builder withRedirectUrl(String url) {
            request.redirect_url = url;
            return this;
        }
        
        public Builder withCustomerData(CustomerData data) {
            request.customer_data = data;
            return this;
        }

        public Builder withAcceptanceToken(String token) {
            request.acceptance_token = token;
            return this;
        }

        public Builder withSignature(String signature) {
            request.signature = signature;
            return this;
        }

        public TransactionRequest build() {
            return request;
        }
    }
}
