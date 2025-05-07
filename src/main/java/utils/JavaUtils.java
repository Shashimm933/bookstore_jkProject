package utils;

import java.util.Random;

public class JavaUtils {

    private static final Random random = new Random();

    public static int generateRandomId() {
        return 1000 + random.nextInt(9000);  // Generates ID between 1000 and 9999
    }

    public static String generateRandomString(String prefix) {
        return prefix + "_" + random.nextInt(9999);
    }

    public static String generateRandomEmail() {
        return "user" + System.currentTimeMillis() + "@example.com";
    }
}
