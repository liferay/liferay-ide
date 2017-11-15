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

	static final String xmlBindingPath = LiferayPortletXml.class.getAnnotation(XmlBinding.class).path();
	static final String publicIdTemplate = "-//Liferay//DTD Portlet Application {0}//EN";
	static final String systemIdTemplate = "http://www.liferay.com/dtd/liferay-portlet-app_{0}.dtd";
	static final Pattern publicIdPattern = Pattern.compile("^-//Liferay//DTD Portlet Application (.*)//EN$");
	static final Pattern systemIdPattern = Pattern.compile("^http://www.liferay.com/dtd/liferay-portlet-app_(.*).dtd$");

	public LiferayPortletRootElementController() {
		super(xmlBindingPath, publicIdTemplate, systemIdTemplate, publicIdPattern, systemIdPattern);
	}

}