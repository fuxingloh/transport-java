package dev.fuxing.validator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.annotation.ElementType.*;

/**
 * Validate enum that are structured with nullable string.
 * Will fail = null, or "null"
 *
 * @see Example for example of how it works.
 * <p>
 * Created by: Fuxing
 * Date: 2019-04-16
 * Time: 01:29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {
    String message() default "enum not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

/**
 * This is an example of a enum that works with ValidEnum.
 */
enum Example {
    EXAMPLE("EXAMPLE"),

    // If any enum is this value, it will fail validation
    UNKNOWN_TO_SDK_VERSION(null);

    private final String value;

    Example(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Use this in place of valueOf to convert the raw string returned by the service into the enum value.
     *
     * @param value real value
     * @return Example corresponding to the value
     */
    @JsonCreator
    public static Example fromValue(String value) {
        if (value == null) {
            return null;
        }
        return Stream.of(Example.values()).filter(e -> e.toString().equals(value)).findFirst()
                .orElse(UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<Example> knownValues() {
        return Stream.of(values()).filter(v -> v != UNKNOWN_TO_SDK_VERSION).collect(Collectors.toSet());
    }
}
