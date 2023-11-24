package net.bypiramid.commandmanager.common.parameter;

import java.lang.reflect.Parameter;

public class ParamInfo {

    private final String name;
    private final boolean concated;
    private final Parameter parameter;

    public ParamInfo(String name, boolean concated, Parameter parameter) {
        this.name = name;
        this.concated = concated;
        this.parameter = parameter;
    }

    public String getName() {
        return name;
    }

    public boolean isConcated() {
        return concated;
    }

    public Parameter getParameter() {
        return parameter;
    }
}
