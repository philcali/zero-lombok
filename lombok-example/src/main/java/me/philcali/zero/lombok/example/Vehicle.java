package me.philcali.zero.lombok.example;

import me.philcali.zero.lombok.annotation.AllArgsConstructor;
import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Data;

@Builder @Data @AllArgsConstructor
public interface Vehicle {
    String make();

    String model();

    int year();
}
