package me.philcali.zero.lombok.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ConcreteMap {
    Class<? extends Map> value();
}
