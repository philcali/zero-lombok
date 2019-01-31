package me.philcali.zero.lombok.processor.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import me.philcali.zero.lombok.processor.template.exception.TemplateProcessException;

public class TemplateEngineComposite implements TemplateEngine {
    private final TemplateEngine engine;
    private final List<String> templatePrefixes;

    public TemplateEngineComposite(final TemplateEngine engine, final List<String> templatePrefixes) {
        this.engine = engine;
        this.templatePrefixes = templatePrefixes;
    }

    public TemplateEngineComposite(final TemplateEngine engine) {
        this(engine, new ArrayList<>());
    }

    @Override
    public String getId() {
        return engine.getId();
    }

    @Override
    public void templatePrefix(String location) {
        templatePrefixes.add(location);
    }

    @Override
    public String apply(final String templateName, final Object context) throws TemplateProcessException {
        TemplateProcessException e = null;
        for (final String templatePrefix : templatePrefixes) {
            try {
                engine.templatePrefix(templatePrefix);
                return engine.apply(templateName, context);
            } catch (TemplateProcessException tpe) {
                e = tpe;
            }
        }
        throw Optional.ofNullable(e).orElseGet(() -> new TemplateProcessException("Could not process " + templateName));
    }
}
