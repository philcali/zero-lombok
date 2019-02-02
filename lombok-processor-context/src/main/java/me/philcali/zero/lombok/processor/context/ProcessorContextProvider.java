package me.philcali.zero.lombok.processor.context;

import me.philcali.zero.lombok.processor.template.TemplateEngine;

public interface ProcessorContextProvider {
    String get(ProcessorContext context, TemplateEngine engine);
}
