package me.philcali.zero.lombok.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Template {
    public static final String DEFAULT_ENGINE = "handlebars";

    public static final String DEFAULT_LOCATION = "/templates";

    String value() default DEFAULT_ENGINE;

    String location() default DEFAULT_LOCATION;
}
