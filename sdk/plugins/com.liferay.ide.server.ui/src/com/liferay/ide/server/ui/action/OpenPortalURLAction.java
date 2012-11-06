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

import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.ui.LiferayServerUIPlugin;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public abstract class OpenPortalURLAction extends AbstractServerRunningAction
{

    public OpenPortalURLAction()
    {
        super();
    }

    protected ILiferayServer getLiferayServer()
    {
        return (ILiferayServer) selectedServer.loadAdapter( ILiferayServer.class, null );
    }

    protected abstract URL getPortalURL();

    protected abstract String getPortalURLTitle();

    @Override
    protected int getRequiredServerState()
    {
        return IServer.STATE_STARTED;
    }

    protected void openBrowser( final URL url, final String browserTitle )
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                try
                {
                    IWorkbenchBrowserSupport browserSupport =
                        ServerUIPlugin.getInstance().getWorkbench().getBrowserSupport();

                    IWebBrowser browser =
                        browserSupport.createBrowser( IWorkbenchBrowserSupport.LOCATION_BAR |
                            IWorkbenchBrowserSupport.NAVIGATION_BAR, null, browserTitle, null );

                    browser.openURL( url );
                }
                catch( Exception e )
                {
                    LiferayServerUIPlugin.logError( e );
                }
            }
        } );
    }

    protected void openPortalURL( ILiferayServer portalServer )
    {
        URL portalUrl = getPortalURL();

        if( portalUrl == null )
        {
            MessageDialog.openError(
                getActiveShell(), "Open Portal URL",
                "Could not determine portal URL. Please make sure the server is properly configured." );
            return;
        }

        openBrowser( portalUrl, getPortalURLTitle() );
    }

    public void run( IAction action )
    {
        if( selectedServer != null )
        {
            final ILiferayServer portalServer = getLiferayServer();

            new Job( "Open portal url" )
            {
                @Override
                protected IStatus run( IProgressMonitor monitor )
                {
                    openPortalURL( portalServer );
                    return Status.OK_STATUS;
                }

            }.schedule();
        }
    }

}
