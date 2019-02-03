package me.philcali.zero.lombok.example;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.NonNull;

@Data @Builder
public interface Employee extends Person {
    @NonNull
    String getId();
}
