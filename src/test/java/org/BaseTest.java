package org;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public static ExtentReports extent;
    private static String reportPath;
    ExtentTest test;

    @BeforeSuite
    public void setUpReportAndConfig() {
        // Log4j setup
        log = Logger.getLogger("API_Logger");
        PropertyConfigurator.configure(System.getProperty("user.dir") + "/src/main/resources/config/log4j.properties");

        // Load config properties
        loadConfig();

        // Set RestAssured base URI
        RestAssured.baseURI = baseURI;

        // Setup Extent Report in target/extent-reports/<timestamp>/
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        reportPath = System.getProperty("user.dir") + "/target/extent-reports/" + timestamp + "/ExtentReport.html";
        new File(reportPath).getParentFile().mkdirs();

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        extent = new ExtentReports();
        extent.attachReporter(spark);

        extent.setSystemInfo("Tester", "Shashi Kumar");
        log.info("Extent report configured at: " + reportPath);
    }

    @BeforeMethod
    public void setUpTest() {
        // Initialize ExtentTest for each test
        // Creates a new test instance for each method
        test = extent.createTest(this.getClass().getName());
    }

    @AfterMethod
    public void getResult(ITestResult result) {
        // Update the ExtentTest result
        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail(result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test Passed");
        } else {
            test.skip("Test Skipped");
        }
    }

    @AfterSuite
    public void tearDownReport() {
        extent.flush();
        log.info("Extent report flushed and saved to: " + reportPath);
    }

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

    public static RequestSpecification getRequestWithToken() {
        return RestAssured.given()
                .header("Authorization", "Bearer " + getAuthToken());
    }
}
