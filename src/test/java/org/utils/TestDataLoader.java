
package org.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.DataProvider;

import java.io.FileReader;

public class TestDataLoader {
    static String expiredToken="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzaGFzaGlAZ21haWwuY29tIiwiZXhwIjoxNzQ2NTk4NjY0fQ.MS4Dod3S4G0VnxzxSulcYUkpGg-cdDuEb_FQ0TEGCio";

    @DataProvider(name = "tokenScenarios")
    public static Object[][] tokenScenarios() {
        return new Object[][]{
                {"", 403, "Not authenticated"}, // No token
                {"Bearer invalid_token", 403, "Invalid token or expired token"}, // Invalid token
                {"Bearer malformed.token.structure", 403, "Invalid token or expired token"}, // Malformed token
                {"Bearer "+expiredToken+" ", 403, "Invalid token or expired token"} // Simulated expired token
        };
    }
}
