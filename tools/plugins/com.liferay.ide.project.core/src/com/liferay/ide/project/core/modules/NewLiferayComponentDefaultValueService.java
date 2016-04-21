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

import org.apache.commons.lang.WordUtils;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentDefaultValueService extends DefaultValueService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initDefaultValueService()
    {
        super.initDefaultValueService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewLiferayComponentOp.PROP_PROJECT_NAME ).attach( this.listener );
        op().property( NewLiferayComponentOp.PROP_COMPONENT_CLASS_TEMPLATE_NAME ).attach( this.listener );
    }

    
    @Override
    protected String compute()
    {
        String retVal = "";

        final String projectName = op().getProjectName().content( true );

        if( projectName == null )
        {
            return retVal;
        }

        IComponentTemplate<NewLiferayComponentOp> componentTemplate =
            op().getComponentClassTemplateName().content( true );

        if( componentTemplate != null )
        {
            final String projectTemplate = op().getComponentClassTemplateName().content( true ).getShortName();

            final char[] tokens = new char[] { '-', '.', '_' };

            String finalProjectName = WordUtils.capitalizeFully( projectName, tokens );

            for( char token : tokens )
            {
                finalProjectName = finalProjectName.replaceAll( "\\" + token, "" );
            }

            final StringBuffer componentNameBuffer = new StringBuffer( finalProjectName );

            componentNameBuffer.append( projectTemplate );

            return componentNameBuffer.toString();
        }
        return null;
    }

    private NewLiferayComponentOp op()
    {
        return context( NewLiferayComponentOp.class );
    }

    @Override
    public void dispose()
    {
        if( op() != null )
        {
            op().property( NewLiferayComponentOp.PROP_COMPONENT_CLASS_TEMPLATE_NAME ).detach( this.listener );
            op().property( NewLiferayComponentOp.PROP_PROJECT_NAME ).detach( this.listener );
        }

        super.dispose();
    }
}
