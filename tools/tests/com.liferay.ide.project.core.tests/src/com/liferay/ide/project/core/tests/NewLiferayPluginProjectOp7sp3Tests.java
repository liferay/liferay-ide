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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.operation.INewLayoutTplDataModelProperties;
import com.liferay.ide.layouttpl.core.operation.LayoutTplDescriptorHelper;
import com.liferay.ide.layouttpl.core.operation.NewLayoutTplDataModelProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Terry Jia
 */
public class NewLiferayPluginProjectOp7sp3Tests extends NewLiferayPluginProjectOpBase
{

    @Override
    protected IPath getLiferayPluginsSdkDir()
    {
        return ProjectCore.getDefault().getStateLocation().append(
            "com.liferay.portal.plugins.sdk-1.0.16-withdependencies" );
    }

    @Override
    protected IPath getLiferayPluginsSDKZip()
    {
        return getLiferayBundlesPath().append(
            "com.liferay.portal.plugins.sdk-1.0.16-withdependencies.zip" );
    }

    @Override
    protected String getLiferayPluginsSdkZipFolder()
    {
        return "com.liferay.portal.plugins.sdk-1.0.16-withdependencies/";
    }

    @AfterClass
    public static void removePluginsSDK() throws Exception
    {
        deleteAllWorkspaceProjects();
    }

    @Override
	protected IPath getLiferayRuntimeDir()
    {
        return ProjectCore.getDefault().getStateLocation().append( "liferay-ce-portal-7.0-ga5/tomcat-8.0.32" );
    }

    @Override
	protected IPath getLiferayRuntimeZip()
    {
        return getLiferayBundlesPath().append( "liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip" );
    }

    @Override
	public String getRuntimeVersion()
    {
        return "7.0.2";
    }

    @Override
    protected String getServiceXmlDoctype()
    {
        return "service-builder PUBLIC \"-//Liferay//DTD Service Builder 7.0.0//EN\" \"http://www.liferay.com/dtd/liferay-service-builder_7_0_0.dtd";
    }

    @Override
    @Test
    @Ignore
    public void testLocationListener() throws Exception
    {
    }

    @Override
    @Test
    @Ignore
    public void testNewJsfRichfacesProjects() throws Exception
    {
    }

    @Override
    @Test
    public void testNewLayoutAntProject() throws Exception
    {
    	final String projectName = "test-layouttpl-project-sdk";
		final NewLiferayPluginProjectOp op = newProjectOp(projectName);
		op.setPluginType(PluginType.layouttpl);

		IProject layouttplProject = createAntProject(op);

		final IFolder webappRoot = LiferayCore.create(IWebProject.class, layouttplProject).getDefaultDocrootFolder();

		assertNotNull(webappRoot);

		final IFile layoutXml = webappRoot.getFile("WEB-INF/liferay-layout-templates.xml");

		assertEquals(true, layoutXml.exists());

		final IFile wapTpl = webappRoot.getFile("test_layouttpl_project_sdk_7.0.2.wap.tpl");

		assertFalse(wapTpl.exists());

		final IDataModel model = DataModelFactory.createDataModel(new NewLayoutTplDataModelProvider());

		model.setProperty(INewLayoutTplDataModelProperties.LAYOUT_TEMPLATE_ID, "newtemplate");
		model.setProperty(INewLayoutTplDataModelProperties.LAYOUT_TEMPLATE_NAME, "New Template");
		model.setProperty(INewLayoutTplDataModelProperties.LAYOUT_THUMBNAIL_FILE, "/newtemplate.png");
		model.setProperty(INewLayoutTplDataModelProperties.LAYOUT_WAP_TEMPLATE_FILE, "/newtemplate.wap.tpl");
		model.setProperty(INewLayoutTplDataModelProperties.LAYOUT_TEMPLATE_FILE, "/newtemplate.tpl");

		LayoutTplDescriptorHelper layoutHelper = new LayoutTplDescriptorHelper(layouttplProject);

		layoutHelper.addNewLayoutTemplate(model);

		final String contents = CoreUtil.readStreamToString(layoutXml.getContents(true));

		assertFalse(contents.contains("<wap-template-path>"));
    }

    @Test
    @Ignore
    public void testNewProjectCustomLocationPortlet() throws Exception
    {
    }

    @Test
    @Ignore
    public void testNewProjectCustomLocationWrongSuffix() throws Exception
    {
    }

    @Test
    @Ignore
    public void testNewSDKProjectCustomLocation() throws Exception
    {
    }

    @Test
    @Ignore
    public void testNewSDKProjectEclipseWorkspace() throws Exception
    {
    }

    @Override
    @Test
    @Ignore
    public void testNewSDKProjectInSDK() throws Exception
    {
    }

    @Ignore
    @Override
    @Test
    public void testNewThemeProjects() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        super.testNewThemeProjects();
    }

    @Override
	@Test
    @Ignore
    public void testNewJsfAntProjects() throws Exception
    {
    }

    @Override
    @Test
    @Ignore
    public void testPluginTypeListener() throws Exception
    {
    }

    @Test
    @Ignore
    public void testProjectNameValidation() throws Exception
    {
    }

    @Override
    @Test
    @Ignore
    public void testNewSDKProjects() throws Exception
    {
    }

    @Test
    @Ignore
    public void testNewWebAntProjectValidation() throws Exception
    {
    }

    @Test
    public void testNewExtAntProjectNotSupportedWithWorkspaceSDK() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final String projectName = "test-ext-project-sdk";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );

        op.setPluginType( PluginType.ext );
        op.setSdkLocation( PathBridge.create( getLiferayPluginsSdkDir() ) );
        Status validation = op.validation();
        assertEquals( true, validation.ok() );

        final IProject extProject = createAntProject( op );

        assertNotNull( extProject );
    }

    @BeforeClass
    public static void removeAllProjects() throws Exception
    {
        IProgressMonitor monitor = new NullProgressMonitor();

        for( IProject project : CoreUtil.getAllProjects() )
        {
            project.delete( true, monitor );

            assertFalse( project.exists() );
        }
    }

}
