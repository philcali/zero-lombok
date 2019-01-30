package me.philcali.zero.lombok.example;

import java.util.List;
import java.util.Map;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.NonNull;

@Builder @Data
public interface Person {
    @NonNull
    String getName();

    int getAge();

    boolean isDead();

    List<String> getScopes();

    Map<String, Vehicle> getVehicles();
}
