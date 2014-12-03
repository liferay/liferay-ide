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

package com.liferay.ide.alloy.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.alloy.core.LautRunner;
import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class AlloyCoreTests extends BaseTests
{

    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }


    @Test
    @Ignore
    public void fileExecutable() throws Exception
    {
        final LautRunner lautRunner = AlloyCore.getLautRunner();

        final String execPath = lautRunner.getExecPath();

        assertNotNull( execPath );

        final File execFile = new Path( execPath ).toFile();

        assertTrue( execFile.exists() );

        if( CoreUtil.isLinux() || CoreUtil.isMac() )
        {
            assertTrue( "Expected setExecutable(true) to return true", execFile.setExecutable( true ) );

            final File nodeFile = new Path( execPath ).removeLastSegments( 1 ).append( "node/bin/node" ).toFile();

            assertTrue( "Expected setExecutable(true) to return true", nodeFile.setExecutable( true ) );
        }
    }

    @Test
    @Ignore
    public void testLautInstallation() throws Exception
    {
        final LautRunner lautRunner = AlloyCore.getLautRunner();

        assertNotNull( lautRunner );

        final String execPath = lautRunner.getExecPath();

        assertNotNull( execPath );

        final File exec = new File( execPath );

        assertEquals( true, exec.exists() );

        lautRunner.exec( a, new NullProgressMonitor() );
    }

}
