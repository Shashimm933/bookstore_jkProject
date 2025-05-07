package org;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import payload.BookPayload;
import utils.JavaUtils;
import org.utils.TestDataLoader;

import static io.restassured.RestAssured.*;

public class UpdateBookTest extends BaseTest {

    int updatedYear;

    @BeforeClass
    public void setUp(){
        randomName = JavaUtils.generateRandomString("Book");
        updatedAuthor = JavaUtils.generateRandomString("Author");
        updatedYear = 2024;
        updatedSummary = "Updated summary " + JavaUtils.generateRandomString("Info");
        payload = BookPayload.updateBookPayload(randomName, updatedAuthor, updatedYear, updatedSummary);
    }

    @Test
    public void testUpdateBook() {
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .body(payload)
                .when()
                .put("/books/" + book_id)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
        Assert.assertEquals(response.jsonPath().getString("name"), randomName, "Name mismatch");
        Assert.assertEquals(response.jsonPath().getString("author"), updatedAuthor, "Author mismatch");
        Assert.assertEquals((int) response.jsonPath().getInt("published_year"), updatedYear, "Year mismatch");
        Assert.assertEquals(response.jsonPath().getString("book_summary"), updatedSummary, "Summary mismatch");
    }

    @Test
    public void testUpdateBookWithWrongBookId() {
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .body(payload)
                .when()
                .put("/books/" + 0)
                .then()
                .log().all()
                .statusCode(404)
                .extract()
                .response();
        Assert.assertEquals(response.jsonPath().getString("detail"), "Book not found", "Name mismatch");
    }

    @Test
    public void testUpdateBookWithWrongBookIdAsString() {
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .log().all()
                .body(payload)
                .when()
                .put("/books/hi")
                .then()
                .log().all()
                .statusCode(422) // Unprocessable Entity
                .extract()
                .response();
        String errorMessage = response.jsonPath().getString("detail[0].msg");
        Assert.assertEquals(errorMessage,
                "Input should be a valid integer, unable to parse string as an integer",
                "Expected integer parsing error");
    }
    @Test(priority = -1)
    public void testUpdateBookInvalidYear() {
        String payload = BookPayload.updateBookPayload("Book A", "Author A", 3024, "Future year");
        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .put("/books/" + book_id)
                .then()
                .statusCode(200)//validation missing for future dates
                .extract().response();

        Assert.assertEquals((int) response.jsonPath().getInt("published_year"), 3024, "Year mismatch");
    }

    @Test
    public void testUpdateBookWithNullName() {
        String payload = "{\"name\":null, \"author\":\"Author A\", \"published_year\":2023, \"book_summary\":\"summary\"}";

        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .put("/books/" + book_id)
                .then()
                .statusCode(500) //validation missing
                .extract().response();

//        Assert.assertTrue(response.asString().contains("Name cannot be null"));
    }

    @Test
    public void testUpdateBookMalformedJson() {
        String malformedPayload = "{\"name\":\"Book B\", \"author\":\"Author B\""; // Broken JSON

        Response response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(malformedPayload)
                .when()
                .put("/books/" + book_id)
                .then()
                .statusCode(422)//validation missing
                .extract().response();

//        Assert.assertTrue(response.asString().contains("Malformed JSON"));
    }

    @Test(dataProvider = "tokenScenarios", dataProviderClass = TestDataLoader.class)
    public void testPutWithInvalidTokens(String token, int expectedStatus, String expectedMessage) {
        Response response = given()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .body(payload)
                .log().all()
                .when()
                .put("/books/" + book_id)
                .then()
                .log().all()
                .statusCode(expectedStatus)
                .extract().response();

        Assert.assertTrue(response.asString().contains(expectedMessage), "Expected message not found in response");
    }


}

