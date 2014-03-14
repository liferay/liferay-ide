/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.junit.Test;

/**
 * @author Simon Jiang
 */
public class NewLiferayPluginProjectPortletNameOpTests extends ProjectCoreBase
{
    protected IProject createNewJSFPortletProjectCustomPortletName(
        final String  jsfSuite, String suffix, String customPortletName ) throws Exception
    {
        final String projectName = "test-" + jsfSuite + suffix + "-sdk-project";
        final NewLiferayPluginProjectOp op = newProjectOp( projectName );
        op.setIncludeSampleCode( true );
        op.setPortletFramework( "jsf-2.x" );
        op.setPortletFrameworkAdvanced( "liferay_faces_alloy" );
        op.setPortletName( customPortletName );
        final IProject jsfProject = createAntProject( op );

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( jsfProject );

        assertNotNull( webappRoot );

        final IVirtualFile config = webappRoot.getFile( "WEB-INF/faces-config.xml" );

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

        final IVirtualFolder webappRoot = CoreUtil.getDocroot( mvcProject );

        assertNotNull( webappRoot );

        return mvcProject;
    }

    @Test
    public void testNewJSFPortletProjectPortletName() throws Exception
    {
        final String jsfSuite = "jsf-2.x";
        final String suffix = "";
        final String customPortletName = "test111";

        IProject jsfProject = createNewJSFPortletProjectCustomPortletName(jsfSuite,suffix,customPortletName);

        final IVirtualFile portletXml = CoreUtil.getDocroot( jsfProject ).getFile( "WEB-INF/portlet.xml" );

        assertEquals( true, portletXml.exists() );

        final String portletXmlContent = CoreUtil.readStreamToString( portletXml.getUnderlyingFile().getContents() );

        assertEquals( true, portletXmlContent.contains( customPortletName ) );

        final IVirtualFile liferayPortletXml = CoreUtil.getDocroot( jsfProject ).getFile( "WEB-INF/liferay-portlet.xml" );

        assertEquals( true, liferayPortletXml.exists() );

        final String liferayPortletXmlContent = CoreUtil.readStreamToString( liferayPortletXml.getUnderlyingFile().getContents() );

        assertEquals( true, liferayPortletXmlContent.contains( customPortletName ) );

        final IVirtualFile liferayDisplayXml = CoreUtil.getDocroot( jsfProject ).getFile( "WEB-INF/liferay-display.xml" );

        assertEquals( true, liferayDisplayXml.exists() );

        final String liferayDisplayXmlContent = CoreUtil.readStreamToString( liferayDisplayXml.getUnderlyingFile().getContents() );

        assertEquals( true, liferayDisplayXmlContent.contains( customPortletName ) );
    }

    @Test
    public void testNewMVCPortletProjectCustomPortletName() throws Exception
    {
        final String customPortletName = "test111";

        IProject jsfProject = createNewMVCPortletProjectCustomPortletName(customPortletName);

        final IVirtualFile portletXml = CoreUtil.getDocroot( jsfProject ).getFile( "WEB-INF/portlet.xml" );

        assertEquals( true, portletXml.exists() );

        final String portletXmlContent = CoreUtil.readStreamToString( portletXml.getUnderlyingFile().getContents() );

        assertEquals( true, portletXmlContent.contains( customPortletName ) );

        final IVirtualFile liferayPortletXml = CoreUtil.getDocroot( jsfProject ).getFile( "WEB-INF/liferay-portlet.xml" );

        assertEquals( true, liferayPortletXml.exists() );

        final String liferayPortletXmlContent = CoreUtil.readStreamToString( liferayPortletXml.getUnderlyingFile().getContents() );

        assertEquals( true, liferayPortletXmlContent.contains( customPortletName ) );

        final IVirtualFile liferayDisplayXml = CoreUtil.getDocroot( jsfProject ).getFile( "WEB-INF/liferay-display.xml" );

        assertEquals( true, liferayDisplayXml.exists() );

        final String liferayDisplayXmlContent = CoreUtil.readStreamToString( liferayDisplayXml.getUnderlyingFile().getContents() );

        assertEquals( true, liferayDisplayXmlContent.contains( customPortletName ) );
    }

}
