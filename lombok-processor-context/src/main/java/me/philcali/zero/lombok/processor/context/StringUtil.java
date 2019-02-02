package me.philcali.zero.lombok.processor.context;

import java.util.function.Function;

public final class StringUtil {
    private StringUtil() {
    }

    public static String applyCase(final Function<Character, Character> casing, final String name) {
        return casing.apply(name.charAt(0)) + name.substring(1);
    }
}
