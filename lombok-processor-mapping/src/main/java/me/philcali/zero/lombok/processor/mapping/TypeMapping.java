package me.philcali.zero.lombok.processor.mapping;

import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public interface TypeMapping {
    String getContract();

    String getImplementation();

    default String getVerb() {
        return "add";
    }

    default boolean collectionType() {
        return getContract().equals(List.class.getCanonicalName())
                || getContract().equals(Set.class.getCanonicalName())
                || getContract().equals(Queue.class.getCanonicalName())
                || getContract().equals(Deque.class.getCanonicalName());
    }
}
