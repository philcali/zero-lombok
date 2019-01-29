package me.philcali.zero.lombok.processor.mapping;

public interface TypeMapping<C, I extends C> {
    Class<C> getContract();

    Class<I> getImplementation();
}
