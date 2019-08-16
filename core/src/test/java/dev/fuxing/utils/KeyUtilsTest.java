package dev.fuxing.utils;

import org.apache.commons.codec.EncoderException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Created by: Fuxing
 * Date: 27/7/18
 * Time: 11:34 AM
 */
class KeyUtilsTest {

    @Test
    void randomMillisUUID() {
        System.out.println(KeyUtils.randomMillisUUID());
        System.out.println(KeyUtils.randomMillisUUID());
        System.out.println(KeyUtils.randomMillisUUID());
    }

    @Test
    void createUUID() {
        System.out.println(KeyUtils.createUUID(1000, 0));
        System.out.println(KeyUtils.createUUID(1000, Long.MIN_VALUE));
        System.out.println(KeyUtils.createUUID(1000, Long.MAX_VALUE));
    }

    @Test
    void createI4() {
        System.out.println(KeyUtils.createUUID(0, -100, 0, Integer.MIN_VALUE));
        System.out.println(KeyUtils.createUUID(0, -100, 0, Integer.MAX_VALUE));
        System.out.println(KeyUtils.createUUID(1000, 0, 2043, 0));

        System.out.println(KeyUtils.createUUID(0, 0, 0, Integer.MAX_VALUE));
        System.out.println(KeyUtils.createUUID(0, 0, 0, Integer.MIN_VALUE));
        System.out.println(Integer.MIN_VALUE + 2);
        System.out.println(KeyUtils.createUUID(0, 0, 0, Integer.MIN_VALUE + 2));
    }

    @Test
    void createBase32() throws IOException, EncoderException {
        System.out.println(KeyUtils.nextL12());
        System.out.println(KeyUtils.nextL12());
        System.out.println(KeyUtils.nextL12().length());
    }
}
