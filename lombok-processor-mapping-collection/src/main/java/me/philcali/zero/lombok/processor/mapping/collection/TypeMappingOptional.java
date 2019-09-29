package me.philcali.zero.lombok.processor.mapping.collection;

import java.util.Optional;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.processor.mapping.TypeMapping;

@AutoService(TypeMapping.class)
public class TypeMappingOptional implements TypeMapping {

    @Override
    public String getContract() {
        return Optional.class.getCanonicalName();
    }

    @Override
    public String getImplementation() {
        return Optional.class.getCanonicalName();
    }

    @Override
    public String getVerb() {
        return "set";
    }
}
