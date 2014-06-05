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
package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.core.resources.IProject;
import org.junit.Test;


/**
 * @author Gregory Amerson
 */
public class VaadinPortletProjectTests extends LiferayMavenProjectTestCase
{

    @Test
    public void testNewVaadin7PortletProject() throws Exception
    {
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "vaadin7" );
        op.setProjectProvider( "maven" );
        op.setPortletFramework( "vaadin" );

        createTestBundleProfile( op );

        final IProject newProject = base.createProject( op );

        assertNotNull( newProject );

        String pomContents = CoreUtil.readStreamToString( newProject.getFile( "pom.xml" ).getContents() );

        assertTrue( pomContents.contains( "<artifactId>vaadin-server</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>vaadin-client</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>portal-service</artifactId>" ) );
    }
}
