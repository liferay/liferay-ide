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

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.operation.INewLayoutTplDataModelProperties;
import com.liferay.ide.layouttpl.core.operation.LayoutTplDescriptorHelper;
import com.liferay.ide.layouttpl.core.operation.NewLayoutTplDataModelProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewLiferayPluginProjectOp625Tests extends NewLiferayPluginProjectOpBase
{

    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    @Override
    protected String getServiceXmlDoctype()
    {
        return "service-builder PUBLIC \"-//Liferay//DTD Service Builder 6.2.0//EN\" \"http://www.liferay.com/dtd/liferay-service-builder_6_2_0.dtd";
    }

    @Override
    @Test
    @Ignore
    public void testLocationListener() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testLocationListener();
    }

    @Override
    @Test
    @Ignore
    public void testNewJsfRichfacesProjects() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewJsfRichfacesProjects();
    }

    @Override
    @Test
    public void testNewLayoutAntProject() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        String projectName = "test-layouttpl-project-sdk";
        NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setPluginType( PluginType.layouttpl );

        IProject layouttplProject = createAntProject( op );

        IFolder webappRoot = LiferayCore.create( IWebProject.class, layouttplProject ).getDefaultDocrootFolder();

        assertNotNull( webappRoot );

        IFile layoutXml = webappRoot.getFile( "WEB-INF/liferay-layout-templates.xml" );

        assertEquals( true, layoutXml.exists() );

        IFile wapTpl = webappRoot.getFile("test_layouttpl_project_sdk_6.2.5.wap.tpl");

        assertTrue(wapTpl.exists());

        IDataModel model = DataModelFactory.createDataModel( new NewLayoutTplDataModelProvider() );

        model.setProperty(INewLayoutTplDataModelProperties.LAYOUT_TEMPLATE_ID, "newtemplate");
        model.setProperty(INewLayoutTplDataModelProperties.LAYOUT_TEMPLATE_NAME, "New Template");
        model.setProperty(INewLayoutTplDataModelProperties.LAYOUT_THUMBNAIL_FILE, "/newtemplate.png");
        model.setProperty(INewLayoutTplDataModelProperties.LAYOUT_WAP_TEMPLATE_FILE, "/newtemplate.wap.tpl");
        model.setProperty(INewLayoutTplDataModelProperties.LAYOUT_TEMPLATE_FILE, "/newtemplate.tpl");

        LayoutTplDescriptorHelper layoutHelper = new LayoutTplDescriptorHelper(layouttplProject);

        layoutHelper.addNewLayoutTemplate(model);

        String contents = CoreUtil.readStreamToString( layoutXml.getContents( true ) );

        assertTrue(contents.contains("<wap-template-path>"));
    }

    @Test
    @Ignore
    public void testNewProjectCustomLocationPortlet() throws Exception
    {
        // not supported in 6.2.3
    }

    @Test
    @Ignore
    public void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
        // not supported in 6.2.3
    }

    @Test
    @Ignore
    public void testNewSDKProjectCustomLocation() throws Exception
    {
        // not supported in 6.2.3
    }

    @Test
    @Ignore
    public void testNewSDKProjectEclipseWorkspace() throws Exception
    {
        // not supported in 6.2.3
    }

    @Override
    @Test
    public void testNewSDKProjectInSDK() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewSDKProjectInSDK();
    }

    @Override
    @Test
    public void testNewThemeProjects() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testNewThemeProjects();
    }

    @Override
    @Test
    public void testPluginTypeListener() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testPluginTypeListener( true );
    }

    @Test
    public void testProjectNameValidation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        super.testProjectNameValidation( "project-name-validation-623" );
    }

    @Override
    @Test
    public void testNewSDKProjects() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        createAntProject( newProjectOp( "test-name-1" ) );
        createAntProject( newProjectOp( "test_name_1" ) );
        createAntProject( newProjectOp( "-portlet-portlet" ) );
        createAntProject( newProjectOp( "-portlet-hook" ) );

        final NewLiferayPluginProjectOp op = newProjectOp( "-hook-hook" );
        op.setPluginType( PluginType.hook );
        createAntProject( op );
    }

    @Test
    public void testNewWebAntProjectValidation() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        final String projectName = "test-web-project-sdk";

        final NewLiferayPluginProjectOp op = newProjectOp( projectName );

        op.setPluginType( PluginType.web );

        op.setSdkLocation( PathBridge.create( getLiferayPluginsSdkDir() ) );

        assertEquals(
            "The selected Plugins SDK does not support creating new web type plugins. Please configure version 7.0 or greater.",
            op.getSdkLocation().validation().message() );
    }

    @Test
    public void testProjectNameValidationServiceAfterProjectCreated() throws Exception
    {
        if( shouldSkipBundleTests() ) return;

        // test service-builder project
        NewLiferayPluginProjectOp opCreateProjectA = newProjectOp("test-project-name");

        opCreateProjectA.setIncludeSampleCode( false );
        opCreateProjectA.setPluginType( PluginType.portlet );

        createAntProject( opCreateProjectA );

        Status projectNameAValidationResult = opCreateProjectA.getProjectName().validation();

        assertEquals(true, projectNameAValidationResult.ok());

        NewLiferayPluginProjectOp opCreateProjectB = newProjectOp("test-project-name");
        Status projectNameBValidationResult = opCreateProjectB.getProjectName().validation();
        assertEquals(false, projectNameBValidationResult.ok());
     }
}
