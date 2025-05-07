package org;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.utils.TestDataLoader;

import static io.restassured.RestAssured.*;

public class DeleteBookTest extends BaseTest {

    @Test
    public void testDeleteBookWithValidId() {
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .delete("/books/" + book_id)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        // Validate success message or response content (if any)
        Assert.assertEquals(response.jsonPath().getString("message"), "Book deleted successfully");
    }

    @Test
    public void testDeleteBookWithInvalidId() {
        int invalidBookId = 9999; // Non-existing book_id for deletion
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .delete("/books/" + invalidBookId)
                .then()
                .log().all()
                .statusCode(404)
                .extract()
                .response();

        // Validate that the response indicates book not found
        Assert.assertEquals(response.jsonPath().getString("detail"), "Book not found");
    }

    @Test
    public void testDeleteBookWithInvalidFormatId() {
        String invalidBookId = "invalid"; // Non-numeric book_id

        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .delete("/books/" + invalidBookId)
                .then()
                .log().all()
                .statusCode(422)
                .extract()
                .response();

        // Validate specific error message from the response
        String errorMsg = response.jsonPath().getString("detail[0].msg");
        Assert.assertEquals(errorMsg,
                "Input should be a valid integer, unable to parse string as an integer",
                "Unexpected error message for invalid book_id format");
    }

    @Test
    public void testDeleteBookWithZeroId() {
        int zeroBookId = 0; // Invalid book_id (0)
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .delete("/books/" + zeroBookId)
                .then()
                .log().all()
                .statusCode(404)
                .extract()
                .response();

        // Validate that the response indicates invalid book_id format
        Assert.assertEquals(response.jsonPath().getString("detail"), "Book not found");
    }

    @Test(dataProvider = "tokenScenarios", dataProviderClass = TestDataLoader.class)
    public void testDeleteWithInvalidTokens(String token, int expectedStatus, String expectedMessage) {
        Response response = given()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .delete("/books/" + book_id)
                .then()
                .log().all()
                .statusCode(expectedStatus)
                .extract().response();

        Assert.assertTrue(response.asString().contains(expectedMessage), "Expected message not found in response");
    }

}
