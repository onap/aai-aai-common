package org.onap.aai.util;

import com.google.common.collect.Multimap;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtils {
    private CollectionUtils() {
    }
    
    public static <K, V, V0> Function<Multimap<K, V>, Set<V0>> collectValues(Function<V, V0> f) {
        return as -> as.values().stream().map(f).collect(Collectors.toSet());
    }
}
