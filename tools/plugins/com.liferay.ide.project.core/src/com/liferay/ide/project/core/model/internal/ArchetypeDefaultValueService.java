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

import java.util.List;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;


/**
 * @author Simon Jiang
 */
public class ArchetypeDefaultValueService extends DefaultValueService
{
    private static final String LIFERAY_ARCHETYPES_GROUP_ID = "com.liferay.maven.archetypes";

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
        final IPortletFramework portletFramework = op().getPortletFramework().content( true );
        final PluginType pluginType = op().getPluginType().content();
        String archetypeArtifactId = null;
        String archetypeType = null;

        if( pluginType.equals( PluginType.portlet ) )
        {
            archetypeArtifactId = portletFramework.getArchetypeGAV();

            if( archetypeArtifactId == null )
            {
                if( portletFramework.isRequiresAdvanced() )
                {

                    String frameworkName = portletFramework.getShortName();

                    if( portletFramework.isRequiresAdvanced() )
                    {
                        frameworkName = op().getPortletFrameworkAdvanced().content().getShortName();
                    }

                    archetypeType = "portlet-" + frameworkName.replace( "_", "-" );
                }
                else
                {
                    archetypeType = pluginType.name();
                }
            }
        }
        else
        {
            archetypeType = pluginType.name();
        }

        if( archetypeArtifactId == null )
        {
            final String archetypeArtifactIdkey =
                LIFERAY_ARCHETYPES_GROUP_ID + ":liferay-" + archetypeType + "-archetype";
            
            List<String> archetypeValues =
                op().getProjectProvider().content().getData(
                    "archetypeGav", String.class, archetypeArtifactIdkey, "6.2.1" );

            if( !archetypeValues.isEmpty() )
            {
                archetypeArtifactId = archetypeArtifactIdkey + ":" + archetypeValues.get( 0 );
            }
        }

        return archetypeArtifactId;
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
