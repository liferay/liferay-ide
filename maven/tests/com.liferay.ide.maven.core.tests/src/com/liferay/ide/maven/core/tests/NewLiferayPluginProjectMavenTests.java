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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.tests.ProjectCoreBase;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.EnablementService;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.services.ValidationService;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class NewLiferayPluginProjectMavenTests extends ProjectCoreBase
{

    protected IProject createMavenProject( NewLiferayPluginProjectOp op ) throws Exception
    {
        IProject project = createProject( op );

        assertEquals( true, project.getFolder( "src" ).exists() );

        return project;
    }

    protected void createMavenProjectName( final String projectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setProjectProvider( "maven" );

        createMavenProject( op );
    }

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2.0" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append( "liferay-plugins-sdk-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "liferay-plugins-sdk-6.2.0/";
    }

    @Override
    protected IPath getLiferayRuntimeDir()
    {
        return LiferayProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-ga1/tomcat-7.0.42" );
    }

    @Override
    protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-portal-tomcat-6.2.0-ce-ga1-20131101192857659.zip" );
    }

    @Override
    protected String getRuntimeId()
    {
        return "com.liferay.ide.eclipse.server.tomcat.runtime.70";
    }

    @Override
    protected String getRuntimeVersion()
    {
        return "6.2.0";
    }

    @Test
    public void testCreateNewxMavenProject() throws Exception
    {
        createMavenProjectName( "test-name-1" );
        createMavenProjectName( "Test With Spaces" );
        createMavenProjectName( "test_name_1" );
    }

    @Test
    public void testGroupIdValidation() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-group-id-validation" );
        op.setProjectProvider( "maven" );

        final ValidationService vs = op.getGroupId().service( ValidationService.class );

        op.setGroupId( ".com.liferay.test" );
        final String expected = "A package name cannot start or end with a dot";
        assertEquals( expected, vs.validation().message() );
        assertEquals( expected, op.getGroupId().validation().message() );

        op.setGroupId( "com.life*ray.test" );
        final String expected2 = "'life*ray' is not a valid Java identifier";
        assertEquals( expected2, vs.validation().message() );
        assertEquals( expected2, op.getGroupId().validation().message() );
    }

    @Test
    @Ignore
    public void testLocationValidation() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-location-validation-service" );
        op.setProjectProvider( "maven" );
        op.setPluginType( "portlet" );
        op.setUseDefaultLocation( false );

        final ValidationService vs = op.getLocation().service( ValidationService.class );

        String invalidLocation = null;
        invalidLocation = "not-absolute-location";
        op.setLocation( invalidLocation );

        final String expected = "\"" + invalidLocation + "\" is not an absolute path.";

        assertEquals( expected, vs.validation().message() );
        assertEquals( expected, op.getLocation().validation().message() );

        if( Platform.getOS().equals( Platform.OS_WIN32 ) )
        {
            invalidLocation = "Z:\\test-location-validation-service";
        }
        else
        {
            invalidLocation = "/test-location-validation-service";
        }

        op.setLocation( invalidLocation );
        final String expected2 = "Cannot create project content at " + "\"" + invalidLocation + "\"";
        assertEquals( expected2, vs.validation().message() );
        assertEquals( expected2, op.getLocation().validation().message() );

        invalidLocation = CoreUtil.getWorkspaceRoot().getLocation().getDevice() + "\\";
        op.setLocation( invalidLocation );

        final String expected3 = "Project location is not empty or a parent pom.";

        assertEquals( expected3, vs.validation().message() );
        assertEquals( expected3, op.getLocation().validation().message() );

        op.setLocation( "" );

        final String expected4 = "Location must be specified.";
        assertEquals( expected4, vs.validation().message() );
        assertEquals( expected4, op.getLocation().validation().message() );
    }

    @Test
    public void testPortletFrameworkValidation() throws Exception
    {
        NewLiferayPluginProjectOp op = newProjectOp( "test-portlet-framework-validation" );
        op.setPluginType( "portlet" );
        final ValidationService vs = op.getPortletFramework().service( ValidationService.class );

        assertEquals( true, vs.validation().ok() );

        final ILiferayProjectProvider maven = LiferayProjectCore.getProvider( "maven" );
        op.setProjectProvider( maven );
        op.setPortletFramework( "vaadin" );
        assertEquals(
            "Selected portlet framework is not supported with " + maven.getDisplayName(), vs.validation().message() );
        assertEquals(
            "Selected portlet framework is not supported with " + maven.getDisplayName(),
            op.getPortletFramework().validation().message() );

        final SDK newSDK = createNewSDK();
        newSDK.setVersion( "6.0.0" );

        final IPortletFramework jsf = LiferayProjectCore.getPortletFramework( "jsf" );

        op.setProjectProvider( "ant" );
        op.setPortletFramework( "jsf" );
        op.setPluginsSDKName( newSDK.getName() );

        assertEquals(
            "Selected portlet framework requires SDK version at least " + jsf.getRequiredSDKVersion(),
            vs.validation().message() );
        // Value is not excepted.
        /*
         * assertEquals( "Selected portlet framework requires SDK version at least " + jsf.getRequiredSDKVersion(),
         * op.getPortletFramework().validation().message() );
         */
    }

    @Test
    public void testProjectNameListener() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp();
        final SDK sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );

        final String projectName = "test-project-name-listener";
        final String projectName2 = "test-project-name-listener-2";

        op.setProjectProvider( "ant" );
        op.setUseDefaultLocation( true );
        op.setPluginType( "portlet" );

        IPath exceptedLocation = null;

        op.setProjectName( projectName );
        exceptedLocation = sdk.getLocation().append( "portlets" ).append( projectName + "-portlet" );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );

        op.setProjectName( projectName2 );
        exceptedLocation = sdk.getLocation().append( "portlets" ).append( projectName2 + "-portlet" );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );

        op.setProjectProvider( "maven" );

        op.setProjectName( projectName );
        exceptedLocation = CoreUtil.getWorkspaceRoot().getLocation().append( projectName );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );

        op.setProjectName( projectName2 );
        exceptedLocation = CoreUtil.getWorkspaceRoot().getLocation().append( projectName2 );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );
    }

    @Test
    public void testProjectProviderListener() throws Exception
    {
        final String projectName = "test-project-provider-listener";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setPluginType( "portlet" );
        op.setUseDefaultLocation( true );

        final SDK sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );

        IPath exceptedLocation = null;

        op.setProjectProvider( "ant" );
        exceptedLocation = sdk.getLocation().append( "portlets" ).append( projectName + "-portlet" );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );

        op.setProjectProvider( "maven" );
        exceptedLocation = CoreUtil.getWorkspaceRoot().getLocation().append( projectName );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );
    }

    @Test
    public void testProjectProviderPossibleValues() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-project-provider-possbile-values" );

        final Set<String> acturalValues = op.getProjectProvider().service( PossibleValuesService.class ).values();

        assertNotNull( acturalValues );

        Set<String> exceptedValues = new HashSet<String>();
        exceptedValues.add( "ant" );
        exceptedValues.add( "maven" );

        assertEquals( true, exceptedValues.containsAll( acturalValues ) );
        assertEquals( true, acturalValues.containsAll( exceptedValues ) );
    }

    @Test
    public void testUseDefaultLocationEnablement() throws Exception
    {
        this.testUseDefaultLocationEnablement( true );
    }

    public void testUseDefaultLocationEnablement( boolean versionRestriction ) throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-use-default-location-enablement" );
        op.setProjectProvider( "maven" );

        assertEquals( true, op.getUseDefaultLocation().service( EnablementService.class ).enablement() );
        assertEquals( true, op.getUseDefaultLocation().enabled() );

        if( versionRestriction )
        {
            op.setProjectProvider( "ant" );

            assertEquals( true, op.getUseDefaultLocation().service( EnablementService.class ).enablement() );
            assertEquals( true, op.getUseDefaultLocation().enabled() );
        }
    }

    @Test
    public void testUseDefaultLocationListener() throws Exception
    {
        this.testUseDefaultLocationListener( false );
    }

    protected void testUseDefaultLocationListener( boolean versionRestriction ) throws Exception
    {
        final String projectName = "test-use-default-location-listener";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setProjectProvider( "maven" );

        IPath exceptedLocation = null;

        op.setUseDefaultLocation( true );
        exceptedLocation = CoreUtil.getWorkspaceRoot().getLocation().append( projectName );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );

        op.setUseDefaultLocation( false );
        assertEquals( null, op.getLocation().content() );

        if( versionRestriction )
        {
            op.setProjectProvider( "ant" );
            op.setPluginType( "portlet" );
            op.setUseDefaultLocation( true );

            final SDK sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );

            exceptedLocation = sdk.getLocation().append( "portlets" ).append( projectName + "-portlet" );
            assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );

            op.setUseDefaultLocation( false );
            assertEquals( null, op.getLocation().content() );
        }
    }
}
