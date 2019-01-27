package me.philcali.zero.lombok.example;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.NonNull;

@Builder @Data
public interface Person {
    @NonNull
    String getName();

    int getAge();

    boolean isDead();
}
