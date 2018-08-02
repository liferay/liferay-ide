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

/**
 * @author Gregory Amerson
 */
public class AntPropertyCopy extends AbstractPropertySetterTask {

	public AntPropertyCopy() {
		_from = null;
		_silent = false;
	}

	public void execute() throws BuildException {
		validate();

		String value = getProject().getProperty(_from);

		if ((value == null) && !_silent) {
			throw new BuildException("Property '" + _from + "' is not defined.");
		}

		if (value != null) {
			setPropertyValue(value);
		}
	}

	public void setFrom(String from) {
		_from = from;
	}

	public void setName(String name) {
		setProperty(name);
	}

	public void setSilent(boolean silent) {
		_silent = silent;
	}

	protected void validate() {
		super.validate();

		if (_from == null) {
			throw new BuildException("Missing the 'from' attribute");
		}
	}

	private String _from;
	private boolean _silent;

}