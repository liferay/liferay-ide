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

    @SuppressWarnings( "rawtypes" )
    @Override
    public PublishOperation[] getTasks( IServer server, List modules )
    {
        return super.getTasks( server, modules );
    }

    @SuppressWarnings( "rawtypes" )
    public PublishOperation[] getTasks( IServer server, int kind, List modules, List kindList )
    {
        List<PublishOperation> tasks = new ArrayList<PublishOperation>();

        if( !CoreUtil.isNullOrEmpty( modules ) )
        {
            final int size = modules.size();

            for( int i = 0; i < size; i++ )
            {
                IModule[] module = (IModule[]) modules.get( i );
                Integer deltaKind = (Integer) kindList.get( i );

                PublishOperation op = null;

                switch( kind )
                {
                    case IServer.PUBLISH_FULL:
                        switch( deltaKind )
                        {
                            case ServerBehaviourDelegate.ADDED:
                            case ServerBehaviourDelegate.CHANGED:
                                op = new BundlePublishFullAdd( server, module );
                                break;

                            case ServerBehaviourDelegate.REMOVED:
                                op = new BundlePublishFullRemove( server, module );
                                break;

                            case ServerBehaviourDelegate.NO_CHANGE:
                                //TODO need to checkt to see if the latest jar is actually the one deployed
                                op = new BundlePublishFullAdd( server, module );
                                break;

                            default:
                                System.out.println( "Unhandled deltaKind " + deltaKind );
                                break;
                        }
                        break;

                    case IServer.PUBLISH_INCREMENTAL:
                        switch( deltaKind )
                        {
                            case ServerBehaviourDelegate.ADDED:
                            case ServerBehaviourDelegate.CHANGED:
                                op = new BundlePublishFullAdd( server, module );
                                break;

                            case ServerBehaviourDelegate.REMOVED:
                                op = new BundlePublishFullRemove( server, module );
                                break;

                            case ServerBehaviourDelegate.NO_CHANGE:
                                //TODO need to checkt to see if the latest jar is actually the one deployed
                                op = new BundlePublishFullAdd( server, module );
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

                if( op != null )
                {
                    tasks.add( op );
                }
            }
        }

        return tasks.toArray( new PublishOperation[0] );
    }
}
