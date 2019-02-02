package me.philcali.zero.lombok.processor.context.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.EqualsAndHashCode;
import me.philcali.zero.lombok.processor.context.DataContext;
import me.philcali.zero.lombok.processor.context.ProcessorContext;
import me.philcali.zero.lombok.processor.template.TemplateEngine;

@AutoService(DataContext.class)
public class EqualsAndHashCodeDataContext implements DataContext {
    private static final String TEMPLATE_NAME = "EqualsAndHashCode";

    @Override
    public String getName() {
        return TEMPLATE_NAME;
    }

    @Override
    public boolean isApplicable(final ProcessorContext context) {
        return Objects.isNull(context.getParentContext())
                && (Objects.nonNull(context.getElement().getAnnotation(EqualsAndHashCode.class))
                        || Objects.nonNull(context.getElement().getAnnotation(Data.class)));
    }

    @Override
    public Object process(final ProcessorContext context, final TemplateEngine engine) {
        final Map<String, Object> templateContext = new HashMap<>();
        templateContext.put("elementName", context.getElementName());
        templateContext.put("fields", context.getFields().entrySet().stream()
                .map(this::createFieldContext)
                .collect(Collectors.toList()));
        return engine.apply(TEMPLATE_NAME, templateContext);
    }

}
