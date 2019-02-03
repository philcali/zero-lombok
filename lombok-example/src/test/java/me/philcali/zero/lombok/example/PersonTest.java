package me.philcali.zero.lombok.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PersonTest {
    private Person person;

    @Before
    public void setUp() {
        person = PersonData.builder()
                .withName("Philip Cali")
                .withDead(true)
                .withAge(99)
                .addScopes("blue")
                .addScopes("42")
                .putVehicles("Old Blue", VehicleData.builder()
                        .withMake("Nissan")
                        .withModel("Leaf")
                        .withYear(2015)
                        .build())
                .build();
    }

    @Test
    public void testJacksonIntegration() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final String json = mapper.writeValueAsString(person);
        final String expectedJson = "{\"created\":" + Person.DEFAULT_CREATED + ",\"name\":\"Philip Cali\",\"vehicles\":{\"Old Blue\":{\"year\":2015,\"model\":\"Leaf\",\"make\":\"Nissan\"}},\"dead\":true,\"scopes\":[\"blue\",\"42\"],\"age\":99}";
        assertEquals(expectedJson, json);
        final Person otherPerson = mapper.readValue(json, Person.class);
        assertEquals(person, otherPerson);
    }

    @Test(expected = NullPointerException.class)
    public void testRequiredParams() {
        new PersonData(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRequiredParamOnBuilder() {
        PersonData.builder().build();
    }

    @Test
    public void testEquals() {
        assertNotEquals(person, null);
        assertNotEquals(person, "blue");
        assertNotEquals(person, 42);

        final PersonData other = new PersonData("Billy Goat, Bill");
        assertNotEquals(person, other);
        other.setName("Philip Cali");
        assertNotEquals(person, other);
        other.setDead(true);
        assertNotEquals(person, other);
        other.setAge(99);
        assertNotEquals(person, other);
        other.setScopes(Arrays.asList("blue", "42"));
        assertNotEquals(person, other);
        final Map<String, Vehicle> vehicles = new HashMap<>();
        vehicles.put("Old Blue", VehicleData.builder()
                .withMake("Nissan")
                .withModel("Leaf")
                .withYear(2015)
                .build());
        other.setVehicles(vehicles);
        assertEquals(person, other);
    }

    @Test
    public void testHashCode() {
        final PersonData other = new PersonData("Billy Goat, Bill");
        assertNotEquals(person.hashCode(), other.hashCode());
        other.setName("Philip Cali");
        assertNotEquals(person.hashCode(), other.hashCode());
        other.setDead(true);
        assertNotEquals(person.hashCode(), other.hashCode());
        other.setAge(99);
        assertNotEquals(person.hashCode(), other.hashCode());
        other.setScopes(Arrays.asList("blue", "42"));
        assertNotEquals(person.hashCode(), other.hashCode());
        final Map<String, Vehicle> vehicles = new HashMap<>();
        vehicles.put("Old Blue", VehicleData.builder()
                .withMake("Nissan")
                .withModel("Leaf")
                .withYear(2015)
                .build());
        other.setVehicles(vehicles);
        assertEquals(person.hashCode(), other.hashCode());
    }
}
