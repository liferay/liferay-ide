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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.Test;
import org.osgi.framework.Version;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BladeCLITests
{

    private void setDBladeURLefaultPreferences( IEclipsePreferences prefs )
    {
        IEclipsePreferences defaults = DefaultScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        final String defaultValue = defaults.get( BladeCLI.BLADE_CLI_REPO_URL, "" );

        prefs.put( BladeCLI.BLADE_CLI_REPO_URL, defaultValue );

        try
        {
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
        }
    }

    @Test
    public void testUpdate1xWillFail() throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        prefs.put( BladeCLI.BLADE_CLI_REPO_URL, "http://releases.liferay.com/tools/blade-cli/1.x/" );

        prefs.flush();

        try
        {
            BladeCLI.fetchLatestVersion();
        }
        catch( BladeCLIException e )
        {
            assertEquals( e.getMessage(), "remote blade verion is not in the range [2.0.2,3)" );
        }

        setDBladeURLefaultPreferences( prefs );
    }

    @Test
    public void testUpdateBladeToCloudbees() throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        prefs.put( BladeCLI.BLADE_CLI_REPO_URL,
            "https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/lastSuccessfulBuild/artifact/build/generated/p2/" );

        prefs.flush();

        String currentVersionStr = BladeCLI.getCurrentVersion();

        BladeCLI.updateBladeToLatest();

        // not update autually
        assertEquals( currentVersionStr, BladeCLI.getCurrentVersion() );

        String remoteVersionStr = BladeCLI.fetchLatestVersion();

        Version remoteVersion = new Version( remoteVersionStr );

        Version currentVersion = new Version( currentVersionStr );

        assertTrue( remoteVersion.compareTo( currentVersion ) > 0 );

        BladeCLI.updateBladeToLatest();

        // do update
        assertEquals( remoteVersionStr, BladeCLI.getCurrentVersion() );

        setDBladeURLefaultPreferences( prefs );
    }

    @Test
    public void bladeCLICreateProject() throws Exception
    {
        Path temp = Files.createTempDirectory( "path with spaces" );

        StringBuilder sb = new StringBuilder();
        sb.append( "create " );
        sb.append( "-d \"" + temp.toAbsolutePath().toString() + "\" " );
        sb.append( "-t mvc-portlet " );
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

        assertEquals( "activator", projectTemplates[0] );

        assertEquals( "theme-contributor", projectTemplates[projectTemplates.length - 1] );
    }

}
