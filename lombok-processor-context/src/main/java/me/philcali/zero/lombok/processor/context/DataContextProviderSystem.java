package me.philcali.zero.lombok.processor.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import me.philcali.zero.lombok.processor.context.annotation.GroupedDataContext;

public class DataContextProviderSystem implements DataContextProvider {
    private final ClassLoader loader;

    public DataContextProviderSystem(final ClassLoader loader) {
        this.loader = loader;
    }

    public DataContextProviderSystem() {
        this(ClassLoader.getSystemClassLoader());
    }

    @Override
    public List<DataContext> get(final ProcessorContext context) {
        final Map<String, GroupedDataContext> groupedContexts = new HashMap<>();
        StreamSupport.stream(ServiceLoader.load(DataContext.class, loader).spliterator(), false)
        .filter(data -> data.isApplicable(context))
        .forEach(dataContext -> {
            groupedContexts.compute(dataContext.getName(), (key, groupedContext) -> {
                if (Objects.isNull(groupedContext)) {
                    return new GroupedDataContext(dataContext);
                }
                return groupedContext.addContext(dataContext);
            });
        });
        return Collections.unmodifiableList(new ArrayList<>(groupedContexts.values()));
    }
}
