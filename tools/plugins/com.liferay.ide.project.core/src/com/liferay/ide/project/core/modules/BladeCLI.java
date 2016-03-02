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
import aQute.bnd.osgi.Processor;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.StringBufferOutputStream;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Gregory Amerson
 * @author Andy Wu
 */

public class BladeCLI
{
    static final File _settingsDir = LiferayCore.GLOBAL_SETTINGS_PATH.toFile();
    static final String localJarKey = "localjar";
    static String[] projectTemplateNames;
    static final File repoCache = new File( _settingsDir, "repoCache" );
    static final String repoUrl = "https://liferay-test-01.ci.cloudbees.com/job/blade.tools/lastSuccessfulBuild/artifact/p2_build/generated/p2/index.xml.gz";
    static final File repoUrlCacheDir = new File(repoCache,"https%3A%2F%2Fliferay-test-01.ci.cloudbees.com%2Fjob%2Fblade.tools%2FlastSuccessfulBuild%2Fartifact%2Fp2_build%2Fgenerated%2Fp2%2Fplugins");
    static final String timeStampKey = "up2date.check";

    static IPath cachedBladeCLIPath;

    public static String checkForErrors( String[] lines )
    {
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

        return errors.toString();
    }

    public static String[] execute( String args ) throws BladeCLIException
    {
        final IPath bladeCLIPath = getBladeCLIPath();

        if( bladeCLIPath == null || !bladeCLIPath.toFile().exists() )
        {
            throw new BladeCLIException("Could not get blade cli jar.");
        }

        final Project project = new Project();
        final Java javaTask = new Java();

        javaTask.setProject( project );
        javaTask.setFork( true );
        javaTask.setFailonerror( true );
        javaTask.setJar( bladeCLIPath.toFile() );
        javaTask.setArgs( args );

        final DefaultLogger logger = new DefaultLogger();
        project.addBuildListener(logger);

        final StringBufferOutputStream out = new StringBufferOutputStream();

        logger.setOutputPrintStream( new PrintStream( out ) );
        logger.setMessageOutputLevel(Project.MSG_INFO);

        javaTask.executeJava();

        final List<String> lines = new ArrayList<>();
        final Scanner scanner = new Scanner( out.toString() );

        while( scanner.hasNextLine() )
        {
            lines.add( scanner.nextLine().replaceAll( ".*\\[null\\] ", "" ) );
        }

        scanner.close();

        return lines.toArray( new String[0] );
    }

    public static synchronized IPath getBladeCLIPath() throws BladeCLIException
    {
        final IPath stateLocation = ProjectCore.getDefault().getStateLocation();
        final File bladeCacheSettings = new File( stateLocation.toFile(), "blade-cache.properties" );
        final SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmm" );

        if( !bladeCacheSettings.exists() )
        {
            Properties props = new Properties();
            String localJar = getLatestRemoteBladeCLIJar();

            props.setProperty( timeStampKey, sdf.format( new Date() ) );
            props.setProperty( localJarKey, localJar );

            PropertiesUtil.saveProperties( props, bladeCacheSettings );
        }
        else
        {
            Properties props = PropertiesUtil.loadProperties( bladeCacheSettings );

            if( props == null )
            {
                throw new BladeCLIException( "Could not load file blade-cache.properties." );
            }

            String up2dateCheckTimestamp = props.getProperty( timeStampKey );
            Date lastTime = null;

            try
            {
                lastTime = sdf.parse( up2dateCheckTimestamp );
            }
            catch( ParseException e )
            {
            }

            if( lastTime == null )
            {
                throw new BladeCLIException( "Could not get up2date check timestamp from blade-cache.properties." );
            }

            Date currentTime = new Date();

            long distance = currentTime.getTime() - lastTime.getTime();

            long hours = distance / 1000 / 3600;

            if( hours >= 24 )
            {
                String localJar = getLatestRemoteBladeCLIJar();

                props.setProperty( timeStampKey, sdf.format( currentTime ) );
                props.setProperty( localJarKey, localJar );

                PropertiesUtil.saveProperties( props, bladeCacheSettings );
            }
            else
            {
                try
                {
                    File locaJarFile = new File( repoUrlCacheDir, props.getProperty( localJarKey ) );

                    cachedBladeCLIPath = new Path( locaJarFile.getCanonicalPath() );
                }
                catch( IOException e )
                {
                    throw new BladeCLIException( "Could not get blade cli jar from local cache dir." );
                }
            }
        }

        return cachedBladeCLIPath;
    }

    private static String getLatestRemoteBladeCLIJar() throws BladeCLIException
    {
        _settingsDir.mkdirs();
        repoCache.mkdirs();

        Processor reporter = new Processor();
        FixedIndexedRepo repo = new FixedIndexedRepo();
        Map<String, String> props = new HashMap<String, String>();
        props.put( "name", "index1" );
        props.put( "locations", repoUrl );
        props.put( FixedIndexedRepo.PROP_CACHE, repoCache.getAbsolutePath() );

        repo.setProperties( props );
        repo.setReporter( reporter );

        try
        {
            File[] files = repo.get( "com.liferay.blade.cli", "[1,2)" );

            File agentJar = files[0];

            cachedBladeCLIPath = new Path( agentJar.getCanonicalPath() );

            return agentJar.getName();
        }
        catch( Exception e )
        {
            throw new BladeCLIException( "Could not get blade cli jar from repository." );
        }
    }

    public static String[] getProjectTemplates() throws BladeCLIException
    {
        if( projectTemplateNames == null )
        {
            Set<String> templateNames = new HashSet<>();
            IPath bladeJarPath = getBladeCLIPath();

            try(ZipFile zip = new ZipFile( bladeJarPath.toFile() ) )
            {
                File temp = File.createTempFile( "templates", ".zip" );
                FileUtil.writeFileFromStream( temp, zip.getInputStream( zip.getEntry( "templates.zip" ) ) );

                try( ZipFile templatesZipFile = new ZipFile( temp ) )
                {
                    Enumeration<? extends ZipEntry> entries = templatesZipFile.entries();

                    while( entries.hasMoreElements() )
                    {
                        ZipEntry entry = entries.nextElement();
                        IPath entryPath = new Path(entry.getName());

                        if( entryPath.segmentCount() > 1 )
                        {
                            String name = entryPath.segment( 1 );

                            if( !name.equals( "fragment" ) )
                            {
                                templateNames.add( name );
                            }
                        }
                    }
                }
            }
            catch( IOException e )
            {
                throw new BladeCLIException( "Unable to open blade cli jar.", e );
            }

            projectTemplateNames = templateNames.toArray( new String[0] );
        }

        return projectTemplateNames;
    }

    public static void main(String[] args) throws Exception
    {
        String[] output = execute( "help" );

        for( String s : output )
        {
            System.out.println( s );
        }
    }

}
