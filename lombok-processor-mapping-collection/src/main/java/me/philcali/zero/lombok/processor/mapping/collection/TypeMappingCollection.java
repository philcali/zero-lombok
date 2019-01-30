package me.philcali.zero.lombok.processor.mapping.collection;

import java.util.ArrayList;
import java.util.Collection;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.processor.mapping.TypeMapping;

@AutoService(TypeMapping.class)
public class TypeMappingCollection implements TypeMapping {

    @Override
    public String getContract() {
        return Collection.class.getCanonicalName();
    }

    @Override
    public String getImplementation() {
        return ArrayList.class.getCanonicalName();
    }

    @Override
    public String getVerb() {
        return "add";
    }
}
