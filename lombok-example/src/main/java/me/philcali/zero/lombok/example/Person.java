package me.philcali.zero.lombok.example;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.NonNull;

@Builder @Data
// Use a builder if there are required things
@JsonDeserialize(builder = PersonData.Builder.class)
public interface Person {
    @Builder.Default Supplier<Long> DEFAULT_CREATED = System::currentTimeMillis;

    @Builder.Default int DEFAULT_AGE = 99;

    @NonNull
    String getName();

    int getAge();

    boolean isDead();

    long getCreated();

    List<String> getScopes();

    Map<String, Vehicle> getVehicles();
}
