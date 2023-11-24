package net.bypiramid.commandmanager.common.parameter.impl;

import net.bypiramid.commandmanager.common.context.Context;
import net.bypiramid.commandmanager.common.parameter.Adapter;

public class IntegerAdapter extends Adapter<Integer> {

    public Integer adapt(Context<?> context, String supplied) {
        try {
            return Integer.parseInt(supplied);
        } catch(Exception ex) {
            return null;
        }
    }
}
