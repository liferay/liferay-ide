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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public abstract class NewLiferayPluginProjectOpBaseTests extends ProjectCoreBaseTests
{
    final static IPath tempDownloadsPath = new Path( System.getProperty(
        "liferay.plugin.project.tests.tempdir", System.getProperty( "java.io.tmpdir" ) ) );

    protected IProject createAntProject( NewLiferayPluginProjectOp op ) throws Exception
    {
        final IProject project = createProject( op );

        assertEquals(
            "SDK project layout is not standard, /src folder exists.", false, project.getFolder( "src" ).exists() );

        switch( op.getPluginType().content() )
        {
            case ext:
                break;
            case hook:
            case portlet:

                assertEquals(
                    "java source folder docroot/WEB-INF/src doesn't exist.", true,
                    project.getFolder( "docroot/WEB-INF/src" ).exists() );

                break;
            case layouttpl:
                break;
            case theme:
                break;
            default:
                break;
        }

        return project;
    }

    protected IProject createNewJsfAntProject( String jsfSuite ) throws Exception
    {
        final String projectName = "test-" + jsfSuite +  "-sdk-project";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPortletFramework( "jsf-2.x" );
        op.setPortletFrameworkAdvanced( jsfSuite );

        final IProject jsfProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( jsfProject );

        assertNotNull( webappRoot );

        final IVirtualFile config = webappRoot.getFile( "WEB-INF/faces-config.xml" );

        assertEquals( true, config.exists() );

        return checkNewJsfAntProjectIvyFile( jsfProject, jsfSuite );
    }

    protected IProject checkNewJsfAntProjectIvyFile( IProject jsfProject, String jsfSuite ) throws Exception
    {
        final IFile ivyXml = jsfProject.getFile( "ivy.xml" );

        final String ivyXmlContent = CoreUtil.readStreamToString( ivyXml.getContents() );

        final String expectedIvyXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/" + getRuntimeVersion() + "/ivy-" + jsfSuite + ".xml" ) );

        assertEquals(
            stripCarriageReturns( expectedIvyXmlContent ), stripCarriageReturns( ivyXmlContent ) );

        return jsfProject;
    }

    protected IProject createNewSDKProjectCustomLocation( final NewLiferayPluginProjectOp newProjectOp, IPath customLocation ) throws Exception
    {
        newProjectOp.setUseDefaultLocation( false );

        newProjectOp.setLocation( PathBridge.create( customLocation ) );

        return createAntProject( newProjectOp );
    }

    protected IProject createNewThemeAntProject(  final NewLiferayPluginProjectOp op ) throws Exception
    {
        final IProject themeProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( themeProject );

        assertNotNull( webappRoot );

        return themeProject;
    }

    protected IProject createNewThemeAntProject(String themeParent, String themeFramework ) throws Exception
    {
        final String projectName = "test-theme-project-sdk-" + themeParent + "-" + themeFramework;
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.theme );
        op.setThemeParent( themeParent );
        op.setThemeFramework( themeFramework );

        final IProject project = createNewThemeAntProject( op );

        return checkNewThemeAntProject( op, project, null );
    }

    protected IProject checkNewThemeAntProject( NewLiferayPluginProjectOp op, IProject project, String expectedBuildFile ) throws Exception
    {
        final String themeParent = op.getThemeParent().content();
        final String themeFramework = op.getThemeFramework().content();
        final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );
        final IVirtualFile readme = webappRoot.getFile( "WEB-INF/src/resources-importer/readme.txt" );

        assertEquals( true, readme.exists() );

        final IFile buildXml = project.getFile( "build.xml" );

        final String buildXmlContent = CoreUtil.readStreamToString( buildXml.getContents() );

        if( expectedBuildFile == null )
        {
            expectedBuildFile = "build-theme-" + themeParent + "-" + themeFramework + ".xml";
        }

        final String expectedbuildXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream( "files/" + expectedBuildFile ) );

        assertEquals(
            stripCarriageReturns( expectedbuildXmlContent ), stripCarriageReturns( buildXmlContent ) );

        return project;
    }

    protected IProject createNewThemeAntProjectDefaults() throws Exception
    {
        final String projectName = "test-theme-project-sdk-defaults";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.theme );

        IProject project = createNewThemeAntProject( op );

        return checkNewThemeAntProject( op, project, "build-theme-defaults.xml" );
    }

    protected IPath getCustomLocationBase()
    {
        final IPath customLocationBase =
            new Path( System.getProperty( "java.io.tmpdir" ) ).append( "custom-project-location-tests" );
        return customLocationBase;
    }

    protected abstract IPath getLiferayPluginsSdkDir();

    protected abstract IPath getLiferayPluginsSDKZip();

    protected abstract String getLiferayPluginsSDKZipUrl();

    protected abstract IPath getLiferayRuntimeDir();

    protected abstract IPath getLiferayRuntimeZip();

    protected abstract String getLiferayRuntimeZipUrl();

    protected abstract String getRuntimeId();

    protected abstract String getRuntimeVersion();

    protected NewLiferayPluginProjectOp newProjectOp( final String projectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );

        return op;
    }

    /**
     * @throws Exception
     */
    @Before
    public void setupPluginsSDKAndRuntime() throws Exception
    {
        final File liferayPluginsSdkDirFile = getLiferayPluginsSdkDir().toFile();

        if( ! liferayPluginsSdkDirFile.exists() )
        {
            final File liferayPluginsSDKZipFile = getLiferayPluginsSDKZip().toFile();

            if( ! liferayPluginsSDKZipFile.exists() )
            {
                FileUtil.downloadFile( getLiferayPluginsSDKZipUrl(), getLiferayPluginsSDKZip().toFile() );
            }

            assertEquals( true, liferayPluginsSDKZipFile.exists() );

            ZipUtil.unzip(
                liferayPluginsSDKZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayPluginsSdkDirFile.exists() );

        SDK sdk = null;

        final SDK existingSdk = SDKManager.getInstance().getSDK( getLiferayPluginsSdkDir() );

        if( existingSdk == null )
        {
            sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        }
        else
        {
            sdk = existingSdk;
        }

        assertNotNull( sdk );

        sdk.setDefault( true );

        SDKManager.getInstance().setSDKs( new SDK[] { sdk } );

        final File liferayRuntimeDirFile = getLiferayRuntimeDir().toFile();

        if( ! liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = getLiferayRuntimeZip().toFile();

            if( ! liferayRuntimeZipFile.exists() )
            {
                FileUtil.downloadFile( getLiferayRuntimeZipUrl(), getLiferayRuntimeZip().toFile() );
            }

            assertEquals( true, liferayRuntimeZipFile.exists() );

            ZipUtil.unzip(
                liferayRuntimeZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        final String runtimeName = getRuntimeVersion();

        IRuntime runtime = ServerCore.findRuntime( runtimeName );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( runtimeName, npm );

            runtimeWC.setName( runtimeName );
            runtimeWC.setLocation( getLiferayRuntimeDir() );

            runtime = runtimeWC.save( true, npm );
        }

        assertNotNull( runtime );

        final ILiferayTomcatRuntime liferayRuntime =
            (ILiferayTomcatRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter(
                ILiferayTomcatRuntime.class, npm );

        assertNotNull( liferayRuntime );

        final IPath customLocationBase = getCustomLocationBase();

        final File customBaseDir = customLocationBase.toFile();

        if( customBaseDir.exists() )
        {
            FileUtil.deleteDir( customBaseDir, true );

            assertEquals( "Unable to delete pre-existing customBaseDir", false, customBaseDir.exists() );
        }
    }



    @Test
    public void testCreateNewExtAntProject() throws Exception
    {
        final String projectName = "test-ext-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.ext );

        IProject extProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( extProject );

        assertNotNull( webappRoot );

        final IVirtualFile extFile = webappRoot.getFile( "WEB-INF/liferay-portlet-ext.xml" );

        assertEquals( true, extFile.exists() );
    }

    @Test
    public void testCreateNewHookAntProject() throws Exception
    {
        final String projectName = "test-hook-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.hook );

        final IProject hookProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( hookProject );

        assertNotNull( webappRoot );

        final IVirtualFile hookXml = webappRoot.getFile( "WEB-INF/liferay-hook.xml" );

        assertEquals( true, hookXml.exists() );
    }

    @Test
    public void testCreateNewJsfAntProjects() throws Exception
    {
        createNewJsfAntProject( "jsf" );
        createNewJsfAntProject( "liferay_faces_alloy" );
        createNewJsfAntProject( "icefaces" );
        createNewJsfAntProject( "primefaces" );
        createNewJsfAntProject( "richfaces" );
    }

    @Test
    public void testCreateNewLayoutAntProject() throws Exception
    {
        final String projectName = "test-layouttpl-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.layouttpl );

        IProject layouttplProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( layouttplProject );

        assertNotNull( webappRoot );

        final IVirtualFile layoutXml = webappRoot.getFile( "WEB-INF/liferay-layout-templates.xml" );

        assertEquals( true, layoutXml.exists() );
    }

    @Test
    public void testCreateNewSDKProjectCustomLocation() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomLocationName = "test-project-custom-1-location";
        final IPath customLocation = customLocationBase.append( testProjectCustomLocationName + "-portlet");

        final IProject newProject =
            createNewSDKProjectCustomLocation( newProjectOp( testProjectCustomLocationName ), customLocation );

        assertEquals( "Project not at expected custom location", true, newProject.getLocation().equals( customLocation ) );
    }

    @Test
    public void testCreateNewSDKProjectEclipseWorkspace() throws Exception
    {
        final NewLiferayPluginProjectOp newProjectOp = newProjectOp( "test-project-in-workspace" );
        newProjectOp.setUseSdkLocation( true );

        final IProject projectInWorkspace = createAntProject( newProjectOp );

        assertEquals(
            "project was not located in the eclipse workspace.", true,
            CoreUtil.getWorkspace().getRoot().getLocation().isPrefixOf( projectInWorkspace.getLocation() ) );
    }

    @Test
    public void testCreateNewSDKProjectInSDK() throws Exception
    {
        final IProject projectInSDK = createAntProject( newProjectOp( "test-project-in-sdk" ) );

        assertNotNull( projectInSDK );

        assertEquals( true, projectInSDK.exists() );

        final SDK sdk = SDKManager.getInstance().getDefaultSDK();

        assertEquals( true, sdk.getLocation().isPrefixOf( projectInSDK.getLocation() ) );

        final IFile buildXml = projectInSDK.getFile( "build.xml" );

        assertNotNull( buildXml );

        assertEquals( true, buildXml.exists() );

        final String buildXmlContent = CoreUtil.readStreamToString( buildXml.getContents( true ) );

        final Pattern p =
            Pattern.compile( ".*<import file=\"\\.\\./build-common-portlet.xml\".*", Pattern.MULTILINE | Pattern.DOTALL );

        final Matcher m = p.matcher( buildXmlContent );

        assertEquals( "sdk project build.xml didn't use relative import.", true, m.matches() );
    }

    @Test
    public void testCreateNewSDKProjects() throws Exception
    {
        createAntProject( newProjectOp( "test-name-1" ) );
        createAntProject( newProjectOp( "Test With Spaces" ) );
        createAntProject( newProjectOp( "test_name_1" ) );
    }

    @Test
    public void testCreateNewThemeProjects() throws Exception
    {
        createNewThemeAntProjectDefaults();
        createNewThemeAntProject( "_unstyled", "Freemarker" );
        createNewThemeAntProject( "_styled", "Velocity" );
        createNewThemeAntProject( "classic", "JSP" );
    }

    @Test
    public void testCreateNewVaadinAntProject() throws Exception
    {
        final String projectName = "test-vaadin-project-sdk";
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "vaadin" );

        IProject vaadinProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( vaadinProject );

        assertNotNull( webappRoot );

        final IVirtualFile application =
            webappRoot.getFile( "WEB-INF/src/testvaadinprojectsdk/TestVaadinProjectSdkApplication.java" );

        assertEquals( true, application.exists() );
    }

    @Test
    public void testCreateProjectCustomLocationPortlet() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomLocationPortletName = "test-project-custom-2-location-portlet";
        final IPath customLocationPortlet = customLocationBase.append( testProjectCustomLocationPortletName );

        final IProject newProjectPortlet =
            createNewSDKProjectCustomLocation( newProjectOp( testProjectCustomLocationPortletName ), customLocationPortlet );

        assertEquals( "Project not at expected custom location", true, newProjectPortlet.getLocation().equals( customLocationPortlet ) );
    }

    @Test
    public void testCreateProjectCustomLocationWrongSuffix() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomWrongSuffix = "test-project-custom-1-wrong-suffix";
        final IPath customLocationWrongSuffix = customLocationBase.append( testProjectCustomWrongSuffix );

        final IProject newProjectWrongSuffix =
            createNewSDKProjectCustomLocation(
                newProjectOp( testProjectCustomWrongSuffix ), customLocationWrongSuffix );

        assertEquals(
            "Project not at expected custom location",
            true,
            newProjectWrongSuffix.getLocation().equals(
                customLocationWrongSuffix.append( testProjectCustomWrongSuffix + "-portlet" ) ) );
    }

    @Test
    public void testCreateProjectCustomLocationWrongSuffixPortlet() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomWrongSuffix2 = "test-project-custom-2-wrong-suffix";
        final IPath customLocationWrongSuffix2 = customLocationBase.append( testProjectCustomWrongSuffix2 );

        final IProject newProjectWrongSuffix2 =
            createNewSDKProjectCustomLocation(
                newProjectOp( testProjectCustomWrongSuffix2 ), customLocationWrongSuffix2 );

        assertEquals(
            "Project not at expected custom location",
            true,
            newProjectWrongSuffix2.getLocation().equals(
                customLocationWrongSuffix2.append( testProjectCustomWrongSuffix2 + "-portlet" ) ) );
    }

    @Test
    public void testNewLiferayPluginProjectOpDefaults() throws Exception
    {
        // TODO test all @Default services
    }

    @Test
    public void testNewLiferayPluginProjectOpEnablement() throws Exception
    {
        // TODO test all @Enablement
    }

    @Test
    public void testNewLiferayPluginProjectOpListeners() throws Exception
    {
        // TODO test all @Listeners
    }

    @Test
    public void testNewLiferayPluginProjectOpPossibleValues() throws Exception
    {
        // TODO test all @PossibleValue services
    }

    @Test
    public void testNewLiferayPluginProjectOpValidation() throws Exception
    {
        // TODO test all @Validation services
    }



}
