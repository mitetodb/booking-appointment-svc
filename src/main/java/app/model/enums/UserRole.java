package app.model.enums;

public enum UserRole {
    ADMIN("Admin"),
    DOCTOR("Doctor"),
    ASSISTANT("Assistant"),
    USER("User");

    private String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
