package me.philcali.zero.lombok.example;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.EqualsAndHashCode;
import me.philcali.zero.lombok.annotation.NoArgsConstructor;
import me.philcali.zero.lombok.annotation.NonNull;
import me.philcali.zero.lombok.annotation.ToString;

@Builder @ToString @NoArgsConstructor @EqualsAndHashCode
public interface Person {
    @NonNull
    String getName();

    int getAge();

    boolean isDead();
}
