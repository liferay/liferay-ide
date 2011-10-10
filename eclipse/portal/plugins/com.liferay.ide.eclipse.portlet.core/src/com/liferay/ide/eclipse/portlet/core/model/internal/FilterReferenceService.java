/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt Ltd., All rights reserved.
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

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import com.liferay.ide.eclipse.portlet.core.model.IFilter;
import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;

import org.eclipse.sapphire.services.ReferenceService;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */

public final class FilterReferenceService extends ReferenceService {

	@Override
	public Object resolve( final String reference ) {
		final IPortletApp portletApp = context( IPortletApp.class );

		if ( portletApp != null ) {
			for ( IFilter iFilter : portletApp.getFilters() ) {
				if ( equal( iFilter.getName().getContent(), reference ) ) {
					return iFilter;
				}
			}
		}

		return null;
	}

}
