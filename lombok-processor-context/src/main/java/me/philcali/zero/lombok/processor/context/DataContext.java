package me.philcali.zero.lombok.processor.context;

import java.util.Map;
import java.util.Optional;

import javax.lang.model.element.ExecutableElement;

import me.philcali.template.api.TemplateEngine;
import me.philcali.zero.lombok.annotation.NonNull;
import me.philcali.zero.lombok.processor.context.annotation.FieldContext;

public interface DataContext {
    String getName();

    boolean isApplicable(ProcessorContext context);

    Object process(ProcessorContext context, TemplateEngine engine);

    default FieldContext createFieldContext(final Map.Entry<String, ExecutableElement> method) {
        final FieldContext fieldContext = new FieldContext();
        fieldContext.setFieldName(method.getKey());
        fieldContext.setReturnType(method.getValue().getReturnType().toString());
        fieldContext.setMethodName(method.getValue().getSimpleName().toString());
        Optional.ofNullable(method.getValue().getAnnotation(NonNull.class)).ifPresent(required -> {
            fieldContext.setRequired(true);
            fieldContext.setMessage(requiredMessage(method.getKey(), required));
        });
        return fieldContext;
    }

    default String requiredMessage(final String fieldName, final NonNull required) {
        return Optional.ofNullable(required.value())
                .filter(m -> !m.isEmpty())
                .orElseGet(() -> "Field " + fieldName + " is a required field.");
    }
}
