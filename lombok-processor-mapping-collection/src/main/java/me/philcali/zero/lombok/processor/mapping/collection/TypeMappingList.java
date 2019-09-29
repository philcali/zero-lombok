package me.philcali.zero.lombok.processor.mapping.collection;

import java.util.ArrayList;
import java.util.List;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.processor.mapping.TypeMapping;

@AutoService(TypeMapping.class)
public class TypeMappingList implements TypeMapping {

    @Override
    public String getContract() {
        return List.class.getCanonicalName();
    }

    @Override
    public String getImplementation() {
        return ArrayList.class.getCanonicalName();
    }
}
