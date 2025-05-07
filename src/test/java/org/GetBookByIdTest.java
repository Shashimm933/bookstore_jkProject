package org;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.utils.TestDataLoader;

import static io.restassured.RestAssured.*;

public class GetBookByIdTest extends BaseTest {

    @Test
    public void testGetBookAndValidateAllFields() {
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .get("/books/" + book_id)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertEquals(response.jsonPath().getString("name"), randomName, "Name mismatch");
        Assert.assertEquals(response.jsonPath().getString("author"), updatedAuthor, "Author mismatch");
        Assert.assertEquals(response.jsonPath().getInt("published_year"), 2024, "Published year mismatch");
        Assert.assertEquals(response.jsonPath().getString("book_summary"), updatedSummary, "Summary mismatch");
        Assert.assertEquals(response.jsonPath().getInt("id"), book_id, "ID mismatch");
    }

    @Test
    public void testGetBookWithInvalidIdAsString() {
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .get("/books/abc")
                .then()
                .log().all()
                .statusCode(422)
                .extract()
                .response();

        Assert.assertTrue(response.asString().contains("Input should be a valid integer"), "Error message mismatch");
    }

    @Test
    public void testGetBookWithNonExistentId() {
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .get("/books/999999")
                .then()
                .log().all()
                .statusCode(404)
                .extract()
                .response();

        Assert.assertTrue(response.asString().contains("Book not found"), "Error message mismatch");
    }

    @Test
    public void testWrongMethodNameGetBookById() {
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .post("/books/" + book_id)
                .then()
                .log().all()
                .statusCode(405)
                .extract()
                .response();

        Assert.assertTrue(response.asString().toLowerCase().contains("method not allowed"), "Expected method not allowed error");
    }

    @Test
    public void testGetBookWithZeroId() {
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .get("/books/0")
                .then()
                .log().all()
                .statusCode(404)
                .extract()
                .response();

        Assert.assertTrue(response.asString().contains("Book not found"), "Expected not found error");
    }

    @Test
    public void testGetBookWithNegativeId() {
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .get("/books/" + -1) // Using negative ID
                .then()
                .log().all()
                .statusCode(404) // Unprocessable Entity (or whatever your API returns)
                .extract()
                .response();

        // Validate the JSON structure and message
        String actualType = response.jsonPath().getString("detail");
        Assert.assertEquals(actualType, "Book not found", "Validation type mismatch");
    }

    @Test
    public void testWithoutToken() {
        Response response = given()
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .get("/books/" + book_id)
                .then()
                .log().all()
                .statusCode(403) // Unauthorized
                .extract().response();

        Assert.assertTrue(response.asString().contains("Not authenticated"), "Expected not authenticated message");
    }

    @Test(dataProvider = "tokenScenarios", dataProviderClass = TestDataLoader.class)
    public void testGetWithInvalidTokens(String token, int expectedStatus, String expectedMessage) {
        Response response = given()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .log().all()
                .when()
                .get("/books/" + book_id)
                .then()
                .log().all()
                .statusCode(expectedStatus)
                .extract().response();

        Assert.assertTrue(response.asString().contains(expectedMessage), "Expected message not found in response");
    }









}

