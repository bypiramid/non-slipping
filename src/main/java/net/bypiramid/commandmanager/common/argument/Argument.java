package net.bypiramid.commandmanager.common.argument;

import net.bypiramid.commandmanager.common.CommandFrame;
import net.bypiramid.commandmanager.common.context.Context;
import net.bypiramid.commandmanager.common.parameter.Adapter;
import net.bypiramid.commandmanager.common.parameter.ParamInfo;

import java.util.List;

public class Argument {

    private final CommandFrame commandFrame;
    private final ParamInfo paramInfo;
    private final String supplied;
    private final Context<?> context;

    public Argument(CommandFrame commandFrame, ParamInfo paramInfo, String supplied, Context<?> context) {
        this.commandFrame = commandFrame;
        this.paramInfo = paramInfo;
        this.supplied = supplied;
        this.context = context;
    }

    public CommandFrame getCommandManager() {
        return commandFrame;
    }

    public ParamInfo getParamInfo() {
        return paramInfo;
    }

    public String getSupplied() {
        return supplied;
    }

    public Context<?> getContext() {
        return context;
    }

    /**
     * Processes the param into an object
     *
     * @return Processed Object
     */
    public <T> T get() {
        Class<?> type = paramInfo.getParameter().getType();
        Adapter<?> adapter = commandFrame.getAdapterMap().get(paramInfo.getParameter().getType());

        if (adapter == null) {
            if (paramInfo.getParameter().getType() != String.class) {
                throw new IllegalStateException("No adapter found for type: "
                        + type.toString());
            }

            return supplied.isEmpty() ? null : (T) supplied;
        }

        return (T) adapter.adapt(context, supplied);
    }

    /**
     * Gets the tab completions for the param processor
     *
     * @return Tab Completions
     */
    public List<String> getTabComplete() {
        Class<?> type = paramInfo.getParameter().getType();
        Adapter<?> adapter = commandFrame.getAdapterMap().get(paramInfo.getParameter().getType());

        if (adapter == null) {
            if (type != String.class) {
                throw new IllegalStateException("No adapter found for type: "
                        + type.toString());
            }

            return null;
        }

        return adapter.tabComplete(context, supplied);
    }
}
