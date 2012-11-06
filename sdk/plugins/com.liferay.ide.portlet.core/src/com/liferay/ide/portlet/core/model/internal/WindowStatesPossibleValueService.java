/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.portlet.core.model.CustomWindowState;
import com.liferay.ide.portlet.core.model.PortletApp;

import java.util.List;
import java.util.SortedSet;

import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class WindowStatesPossibleValueService extends PossibleValuesService
{

    // provided by Portlet Specification
    private static final String[] DEFAULT_STATES = { "maximized", "minimized", "normal" };

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.util.SortedSet)
     */
    @Override
    protected void fillPossibleValues( SortedSet<String> values )
    {
        PortletApp portletApp = context( PortletApp.class );

        for( int i = 0; i < DEFAULT_STATES.length; i++ )
        {
            values.add( DEFAULT_STATES[i] );
        }

        // Add the ones defined in portlet.xml
        List<CustomWindowState> customWindowStates = portletApp.getCustomWindowStates();
        for( CustomWindowState iCustomWindowState : customWindowStates )
        {
            String customWindowState = iCustomWindowState.getWindowState().getText( false );
            if( customWindowState != null )
            {
                values.add( customWindowState );
            }
        }

    }

    @Override
    public boolean isCaseSensitive()
    {
        return false;
    }

}
