package me.philcali.zero.lombok.processor.mapping.collection;

import java.util.HashSet;
import java.util.Set;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.processor.mapping.TypeMapping;

@AutoService(TypeMapping.class)
public class TypeMappingSet implements TypeMapping {
    @Override
    public String getContract() {
        return Set.class.getCanonicalName();
    }

    @Override
    public String getImplementation() {
        return HashSet.class.getCanonicalName();
    }
}
