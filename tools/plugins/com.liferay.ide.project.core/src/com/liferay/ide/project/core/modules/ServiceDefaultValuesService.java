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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.portal.PortalServer;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Terry Jia
 */
public class ServiceDefaultValuesService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        final NewLiferayModuleProjectOp op = op();
        final String template = op.getProjectTemplateName().content( true );
        IServer runningServer = null;
        final IServer[] servers = ServerCore.getServers();

        String retVal = "";
        
        if( template.equals( "service-wrapper" ) )
        {
            for( IServer server : servers )
            {
                if( server.getServerType().getId().equals( PortalServer.ID ) )
                {
                    runningServer = server;
                    break;
                }
            }

            try
            {
                ServiceContainer serviceWrapperList = new ServiceWrapperCommand( runningServer ).execute();
                retVal = serviceWrapperList.getServiceList().get( 0 );
            }
            catch( Exception e )
            {
                ProjectCore.logError( "Get service wrapper list error.", e );
            }
        }
        else if( template.equals( "service" ) )
        {
            for( IServer server : servers )
            {
                if( server.getServerState() == IServer.STATE_STARTED &&
                    server.getServerType().getId().equals( PortalServer.ID ) )
                {
                    runningServer = server;
                    break;
                }
            }

            try
            {
                ServiceCommand serviceCommand = new ServiceCommand( runningServer );

                ServiceContainer allServices = serviceCommand.execute();

                retVal =  allServices.getServiceList().get( 0 );
            }
            catch( Exception e )
            {
                ProjectCore.logError( "Get services list error. ", e );
            }
        }

        return retVal;
    }

    private NewLiferayModuleProjectOp op()
    {
        return context( NewLiferayModuleProjectOp.class );
    }

}
