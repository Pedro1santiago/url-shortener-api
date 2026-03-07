package urlshortener.util;

public class Base62 {

    private static final String CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String encode(long value) {

        StringBuilder result = new StringBuilder();

        while (value > 0) {
            int remainder = (int) (value % 62);
            result.append(CHARACTERS.charAt(remainder));
            value = value / 62;
        }

        return result.reverse().toString();
    }
}