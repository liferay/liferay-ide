/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt. Ltd., All rights reserved.
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
 *
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model.internal;

import java.util.List;
import java.util.SortedSet;

import org.eclipse.sapphire.modeling.PossibleValuesService;

import com.liferay.ide.eclipse.portlet.core.model.ICustomPortletMode;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class PortletModePossibleValueService extends PossibleValuesService {

	// provided by Portlet Specification and Liferay
	private static final String[] DEFAULT_MODES = { "VIEW", "EDIT", "HELP", "CONFIG" };

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.util.SortedSet)
	 */
	@Override
	protected void fillPossibleValues( SortedSet<String> values ) {
		IPortletApp portletApp = nearest( IPortletApp.class );
		for ( int i = 0; i < DEFAULT_MODES.length; i++ ) {
			values.add( DEFAULT_MODES[i] );
		}

		// Add the ones defined in portlet.xml
		List<ICustomPortletMode> customPortletModes = portletApp.getCustomPortletModes();
		for ( ICustomPortletMode iCustomPortletMode : customPortletModes ) {
			String customPortletMode = iCustomPortletMode.getPortletMode().getText( false );
			if ( customPortletMode != null ) {
				values.add( customPortletMode );
			}

		}

	}
}
