package me.philcali.zero.lombok.processor.mapping;

public class TypeMappingBasic implements TypeMapping {
    private String contract;
    private String implementation;
    private String verb;

    public TypeMappingBasic(
            final String contract,
            final String implementation,
            final String verb) {
        this.contract = contract;
        this.implementation = implementation;
        this.verb = verb;
    }

    @Override
    public String getContract() {
        return contract;
    }

    @Override
    public String getImplementation() {
        return implementation;
    }

    @Override
    public String getVerb() {
        return verb;
    }
}
