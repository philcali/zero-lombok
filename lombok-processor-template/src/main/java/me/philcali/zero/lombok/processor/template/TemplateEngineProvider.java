package me.philcali.zero.lombok.processor.template;

import java.util.Optional;

public interface TemplateEngineProvider {
    Optional<TemplateEngine> get(String templateId);
}
