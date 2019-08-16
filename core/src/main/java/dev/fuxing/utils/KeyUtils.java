package dev.fuxing.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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

    private static final KeyUtils.CrockfordBase32 CROCKFORD_BASE_32 = new KeyUtils.CrockfordBase32();


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
     * L12 Value Regex for @Pattern validator
     */
    public static final String L12_REGEX = "^[0123456789abcdefghjkmnpqrstvwxyz]{12}$";

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
     * @return length 12 base 32 crockford
     */
    public static String nextL12() {
        byte[] bytes = RandomUtils.nextBytes(7);
        return CROCKFORD_BASE_32.encodeToString(bytes).toLowerCase();
    }

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
     * @param text to generate
     * @return url safe slug
     */
    public static String generateSlug(String text) {
        if (StringUtils.isBlank(text)) return null;

        text = text.toLowerCase();
        text = text.replace(" ", "-");
        text = text.replaceAll("[^a-z0-9-]", "");
        return StringUtils.substring(text, 0, 255);
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

    /**
     * <p>Provides Base32 encoding and decoding as defined by <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a>.
     * However it uses a custom alphabet first coined by Douglas Crockford. Only addition to the alphabet is that 'u' and
     * 'U' characters decode as if they were 'V' to improve mistakes by human input.</p>
     * <p>
     * This class operates directly on byte streams, and not character streams.
     * </p>
     *
     * @version $Id: Base32.java 1382498 2012-09-09 13:41:55Z sebb $
     * @see <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a>
     * @see <a href="http://www.crockford.com/wrmg/base32.html">Douglas Crockford's Base32 Encoding</a>
     * @since 1.5
     */
    public static class CrockfordBase32 {

        /**
         * Mask used to extract 8 bits, used in decoding bytes
         */
        protected static final int MASK_8BITS = 0xff;
        private static final Charset UTF8 = Charset.forName("UTF-8");
        private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
        /**
         * Defines the default buffer size - currently {@value}
         * - must be large enough for at least one encoded block+separator
         */
        private static final int DEFAULT_BUFFER_SIZE = 8192;
        /**
         * Mask used to extract 5 bits, used when encoding Base32 bytes
         */
        private static final int MASK_5BITS = 0x1f;
        /**
         * BASE32 characters are 5 bits in length.
         * They are formed by taking a block of five octets to form a 40-bit string,
         * which is converted into eight BASE32 characters.
         */
        private static final int BITS_PER_ENCODED_BYTE = 5;
        private static final int BYTES_PER_ENCODED_BLOCK = 8;
        private static final int BYTES_PER_UNENCODED_BLOCK = 5;
        private static final byte PAD = '=';
        /**
         * This array is a lookup table that translates 5-bit positive integer index values into their "Base32 Alphabet"
         * equivalents as specified in Table 3 of RFC 2045.
         */
        private static final byte[] ENCODE_TABLE = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M',
                'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'
        };
        /**
         * Convenience variable to help us determine when our buffer is going to run out of room and needs resizing.
         * <code>decodeSize = {@link #BYTES_PER_ENCODED_BLOCK} - 1 + lineSeparator.length;</code>
         */
        private final int decodeSize;
        /**
         * Convenience variable to help us determine when our buffer is going to run out of room and needs resizing.
         * <code>encodeSize = {@link #BYTES_PER_ENCODED_BLOCK} + lineSeparator.length;</code>
         */
        private final int encodeSize;
        /**
         * Wheather this encoder should use a padding character at the end of encoded Strings.
         */
        private final boolean usePaddingCharacter;
        /**
         * Buffer for streaming.
         */
        protected byte[] buffer;
        /**
         * Position where next character should be written in the buffer.
         */
        protected int pos;
        /**
         * Boolean flag to indicate the EOF has been reached. Once EOF has been reached, this object becomes useless,
         * and must be thrown away.
         */
        protected boolean eof;
        /**
         * Writes to the buffer only occur after every 3/5 reads when encoding, and every 4/8 reads when decoding.
         * This variable helps track that.
         */
        protected int modulus;
        /**
         * Place holder for the bytes we're dealing with for our based logic.
         * Bitwise operations store and extract the encoding or decoding from this variable.
         */
        private long bitWorkArea;

        public CrockfordBase32() {
            this(false);
        }

        /**
         * Creates a Base32 codec used for decoding and encoding.
         * <p>
         * When encoding the line length is 0 (no chunking).
         * </p>
         *
         * @param usePaddingCharacter whether to use padding character =
         */
        public CrockfordBase32(boolean usePaddingCharacter) {
            this.usePaddingCharacter = usePaddingCharacter;
            this.encodeSize = BYTES_PER_ENCODED_BLOCK;
            this.decodeSize = this.encodeSize - 1;
        }

        private static byte decode(byte octet) {
            switch (octet) {
                case '0':
                case 'O':
                case 'o':
                    return 0;

                case '1':
                case 'I':
                case 'i':
                case 'L':
                case 'l':
                    return 1;

                case '2':
                    return 2;
                case '3':
                    return 3;
                case '4':
                    return 4;
                case '5':
                    return 5;
                case '6':
                    return 6;
                case '7':
                    return 7;
                case '8':
                    return 8;
                case '9':
                    return 9;

                case 'A':
                case 'a':
                    return 10;

                case 'B':
                case 'b':
                    return 11;

                case 'C':
                case 'c':
                    return 12;

                case 'D':
                case 'd':
                    return 13;

                case 'E':
                case 'e':
                    return 14;

                case 'F':
                case 'f':
                    return 15;

                case 'G':
                case 'g':
                    return 16;

                case 'H':
                case 'h':
                    return 17;

                case 'J':
                case 'j':
                    return 18;

                case 'K':
                case 'k':
                    return 19;

                case 'M':
                case 'm':
                    return 20;

                case 'N':
                case 'n':
                    return 21;

                case 'P':
                case 'p':
                    return 22;

                case 'Q':
                case 'q':
                    return 23;

                case 'R':
                case 'r':
                    return 24;

                case 'S':
                case 's':
                    return 25;

                case 'T':
                case 't':
                    return 26;

                case 'U':
                case 'u':
                case 'V':
                case 'v':
                    return 27;

                case 'W':
                case 'w':
                    return 28;

                case 'X':
                case 'x':
                    return 29;

                case 'Y':
                case 'y':
                    return 30;

                case 'Z':
                case 'z':
                    return 31;

                default:
                    return -1;
            }
        }

        /**
         * Checks if a byte value is whitespace or not.
         * Whitespace is taken to mean: space, tab, CR, LF
         *
         * @param byteToCheck the byte to check
         * @return true if byte is whitespace, false otherwise
         */
        protected static boolean isWhiteSpace(byte byteToCheck) {
            switch (byteToCheck) {
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Tests a given String to see if it contains only valid characters within the alphabet.
         * The method treats whitespace and PAD as valid.
         *
         * @param base32 String to test
         * @return <code>true</code> if all characters in the String are valid characters in the alphabet or if
         * the String is empty; <code>false</code>, otherwise
         * @see #isInAlphabet(byte[], boolean)
         */
        public static boolean isInAlphabet(String base32) {
            return isInAlphabet(base32.getBytes(UTF8), true);
        }

        /**
         * Tests a given byte array to see if it contains only valid characters within the alphabet.
         * The method optionally treats whitespace and pad as valid.
         *
         * @param arrayOctet byte array to test
         * @param allowWSPad if <code>true</code>, then whitespace and PAD are also allowed
         * @return <code>true</code> if all bytes are valid characters in the alphabet or if the byte array is empty;
         * <code>false</code>, otherwise
         */
        public static boolean isInAlphabet(byte[] arrayOctet, boolean allowWSPad) {
            for (int i = 0; i < arrayOctet.length; i++) {
                if (!isInAlphabet(arrayOctet[i]) &&
                        (!allowWSPad || (arrayOctet[i] != PAD) && !isWhiteSpace(arrayOctet[i]))) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns whether or not the <code>octet</code> is in the Base32 alphabet.
         *
         * @param octet The value to test
         * @return <code>true</code> if the value is defined in the the Base32 alphabet <code>false</code> otherwise.
         */
        public static boolean isInAlphabet(byte octet) {
            return decode(octet) != -1;
        }

        /**
         * Returns the amount of buffered data available for reading.
         *
         * @return The amount of buffered data available for reading.
         */
        int available() {  // package protected for access from I/O streams
            return buffer != null ? pos : 0;
        }

        /**
         * Increases our buffer by the {@link #DEFAULT_BUFFER_RESIZE_FACTOR}.
         */
        private void resizeBuffer() {
            if (buffer == null) {
                buffer = new byte[DEFAULT_BUFFER_SIZE];
                pos = 0;
            } else {
                byte[] b = new byte[buffer.length * DEFAULT_BUFFER_RESIZE_FACTOR];
                System.arraycopy(buffer, 0, b, 0, buffer.length);
                buffer = b;
            }
        }

        /**
         * Ensure that the buffer has room for <code>size</code> bytes
         *
         * @param size minimum spare space required
         */
        protected void ensureBufferSize(int size) {
            if ((buffer == null) || (buffer.length < pos + size)) {
                resizeBuffer();
            }
        }

        /**
         * Extracts buffered data into the provided byte[] array, starting at position bPos,
         * up to a maximum of bAvail bytes. Returns how many bytes were actually extracted.
         *
         * @param b byte[] array to extract the buffered data into.
         * @return The number of bytes successfully extracted into the provided byte[] array.
         */
        int readResults(byte[] b) {  // package protected for access from I/O streams
            if (buffer != null) {
                int len = available();
                System.arraycopy(buffer, 0, b, 0, len);
                buffer = null; // so hasData() will return false, and this method can return -1
                return len;
            }
            return eof ? -1 : 0;
        }

        /**
         * Resets this object to its initial newly constructed state.
         */
        private void reset() {
            buffer = null;
            pos = 0;
            modulus = 0;
            eof = false;
        }

        /**
         * Encodes a String containing characters in the Base32 alphabet.
         *
         * @param pArray A String containing Base32 character data
         * @return A String containing only Base32 character data
         */
        public String encodeToString(String pArray) {
            return encodeToString(pArray.getBytes(UTF8));
        }

        /**
         * Encodes a byte[] containing binary data, into a String containing characters in the Base-N alphabet.
         *
         * @param pArray a byte array containing binary data
         * @return A String containing only Base32 character data
         */
        public String encodeToString(byte[] pArray) {
            return new String(encode(pArray), UTF8);
        }

        /**
         * Encodes a String containing characters in the Base32 alphabet.
         *
         * @param pArray A String containing Base32 character data
         * @return A UTF-8 decoded String
         */
        public String decodeToString(String pArray) {
            return decodeToString(pArray.getBytes(UTF8));
        }

        /**
         * Decodes a byte[] containing binary data, into a String containing UTF-8 decoded String.
         *
         * @param pArray a byte array containing binary data
         * @return A UTF-8 decoded String
         */
        public String decodeToString(byte[] pArray) {
            return new String(decode(pArray), UTF8);
        }

        /**
         * Decodes a String containing characters in the Base-N alphabet.
         *
         * @param pArray A String containing Base-N character data
         * @return a byte array containing binary data
         */
        public byte[] decode(String pArray) {
            return decode(pArray.getBytes(UTF8));
        }

        /**
         * Encodes a String containing characters in the Base32 alphabet.
         *
         * @param pArray A String containing Base-N character data
         * @return a byte array containing binary data
         */
        public byte[] encode(String pArray) {
            return encode(pArray.getBytes(UTF8));
        }

        /**
         * Decodes a byte[] containing characters in the Base-N alphabet.
         *
         * @param pArray A byte array containing Base-N character data
         * @return a byte array containing binary data
         */
        public byte[] decode(byte[] pArray) {
            reset();
            if (pArray == null || pArray.length == 0) {
                return pArray;
            }
            decode(pArray, 0, pArray.length);
            decode(pArray, 0, -1); // Notify decoder of EOF.
            byte[] result = new byte[pos];
            readResults(result);
            return result;
        }

        // The static final fields above are used for the original static byte[] methods on Base32.
        // The private member fields below are used with the new streaming approach, which requires
        // some state be preserved between calls of encode() and decode().

        /**
         * Encodes a byte[] containing binary data, into a byte[] containing characters in the alphabet.
         *
         * @param pArray a byte array containing binary data
         * @return A byte array containing only the basen alphabetic character data
         */
        public byte[] encode(byte[] pArray) {
            reset();
            if (pArray == null || pArray.length == 0) {
                return pArray;
            }
            encode(pArray, 0, pArray.length);
            encode(pArray, 0, -1); // Notify encoder of EOF.
            byte[] buf = new byte[pos];
            readResults(buf);
            return buf;
        }

        /**
         * <p>
         * Decodes all of the provided data, starting at inPos, for inAvail bytes. Should be called at least twice: once
         * with the data to decode, and once with inAvail set to "-1" to alert decoder that EOF has been reached. The "-1"
         * call is not necessary when decoding, but it doesn't hurt, either.
         * </p>
         * <p>
         * Ignores all non-Base32 characters. This is how chunked (e.g. 76 character) data is handled, since CR and LF are
         * silently ignored, but has implications for other bytes, too. This method subscribes to the garbage-in,
         * garbage-out philosophy: it will not check the provided data for validity.
         * </p>
         *
         * @param in      byte[] array of ascii data to Base32 decode.
         * @param inPos   Position to start reading data from.
         * @param inAvail Amount of bytes available from input for encoding.
         *                <p/>
         *                Output is written to {@link #buffer} as 8-bit octets, using {@link #pos} as the buffer position
         */
        void decode(byte[] in, int inPos, int inAvail) { // package protected for access from I/O streams
            if (eof) {
                return;
            }
            if (inAvail < 0) {
                eof = true;
            }
            for (int i = 0; i < inAvail; i++) {
                byte b = in[inPos++];
                if (b == PAD) {
                    // We're done.
                    eof = true;
                    break;
                } else {
                    ensureBufferSize(decodeSize);
                    if (isInAlphabet(b)) {
                        int result = decode(b);
                        modulus = (modulus + 1) % BYTES_PER_ENCODED_BLOCK;
                        bitWorkArea = (bitWorkArea << BITS_PER_ENCODED_BYTE) + result; // collect decoded bytes
                        if (modulus == 0) { // we can output the 5 bytes
                            buffer[pos++] = (byte) ((bitWorkArea >> 32) & MASK_8BITS);
                            buffer[pos++] = (byte) ((bitWorkArea >> 24) & MASK_8BITS);
                            buffer[pos++] = (byte) ((bitWorkArea >> 16) & MASK_8BITS);
                            buffer[pos++] = (byte) ((bitWorkArea >> 8) & MASK_8BITS);
                            buffer[pos++] = (byte) (bitWorkArea & MASK_8BITS);
                        }
                    }
                }
            }

            // Two forms of EOF as far as Base32 decoder is concerned: actual
            // EOF (-1) and first time '=' character is encountered in stream.
            // This approach makes the '=' padding characters completely optional.
            if (eof && modulus >= 2) { // if modulus < 2, nothing to do
                ensureBufferSize(decodeSize);

                //  we ignore partial bytes, i.e. only multiples of 8 count
                switch (modulus) {
                    case 2: // 10 bits, drop 2 and output one byte
                        buffer[pos++] = (byte) ((bitWorkArea >> 2) & MASK_8BITS);
                        break;
                    case 3: // 15 bits, drop 7 and output 1 byte
                        buffer[pos++] = (byte) ((bitWorkArea >> 7) & MASK_8BITS);
                        break;
                    case 4: // 20 bits = 2*8 + 4
                        bitWorkArea = bitWorkArea >> 4; // drop 4 bits
                        buffer[pos++] = (byte) ((bitWorkArea >> 8) & MASK_8BITS);
                        buffer[pos++] = (byte) ((bitWorkArea) & MASK_8BITS);
                        break;
                    case 5: // 25bits = 3*8 + 1
                        bitWorkArea = bitWorkArea >> 1;
                        buffer[pos++] = (byte) ((bitWorkArea >> 16) & MASK_8BITS);
                        buffer[pos++] = (byte) ((bitWorkArea >> 8) & MASK_8BITS);
                        buffer[pos++] = (byte) ((bitWorkArea) & MASK_8BITS);
                        break;
                    case 6: // 30bits = 3*8 + 6
                        bitWorkArea = bitWorkArea >> 6;
                        buffer[pos++] = (byte) ((bitWorkArea >> 16) & MASK_8BITS);
                        buffer[pos++] = (byte) ((bitWorkArea >> 8) & MASK_8BITS);
                        buffer[pos++] = (byte) ((bitWorkArea) & MASK_8BITS);
                        break;
                    case 7: // 35 = 4*8 +3
                        bitWorkArea = bitWorkArea >> 3;
                        buffer[pos++] = (byte) ((bitWorkArea >> 24) & MASK_8BITS);
                        buffer[pos++] = (byte) ((bitWorkArea >> 16) & MASK_8BITS);
                        buffer[pos++] = (byte) ((bitWorkArea >> 8) & MASK_8BITS);
                        buffer[pos++] = (byte) ((bitWorkArea) & MASK_8BITS);
                        break;
                }
            }
        }

        /**
         * <p>
         * Encodes all of the provided data, starting at inPos, for inAvail bytes. Must be called at least twice: once with
         * the data to encode, and once with inAvail set to "-1" to alert encoder that EOF has been reached, so flush last
         * remaining bytes (if not multiple of 5).
         * </p>
         *
         * @param in      byte[] array of binary data to Base32 encode.
         * @param inPos   Position to start reading data from.
         * @param inAvail Amount of bytes available from input for encoding.
         */
        void encode(byte[] in, int inPos, int inAvail) { // package protected for access from I/O streams
            if (eof) {
                return;
            }
            // inAvail < 0 is how we're informed of EOF in the underlying data we're
            // encoding.
            if (inAvail < 0) {
                eof = true;
                if (0 == modulus) {
                    return; // no leftovers to process
                }
                ensureBufferSize(encodeSize);
                int savedPos = pos;
                switch (modulus) { // % 5
                    case 1: // Only 1 octet; take top 5 bits then remainder
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 3) & MASK_5BITS]; // 8-1*5 = 3
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea << 2) & MASK_5BITS]; // 5-3=2
                        if (usePaddingCharacter) {
                            buffer[pos++] = PAD;
                            buffer[pos++] = PAD;
                            buffer[pos++] = PAD;
                            buffer[pos++] = PAD;
                            buffer[pos++] = PAD;
                            buffer[pos++] = PAD;
                        }
                        break;

                    case 2: // 2 octets = 16 bits to use
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 11) & MASK_5BITS]; // 16-1*5 = 11
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 6) & MASK_5BITS]; // 16-2*5 = 6
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 1) & MASK_5BITS]; // 16-3*5 = 1
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea << 4) & MASK_5BITS]; // 5-1 = 4
                        if (usePaddingCharacter) {
                            buffer[pos++] = PAD;
                            buffer[pos++] = PAD;
                            buffer[pos++] = PAD;
                            buffer[pos++] = PAD;
                        }
                        break;
                    case 3: // 3 octets = 24 bits to use
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 19) & MASK_5BITS]; // 24-1*5 = 19
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 14) & MASK_5BITS]; // 24-2*5 = 14
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 9) & MASK_5BITS]; // 24-3*5 = 9
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 4) & MASK_5BITS]; // 24-4*5 = 4
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea << 1) & MASK_5BITS]; // 5-4 = 1
                        if (usePaddingCharacter) {
                            buffer[pos++] = PAD;
                            buffer[pos++] = PAD;
                            buffer[pos++] = PAD;
                        }
                        break;
                    case 4: // 4 octets = 32 bits to use
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 27) & MASK_5BITS]; // 32-1*5 = 27
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 22) & MASK_5BITS]; // 32-2*5 = 22
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 17) & MASK_5BITS]; // 32-3*5 = 17
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 12) & MASK_5BITS]; // 32-4*5 = 12
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 7) & MASK_5BITS]; // 32-5*5 =  7
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 2) & MASK_5BITS]; // 32-6*5 =  2
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea << 3) & MASK_5BITS]; // 5-2 = 3
                        if (usePaddingCharacter) {
                            buffer[pos++] = PAD;
                        }
                        break;
                }
            } else {
                for (int i = 0; i < inAvail; i++) {
                    ensureBufferSize(encodeSize);
                    modulus = (modulus + 1) % BYTES_PER_UNENCODED_BLOCK;
                    int b = in[inPos++];
                    if (b < 0) {
                        b += 256;
                    }
                    bitWorkArea = (bitWorkArea << 8) + b; // BITS_PER_BYTE
                    if (0 == modulus) { // we have enough bytes to create our output
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 35) & MASK_5BITS];
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 30) & MASK_5BITS];
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 25) & MASK_5BITS];
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 20) & MASK_5BITS];
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 15) & MASK_5BITS];
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 10) & MASK_5BITS];
                        buffer[pos++] = ENCODE_TABLE[(int) (bitWorkArea >> 5) & MASK_5BITS];
                        buffer[pos++] = ENCODE_TABLE[(int) bitWorkArea & MASK_5BITS];
                    }
                }
            }
        }

    }
}
