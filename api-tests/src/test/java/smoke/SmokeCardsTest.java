package smoke;

import base.BaseApiTest;
import config.TestConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SmokeCardsTest extends BaseApiTest {

    private static Long createdCardId;

    @Test
    @Order(1)
    void createCard() {
        var response =
            given()
                    .header("Authorization", "Bearer " + token)
                    .contentType(ContentType.JSON)
                    .body("""
                            {
                              "holderName":"Tomas Conesa",
                              "brand":"VISA",
                              "type":"CREDIT",
                              "number":"4111111111115050",
                              "expiryMonth":7,
                              "expiryYear":2030
                            }
                            """)
                    .when()
                    .post("/cards")
                    .then()
                    .statusCode(201)
                    .extract()
                    .response();

        createdCardId = response.jsonPath().getLong("cardId");
        assertThat(createdCardId).isNotNull();
    }

    @Test
    @Order(2)
    void linkCardToAccount() {
        assertThat(createdCardId).isNotNull();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/accounts/{accountId}/cards?cardId={cardId}", TestConfig.ACCOUNT_ID, createdCardId)
                .then()
                .statusCode(201);
    }

    @Test
    @Order(3)
    void listCardsByAccount() {
        assertThat(createdCardId).isNotNull();

        var response =
                given()
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/cards/{accountId}/cards", TestConfig.ACCOUNT_ID)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

        var cardIds = response.jsonPath().getList("cardId", Long.class);

        assertThat(cardIds)
                .as("La lista de tarjetas debe contener la tarjeta creada")
                .contains(createdCardId);
    }

    @Test
    @Order(4)
    void deleteCard() {
        assertThat(createdCardId).isNotNull();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/accounts/{accountId}/cards/{cardId}",
                        TestConfig.ACCOUNT_ID, createdCardId)
                .then()
                .statusCode(200);
    }


}
