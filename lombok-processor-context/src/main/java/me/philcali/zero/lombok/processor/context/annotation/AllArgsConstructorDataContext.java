package me.philcali.zero.lombok.processor.context.annotation;

import java.util.Map.Entry;
import java.util.Objects;

import javax.lang.model.element.ExecutableElement;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.annotation.AllArgsConstructor;
import me.philcali.zero.lombok.processor.context.DataContext;
import me.philcali.zero.lombok.processor.context.ProcessorContext;

@AutoService(DataContext.class)
public class AllArgsConstructorDataContext implements ConstructorDataContext {

    @Override
    public boolean isApplicable(final ProcessorContext context) {
        return Objects.isNull(context.getParentContext())
                && Objects.nonNull(context.getElement().getAnnotation(AllArgsConstructor.class));
    }

    @Override
    public FieldContext createFieldContext(final Entry<String, ExecutableElement> method) {
        final FieldContext context = ConstructorDataContext.super.createFieldContext(method);
        context.setOutput(true);
        return context;
    }
}
