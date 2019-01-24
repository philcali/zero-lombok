package me.philcali.zero.lombok.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.annotation.Builder;
import me.philcali.zero.lombok.annotation.Required;

@AutoService(Processor.class)
public class POJOProcessor extends AbstractProcessor {
    private Messager log;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.log = processingEnv.getMessager();
    }

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		for (final TypeElement annotation : annotations) {
		    log.printMessage(Kind.NOTE, "Processing annotation: ", annotation);
		    final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
		    elements.stream().filter(this::isInterface).forEach(element -> {
		        log.printMessage(Kind.NOTE, "Found annotated element: ", element);
		        createDataObject((TypeElement) element, annotation);
		    });
		}
	    return true;
	}

	private boolean isInterface(final Element element) {
	    return element.getKind() == ElementKind.INTERFACE;
	}

	private void createDataObject(final TypeElement element, final TypeElement annotation) {
	    final String className = String.format("%sData", element.getQualifiedName());
	    final int lastDot = className.lastIndexOf('.');
	    final String packageName = className.substring(0, lastDot);
	    final String simpleName = className.substring(lastDot + 1);
	    try {
            final JavaFileObject object = processingEnv.getFiler().createSourceFile(className, element);
            try (PrintWriter writer = new PrintWriter(object.openWriter())) {
                // Class definition
                writer.println("package " + packageName + ";");
                writer.println("import " + Objects.class.getCanonicalName() + ";");
                writer.println();
                writer.println("public class " + simpleName + " implements " + element.getSimpleName() + " {");

                final Map<String, ExecutableElement> methods = element.getEnclosedElements().stream()
                        .filter(method -> method.getKind() == ElementKind.METHOD)
                        .map(e -> (ExecutableElement) e)
                        .collect(Collectors.toMap(
                                e -> applyCase(Character::toLowerCase, e.getSimpleName().toString().replaceAll("^(get|is)", "")),
                                Function.identity()));

                // Standard pojo
                printFields(writer, methods, "set", simpleName, true);
                // Equals
                printEquals(writer, element, methods);
                // HashCode
                printHashCode(writer, methods);
                // ToString
                printToString(writer, simpleName, methods);
                // Builder
                printBuilder(writer, simpleName, methods);

                writer.println("}");
            }
        } catch (IOException e) {
            log.printMessage(Kind.ERROR, e.getMessage(), element);
        }
	}

	private void printFields(
	        final PrintWriter writer,
	        final Map<String, ExecutableElement> methods,
	        final String methodPrefix,
	        final String returnType,
	        final boolean getters) {
	    methods.forEach((name, method) -> {
            writer.println("    private " + method.getReturnType().toString() + " " + name + ";");
            writer.println();

            if (getters) {
                writer.println("    @Override");
                writer.println("    public " + method.getReturnType().toString() + " " + method.getSimpleName() + "() {");
                writer.println("        return " + name + ";");
                writer.println("    }");
                writer.println();
            }

            final String upperCased = Optional.ofNullable(name)
                    .filter(other -> !method.getSimpleName().toString().equals(other))
                    .map(other -> methodPrefix + applyCase(Character::toUpperCase, other))
                    .orElse(name);
            writer.println("    public " + returnType + " " + upperCased + "(final " + method.getReturnType().toString() + " " + name + ") {");
            writer.println("        this." + name + " = " + name + ";");
            writer.println("        return this;");
            writer.println("    }");
            writer.println();
        });
	}

	private void printEquals(final PrintWriter writer, final TypeElement element, final Map<String, ExecutableElement> methods) {
        writer.println("    @Override");
        writer.println("    public boolean equals(final Object object) {");
        writer.println(String.format("        if (Objects.isNull(object) || !(object instanceof %s)) {", element.getSimpleName()));
        writer.println("            return false;");
        writer.println("        }");
        writer.println(String.format("        final %s other = (%s) object;", element.getSimpleName(), element.getSimpleName()));
        final String fieldsEquals = methods.entrySet().stream().map(entry -> {
            return String.format("Objects.equals(%s, other.%s())", entry.getKey(), entry.getValue().getSimpleName());
        }).collect(Collectors.joining("\n            && "));
        writer.println("        return " + fieldsEquals + ";");
        writer.println("    }");
        writer.println();
	}

	private void printHashCode(final PrintWriter writer, final Map<String, ExecutableElement> methods) {
        writer.println("    @Override");
        writer.println("    public int hashCode() {");
        writer.println(String.format("        return Objects.hash(%s);", methods.keySet().stream()
                .collect(Collectors.joining(", ")) ));
        writer.println("    }");
        writer.println();
	}

	private void printToString(final PrintWriter writer, final String simpleName, final Map<String, ExecutableElement> methods) {
        writer.println("    @Override");
        writer.println("    public String toString() {");
        writer.println(String.format("        return \"%s:[%s]\";", simpleName, methods.keySet().stream()
                .map(name -> String.format("%s=\" + %s + \"", name, name))
                .collect(Collectors.joining(", "))));
        writer.println("    }");
	}

	private void printBuilder(final PrintWriter writer, final String simpleName, final Map<String, ExecutableElement> methods) {
	    writer.println();
        writer.println("    public " + simpleName + "() {");
        writer.println("    }");

        writer.println();
        writer.println("    public " + simpleName + "(final Builder builder) {");
        methods.forEach((name, method) -> {
            writer.println("        this." + name + " = builder." + name + ";");
        });
        writer.println("    }");

        writer.println();
        writer.println("    public static Builder builder() {");
        writer.println("        return new Builder();");
        writer.println("    }");
        writer.println();

        writer.println("    public static final class Builder {");
        printFields(writer, methods, "with", "Builder", false);
        writer.println("        public " + simpleName + " build() {");
        methods.forEach((name, method) -> {
            final Required requiredMessage = method.getAnnotation(Required.class);
            if (Objects.nonNull(requiredMessage)) {
                final String message = Optional.ofNullable(requiredMessage.value())
                        .filter(m -> !m.isEmpty())
                        .orElseGet(() -> "Field '" + name + "' must be set");
                writer.println("            Objects.requireNonNull(" + name + ", \"" + message + "\");");
            }
        });
        writer.println("            return new " + simpleName + "(this);");
        writer.println("        }");
        writer.println("    }");
	}

	private String applyCase(final Function<Character, Character> casing, final String name) {
	    return casing.apply(name.charAt(0)) + name.substring(1);
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
	    final Set<String> supportedAnnotations = new HashSet<>();
	    supportedAnnotations.add(Builder.class.getCanonicalName());
	    return Collections.unmodifiableSet(supportedAnnotations);
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
	    return SourceVersion.latestSupported();
	}
}
