package urlshortener.validation;

import urlshortener.exception.InvalidUrlException;

import java.net.URL;

public class Url {

    public static void validate(String url) {

        if (url == null || url.isBlank()) {
            throw new InvalidUrlException();
        }

        try {

            URL parsed = new URL(url);

            if (!parsed.getProtocol().equals("http://") &&
                    !parsed.getProtocol().equals("https://")) {

                throw new InvalidUrlException();
            }

        } catch (Exception e) {
            throw new InvalidUrlException();
        }
    }
}
