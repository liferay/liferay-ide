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

package com.liferay.ide.xml.search.ui.markerResolutions;

import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.server.util.ComponentUtil;
import com.liferay.ide.xml.search.ui.LiferayXMLSearchUI;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;

import org.osgi.framework.Bundle;

/**
 * @author Kuo Zhang
 */
public class DecreaseInstanceScopeXmlValidationLevel implements IMarkerResolution2 {

	public DecreaseInstanceScopeXmlValidationLevel() {
	}

	@Override
	public String getDescription() {
		return _MESSAGE;
	}

	@Override
	public Image getImage() {
		LiferayXMLSearchUI plugin = LiferayXMLSearchUI.getDefault();

		Bundle bundle = plugin.getBundle();

		URL url = bundle.getEntry("/icons/arrow_down.png");

		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);

		return imageDescriptor.createImage();
	}

	@Override
	public String getLabel() {
		return _MESSAGE;
	}

	@Override
	public void run(IMarker marker) {

		// if the project scope is used, set its validation level to "Ignore"
		// first.

		IEclipsePreferences node = new ProjectScope(
			MarkerUtil.getProject(marker)
		).getNode(
			ProjectCore.PLUGIN_ID
		);

		String liferayPluginValidationType = marker.getAttribute(
			XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE, null);

		if (liferayPluginValidationType == null) {
			liferayPluginValidationType = marker.getAttribute(
				XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE_OLD, null);
		}

		if (liferayPluginValidationType != null) {
			if (node.getBoolean(ProjectCore.USE_PROJECT_SETTINGS, false)) {
				ValidationPreferences.setProjectScopeValidationLevel(
					MarkerUtil.getProject(marker), liferayPluginValidationType, -1);
			}

			ValidationPreferences.setInstanceScopeValidationLevel(liferayPluginValidationType, -1);
			ComponentUtil.validateFile((IFile)marker.getResource(), new NullProgressMonitor());
		}
	}

	private static final String _MESSAGE = "Disable this type of validation in all projects";

}