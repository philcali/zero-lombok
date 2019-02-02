# Zero Lombok

This is an experimental project to supply source generated POJO's
with a builder, `toString`, `equals`, `hashCode` other things without
having to manipulate byte code (ie: lombok).


## Why?

Leave my byte code alone! For more reasons than one, I abhore libraries
that manipulate my (non-test) source byte code. On the other hand:
I fully appreciate utilizing code that I don't have to write, which what
libraries like lombok provide.

Why not achieve the best of both worlds? Let's generate legit source code
from source time annotations + processing!

## How?

Check out the `lombox-example` module. Basically, you define your contract
(because you know, separate contract from implementation), and annotate it
the exact same way you would annotate a Lombok POJO:

__Step 1__: Define your contract

``` java
@Builer @Data
public interface Person {
    @NonNull
    String getName();

    int getAge();

    boolean isDead();
}
```

Pretty straight forward right? You have now defined your contract as an
interface. This is good for multiple reasons, but now you have a Java
compatible contract that can be appropariately mocked, interveaved,
decorated, proxied without *any* byte code manipulation.

__Step 2__: Add the annotation processor

In your maven `build.xml`

``` xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.6.1</version>
      <configuration>
        <annotationProcessorPaths>
          <path>
            <groupId>me.philcali.zero</groupId>
            <artifactId>lombok-processor</artifactId>
            <version>0.0.1-SNAPSHOT</version>
          </path>
        </annotationProcessorPaths>
      </configuration>
    </plugin>
  </plugins>
</build>
```

Adding this processor as a build plugin or an optional dependency will inform
the maven compiler to process annotations in the project (using the processor).

__Step 3__: Use the generated implementation!


For every contract implementation, a new class will be generated with the same
package name and `simpleName` of the class plus `Data`. In this case `PersonData`:

``` java
import java.util.Objects;

public class PersonData implements Person {

    private java.lang.String name;

    @Override
    public java.lang.String getName() {
        return name;
    }
    public void setName(final java.lang.String name) {
        this.name = name;
    }
    private boolean dead;

    @Override
    public boolean isDead() {
        return dead;
    }
    public void setDead(final boolean dead) {
        this.dead = dead;
    }
    private int age;

    @Override
    public int getAge() {
        return age;
    }
    public void setAge(final int age) {
        this.age = age;
    }
    public PersonData(
        final java.lang.String name
    ) {
        Objects.requireNonNull(name, "Field name is a required field");
        this.name = name;
    }
    @Override
    public String toString() {
        return "PersonData:[name = " + name
            + ", dead = " + dead
            + ", age = " + age
            + "]";
    }
    @Override
    public boolean equals(final Object object) {
        if (Objects.isNull(object) || !(object instanceof Person)) {
            return false;
        }
        final Person other = (Person) object;
        return Objects.equals(name, other.getName())
            && Objects.equals(dead, other.isDead())
            && Objects.equals(age, other.getAge());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            name,
            dead,
            age);
    }
    private PersonData(final Builder builder) {

        this.name = builder.name;
        this.dead = builder.dead;
        this.age = builder.age;
    }

    public static final class Builder {

        private java.lang.String name;

        public Builder withName(final java.lang.String name) {
            this.name = name;
            return this;
        }

        private boolean dead;

        public Builder withDead(final boolean dead) {
            this.dead = dead;
            return this;
        }

        private int age;

        public Builder withAge(final int age) {
            this.age = age;
            return this;
        }

        public Person build() {

            Objects.requireNonNull(name, "Field name is a required field");

            return new PersonData(this);
        }
        @Override
        public String toString() {
            return "PersonData.Builder:[name = " + name
                + ", dead = " + dead
                + ", age = " + age
                + "]";
        }

    }

    public static Builder builder() {
        return new Builder();
    }

}
```

All of this is done without any byte code manipulation.

## What about complex builders?

Generated builders work pretty great as is, but `zero-lombok` supports
helpers for building top level collections and maps. By default,
fields that are `List` or `Collection` can be build with `addList`.

``` java
@Builder
public interface Person {
    List<String> getScopes();

    Map<String, Vehicle> getVehicles();
}

@Data @AllArgsConstructor
public interface Vehicle {
    String getMake();
    String getModel();
    int getYear();
}
```

The generated implementation:

``` java
final Person person = PersonData.builder()
    .addScopes("Blue", "42")
    .putVehicles("Car", new VehicleData("Nissan", "Leaf", 2015))
    .build();
```

## What about serialization?

Works out of the box! The `lombok-example` package demonstrates how a single anntation will facilitate
the proper concrete deserialization using the Jackson module.

``` java
@Data
@JsonDeserialize(as = PersonData.class)
public interface Person {
    String getName();
}
```

If you want to enforce encapsulation, immutability, and validation, then Jackson
supports the builders generated by `zero-lombok`.

``` java
@Builder @Data
@JsonDeserialize(builder = PersonData.Builder.class)
public interface Person {
    @NonNull
    String getName();
}
```

Done-zo!

## What about the generated Code Template?

The `zero-lombok` implementation used a pluggable template system, with
the default implementation being `lombok-processor-template-handlebars`.

There different ways to override the generated Java source with the template.

- Per interface override


