package me.philcali.zero.lombok.processor.mapping;

import java.util.Optional;

public interface TypeMappingProvider {
    Optional<TypeMapping> get(String fullName);
}
