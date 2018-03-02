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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.PublishTaskDelegate;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class PortalPublishTask extends PublishTaskDelegate
{

    public PortalPublishTask()
    {
        super();
    }

    private void addOperation(
        Class<? extends BundlePublishOperation> opClass, List<BundlePublishOperation> tasks, IServer server,
        IModule[] module )
    {
        for( BundlePublishOperation task : tasks )
        {
            if( task.getClass().equals( opClass ) )
            {
                task.addModule( module );
                return;
            }
        }

        try
        {
            BundlePublishOperation op =
                opClass.getConstructor( IServer.class, IModule[].class ).newInstance( server, module );
            tasks.add( op );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( "Unable to add bundle operation", e );
        }
    }

    @SuppressWarnings( "rawtypes" )
    public PublishOperation[] getTasks( IServer server, int kind, List modules, List kindList )
    {
        List<BundlePublishOperation> tasks = new ArrayList<BundlePublishOperation>();

        PortalServerBehavior serverBehavior =
            (PortalServerBehavior) server.loadAdapter( PortalServerBehavior.class, null );

        if( ListUtil.isNotEmpty(modules) )
        {
            final int size = modules.size();

            for( int i = 0; i < size; i++ )
            {
                IModule[] module = (IModule[]) modules.get( i );
                Integer deltaKind = (Integer) kindList.get( i );
                boolean needClean = false;
                IModuleResourceDelta[] deltas = ( (Server) server ).getPublishedResourceDelta( module );

                for( IModuleResourceDelta delta : deltas )
                {
                    final IModuleResource resource = delta.getModuleResource();
                    final IFile resourceFile = (IFile) resource.getAdapter( IFile.class );

                    if( resourceFile != null && resourceFile.getName().equals( "bnd.bnd" ) )
                    {
                        needClean = true;
                        break;
                    }
                }

                switch( kind )
                {
                    case IServer.PUBLISH_FULL:
                    case IServer.PUBLISH_INCREMENTAL:
                    case IServer.PUBLISH_AUTO:
                        final IProject project = module[0].getProject();

                        switch( deltaKind )
                        {
                            case ServerBehaviourDelegate.ADDED:
                                addOperation( BundlePublishFullAddCleanBuild.class, tasks, server, module );
                                break;

                            case ServerBehaviourDelegate.CHANGED:
                                if (needClean)
                                {
                                    addOperation( BundlePublishFullAddCleanBuild.class, tasks, server, module );
                                }
                                else
                                {
                                    addOperation( BundlePublishFullAdd.class, tasks, server, module );
                                }

                                break;

                            case ServerBehaviourDelegate.REMOVED:
                                addOperation( BundlePublishFullRemove.class, tasks, server, module );
                                break;

                            case ServerBehaviourDelegate.NO_CHANGE:
                                final IBundleProject bundleProject =
                                    LiferayCore.create( IBundleProject.class, project );

                                if( bundleProject != null )
                                {
                                    try
                                    {
                                        if( isUserRedeploy( serverBehavior, module[0] ) ||
                                           !isDeployed(server,serverBehavior,bundleProject.getSymbolicName()) )
                                        {
                                            addOperation(
                                                BundlePublishFullAddCleanBuild.class, tasks, server, module );
                                        }
                                    }
                                    catch( CoreException e )
                                    {
                                        LiferayServerCore.logError(
                                            "Unable to get bsn for project " + project.getName(), e );
                                    }
                                }

                                break;

                            default:
                                break;
                            }
                        break;

                    default:
                        break;
                }
            }
        }

        return tasks.toArray( new PublishOperation[0] );
    }

    @SuppressWarnings( "rawtypes" )
    @Override
    public PublishOperation[] getTasks( IServer server, List modules )
    {
        return super.getTasks( server, modules );
    }

    private boolean isDeployed(IServer server , PortalServerBehavior serverBehavior , String bsn )
    {
        boolean isDeployed = false;

        if( server.getServerState() == IServer.STATE_STARTED )
        {
            try
            {
                isDeployed = new GogoBundleDeployer().getBundleId( bsn ) > 0;
            }
            catch( Exception e )
            {
            }
        }

        return isDeployed;
    }

    private boolean isUserRedeploy( PortalServerBehavior serverBehavior, IModule module )
    {
        if( serverBehavior.getInfo() != null )
        {
            Object moduleInfo = serverBehavior.getInfo().getAdapter( IModule.class );

            return module.equals( moduleInfo );
        }

        return false;
    }
}
