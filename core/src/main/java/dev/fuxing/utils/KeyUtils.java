package dev.fuxing.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This utility it created to generate key id that are URL Safe.
 * <p>
 * Supported keys are:
 * <pre>
 * {@code
 * // UUID v4
 * KeyUtils.randomUUID();
 * // UUID v4 as Base 64 URL Safe
 * KeyUtils.randomUUIDBase64();
 *
 * // SHA 256 HEX
 * KeyUtils.sha256("Some Text");
 * // SHA 256 Protocol Trimmed HEX
 * KeyUtils.sha256Url("https://fuxing.dev/blog/help-me");
 * // SHA 256 Base 64 URL Safe
 * KeyUtils.sha256Base64("Some Text");
 * // SHA 256 Protocol Trimmed Base 64 URL Safe
 * KeyUtils.sha256UrlBase64("https://fuxing.dev/blog/i-am-trapped");
 *
 * // Universally unique Lexicographically sortable IDentifiers
 * // URL Safe, Sortable, Base 32
 * KeyUtils.nextULID();
 * }
 * </pre>
 * Created by: Fuxing
 * Date: 3/5/18
 * Time: 6:23 PM
 */
@SuppressWarnings("SpellCheckingInspection")
public final class KeyUtils {
    private static final Pattern HTTP_PATTERN = Pattern.compile("^https?://", Pattern.CASE_INSENSITIVE);
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static final ULID ULID_INSTANCE = new ULID();

    public static final String BASE64_URL_SAFE_REGEX = "^[a-zA-Z0-9_-]{43}$";

    /**
     * UUID Value Regex for @Pattern validator
     */
    public static final String UUID_REGEX = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    /**
     * ULID Value Regex for @Pattern validator
     */
    public static final String ULID_REGEX = "^[0123456789abcdefghjkmnpqrstvwxyz]{26}$";

    /**
     * UUID Max Value to greater than and lesser than sorting
     */
    public static final String UUID_MAX = "ffffffff-ffff-ffff-ffff-ffffffffffff";

    /**
     * UUID Min Value to greater than and lesser than sorting
     */
    public static final String UUID_MIN = "00000000-0000-0000-0000-000000000000";


    /**
     * ULID Max Value to greater than and lesser than sorting
     */
    public static final String ULID_MAX = "7ZZZZZZZZZZZZZZZZZZZZZZZZZ";

    /**
     * ULID Min Value to greater than and lesser than sorting
     */
    public static final String ULID_MIN = "00000000000000000000000000";

    private KeyUtils() {/**/}

    /**
     * ULID is: Universally unique Lexicographically sortable IDentifiers
     * <br>
     * - 128-bit compatibility with UUID
     * <br>
     * - 1.21e+24 unique ULIDs per millisecond
     * <br>
     * - Lexicographically sortable
     * <br>
     * - 26 character string
     * <br>
     * - base32
     * <br>
     * - Case insensitive, but lower-cased
     * <br>
     * - No special characters (URL safe)
     * <p>
     * See spec: https://github.com/ulid/spec
     *
     * @return ULID in String, uppercase and sortable
     */
    public static String nextULID() {
        return ULID_INSTANCE.nextULID(System.currentTimeMillis());
    }

    /**
     * See also {@link #nextULID()}.
     *
     * @param millis current millis
     * @return ULID in String, uppercase and sortable
     */
    public static String nextULID(long millis) {
        return ULID_INSTANCE.nextULID(millis);
    }

    /**
     * @return random uuid4 in String
     */
    public static String randomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * @return MostSignificantBits = System.currentTimeMillis(), LeastSignificantBits = randomLong()
     */
    public static String randomMillisUUID() {
        return createUUID(System.currentTimeMillis(), RandomUtils.nextLong());
    }

    /**
     * 64bit Long x 2
     *
     * @param left  MostSignificantBits
     * @param right LeastSignificantBits
     * @return UUID
     */
    public static String createUUID(long left, long right) {
        return new UUID(left, right).toString();
    }

    /**
     * 32bit Integer x 4
     *
     * @param i1 integer 1
     * @param i2 integer 2
     * @param i3 integer 3
     * @param i4 integer 4
     * @return 128 bit UUID
     */
    public static String createUUID(int i1, int i2, int i3, int i4) {
        ByteBuffer buffer = ByteBuffer.allocate(16)
                .putInt(i1)
                .putInt(i2)
                .putInt(i3)
                .putInt(i4);

        buffer.position(0);

        long left = buffer.getLong();
        long right = buffer.getLong();
        return createUUID(left, right);
    }

