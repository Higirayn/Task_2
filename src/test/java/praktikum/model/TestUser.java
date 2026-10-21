package praktikum.model;

import java.util.Map;
import java.util.UUID;

public class TestUser {
    private final String email;
    private final String password;
    private final String name;

    public TestUser(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static TestUser unique() {
        return new TestUser("student-" + UUID.randomUUID() + "@example.com", "Password123!", "Александр");
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public Map<String, String> registration() {
        return Map.of("email", email, "password", password, "name", name);
    }
    public Map<String, String> credentials() {
        return Map.of("email", email, "password", password);
    }
}
