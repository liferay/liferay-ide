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

package com.liferay.ide.portlet.core.model;

import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Kamesh Sampath
 */
public enum LifeCycleType {

	@Label(standard = "action")
	ACTION_PHASE,
	@Label(standard = "event")
	EVENT_PHASE,
	@Label(standard = "render")
	RENDER_PHASE,
	@Label(standard = "resource")
	RESOURCE_PHASE

}