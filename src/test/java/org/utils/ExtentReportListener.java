package org.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.*;
import org.testng.xml.XmlSuite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class ExtentReportListener implements IReporter {

    private static final Logger log = LogManager.getLogger(ExtentReportListener.class);

    private ExtentReports extent;
    private ExtentTest test;

    public void initializeReporter() {
        // Initialize the ExtentReports instance
        String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport.html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        try {
            initializeReporter();
            for (ISuite suite : suites) {
                Map<String, ISuiteResult> results = suite.getResults();
                for (ISuiteResult result : results.values()) {
                    ITestContext context = result.getTestContext();

                    // Logging test pass/fail statuses
                    for (ITestResult passedTest : context.getPassedTests().getAllResults()) {
                        test = extent.createTest(passedTest.getMethod().getMethodName());
                        test.pass("Test passed");
                    }

                    for (ITestResult failedTest : context.getFailedTests().getAllResults()) {
                        test = extent.createTest(failedTest.getMethod().getMethodName());
                        test.fail(failedTest.getThrowable());
                    }

                    // Add other test statuses like skipped, etc.
                }
            }
            extent.flush();  // Save the report
        } catch (Exception e) {
            log.error("Error generating Extent Report", e);
        }
    }
}
