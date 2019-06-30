package dev.fuxing.validator;

import dev.fuxing.err.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by: Fuxing
 * Date: 2019-04-16
 * Time: 01:50
 */
class ValidEnumTest {
    @Test
    void valid() {
        Bean bean = new Bean();
        bean.example = Example.EXAMPLE;

        ValidationException.validate(bean);
    }

    @Test
    void empty() {
        Bean bean = new Bean();
        bean.example = null;

        Assertions.assertThrows(ValidationException.class, () -> {
            ValidationException.validate(bean);
        });
    }

    @Test
    void unknown() {
        Bean bean = new Bean();
        bean.example = Example.UNKNOWN_TO_SDK_VERSION;

        Assertions.assertThrows(ValidationException.class, () -> {
            ValidationException.validate(bean);
        });
    }
}

class Bean {
    @ValidEnum
    public Example example;
}
