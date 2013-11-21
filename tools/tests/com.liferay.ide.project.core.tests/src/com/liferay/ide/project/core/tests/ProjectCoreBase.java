/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;


/**
 * @author Gregory Amerson
 */
public class ProjectCoreBase extends BaseTests
{

    protected IProject createProject( NewLiferayPluginProjectOp op )
    {
        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );

        assertEquals(
            status.toString(),
            Status.createOkStatus().message().toLowerCase(), status.message().toLowerCase() );

        final IProject newLiferayPluginProject = project( op.getFinalProjectName().content() );

        assertNotNull( newLiferayPluginProject );

        assertEquals( true, newLiferayPluginProject.exists() );

        final IFacetedProject facetedProject = ProjectUtil.getFacetedProject( newLiferayPluginProject );

        assertNotNull( facetedProject );

        final IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet( facetedProject );

        assertNotNull( liferayFacet );

        final PluginType pluginTypeValue = op.getPluginType().content( true );

        if( pluginTypeValue.equals( PluginType.servicebuilder ) )
        {
            assertEquals( "liferay.portlet", liferayFacet.getId() );
        }
        else
        {
            assertEquals( "liferay." + pluginTypeValue, liferayFacet.getId() );
        }


        return newLiferayPluginProject;
    }

}
