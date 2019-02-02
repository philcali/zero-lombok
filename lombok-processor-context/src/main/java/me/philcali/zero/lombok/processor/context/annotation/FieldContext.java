package me.philcali.zero.lombok.processor.context.annotation;

public class FieldContext {
    private boolean output;
    private boolean required;
    private String fieldName;
    private String returnType;
    private String message;
    private String methodName;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(final String returnType) {
        this.returnType = returnType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(final boolean output) {
        this.output = output;
    }
}
