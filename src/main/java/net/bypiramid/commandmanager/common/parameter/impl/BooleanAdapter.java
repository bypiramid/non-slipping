package net.bypiramid.commandmanager.common.parameter.impl;

import net.bypiramid.commandmanager.common.context.Context;
import net.bypiramid.commandmanager.common.parameter.Adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BooleanAdapter extends Adapter<Boolean> {

    private final Map<String, Boolean> values = new HashMap<>();

    public BooleanAdapter() {
        values.put("true", true);
        values.put("on", true);
        values.put("yes", true);
        values.put("enable", true);

        values.put("false", false);
        values.put("off", false);
        values.put("no", false);
        values.put("disable", false);
    }

    public Boolean adapt(Context<?> context, String supplied) {
        supplied = supplied.toLowerCase();

        if(!values.containsKey(supplied)) {
            return null;
        }

        return values.get(supplied);
    }

    public List<String> tabComplete(Context context, String supplied) {
        return values.keySet().stream().filter(s -> s.toLowerCase().startsWith(supplied.toLowerCase())).collect(Collectors.toList());
    }

}
