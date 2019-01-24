package me.philcali.zero.lombok.processor.template.exception;

public class TemplateNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 7868773754659539102L;
    private final String templateId;

    public TemplateNotFoundException(final String templateId) {
        super("Could not find a template with id: " + templateId);
        this.templateId = templateId;
    }

    public String getTemplateId() {
        return templateId;
    }
}
