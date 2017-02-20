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

import aQute.bnd.deployer.repository.FixedIndexedRepo;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.StringBufferOutputStream;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BladeCLI
{

    static final File _settingsDir = LiferayCore.GLOBAL_SETTINGS_PATH.toFile();
    static final File repoCache = new File( _settingsDir, "repoCache" );
    public static final String BLADE_CLI_REPO_URL = "BLADE_CLI_REPO_URL";
    static final String defaultRepoUrl = "http://releases.liferay.com/tools/blade-cli/2.x/";

    static File latestBladeFile;

    public static String[] execute( String args ) throws BladeCLIException
    {
        final IPath bladeCLIPath = getBladeCLIPath();

        if( bladeCLIPath == null || !bladeCLIPath.toFile().exists() )
        {
            throw new BladeCLIException( "Could not get blade cli jar." );
        }

        final Project project = new Project();
        final Java javaTask = new Java();

        javaTask.setProject( project );
        javaTask.setFork( true );
        javaTask.setFailonerror( true );
        javaTask.setJar( bladeCLIPath.toFile() );
        javaTask.setArgs( args );

        final DefaultLogger logger = new DefaultLogger();
        project.addBuildListener( logger );

        final StringBufferOutputStream out = new StringBufferOutputStream();

        logger.setOutputPrintStream( new PrintStream( out ) );
        logger.setMessageOutputLevel( Project.MSG_INFO );

        int returnCode = javaTask.executeJava();

        final List<String> lines = new ArrayList<>();
        final Scanner scanner = new Scanner( out.toString() );

        while( scanner.hasNextLine() )
        {
            lines.add( scanner.nextLine().replaceAll( ".*\\[null\\] ", "" ) );
        }

        scanner.close();

        boolean hasErrors = false;

        final StringBuilder errors = new StringBuilder();

        for( String line : lines )
        {
            if( line.startsWith( "Error" ) )
            {
                hasErrors = true;
            }
            else if( hasErrors )
            {
                errors.append( line );
            }
        }

        if( returnCode != 0 || hasErrors )
        {
            throw new BladeCLIException( errors.toString() );
        }

        return lines.toArray( new String[0] );
    }

    public static synchronized IPath getBladeCLIPath() throws BladeCLIException
    {
        File bladeFile = new File( repoCache, "com.liferay.blade.cli.jar" );

        IPath bladeCLIPath = null;

        if( bladeFile.exists() && bladeFile.isFile() )
        {
            try(Jar jarFile = new Jar( bladeFile ))
            {
                if( isSupportedVersion( jarFile.getVersion() ) )
                {
                    bladeCLIPath = new Path( bladeFile.getCanonicalPath() );
                }
            }
            catch( Exception e )
            {
                throw new BladeCLIException( "get blade error", e );
            }
        }
        else
        {
            try
            {
                repoCache.mkdirs();

                File embededBladeJar = new File(
                    FileLocator.toFileURL(
                        ProjectCore.getDefault().getBundle().getEntry( "lib/com.liferay.blade.cli.jar" ) ).getFile() );

                FileUtil.copyFileToDir( embededBladeJar, repoCache );

                File localBladeJar = new File( repoCache, embededBladeJar.getName() );

                bladeCLIPath = new Path( localBladeJar.getCanonicalPath() );
            }
            catch( Exception e )
            {
                throw new BladeCLIException( "get blade error", e );
            }
        }

        return bladeCLIPath;
    }

    public static String getCurrentVersion()
    {
        String version = "null";

        try
        {
            IPath path = getBladeCLIPath();

            try(Jar jar = new Jar( path.toFile() ))
            {
                version = jar.getVersion();
            }
        }
        catch( Exception e )
        {
        }

        return version;
    }

    public static String fetchLatestVersion() throws BladeCLIException
    {
        _settingsDir.mkdirs();
        repoCache.mkdirs();

        Processor reporter = new Processor();
        FixedIndexedRepo repo = new FixedIndexedRepo();
        Map<String, String> props = new HashMap<String, String>();
        props.put( "name", "index1" );
        props.put( "locations", getRepoURL() + "index.xml.gz" );
        props.put( FixedIndexedRepo.PROP_CACHE, repoCache.getAbsolutePath() );

        repo.setProperties( props );
        repo.setReporter( reporter );

        String latestVersion = "null";

        latestBladeFile = null;

        try
        {
            File[] files = repo.get( "com.liferay.blade.cli", "[2.0.2,3)" );

            if( files != null && files.length > 0 )
            {
                File cliFile = files[0];

                latestBladeFile = cliFile;

                try(Jar cliJar = new Jar( cliFile ))
                {
                    latestVersion = cliJar.getVersion();
                }
            }
            else
            {
                throw new BladeCLIException( "remote blade verion is not in the range [2.0.2,3)" );
            }
        }
        catch( BladeCLIException e )
        {
            throw e;
        }
        catch( Exception e1 )
        {
            throw new BladeCLIException( "get blade version error", e1 );
        }

        return latestVersion;
    }

    public static synchronized String[] getProjectTemplates() throws BladeCLIException
    {
        List<String> templateNames = new ArrayList<>();

        String[] retval = execute( "create -l" );

        Collections.addAll( templateNames, retval );

        return templateNames.toArray( new String[0] );
    }

    private static String getRepoURL()
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

        String repoURL = prefs.get( BLADE_CLI_REPO_URL, defaultRepoUrl );

        if( !repoURL.endsWith( "/" ) )
        {
            repoURL = repoURL + "/";
        }

        return repoURL;
    }

    private static boolean isSupportedVersion( String verisonString )
    {
        Version version = new Version( verisonString );
        Version lowVersion = new Version( "2.0" );
        Version highVersion = new Version( "2.3" );

        if( version.compareTo( lowVersion ) >= 0 && version.compareTo( highVersion ) < 0 )
        {
            return true;
        }

        return false;
    }

    public static void updateBladeToLatest() throws BladeCLIException
    {
        if( latestBladeFile != null )
        {
            FileUtil.copyFile( latestBladeFile, getBladeCLIPath().toFile() );
        }
    }
}
