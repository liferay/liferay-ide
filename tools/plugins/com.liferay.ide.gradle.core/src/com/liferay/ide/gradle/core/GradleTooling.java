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

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class GradleTooling
{

    private static String getBundlePath( String bundleId, File tempDir )
    {
        String installPath = Platform.getInstallLocation().getURL().getPath();

        final String bundleLocation = Platform.getBundle( bundleId ).getLocation();

        String bundleLocationString = bundleLocation.replaceAll( "reference:", "" ).replaceAll( "file:", "" );

        File srcBundle = new File( bundleLocationString );

        if( bundleLocationString.contains( "@" ) )
        {
            String p2Path = bundleLocationString.substring(
                bundleLocationString.indexOf( "@" ) + 1, bundleLocationString.length() );
            srcBundle = new File( p2Path );
        }
        else if( !bundleLocationString.contains( installPath ) )
        {
            srcBundle = new File( installPath, bundleLocationString );
        }

        File desBundle = new File( tempDir, srcBundle.getName() );

        if( !desBundle.exists() )
        {
            FileUtil.copyFile( srcBundle, desBundle );
        }

        String result = desBundle.getAbsolutePath().replaceAll( "\\\\", "/" );

        return result;
    }

    public static <T> T getModel( Class<T> modelClass, File cacheDir, File projectDir ) throws Exception
    {
        T retval = null;

        final GradleConnector connector = GradleConnector.newConnector();
        connector.forProjectDirectory( projectDir );

        ProjectConnection connection = null;

        try
        {
            connection = connector.connect();

            final ModelBuilder<T> modelBuilder = (ModelBuilder<T>) connection.model( modelClass );

            final String initScriptTemplate =
                CoreUtil.readStreamToString( GradleTooling.class.getResourceAsStream( "init.gradle" ) );

            final File scriptFile = Files.createTempFile( "ide", "init.gradle" ).toFile();
            final File tempDir = scriptFile.getParentFile();

            final String modelBundlePath = getBundlePath( "com.liferay.blade.gradle.model", tempDir );
            final String pluginBundlePath = getBundlePath( "com.liferay.blade.gradle.plugin", tempDir );

            String initScriptContents = initScriptTemplate.replaceFirst( "%model%", modelBundlePath ).replaceFirst(
                "%plugin%", pluginBundlePath );

            FileUtil.writeFileFromStream( scriptFile, new ByteArrayInputStream( initScriptContents.getBytes() ) );

            modelBuilder.withArguments( "--init-script", scriptFile.getAbsolutePath() );

            retval = modelBuilder.get();
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

    public static Set<File> getOutputFiles( File cacheDir, File buildDir ) throws Exception
    {
        final CustomModel model = getModel( CustomModel.class, cacheDir, buildDir );

        return model.getOutputFiles();
    }

}
