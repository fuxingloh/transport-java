package dev.fuxing.transport;

import dev.fuxing.utils.KeyUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by: Fuxing
 * Date: 2019-04-16
 * Time: 13:44
 */
class TransportCursorTest {

    @Test
    void sortIndexParameter() {
        String providerId = KeyUtils.randomMillisUUID();
        TransportCursor.Builder builder = new TransportCursor.Builder();
        builder.put("providerId", providerId);
        TransportCursor cursor = builder.build();

        String base64 = cursor.toBase64();
        System.out.println(base64);
        TransportCursor fromBase64 = TransportCursor.fromBase64(base64);

        assertNotNull(fromBase64);
        assertEquals(providerId, cursor.get("providerId"));
    }

    @Test
    void parameter() {
        TransportCursor.Builder builder = new TransportCursor.Builder();
        builder.put("providerId", KeyUtils.randomMillisUUID());
        TransportCursor cursor = builder.build();

        System.out.println(cursor.toBase64());
    }

    @Test
    void multipleParameter() {
        TransportCursor.Builder builder = new TransportCursor.Builder();
        builder.put("providerId", KeyUtils.randomMillisUUID());
        builder.put("createdMillis", RandomUtils.nextLong());
        TransportCursor cursor = builder.build();

        System.out.println(cursor.toBase64());
    }
}