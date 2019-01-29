package me.philcali.zero.lombok.processor.mapping;

import java.util.Optional;

public interface TypeMappingProvider {
    <C> Optional<TypeMapping<C, ? extends C>> get(String fullName);

    default <C> Optional<TypeMapping<C, ? extends C>> get(final Class<C> contract) {
        return get(contract.getCanonicalName());
    }
}
