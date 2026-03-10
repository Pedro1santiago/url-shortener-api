package urlshortener.validation;

import urlshortener.exception.InvalidUrlException;

import java.net.URL;

public class Url {

    /** Validates that the URL is non-blank and uses http/https. */
    public static void validate(String url) {

        if (url == null || url.isBlank()) {
            throw new InvalidUrlException();
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")){
            throw new InvalidUrlException();
        }

        try {

            URL parsed = new URL(url);

            if (!parsed.getProtocol().equals("http") &&
                    !parsed.getProtocol().equals("https")) {

                throw new InvalidUrlException();
            }

        } catch (Exception e) {
            throw new InvalidUrlException();
        }
    }
}
