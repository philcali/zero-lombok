package me.philcali.zero.lombok.processor.context.annotation;

import java.util.ArrayList;
import java.util.List;

import me.philcali.zero.lombok.processor.context.DataContext;
import me.philcali.zero.lombok.processor.context.ProcessorContext;
import me.philcali.zero.lombok.processor.template.TemplateEngine;

public class GroupedDataContext implements DataContext {
    private final DataContext initialContext;
    private final List<DataContext> sameContexts;

    public GroupedDataContext(final DataContext initialContext, final List<DataContext> sameContexts) {
        this.initialContext = initialContext;
        this.sameContexts = sameContexts;
    }

    public GroupedDataContext(final DataContext initialContext) {
        this(initialContext, new ArrayList<>());
    }

    @Override
    public String getName() {
        return initialContext.getName();
    }

    @Override
    public boolean isApplicable(final ProcessorContext context) {
        return initialContext.isApplicable(context);
    }

    @Override
    public Object process(final ProcessorContext context, final TemplateEngine engine) {
        final List<Object> results = new ArrayList<>();
        results.add(initialContext.process(context, engine));
        sameContexts.forEach(dataContext -> {
            results.add(dataContext.process(context, engine));
        });
        return sameContexts.isEmpty() ? results.get(0) : results;
    }

    public GroupedDataContext addContext(final DataContext otherContext) {
        if (otherContext.getName().equals(getName())) {
            this.sameContexts.add(otherContext);
        }
        return this;
    }
}
