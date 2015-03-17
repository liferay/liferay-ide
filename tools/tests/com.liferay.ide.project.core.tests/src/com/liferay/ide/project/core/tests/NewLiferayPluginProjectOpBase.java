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

package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public abstract class NewLiferayPluginProjectOpBase extends ProjectCoreBase
{
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
        final IFolder defaultDocroot = LiferayCore.create( IWebProject.class, project ).getDefaultDocrootFolder();
        final IFile readme = defaultDocroot.getFile( "WEB-INF/src/resources-importer/readme.txt" );

        assertEquals( true, readme.exists() );

        final IFile buildXml = project.getFile( "build.xml" );

        final String buildXmlContent = CoreUtil.readStreamToString( buildXml.getContents() );

        if( expectedBuildFile == null )
        {
            expectedBuildFile = "build-theme-" + themeParent + "-" + themeFramework + ".xml";
        }

        final String expectedbuildXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream( "files/" + expectedBuildFile ) ).replaceAll(
                "RUNTIMEVERSION", getRuntimeVersion() );

        assertEquals( stripCarriageReturns( expectedbuildXmlContent ), stripCarriageReturns( buildXmlContent ) );

        return project;
    }

    protected IProject createNewJsfAntProject( String jsfSuite, String suffix ) throws Exception
    {
        final String projectName = jsfSuite + suffix;
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setPortletFramework( "jsf-2.x" );
        op.setPortletFrameworkAdvanced( jsfSuite );

        final IProject jsfProject = createAntProject( op );

        final IFolder defaultDocroot =
            LiferayCore.create( IWebProject.class, jsfProject ).getDefaultDocrootFolder();

        assertNotNull( defaultDocroot );

        final IFile config = defaultDocroot.getFile( "WEB-INF/faces-config.xml" );

        assertEquals( true, config.exists() );

        return checkNewJsfAntProjectIvyFile( jsfProject, jsfSuite );
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

        final IFolder defaultDocroot =
            LiferayCore.create( IWebProject.class, themeProject ).getDefaultDocrootFolder();

        assertNotNull( defaultDocroot );

        return themeProject;
    }

    protected IProject createNewThemeAntProject( String themeParent, String themeFramework ) throws Exception
    {
        final String projectName = "test-theme-project-sdk-" + themeParent + "-" + themeFramework;
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setPluginType( PluginType.theme );
        op.setThemeParent( themeParent );
        op.setThemeFramework( themeFramework );

        final IProject project = createNewThemeAntProject( op );

        return checkNewThemeAntProject( op, project, null );
    }

    protected IProject createNewThemeAntProjectDefaults() throws Exception
    {
        final String projectName = "test-theme-project-sdk-defaults";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setPluginType( PluginType.theme );

        IProject project = createNewThemeAntProject( op );

        return checkNewThemeAntProject( op, project, "build-theme-defaults.xml" );
    }

    protected abstract String getServiceXmlDoctype();

    @Test
    public void testDisplayNameDefaultValue() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "display-name-default-value" );

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

    protected void testLocationListener() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "location-listener" );
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
        assertEquals( locationWithoutSuffix , op.getLocation().content().toString() );

        // Location has a type suffix.
        op.setLocation( locationWithoutSuffix + suffix );
        assertEquals( locationWithoutSuffix + suffix, op.getLocation().content().toString() );
    }

    @Test
    public void testNewExtAntProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "ext" );
        op.setPluginType( PluginType.ext );

        IProject extProject = createAntProject( op );

        final IFolder defaultDocroot =
            LiferayCore.create( IWebProject.class, extProject ).getDefaultDocrootFolder();

        assertNotNull( defaultDocroot );

        final IFile extFile = defaultDocroot.getFile( "WEB-INF/liferay-portlet-ext.xml" );

        assertEquals( true, extFile.exists() );
    }

    @Test
    public void testNewHookAntProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "test-hook-project-sdk" );
        op.setPluginType( PluginType.hook );

        final IProject hookProject = createAntProject( op );

        final IFolder webappRoot = LiferayCore.create( IWebProject.class, hookProject ).getDefaultDocrootFolder();

        assertNotNull( webappRoot );

        final IFile hookXml = webappRoot.getFile( "WEB-INF/liferay-hook.xml" );

        assertEquals( true, hookXml.exists() );
    }

    @Test
    public void testNewJsfAntProjects() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        createNewJsfAntProject( "jsf", "" );
        createNewJsfAntProject( "liferay_faces_alloy", "" );
        createNewJsfAntProject( "icefaces", "" );
        createNewJsfAntProject( "primefaces", "" );
    }

    private void testNewJsfRichfacesProject( String framework, boolean richfacesEnabled ) throws Exception
    {
        final IProject project = createNewJsfAntProject( framework, "rf" );

        final String contents =
            CoreUtil.readStreamToString( project.getFile( "docroot/WEB-INF/web.xml" ).getContents( true ) );

        assertEquals( richfacesEnabled, contents.contains( "org.richfaces.resourceMapping.enabled" ) );

        assertEquals( richfacesEnabled, contents.contains( "org.richfaces.webapp.ResourceServlet" ) );
    }

    protected void testNewJsfRichfacesProjects() throws Exception
    {
        testNewJsfRichfacesProject( "primefaces", false );
        testNewJsfRichfacesProject( "richfaces", true );
    }

    protected void testNewLayoutAntProject() throws Exception
    {
        final String projectName = "test-layouttpl-project-sdk";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setPluginType( PluginType.layouttpl );

        IProject layouttplProject = createAntProject( op );

        final IFolder webappRoot = LiferayCore.create( IWebProject.class, layouttplProject ).getDefaultDocrootFolder();

        assertNotNull( webappRoot );

        final IFile layoutXml = webappRoot.getFile( "WEB-INF/liferay-layout-templates.xml" );

        assertEquals( true, layoutXml.exists() );
    }

    @Test
    public void testNewPortletAntProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "portlet-without-servicexml" );
        op.setPluginType( PluginType.portlet );

        final IProject portletProject = createAntProject( op );

        final IFolder webappRoot = LiferayCore.create( IWebProject.class, portletProject ).getDefaultDocrootFolder();

        assertNotNull( webappRoot );

        final IFile serviceXml = webappRoot.getFile( "WEB-INF/service.xml" );

        assertEquals( false, serviceXml.exists() );
    }

    protected void testNewProjectCustomLocationPortlet() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final NewLiferayPluginProjectOp op = newProjectOp( "test-project-custom-2-location" );
        final String testProjectCustomLocationPortletName = op.getProjectName().content() + "-portlet";
        final IPath customLocationPortlet = customLocationBase.append( testProjectCustomLocationPortletName );

        final IProject newProjectPortlet = createNewSDKProjectCustomLocation( op, customLocationPortlet );

        assertEquals(
            "Project not at expected custom location", newProjectPortlet.getLocation(), customLocationPortlet );
    }


    protected void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final NewLiferayPluginProjectOp op = newProjectOp( "test-project-custom-1-wrong-suffix" );
        final String testProjectCustomWrongSuffix = op.getProjectName().content();
        final IPath customLocationWrongSuffix = customLocationBase.append( testProjectCustomWrongSuffix );

        final IProject newProjectWrongSuffix = createNewSDKProjectCustomLocation( op, customLocationWrongSuffix );

        assertEquals(
            "Project not at expected custom location",
            true,
            newProjectWrongSuffix.getLocation().equals(
                customLocationWrongSuffix.append( testProjectCustomWrongSuffix + "-portlet" ) ) );
    }


    protected void testNewSDKProjectCustomLocation() throws Exception
    {
        final IPath customLocationBase = getCustomLocationBase();

        final NewLiferayPluginProjectOp op = newProjectOp( "test-project-custom-1-location" );
        final String testProjectCustomLocationName = op.getProjectName().content();
        final IPath customLocation = customLocationBase.append( testProjectCustomLocationName + "-portlet" );

        final IProject newProject = createNewSDKProjectCustomLocation( op, customLocation );

        assertEquals( "Project expected to be at custom location", true, newProject.getLocation().equals( customLocation ) );

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

    protected void testNewSDKProjectEclipseWorkspace() throws Exception
    {
        final NewLiferayPluginProjectOp newProjectOp = newProjectOp( "test-project-in-workspace" );
        newProjectOp.setUseSdkLocation( false );

        final IProject projectInWorkspace = createAntProject( newProjectOp );

        assertEquals(
            "project was not located in the eclipse workspace.", true,
            CoreUtil.getWorkspace().getRoot().getLocation().isPrefixOf( projectInWorkspace.getLocation() ) );
    }

    protected void testNewSDKProjectInSDK() throws Exception
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
        if( shouldSkipBundleTests() ) return;

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
    public void testNewServiceBuilderPortletAntProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "portlet-with-servicexml" );
        op.setPluginType( PluginType.servicebuilder );

        final IProject portletProject = createAntProject( op );

        final IFolder webappRoot = LiferayCore.create( IWebProject.class, portletProject ).getDefaultDocrootFolder();

        assertNotNull( webappRoot );

        final IFile serviceXml = webappRoot.getFile( "WEB-INF/service.xml" );

        assertEquals( true, serviceXml.exists() );

        final String serviceXmlContent = CoreUtil.readStreamToString( serviceXml.getContents() );

        assertEquals( true, serviceXmlContent.contains( getServiceXmlDoctype() ) );
    }

    protected void testNewThemeProjects() throws Exception
    {
        createNewThemeAntProjectDefaults();
        createNewThemeAntProject( "_unstyled", "Freemarker" );
        createNewThemeAntProject( "_styled", "Velocity" );
        createNewThemeAntProject( "classic", "JSP" );
    }

    @Test
    public void testNewVaadinAntProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "test-vaadin-project-sdk" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "vaadin" );

        IProject vaadinProject = createAntProject( op );

        final IFolder webappRoot = LiferayCore.create( IWebProject.class, vaadinProject ).getDefaultDocrootFolder();

        assertNotNull( webappRoot );

        final IFile application =
            webappRoot.getFile( "WEB-INF/src/testvaadinprojectsdk" + getRuntimeVersion() +
                "/TestVaadinProjectSdk" + getRuntimeVersion() + "Application.java" );

        assertEquals( true, application.exists() );
    }

    @Test
    public void testPluginsSDKNameDefaultValue() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final SDK originalSDK = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        originalSDK.setDefault( true );

        final SDK newSDK = createNewSDK();
        newSDK.setDefault( false );

        SDKManager.getInstance().setSDKs( new SDK[] { originalSDK, newSDK } );

        final NewLiferayPluginProjectOp op1 = newProjectOp( "" );
        DefaultValueService dvs = op1.getPluginsSDKName().service( DefaultValueService.class );

        assertEquals( originalSDK.getName(), dvs.value() );
        assertEquals( originalSDK.getName(), op1.getPluginsSDKName().content() );

        op1.dispose();

        originalSDK.setDefault( false );
        newSDK.setDefault( true );

        final NewLiferayPluginProjectOp op2 = newProjectOp( "" );
        dvs = op2.getPluginsSDKName().service( DefaultValueService.class );

        assertEquals( newSDK.getName(), dvs.value() );
        assertEquals( newSDK.getName(), op2.getPluginsSDKName().content() );
    }

    @Test
    public void testPluginsSDKNameListener() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "test-plugin-sdk-name-listener" );
        final String projectName = op.getProjectName().content();
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
        if( shouldSkipBundleTests() ) return;

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
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "test-plugins-sdk-name-validation" );
        op.setProjectProvider( "ant" );

        final ValidationService vs = op.getPluginsSDKName().service( ValidationService.class );

        assertEquals( "ok", vs.validation().message() );
        assertEquals( "ok", op.getPluginsSDKName().validation().message() );

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
        assertEquals( "ok", vs.validation().message() );
        assertEquals( "ok", op.getPluginsSDKName().validation().message() );

    }

    protected void testPluginTypeListener() throws Exception
    {
        this.testPluginTypeListener( false );
    }

    protected void testPluginTypeListener( Boolean versionRestriction ) throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-plugin-type-listener" );
        final String projectName = op.getProjectName().content();
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
        if( shouldSkipBundleTests() ) return;

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
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "test-portlet-framework-possible-values" );
        op.setProjectProvider( "ant" );
        op.setPluginType( "portlet" );

        final Set<String> acturalFrameworks = op.getPortletFramework().service( PossibleValuesService.class ).values();

        assertNotNull( acturalFrameworks );

        Set<String> exceptedFrameworks = new HashSet<String>();
        exceptedFrameworks.add( "mvc" );
        exceptedFrameworks.add( "jsf-2.x" );
        exceptedFrameworks.add( "vaadin" );
        exceptedFrameworks.add( "spring_mvc" );

        assertNotNull( exceptedFrameworks );

        assertEquals( true, exceptedFrameworks.containsAll( acturalFrameworks ) );
        assertEquals( true, acturalFrameworks.containsAll( exceptedFrameworks ) );
    }

    @Test
    public void testPortletFrameworkValueLabel() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "test-portlet-framework-value-label" );

        final ValueLabelService vls = op.getPortletFramework().service( ValueLabelService.class );

        Set<String> acturalLables = new HashSet<String>();

        for( IPortletFramework pf : ProjectCore.getPortletFrameworks() )
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
        exceptedLables.add( "Spring MVC" );

        assertEquals( true, exceptedLables.containsAll( acturalLables ) );
        assertEquals( true, acturalLables.containsAll( exceptedLables ) );
    }

    @Test
    public void testServiceBuilderProjectName() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-sb" );

        op.setPluginType( PluginType.servicebuilder );
        op.setUseDefaultLocation( true );

        final IProject expectedProject = createAntProject( op );

        String expectedProjectName = expectedProject.getName();

        String actualProjectName = op.getProjectNames().get(0).getName().content();

        assertEquals( expectedProjectName, actualProjectName );
    }

    protected void testProjectNameValidation( final String initialProjectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op1 = newProjectOp( "" );
        op1.setUseDefaultLocation( true );

        ValidationService vs = op1.getProjectName().service( ValidationService.class );

        final String validProjectName = initialProjectName;

        op1.setProjectName( validProjectName );
        assertEquals( "ok", vs.validation().message() );
        assertEquals( "ok", op1.getProjectName().validation().message() );

        op1.setProjectName( validProjectName + "-portlet" );
        assertEquals( "ok", vs.validation().message() );
        assertEquals( "ok", op1.getProjectName().validation().message() );

        final IProject proj = createProject( op1 );

        op1.dispose();

        final NewLiferayPluginProjectOp op2 = newProjectOp( "" );
        vs = op2.getProjectName().service( ValidationService.class );

        op2.setProjectName( validProjectName + "-portlet" );
        assertEquals( "A project with that name already exists.", vs.validation().message() );
        assertEquals( "A project with that name already exists.", op2.getProjectName().validation().message() );

        op2.setProjectName( validProjectName );
        assertEquals( "A project with that name already exists.", vs.validation().message() );
        assertEquals( "A project with that name already exists.", op2.getProjectName().validation().message() );

        final IPath dotProjectLocation = proj.getLocation().append( ".project" );

        if( dotProjectLocation.toFile().exists() )
        {
            dotProjectLocation.toFile().delete();
        }

        op2.dispose();

        final NewLiferayPluginProjectOp op3 = newProjectOp( "" );
        vs = op3.getProjectName().service( ValidationService.class );

        op3.setProjectName( validProjectName );
        assertEquals( "A project with that name already exists.", vs.validation().message() );
        assertEquals( "A project with that name already exists.", op3.getProjectName().validation().message() );

        String invalidProjectName = validProjectName + "/";
        op3.setProjectName( invalidProjectName );
        assertEquals(
            "/ is an invalid character in resource name '" + invalidProjectName + "'.", vs.validation().message() );

//        invalidProjectName = validProjectName + ".";
//        op3.setProjectName( invalidProjectName );
//        assertEquals( "'" + invalidProjectName + "' is an invalid name on this platform.", vs.validation().message() );
//        assertEquals(
//            "'" + invalidProjectName + "' is an invalid name on this platform.",
//            op3.getProjectName().validation().message() );
    }

    @Test
    public void testProjectProviderValueLabel() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "test-project-provider-value-label" );

        final ValueLabelService vls = op.getProjectProvider().service( ValueLabelService.class );

        final Set<String> actualProviderShortNames =
            op.getProjectProvider().service( PossibleValuesService.class ).values();

        Set<String> actualLabels = new HashSet<String>();

        for( String shortName : actualProviderShortNames )
        {
            actualLabels.add( vls.provide( shortName ) );
        }

        assertNotNull( actualLabels );

        Set<String> exceptedLabels = new HashSet<String>();
        exceptedLabels.add( "Ant (liferay-plugins-sdk)" );
        exceptedLabels.add( "Maven (liferay-maven-plugin)" );

        assertEquals( true, exceptedLabels.containsAll( actualLabels ) );
    }

    @Test
    public void testRuntimeNameDefaultValue() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        removeAllRuntimes();

        setupPluginsSDKAndRuntime();

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

        assertEquals(
            true, op.getRuntimeName().service( DefaultValueService.class ).value().contains( originalRuntimeName ) );
        assertEquals( true, op.getRuntimeName().content().contains( originalRuntimeName ) );
    }

    @Test
    public void testRuntimeNamePossibleValues() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

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
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "test-runtime-name-validation" );
        op.setProjectProvider( "ant" );

        final ValidationService vs = op.getRuntimeName().service( ValidationService.class );

        final String newVersion = getRuntimeVersion() + ".0";

        op.setRuntimeName( newVersion );

        assertEquals( "Liferay runtime must be configured.", vs.validation().message() );
        // Value is not excepted.
        // assertEquals( "Liferay runtime must be configured.", op.getRuntimeName().validation().message() );
    }

    protected void testUseSdkLocationListener() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-use-sdk-location-listener" );
        final String projectName = op.getProjectName().content();
        op.setProjectProvider( "ant" );
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

    @Test
    public void testHookProjectName() throws Exception
    {
        final NewLiferayPluginProjectOp op = newProjectOp( "test-hook" );

        op.setPluginType( PluginType.hook );
        op.setUseDefaultLocation( true );

        final IProject expectedProject = createAntProject( op );

        String expectedProjectName = expectedProject.getName();

        String actualProjectName = op.getProjectNames().get(0).getName().content();

        assertEquals( expectedProjectName, actualProjectName );
    }

    @Test
    public void testIncludeSampleCode() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        // test portlet project
        NewLiferayPluginProjectOp op = newProjectOp("test-include-sample-code-portlet");

        op.setIncludeSampleCode( true );
        op.setPluginType( PluginType.portlet );

        IProject project = createAntProject( op );

        ILiferayProject lr = LiferayCore.create( project );

        IFile portletXml = lr.getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );
        IFile liferayPortletXml = lr.getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
        IFile liferayDisplayXml = lr.getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );

        assertEquals( 1, countElements( portletXml, "portlet" ) );
        assertEquals( 1, countElements( liferayPortletXml, "portlet" ) );
        assertEquals( 1, countElements( liferayDisplayXml, "category" ) );
    }

    @Test
    public void testIncludeSampleCodeServiceBuilder() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        // test service-builder project
        NewLiferayPluginProjectOp op = newProjectOp("test-include-sample-code-service-builder");

        op.setIncludeSampleCode( true );
        op.setPluginType( PluginType.servicebuilder );

        IProject project = createAntProject( op );
        ILiferayProject lr = LiferayCore.create( project );

        IFile portletXml = lr.getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );
        IFile liferayPortletXml = lr.getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
        IFile liferayDisplayXml = lr.getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );
        IFile serviceXml = lr.getDescriptorFile( ILiferayConstants.SERVICE_XML_FILE );

        assertEquals( 1, countElements( portletXml, "portlet" ) );
        assertEquals( 1, countElements( liferayPortletXml, "portlet" ) );
        assertEquals( 1, countElements( liferayDisplayXml, "category" ) );
        assertEquals( 1, countElements( serviceXml, "entity" ) );
    }

    @Test
    public void testDontIncludeSampleCode() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        // test portlet project
        NewLiferayPluginProjectOp op = newProjectOp("test-dont-include-sample-code-portlet");

        op.setIncludeSampleCode( false );
        op.setPluginType( PluginType.portlet );

        IProject project = createAntProject( op );

        ILiferayProject lr = LiferayCore.create( project );
        IFile portletXml = lr.getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );
        IFile liferayPortletXml = lr.getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
        IFile liferayDisplayXml = lr.getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );

        assertEquals( 0, countElements( portletXml, "portlet" ) );
        assertEquals( 0, countElements( liferayPortletXml, "portlet" ) );
        assertEquals( 0, countElements( liferayDisplayXml, "category" ) );
    }

    @Test
    public void testDontIncludeSampleCodeServiceBuilder() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        // test service-builder project
        NewLiferayPluginProjectOp op = newProjectOp("test-dont-include-sample-code-service-builder");

        op.setIncludeSampleCode( false );
        op.setPluginType( PluginType.servicebuilder );

        IProject project = createAntProject( op );

        ILiferayProject lr = LiferayCore.create( project );
        IFile portletXml = lr.getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );
        IFile liferayPortletXml = lr.getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
        IFile liferayDisplayXml = lr.getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );
        IFile serviceXml = lr.getDescriptorFile( ILiferayConstants.SERVICE_XML_FILE );

        assertEquals( 0, countElements( portletXml, "portlet" ) );
        assertEquals( 0, countElements( liferayPortletXml, "portlet" ) );
        assertEquals( 0, countElements( liferayDisplayXml, "category" ) );
        assertEquals( 0, countElements( serviceXml, "entity" ) );
    }

    protected int countElements( IFile file, String elementName ) throws Exception
    {
        final IDOMModel domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead( file );
        final IDOMDocument document = domModel.getDocument();

        final int count = document.getElementsByTagName( elementName ).getLength();

        domModel.releaseFromRead();

        return count;
    }

}
