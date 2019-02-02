package me.philcali.zero.lombok.processor.context.annotation;

import java.util.Map.Entry;
import java.util.Objects;

import javax.lang.model.element.ExecutableElement;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.annotation.Data;
import me.philcali.zero.lombok.annotation.RequiredArgsConstructor;
import me.philcali.zero.lombok.processor.context.DataContext;
import me.philcali.zero.lombok.processor.context.ProcessorContext;

@AutoService(DataContext.class)
public class RequiredArgsConstructorDataContext implements ConstructorDataContext {

    @Override
    public boolean isApplicable(final ProcessorContext context) {
        return Objects.isNull(context.getParentContext())
                && (Objects.nonNull(context.getElement().getAnnotation(RequiredArgsConstructor.class))
                        || Objects.nonNull(context.getElement().getAnnotation(Data.class)));
    }

    @Override
    public FieldContext createFieldContext(final Entry<String, ExecutableElement> method) {
        final FieldContext context = ConstructorDataContext.super.createFieldContext(method);
        if (context.isRequired()) {
            context.setOutput(true);
        }
        return context;
    }
}
