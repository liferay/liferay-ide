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
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Processor;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.StringBufferOutputStream;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.Version;
import org.osgi.service.prefs.Preferences;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */
public class BladeCLI
{

    public static final String BLADE_CLI_REPO_URL = "BLADE_CLI_REPO_URL";

    private static final IEclipsePreferences defaultPrefs = DefaultScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );
    private static final IEclipsePreferences instancePrefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );
    private static final File _settingsDir = LiferayCore.GLOBAL_SETTINGS_PATH.toFile();
    private static final File repoCache = new File( _settingsDir, "repoCache" );
    private static final IPath bladeJarInstanceArea = ProjectCore.getDefault().getStateLocation().append( "blade-jar" );
    private static final IPath bladeJarInstancePath = bladeJarInstanceArea.append( "com.liferay.blade.cli.jar" );

    static
    {
        _settingsDir.mkdirs();
        repoCache.mkdirs();
        bladeJarInstanceArea.toFile().mkdirs();
    }

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

    /**
     * We need to get the correct path to the blade jar, here is the logic
     *
     * First see if we have a instance (workbench) copy of blade cli jar, that means
     * that the developer has intentially updated their blade jar to a newer version than
     * is shipped with the project.core bundle.  The local instance copy will be in the
     * plugin state area.
     *
     * If there is no instance copy of blade cli jar, then fallback to use the one that
     * is in the bundle itself.
     *
     * @return path to local blade jar
     * @throws BladeCLIException
     */
    public static synchronized IPath getBladeCLIPath() throws BladeCLIException
    {
        final File bladeJarInstanceFile = bladeJarInstancePath.toFile();

        if( bladeJarInstanceFile.exists() )
        {
            try
            {
                Domain jar = Domain.domain( bladeJarInstanceFile );

                if( isSupportedVersion( jar.getBundleVersion() ) )
                {
                    return bladeJarInstancePath;
                }
            }
            catch( IOException e )
            {
            }
        }

        try
        {
            return getBladeJarFromBundle();
        }
        catch( IOException e )
        {
            throw new BladeCLIException( "Could not find blade cli jar", e );
        }
    }

    private static IPath getBladeJarFromBundle() throws IOException
    {
        File bladeJarBundleFile = new File(
            FileLocator.toFileURL(
                ProjectCore.getDefault().getBundle().getEntry( "lib/com.liferay.blade.cli.jar" ) ).getFile() );

        return new Path( bladeJarBundleFile.getCanonicalPath() );
    }

    public static File fetchBladeJarFromRepo() throws Exception
    {
        Processor reporter = new Processor();
        FixedIndexedRepo repo = new FixedIndexedRepo();
        Map<String, String> props = new HashMap<String, String>();
        props.put( "name", "index1" );
        props.put( "locations", getRepoURL() + "index.xml.gz" );
        props.put( FixedIndexedRepo.PROP_CACHE, repoCache.getAbsolutePath() );

        repo.setProperties( props );
        repo.setReporter( reporter );

        File[] files = repo.get( "com.liferay.blade.cli", "[2.0.2,3)" );

        return files[0];
    }

    public static synchronized String[] getProjectTemplates() throws BladeCLIException
    {
        List<String> templateNames = new ArrayList<>();

        String[] executeResult = execute( "create -l" );

        for( String name : executeResult )
        {
            // for latest blade which print template descriptor
            if( name.trim().indexOf( " " ) != -1 )
            {
                templateNames.add( name.substring( 0, name.indexOf( " " ) ) );
            }
            // for legacy blade
            else
            {
                templateNames.add( name );
            }
        }

        return templateNames.toArray( new String[0] );
    }

    private static String getRepoURL()
    {
        String repoURL = Platform.getPreferencesService().get( BLADE_CLI_REPO_URL, null, new Preferences[] { instancePrefs, defaultPrefs } );

        if( !repoURL.endsWith( "/" ) )
        {
            repoURL = repoURL + "/";
        }

        return repoURL;
    }

    private static boolean isSupportedVersion( String verisonValue )
    {
        Version version = new Version( verisonValue );
        Version lowVersion = new Version( "2" );
        Version highVersion = new Version( "3" );

        return version.compareTo( lowVersion ) >= 0 && version.compareTo( highVersion ) < 0;
    }

    public static synchronized void addToLocalInstance( File latestBladeJar )
    {
        FileUtil.copyFile( latestBladeJar, bladeJarInstancePath.toFile() );
    }

    public static synchronized void restoreOriginal()
    {
        bladeJarInstancePath.toFile().delete();
    }

}
