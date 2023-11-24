package net.bypiramid.commandmanager.common.parameter;

import net.bypiramid.commandmanager.common.context.Context;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class Adapter<T> {

    private final Class<?> type;

    public Adapter() {
        try {
            Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            this.type = Class.forName(type.getTypeName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getType() {
        return type;
    }

    /**
     * Adapts the object
     */
    public abstract T adapt(Context<?> context, String supplied);

    /**
     * Processes the tab completion
     */
    public List<String> tabComplete(Context context, String supplied) {
        return null;
    }
}
