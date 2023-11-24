package net.bypiramid.commandmanager.common.parameter;

import net.bypiramid.commandmanager.common.parameter.impl.BooleanAdapter;
import net.bypiramid.commandmanager.common.parameter.impl.DoubleAdapter;
import net.bypiramid.commandmanager.common.parameter.impl.IntegerAdapter;
import net.bypiramid.commandmanager.common.parameter.impl.LongAdapter;

import java.util.HashMap;

public class AdapterMap extends HashMap<Class<?>, Adapter<?>> {

    public AdapterMap(boolean registerDefaults) {
        if (!registerDefaults) {
            return;
        }

        put(int.class, new IntegerAdapter());
        put(long.class, new LongAdapter());
        put(double.class, new DoubleAdapter());
        put(boolean.class, new BooleanAdapter());

        put(Integer.class, new IntegerAdapter());
        put(Long.class, new LongAdapter());
        put(Double.class, new DoubleAdapter());
        put(Boolean.class, new BooleanAdapter());
    }

    @SuppressWarnings("unchecked")
    public <T> Adapter<T> registerAdapter(Adapter<T> value) {
        return (Adapter<T>) super.put(value.getType(), value);
    }
}
