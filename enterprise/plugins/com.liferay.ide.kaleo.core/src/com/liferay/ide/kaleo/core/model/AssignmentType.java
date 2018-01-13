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

package com.liferay.ide.kaleo.core.model;

import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
public enum AssignmentType {

	@Label(standard = "assign to asset creator")
	CREATOR,

	@Label(standard = "assign to several role types")
	ROLE_TYPE,

	@Label(standard = "assign to a specific role by id")
	ROLE,

	@Label(standard = "write a script to determine assignments")
	SCRIPTED_ASSIGNMENT,

	@Label(standard = "assign to a specific user")
	USER,

	@Label(standard = "specify resource actions")
	RESOURCE_ACTIONS

}