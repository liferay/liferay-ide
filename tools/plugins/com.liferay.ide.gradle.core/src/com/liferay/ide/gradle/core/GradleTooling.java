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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class GradleTooling
{

    private static void extractJar( File depsDir, String jarName ) throws IOException
    {
        InputStream in = GradleTooling.class.getResourceAsStream( "/lib/" + jarName+".jar" );

        Bundle bundle = Platform.getBundle( GradleCore.PLUGIN_ID );

        String bundleVersion = bundle.getVersion().toString();

        File modelJar = new File( depsDir, jarName+"_"+bundleVersion+".jar" );

        if( !modelJar.exists() )
        {
            FileUtil.writeFileFromStream( modelJar, in );
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

            final String initScriptContents = initScriptTemplate.replaceFirst(
                "%deps%", path);

            final File scriptFile = Files.createTempFile( "ide", "init.gradle" ).toFile();

            FileUtil.writeFileFromStream( scriptFile, new ByteArrayInputStream( initScriptContents.getBytes() ) );

            retval = modelBuilder.withArguments( "--init-script", scriptFile.getAbsolutePath() ).get();
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
