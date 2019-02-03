package me.philcali.zero.lombok.processor.context.exception;

public class ProcessContextException extends RuntimeException {
    private static final long serialVersionUID = -677443084705773270L;

    public ProcessContextException(final String message) {
        super(message);
    }
}
