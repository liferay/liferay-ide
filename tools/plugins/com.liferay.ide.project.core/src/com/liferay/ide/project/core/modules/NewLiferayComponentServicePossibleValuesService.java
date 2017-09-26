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

import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;

import com.liferay.ide.project.core.ProjectCore;

/**
 * @author Simon Jiang
 * @author Lovett Li
 */
public class NewLiferayComponentServicePossibleValuesService extends PossibleValuesService
{

    private Listener listener;

    protected void initPossibleValuesService()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewLiferayComponentOp.PROP_COMPONENT_CLASS_TEMPLATE_NAME ).attach( this.listener );
    }

    @Override
    protected void compute( final Set<String> values )
    {
        final NewLiferayComponentOp op = op();
        final String template = op.getComponentClassTemplateName().content( true ).getShortName();

        if( template.equals( "ServiceHook" ) )
        {
            try
            {
                ServiceContainer serviceWrapperList = new ServiceWrapperCommand().execute();
                values.addAll( serviceWrapperList.getServiceList() );
            }
            catch( Exception e )
            {
                ProjectCore.logError( "Get service wrapper list error.",e );
            }
        }
        else if( template.equals( "service" ) )
        {
            try
            {
                ServiceCommand serviceCommand = new ServiceCommand();

                ServiceContainer allServices = serviceCommand.execute();

                values.addAll( allServices.getServiceList() );
            }
            catch( Exception e )
            {
                ProjectCore.logError( "Get services list error. ", e );
            }
        }
    }

    @Override
    public void dispose()
    {
        if( this.listener != null )
        {
            op().property( NewLiferayComponentOp.PROP_COMPONENT_CLASS_TEMPLATE_NAME ).detach( this.listener );

            this.listener = null;
        }
        super.dispose();
    }

    private NewLiferayComponentOp op()
    {
        return context( NewLiferayComponentOp.class );
    }

    @Override
    public Status problem( final Value<?> value )
    {
        return Status.createOkStatus();
    }
}
