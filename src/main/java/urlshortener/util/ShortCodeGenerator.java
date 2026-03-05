package urlshortener.util;

import java.security.SecureRandom;

public final class ShortCodeGenerator {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final SecureRandom RANDOM = new SecureRandom();

    private ShortCodeGenerator() {
    }


    public static String generateCode(int length){

        StringBuilder code = new StringBuilder();

        for(int i = 0; i < length; i++){
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }
}
