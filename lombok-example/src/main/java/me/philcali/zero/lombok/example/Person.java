package me.philcali.zero.lombok.example;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.NonNull;

@Builder @Data
@JsonDeserialize(builder = PersonData.Builder.class)
public interface Person {
    @NonNull
    String getName();

    int getAge();

    boolean isDead();

    List<String> getScopes();

    Map<String, Vehicle> getVehicles();
}
