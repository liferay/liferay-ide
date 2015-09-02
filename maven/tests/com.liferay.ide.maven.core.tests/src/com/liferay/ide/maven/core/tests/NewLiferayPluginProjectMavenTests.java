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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.tests.ProjectCoreBase;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.EnablementService;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.services.ValidationService;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class NewLiferayPluginProjectMavenTests extends ProjectCoreBase
{
    protected final ProjectCoreBase base = new ProjectCoreBase();

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
        return ProjectCore.getDefault().getStateLocation().append( "liferay-plugins-sdk-6.2.0" );
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
        return ProjectCore.getDefault().getStateLocation().append( "liferay-portal-6.2.0-ce-ga1/tomcat-7.0.42" );
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
    public String getRuntimeVersion()
    {
        return "6.2.0";
    }

    @Test
    public void testCreateNewMavenProject() throws Exception
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
    public void testHookProjectName() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-hook" );
        op.setProjectProvider( "maven" );
        op.setPluginType( PluginType.hook );
        op.setUseDefaultLocation( true );

        IProject expectedProject = createMavenProject(op);

        String expectedProjectName = expectedProject.getName();

        String actualProjectName = op.getProjectNames().get(0).getName().content();

        assertEquals( expectedProjectName, actualProjectName );
    }

    @Test
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

        final String expected = "\"" + invalidLocation + "\"" + " is not an absolute path.";

        assertEquals( normalize( expected ), normalize( vs.validation().message() ) );
        assertEquals( normalize( expected ), normalize( op.getLocation().validation().message() ) );

        if( CoreUtil.isWindows() )
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

        if( CoreUtil.isWindows() )
        {
            invalidLocation = CoreUtil.getWorkspaceRoot().getLocation().getDevice() + "\\";
        }
        else
        {
            invalidLocation = "/";
        }

        op.setLocation( invalidLocation );

        final String expected3 = "Project location is not empty or a parent pom.";

        assertEquals( expected3, vs.validation().message() );
        //assertEquals( expected3, op.getLocation().validation().message() );

        //IDE-2069
        invalidLocation = getLiferayRuntimeDir().removeLastSegments( 2 ).toOSString();
        op.setLocation( invalidLocation );

        final String expected5 = "Project location is not empty or a parent pom.";

        assertEquals( expected5, vs.validation().message() );

        op.setLocation( "" );
        final String expected4 = "Location must be specified.";
        assertEquals( expected4, vs.validation().message() );
        assertEquals( expected4, op.getLocation().validation().message() );
    }

    private String normalize( String val )
    {
        return val.replaceAll( "\\\\", "/" );
    }

    @Test
    public void testParentFolderLocationValidation() throws Exception
    {
        File parentFolder =
            CoreUtil.getWorkspaceRoot().getLocation().append(
                "test-parent-folder-location-validation-service" ).toFile();

        if( !parentFolder.exists() )
        {
            parentFolder.mkdir();
        }

        File pomFile = new File( "projects/validations/location/pom.xml" );

        FileUtil.copyFileToDir( pomFile, parentFolder );

        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "test-parent-folder-location-validation-service" );

        final ValidationService vs = op.getLocation().service( ValidationService.class );

        op.setProjectProvider( "maven" );
        op.setPluginType( "portlet" );
        op.setUseDefaultLocation( false );
        op.setLocation( parentFolder.getAbsolutePath() );

        String projectName = op.getProjectName().content();

        String expected = "The project name \"" + projectName + "\" can't be the same as the parent.";

        assertEquals( expected, vs.validation().message() );
        assertEquals( expected, op.getLocation().validation().message() );
    }

    @Test
    @Ignore
    public void testPortletFrameworkValidation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        NewLiferayPluginProjectOp op = newProjectOp( "test-portlet-framework-validation" );
        op.setPluginType( "portlet" );
        final ValidationService vs = op.getPortletFramework().service( ValidationService.class );

        assertEquals( true, vs.validation().ok() );

        final SDK newSDK = createNewSDK();
        newSDK.setVersion( "6.0.0" );

        final IPortletFramework jsf = ProjectCore.getPortletFramework( "jsf" );

        op.setProjectProvider( "ant" );
        op.setPortletFramework( "jsf" );

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
    public void testPortletProjectName() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-name" );
        op.setProjectProvider( "maven" );
        op.setPluginType( PluginType.portlet );
        op.setUseDefaultLocation( true );
        op.setPortletFramework( "mvc" );
        op.setPortletName( "testPortlet" );

        IProject expectedProject = createMavenProject(op);

        String expectedProjectName = expectedProject.getName();

        String actualProjectName = op.getProjectNames().get(0).getName().content();

        assertEquals( expectedProjectName, actualProjectName );
    }

    @Test
    public void testProjectNameListener() throws Exception
    {
        if( shouldSkipBundleTests() ) return ;

        final NewLiferayPluginProjectOp op = newProjectOp( "" );
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
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "test-project-provider-listener" );
        final String projectName = op.getProjectName().content();
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
        if( shouldSkipBundleTests() ) return;

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
    public void testServiceBuilderProjectName() throws Exception
    {
        NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( "test-name" );
        op.setProjectProvider( "maven" );
        op.setPluginType( PluginType.servicebuilder );

        final IProject project = base.createProject( op, op.getProjectName() + "-portlet" );

        String projectName = project.getName();
        String finalProjectName = projectName.substring( 0, projectName.lastIndexOf( "-" ) );

        ElementList<ProjectName> projectNames = op.getProjectNames();

        List<String> finalProjectnames = new ArrayList<String>();

        for( ProjectName expectedProjectName : projectNames )
        {
            finalProjectnames.add( expectedProjectName.getName().content() );
        }

        assertEquals( true, finalProjectnames.contains( finalProjectName ) );
        assertEquals( true, finalProjectnames.contains( finalProjectName + "-portlet" ) );
        assertEquals( true, finalProjectnames.contains( finalProjectName + "-portlet-service" ) );
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

            assertEquals( false, op.getUseDefaultLocation().service( EnablementService.class ).enablement() );
            assertEquals( false, op.getUseDefaultLocation().enabled() );
        }
    }

    @Test
    public void testUseDefaultLocationListener() throws Exception
    {
        this.testUseDefaultLocationListener( false );
    }

    protected void testUseDefaultLocationListener( boolean versionRestriction ) throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-use-default-location-listener" );
        final String projectName = op.getProjectName().content();
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
