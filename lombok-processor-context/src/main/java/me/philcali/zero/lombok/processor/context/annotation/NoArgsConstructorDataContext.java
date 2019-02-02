package me.philcali.zero.lombok.processor.context.annotation;

import java.util.Objects;

import com.google.auto.service.AutoService;

import me.philcali.zero.lombok.annotation.NoArgsConstructor;
import me.philcali.zero.lombok.processor.context.DataContext;
import me.philcali.zero.lombok.processor.context.ProcessorContext;

@AutoService(DataContext.class)
public class NoArgsConstructorDataContext implements ConstructorDataContext {

    @Override
    public boolean isApplicable(final ProcessorContext context) {
        return Objects.isNull(context.getParentContext())
                && Objects.nonNull(context.getElement().getAnnotation(NoArgsConstructor.class));
    }
}
