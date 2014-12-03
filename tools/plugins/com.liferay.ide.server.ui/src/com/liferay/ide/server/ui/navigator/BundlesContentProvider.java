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
package com.liferay.ide.server.ui.navigator;

import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.OsgiBundle;
import com.liferay.ide.server.core.portal.OsgiConnection;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;
import org.eclipse.wst.server.core.IServer;


/**
 * @author Gregory Amerson
 */
public class BundlesContentProvider extends AbstractNavigatorContentProvider
{
    private final Map<String, BundlesFolder> bundlesFolders = new HashMap<String, BundlesFolder>();

    private final Map<String, IStatus> apiChecks = new HashMap<String, IStatus>();

    public BundlesContentProvider()
    {
        super();
    }

    public Object[] getChildren( Object parent )
    {
        Object[] retval = null;

        if( parent instanceof BundlesFolder )
        {
            final BundlesFolder folder = (BundlesFolder) parent;
            retval = folder.getBundles();
        }

        return retval;
    }

    public Object getParent( Object element )
    {
        return null;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public void getPipelinedChildren( final Object parent, final Set currentChildren )
    {
        if( parent instanceof IServer )
        {
            final IServer server = (IServer) parent;

            if( server.getServerState() == IServer.STATE_STARTED )
            {
                final BundlesFolder bundlesFolder = this.bundlesFolders.get( server.getId() );

                if( bundlesFolder == null )
                {
                    final IStatus apiCheck = this.apiChecks.get( server.getId() );

                    if( apiCheck == null )
                    {
                        final Job check = new Job( "Checking JMX API for osgi.core" )
                        {
                            @Override
                            protected IStatus run( IProgressMonitor monitor )
                            {
                                return checkApi( server, monitor );
                            }
                        };

                        check.schedule();
                    }
                    else
                    {
                        if( apiCheck.isOK() )
                        {
                            insertBundlesFolder( server, currentChildren );
                            this.apiChecks.remove( server.getId() );
                        }
                        else
                        {
                            LiferayServerUI.logInfo( "JMX osgi.core API not available", apiCheck );
                        }
                    }
                }
                else if( ! currentChildren.contains( bundlesFolder ) )
                {
                    currentChildren.add( bundlesFolder );
                    //bundlesFolder.getBundles(); // make sure bundles are cached
                }
            }
            else
            {
                this.bundlesFolders.remove( server.getId() );
                this.apiChecks.remove( server.getId() );
            }
        }
    };

    @Override
    public Object getPipelinedParent( Object element, Object suggestedParent )
    {
        Object retval = null;

        if( element instanceof BundlesFolder )
        {
            final BundlesFolder bundlesFolder = (BundlesFolder) element;
            retval = bundlesFolder.getParent();
        }
        else if( element instanceof OsgiBundle )
        {
            retval = null;
        }

        return retval;
    }

    @Override
    public boolean hasChildren( Object element )
    {
        boolean retval = false;

        if( element instanceof IServer )
        {
            final IServer server = (IServer) element;

            final IStatus check = this.apiChecks.get( server.getId() );

            if( check != null && check.isOK() )
            {
                retval = true;
            }

            if( ServerUtil.isLiferayRuntime( server ) && server.getServerState() == IServer.STATE_STARTED )
            {
                retval = true;
            }
        }
        else if( element instanceof BundlesFolder )
        {
            retval = true;
        }

        return retval;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    private void insertBundlesFolder( final IServer server, final Set currentChildren )
    {
        final BundlesFolder folder = new BundlesFolder( this.getConfig(), server );

        this.bundlesFolders.put( server.getId(), folder );

        currentChildren.add( folder );
    }

    private IStatus checkApi( IServer server, IProgressMonitor monitor )
    {
        IStatus retval = null;

        try
        {
            OsgiConnection osgi = LiferayServerCore.newOsgiConnection( server );

            osgi.ping();

            this.apiChecks.put( server.getId(), Status.OK_STATUS );

            NavigatorUIUtil.refreshUI( getConfig().getService(), server );

            retval = Status.OK_STATUS;
        }
        catch (Exception e)
        {
            retval = LiferayServerUI.createErrorStatus( "Unable to get jmx osgi.core api", e );
            this.apiChecks.put( server.getId(), retval );
        }

        return retval;
    }

    @Override
    public boolean interceptRefresh( PipelinedViewerUpdate viewerUpdate )
    {
        for( Object refreshTarget : viewerUpdate.getRefreshTargets() )
        {
            if( refreshTarget instanceof IServer )
            {
                clearStatuses((IServer) refreshTarget);
            }
        }

        return false;
    }

    private void clearStatuses( IServer server )
    {
        if( ServerUtil.isLiferayRuntime( server ) )
        {
            if( server.getServerState() != IServer.STATE_STARTED )
            {
                this.apiChecks.remove( server.getId() );
            }
        }
    }

    public boolean interceptUpdate( PipelinedViewerUpdate viewerUpdate )
    {
        for( Object updateTarget : viewerUpdate.getRefreshTargets() )
        {
            if( updateTarget instanceof IServer )
            {
                clearStatuses( (IServer) updateTarget );
            }
        }

        return false;
    }

    public void dispose()
    {
    }

    public void aboutToRefresh( IServer server )
    {
        this.bundlesFolders.remove( server.getId() );
    }

}
