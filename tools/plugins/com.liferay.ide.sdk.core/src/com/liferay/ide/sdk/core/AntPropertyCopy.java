package com.liferay.ide.sdk.core;

import org.apache.tools.ant.BuildException;

public class AntPropertyCopy extends AbstractPropertySetterTask {
    private String from;
    private boolean silent;

    public AntPropertyCopy() {
        this.from = null;
        this.silent = false;
    }

    public void setName(String name) {
        setProperty(name);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    protected void validate() {
        super.validate();
        if (this.from == null)
            throw new BuildException("Missing the 'from' attribute.");
    }

    public void execute() throws BuildException {
        validate();

        String value = getProject().getProperty(this.from);

        if ((value == null) && (!(this.silent))) {
            throw new BuildException("Property '" + this.from
                    + "' is not defined.");
        }
        if (value != null)
            setPropertyValue(value);
    }
}