package base;

import config.TestConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;


public abstract class BaseApiTest {

    protected static String token;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = TestConfig.BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        token = loginAndGetToken();
    }

    private static String loginAndGetToken() {
        return given()
                .contentType(ContentType.JSON)
                .body("""
                      {
                        "email": "%s",
                        "password": "%s"
                      }
                      """.formatted(TestConfig.EMAIL, TestConfig.PASSWORD))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
    }
}
