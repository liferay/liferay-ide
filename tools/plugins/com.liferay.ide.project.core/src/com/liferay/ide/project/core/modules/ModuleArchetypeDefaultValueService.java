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


public class ModuleArchetypeDefaultValueService extends DefaultValueService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initDefaultValueService()
    {
        this.listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewLiferayModuleProjectOp.PROP_PROJECT_TEMPLATE_NAME ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        String templateName = op().getProjectTemplateName().content();

        return op().getProjectProvider().content().getData( "archetypeGAV", String.class, templateName ).get( 0 );
    }

    @Override
    public void dispose()
    {
        op().property( NewLiferayModuleProjectOp.PROP_PROJECT_TEMPLATE_NAME ).detach( this.listener );
        super.dispose();
    }

    private NewLiferayModuleProjectOp op()
    {
        return context( NewLiferayModuleProjectOp.class );
    }
}
