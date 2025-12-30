package config;

public class TestConfig {

    public static final String BASE_URL =
            System.getenv().getOrDefault("BASE_URL", "http://localhost:8080");

    public static final String EMAIL =
            System.getenv().getOrDefault("TEST_EMAIL", "test@mail.com");

    public static final String PASSWORD =
            System.getenv().getOrDefault("TEST_PASSWORD", "123456");

    public static final long ACCOUNT_ID =
            Long.parseLong(System.getenv().getOrDefault("TEST_ACCOUNT_ID", "29"));

    public static final long USER_ID =
            Long.parseLong(System.getenv().getOrDefault("TEST_USER_ID", "38"));
}
