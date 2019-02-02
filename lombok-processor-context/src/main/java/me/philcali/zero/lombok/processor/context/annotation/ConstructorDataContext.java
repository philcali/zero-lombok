package me.philcali.zero.lombok.processor.context.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import me.philcali.zero.lombok.processor.context.DataContext;
import me.philcali.zero.lombok.processor.context.ProcessorContext;
import me.philcali.zero.lombok.processor.template.TemplateEngine;

public interface ConstructorDataContext extends DataContext {
    String TEMPLATE_NAME = "Constructor";

    @Override
    default String getName() {
        return "Constructors";
    }

    @Override
    default Object process(final ProcessorContext context, TemplateEngine engine) {
        final Map<String, Object> templateContext = new HashMap<>();
        templateContext.put("simpleName", context.getSimpleName());
        templateContext.put("fields", context.getFields().entrySet().stream()
                .map(this::createFieldContext)
                .collect(Collectors.toList()));
        return engine.apply(TEMPLATE_NAME, templateContext);
    }
}
