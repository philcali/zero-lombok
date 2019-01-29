package me.philcali.zero.lombok.processor.mapping.collection;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TypeMappingCollectionTest {
    private TypeMappingCollection mapping;

    @Before
    public void setUp() {
        mapping = new TypeMappingCollection();
    }

    @Test
    public void testMapping() {
        assertTrue(mapping.getContract().isAssignableFrom(mapping.getImplementation()));
    }
}
