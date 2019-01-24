package me.philcali.zero.lombok.processor.template;

import me.philcali.zero.lombok.processor.template.exception.TemplateNotFoundException;

public interface TemplateEngineProvider {
    TemplateEngine get(String templateId) throws TemplateNotFoundException;
}
