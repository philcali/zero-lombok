package me.philcali.zero.lombok.example;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import me.philcali.zero.lombok.annotation.AllArgsConstructor;
import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Data;

@Builder @Data @AllArgsConstructor
// Use data setters where applicable and no creator exists
@JsonDeserialize(as = VehicleData.class)
public interface Vehicle {
    String getMake();

    String getModel();

    int getYear();
}
