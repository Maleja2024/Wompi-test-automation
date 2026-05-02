package models;

/**
 * Datos del cliente
 */
public class CustomerData {
    
    private String phone_number;
    private String full_name;
    private String legal_id;
    private String legal_id_type;
    
    // Constructor vacío
    public CustomerData() {
    }
    
    // Getters y Setters
    public String getPhone_number() {
        return phone_number;
    }
    
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
    
    public String getFull_name() {
        return full_name;
    }
    
    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
    
    public String getLegal_id() {
        return legal_id;
    }
    
    public void setLegal_id(String legal_id) {
        this.legal_id = legal_id;
    }
    
    public String getLegal_id_type() {
        return legal_id_type;
    }
    
    public void setLegal_id_type(String legal_id_type) {
        this.legal_id_type = legal_id_type;
    }
    
    /**
     * Builder pattern
     */
    public static class Builder {
        private CustomerData data;
        
        public Builder() {
            data = new CustomerData();
        }
        
        public Builder withPhoneNumber(String phoneNumber) {
            data.phone_number = phoneNumber;
            return this;
        }
        
        public Builder withFullName(String fullName) {
            data.full_name = fullName;
            return this;
        }
        
        public Builder withLegalId(String legalId) {
            data.legal_id = legalId;
            return this;
        }
        
        public Builder withLegalIdType(String legalIdType) {
            data.legal_id_type = legalIdType;
            return this;
        }
        
        public CustomerData build() {
            return data;
        }
    }
}

