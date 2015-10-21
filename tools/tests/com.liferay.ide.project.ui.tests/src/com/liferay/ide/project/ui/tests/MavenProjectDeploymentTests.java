/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.project.ui.tests;

import static org.junit.Assert.assertTrue;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Li Lu
 */
public class MavenProjectDeploymentTests extends ProjectUITestBase
{

    final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
    private IProject project;

    @Before
    public void start() throws Exception
    {
        super.startServer();
    }

    @AfterClass
    public static void cleanUp() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    @Test
    public void testDeployMVCPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "MVCPortletMavenProjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "mvc" );
        op.setIncludeSampleCode( true );

        project = createMavenProject( op );

        Thread.sleep(2000);
        addProjectToServer( project );

        String expectedMessage = "1 portlet for MVCPortletMavenProjectTest-portlet is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeployJSFPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "JSFPortletMavenProjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "jsf-2.x" );
        op.setIncludeSampleCode( true );

        project = createMavenProject( op );

        addProjectToServer( project );

        String expectedMessage = "1 portlet for JSFPortletMavenProjectTest-portlet is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeploySpringMVCPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "SpringMVCPortletMavenProjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "spring-mvc" );
        op.setIncludeSampleCode( true );

        project = createMavenProject( op );

        addProjectToServer( project );

        String expectedMessage = "1 portlet for SpringMVCPortletMavenProjectTest-portlet is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeployVaadinPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "VaadinPortletMavenProjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "vaadin" );
        op.setIncludeSampleCode( true );
        
        project = createMavenProject( op );

        File liferayDisplayXML = LiferayCore.create( project ).getDescriptorFile( "liferay-display.xml" ).getRawLocation().toFile();
        FileUtil.searchAndReplace( liferayDisplayXML, "7.4.0", "6.2.0" );
        
        File liferayPortletXML = LiferayCore.create( project ).getDescriptorFile( "liferay-portlet.xml" ).getRawLocation().toFile();
        FileUtil.searchAndReplace( liferayPortletXML, "7.4.0", "6.2.0" );
        
        project.refreshLocal( IResource.DEPTH_INFINITE, null );
        
        Thread.sleep( 3000 );
        addProjectToServer( project );

        String expectedMessage = "1 portlet for VaadinPortletMavenProjectTest-portlet is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeployHookPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "HookMavenProjectTest" );
        op.setPluginType( PluginType.hook );
        project = createMavenProject( op );

        addProjectToServer( project );

        String expectedMessage = "Hook for HookMavenProjectTest-hook is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeployServiceBuilderPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ServiceBuilderMavenProjectTest" );
        op.setPluginType( PluginType.servicebuilder );
        op.setIncludeSampleCode( true );

        createMavenProject( op );

        project = ProjectUtil.getProject( "ServiceBuilderMavenProjectTest-portlet" );
        addProjectToServer( project );

        String expectedMessage = "1 portlet for ServiceBuilderMavenProjectTest-portlet is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeployThemeProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ThemeMavenProjectTest" );
        op.setPluginType( PluginType.theme );

        project = createMavenProject( op );

        Thread.sleep( 3000 );
        addProjectToServer( project );

        String expectedMessage = "1 theme for ThemeMavenProjectTest-theme is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    public void testLayoutProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "LayoutMavenProjectTest" );
        op.setPluginType( PluginType.layouttpl );

        project = createMavenProject( op );

        addProjectToServer( project );

        String expectedMessage = "1 layout template for LayoutMavenProjectTest-layouttpl is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }
    
    @Test
    public void testExtProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ExtMavenPorjectTest" );
        op.setPluginType( PluginType.ext );

        project = createMavenProject( op );

        addProjectToServer( project );
    }
}
