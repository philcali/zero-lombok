package me.philcali.zero.lombok.processor.context.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.ConcreteTypes;
import me.philcali.zero.lombok.processor.context.DataContext;
import me.philcali.zero.lombok.processor.context.ProcessorContext;
import me.philcali.zero.lombok.processor.context.StringUtil;
import me.philcali.zero.lombok.processor.mapping.TypeMapping;
import me.philcali.zero.lombok.processor.mapping.TypeMappingBasic;
import me.philcali.zero.lombok.processor.mapping.TypeMappingProvider;
import me.philcali.zero.lombok.processor.mapping.TypeMappingProviderBasic;
import me.philcali.zero.lombok.processor.mapping.TypeMappingProviderChain;
import me.philcali.zero.lombok.processor.mapping.TypeMappingProviderSystem;
import me.philcali.zero.lombok.processor.template.TemplateEngine;

@AutoService(DataContext.class)
public class MethodDataContext implements DataContext {
    private static final String TEMPLATE_NAME = "Field";
    private final TypeMappingProvider provider;

    public MethodDataContext(final TypeMappingProvider provider) {
        this.provider = provider;
    }

    public MethodDataContext() {
        this(new TypeMappingProviderSystem(MethodDataContext.class.getClassLoader()));
    }

    @Override
    public String getName() {
        return "Methods";
    }

    @Override
    public boolean isApplicable(final ProcessorContext context) {
        return !context.getFields().isEmpty();
    }

    @Override
    public Object process(final ProcessorContext context, final TemplateEngine engine) {
        final List<String> methods = new ArrayList<>();
        final Builder.Type setterType = Optional.ofNullable(context.getElement().getAnnotation(Builder.class))
                .filter(builder -> Objects.nonNull(context.getParentContext()))
                .map(Builder::value)
                .orElse(Builder.Type.SETTER);
        final TypeMappingProvider typeProvider = decorateTypeProvider(context.getElement());
        context.getFields().forEach((fieldName, method) -> {
            final Map<String, Object> templateContext = new HashMap<>();
            templateContext.put("fieldName", fieldName);
            templateContext.put("methodName", method.getSimpleName());
            templateContext.put("returnType", method.getReturnType().toString());
            templateContext.put("setterReturnType", context.getSimpleName());
            if (Objects.isNull(context.getParentContext())) {
                templateContext.put("contract", true);
            } else {
                if (method.getReturnType().getKind() == TypeKind.DECLARED) {
                    final DeclaredType declaredType = (DeclaredType) method.getReturnType();
                    final Stream<TypeElement> roots = getRootInterfaces(declaredType.asElement());
                    roots.map(iElement -> typeProvider.get(iElement.getQualifiedName().toString()))
                    .filter(opt -> opt.isPresent())
                    .findFirst()
                    .flatMap(Function.identity())
                    .ifPresent(mapping -> {
                        final int lastNameDot = mapping.getContract().lastIndexOf('.');
                        final String mappingName = mapping.getContract().substring(lastNameDot + 1);
                        templateContext.put(mappingName.toLowerCase(), true);
                        templateContext.put("actionMethodName", mapping.getVerb() + StringUtil.applyCase(
                                Character::toUpperCase, fieldName));
                        templateContext.put("implementation", mapping.getImplementation());
                        templateContext.put("valueTypes", declaredType.getTypeArguments().stream()
                                .map(arg -> arg.toString())
                                .collect(Collectors.toList()));
                    });
                }
            }
            if (fieldName.equals(method.getSimpleName().toString())) {
                templateContext.put("setterMethodName", fieldName);
            } else {
                templateContext.put("setterMethodName", setterType.getVerb() + StringUtil.applyCase(
                        Character::toUpperCase, fieldName));
            }
            methods.add(engine.apply(TEMPLATE_NAME, templateContext));
        });
        return methods;
    }

    @SuppressWarnings("unchecked")
    private TypeMappingProvider decorateTypeProvider(final TypeElement element) {
        final Map<String, TypeMapping> basicMappings = new HashMap<>();
        element.getAnnotationMirrors().stream()
        .filter(mirror -> mirror.getAnnotationType().asElement().getSimpleName().toString().equals(ConcreteTypes.class.getSimpleName()))
        .findFirst()
        .flatMap(mirror -> mirror.getElementValues().values().stream().findFirst())
        .map(value -> (List<AnnotationValue>) value.getValue())
        .ifPresent(values -> {
            values.forEach(value -> {
                final AnnotationMirror mirror = (AnnotationMirror) value.getValue();
                final Map<String, ? extends AnnotationValue> type = mirror.getElementValues()
                        .entrySet().stream().collect(Collectors.toMap(
                                entry -> entry.getKey().getSimpleName().toString(),
                                entry -> entry.getValue()));
                basicMappings.put(type.get("contract").getValue().toString(), new TypeMappingBasic(
                        type.get("contract").getValue().toString(),
                        type.get("implementation").getValue().toString(),
                        Optional.ofNullable(type.get("verb")).map(t -> t.getValue().toString()).orElse("add")));
            });
        });
        return new TypeMappingProviderChain(new TypeMappingProviderBasic(basicMappings), provider);
    }

    private Stream<TypeElement> getRootInterfaces(final Element element) {
        if (element.getKind() == ElementKind.INTERFACE) {
            final TypeElement type = (TypeElement) element;
            return Stream.concat(Stream.of(type), type.getInterfaces().stream()
                    .filter(parent -> parent.getKind() == TypeKind.DECLARED)
                    .map(parent -> (DeclaredType) parent)
                    .flatMap(parent -> getRootInterfaces(parent.asElement())));
        }
        return Stream.empty();
    }
}
