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

package com.liferay.ide.hook.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.hook.core.util.HookUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.junit.Before;
import org.junit.Test;

public class HookCoreTests extends BaseTests
{

    private static final String LIFERAY_HOOK_610_XML = "files/liferay-hook-610.xml";
    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }

    @Test
    public void testLiferayHook610XML() throws Exception
    {
        final IFile hooksFile =
            createFile( this.a, LIFERAY_HOOK_610_XML, this.getClass().getResourceAsStream( LIFERAY_HOOK_610_XML ) );

        assertEquals( hooksFile.getFullPath().lastSegment(), "liferay-hook-610.xml" );

        final RootXmlResource resource = new RootXmlResource( new XmlResourceStore( hooksFile.getContents() ) );

        String dtdVersion = HookUtil.getDTDVersion( resource.getDomDocument() );

        assertEquals( dtdVersion, "6.1.0" );

        //final Hook6xx hook610 = Hook6xx.TYPE.instantiate( resource );

        //CustomJspDir customJspDir = hook610.getCustomJspDir().element( false );

        //Assert.assertNotNull( customJspDir.getValue().getContent() );

        //Assert.assertEquals( "/custom_jsps", customJspDir.getValue().getContent().toPortableString() );

    }

}
