package dev.fuxing.exception;

import com.fasterxml.jackson.databind.JsonNode;
import dev.fuxing.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created By: Fuxing Loh
 * Date: 5/7/2017
 * Time: 7:49 PM
 */
public class ValidationException extends TransportException {
    public static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private List<String> reasons;

    static {
        ExceptionParser.register(ValidationException.class, ValidationException::new);
    }

    ValidationException(TransportException e) {
        super(e);
    }

    /**
     * @param key    of field validated
     * @param reason for the validation error
     */
    public ValidationException(String key, String reason) {
        this("Validation failed on " + key + "\nReason: " + reason + ".");
    }

    /**
     * @param reasons a list of reasons for the validation error
     */
    private ValidationException(List<String> reasons) {
        this(reasons.size() + " validation failed.\n" + String.join("\n", reasons));
        this.reasons = reasons;
    }

    /**
     * @param reasons for user to see
     * @param object  converted into json as Stacktrace
     */
    private ValidationException(List<String> reasons, Object object) {
        this(reasons.size() + " validation failed.\n" + String.join("\n", reasons), JsonUtils.toString(object));
        this.reasons = reasons;

    }

    private ValidationException(String reason) {
        super(422, ValidationException.class, reason);
    }

    public List<String> getReasons() {
        return reasons;
    }

    /**
     * Use Hibernate validator for violations validation
     *
     * @param object to validate
     * @param groups optional group or list of groups targeted for validation
     * @param <T>    the type of the object to validate
     * @throws ValidationException throws exception if failed
     */
    public static <T> void validate(T object, Class<?>... groups) throws ValidationException {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (violations.isEmpty()) return;

        List<String> reasons = violations.stream()
                .map(violation -> {
                    Path path = violation.getPropertyPath();
                    String message = violation.getMessage();
                    return path.toString() + ": " + message;
                })
                .collect(Collectors.toList());
        throw new ValidationException(reasons, object);

    }

    /**
     * @param node check if node is not null or missing
     * @return same node
     * @throws ValidationException if null or missing
     */
    public static JsonNode require(String key, JsonNode node) throws ValidationException {
        if (node.isNull() && node.isMissingNode()) {
            throw new ValidationException(key, "Required node is missing or null");
        }
        return node;
    }

    /**
     * @param node check if node is not null, missing or blank
     * @return same node
     * @throws ValidationException if null, missing or blank
     */
    public static String requireNonBlank(String key, JsonNode node) throws ValidationException {
        require(key, node);
        String text = node.asText(null);
        if (StringUtils.isBlank(text)) throw new ValidationException(key, "Required node is blank.");
        return text;
    }

    /**
     * Helper method to check and get values
     *
     * @param key   key to param
     * @param value value that is required non null
     * @return same value
     * @throws ParamException if null
     */
    public static <T> T requireNonNull(String key, T value) throws ParamException {
        if (value == null) throw new ValidationException(key, "Value cannot be null.");
        return value;
    }

    /**
     * Helper method to check and get values
     *
     * @param key   key to param
     * @param value value that is required non blank
     * @return same value
     * @throws ParamException if blank
     */
    public static <T extends CharSequence> T requireNonBlank(String key, T value) throws ParamException {
        if (StringUtils.isBlank(value)) throw new ValidationException(key, "Value cannot be blank.");
        return value;
    }
}
