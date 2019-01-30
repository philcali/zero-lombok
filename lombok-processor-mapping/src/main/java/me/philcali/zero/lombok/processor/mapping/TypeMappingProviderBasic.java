package me.philcali.zero.lombok.processor.mapping;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TypeMappingProviderBasic implements TypeMappingProvider {
    private final Map<String, TypeMapping> basicMappings;

    public TypeMappingProviderBasic(final Map<String, TypeMapping> basicMappings) {
        this.basicMappings = basicMappings;
    }

    public TypeMappingProviderBasic(final TypeMapping ... basicMappings) {
        this(Arrays.stream(basicMappings).collect(Collectors.toMap(
                mapping -> mapping.getContract(),
                Function.identity())));
    }

    @Override
    public Optional<TypeMapping> get(final String fullName) {
        return Optional.ofNullable(basicMappings.get(fullName));
    }
}
