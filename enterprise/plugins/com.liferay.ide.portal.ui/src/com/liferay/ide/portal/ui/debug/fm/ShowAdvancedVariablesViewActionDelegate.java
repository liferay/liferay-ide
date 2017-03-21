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
 *******************************************************************************/

package com.liferay.ide.portal.ui.debug.fm;

import com.liferay.ide.portal.core.PortalCore;
import com.liferay.ide.portal.ui.PortalUI;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Gregory Amerson
 */
public class ShowAdvancedVariablesViewActionDelegate implements IViewActionDelegate, IActionDelegate2
{

    private IAction fAction;
    private IDebugView fView;

    public void dispose()
    {
    }

    public void init( IAction action )
    {
        fAction = action;
        fAction.setChecked( PortalCore.getPrefs().getBoolean( PortalCore.PREF_ADVANCED_VARIABLES_VIEW, false ) );
    }

    public void init( IViewPart view )
    {
        if( view instanceof IDebugView )
        {
            fView = (IDebugView) view;
        }
    }

    public void run( IAction action )
    {
        try
        {
            final IEclipsePreferences prefs = PortalCore.getPrefs();
            prefs.putBoolean( PortalCore.PREF_ADVANCED_VARIABLES_VIEW, action.isChecked() );
            prefs.flush();

            fView.getViewer().refresh();
        }
        catch( Exception e )
        {
            PortalUI.logError( "Unable to change advanced variables view preference", e );
        }
    }

    public void runWithEvent( IAction action, Event event )
    {
        run( action );
    }

    public void selectionChanged( IAction action, ISelection selection )
    {
    }

}
