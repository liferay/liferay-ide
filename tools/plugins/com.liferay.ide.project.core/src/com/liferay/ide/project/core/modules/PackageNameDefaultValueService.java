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
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Andy Wu
 */
public class PackageNameDefaultValueService extends DefaultValueService
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

        op().property( NewLiferayModuleProjectOp.PROP_PROJECT_NAME ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        String retVal = "";

        final String projectName = op().getProjectName().content( true );

        if( projectName != null )
        {
            String projectTemplate = op().getProjectTemplateName().content( true );

            if( projectTemplate != null )
            {
                retVal = projectName.replaceAll( "([-._])+", "." ).toLowerCase();
            }
        }

        return retVal;
    }

    private NewLiferayModuleProjectOp op()
    {
        return context( NewLiferayModuleProjectOp.class );
    }

    @Override
    public void dispose()
    {
        if( op() != null )
        {
            op().property( NewLiferayModuleProjectOp.PROP_PROJECT_NAME ).detach( this.listener );
        }

        super.dispose();
    }
}
