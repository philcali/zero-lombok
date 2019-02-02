package me.philcali.zero.lombok.processor.context;

import java.util.HashMap;
import java.util.Map;

import me.philcali.zero.lombok.processor.template.TemplateEngine;

public class ProcessorContextProviderSystem implements ProcessorContextProvider {
    private static final String TEMPLATE_NAME = "Data";
    private final DataContextProvider provider;

    public ProcessorContextProviderSystem(final DataContextProvider provider) {
        this.provider = provider;
    }

    public ProcessorContextProviderSystem(final ClassLoader loader) {
        this(new DataContextProviderSystem(loader));
    }

    public ProcessorContextProviderSystem() {
        this(ClassLoader.getSystemClassLoader());
    }

    @Override
    public String get(final ProcessorContext context, final TemplateEngine engine) {
        final Map<String, Object> templateContext = new HashMap<>();
        templateContext.put("simpleName", context.getSimpleName());
        templateContext.put("packageName", context.getPackageName());
        templateContext.put("elementName", context.getElementName());
        provider.get(context).forEach(dataContext -> {
            templateContext.put(dataContext.getName(), dataContext.process(context, engine));
        });
        return engine.apply(TEMPLATE_NAME, templateContext);
    }
}
