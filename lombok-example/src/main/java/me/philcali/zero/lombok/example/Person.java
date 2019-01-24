package me.philcali.zero.lombok.example;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Required;

@Builder
public interface Person {
    @Required
    String getName();

    int getAge();

    boolean isDead();
}
