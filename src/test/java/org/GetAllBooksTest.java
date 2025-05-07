package org;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;

public class GetAllBooksTest extends BaseTest{

    @Test
    public void validateFirstBookEntry_PositiveCase() {
        Response response = given(getRequestWithToken())
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<Map<String, Object>> books = response.jsonPath().getList("$");
        Optional<Map<String, Object>> firstBookOpt = books.stream()
                .filter(book -> Integer.valueOf(1).equals(book.get("id")))
                .findFirst();
        Assert.assertTrue(firstBookOpt.isPresent(), "Book with ID 1 should be present");
        Map<String, Object> book = firstBookOpt.get();
        Assert.assertEquals(book.get("name"), "Shashi", "Name should match");
        Assert.assertEquals(book.get("author"), "Author A", "Author should match");
        Assert.assertEquals(book.get("published_year"), 2024, "Published year should be 2024");
        Assert.assertEquals(book.get("book_summary"), "Good book", "Book summary should match");
    }

    @Test
    public void validateUniqueBookIds() {
        Response response = given(getRequestWithToken())
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<Map<String, Object>> books = response.jsonPath().getList("$");

        // Check that all books have unique IDs
        long duplicateIdsCount = books.stream()
                .map(book -> book.get("id"))
                .distinct()
                .count();

        Assert.assertEquals(duplicateIdsCount, books.size(), "Book IDs should be unique");
    }

}
