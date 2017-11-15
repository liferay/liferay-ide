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

import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.core.model.PublicRenderParameter;

import java.util.Set;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Kamesh Sampath
 */
public class PublicRenderParameterValuesService extends PossibleValuesService {

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.
	 *      util.SortedSet)
	 */
	@Override
	protected void compute(Set<String> values) {
		PortletApp portletApp = context(PortletApp.class);

		ElementList<PublicRenderParameter> publicRenderParameters = portletApp.getPublicRenderParameters();

		for (PublicRenderParameter renderParameter : publicRenderParameters) {
			String indentifer = renderParameter.getIdentifier().content();

			values.add(indentifer);
		}
	}

}