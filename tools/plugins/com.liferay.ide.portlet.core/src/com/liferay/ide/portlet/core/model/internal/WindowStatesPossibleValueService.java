/*******************************************************************************
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
 *
 * Contributors:
 *    Kamesh Sampath - initial implementation
 *    Greory Amerson - initial implementation review and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.portlet.core.model.CustomWindowState;
import com.liferay.ide.portlet.core.model.PortletApp;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.ElementDisposeEvent;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class WindowStatesPossibleValueService extends PossibleValuesService
{
    // provided by Portlet Specification
    private static final String[] DEFAULT_STATES = { "maximized", "minimized", "normal" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private boolean initialized;
    private boolean readPriorToInit;
    private Set<String> values = Collections.emptySet();

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.util.SortedSet)
     */
    @Override
    protected void compute( final Set<String> values )
    {
        if( ! this.initialized )
        {
            this.readPriorToInit = true;
        }

        values.addAll( this.values );
    }

    @Override
    protected void initPossibleValuesService()
    {
        super.initPossibleValuesService();

        final PortletApp portletApp = context( PortletApp.class );

        final Listener listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refreshValues();
            }
        };

        portletApp.attach( listener, PortletApp.PROP_CUSTOM_WINDOW_STATES.name() );

        refreshValues();

        portletApp.attach
        (
            new FilteredListener<ElementDisposeEvent>()
            {
                @Override
                protected void handleTypedEvent( final ElementDisposeEvent event )
                {
                    portletApp.detach( listener, PortletApp.PROP_CUSTOM_WINDOW_STATES.name() );
                }
            }
        );

        this.initialized = true;
    }

    private void refreshValues()
    {
        final PortletApp portletApp = context( PortletApp.class );

        if( portletApp != null && ! portletApp.disposed() )
        {
            final Set<String> newValues = new TreeSet<String>();

            for( int i = 0; i < DEFAULT_STATES.length; i++ )
            {
                newValues.add( DEFAULT_STATES[i] );
            }

            final List<CustomWindowState> customWindowStates = portletApp.getCustomWindowStates();

            for( CustomWindowState iCustomWindowState : customWindowStates )
            {
                String customWindowState = iCustomWindowState.getWindowState().text( false );

                if( customWindowState != null )
                {
                    newValues.add( customWindowState );
                }
            }

            if( ! this.values.equals( newValues ) )
            {
                this.values = Collections.unmodifiableSet( newValues );
            }

            if( this.initialized || this.readPriorToInit )
            {
                refresh();
            }
        }
    }

}
