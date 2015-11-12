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

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.BundleDeployer;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.wst.server.core.IServer;
import org.osgi.framework.dto.BundleDTO;


/**
 * @author Gregory Amerson
 */
public class BundlesFolder
{

    private Job cacheBundlesJob;
    private BundleDTO[] cachedBundles;
    private final ICommonContentExtensionSite config;
    private IStatus currentStatus;
    private BundleDTO[] loading = new BundleDTO[] { new BundleDTOLoading() };
    private final IServer server;

    public BundlesFolder( ICommonContentExtensionSite config, IServer server )
    {
        this.config = config;
        this.server = server;
    }

    protected boolean filter( final BundleDTO bundle )
    {
        return bundle != null;
    }

    public synchronized BundleDTO[] getBundles()
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

    protected boolean isWorkspaceBundle( final BundleDTO bundle )
    {
        final String bsn = bundle.symbolicName;

        if( bsn != null )
        {
            for( final IProject project : CoreUtil.getAllProjects() )
            {
                final IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, project );

                try
                {
                    if( bundleProject != null && bsn.equals( bundleProject.getSymbolicName() ) )
                    {
                        return true;
                    }
                }
                catch( CoreException e )
                {
                }
            }
        }

        return false;
    }

    private void scheduleCacheBundlesJob()
    {
        if( cacheBundlesJob == null )
        {
            cacheBundlesJob = new Job( "Loading OSGi bundles..." )
            {
                @Override
                protected IStatus run( IProgressMonitor monitor )
                {
                    currentStatus = LiferayServerCore.info( "Loading OSGi bundles..." );

                    final BundleDeployer deployer = LiferayServerCore.getBundleDeployer( server );

                    List<BundleDTO> bundlesToShow = new ArrayList<BundleDTO>();

                    IStatus errorStatus = null;

                    try
                    {
                        BundleDTO[] bundles = deployer.listBundles();

                        for( BundleDTO bundle : bundles )
                        {
                            if( filter( bundle ) )
                            {
                                bundlesToShow.add( bundle );
                            }
                        }

                        cachedBundles = bundlesToShow.toArray( new BundleDTO[0] );
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
