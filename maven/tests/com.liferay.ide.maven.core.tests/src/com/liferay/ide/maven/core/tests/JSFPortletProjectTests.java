/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
public class JSFPortletProjectTests extends LiferayMavenProjectTestCase
{

    private NewLiferayPluginProjectOp newMavenProjectOp( String name, String framework, String advFramework )
    {
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( name );
        op.setProjectProvider( "maven" );
        op.setPortletFramework( framework );
        op.setPortletFrameworkAdvanced( advFramework );

        createTestBundleProfile( op );

        return op;
    }

    @Test
    public void testNewAlloyPortletProject() throws Exception
    {
        NewLiferayPluginProjectOp op = newMavenProjectOp( "liferay_faces_alloy", "jsf-2.x", "liferay_faces_alloy" );

        final IProject newProject = base.createProject( op );

        assertNotNull( newProject );

        String pomContents = CoreUtil.readStreamToString( newProject.getFile( "pom.xml" ).getContents() );

        assertTrue( pomContents.contains( "<artifactId>liferay-faces-alloy</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-faces-bridge-impl</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-faces-portal</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-maven-plugin</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>portal-service</artifactId>" ) );
    }

    @Test
    public void testNewIcefacesPortletProject() throws Exception
    {
        NewLiferayPluginProjectOp op = newMavenProjectOp( "icefaces", "jsf-2.x", "icefaces" );

        final IProject newProject = base.createProject( op );

        assertNotNull( newProject );

        String pomContents = CoreUtil.readStreamToString( newProject.getFile( "pom.xml" ).getContents() );

        assertTrue( pomContents.contains( "<artifactId>icefaces</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-faces-bridge-impl</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-faces-portal</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-maven-plugin</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>portal-service</artifactId>" ) );
    }

    @Test
    public void testNewJSFPortletProject() throws Exception
    {
        NewLiferayPluginProjectOp op = newMavenProjectOp( "jsf-2.x", "jsf-2.x", "jsf" );

        final IProject newProject = base.createProject( op );

        assertNotNull( newProject );

        String pomContents = CoreUtil.readStreamToString( newProject.getFile( "pom.xml" ).getContents() );

        assertTrue( pomContents.contains( "<artifactId>liferay-faces-bridge-impl</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-faces-portal</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-maven-plugin</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>portal-service</artifactId>" ) );
    }

    @Test
    public void testNewPrimefacesPortletProject() throws Exception
    {
        NewLiferayPluginProjectOp op = newMavenProjectOp( "primefaces", "jsf-2.x", "primefaces" );

        final IProject newProject = base.createProject( op );

        assertNotNull( newProject );

        String pomContents = CoreUtil.readStreamToString( newProject.getFile( "pom.xml" ).getContents() );

        assertTrue( pomContents.contains( "<artifactId>primefaces</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-faces-bridge-impl</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-faces-portal</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>liferay-maven-plugin</artifactId>" ) );
        assertTrue( pomContents.contains( "<artifactId>portal-service</artifactId>" ) );
    }

}
