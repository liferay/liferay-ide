/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.sdk.core;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractPropertySetterTask extends Task {

	public void setOverride(boolean override) {
		_override = override;
	}

	public void setProperty(String property) {
		_property = property;
	}

	@SuppressWarnings("deprecation")
	protected void setPropertyValue(String value) {
		if (value == null) {
			return;
		}

		if (_override) {
			if (getProject().getUserProperty(_property) == null) {
				getProject().setProperty(_property, value);
			}
			else {
				getProject().setUserProperty(_property, value);
			}
		}
		else {
			Property p = (Property)project.createTask("property");

			p.setName(_property);
			p.setValue(value);
			p.execute();
		}
	}

	protected void validate() {
		if (_property == null) {
			throw new BuildException("You must specify a property to set.");
		}
	}

	private boolean _override;
	private String _property;

}