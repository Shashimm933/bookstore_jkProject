package org;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BaseTest {

    public static String payload, randomName, updatedAuthor, updatedSummary;
    public static int book_id;

    public static String baseURI;
    public static String token;
    public static String username;
    public static String password;

    public static Logger log;
    public static Properties prop;

    @BeforeClass
    public void setup() {
        // Initialize logger
        log = Logger.getLogger("API_Logger");
        PropertyConfigurator.configure(System.getProperty("user.dir") + "/src/main/resources/config/log4j.properties");

        // Load configuration
        loadConfig();

        // Set RestAssured base URI
        RestAssured.baseURI = baseURI;
        log.info("Base URI set to: " + baseURI);
    }

    // Load properties from config file
    private void loadConfig() {
        prop = new Properties();
        try (FileInputStream ip = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/config/config.properties")) {
            prop.load(ip);
            baseURI = prop.getProperty("baseURI");
            username = prop.getProperty("username");
            password = prop.getProperty("password");
            log.info("Config loaded successfully");
        } catch (IOException e) {
            log.error("Failed to load config.properties", e);
        }
    }

    // Get authentication token
    public static String getAuthToken() {
        if (token == null) {
            log.info("Getting new auth token for user: " + username);
            token = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .body(String.format("{\"email\":\"%s\",\"password\":\"%s\"}", username, password))
                    .post("/login")
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("access_token");
            log.info("Token generated successfully");
        }
        return token;
    }

    // Create request with Bearer token
    public static RequestSpecification getRequestWithToken() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + getAuthToken());
    }
}