    /**
     * @param bytes 16 length
     * @return 128 bit UUID
     */
    public static String createUUID(byte[] bytes) {
        if (bytes.length != 16) throw new IllegalArgumentException("bytes.length must be 16");

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.position(0);

        long left = buffer.getLong();
        long right = buffer.getLong();
        return createUUID(left, right);
    }

    /**
     * @return random uuid4 in String Base64 Url Safe
     */
    public static String randomUUIDBase64() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer uuidBytes = ByteBuffer.wrap(new byte[16]);
        uuidBytes.putLong(uuid.getMostSignificantBits());
        uuidBytes.putLong(uuid.getLeastSignificantBits());
        return ENCODER.encodeToString(uuidBytes.array());
    }

    /**
     * @param text to sha 256
     * @return sha 256 in hex
     */
    public static String sha256(String text) {
        return DigestUtils.sha256Hex(text);
    }

    /**
     * @param text to sha 256
     * @return sha 256 in base 64
     */
    public static String sha256Base64(String text) {
        return ENCODER.encodeToString(DigestUtils.sha256(text));
    }

    /**
     * @param url to sha 256, with protocol trimmed
     * @return sha 256 in hex
     */
    public static String sha256Url(String url) {
        String trimmed = HTTP_PATTERN.matcher(url).replaceFirst("");
        return DigestUtils.sha256Hex(trimmed);
    }

    /**
     * @param url to sha 256, with protocol trimmed
     * @return sha 256 in base 64
     */
    public static String sha256Base64Url(String url) {
        String trimmed = HTTP_PATTERN.matcher(url).replaceFirst("");
        return ENCODER.encodeToString(DigestUtils.sha256(trimmed));
    }

    /**
     * https://github.com/ulid/spec
     * <p>
     * Created by: Fuxing
     * Date: 2019-04-16
     * Time: 02:15
     */
    public static class ULID {
        private static final char[] ENCODING_CHARS = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',
                'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x',
                'y', 'z',
        };

        private static final byte[] DECODING_CHARS = new byte[]{
                // 0
                -1, -1, -1, -1, -1, -1, -1, -1,
                // 8
                -1, -1, -1, -1, -1, -1, -1, -1,
                // 16
                -1, -1, -1, -1, -1, -1, -1, -1,
                // 24
                -1, -1, -1, -1, -1, -1, -1, -1,
                // 32
                -1, -1, -1, -1, -1, -1, -1, -1,
                // 40
                -1, -1, -1, -1, -1, -1, -1, -1,
                // 48
                0, 1, 2, 3, 4, 5, 6, 7,
                // 56
                8, 9, -1, -1, -1, -1, -1, -1,
                // 64
                -1, 10, 11, 12, 13, 14, 15, 16,
                // 72
                17, 1, 18, 19, 1, 20, 21, 0,
                // 80
                22, 23, 24, 25, 26, -1, 27, 28,
                // 88
                29, 30, 31, -1, -1, -1, -1, -1,
                // 96
                -1, 10, 11, 12, 13, 14, 15, 16,
                // 104
                17, 1, 18, 19, 1, 20, 21, 0,
                // 112
                22, 23, 24, 25, 26, -1, 27, 28,
                // 120
                29, 30, 31,
        };

        private static final int MASK = 0x1F;
        private static final int MASK_BITS = 5;
        private static final long TIMESTAMP_OVERFLOW_MASK = 0xFFFF_0000_0000_0000L;
        private static final long TIMESTAMP_MSB_MASK = 0xFFFF_FFFF_FFFF_0000L;
        private static final long RANDOM_MSB_MASK = 0xFFFFL;

        private final Random random;

        public ULID() {
            this(new SecureRandom());
        }

        public ULID(Random random) {
            Objects.requireNonNull(random, "random must not be null!");
            this.random = random;
        }

        public void appendULID(StringBuilder stringBuilder) {
            Objects.requireNonNull(stringBuilder, "stringBuilder must not be null!");
            internalAppendULID(stringBuilder, System.currentTimeMillis(), random);
        }

        public String nextULID() {
            return nextULID(System.currentTimeMillis());
        }

        public String nextULID(long timestamp) {
            return internalUIDString(timestamp, random);
        }

        public Value nextValue() {
            return nextValue(System.currentTimeMillis());
        }

        public Value nextValue(long timestamp) {
            return internalNextValue(timestamp, random);
        }

        /**
         * Returns the next monotonic value. If an overflow happened while incrementing
         * the random part of the given previous ULID value then the returned value will
         * have a zero random part.
         *
         * @param previousUlid the previous ULID value.
         * @return the next monotonic value.
         */
        public Value nextMonotonicValue(Value previousUlid) {
            return nextMonotonicValue(previousUlid, System.currentTimeMillis());
        }

        /**
         * Returns the next monotonic value. If an overflow happened while incrementing
         * the random part of the given previous ULID value then the returned value will
         * have a zero random part.
         *
         * @param previousUlid the previous ULID value.
         * @param timestamp    the timestamp of the next ULID value.
         * @return the next monotonic value.
         */
        public Value nextMonotonicValue(Value previousUlid, long timestamp) {
            Objects.requireNonNull(previousUlid, "previousUlid must not be null!");
            if (previousUlid.timestamp() == timestamp) {
                return previousUlid.increment();
            }
            return nextValue(timestamp);
        }

        /**
         * Returns the next monotonic value or empty if an overflow happened while incrementing
         * the random part of the given previous ULID value.
         *
         * @param previousUlid the previous ULID value.
         * @return the next monotonic value or empty if an overflow happened.
         */
        public Optional<Value> nextStrictlyMonotonicValue(Value previousUlid) {
            return nextStrictlyMonotonicValue(previousUlid, System.currentTimeMillis());
        }

        /**
         * Returns the next monotonic value or empty if an overflow happened while incrementing
         * the random part of the given previous ULID value.
         *
         * @param previousUlid the previous ULID value.
         * @param timestamp    the timestamp of the next ULID value.
         * @return the next monotonic value or empty if an overflow happened.
         */
        public Optional<Value> nextStrictlyMonotonicValue(Value previousUlid, long timestamp) {
            Value result = nextMonotonicValue(previousUlid, timestamp);
            if (result.compareTo(previousUlid) < 1) {
                return Optional.empty();
            }
            return Optional.of(result);
        }

        public static Value parseULID(String ulidString) {
            Objects.requireNonNull(ulidString, "ulidString must not be null!");
            if (ulidString.length() != 26) {
                throw new IllegalArgumentException("ulidString must be exactly 26 chars long.");
            }

            String timeString = ulidString.substring(0, 10);
            long time = internalParseCrockford(timeString);
            if ((time & TIMESTAMP_OVERFLOW_MASK) != 0) {
                throw new IllegalArgumentException("ulidString must not exceed '7ZZZZZZZZZZZZZZZZZZZZZZZZZ'!");
            }
            String part1String = ulidString.substring(10, 18);
            String part2String = ulidString.substring(18);
            long part1 = internalParseCrockford(part1String);
            long part2 = internalParseCrockford(part2String);

            long most = (time << 16) | (part1 >>> 24);
            long least = part2 | (part1 << 40);
            return new Value(most, least);
        }

        public static Value fromBytes(byte[] data) {
            Objects.requireNonNull(data, "data must not be null!");
            if (data.length != 16) {
                throw new IllegalArgumentException("data must be 16 bytes in length!");
            }
            long mostSignificantBits = 0;
            long leastSignificantBits = 0;
            for (int i = 0; i < 8; i++) {
                mostSignificantBits = (mostSignificantBits << 8) | (data[i] & 0xff);
            }
            for (int i = 8; i < 16; i++) {
                leastSignificantBits = (leastSignificantBits << 8) | (data[i] & 0xff);
            }
            return new Value(mostSignificantBits, leastSignificantBits);
        }

        public static class Value
                implements Comparable<Value>, Serializable {
            private static final long serialVersionUID = -3563159514112487717L;

            /*
             * The most significant 64 bits of this ULID.
             */
            private final long mostSignificantBits;

            /*
             * The least significant 64 bits of this ULID.
             */
            private final long leastSignificantBits;

            public Value(long mostSignificantBits, long leastSignificantBits) {
                this.mostSignificantBits = mostSignificantBits;
                this.leastSignificantBits = leastSignificantBits;
            }

            /**
             * Returns the most significant 64 bits of this ULID's 128 bit value.
             *
             * @return The most significant 64 bits of this ULID's 128 bit value
             */
            public long getMostSignificantBits() {
                return mostSignificantBits;
            }

            /**
             * Returns the least significant 64 bits of this ULID's 128 bit value.
             *
             * @return The least significant 64 bits of this ULID's 128 bit value
             */
            public long getLeastSignificantBits() {
                return leastSignificantBits;
            }


            public long timestamp() {
                return mostSignificantBits >>> 16;
            }

            public byte[] toBytes() {
                byte[] result = new byte[16];
                for (int i = 0; i < 8; i++) {
                    result[i] = (byte) ((mostSignificantBits >> ((7 - i) * 8)) & 0xFF);
                }
                for (int i = 8; i < 16; i++) {
                    result[i] = (byte) ((leastSignificantBits >> ((15 - i) * 8)) & 0xFF);
                }

                return result;
            }

            public Value increment() {
                long lsb = leastSignificantBits;
                if (lsb != 0xFFFF_FFFF_FFFF_FFFFL) {
                    return new Value(mostSignificantBits, lsb + 1);
                }
                long msb = mostSignificantBits;
                if ((msb & RANDOM_MSB_MASK) != RANDOM_MSB_MASK) {
                    return new Value(msb + 1, 0);
                }
                return new Value(msb & TIMESTAMP_MSB_MASK, 0);
            }

            @Override
            public int hashCode() {
                long hilo = mostSignificantBits ^ leastSignificantBits;
                return ((int) (hilo >> 32)) ^ (int) hilo;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Value value = (Value) o;

                return mostSignificantBits == value.mostSignificantBits
                        && leastSignificantBits == value.leastSignificantBits;
            }

            @Override
            public int compareTo(Value val) {
                // The ordering is intentionally set up so that the ULIDs
                // can simply be numerically compared as two numbers
                return (this.mostSignificantBits < val.mostSignificantBits ? -1 :
                        (this.mostSignificantBits > val.mostSignificantBits ? 1 :
                                (this.leastSignificantBits < val.leastSignificantBits ? -1 :
                                        (this.leastSignificantBits > val.leastSignificantBits ? 1 :
                                                0))));
            }

            @Override
            public String toString() {
                char[] buffer = new char[26];

                internalWriteCrockford(buffer, timestamp(), 10, 0);
                long value = ((mostSignificantBits & 0xFFFFL) << 24);
                long interim = (leastSignificantBits >>> 40);
                value = value | interim;
                internalWriteCrockford(buffer, value, 8, 10);
                internalWriteCrockford(buffer, leastSignificantBits, 8, 18);

                return new String(buffer);
            }
        }

        /*
         * http://crockford.com/wrmg/base32.html
         */
        static void internalAppendCrockford(StringBuilder builder, long value, int count) {
            for (int i = count - 1; i >= 0; i--) {
                int index = (int) ((value >>> (i * MASK_BITS)) & MASK);
                builder.append(ENCODING_CHARS[index]);
            }
        }

        static long internalParseCrockford(String input) {
            Objects.requireNonNull(input, "input must not be null!");
            int length = input.length();
            if (length > 12) {
                throw new IllegalArgumentException("input length must not exceed 12 but was " + length + "!");
            }

            long result = 0;
            for (int i = 0; i < length; i++) {
                char current = input.charAt(i);
                byte value = -1;
                if (current < DECODING_CHARS.length) {
                    value = DECODING_CHARS[current];
                }
                if (value < 0) {
                    throw new IllegalArgumentException("Illegal character '" + current + "'!");
                }
                result |= ((long) value) << ((length - 1 - i) * MASK_BITS);
            }
            return result;
        }

        /*
         * http://crockford.com/wrmg/base32.html
         */
        static void internalWriteCrockford(char[] buffer, long value, int count, int offset) {
            for (int i = 0; i < count; i++) {
                int index = (int) ((value >>> ((count - i - 1) * MASK_BITS)) & MASK);
                buffer[offset + i] = ENCODING_CHARS[index];
            }
        }

        static String internalUIDString(long timestamp, Random random) {
            checkTimestamp(timestamp);

            char[] buffer = new char[26];

            internalWriteCrockford(buffer, timestamp, 10, 0);
            // could use nextBytes(byte[] bytes) instead
            internalWriteCrockford(buffer, random.nextLong(), 8, 10);
            internalWriteCrockford(buffer, random.nextLong(), 8, 18);

            return new String(buffer);
        }

        static void internalAppendULID(StringBuilder builder, long timestamp, Random random) {
            checkTimestamp(timestamp);

            internalAppendCrockford(builder, timestamp, 10);
            // could use nextBytes(byte[] bytes) instead
            internalAppendCrockford(builder, random.nextLong(), 8);
            internalAppendCrockford(builder, random.nextLong(), 8);
        }

        static Value internalNextValue(long timestamp, Random random) {
            checkTimestamp(timestamp);
            // could use nextBytes(byte[] bytes) instead
            long mostSignificantBits = random.nextLong();
            long leastSignificantBits = random.nextLong();
            mostSignificantBits &= 0xFFFF;
            mostSignificantBits |= (timestamp << 16);
            return new Value(mostSignificantBits, leastSignificantBits);
        }

        private static void checkTimestamp(long timestamp) {
            if ((timestamp & TIMESTAMP_OVERFLOW_MASK) != 0) {
                throw new IllegalArgumentException("ULID does not support timestamps after +10889-08-02T05:31:50.655Z!");
            }
        }
    }
}
