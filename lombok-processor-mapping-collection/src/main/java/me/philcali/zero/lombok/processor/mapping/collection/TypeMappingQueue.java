package me.philcali.zero.lombok.processor.mapping.collection;

import java.util.LinkedList;
import java.util.Queue;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.processor.mapping.TypeMapping;

@AutoService(TypeMapping.class)
public class TypeMappingQueue implements TypeMapping {

    @Override
    public String getContract() {
        return Queue.class.getCanonicalName();
    }

    @Override
    public String getImplementation() {
        return LinkedList.class.getCanonicalName();
    }

    @Override
    public String getVerb() {
        return "enqueue";
    }
}
