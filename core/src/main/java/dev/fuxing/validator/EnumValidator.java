package dev.fuxing.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by: Fuxing
 * Date: 2019-04-16
 * Time: 01:29
 */
public class EnumValidator implements ConstraintValidator<ValidEnum, Enum> {
    @Override
    public boolean isValid(Enum value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return !value.toString().equals("null");
    }
}
