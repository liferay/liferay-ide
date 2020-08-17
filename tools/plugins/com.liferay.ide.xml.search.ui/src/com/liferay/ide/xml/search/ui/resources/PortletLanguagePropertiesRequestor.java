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

package com.liferay.ide.xml.search.ui.resources;

import com.liferay.ide.core.util.PropertiesUtil;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.properties.DefaultPropertiesRequestor;
import org.eclipse.wst.xml.search.core.properties.IPropertiesRequestor;

/**
 * @author Terry Jia
 */
public class PortletLanguagePropertiesRequestor extends DefaultPropertiesRequestor {

	public static IPropertiesRequestor instance = new PortletLanguagePropertiesRequestor();

	protected boolean accept(IFile file, IResource rootResource) {
		List<IFile> defaultPortletLanguagePropertiesFiles = PropertiesUtil.getDefaultLanguagePropertiesFromProject(
			rootResource.getProject());

		for (IFile defaultPortletLanguagePropertiesFile : defaultPortletLanguagePropertiesFiles) {
			if (defaultPortletLanguagePropertiesFile.equals(file)) {
				return true;
			}
		}

		return false;
	}

}