package me.philcali.zero.lombok.processor.template.handlebars;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.processor.template.TemplateEngine;
import me.philcali.zero.lombok.processor.template.exception.TemplateProcessException;

@AutoService(TemplateEngine.class)
public class TemplateEngineHandlebars implements TemplateEngine {
    private static final String TEMPLATE_ID = "handlebars";
    private final Handlebars handlebars;

    public TemplateEngineHandlebars(final Handlebars handlebars) {
        this.handlebars = handlebars;
    }

    public TemplateEngineHandlebars() {
        this(new Handlebars());
    }

    @Override
    public String getId() {
        return TEMPLATE_ID;
    }

    @Override
    public String apply(final String templateName, final Object context) throws TemplateProcessException {
        try {
            final Template template = handlebars.compile(templateName);
            return template.apply(context);
        } catch (IOException e) {
            throw new TemplateProcessException(e);
        }
    }

    @Override
    public void templatePrefix(final String location) {
        handlebars.with(new ClassPathTemplateLoader(location));
    }
}
