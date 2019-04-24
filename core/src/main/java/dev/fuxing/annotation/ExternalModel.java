package dev.fuxing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Marker interface for model that is referencing an external source.
 * Parameters should follow source convention instead of your own convention.
 * <p>
 * Created by: Fuxing
 * Date: 2019-04-08
 * Time: 02:00
 */
@Documented
@Retention(SOURCE)
@Target({PACKAGE, TYPE, ANNOTATION_TYPE, METHOD, CONSTRUCTOR, FIELD,
        LOCAL_VARIABLE, PARAMETER})
public @interface ExternalModel {
    /**
     * This value element MUST have the name of the source.
     * The recommended convention is to use the full qualified url of the source.
     * For example: https://instagrma.com/dev/doc
     */
    String[] value();

    /**
     * Date when the source was generated.
     */
    String date() default "";

    /**
     * A place holder for any comments that the code generator may want to
     * include in the generated code.
     */
    String comments() default "";
}
