package me.philcali.zero.lombok.processor.template.exception;

public class TemplateProcessException extends RuntimeException {
    private static final long serialVersionUID = 6798549857326847412L;

    public TemplateProcessException(final Throwable ex) {
        super(ex);
    }

    public TemplateProcessException(final String message) {
        super(message);
    }
}
