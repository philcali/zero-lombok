package me.philcali.zero.lombok.processor.mapping.collection;

import java.util.Deque;
import java.util.LinkedList;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.processor.mapping.TypeMapping;

@AutoService(TypeMapping.class)
public class TypeMappingDeque implements TypeMapping {

    @Override
    public String getImplementation() {
        return LinkedList.class.getCanonicalName();
    }

    @Override
    public String getContract() {
        return Deque.class.getCanonicalName();
    }
}
