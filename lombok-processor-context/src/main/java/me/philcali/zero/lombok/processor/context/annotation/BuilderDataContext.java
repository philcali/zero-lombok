package me.philcali.zero.lombok.processor.context.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.auto.service.AutoService;

import me.philcali.template.api.TemplateEngine;
import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.processor.context.DataContext;
import me.philcali.zero.lombok.processor.context.DataContextProvider;
import me.philcali.zero.lombok.processor.context.DataContextProviderSystem;
import me.philcali.zero.lombok.processor.context.ProcessorContext;

@AutoService(DataContext.class)
public class BuilderDataContext implements DataContext {
    private static final String TEMPLATE_NAME = "Builder";
    private final DataContextProvider provider;

    public BuilderDataContext(final DataContextProvider provider) {
        this.provider = provider;
    }

    public BuilderDataContext() {
        this(new DataContextProviderSystem(BuilderDataContext.class.getClassLoader()));
    }

    @Override
    public String getName() {
        return TEMPLATE_NAME;
    }

    @Override
    public boolean isApplicable(final ProcessorContext context) {
        return Objects.isNull(context.getParentContext())
                && Objects.nonNull(context.getElement().getAnnotation(Builder.class));
    }

    @Override
    public Object process(final ProcessorContext context, final TemplateEngine engine) {
        final Map<String, Object> templateContext = new HashMap<>();
        templateContext.put("simpleName", context.getSimpleName());
        templateContext.put("elementName", context.getElementName());
        templateContext.put("fields", context.getFields().entrySet().stream()
                .map(this::createFieldContext)
                .collect(Collectors.toList()));
        final ProcessorContext builderContext = ProcessorContext.builder()
                .withPackageName(context.getPackageName())
                .withElement(context.getElement())
                .withElementName(context.getElementName())
                .withSimpleName(context.getSimpleName() + ".Builder")
                .withFields(context.getFields())
                .withParentContext(context)
                .build();
        provider.get(builderContext).forEach(dataContext -> {
            templateContext.put(dataContext.getName(), dataContext.process(builderContext, engine));
        });
        return engine.apply(TEMPLATE_NAME, templateContext);
    }
}
