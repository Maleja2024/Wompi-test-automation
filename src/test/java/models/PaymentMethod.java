package models;

/**
 * Objeto payment_method para Wompi API
 * Combina el tipo de pago y los datos específicos del método
 */
public class PaymentMethod {

    private String type;
    private int user_type; // 0 = PERSON, 1 = BUSINESS
    private String user_legal_id_type;
    private String user_legal_id;
    private String financial_institution_code;
    private String payment_description;

    // Constructor vacío
    public PaymentMethod() {
    }

    //Getters y Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getUser_legal_id_type() {
        return user_legal_id_type;
    }

    public void setUser_legal_id_type(String user_legal_id_type) {
        this.user_legal_id_type = user_legal_id_type;
    }

    public String getUser_legal_id() {
        return user_legal_id;
    }

    public void setUser_legal_id(String user_legal_id) {
        this.user_legal_id = user_legal_id;
    }

    public String getFinancial_institution_code() {
        return financial_institution_code;
    }

    public void setFinancial_institution_code(String financial_institution_code) {
        this.financial_institution_code = financial_institution_code;
    }

    public String getPayment_description() {
        return payment_description;
    }

    public void setPayment_description(String payment_description) {
        this.payment_description = payment_description;
    }

    /**
     * Builder para PSE
     */
    public static class Builder {
        private PaymentMethod paymentMethod;

        public Builder() {
            paymentMethod = new PaymentMethod();
        }

        public Builder withType(String type) {
            paymentMethod.type = type;
            return this;
        }

        public Builder withUserType(int userType) {
            paymentMethod.user_type = userType;
            return this;
        }

        public Builder withUserLegalIdType(String idType) {
            paymentMethod.user_legal_id_type = idType;
            return this;
        }

        public Builder withUserLegalId(String legalId) {
            paymentMethod.user_legal_id = legalId;
            return this;
        }

        public Builder withFinancialInstitutionCode(String code) {
            paymentMethod.financial_institution_code = code;
            return this;
        }

        public Builder withPaymentDescription(String description) {
            paymentMethod.payment_description = description;
            return this;
        }

        public PaymentMethod build() {
            return paymentMethod;
        }
    }
}

