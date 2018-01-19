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
import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

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
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.services.ValidationService;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Terry Jia
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

    protected void checkNewJsfAntProjectXHtmlPagesLocation( IProject jsfProject ) throws Exception
    {
        final IFolder docroot = CoreUtil.getDefaultDocrootFolder( jsfProject );
        final IFolder views = docroot.getFolder( "/WEB-INF/views" );

        assertEquals( true, views.exists() );

        final IFolder oldViews = docroot.getFolder( "/views" );

        assertEquals( false, oldViews.exists() );

        final String contents =
            CoreUtil.readStreamToString( docroot.getFile( "/WEB-INF/portlet.xml" ).getContents( true ) );

        if( contents.contains( "init-param" ) )
        {
            assertEquals( true, contents.contains( "/WEB-INF/views/view.xhtml" ) );
        }
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
            CoreUtil.readStreamToString(
                this.getClass().getResourceAsStream( "files/" + getRuntimeVersion() + "/" + expectedBuildFile ) ).replaceAll(
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

        checkNewJsfAntProjectXHtmlPagesLocation( jsfProject );

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
    @Ignore
    public void testNewExtAntProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "ext" );
        op.setPluginType( PluginType.ext );

        IProject extProject = null;

        try
        {
            extProject = createAntProject( op );
        }
        catch( Throwable e )
        {
        }

        assertNotNull( extProject );

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

    protected void testNewSDKProjectInSDK() throws Exception
    {
        final IProject projectInSDK = createAntProject( newProjectOp( "test-project-in-sdk" ) );

        assertNotNull( projectInSDK );

        assertEquals( true, projectInSDK.exists() );

        final SDK sdk = SDKUtil.getWorkspaceSDK();

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
        if( shouldSkipBundleTests() ) return;

        final NewLiferayPluginProjectOp op = newProjectOp( "test-sb" );

        op.setPluginType( PluginType.servicebuilder );
        op.setUseDefaultLocation( true );

        final IProject expectedProject = createAntProject( op );

        String expectedProjectName = expectedProject.getName();

        String actualProjectName = op.getProjectNames().get(0).getName().content();

        assertEquals( expectedProjectName, actualProjectName );
    }

    @Test
    public void testSDKLocationValidation() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        NewLiferayPluginProjectOp op = newProjectOp( "test-sdk" );

        op.setProjectProvider( "ant" );

        Status status = op.execute( new ProgressMonitor() );

        if( !status.ok() )
        {
            throw new Exception( status.exception() );
        }

        SDK sdk = SDKUtil.getWorkspaceSDK();

        IPath sdkLocation = sdk.getLocation();

        if( sdk != null )
        {
            CoreUtil.getWorkspaceRoot().getProject( sdk.getName() ).delete( false, false, null );
        }

        // set existed project name
        IProject project = getProject( "portlets", "test-sdk-" + getRuntimeVersion() + "-portlet" );
        project.delete( false, false, null );
        op.setSdkLocation( sdkLocation.toOSString() );
        assertTrue( op.validation().message().contains( "is not valid because a project already exists at that location." ) );

        op = newProjectOp( "test2-sdk" );

        op.setSdkLocation( "" );
        assertEquals( "This sdk location is empty.", op.validation().message() );

        op.setSdkLocation( sdk.getLocation().getDevice() + "/" );
        assertEquals( "This sdk location is not correct.", op.validation().message() );

        // sdk has no build.USERNAME.properties file
        sdkLocation.append( "build." + System.getenv().get( "USER" ) + ".properties" ).toFile().delete();
        sdkLocation.append( "build." + System.getenv().get( "USERNAME" ) + ".properties" ).toFile().delete();
        op.setSdkLocation( sdkLocation.toOSString() );

        String expectedMessageRegx = ".*app.server.*";
        assertTrue( op.validation().message().matches( expectedMessageRegx ) );

        assertEquals( false, op.validation().ok() );
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

        op3.dispose();
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
    public void testHookProjectName() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

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

        IWebProject webProject = LiferayCore.create(IWebProject.class, project);

        IFile portletXml = webProject.getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );
        IFile liferayPortletXml = webProject.getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
        IFile liferayDisplayXml = webProject.getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );

        assertNotNull( portletXml );
        assertNotNull( liferayPortletXml );
        assertNotNull( liferayDisplayXml );

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
        IWebProject webProject = LiferayCore.create(IWebProject.class, project);

        IFile portletXml = webProject.getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );
        IFile liferayPortletXml = webProject.getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
        IFile liferayDisplayXml = webProject.getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );
        IFile serviceXml = webProject.getDescriptorFile( ILiferayConstants.SERVICE_XML_FILE );

        assertNotNull( portletXml );
        assertNotNull( liferayPortletXml );
        assertNotNull( liferayDisplayXml );
        assertNotNull( serviceXml );

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

        IWebProject webProject = LiferayCore.create(IWebProject.class, project);

        IFile portletXml = webProject.getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );
        IFile liferayPortletXml = webProject.getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
        IFile liferayDisplayXml = webProject.getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );

        assertNotNull( portletXml );
        assertNotNull( liferayPortletXml );
        assertNotNull( liferayDisplayXml );

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

        IWebProject webProject = LiferayCore.create(IWebProject.class, project);

        IFile portletXml = webProject.getDescriptorFile( ILiferayConstants.PORTLET_XML_FILE );
        IFile liferayPortletXml = webProject.getDescriptorFile( ILiferayConstants.LIFERAY_PORTLET_XML_FILE );
        IFile liferayDisplayXml = webProject.getDescriptorFile( ILiferayConstants.LIFERAY_DISPLAY_XML_FILE );
        IFile serviceXml = webProject.getDescriptorFile( ILiferayConstants.SERVICE_XML_FILE );

        assertNotNull( portletXml );
        assertNotNull( liferayPortletXml );
        assertNotNull( liferayDisplayXml );
        assertNotNull( serviceXml );

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
