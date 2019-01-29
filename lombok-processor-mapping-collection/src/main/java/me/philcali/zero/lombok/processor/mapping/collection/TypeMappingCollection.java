package me.philcali.zero.lombok.processor.mapping.collection;

import java.util.ArrayList;
import java.util.Collection;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.processor.mapping.TypeMapping;

@AutoService(TypeMapping.class)
public class TypeMappingCollection implements TypeMapping<Collection, ArrayList> {

    @Override
    public Class<Collection> getContract() {
        return Collection.class;
    }

    @Override
    public Class<ArrayList> getImplementation() {
        return ArrayList.class;
    }
}
