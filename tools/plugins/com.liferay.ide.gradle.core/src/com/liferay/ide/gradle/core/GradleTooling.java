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

package com.liferay.ide.gradle.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.FileLocator;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class GradleTooling
{

    private static void extractJar( File depsDir, String jarName ) throws IOException
    {
        String fullFileName = jarName + ".jar";

        File[] files = depsDir.listFiles();

        // clear legacy dependency files
        for( File file : files )
        {
            if( file.isFile() && file.getName().startsWith( jarName ) && !file.getName().equals( fullFileName ) )
            {
                if( !file.delete() )
                {
                    GradleCore.logError( "Error: delete file " + file.getAbsolutePath() + " fail" );
                }
            }
        }

        String embeddedJarVersion = null;

        File embeddedJarFile = new File(
            FileLocator.toFileURL( GradleCore.getDefault().getBundle().getEntry( "lib/" + fullFileName ) ).getFile() );

        try(JarFile embededJarFile = new JarFile( embeddedJarFile ))
        {
            embeddedJarVersion = embededJarFile.getManifest().getMainAttributes().getValue( "Bundle-Version" );
        }
        catch( IOException e )
        {
        }

        File jarFile = new File( depsDir, fullFileName );

        if( jarFile.exists() )
        {
            boolean shouldDelete = false;

            try(JarFile jar = new JarFile( jarFile ))
            {
                String bundleVersion = jar.getManifest().getMainAttributes().getValue( "Bundle-Version" );

                if( !CoreUtil.empty( bundleVersion ) )
                {
                    Version rightVersion = new Version( embeddedJarVersion );
                    Version version = new Version( bundleVersion );

                    if( version.compareTo( rightVersion ) != 0 )
                    {
                        shouldDelete = true;
                    }
                }
                else
                {
                    shouldDelete = true;
                }
            }
            catch( Exception e )
            {
            }

            if( shouldDelete )
            {
                if( !jarFile.delete() )
                {
                    GradleCore.logError( "Error: delete file " + jarFile.getAbsolutePath() + " fail" );
                }
            }
        }

        if( !jarFile.exists() )
        {
            FileUtil.copyFile( embeddedJarFile, jarFile );
        }
    }

    public static <T> T getModel( Class<T> modelClass, File cacheDir, File projectDir ) throws Exception
    {
        T retval = null;

        final GradleConnector connector = GradleConnector.newConnector().forProjectDirectory( projectDir );

        ProjectConnection connection = null;

        try
        {
            connection = connector.connect();

            final ModelBuilder<T> modelBuilder = (ModelBuilder<T>) connection.model( modelClass );

            final File depsDir = new File(cacheDir, "deps");

            depsDir.mkdirs();

            String path = depsDir.getAbsolutePath();

            path = path.replaceAll("\\\\", "/");

            extractJar( depsDir, "com.liferay.blade.gradle.model" );
            extractJar( depsDir, "com.liferay.blade.gradle.plugin" );

            final String initScriptTemplate =
                CoreUtil.readStreamToString( GradleTooling.class.getResourceAsStream( "init.gradle" ) );

            final String initScriptContents = initScriptTemplate.replaceFirst( "%deps%", path );

            final File scriptFile = new File( cacheDir, "init.gradle" );

            if( !scriptFile.exists() )
            {
                scriptFile.createNewFile();
            }

            FileUtil.writeFileFromStream( scriptFile, new ByteArrayInputStream( initScriptContents.getBytes() ) );

            retval = modelBuilder.withArguments( "--init-script", scriptFile.getAbsolutePath() ).get();
        }
        catch(Exception e)
        {
            GradleCore.logError( "get gradle custom model error", e );
        }
        finally
        {
            if( connection != null )
            {
                connection.close();
            }
        }

        return retval;
    }

}
