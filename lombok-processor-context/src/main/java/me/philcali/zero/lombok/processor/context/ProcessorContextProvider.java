package me.philcali.zero.lombok.processor.context;

import me.philcali.template.api.TemplateEngine;

public interface ProcessorContextProvider {
    String get(ProcessorContext context, TemplateEngine engine);
}
