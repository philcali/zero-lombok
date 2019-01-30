package me.philcali.zero.lombok.processor.mapping.collection;

import java.util.HashMap;
import java.util.Map;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.processor.mapping.TypeMapping;

@AutoService(TypeMapping.class)
public class TypeMappingMap implements TypeMapping {
    @Override
    public String getContract() {
        return Map.class.getCanonicalName();
    }

    @Override
    public String getImplementation() {
        return HashMap.class.getCanonicalName();
    }

    @Override
    public String getVerb() {
        return "put";
    }
}
