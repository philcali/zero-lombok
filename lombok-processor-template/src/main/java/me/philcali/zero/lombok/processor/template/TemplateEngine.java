package me.philcali.zero.lombok.processor.template;

import me.philcali.zero.lombok.processor.template.exception.TemplateProcessException;

public interface TemplateEngine {
    String getId();

    String apply(String templateName, Object context) throws TemplateProcessException;

    void templatePrefix(String location);
}
