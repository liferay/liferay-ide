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

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class NewLiferayPluginProjectPortletNameOpTests extends ProjectCoreBase
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
	public static void removePluginsSDK() throws CoreException {
		IProject[] projects = CoreUtil.getAllProjects();

		for (IProject project : projects) {
			if (project != null && project.isAccessible() && project.exists()) {
				try {
					project.close(new NullProgressMonitor());
					project.delete(true, new NullProgressMonitor());
				}
				catch (ResourceException re) {
					project.close(new NullProgressMonitor());
					project.delete(true, new NullProgressMonitor());
				}
			}
		}
	}

    protected IProject createNewJSFPortletProjectCustomPortletName(
        final String jsfSuite, String suffix, String customPortletName ) throws Exception
    {
        final String projectName = "test-" + jsfSuite + suffix + "-sdk-project";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setIncludeSampleCode( true );
        op.setPortletFramework( "jsf-2.x" );
        op.setPortletFrameworkAdvanced( "liferay_faces_alloy" );
        op.setPortletName( customPortletName );
        final IProject jsfProject = createAntProject( op );

        final IFolder webappRoot = LiferayCore.create( IWebProject.class, jsfProject ).getDefaultDocrootFolder();

        assertNotNull( webappRoot );

        final IFile config = webappRoot.getFile( "WEB-INF/faces-config.xml" );

        assertEquals( true, config.exists() );

        return jsfProject;
    }

    protected IProject createNewMVCPortletProjectCustomPortletName( String customPortletName ) throws Exception
    {
        final String projectName = "test-mvc-sdk-project";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setIncludeSampleCode( true );
        op.setPortletFramework( "mvc" );
        op.setPortletName( customPortletName );

        final IProject mvcProject = createAntProject( op );

        assertNotNull( LiferayCore.create( IWebProject.class, mvcProject ).getDefaultDocrootFolder() );

        return mvcProject;
    }

    @Test
    public void testNewJSFPortletProjectPortletName() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final String jsfSuite = "jsf-2.x";
        final String suffix = "";
        final String customPortletName = "test111";

        IProject jsfProject = createNewJSFPortletProjectCustomPortletName( jsfSuite, suffix, customPortletName );

        final IFolder defaultDocroot = LiferayCore.create( IWebProject.class, jsfProject ).getDefaultDocrootFolder();

        final IFile portletXml = defaultDocroot.getFile( "WEB-INF/portlet.xml" );

        assertEquals( true, portletXml.exists() );

        final String portletXmlContent = CoreUtil.readStreamToString( portletXml.getContents() );

        assertEquals( true, portletXmlContent.contains( customPortletName ) );

        final IFile liferayPortletXml = defaultDocroot.getFile( "WEB-INF/liferay-portlet.xml" );

        assertEquals( true, liferayPortletXml.exists() );

        final String liferayPortletXmlContent = CoreUtil.readStreamToString( liferayPortletXml.getContents() );

        assertEquals( true, liferayPortletXmlContent.contains( customPortletName ) );

        final IFile liferayDisplayXml = defaultDocroot.getFile( "WEB-INF/liferay-display.xml" );

        assertEquals( true, liferayDisplayXml.exists() );

        final String liferayDisplayXmlContent = CoreUtil.readStreamToString( liferayDisplayXml.getContents() );

        assertEquals( true, liferayDisplayXmlContent.contains( customPortletName ) );
    }

    @Test
    public void testNewMVCPortletProjectCustomPortletName() throws Exception
    {
        if( shouldSkipBundleTests() )
            return;

        final String customPortletName = "test111";

        IProject jsfProject = createNewMVCPortletProjectCustomPortletName( customPortletName );

        final IFolder docroot = LiferayCore.create( IWebProject.class, jsfProject ).getDefaultDocrootFolder();

        final IFile portletXml = docroot.getFile( "WEB-INF/portlet.xml" );

        assertEquals( true, portletXml.exists() );

        final String portletXmlContent = CoreUtil.readStreamToString( portletXml.getContents() );

        assertEquals( true, portletXmlContent.contains( customPortletName ) );

        final IFile liferayPortletXml = docroot.getFile( "WEB-INF/liferay-portlet.xml" );

        assertEquals( true, liferayPortletXml.exists() );

        final String liferayPortletXmlContent = CoreUtil.readStreamToString( liferayPortletXml.getContents() );

        assertEquals( true, liferayPortletXmlContent.contains( customPortletName ) );

        final IFile liferayDisplayXml = docroot.getFile( "WEB-INF/liferay-display.xml" );

        assertEquals( true, liferayDisplayXml.exists() );

        final String liferayDisplayXmlContent = CoreUtil.readStreamToString( liferayDisplayXml.getContents() );

        assertEquals( true, liferayDisplayXmlContent.contains( customPortletName ) );
    }
}
