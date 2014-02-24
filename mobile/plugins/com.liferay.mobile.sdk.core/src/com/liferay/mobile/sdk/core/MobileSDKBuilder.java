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
package com.liferay.mobile.sdk.core;

import com.liferay.mobile.sdk.SDKBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;


/**
 * @author Gregory Amerson
 */
public class MobileSDKBuilder
{
    private static final String PLATFORM = "android";

    public void build( String server, String contextName, String packageName, String buildDir )
    {
        build( server, contextName, packageName, null, buildDir );
    }

    public void build( String server, String contextName, String packageName, String filter, String buildDir )
    {
        try
        {
            SDKBuilder.build( PLATFORM, server, contextName, packageName, filter, buildDir );
        }
        catch( Exception e )
        {
            MobileSDKCore.logError( "Mobile sdk builder build error", e );
        }
    }

    public File[] buildJars( String server, String contextName, String packageName )
    {
        return buildJars( server, contextName, packageName, null );
    }

    public File[] buildJars( String server, String contextName, String packageName, String filter )
    {
        final File sourceDir = MobileSDKCore.newTempDir();
        final File classDir = MobileSDKCore.newTempDir();
        File[] retval = null;

        try
        {
            build( server, contextName, packageName, filter, sourceDir.getCanonicalPath() );

            if( compile( sourceDir.getCanonicalPath(), classDir.getCanonicalPath() ) )
            {
                final File customJar = MobileSDKCore.newTempFile( "liferay-android-sdk-custom.jar" );
                final File customJarSrc = MobileSDKCore.newTempFile( "liferay-android-sdk-custom-src.jar" );

                jar( classDir, "**/*.class", customJar );
                jar( sourceDir, "**/*.java", customJarSrc );

                if( customJar.exists() && customJarSrc.exists() )
                {
                    retval = new File[] { customJar, customJarSrc };
                }
            }
        }
        catch( Exception e )
        {
            MobileSDKCore.logError( "Error building jars", e );
        }
        finally
        {
            sourceDir.delete();
            classDir.delete();
        }

        return retval;
    }

    private String bundlePath( String bundle ) throws MalformedURLException
    {
        final String path = new URL( new URL( Platform.getBundle( bundle ).getLocation() ).getPath() ).getPath();

        return path.startsWith( "/" ) ? path.substring( 1, path.length() ) : path;
    }

    private boolean compile( String sourceDir, String destDir ) throws IOException
    {
        final List<String> args = new ArrayList<String>();

        args.add( "-cp" );

        args.add( bundlePath( "org.json" ) + ";"  + libPath( "liferay-android-sdk-1.1-core.jar" ) );

        args.add( "-1.6" );

        args.add("-d");
        args.add( destDir );

        args.add( sourceDir );

        final CompilationProgress progress =new CompilationProgress()
        {
            public void begin( int remainingWork )
            {
            }

            public void done()
            {
            }

            public boolean isCanceled()
            {
                return false;
            }

            public void setTaskName( String name )
            {
            }

            public void worked( int workIncrement, int remainingWork )
            {
            }
        };

        return BatchCompiler.compile( args.toArray( new String[0] ), new PrintWriter( System.out ), new PrintWriter(
            System.err ), progress );
    }

    private void jar( File srcDir, String include, File destFile )
    {
        final Jar jar = new Jar();

        final Project project = new Project();
        project.setBaseDir( MobileSDKCore.getDefault().getStateLocation().toFile() );

        jar.setProject( project );

        final FileSet set = new FileSet();
        set.setDir( srcDir );
        set.setIncludes( include );

        jar.setDestFile( destFile );
        jar.addFileset( set );
        jar.execute();
    }

    private String libPath( String lib ) throws IOException
    {
        return FileLocator.toFileURL( MobileSDKCore.getDefault().getBundle().getEntry( "lib/" + lib ) ).getPath();
    }
}
