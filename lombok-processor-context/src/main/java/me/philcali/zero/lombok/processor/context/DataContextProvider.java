package me.philcali.zero.lombok.processor.context;

import java.util.List;

public interface DataContextProvider {
    List<DataContext> get(ProcessorContext context);
}
