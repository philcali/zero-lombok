package me.philcali.zero.lombok.processor.context.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.auto.service.AutoService;

import me.philcali.template.api.TemplateEngine;
import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.ToString;
import me.philcali.zero.lombok.processor.context.DataContext;
import me.philcali.zero.lombok.processor.context.ProcessorContext;

@AutoService(DataContext.class)
public class ToStringDataContext implements DataContext {
    private static final String TEMPLATE_NAME = "ToString";

    @Override
    public String getName() {
        return TEMPLATE_NAME;
    }

    @Override
    public boolean isApplicable(final ProcessorContext context) {
        return Objects.nonNull(context.getElement().getAnnotation(ToString.class))
                || Objects.nonNull(context.getElement().getAnnotation(Data.class));
    }

    @Override
    public Object process(final ProcessorContext context, final TemplateEngine engine) {
        final Map<String, Object> templateContext = new HashMap<>();
        final List<Map<String, Object>> fields = new ArrayList<>();
        context.getFields().forEach((fieldName, field) -> {
            final Map<String, Object> fieldContext = new HashMap<>();
            fieldContext.put("fieldName", fieldName);
            fields.add(fieldContext);
        });
        templateContext.put("simpleName", context.getSimpleName());
        templateContext.put("fields", fields);
        return engine.apply(TEMPLATE_NAME, templateContext);
    }
}
