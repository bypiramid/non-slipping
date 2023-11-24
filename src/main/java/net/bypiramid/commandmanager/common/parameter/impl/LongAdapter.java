package net.bypiramid.commandmanager.common.parameter.impl;

import net.bypiramid.commandmanager.common.context.Context;
import net.bypiramid.commandmanager.common.parameter.Adapter;

public class LongAdapter extends Adapter<Long> {

    public Long adapt(Context<?> context, String supplied) {
        try {
            return Long.parseLong(supplied);
        } catch(Exception ex) {
            return null;
        }
    }
}
