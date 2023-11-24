package net.bypiramid.commandmanager.common.parameter.impl;

import net.bypiramid.commandmanager.common.context.Context;
import net.bypiramid.commandmanager.common.parameter.Adapter;

public class DoubleAdapter extends Adapter<Double> {

    public Double adapt(Context<?> context, String supplied) {
        try {
            return Double.parseDouble(supplied);
        } catch(Exception ex) {
            return null;
        }
    }
}
