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

package com.liferay.ide.project.core.upgrade;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Simon Jiang
 */
public class BreakingChangeSimpleProject {

	@JsonCreator
	public BreakingChangeSimpleProject() {
	}

	public BreakingChangeSimpleProject(String name, String location) {
		_name = name;
		_location = location;
	}

	@JsonProperty("location")
	public String getLocation() {
		return _location;
	}

	@JsonProperty("name")
	public String getName() {
		return _name;
	}

	public void setLocation(String location) {
		_location = location;
	}

	public void setName(String name) {
		_name = name;
	}

	private String _location;
	private String _name;

}