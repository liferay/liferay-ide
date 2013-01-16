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
 *******************************************************************************/

package com.liferay.ide.server.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayRuntime;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
public class OpenJSONWSAPIAction extends OpenPortalURLAction
{

    public OpenJSONWSAPIAction()
    {
        super();
    }

    protected URL getPortalURL()
    {
        try
        {
            return new URL( getLiferayServer().getPortalHomeUrl(), "/api/jsonws" ); //$NON-NLS-1$
        }
        catch( MalformedURLException e )
        {
        }

        return null;
    }

    @Override
    protected String getPortalURLTitle()
    {
        return Msgs.jsonWsApi;
    }

    @Override
    public void selectionChanged( IAction action, ISelection selection )
    {
        super.selectionChanged( action, selection );

        if( action.isEnabled() )
        {
            if( getLiferayServer() instanceof ServerDelegate )
            {
                IRuntime runtime = ( (ServerDelegate) getLiferayServer() ).getServer().getRuntime();

                if( runtime != null )
                {
                    ILiferayRuntime liferayRuntime =
                        (ILiferayRuntime) runtime.loadAdapter( ILiferayRuntime.class, null );

                    if( liferayRuntime != null )
                    {
                        Version v = new Version( liferayRuntime.getPortalVersion() );

                        if( CoreUtil.compareVersions( v, new Version( "6.1.0" ) ) < 0 ) //$NON-NLS-1$
                        {
                            action.setEnabled( false );
                        }
                    }
                }
            }
        }
    }

    private static class Msgs extends NLS
    {
        public static String jsonWsApi;

        static
        {
            initializeMessages( OpenJSONWSAPIAction.class.getName(), Msgs.class );
        }
    }
}
