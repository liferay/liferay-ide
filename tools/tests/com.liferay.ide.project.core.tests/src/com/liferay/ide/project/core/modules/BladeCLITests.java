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

package com.liferay.ide.project.core.modules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import aQute.bnd.osgi.Domain;

import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.runtime.IPath;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BladeCLITests
{

    @Test
    public void testBundleFileIsValid() throws Exception
    {
        IPath path = BladeCLI.getBladeCLIPath();

        final File bladeFile = path.toFile();

        assertTrue( bladeFile.exists() );

        Domain domain = Domain.domain( bladeFile );

        assertTrue( domain.getBundleVersion().startsWith( "7" ) );

        assertFalse( domain.getBundleVersion().startsWith( "5" ) );
    }

    @Test
    public void testBundleFileIsFromBundle() throws Exception
    {
        IPath path = BladeCLI.getBladeCLIPath();

        IPath stateLocation = ProjectCore.getDefault().getStateLocation();

        assertFalse( stateLocation.isPrefixOf( path ) );
    }

    @Test
    public void bladeCLICreateProject() throws Exception {
        Path temp = Files.createTempDirectory("path with spaces");

        StringBuilder sbWorkspace = new StringBuilder();
        sbWorkspace.append("init ");
        sbWorkspace.append("-v ");
        sbWorkspace.append("portal-7.3-ga6 ");
        sbWorkspace.append("testWorkspace ");
        sbWorkspace.append("--base ");
        sbWorkspace.append("\"");
        sbWorkspace.append(temp.toAbsolutePath());
        sbWorkspace.append("\"");

        BladeCLI.execute( sbWorkspace.toString() );

        Path workspacePath = temp.resolve("testWorkspace");

        assertTrue(workspacePath.toFile().exists());

        StringBuilder sb = new StringBuilder();
        sb.append("create ");
        sb.append("-q ");
        sb.append("--base ");
        sb.append("\"");
        sb.append(workspacePath);
        sb.append("\" ");
        sb.append("-t mvc-portlet ");
        sb.append("foo");

        BladeCLI.execute( sb.toString() );

        assertTrue( new File(workspacePath.toFile(), "modules/foo/build.gradle" ).exists() );
    }

    @Test
    public void bladeCLIExecute() throws Exception {
        String[] output = BladeCLI.execute("help -q");

        assertNotNull(output);

        assertTrue(output.length > 0);

        for( String line : output ) {
            if( line.contains( "[null]" ) ) {
                fail( "Output contains [null]" );
            }
        }
    }

    @Test
    public void bladeCLIProjectTemplates() throws Exception
    {
        String[] projectTemplates = BladeCLI.getProjectTemplatesNames();

        assertNotNull( projectTemplates );

        assertTrue( projectTemplates[0], projectTemplates[0].startsWith("api"));

        assertTrue(
            projectTemplates[projectTemplates.length - 1],
            projectTemplates[projectTemplates.length - 1].startsWith( "war-mvc-portlet" ) );
    }

}
