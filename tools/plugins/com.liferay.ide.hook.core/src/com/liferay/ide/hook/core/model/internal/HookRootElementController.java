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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.hook.core.model.Hook6xx;
import com.liferay.ide.project.core.util.VersionedDTDRootElementController;

import java.util.regex.Pattern;

import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
public class HookRootElementController extends VersionedDTDRootElementController {

	public HookRootElementController() {
		super(_xmlBindingPath, _publicIdTemplate, _systemIdTemplate, _publicIdPattern, _systemIdPattern);
	}

	private static final Pattern _publicIdPattern = Pattern.compile("^-//Liferay//DTD Hook (.*)//EN$");
	private static final String _publicIdTemplate = "-//Liferay//DTD Hook {0}//EN";
	private static final Pattern _systemIdPattern = Pattern.compile(
		"^http://www.liferay.com/dtd/liferay-hook_(.*).dtd$");
	private static final String _systemIdTemplate = "http://www.liferay.com/dtd/liferay-hook_{0}.dtd";
	private static final String _xmlBindingPath = Hook6xx.class.getAnnotation(XmlBinding.class).path();

}