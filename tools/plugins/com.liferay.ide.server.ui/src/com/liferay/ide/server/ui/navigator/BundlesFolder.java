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
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.wst.server.core.IServer;


/**
 * @author Gregory Amerson
 */
public class BundlesFolder
{

    private final ICommonContentExtensionSite config;
    private final IServer server;
    private IStatus currentStatus;
    private OsgiBundle[] cachedBundles;
    private OsgiBundle[] loading = new OsgiBundle[] { new OsgiBundleLoading() };
    private Job cacheBundlesJob;

    public BundlesFolder( ICommonContentExtensionSite config, IServer server )
    {
        this.config = config;
        this.server = server;
    }

    public synchronized OsgiBundle[] getBundles()
    {
        if( this.server.getServerState() != IServer.STATE_STARTED )
        {
            return null;
        }

        if( this.cachedBundles == null )
        {
            scheduleCacheBundlesJob();

            return loading;
        }

        return this.cachedBundles;
    }

    public Object getParent()
    {
        return this.server;
    }

    public IStatus getStatus()
    {
        return this.currentStatus;
    }

    private void scheduleCacheBundlesJob()
    {
        if( cacheBundlesJob == null )
        {
            cacheBundlesJob = new Job( "Loading osgi bundles..." )
            {
                @Override
                protected IStatus run( IProgressMonitor monitor )
                {
                    currentStatus = LiferayServerCore.info( "Loading osgi bundles..." );

                    final OsgiConnection osgi = LiferayServerCore.newOsgiConnection( server );

                    List<OsgiBundle> bundlesToShow = new ArrayList<OsgiBundle>();

                    IStatus errorStatus = null;

                    try
                    {
                        OsgiBundle[] bundles = osgi.getBundles();

                        for( OsgiBundle bundle : bundles )
                        {
                            bundlesToShow.add( bundle );
                        }

                        cachedBundles = bundlesToShow.toArray( new OsgiBundle[0] );
                    }
                    catch( Exception e )
                    {
                        errorStatus = LiferayServerUI.createErrorStatus( e );
                    }

                    if( errorStatus != null )
                    {
                        currentStatus = errorStatus;
                    }
                    else
                    {
                        currentStatus = null;
                    }

                    cacheBundlesJob = null;

                    UIUtil.async( new Runnable()
                    {
                        public void run()
                        {
                            final CommonViewer viewer = NavigatorUIUtil.getViewer( config );
                            viewer.refresh( BundlesFolder.this );
                        }
                    });

                    return Status.OK_STATUS;
                }
            };

            cacheBundlesJob.schedule();
        }
    }
}
