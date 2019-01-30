package me.philcali.zero.lombok.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ConcreteType {
    Class<?> contract();
    Class<?> implementation();
    String verb() default "add";
}
