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

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.EnablementService;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.server.util.ServerUtil;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public abstract class NewLiferayPluginProjectOpBase extends ProjectCoreBase
{

    final static IPath tempDownloadsPath = new Path( System.getProperty(
        "liferay.plugin.project.tests.tempdir", System.getProperty( "java.io.tmpdir" ) ) );

    protected IProject checkNewJsfAntProjectIvyFile( IProject jsfProject, String jsfSuite ) throws Exception
    {
        final IFile ivyXml = jsfProject.getFile( "ivy.xml" );

        final String ivyXmlContent = CoreUtil.readStreamToString( ivyXml.getContents() );

        final String expectedIvyXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/" + getRuntimeVersion() + "/ivy-" + jsfSuite + ".xml" ) );

        assertEquals( stripCarriageReturns( expectedIvyXmlContent ), stripCarriageReturns( ivyXmlContent ) );

        return jsfProject;
    }

    protected IProject checkNewThemeAntProject( NewLiferayPluginProjectOp op, IProject project, String expectedBuildFile )
        throws Exception
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

        assertEquals( stripCarriageReturns( expectedbuildXmlContent ), stripCarriageReturns( buildXmlContent ) );

        return project;
    }

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
        final String projectName = "test-" + jsfSuite + "-sdk-project";
        final NewLiferayPluginProjectOp op = newProjectOp();
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

    private IRuntime createNewRuntime( final String name ) throws Exception
    {
        final IPath newRuntimeLocation = new Path( getLiferayRuntimeDir().toString() + "-new" );

        if( ! newRuntimeLocation.toFile().exists() )
        {
            FileUtils.copyDirectory( getLiferayRuntimeDir().toFile(), newRuntimeLocation.toFile() );
        }

        assertEquals( true, newRuntimeLocation.toFile().exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        IRuntime runtime = ServerCore.findRuntime( name );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( name, npm );

            runtimeWC.setName( name );
            runtimeWC.setLocation( newRuntimeLocation );

            runtime = runtimeWC.save( true, npm );
        }

        ServerCore.getRuntimes();
        assertNotNull( runtime );

        return runtime;
    }

    private SDK createNewSDK() throws Exception
    {
        final IPath newSDKLocation = new Path( getLiferayPluginsSdkDir().toString() + "-new" );

        if( ! newSDKLocation.toFile().exists() )
        {
            FileUtils.copyDirectory( getLiferayPluginsSdkDir().toFile(), newSDKLocation.toFile() );
        }

        assertEquals( true, newSDKLocation.toFile().exists() );

        final SDK newSDK = SDKUtil.createSDKFromLocation( newSDKLocation );

        SDKManager.getInstance().addSDK( newSDK );

        return newSDK;
    }

    protected IProject createNewSDKProjectCustomLocation(
        final NewLiferayPluginProjectOp newProjectOp, IPath customLocation ) throws Exception
    {
        newProjectOp.setUseDefaultLocation( false );

        newProjectOp.setLocation( PathBridge.create( customLocation ) );

        return createAntProject( newProjectOp );
    }

    protected IProject createNewThemeAntProject( final NewLiferayPluginProjectOp op ) throws Exception
    {
        final IProject themeProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( themeProject );

        assertNotNull( webappRoot );

        return themeProject;
    }

    protected IProject createNewThemeAntProject( String themeParent, String themeFramework ) throws Exception
    {
        final String projectName = "test-theme-project-sdk-" + themeParent + "-" + themeFramework;
        final NewLiferayPluginProjectOp op = newProjectOp();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.theme );
        op.setThemeParent( themeParent );
        op.setThemeFramework( themeFramework );

        final IProject project = createNewThemeAntProject( op );

        return checkNewThemeAntProject( op, project, null );
    }

    protected IProject createNewThemeAntProjectDefaults() throws Exception
    {
        final String projectName = "test-theme-project-sdk-defaults";
        final NewLiferayPluginProjectOp op = newProjectOp();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.theme );

        IProject project = createNewThemeAntProject( op );

        return checkNewThemeAntProject( op, project, "build-theme-defaults.xml" );
    }

    @SuppressWarnings( "restriction" )
    protected IPath getCustomLocationBase()
    {
        final IPath customLocationBase =
            org.eclipse.core.internal.utils.FileUtil.canonicalPath( new Path( System.getProperty( "java.io.tmpdir" ) ) ).append(
                "custom-project-location-tests" );

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

    protected NewLiferayPluginProjectOp newProjectOp()
    {
        return NewLiferayPluginProjectOp.TYPE.instantiate();
    }

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

            ZipUtil.unzip( liferayPluginsSDKZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
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

            ZipUtil.unzip( liferayRuntimeZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
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
            (ILiferayTomcatRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter( ILiferayTomcatRuntime.class, npm );

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
    public void testDisplayNameDefaultValue() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp();

        final DefaultValueService dvs = op.getDisplayName().service( DefaultValueService.class );

        final String exceptedDisplayName = "Test Display Name Default Value";

        op.setProjectName( "test display name default value" );
        assertEquals( exceptedDisplayName, op.getDisplayName().content() );
        assertEquals( exceptedDisplayName, dvs.value() );

        op.setProjectName( "Test-Display-Name-Default-Value" );
        assertEquals( exceptedDisplayName, op.getDisplayName().content() );
        assertEquals( exceptedDisplayName, dvs.value() );

        op.setProjectName( "Test_Display_Name_Default_Value" );
        assertEquals( exceptedDisplayName, op.getDisplayName().content() );
        assertEquals( exceptedDisplayName, dvs.value() );

        op.setProjectName( "test-Display_name Default-value" );
        assertEquals( exceptedDisplayName, op.getDisplayName().content() );
        assertEquals( exceptedDisplayName, dvs.value() );

        final String projectName = "test-display_name default value";

        final String[] suffixs = { "-portlet", "-hook", "-theme", "-layouttpl", "-ext" };

        for( String suffix : suffixs )
        {
            op.setProjectName( projectName + suffix );
            assertEquals( exceptedDisplayName, op.getDisplayName().content() );
            assertEquals( exceptedDisplayName, dvs.value() );
        }
    }

    @Test
    public void testGroupIdValidation() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-group-id-validation" );
        op.setProjectProvider( "maven" );

        final ValidationService vs = op.getGroupId().service( ValidationService.class );

        op.setGroupId( ".com.liferay.test" );
        assertEquals( "A package name cannot start or end with a dot", vs.validation().message() );
        assertEquals( "A package name cannot start or end with a dot", op.getGroupId().validation().message() );

        op.setGroupId( "com.liferay.test." );
        assertEquals( "A package name cannot start or end with a dot", vs.validation().message() );
        assertEquals( "A package name cannot start or end with a dot", op.getGroupId().validation().message() );

        op.setGroupId( "com..liferay.test" );
        assertEquals( "A package name must not contain two consecutive dots", vs.validation().message() );
        assertEquals( "A package name must not contain two consecutive dots", op.getGroupId().validation().message() );

        op.setGroupId( " com.lifey.test" );
        assertEquals( "A package name must not start or end with a blank", vs.validation().message() );
        assertEquals( "A package name must not start or end with a blank", op.getGroupId().validation().message() );

        op.setGroupId( "com.liferay.test " );
        assertEquals( "A package name must not start or end with a blank", vs.validation().message() );
        assertEquals( "A package name must not start or end with a blank", op.getGroupId().validation().message() );

        op.setGroupId( "com.life*ray.test" );
        assertEquals( "'life*ray' is not a valid Java identifier", vs.validation().message() );
        assertEquals( "'life*ray' is not a valid Java identifier", op.getGroupId().validation().message() );

        op.setGroupId( "Com.liferay.test" );
        assertEquals( "By convention, package names usually start with a lowercase letter", vs.validation().message() );
        assertEquals(
            "By convention, package names usually start with a lowercase letter",
            op.getGroupId().validation().message() );

    }

    protected void testLocationListener() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp();
        op.setProjectProvider( "ant" );
        op.setUseDefaultLocation( false );

        final String projectNameWithoutSuffix = "project-name-without-suffix";
        final String locationWithoutSuffix = "location-without-suffix";

        op.setPluginType( "portlet" );
        final String suffix = "-portlet";

        // Both of project name and location are without type suffix.
        op.setProjectName( projectNameWithoutSuffix );
        op.setLocation( locationWithoutSuffix );
        assertEquals(
            locationWithoutSuffix + "/" + projectNameWithoutSuffix + suffix, op.getLocation().content().toString() );

        // Location does't have a type suffix, project name has one.
        op.setProjectName( projectNameWithoutSuffix + suffix );
        op.setLocation( locationWithoutSuffix );
        assertEquals(
            locationWithoutSuffix + "/" + projectNameWithoutSuffix + suffix, op.getLocation().content().toString() );

        // Location has a type suffix.
        op.setLocation( locationWithoutSuffix + suffix );
        assertEquals( locationWithoutSuffix + suffix, op.getLocation().content().toString() );
    }

    @Test
    public void testLocationValidation() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-location-validation-service" );
        op.setProjectProvider( "maven" );
        op.setPluginType( "protlet" );
        op.setUseDefaultLocation( false );

        final ValidationService vs = op.getLocation().service( ValidationService.class );

        String invalidLocation = null;
        invalidLocation = "not-absolute-location";
        op.setLocation( invalidLocation );
        assertEquals( "\"" + invalidLocation + "\" is not an absolute path.", vs.validation().message() );
        assertEquals( "\"" + invalidLocation + "\" is not an absolute path.", op.getLocation().validation().message() );

        if( Platform.getOS().equals( Platform.OS_WIN32 ) )
        {
            invalidLocation = "Z:\\test-location-validation-service";
        }
        else
        {
            invalidLocation = "/test-location-validation-service";
        }

        op.setLocation( invalidLocation );
        assertEquals( "Cannot create project content at " + "\"" + invalidLocation + "\"", vs.validation().message() );
        assertEquals(
            "Cannot create project content at " + "\"" + invalidLocation + "\"",
            op.getLocation().validation().message() );

        invalidLocation = CoreUtil.getWorkspaceRoot().getLocation().getDevice() + "\\";
        op.setLocation( invalidLocation );
        assertEquals( "\"" + invalidLocation + "\" is not a valid project location.", vs.validation().message() );
        assertEquals(
            "\"" + invalidLocation + "\" is not a valid project location.", op.getLocation().validation().message() );

        op.setLocation( "" );
        assertEquals( "Location must be specified.", vs.validation().message() );
        assertEquals( "Location must be specified.", op.getLocation().validation().message() );

    }

    @Test
    public void testNewExtAntProject() throws Exception
    {
        final String projectName = "test-ext-project-sdk";
        final NewLiferayPluginProjectOp op = newProjectOp();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.ext );

        IProject extProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( extProject );

        assertNotNull( webappRoot );

        final IVirtualFile extFile = webappRoot.getFile( "WEB-INF/liferay-portlet-ext.xml" );

        assertEquals( true, extFile.exists() );
    }

    @Test
    public void testNewHookAntProject() throws Exception
    {
        final String projectName = "test-hook-project-sdk";
        final NewLiferayPluginProjectOp op = newProjectOp();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.hook );

        final IProject hookProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( hookProject );

        assertNotNull( webappRoot );

        final IVirtualFile hookXml = webappRoot.getFile( "WEB-INF/liferay-hook.xml" );

        assertEquals( true, hookXml.exists() );
    }

    @Test
    public void testNewJsfAntProjects() throws Exception
    {
        createNewJsfAntProject( "jsf" );
        createNewJsfAntProject( "liferay_faces_alloy" );
        createNewJsfAntProject( "icefaces" );
        createNewJsfAntProject( "primefaces" );
        createNewJsfAntProject( "richfaces" );
    }

    @Test
    public void testNewLayoutAntProject() throws Exception
    {
        final String projectName = "test-layouttpl-project-sdk";
        final NewLiferayPluginProjectOp op = newProjectOp();
        op.setProjectName( projectName );
        op.setPluginType( PluginType.layouttpl );

        IProject layouttplProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( layouttplProject );

        assertNotNull( webappRoot );

        final IVirtualFile layoutXml = webappRoot.getFile( "WEB-INF/liferay-layout-templates.xml" );

        assertEquals( true, layoutXml.exists() );
    }

    public void testNewProjectCustomLocationPortlet() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomLocationPortletName = "test-project-custom-2-location-portlet";
        final IPath customLocationPortlet = customLocationBase.append( testProjectCustomLocationPortletName );

        final IProject newProjectPortlet =
            createNewSDKProjectCustomLocation(
                newProjectOp( testProjectCustomLocationPortletName ), customLocationPortlet );

        assertEquals(
            "Project not at expected custom location", true,
            newProjectPortlet.getLocation().equals( customLocationPortlet ) );
    }

    public void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomWrongSuffix = "test-project-custom-1-wrong-suffix";
        final IPath customLocationWrongSuffix = customLocationBase.append( testProjectCustomWrongSuffix );

        final IProject newProjectWrongSuffix =
            createNewSDKProjectCustomLocation( newProjectOp( testProjectCustomWrongSuffix ), customLocationWrongSuffix );

        assertEquals(
            "Project not at expected custom location",
            true,
            newProjectWrongSuffix.getLocation().equals(
                customLocationWrongSuffix.append( testProjectCustomWrongSuffix + "-portlet" ) ) );
    }

    public void testNewSDKProjectCustomLocation() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final String testProjectCustomLocationName = "test-project-custom-1-location";
        final IPath customLocation = customLocationBase.append( testProjectCustomLocationName + "-portlet" );

        final IProject newProject =
            createNewSDKProjectCustomLocation( newProjectOp( testProjectCustomLocationName ), customLocation );

        assertEquals( "Project not at expected custom location", true, newProject.getLocation().equals( customLocation ) );

        final IFile buildXml = newProject.getFile( "build.xml" );

        assertEquals( true, buildXml.exists() );

        final InputStream contents = buildXml.getContents( true );
        final String buildXmlContent = CoreUtil.readStreamToString( contents );
        contents.close();

        final Pattern p =
            Pattern.compile( ".*<import file=\".*portlets/build-common-portlet.xml\".*", Pattern.MULTILINE |
                Pattern.DOTALL );

        final Matcher m = p.matcher( buildXmlContent );

        assertEquals( "sdk project build.xml didn't use correct plugin type dir.", true, m.matches() );
    }

    public void testNewSDKProjectEclipseWorkspace() throws Exception
    {
        final NewLiferayPluginProjectOp newProjectOp = newProjectOp( "test-project-in-workspace" );
        newProjectOp.setUseSdkLocation( false );

        final IProject projectInWorkspace = createAntProject( newProjectOp );

        assertEquals(
            "project was not located in the eclipse workspace.", true,
            CoreUtil.getWorkspace().getRoot().getLocation().isPrefixOf( projectInWorkspace.getLocation() ) );
    }

    @Test
    public void testNewSDKProjectInSDK() throws Exception
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
    public void testNewSDKProjects() throws Exception
    {
        createAntProject( newProjectOp( "test-name-1" ) );
        createAntProject( newProjectOp( "Test With Spaces" ) );
        createAntProject( newProjectOp( "test_name_1" ) );
        createAntProject( newProjectOp( "-portlet-portlet" ) );
        createAntProject( newProjectOp( "-portlet-hook" ) );

        final NewLiferayPluginProjectOp op = newProjectOp( "-hook-hook" );
        op.setPluginType( PluginType.hook );
        createAntProject( op );
    }

    @Test
    public void testNewThemeProjects() throws Exception
    {
        createNewThemeAntProjectDefaults();
        createNewThemeAntProject( "_unstyled", "Freemarker" );
        createNewThemeAntProject( "_styled", "Velocity" );
        createNewThemeAntProject( "classic", "JSP" );
    }

    @Test
    public void testNewVaadinAntProject() throws Exception
    {
        final String projectName = "test-vaadin-project-sdk";
        final NewLiferayPluginProjectOp op = newProjectOp();
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
    public void testPluginsSDKNameDefaultValue() throws Exception
    {
        final SDK originalSDK = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        originalSDK.setDefault( true );

        final SDK newSDK = createNewSDK();
        newSDK.setDefault( false );

        SDKManager.getInstance().setSDKs( new SDK[] { originalSDK, newSDK } );

        NewLiferayPluginProjectOp op = newProjectOp();
        DefaultValueService dvs = op.getPluginsSDKName().service( DefaultValueService.class );

        assertEquals( originalSDK.getName(), dvs.value() );
        assertEquals( originalSDK.getName(), op.getPluginsSDKName().content() );

        op.dispose();

        originalSDK.setDefault( false );
        newSDK.setDefault( true );

        op = newProjectOp();
        dvs = op.getPluginsSDKName().service( DefaultValueService.class );

        assertEquals( newSDK.getName(), dvs.value() );
        assertEquals( newSDK.getName(), op.getPluginsSDKName().content() );

        op.dispose();

        SDKManager.getInstance().setSDKs( new SDK[] {} );

        op = newProjectOp();
        dvs = op.getPluginsSDKName().service( DefaultValueService.class );

        assertEquals( "<None>", dvs.value() );
        assertEquals( "<None>", op.getPluginsSDKName().content() );
    }

    @Test
    public void testPluginsSDKNameListener() throws Exception
    {
        final String projectName = "test-plugin-sdk-name-listener";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setProjectProvider( "ant" );
        op.setPluginType( "portlet" );
        op.setUseDefaultLocation( true );

        IPath exceptedLocation = null;

        final SDK originalSDK = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );

        exceptedLocation = originalSDK.getLocation().append( "portlets" ).append( projectName + "-portlet" );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );

        final SDK newSDK = createNewSDK();

        op.setPluginsSDKName( newSDK.getName() );

        exceptedLocation = newSDK.getLocation().append( "portlets" ).append( projectName + "-portlet" );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );
    }

    @Test
    public void testPluginsSDKNamePossibleValues() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-plugins-sdk-name-possbile-values" );
        op.setProjectProvider( "ant" );

        final SDK originSDK = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        final SDK newSDK = createNewSDK();

        Set<String> exceptedSDKNames = new HashSet<String>();
        exceptedSDKNames.add( originSDK.getName() );
        exceptedSDKNames.add( newSDK.getName() );

        final Set<String> acturalSDKNames = op.getPluginsSDKName().service( PossibleValuesService.class ).values();

        assertEquals( true, exceptedSDKNames.containsAll( acturalSDKNames ) );
        assertEquals( true, acturalSDKNames.containsAll( exceptedSDKNames ) );
    }

    @Test
    public void testPluginsSDKNameValidation() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-plugins-sdk-name-validation" );
        op.setProjectProvider( "ant" );

        final ValidationService vs = op.getPluginsSDKName().service( ValidationService.class );

        assertEquals( true, vs.validation().ok() );
        assertEquals( true, op.getPluginsSDKName().validation().ok() );

        op.setPluginsSDKName( "sdk-must-be-configured" );
        assertEquals( "Plugins SDK must be configured.", vs.validation().message() );
        // Value is not excepted.
        // assertEquals( "Plugins SDK must be configured.", op.getPluginsSDKName().validation().message() );

        // Create a new sdk and delete files of the sdk to make it invalid.
        final SDK newSDK = createNewSDK();

        if( newSDK.getLocation().toFile().exists() )
        {
            FileUtil.deleteDir( newSDK.getLocation().toFile(), true );
        }

        op.setPluginsSDKName( newSDK.getName() );
        assertEquals( "Plugins SDK " + newSDK.getName() + " is invalid.", vs.validation().message() );
        assertEquals( "Plugins SDK " + newSDK.getName() + " is invalid.", op.getPluginsSDKName().validation().message() );

        final SDK originSDK = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );

        op.setPluginsSDKName( originSDK.getName() );
        assertEquals( true, vs.validation().ok() );
        assertEquals( true, op.getPluginsSDKName().validation().ok() );

    }

    protected void testPluginTypeListener() throws Exception
    {
        this.testPluginTypeListener( false );
    }

    protected void testPluginTypeListener( Boolean versionRestriction ) throws Exception
    {
        final String projectName = "test-plugin-type-listener";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setProjectProvider( "ant" );
        op.setUseDefaultLocation( true );

        final SDK sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );

        final String[] pluginTypes = { "portlet", "hook", "layouttpl", "theme", "ext" };

        IPath exceptedLocation = null;

        for( String pluginType : pluginTypes )
        {
            op.setPluginType( pluginType );

            if( pluginType.equals( "portlet" ) )
            {
                exceptedLocation = sdk.getLocation().append( "portlets" ).append( projectName + "-portlet" );
            }
            else if( pluginType.equals( "hook" ) )
            {
                exceptedLocation = sdk.getLocation().append( "hooks" ).append( projectName + "-hook" );
            }
            else if( pluginType.equals( "layouttpl" ) )
            {
                exceptedLocation = sdk.getLocation().append( "layouttpl" ).append( projectName + "-layouttpl" );
            }
            else if( pluginType.equals( "theme" ) )
            {
                exceptedLocation = sdk.getLocation().append( "themes" ).append( projectName + "-theme" );
            }
            else
            {
                exceptedLocation = sdk.getLocation().append( "ext" ).append( projectName + "-ext" );
            }

            assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );
        }

        if( versionRestriction )
        {
            op.setUseSdkLocation( false );

            for( String pluginType : pluginTypes )
            {
                op.setPluginType( pluginType );

                if( pluginType.equals( "portlet" ) )
                {
                    exceptedLocation = CoreUtil.getWorkspaceRoot().getLocation().append( projectName + "-portlet" );
                }
                else if( pluginType.equals( "hook" ) )
                {
                    exceptedLocation = CoreUtil.getWorkspaceRoot().getLocation().append( projectName + "-hook" );
                }
                else if( pluginType.equals( "layouttpl" ) )
                {
                    exceptedLocation = CoreUtil.getWorkspaceRoot().getLocation().append( projectName + "-layouttpl" );
                }
                else if( pluginType.equals( "theme" ) )
                {
                    exceptedLocation = CoreUtil.getWorkspaceRoot().getLocation().append( "/" + projectName + "-theme" );
                }
                else
                {
                    exceptedLocation = CoreUtil.getWorkspaceRoot().getLocation().append( "/" + projectName + "-ext" );
                }

                assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );
            }
        }
    }

    @Test
    public void testPortletFrameworkAdvancedPossibleValues() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-portlet-framework-advanced-possible-values" );

        final Set<String> acturalFrameworks =
            op.getPortletFrameworkAdvanced().service( PossibleValuesService.class ).values();

        assertNotNull( acturalFrameworks );

        Set<String> exceptedFrameworks = new HashSet<String>();
        exceptedFrameworks.add( "jsf" );
        exceptedFrameworks.add( "icefaces" );
        exceptedFrameworks.add( "liferay_faces_alloy" );
        exceptedFrameworks.add( "primefaces" );
        exceptedFrameworks.add( "richfaces" );

        assertNotNull( exceptedFrameworks );

        assertEquals( true, exceptedFrameworks.containsAll( acturalFrameworks ) );
        assertEquals( true, acturalFrameworks.containsAll( exceptedFrameworks ) );

    }

    @Test
    public void testPortletFrameworkPossibleValues() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-portlet-framework-possible-values" );
        op.setProjectProvider( "ant" );
        op.setPluginType( "portlet" );

        final Set<String> acturalFrameworks = op.getPortletFramework().service( PossibleValuesService.class ).values();

        assertNotNull( acturalFrameworks );

        Set<String> exceptedFrameworks = new HashSet<String>();
        exceptedFrameworks.add( "mvc" );
        exceptedFrameworks.add( "jsf-2.x" );
        exceptedFrameworks.add( "vaadin" );

        assertNotNull( exceptedFrameworks );

        assertEquals( true, exceptedFrameworks.containsAll( acturalFrameworks ) );
        assertEquals( true, acturalFrameworks.containsAll( exceptedFrameworks ) );
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
    public void testPortletFrameworkValueLabel() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-portlet-framework-value-label" );

        final ValueLabelService vls = op.getPortletFramework().service( ValueLabelService.class );

        Set<String> acturalLables = new HashSet<String>();

        for( IPortletFramework pf : LiferayProjectCore.getPortletFrameworks() )
        {
            acturalLables.add( vls.provide( pf.getShortName() ) );
        }

        assertNotNull( acturalLables );

        Set<String> exceptedLables = new HashSet<String>();
        exceptedLables.add( "Liferay MVC" );
        exceptedLables.add( "JSF 2.x" );
        exceptedLables.add( "Vaadin" );
        exceptedLables.add( "JSF standard" );
        exceptedLables.add( "ICEfaces" );
        exceptedLables.add( "Liferay Faces Alloy" );
        exceptedLables.add( "PrimeFaces" );
        exceptedLables.add( "RichFaces" );

        assertEquals( true, exceptedLables.containsAll( acturalLables ) );
        assertEquals( true, acturalLables.containsAll( exceptedLables ) );
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
    public void testProjectNameValidation() throws Exception
    {
        NewLiferayPluginProjectOp op = newProjectOp();
        op.setUseDefaultLocation( true );

        ValidationService vs = op.getProjectName().service( ValidationService.class );

        final String validProjectName = "test-project-name-validation-service";

        op.setProjectName( validProjectName );
        assertEquals( true, vs.validation().ok() );
        assertEquals( true, op.getProjectName().validation().ok() );

        op.setProjectName( validProjectName + "-portlet" );
        assertEquals( true, vs.validation().ok() );
        assertEquals( true, op.getProjectName().validation().ok() );

        final IProject proj = createProject( op );

        op.dispose();
        op = newProjectOp();
        vs = op.getProjectName().service( ValidationService.class );

        op.setProjectName( validProjectName + "-portlet" );
        assertEquals( "A project with that name already exists.", vs.validation().message() );
        assertEquals( "A project with that name already exists.", op.getProjectName().validation().message() );

        op.setProjectName( validProjectName );
        assertEquals( "\"" + op.getLocation().content().toOSString() +
            "\" is not a valid because a project already exists at that location.", vs.validation().message() );
        assertEquals(
            "\"" + op.getLocation().content().toOSString() +
                "\" is not a valid because a project already exists at that location.",
            op.getProjectName().validation().message() );

        final IPath dotProjectLocation = proj.getLocation().append( ".project" );

        if( dotProjectLocation.toFile().exists() )
        {
            dotProjectLocation.toFile().delete();
        }

        op.dispose();
        op = newProjectOp();
        vs = op.getProjectName().service( ValidationService.class );

        op.setProjectName( validProjectName );
        assertEquals( "\"" + op.getLocation().content().toOSString() +
            "\" is not a valid because it already contains files.", vs.validation().message() );
        assertEquals( "\"" + op.getLocation().content().toOSString() +
            "\" is not a valid because it already contains files.", op.getProjectName().validation().message() );

        String invalidProjectName = validProjectName + "*";
        op.setProjectName( invalidProjectName );
        assertEquals(
            "* is an invalid character in resource name '" + invalidProjectName + "'.", vs.validation().message() );

        invalidProjectName = validProjectName + ".";
        op.setProjectName( invalidProjectName );
        assertEquals( "'" + invalidProjectName + "' is an invalid name on this platform.", vs.validation().message() );
        assertEquals(
            "'" + invalidProjectName + "' is an invalid name on this platform.",
            op.getProjectName().validation().message() );
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
    public void testProjectProviderValueLabel() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-project-provider-value-lable" );

        final ValueLabelService vls = op.getProjectProvider().service( ValueLabelService.class );

        final Set<String> acturaProviderShortNames =
            op.getProjectProvider().service( PossibleValuesService.class ).values();

        Set<String> acturalLables = new HashSet<String>();

        for( String shortName : acturaProviderShortNames )
        {
            acturalLables.add( vls.provide( shortName ) );
        }

        assertNotNull( acturalLables );

        Set<String> exceptedLables = new HashSet<String>();
        exceptedLables.add( "Ant (liferay-plugins-sdk)" );
        exceptedLables.add( "Maven (liferay-maven-plugin)" );

        assertEquals( true, exceptedLables.containsAll( acturalLables ) );
        assertEquals( true, acturalLables.containsAll( exceptedLables ) );
    }

    @Test
    public void testRuntimeNameDefaultValue() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-runtime-name-default-value" );

        final String originalRuntimeName = getRuntimeVersion();
        IRuntime originalRuntime = ServerCore.findRuntime( originalRuntimeName );

        assertNotNull( originalRuntime );
        assertEquals( true, ServerUtil.isLiferayRuntime( originalRuntime ) );

        final String newRuntimeName = originalRuntimeName + "-new";
        IRuntime newRuntime = createNewRuntime( newRuntimeName );

        assertNotNull( newRuntime );
        assertEquals( true, ServerUtil.isLiferayRuntime( newRuntime ) );

        Set<String> runtimeNames = new HashSet<String>();
        runtimeNames.add( originalRuntimeName );
        runtimeNames.add( newRuntimeName );

        final String defaultRuntimeName = runtimeNames.iterator().next();

        assertEquals( defaultRuntimeName, op.getRuntimeName().service( DefaultValueService.class ).value() );
        assertEquals( defaultRuntimeName, op.getRuntimeName().content() );

    }

    @Test
    public void testRuntimeNamePossibleValues() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-runtime-possbile-values" );

        final String originalRuntimeName = getRuntimeVersion();
        final IRuntime originalRuntime = ServerCore.findRuntime( originalRuntimeName );

        assertNotNull( originalRuntime );
        assertEquals( true, ServerUtil.isLiferayRuntime( originalRuntime ) );

        final String newRuntimeName = originalRuntimeName + "-new";
        IRuntime newRuntime = createNewRuntime( newRuntimeName );

        assertNotNull( newRuntime );
        assertEquals( true, ServerUtil.isLiferayRuntime( newRuntime ) );

        Set<String> exceptedRuntimeNames = new HashSet<String>();
        exceptedRuntimeNames.add( originalRuntimeName );
        exceptedRuntimeNames.add( newRuntimeName );

        final Set<String> acturalRuntimeNames = op.getRuntimeName().service( PossibleValuesService.class ).values();
        assertNotNull( acturalRuntimeNames );

        assertEquals( true, exceptedRuntimeNames.containsAll( acturalRuntimeNames ) );
        assertEquals( true, acturalRuntimeNames.containsAll( exceptedRuntimeNames ) );
    }

    @Test
    public void testRuntimeNameValidation() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-runtime-name-validation" );
        op.setProjectProvider( "ant" );

        final ValidationService vs = op.getRuntimeName().service( ValidationService.class );

        final String newVersion = getRuntimeVersion() + ".0";

        op.setRuntimeName( newVersion );

        assertEquals( "Liferay runtime must be configured.", vs.validation().message() );
        // Value is not excepted.
        // assertEquals( "Liferay runtime must be configured.", op.getRuntimeName().validation().message() );
    }

    protected void testUseDefaultLocationEnablement() throws Exception
    {
        this.testUseDefaultLocationEnablement( false );
    }

    protected void testUseDefaultLocationEnablement( boolean versionRestriction ) throws Exception
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

    protected void testUseDefaultLocationListener() throws Exception
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

    protected void testUseSdkLocationListener() throws Exception
    {
        final String projectName = "test-use-sdk-location-listener";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setProjectProvider( "ant" );
        op.setPluginType( "porlet" );
        op.setUseSdkLocation( true );

        assertNotNull( op.getLocation().content() );

        IPath exceptedLocation = null;

        final SDK sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );

        exceptedLocation = sdk.getLocation().append( "portlets" ).append( projectName + "-portlet" );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );

        op.setUseSdkLocation( false );
        exceptedLocation = CoreUtil.getWorkspaceRoot().getLocation().append( projectName + "-portlet" );
        assertEquals( exceptedLocation, PathBridge.create( op.getLocation().content() ) );
    }

}
