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

package com.liferay.ide.portlet.ui.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.junit.Before;
import org.junit.Test;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.service.core.operation.INewServiceBuilderDataModelProperties;
import com.liferay.ide.service.ui.wizard.NewServiceBuilderWizard;

/**
 * @author Li Lu
 */
@SuppressWarnings( "restriction" )
public class NewServiceBuilderWizardTests extends PortletUITestBase implements INewServiceBuilderDataModelProperties
{

    NewServiceBuilderWizard wizard;
    IProject project;
    IDataModel dataModel;
    IFile serviceFile;

    @Before
    public void createPortletProject() throws Exception
    {
        if( shouldSkipBundleTests() )return;

        project = createProject( "service-builder-portlet-project-test", PluginType.portlet, "mvc" );
        wizard = new NewServiceBuilderWizard();
        dataModel = wizard.getDataModel();

        // delete service.xml
        if( getServiceFile().exists() )
        {
            serviceFile.delete( true, new NullProgressMonitor() );
        }
    }

    public IFile getServiceFile()
    {
        IFolder defaultDocroot = CoreUtil.getDefaultDocrootFolder( project );
        serviceFile = defaultDocroot.getFile( "WEB-INF/service.xml" );

        return serviceFile;
    }

    @Test
    public void testContentDefaultValues()
    {
        dataModel.setProperty( PROJECT_NAME, project.getName() );

        assertEquals( "service.xml", dataModel.getProperty( SERVICE_FILE ) );
        assertNull( dataModel.getDefaultProperty( PACKAGE_PATH ) );
        assertNull( dataModel.getDefaultProperty( NAMESPACE ) );
        assertEquals( System.getProperty( "user.name" ), dataModel.getDefaultProperty( AUTHOR ) );
        assertEquals( true, dataModel.getDefaultProperty( USE_SAMPLE_TEMPLATE ) );
        assertEquals( false, wizard.canFinish() );
    }

    @Test
    public void testPluginProject()
    {
        dataModel.setProperty( PACKAGE_PATH, "com.test" );
        dataModel.setProperty( NAMESPACE, "foo" );

        String message = dataModel.validate().getMessage();
        assertEquals( "Enter a project name.", message );

        dataModel.setProperty( PROJECT_NAME, project.getName() );
    }

    @Test
    public void testPackagePath() throws Exception
    {
        assertEquals( true, dataModel.isPropertyEnabled( PACKAGE_PATH ) );

        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( NAMESPACE, "foo" );

        dataModel.setProperty( PACKAGE_PATH, "   " );
        String message = dataModel.validateProperty( PACKAGE_PATH ).getMessage();
        assertEquals( "Package path cannot be empty.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( PACKAGE_PATH, "test test" );
        message = dataModel.validateProperty( PACKAGE_PATH ).getMessage();
        assertEquals( "Invalid Java package name: 'test test' is not a valid Java identifier", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( PACKAGE_PATH, "com.test" );
        assertEquals( true, wizard.canFinish() );
    }

    @Test
    public void testNamespace()
    {
        assertEquals( true, dataModel.isPropertyEnabled( NAMESPACE ) );

        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( PACKAGE_PATH, "com.test" );

        dataModel.setProperty( NAMESPACE, "" );
        String message = dataModel.validateProperty( NAMESPACE ).getMessage();
        assertEquals( "Namespace cannot be empty.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( NAMESPACE, "test.test" );
        message = dataModel.validateProperty( NAMESPACE ).getMessage();
        assertEquals( "The namespace element must be a valid keyword.", message );
        assertEquals( false, wizard.canFinish() );

        dataModel.setProperty( NAMESPACE, "test_test" );
        message = dataModel.validate().getMessage();
        assertEquals( true, wizard.canFinish() );
    }

    @Test
    public void testIncludeSample() throws Exception
    {
        assertEquals( true, dataModel.isPropertyEnabled( USE_SAMPLE_TEMPLATE ) );
    }

    @Test
    public void testServiceFile() throws Exception
    {
        assertEquals( false, dataModel.isPropertyEnabled( SERVICE_FILE ) );
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( PACKAGE_PATH, "com.test" );
        dataModel.setProperty( NAMESPACE, "foo" );

        assertEquals( true, dataModel.isPropertyEnabled( USE_SAMPLE_TEMPLATE ) );
        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( project, "service.xml", "WEB-INF" ) );

        dataModel = wizard.getDataModel();
        dataModel.setProperty( PROJECT_NAME, project.getName() );
        dataModel.setProperty( PACKAGE_PATH, "com.test" );
        dataModel.setProperty( NAMESPACE, "foo" );

        String message = dataModel.validate().getMessage();
        assertEquals( "Project already contains service.xml file, please select another project.", message );
        assertEquals( false, wizard.canFinish() );

        // select another project
        IProject anotherProject = createProject( "another-portlet-project-test", PluginType.portlet, "mvc" );
        dataModel.setProperty( PROJECT_NAME, anotherProject.getName() );
        message = dataModel.validate().getMessage();
        assertEquals( "OK", message );
        assertEquals( true, wizard.canFinish() );

        dataModel.getDefaultOperation().execute( new NullProgressMonitor(), null );
        assertEquals( true, checkFileExist( anotherProject, "service.xml", "WEB-INF" ) );

        // check file content
        IFile serviceFile = getServiceFile();
        assertEquals( true, checkFileHasContent( serviceFile, "service-builder", "package-path", "com.test" ) );
        assertEquals( true, checkFileHasContent( serviceFile, "author", null, System.getProperty( "user.name" ) ) );
        assertEquals( true, checkFileHasContent( serviceFile, "namespace", null, "foo" ) );
        assertEquals( true, checkFileHasContent( serviceFile, "entity", "name", "Foo" ) );
    }
}
