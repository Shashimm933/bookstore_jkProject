package org;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import utils.JavaUtils;

public class SignUpTest extends BaseTest {

    @Test(priority = -1)
    public void testSignUpSuccess() {
        int randomId = JavaUtils.generateRandomId();
        String randomEmail = JavaUtils.generateRandomEmail();

        Response response = given()
                .header("Content-Type", "application/json")
                .log().all()
                .body("{\"id\":" + randomId + ",\"email\":\"" + randomEmail + "\",\"password\":\"Test@123\"}")
                .when()
                .post("/signup")
                .then()
                .statusCode(200)
                .log().all().extract().response();
        Assert.assertEquals(response.jsonPath().getString("message"),"User created successfully");
    }

    @Test
    public void testSignUpFailure() {
        given()
                .header("Content-Type", "application/json")
                .log().all()
                .body("{\"id\":101,\"password\":\"Test@123\"}") // missing email
                .when()
                .post("/signup")
                .then()
                .statusCode(500)
                .log().all();// likely 422 for missing required fields in FastAPI
//                .body("detail[0].msg", containsString("Field required"))
//                .body("detail[0].loc", hasItem("email")); // optional: check the field name
    }

}

