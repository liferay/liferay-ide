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

package com.liferay.ide.xml.search.ui.searcher;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.xml.search.ui.PortalLanguagePropertiesCacheUtil;

import java.io.InputStream;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceTo;
import org.eclipse.wst.xml.search.editor.references.IXMLReferenceToProperty;
import org.eclipse.wst.xml.search.editor.searchers.properties.XMLSearcherForProperties;

/**
 * @author Terry Jia
 */
public class LiferayJspLanguagePropertiesSearcher extends XMLSearcherForProperties {

	public String searchForTextHover(
		Object selectedNode, int offset, String mathingString, int startIndex, int endIndex, IFile file,
		IXMLReferenceTo referenceTo) {

		StringBuffer sb = new StringBuffer();

		if (referenceTo instanceof IXMLReferenceToProperty) {
			IProject project = file.getProject();

			List<IFile> languageFiles = PropertiesUtil.getDefaultLanguagePropertiesFromProject(project);

			for (IFile languageFile : languageFiles) {
				Properties properties = new Properties();

				try (InputStream contents = languageFile.getContents();) {
					properties.load(contents);

					Object key = properties.get(mathingString);

					if (key != null) {
						IPath fullPath = languageFile.getFullPath();

						sb.append(NLS.bind(_HOVER, key, fullPath.toString()));

						sb.append("<br/>");
					}
					else {
						continue;
					}
				}
				catch (Exception e) {
				}
			}

			if (CoreUtil.isNullOrEmpty(sb.toString())) {
				Properties portalProperties = PortalLanguagePropertiesCacheUtil.getPortalLanguageProperties(
					LiferayCore.create(project));

				if (portalProperties != null) {
					Object key = portalProperties.get(mathingString);

					if (key != null) {
						sb.append(NLS.bind(_HOVER, key, "Liferay Portal Language.properties"));
					}
				}
			}
		}

		return sb.toString();
	}

	private static final String _HOVER = "\"{0}\" in {1}";

}