package me.philcali.zero.lombok.processor.mapping;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public class TypeMappingProviderSystem implements TypeMappingProvider {
    private final ClassLoader loader;


    public TypeMappingProviderSystem(final ClassLoader loader) {
        this.loader = loader;
    }

    public TypeMappingProviderSystem() {
        this(ClassLoader.getSystemClassLoader());
    }

    @Override
    public Optional<TypeMapping> get(final String contract) {
        return StreamSupport.stream(ServiceLoader.load(TypeMapping.class, loader).spliterator(), false)
                .filter(mapping -> contract.equals(mapping.getContract()))
                .findFirst();
    }
}
