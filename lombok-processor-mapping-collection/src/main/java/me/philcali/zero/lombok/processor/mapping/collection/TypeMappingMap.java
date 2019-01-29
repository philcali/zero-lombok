package me.philcali.zero.lombok.processor.mapping.collection;

import java.util.HashMap;
import java.util.Map;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.processor.mapping.TypeMapping;

@AutoService(TypeMapping.class)
public class TypeMappingMap implements TypeMapping<Map, HashMap> {
    @Override
    public Class<Map> getContract() {
        return Map.class;
    }

    @Override
    public Class<HashMap> getImplementation() {
        return HashMap.class;
    }
}
