package com.liferay.ide.sdk.core;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;

public abstract class AbstractPropertySetterTask extends Task {
    private boolean override;
    private String property;

    public void setOverride(boolean override) {
        this.override = override;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    protected void validate() {
        if (this.property == null)
            throw new BuildException("You must specify a property to set.");
    }

    @SuppressWarnings( "deprecation" )
    protected final void setPropertyValue(String value) {
        if (value == null)
            return;
        if (this.override) {
            if (getProject().getUserProperty(this.property) == null)
                getProject().setProperty(this.property, value);
            else
                getProject().setUserProperty(this.property, value);
        } else {
            Property p = (Property) this.project.createTask("property");
            p.setName(this.property);
            p.setValue(value);
            p.execute();
        }
    }
}