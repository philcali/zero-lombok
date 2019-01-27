package me.philcali.zero.lombok.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

public class PersonTest {
    private Person person;

    @Before
    public void setUp() {
        person = PersonData.builder()
                .withName("Philip Cali")
                .withDead(true)
                .withAge(99)
                .build();
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
        assertEquals(person.hashCode(), other.hashCode());
    }
}
