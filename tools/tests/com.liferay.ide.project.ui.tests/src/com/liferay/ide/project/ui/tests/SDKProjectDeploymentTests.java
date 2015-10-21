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

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.ui.IvyUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Li Lu
 */
public class SDKProjectDeploymentTests extends ProjectUITestBase
{

    final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

    private IProject project;

    @AfterClass
    public static void cleanUp() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    @Before
    public void startUp() throws Exception
    {
        super.startServer();
    }

    @Override
    protected IProject createAntProject( NewLiferayPluginProjectOp op ) throws Exception
    {
        project = super.createAntProject( op );

        IvyUtil.configureIvyProject( project, new NullProgressMonitor() );

        return project;
    }

    @Test
    public void testDeployHookPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "HookPortletPorjectTest" );
        op.setPluginType( PluginType.hook );
        project = createAntProject( op );

        addProjectToServer( project );

        String expectedMessage = "Hook for " + project.getName() + " is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeployJSFPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "JSFPortletPorjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "jsf-2.x" );
        op.setIncludeSampleCode( true );

        project = createAntProject( op );

        Thread.sleep( 2000 );
        addProjectToServer( project );

        String expectedMessage = "1 portlet for " + project.getName() + " is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeployMVCPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "MVCPortletPorjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "mvc" );
        op.setIncludeSampleCode( true );

        project = createAntProject( op );

        addProjectToServer( project );

        String expectedMessage = "1 portlet for " + project.getName() + " is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeployServiceBuilderPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ServiceBuilderPorjectTest" );
        op.setPluginType( PluginType.servicebuilder );
        op.setIncludeSampleCode( true );

        project = createAntProject( op );

        addProjectToServer( project );

        String expectedMessage = "1 portlet for " + project.getName() + " is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeploySpringMVCPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "SpringMVCPortletPorjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "spring-mvc" );
        op.setIncludeSampleCode( true );

        project = createAntProject( op );

        addProjectToServer( project );

        String expectedMessage = "1 portlet for " + project.getName() + " is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeployThemeProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ThemePorjectTest" );
        op.setPluginType( PluginType.theme );
        project = createAntProject( op );

        Thread.sleep( 5000 );
        addProjectToServer( project );

        String expectedMessage = "1 theme for " + project.getName() + " is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testDeployVaadinPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "VaadinPortletPorjectTest" );
        op.setPluginType( PluginType.portlet );
        op.setPortletFramework( "vaadin" );
        op.setIncludeSampleCode( true );

        project = createAntProject( op );

        addProjectToServer( project );

        String expectedMessage = "1 portlet for " + project.getName() + " is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }

    @Test
    public void testExtProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "ExtPorjectTest" );
        op.setPluginType( PluginType.ext );

        project = createAntProject( op );

        addProjectToServer( project );
        publishToServer( project );

        String expectedMessage = "Server startup in";
        Thread.sleep( 50000 );
        assertTrue( checkConsoleMessage( expectedMessage ) );
        Thread.sleep( 10000 );
    }

    @Test
    public void testLayoutProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        op.setProjectName( "LayoutPorjectTest" );
        op.setPluginType( PluginType.layouttpl );
        project = createAntProject( op );

        addProjectToServer( project );

        String expectedMessage = "1 layout template for " + project.getName() + " is available for use";
        assertTrue( checkConsoleMessage( expectedMessage ) );
    }
}
