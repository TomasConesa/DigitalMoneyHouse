package smoke;

import base.BaseApiTest;
import config.TestConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransferencesTest extends BaseApiTest {

    private static Long createdTransferId;

    private static final Long CARD_ID = 18L;

    @Test
    @Order(1)
    void addMoneyFromCard() {
        var response =
                given()
                        .header("Authorization", "Bearer " + token)
                        .contentType(ContentType.JSON)
                        .body("""
                            {
                                "cardId": %d,
                                "amount": 500.00,
                                "description": "Carga desde tarjeta para test user"
                            }
                            """.formatted(CARD_ID))
                        .when()
                        .post("/accounts/{accountId}/transferences", TestConfig.ACCOUNT_ID)
                        .then()
                        .statusCode(201)
                        .extract()
                        .response();

        createdTransferId = response.jsonPath().getLong("id");

        assertThat(createdTransferId).isNotNull();

        String responseAmount = response.jsonPath().getString("amount");
        String normalized = responseAmount.replace(",",".");
        BigDecimal amount = new BigDecimal(normalized);
        assertThat(amount).isEqualByComparingTo("500.00");

        assertThat(response.jsonPath().getString("description")).isEqualTo("Carga desde tarjeta para test user");
        assertThat(response.jsonPath().getString("createdAt")).isNotNull();
    }

    @Test
    @Order(2)
    void getActivity() {
        assertThat(createdTransferId).isNotNull();

        var response =
                given()
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/accounts/{accountId}/activity", TestConfig.ACCOUNT_ID)
                        .then().statusCode(200)
                        .extract()
                        .response();

        var ids = response.jsonPath().getList("id", Long.class);

        assertThat(ids)
                .as("La lista de actividades debe contener la transferencia creada")
                .isNotNull()
                .contains(createdTransferId);
    }

    @Test
    @Order(3)
    void getTransactionDetail() {
        assertThat(createdTransferId).isNotNull();

        var response =
                given()
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/accounts/{accountId}/activity/{transferId}", TestConfig.ACCOUNT_ID, createdTransferId)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        assertThat(response.jsonPath().getLong("id")).isEqualTo(createdTransferId);

        String responseAmount = response.jsonPath().getString("amount");
        String normalized = responseAmount.replace(",",".");
        BigDecimal amount = new BigDecimal(normalized);
        assertThat(amount).isEqualByComparingTo("500.00");

        assertThat(response.jsonPath().getString("description")).isEqualTo("Carga desde tarjeta para test user");
        assertThat(response.jsonPath().getString("createdAt")).isNotNull();
    }
}
