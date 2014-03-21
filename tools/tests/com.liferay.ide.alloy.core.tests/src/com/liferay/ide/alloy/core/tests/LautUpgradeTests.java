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

package com.liferay.ide.alloy.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class LautUpgradeTests extends BaseTests
{

    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }


    @Test
    public void testUpgrade() throws Exception
    {
        final IFolder cssFolder = this.a.getFolder( new Path( "docroot/css/" ) );

        CoreUtil.prepareFolder( cssFolder );

        assertEquals( true, cssFolder.exists() );

        final IFile cssFile = cssFolder.getFile( "main.css" );

        final String originalMainCss = CoreUtil.readStreamToString(
            this.getClass().getResourceAsStream( "files/01_events-display-portlet/docroot/css/main.css" ) );

        final NullProgressMonitor npm = new NullProgressMonitor();

        cssFile.create( new ByteArrayInputStream( originalMainCss.getBytes() ), true, npm );

        assertEquals( true, cssFile.exists() );

        AlloyCore.getLautRunner().exec( a, npm );

        final String expectedMainCss =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/02_events-display-portlet/docroot/css/main.css" ) );

        assertEquals(
            stripCarriageReturns( expectedMainCss ),
            stripCarriageReturns( CoreUtil.readStreamToString( cssFile.getContents() ) ) );
    }

}
