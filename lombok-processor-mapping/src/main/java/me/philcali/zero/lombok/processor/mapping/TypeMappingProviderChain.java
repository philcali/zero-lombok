package me.philcali.zero.lombok.processor.mapping;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TypeMappingProviderChain implements TypeMappingProvider {
    private final List<TypeMappingProvider> providers;

    public TypeMappingProviderChain(final List<TypeMappingProvider> providers) {
        this.providers = providers;
    }

    public TypeMappingProviderChain(final TypeMappingProvider ... providers) {
        this(Arrays.asList(providers));
    }

    @Override
    public Optional<TypeMapping> get(final String fullName) {
        for (final TypeMappingProvider provider : providers) {
            final Optional<TypeMapping> foundMapping = provider.get(fullName);
            if (foundMapping.isPresent()) {
                return foundMapping;
            }
        }
        return Optional.empty();
    }
}
