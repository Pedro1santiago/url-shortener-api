package urlshortener.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QrCodeServiceTest {

    private final QrCodeService qrCodeService = new QrCodeService();

    @Test
    void generateShouldReturnPngBytes() throws Exception {

        byte[] bytes = qrCodeService.generate("https://example.com");

        assertNotNull(bytes);
        assertTrue(bytes.length > 8);
        assertArrayEquals(pngSignature(), firstBytes(bytes, 8));
    }

    @Test
    void generateShouldThrowWhenUrlIsNull() {
        assertThrows(Exception.class, () -> qrCodeService.generate(null));
    }

    private static byte[] pngSignature() {
        return new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    }

    private static byte[] firstBytes(byte[] bytes, int length) {
        byte[] first = new byte[length];
        System.arraycopy(bytes, 0, first, 0, length);
        return first;
    }
}
