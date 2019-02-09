package me.philcali.zero.lombok.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;

import me.philcali.template.annotation.Template;
import me.philcali.template.api.TemplateEngine;
import me.philcali.template.api.TemplateEngineProvider;
import me.philcali.template.api.TemplateEngineProviderSystem;
import me.philcali.template.api.exception.TemplateNotFoundException;
import me.philcali.zero.lombok.annotation.AllArgsConstructor;
import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.NoArgsConstructor;
import me.philcali.zero.lombok.annotation.RequiredArgsConstructor;
import me.philcali.zero.lombok.processor.context.ProcessorContext;
import me.philcali.zero.lombok.processor.context.ProcessorContextProvider;
import me.philcali.zero.lombok.processor.context.ProcessorContextProviderSystem;

@AutoService(Processor.class)
public class POJOProcessor extends AbstractProcessor {
    private Messager log;
    private TemplateEngineProvider templates;
    private ProcessorContextProvider contextProvider;
    private Map<Name, Element> processedElements;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.log = processingEnv.getMessager();
        this.templates = new TemplateEngineProviderSystem(getClass().getClassLoader());
        this.contextProvider = new ProcessorContextProviderSystem(getClass().getClassLoader());
        this.processedElements = new HashMap<>();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (final TypeElement annotation : annotations) {
            log.printMessage(Kind.NOTE, "Processing annotation: ", annotation);
            final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            elements.stream().filter(this::isInterface).forEach(element -> {
                log.printMessage(Kind.NOTE, "Found annotated element: ", element);
                if (!processedElements.containsKey(element.getSimpleName())) {
                    createDataObject((TypeElement) element);
                    processedElements.put(element.getSimpleName(), element);
                }
            });
        }
        return !processedElements.isEmpty();
    }

    private boolean isInterface(final Element element) {
        return element.getKind() == ElementKind.INTERFACE;
    }

    private void createDataObject(final TypeElement element) {
        final String className = String.format("%sData", element.getQualifiedName());
        try {
            final TemplateEngine engine = templates.get(element.getAnnotation(Template.class));
            final JavaFileObject object = processingEnv.getFiler().createSourceFile(className, element);
            try (PrintWriter writer = new PrintWriter(object.openWriter())) {
                final String result = contextProvider.get(generateContext(className, element), engine);
                writer.print(result.replaceAll("\\n\\n\\n+", "\n"));
            }
        } catch (TemplateNotFoundException | IOException e) {
            log.printMessage(Kind.ERROR, e.getMessage(), element);
        }
    }

    private ProcessorContext generateContext(final String className, final TypeElement element) {
        final int lastDot = className.lastIndexOf('.');
        final String packageName = className.substring(0, lastDot);
        final String simpleName = className.substring(lastDot + 1);
        return ProcessorContext.builder()
                .withElement(element)
                .withSimpleName(simpleName)
                .withPackageName(packageName)
                .build();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> supportedAnnotations = new HashSet<>();
        supportedAnnotations.add(AllArgsConstructor.class.getCanonicalName());
        supportedAnnotations.add(NoArgsConstructor.class.getCanonicalName());
        supportedAnnotations.add(RequiredArgsConstructor.class.getCanonicalName());
        supportedAnnotations.add(Builder.class.getCanonicalName());
        supportedAnnotations.add(Data.class.getCanonicalName());
        return Collections.unmodifiableSet(supportedAnnotations);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
