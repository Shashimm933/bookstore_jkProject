package org;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import payload.BookPayload;
import static io.restassured.RestAssured.given;

import utils.JavaUtils;
import org.utils.RetryAnalyzer;
import org.utils.TestDataLoader;

public class AddBookTest extends BaseTest {
    JsonPath json;
    String payload, randomName;
    int randomId;

    @BeforeClass
    public void setUP(){
        randomId = JavaUtils.generateRandomId();
        randomName = JavaUtils.generateRandomString("Book");
        payload = BookPayload.createBookPayload(randomId, randomName, "Author A", 2024, "Good book");

    }
        @Test(retryAnalyzer = RetryAnalyzer.class)
        public void testCreateBookSuccess() {
            Response response = given(getRequestWithToken())
                    .log().all()
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .when()
                    .post("/books/")
                    .then().statusCode(200)//issue-> creation should throw 201
                    .log().all().extract().response();
            json = response.jsonPath();
            book_id= json.getInt("id");
            Assert.assertEquals(json.getString("name"), randomName, "Book name mismatch");
            Assert.assertEquals(json.getInt("id"), randomId, "Id mismatch");
        }

    @Test
    public void testMissingName() {
        String payload = BookPayload.createBookPayload(102, "", "Author A", 2024, "Missing name");

        ValidatableResponse response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/books/")
                .then();

        Assert.assertEquals(response.extract().statusCode(), 500, "Expected 400 for missing name, got 500");
        // TODO: Change to 400 when name validation is implemented
    }

    @Test
    public void testMissingAuthor() {
        String payload = BookPayload.createBookPayload(103, "Book A", "", 2024, "Missing author");

        ValidatableResponse response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/books/")
                .then();

        Assert.assertEquals(response.extract().statusCode(), 500, "Expected 400 for missing author, got 500");
    }

    @Test
    public void testMissingPublishedYear() {
        String payload = "{\"id\":104,\"name\":\"Book A\",\"author\":\"Author A\",\"book_summary\":\"summary only\"}";

        ValidatableResponse response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/books/")
                .then();

        Assert.assertEquals(response.extract().statusCode(), 500, "Expected 400 for missing published_year, got 500");
    }

    @Test
    public void testInvalidYear() {
        String payload = BookPayload.createBookPayload(105, "Book A", "Author A", 3024, "Future year");

        ValidatableResponse response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/books/")
                .then();

        Assert.assertEquals(response.extract().statusCode(), 500, "Expected 400 for future year, got 500");
    }

    @Test
    public void testDuplicateBookId() {
        String payload = BookPayload.createBookPayload(101, "Another Book", "Another Author", 2023, "Duplicate ID");

        ValidatableResponse response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/books/")
                .then();

        Assert.assertEquals(response.extract().statusCode(), 500, "Expected 409 for duplicate ID, got 500");
    }

    @Test
    public void testWrongDataTypeForId() {
        String payload = "{\"id\":\"wrong\",\"name\":\"Book B\",\"author\":\"Author B\",\"published_year\":2023,\"book_summary\":\"test\"}";

        ValidatableResponse response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/books/")
                .then();

        Assert.assertEquals(response.extract().statusCode(), 500, "Expected 400 for invalid id type, got 500");
    }

    @Test
    public void testOverSizedSummary() {
        String longSummary = "a".repeat(5001); // Assuming 5000 char limit
        String payload = BookPayload.createBookPayload(106, "Book B", "Author B", 2023, longSummary);

        ValidatableResponse response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/books/")
                .then();

        Assert.assertEquals(response.extract().statusCode(), 500, "Expected 400 for oversized summary, got 500");
    }

    @Test
    public void testInvalidJsonFormat() {
        String payload = "{\"id\":107, \"name\":\"Book B\", \"author\":\"Author B\", "; // broken JSON

        ValidatableResponse response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/books/")
                .then();

        Assert.assertEquals(response.extract().statusCode(), 422, "Expected 422 for malformed JSON");
    }

    @Test
    public void testNullValues() {
        String payload = "{\"id\":108,\"name\":null,\"author\":\"Author B\",\"published_year\":2023,\"book_summary\":\"summary\"}";

        ValidatableResponse response = given(getRequestWithToken())
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/books/")
                .then();

        Assert.assertEquals(response.extract().statusCode(), 500, "Expected 400 for null name, got 500");
    }

    @Test(dataProvider = "tokenScenarios", dataProviderClass = TestDataLoader.class)
    public void testPostWithInvalidTokens(String token, int expectedStatus, String expectedMessage) {
        String createPayload = BookPayload.createBookPayload(200, "Book X", "Author X", 2024, "Token Test");
        Response response = given()
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .body(createPayload)
                .log().all()
                .when()
                .post("/books/")
                .then()
                .log().all()
                .statusCode(expectedStatus)
                .extract().response();

        Assert.assertTrue(response.asString().contains(expectedMessage), "Expected message not found in response");
    }
}



