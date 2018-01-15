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

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.portlet.core.model.CustomPortletMode;
import com.liferay.ide.portlet.core.model.PortletApp;

import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Kamesh Sampath
 */
public class PortletModePossibleValueService extends PossibleValuesService {

	// provided by Portlet Specification and Liferay

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.
	 *      util.SortedSet)
	 */
	@Override
	protected void compute(Set<String> values) {
		PortletApp portletApp = context(PortletApp.class);

		for (int i = 0; i < DEFAULT_MODES.length; i++) {
			values.add(DEFAULT_MODES[i]);
		}

		// Add the ones defined in portlet.xml

		List<CustomPortletMode> customPortletModes = portletApp.getCustomPortletModes();

		for (CustomPortletMode iCustomPortletMode : customPortletModes) {
			String customPortletMode = iCustomPortletMode.getPortletMode().text(false);

			if (customPortletMode != null) {
				values.add(customPortletMode);
			}
		}
	}

	private static final String[] DEFAULT_MODES =
		{"view", "edit", "help", "about", "config", "edit_defaults", "edit_guest", "preview", "print"};

}