package me.philcali.zero.lombok.example;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.NonNull;

@Builder
public interface Person {
    @NonNull
    String getName();

    int getAge();

    boolean isDead();
}
