package me.philcali.zero.lombok.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.ConcreteCollection;
import me.philcali.zero.lombok.annotation.ConcreteMap;
import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.NonNull;

@Builder @Data
public interface Person {
    @NonNull
    String getName();

    int getAge();

    boolean isDead();

    @ConcreteCollection(ArrayList.class)
    List<String> getScopes();

    @ConcreteMap(HashMap.class)
    Map<String, Vehicle> getVehicles();
}
