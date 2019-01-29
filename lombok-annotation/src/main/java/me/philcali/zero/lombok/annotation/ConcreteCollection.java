package me.philcali.zero.lombok.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ConcreteCollection {
    Class<? extends Collection> value();
}
