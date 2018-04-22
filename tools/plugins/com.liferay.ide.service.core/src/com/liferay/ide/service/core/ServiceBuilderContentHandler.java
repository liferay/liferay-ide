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

package com.liferay.ide.service.core;

import com.liferay.ide.core.AbstractDefaultHandler;

/**
 * @author Gregory Amerson
 */
public class ServiceBuilderContentHandler extends AbstractDefaultHandler {

	public static final String PUBLIC_ID_PREFIX = "-//Liferay//DTD Service Builder ";

	public static final String PUBLIC_ID_SUFFIX = "//EN";

	public static final String SERVICE_BUILDER = "service-builder";

	public static final String SYSTEM_ID_PREFIX = "http://www.liferay.com/dtd/liferay-service-builder_";

	public static final String SYSTEM_ID_SUFFIX = ".dtd";

	public ServiceBuilderContentHandler() {
		super(PUBLIC_ID_PREFIX, PUBLIC_ID_SUFFIX, SYSTEM_ID_PREFIX, SYSTEM_ID_SUFFIX, SERVICE_BUILDER);
	}

}