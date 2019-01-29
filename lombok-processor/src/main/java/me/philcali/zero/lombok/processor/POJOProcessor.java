package me.philcali.zero.lombok.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.annotation.AllArgsConstructor;
import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.EqualsAndHashCode;
import me.philcali.zero.lombok.annotation.NoArgsConstructor;
import me.philcali.zero.lombok.annotation.NonNull;
import me.philcali.zero.lombok.annotation.RequiredArgsConstructor;
import me.philcali.zero.lombok.annotation.Template;
import me.philcali.zero.lombok.annotation.ToString;
import me.philcali.zero.lombok.processor.mapping.TypeMappingProvider;
import me.philcali.zero.lombok.processor.mapping.TypeMappingProviderSystem;
import me.philcali.zero.lombok.processor.template.TemplateEngine;
import me.philcali.zero.lombok.processor.template.TemplateEngineProvider;
import me.philcali.zero.lombok.processor.template.TemplateEngineProviderSystem;
import me.philcali.zero.lombok.processor.template.exception.TemplateNotFoundException;

@AutoService(Processor.class)
public class POJOProcessor extends AbstractProcessor {
    private static final String DEFAULT_TEMPLATE = "generated-type";
    private Messager log;
    private TemplateEngineProvider provider;
    private TypeMappingProvider mappings;
    private Map<Name, Element> processedElements;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.log = processingEnv.getMessager();
        this.provider = new TemplateEngineProviderSystem(getClass().getClassLoader());
        this.mappings = new TypeMappingProviderSystem(getClass().getClassLoader());
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
        return true;
    }

    private boolean isInterface(final Element element) {
        return element.getKind() == ElementKind.INTERFACE;
    }

    private void createDataObject(final TypeElement element) {
        final String className = String.format("%sData", element.getQualifiedName());

        try {
            final TemplateEngine engine = objectTemplate(element);
            final JavaFileObject object = processingEnv.getFiler().createSourceFile(className, element);
            try (PrintWriter writer = new PrintWriter(object.openWriter())) {
                final String result = engine.apply(DEFAULT_TEMPLATE, generateContext(className, element));
                writer.print(result.replaceAll("\\n\\n\\n+", "\n"));
            }
        } catch (TemplateNotFoundException | IOException e) {
            log.printMessage(Kind.ERROR, e.getMessage(), element);
        }
    }

    private Map<String, ExecutableElement> getElementMethods(final TypeElement element) {
        return element.getEnclosedElements().stream()
                .filter(method -> method.getKind() == ElementKind.METHOD)
                .map(e -> (ExecutableElement) e)
                .collect(Collectors.toMap(
                        e -> applyCase(Character::toLowerCase, e.getSimpleName().toString().replaceAll("^(get|is)", "")),
                        Function.identity()));
    }

    private Object generateContext(final String className, final TypeElement element) {
        final int lastDot = className.lastIndexOf('.');
        final String packageName = className.substring(0, lastDot);
        final String simpleName = className.substring(lastDot + 1);

        final Map<String, ExecutableElement> methods = getElementMethods(element);
        final Map<String, Object> context = new HashMap<>();

        final boolean dataTag = Objects.nonNull(element.getAnnotation(Data.class));
        final Builder builder = element.getAnnotation(Builder.class);
        if (Objects.nonNull(builder)) {
            context.put("builder", true);
        }


        final List<Map<String, Object>> fields = new ArrayList<>();
        methods.forEach((fieldName, method) -> {
            final Map<String, Object> fieldContext = new HashMap<>();
            fieldContext.put("fieldName", fieldName);
            fieldContext.put("returnType", method.getReturnType().toString());
            fieldContext.put("methodName", method.getSimpleName().toString());
            if (fieldName.equals(method.getSimpleName().toString())) {
                fieldContext.put("setterMethodName", fieldName);
            } else {
                fieldContext.put("setterMethodName", "set" + applyCase(Character::toUpperCase, fieldName));
            }
            final NonNull required = method.getAnnotation(NonNull.class);
            if (Objects.nonNull(required)) {
                fieldContext.put("required", true);
                fieldContext.put("message", Optional.ofNullable(required.value())
                        .filter(m -> !m.isEmpty())
                        .orElseGet(() -> "Field " + fieldName + " is a required field"));
            }
            Optional.ofNullable(builder).map(Builder::value).ifPresent(type -> {
                switch (type) {
                case FLUENT:
                    fieldContext.put("fluentMethodName", "with" + applyCase(Character::toUpperCase, fieldName));
                    break;
                default:
                    fieldContext.put("fluentMethodName", fieldContext.get("setterMethodName"));
                    break;
                }
            });
            if (method.getReturnType().getKind() == TypeKind.DECLARED) {
                final DeclaredType declaredType = (DeclaredType) method.getReturnType();
                final Stream<TypeElement> roots = getRootInterfaces(declaredType.asElement());
                roots.map(iElement -> mappings.get(iElement.getQualifiedName().toString()))
                .filter(opt -> opt.isPresent())
                .findFirst()
                .flatMap(Function.identity())
                .ifPresent(mapping -> {
                    fieldContext.put(mapping.getContract().getSimpleName().toLowerCase(), true);
                    fieldContext.put("contract", mapping.getContract().getCanonicalName());
                    fieldContext.put("implementation", mapping.getImplementation().getCanonicalName());
                    fieldContext.put("valueTypes", declaredType.getTypeArguments().stream()
                            .map(arg -> arg.toString())
                            .collect(Collectors.toList()));
                });
            }
            fields.add(fieldContext);
        });

        context.put("packageName", packageName);
        context.put("simpleName", simpleName);
        context.put("elementName", element.getSimpleName());
        context.put("fields", fields);

        context.put("toString", dataTag || Objects.nonNull(element.getAnnotation(ToString.class)));
        context.put("equalsAndHashCode", dataTag || Objects.nonNull(element.getAnnotation(EqualsAndHashCode.class)));
        context.put("noArgs", Objects.nonNull(element.getAnnotation(NoArgsConstructor.class)));
        context.put("allArgs", Objects.nonNull(element.getAnnotation(AllArgsConstructor.class)));
        context.put("requiredArgs", dataTag || Objects.nonNull(element.getAnnotation(RequiredArgsConstructor.class)));
        return context;
    }

    private Stream<TypeElement> getRootInterfaces(final Element element) {
        if (isInterface(element)) {
            final TypeElement type = (TypeElement) element;
            return Stream.concat(Stream.of(type), type.getInterfaces().stream()
                    .filter(parent -> parent.getKind() == TypeKind.DECLARED)
                    .map(parent -> (DeclaredType) parent)
                    .flatMap(parent -> getRootInterfaces(parent.asElement())));
        }
        return Stream.empty();
    }

    private TemplateEngine objectTemplate(final TypeElement element) {
        final Template template = element.getAnnotation(Template.class);
        if (Objects.nonNull(template)) {
            TemplateEngine engine = provider.get(template.value());
            engine.templatePrefix(template.location());
            return engine;
        }
        return provider.get(Template.DEFAULT_ENGINE);
    }

    private String applyCase(final Function<Character, Character> casing, final String name) {
        return casing.apply(name.charAt(0)) + name.substring(1);
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
