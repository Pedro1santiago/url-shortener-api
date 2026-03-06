package urlshortener.util;

import java.security.SecureRandom;

public final class ShortCodeGenerator {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final SecureRandom RANDOM = new SecureRandom();

    private ShortCodeGenerator() {}

    public static String generateCode(int length) {

        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(BASE62.length());
            code.append(BASE62.charAt(index));
        }

        return code.toString();
    }
}