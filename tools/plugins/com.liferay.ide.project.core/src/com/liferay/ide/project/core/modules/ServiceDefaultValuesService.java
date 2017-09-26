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

import org.eclipse.sapphire.DefaultValueService;

import com.liferay.ide.project.core.ProjectCore;

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

        String retVal = "";
        
        if( template.equals( "service-wrapper" ) )
        {

            try
            {
                ServiceContainer serviceWrapperList = new ServiceWrapperCommand().execute();
                retVal = serviceWrapperList.getServiceList().get( 0 );
            }
            catch( Exception e )
            {
                ProjectCore.logError( "Get service wrapper list error.", e );
            }
        }
        else if( template.equals( "service" ) )
        {
            try
            {
                ServiceCommand serviceCommand = new ServiceCommand();

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
