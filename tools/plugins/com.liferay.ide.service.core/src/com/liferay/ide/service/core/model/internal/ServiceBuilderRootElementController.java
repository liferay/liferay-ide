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

package com.liferay.ide.service.core.model.internal;

import com.liferay.ide.project.core.util.VersionedDTDRootElementController;
import com.liferay.ide.service.core.model.ServiceBuilder6xx;

import java.util.regex.Pattern;

import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
public class ServiceBuilderRootElementController extends VersionedDTDRootElementController {

	public static final String PUBLIC_IDE_TEMPLATE = "-//Liferay//DTD Service Builder {0}//EN";

	public static final String SYSTEM_ID_TEMPLATE = "http://www.liferay.com/dtd/liferay-service-builder_{0}.dtd";

	public static final String XML_BINDING_PATH;

	public static final Pattern publicIdPattern = Pattern.compile("^-//Liferay//DTD Service Builder (.*)//EN$");
	public static final Pattern systemIdPattern = Pattern.compile(
		"^http://www.liferay.com/dtd/liferay-service-builder_(.*).dtd$");

	static {
		XmlBinding xmlBinding = ServiceBuilder6xx.class.getAnnotation(XmlBinding.class);

		XML_BINDING_PATH = xmlBinding.path();
	}

	public ServiceBuilderRootElementController() {
		super(XML_BINDING_PATH, PUBLIC_IDE_TEMPLATE, SYSTEM_ID_TEMPLATE, publicIdPattern, systemIdPattern);
	}

}