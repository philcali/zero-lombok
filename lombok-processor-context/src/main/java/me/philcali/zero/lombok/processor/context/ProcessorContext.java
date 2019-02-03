package me.philcali.zero.lombok.processor.context;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

public class ProcessorContext {
    private final String elementName;
    private final String simpleName;
    private final String packageName;
    private final TypeElement element;
    private final ProcessorContext parentContext;
    private final Map<String, ExecutableElement> fields;

    public static final class Builder {
        private String elementName;
        private String simpleName;
        private String packageName;
        private TypeElement element;
        private ProcessorContext parentContext;
        public Map<String, ExecutableElement> fields;

        public Builder withElement(final TypeElement element) {
            this.element = element;
            return this;
        }

        public Builder withElementName(final String elementName) {
            this.elementName = elementName;
            return this;
        }

        public Builder withSimpleName(final String simpleName) {
            this.simpleName = simpleName;
            return this;
        }

        public Builder withPackageName(final String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder withFields(final Map<String, ExecutableElement> fields) {
            this.fields = fields;
            return this;
        }

        public Builder withParentContext(final ProcessorContext parentContext) {
            this.parentContext = parentContext;
            return this;
        }

        public ProcessorContext build() {
            Objects.requireNonNull(element);
            Objects.requireNonNull(packageName);
            Objects.requireNonNull(simpleName);
            this.elementName = Optional.ofNullable(elementName).orElseGet(this::getDefaultElementName);
            this.fields = Optional.ofNullable(fields).orElseGet(this::getDefaultFields);
            return new ProcessorContext(this);
        }

        private String getDefaultElementName() {
            return element.getSimpleName().toString();
        }

        private Stream<Element> getTypeHierarchy(final TypeElement elem) {
            return Stream.concat(Stream.of(elem), elem.getInterfaces().stream()
                    .filter(type -> type.getKind() == TypeKind.DECLARED)
                    .map(type -> (DeclaredType) type)
                    .map(DeclaredType::asElement)
                    .filter(e -> e.getKind() == ElementKind.INTERFACE)
                    .map(e -> (TypeElement) e)
                    .flatMap(this::getTypeHierarchy));
        }

        private Map<String, ExecutableElement> getDefaultFields() {
            return getTypeHierarchy(element)
                    .flatMap(e -> e.getEnclosedElements().stream())
                    .filter(method -> method.getKind() == ElementKind.METHOD)
                    .map(e -> (ExecutableElement) e)
                    .filter(e -> !e.getModifiers().contains(Modifier.DEFAULT))
                    .collect(Collectors.toMap(
                            e -> StringUtil.applyCase(Character::toLowerCase, e.getSimpleName().toString().replaceAll("^(get|is)", "")),
                            Function.identity()));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private ProcessorContext(final Builder builder) {
        this.element = builder.element;
        this.elementName = builder.elementName;
        this.simpleName = builder.simpleName;
        this.packageName = builder.packageName;
        this.parentContext = builder.parentContext;
        this.fields = builder.fields;
    }

    public ProcessorContext getParentContext() {
        return parentContext;
    }

    public String getElementName() {
        return elementName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getPackageName() {
        return packageName;
    }

    public TypeElement getElement() {
        return element;
    }

    public Map<String, ExecutableElement> getFields() {
        return fields;
    }
}
