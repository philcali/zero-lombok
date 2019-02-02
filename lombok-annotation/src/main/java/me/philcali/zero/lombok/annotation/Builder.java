package me.philcali.zero.lombok.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Builder {
    enum Type {
        FLUENT("with"),
        SETTER("set");

        private final String verb;

        Type(final String verb) {
            this.verb = verb;
        }

        public String getVerb() {
            return verb;
        }
    }

    Type value() default Type.FLUENT;
}
