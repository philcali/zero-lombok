package me.philcali.zero.lombok.processor.template;

import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import me.philcali.zero.lombok.processor.template.exception.TemplateNotFoundException;

public class TemplateEngineProviderSystem implements TemplateEngineProvider {
    private final ClassLoader loader;

    public TemplateEngineProviderSystem(final ClassLoader loader) {
        this.loader = loader;
    }

    public TemplateEngineProviderSystem() {
        this(ClassLoader.getSystemClassLoader());
    }

    @Override
    public TemplateEngine get(final String templateId) throws TemplateNotFoundException {
        final ServiceLoader<TemplateEngine> engines = ServiceLoader.load(TemplateEngine.class, loader);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(engines.iterator(), Spliterator.ORDERED), false)
                .filter(engine -> engine.getId().equals(templateId))
                .findFirst()
                .orElseThrow(() -> new TemplateNotFoundException(templateId));
    }
}
