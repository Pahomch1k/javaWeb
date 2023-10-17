package step.learning.services.validation;

import com.google.gson.JsonObject;

public class DataValidation {

    public static ValidationResult validate(JsonObject data) {
        if (data == null) {
            return new ValidationResult(false, "Data is missing.");
        }
        if (!data.has("name") || data.get("name").getAsString().trim().isEmpty()) {
            return new ValidationResult(false, "Field 'name' is missing or empty.");
        }
        if (!data.has("phone") || data.get("phone").getAsString().trim().isEmpty()) {
            return new ValidationResult(false, "Field 'phone' is missing or empty.");
        }
        // Add regex validation for phone if necessary
        return new ValidationResult(true, "Data is valid.");
    }
}