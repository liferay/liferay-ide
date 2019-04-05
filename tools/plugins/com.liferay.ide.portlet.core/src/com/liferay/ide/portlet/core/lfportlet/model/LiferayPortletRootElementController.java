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

package com.liferay.ide.portlet.core.lfportlet.model;

import com.liferay.ide.project.core.util.VersionedDTDRootElementController;

import java.util.regex.Pattern;

import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Simon Jiang
 */
public class LiferayPortletRootElementController extends VersionedDTDRootElementController {

	public static final String PUBLIC_ID_TEMPLATE = "-//Liferay//DTD Portlet Application {0}//EN";

	public static final String SYSTEM_ID_TEMPLATE = "http://www.liferay.com/dtd/liferay-portlet-app_{0}.dtd";

	public static final String XML_BINDING_PATH;

	public static final Pattern publicIdPattern = Pattern.compile("^-//Liferay//DTD Portlet Application (.*)//EN$");
	public static final Pattern systemIdPattern = Pattern.compile(
		"^http://www.liferay.com/dtd/liferay-portlet-app_(.*).dtd$");

	static {
		XmlBinding xmlBinding = LiferayPortletXml.class.getAnnotation(XmlBinding.class);

		XML_BINDING_PATH = xmlBinding.path();
	}

	public LiferayPortletRootElementController() {
		super(XML_BINDING_PATH, PUBLIC_ID_TEMPLATE, SYSTEM_ID_TEMPLATE, publicIdPattern, systemIdPattern);
	}

}