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
package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;


/**
 * @author Simon Jiang
 */
public class ArchetypeDefaultValueService extends DefaultValueService
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

        op().property( NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK ).attach( this.listener );
        op().property( NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK_ADVANCED ).attach( this.listener );
    }

    @Override
    protected String compute()
    {
        final PluginType pluginType = op().getPluginType().content();

        String frameworkType = null;

        if( pluginType.equals( PluginType.portlet ) )
        {
            final IPortletFramework portletFramework = op().getPortletFramework().content();

            if( portletFramework.isRequiresAdvanced() )
            {
                frameworkType = op().getPortletFrameworkAdvanced().content().getShortName();
            }
            else
            {
                frameworkType = portletFramework.getShortName();
            }
        }
        else
        {
            frameworkType = pluginType.name();
        }

        frameworkType = frameworkType.replaceAll( "_", "-" );

        return op().getProjectProvider().content().getData( "archetypeGAV", String.class, frameworkType ).get( 0 );
    }


    @Override
    public void dispose()
    {
        op().property( NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK ).detach( this.listener );
        op().property( NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK_ADVANCED ).detach( this.listener );
        super.dispose();
    }

    private NewLiferayPluginProjectOp op()
    {
        return context( NewLiferayPluginProjectOp.class );
    }
}
