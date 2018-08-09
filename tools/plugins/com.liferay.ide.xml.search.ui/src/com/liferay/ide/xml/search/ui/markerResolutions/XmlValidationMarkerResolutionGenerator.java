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

import com.liferay.ide.project.core.ValidationPreferences;
import com.liferay.ide.xml.search.ui.LiferayXMLSearchUI;
import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

/**
 * @author Kuo Zhang
 */
public class XmlValidationMarkerResolutionGenerator implements IMarkerResolutionGenerator {

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {

		// for future usage, add resolution of creating properties file(
		// portal.properties, Langauge.properties )

		List<IMarkerResolution> retval = new ArrayList<>();

		try {
			Object attribute = marker.getAttribute(XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE);

			if (attribute == null) {
				attribute = marker.getAttribute(XMLSearchConstants.LIFERAY_PLUGIN_VALIDATION_TYPE_OLD);
			}

			if ((attribute != null) && ValidationPreferences.containsKey(attribute.toString())) {
				retval.add(new DecreaseProjectScopeXmlValidationLevel());
				retval.add(new DecreaseInstanceScopeXmlValidationLevel());
			}
		}
		catch (CoreException ce) {
			LiferayXMLSearchUI.logError("Error getting marker type", ce);
		}

		return retval.toArray(new IMarkerResolution[0]);
	}

}