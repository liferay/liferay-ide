/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import static org.eclipse.sapphire.modeling.util.MiscUtil.equal;

import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletApp;

import org.eclipse.sapphire.services.ReferenceService;

/**
 * @author Kamesh Sampath
 */
public final class PortletReferenceService extends ReferenceService
{

    private static final String QUERY_BY_NAME = "portlet-name"; //$NON-NLS-1$
    private static final String QUERY_BY_DISPLAY_NAME = "display-name"; //$NON-NLS-1$

    /**
	 *
	 */
    @Override
    public Object resolve( final String reference )
    {
        final PortletApp portletApp = context( PortletApp.class );

        if( portletApp != null )
        {
            for( Portlet portlet : portletApp.getPortlets() )
            {
                if( params() == null || QUERY_BY_NAME.equals( param( "0" ) ) ) //$NON-NLS-1$
                {
                    if( equal( portlet.getPortletName().content(), reference ) )
                    {
                        return portlet;
                    }
                }
                else if( params() == null || QUERY_BY_DISPLAY_NAME.equals( param( "0" ) ) ) //$NON-NLS-1$
                {
                    if( equal( portlet.getPortletName().content(), reference ) )
                    {
                        return portlet;
                    }
                }
            }
        }

        return null;
    }

}
