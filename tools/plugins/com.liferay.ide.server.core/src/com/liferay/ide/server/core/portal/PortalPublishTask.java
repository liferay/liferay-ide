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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.PublishTaskDelegate;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Gregory Amerson
 */
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

        if( !CoreUtil.isNullOrEmpty( modules ) )
        {
            final int size = modules.size();

            for( int i = 0; i < size; i++ )
            {
                IModule[] module = (IModule[]) modules.get( i );
                Integer deltaKind = (Integer) kindList.get( i );

                switch( kind )
                {
                    case IServer.PUBLISH_FULL:
                    case IServer.PUBLISH_INCREMENTAL:
                    case IServer.PUBLISH_AUTO:
                        switch( deltaKind )
                        {
                            case ServerBehaviourDelegate.ADDED:
                            case ServerBehaviourDelegate.CHANGED:
                                addOperation( BundlePublishFullAdd.class, tasks, server, module );
                                break;

                            case ServerBehaviourDelegate.REMOVED:
                                addOperation( BundlePublishFullRemove.class, tasks, server, module );
                                break;

                            case ServerBehaviourDelegate.NO_CHANGE:
                                //TODO need to checkt to see if the latest jar is actually the one deployed
                                addOperation( BundlePublishFullAdd.class, tasks, server, module );
                                break;

                            default:
                                System.out.println( "Unhandled deltaKind " + deltaKind );
                                break;
                        }
                        break;

                    default:
                        System.out.println( "Unhandled kind " + kind );
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
}
