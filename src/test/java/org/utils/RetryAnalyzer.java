package org.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempt = 1;
    private final int maxRetry = 3;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < maxRetry) {
            System.out.println("Retrying test: " + result.getName() + " | Attempt: " + attempt);
            attempt++;
            return true;
        }
        return false;
    }
}
