package com.mykdes.loganalizer;

/**
 *
 * @author mmykhaylov 
 * the ParamNames is defined to simplify adding new command line
 * parameters if needed in the future
 */
public enum ParamNames {
    DIRECTORY_PARAM("-d"),
    MINUTES_OFFSET_PARAM("-m");
    private final String paramName;

    private ParamNames(String paramName) {
        this.paramName = paramName;
    }

    public static ParamNames fromString(String paramName) {
        if (paramName == null || paramName.isEmpty()) {
            return null;
        }
        for (final ParamNames param : ParamNames.values()) {
            if (param.getParamName().equals(paramName)) {
                return param;
            }
        }
        return null;
    }

    public String getParamName() {
        return paramName;
    }
}
