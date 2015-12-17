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
package com.liferay.ide.project.core.tests.modules;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.liferay.ide.project.core.modules.BladeCLI;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class BladeCLITests
{

    @Test
    public void bladeCLICreateProject() throws Exception
    {
        Path temp = Files.createTempDirectory( "path with spaces" );

        StringBuilder sb = new StringBuilder();
        sb.append( "create " );
        sb.append(  "-d \"" + temp.toAbsolutePath().toString() +  "\" " );
        sb.append( "-t mvcportlet " );
        sb.append( "foo" );

        BladeCLI.execute( sb.toString() );

        assertTrue( new File( temp.toFile(), "foo/build.gradle" ).exists() );
    }

    @Test
    public void bladeCLIExecute() throws Exception
    {
        String[] output = BladeCLI.execute( "help" );

        assertNotNull( output );

        assertTrue( output.length > 0 );

        for( String line : output )
        {
            if( line.contains( "[null]" ) )
            {
                fail( "Output contains [null]" );
            }
        }
    }

    @Test
    public void bladeCLIProjectTemplates() throws Exception
    {
        String[] projectTemplates = BladeCLI.getProjectTemplates();

        assertNotNull( projectTemplates );

        assertTrue( projectTemplates.length > 0 );
    }

}
