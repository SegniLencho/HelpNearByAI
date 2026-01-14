package com.helpnearby.dto;

import java.util.List;
import java.util.Map;

/**
 * Metadata for the request creation form to help frontend applications
 * display proper placeholders, validation rules, and options
 */
public class RequestFormMetadata {
    
    private Map<String, FieldMetadata> fields;
    private List<String> categories;
    private List<String> urgencyLevels;
    private List<String> statusOptions;
    
    public RequestFormMetadata() {}
    
    public RequestFormMetadata(Map<String, FieldMetadata> fields, 
                              List<String> categories, 
                              List<String> urgencyLevels, 
                              List<String> statusOptions) {
        this.fields = fields;
        this.categories = categories;
        this.urgencyLevels = urgencyLevels;
        this.statusOptions = statusOptions;
    }
    
    public static class FieldMetadata {
        private String placeholder;
        private String type;
        private boolean required;
        private Integer maxLength;
        private String validation;
        private String hint;
        
        public FieldMetadata() {}
        
        public FieldMetadata(String placeholder, String type, boolean required, 
                           Integer maxLength, String validation, String hint) {
            this.placeholder = placeholder;
            this.type = type;
            this.required = required;
            this.maxLength = maxLength;
            this.validation = validation;
            this.hint = hint;
        }
        
        // Getters and setters
        public String getPlaceholder() { return placeholder; }
        public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        
        public Integer getMaxLength() { return maxLength; }
        public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }
        
        public String getValidation() { return validation; }
        public void setValidation(String validation) { this.validation = validation; }
        
        public String getHint() { return hint; }
        public void setHint(String hint) { this.hint = hint; }
    }
    
    // Getters and setters
    public Map<String, FieldMetadata> getFields() { return fields; }
    public void setFields(Map<String, FieldMetadata> fields) { this.fields = fields; }
    
    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
    
    public List<String> getUrgencyLevels() { return urgencyLevels; }
    public void setUrgencyLevels(List<String> urgencyLevels) { this.urgencyLevels = urgencyLevels; }
    
    public List<String> getStatusOptions() { return statusOptions; }
    public void setStatusOptions(List<String> statusOptions) { this.statusOptions = statusOptions; }
}