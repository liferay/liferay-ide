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
package com.liferay.mobile.sdk.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;


/**
 * @author Gregory Amerson
 */
public class MobileSDKBuilder
{
    private static final String PLATFORM = "android";

    public void build( String server, String contextName, String packageName, String buildDir, IProgressMonitor pm )
    {
        build( server, contextName, packageName, null, buildDir, pm );
    }

    public static void build(
        String server, String contextName, String packageName, String filter, String buildDir, IProgressMonitor monitor )
    {
        try
        {
            new SDKBuilderHelper( PLATFORM, server, contextName, packageName, filter, buildDir  ).launch( monitor );
        }
        catch( Exception e )
        {
            MobileSDKCore.logError( "Mobile sdk builder build error", e );
        }
    }

    public static File[] buildJars( String server, String packageName, Map<String, String[]> buildSpec, IProgressMonitor monitor )
    {
        File[] retval = null;

        final File sourceDir = MobileSDKCore.newTempDir();
        final File classDir = MobileSDKCore.newTempDir();

        try
        {
            final Set<String> contexts = buildSpec.keySet();

            for( final String context : contexts )
            {
                if( ! context.equals( PortalAPI.NAME ) )
                {
                    final String[] filters = buildSpec.get( context );

                    for( final String filter : filters )
                    {
                        monitor.subTask( "Building services: " + filter );
                        build( server, context, packageName, filter, sourceDir.getCanonicalPath(), monitor );
                        monitor.worked( 1 );
                    }
                }
            }

            if( compile( sourceDir.getCanonicalPath(), classDir.getCanonicalPath() ) )
            {
                final File customJar = MobileSDKCore.newTempFile( "liferay-android-sdk-custom.jar" );
                final File customJarSrc = MobileSDKCore.newTempFile( "liferay-android-sdk-custom-sources.jar" );

                monitor.subTask( "Creating jar: " + customJar.getName() );

                jar( classDir, "**/*.class", customJar );

                monitor.worked( 1 );

                monitor.subTask( "Creating jar: " + customJarSrc.getName() );

                jar( sourceDir, "**/*.java", customJarSrc );

                monitor.worked( 1 );

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

    private static boolean compile( String sourceDir, String destDir ) throws IOException
    {
        final List<String> args = new ArrayList<String>();

        args.add( "-cp" );

        final String jsonPath = libPath( "jars/org.json_20131018.0.0.jar" );
        final String sdkPath = libPath( "jars/liferay-android-sdk-6.2.0.1.jar" );

        args.add( jsonPath + File.pathSeparatorChar + sdkPath );

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

    private static void jar( File srcDir, String include, File destFile )
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

    private static String libPath( String libPath ) throws IOException
    {
        return FileLocator.toFileURL( MobileSDKCore.getDefault().getBundle().getEntry( libPath ) ).getPath();
    }
}
