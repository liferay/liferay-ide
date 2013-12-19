/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.alloy.core;

import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.core.util.ZipUtil;
import com.sun.jna.Library;
import com.sun.jna.Native;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.osgi.framework.Bundle;


/**
 * @author Gregory Amerson
 */
public class LautRunner
{
    static interface CLibrary extends Library
    {
        public int chmod( String path, int mode );
    }

    private static final String LAUT_ENTRY = "/laut";
    private static final String LAUT_PATH = "com.liferay.laut.LautRunnerPath";
    private static final String LAUT_ZIP = LAUT_ENTRY + ".zip";

    private final static String[] lautExeFiles = new String[] { "run.sh", "node/bin/node" };

    private static CLibrary libc = null;

    static
    {
        initializeLautRunner();
    }

    private static CLibrary getLibC()
    {
        if( libc == null && ! com.sun.jna.Platform.isWindows() )
        {
            try
            {
                libc = (CLibrary) Native.loadLibrary( "c", CLibrary.class );
            }
            catch( Exception e )
            {
                AlloyCore.logError( e );
            }
        }

        return libc;
    }

    private static void initializeLautRunner()
    {
        String lautRunnerPath = System.getProperty( LAUT_PATH );

        if( lautRunnerPath != null )
        {
            File lautRunnerFile = new File( lautRunnerPath );

            if( ! lautRunnerFile.exists() )
            {
                lautRunnerPath = null;
            }
        }

        if( lautRunnerPath == null )
        {
            final String LAUT_RUNNER_BUNDLE =
                new StringBuffer( "com.liferay.laut" ).append( "." ).append( Platform.getOS() ).append( "." ).append(
                    Platform.getWS() ).append( "." ).append( Platform.getOSArch() ).toString();

            final Bundle lautRunnerBundle = Platform.getBundle( LAUT_RUNNER_BUNDLE );

            if( lautRunnerBundle == null )
            {
                AlloyCore.logError( "Unable to find laut bundle." );
            }
            else
            {
                URL lautUrl = lautRunnerBundle.getEntry( LAUT_PATH );

                if( lautUrl == null )
                {
                    final URL lautZipUrl = lautRunnerBundle.getEntry( LAUT_ZIP );

                    if( lautZipUrl != null )
                    {
                        try
                        {
                            final File lautZipFile = new File( FileLocator.toFileURL( lautZipUrl ).getFile() );

                            final File lautBundleDir = new File( FileLocator.toFileURL( lautRunnerBundle.getEntry( "" ) ).getFile() );

                            File lautDir = new File( lautBundleDir, "laut" );

                            if( ! lautBundleDir.canWrite() )
                            {
                                lautDir = AlloyCore.getDefault().getStateLocation().append( "laut" ).toFile();
                            }

                            lautDir.mkdirs();

                            ZipUtil.unzip( lautZipFile, lautDir.getParentFile() );

                            lautUrl = lautRunnerBundle.getEntry( LAUT_ENTRY );

                            if( lautUrl != null )
                            {
                                final File lautRunnerDir = new File( FileLocator.toFileURL( lautUrl ).getFile() );
                                final String lautRunnerDirPath = lautRunnerDir.getAbsolutePath();

                                System.setProperty( LAUT_PATH, lautRunnerDirPath );
                                setSDKExecutableFlags( new Path( lautRunnerDirPath ) );
                            }
                        }
                        catch( Exception e )
                        {
                            AlloyCore.logError( "Unable to intialize Laut bundle", e );
                        }
                    }
                }
            }
        }
    }

    public static void setSDKExecutableFlags( IPath lautDir )
    {
        if( getLibC() == null )
        {
            return;
        }

        for( String exeFile : lautExeFiles )
        {
            getLibC().chmod( lautDir.append( exeFile ).toOSString(), 0755 );
        }
    }

    public void exec( final IProject project, final IProgressMonitor monitor )
    {
        final ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();

        final ILaunchConfigurationType configType =
            lm.getLaunchConfigurationType( "org.eclipse.ui.externaltools.ProgramLaunchConfigurationType" );

        try
        {
            final String projectName = project.getName();

            final ILaunchConfigurationWorkingCopy config =
                configType.newInstance( null, lm.generateLaunchConfigurationName( "laut " + projectName ) );

            config.setAttribute( "org.eclipse.debug.ui.ATTR_LAUNCH_IN_BACKGROUND", true );
            config.setAttribute( "org.eclipse.debug.ui.ATTR_CAPTURE_IN_CONSOLE", true );
            config.setAttribute( "org.eclipse.debug.ui.ATTR_PRIVATE", true );
            config.setAttribute( "org.eclipse.debug.core.ATTR_REFRESH_SCOPE",
                "${working_set:<?xml version=\"1.0\" encoding=\"UTF-8\"?><resources><item path=\"/" +
                    project.getName() + "\" type=\"4\"/></resources>}" );
            config.setAttribute( "org.eclipse.ui.externaltools.ATTR_LAUNCH_CONFIGURATION_BUILD_SCOPE",
                "${projects:" + projectName + "}" );
            config.setAttribute( "org.eclipse.ui.externaltools.ATTR_LOCATION", getExecPath() );
            config.setAttribute( "org.eclipse.ui.externaltools.ATTR_TOOL_ARGUMENTS",
                "-f \"" + project.getLocation().toOSString() + "\"" );
            config.setAttribute( "org.eclipse.ui.externaltools.ATTR_WORKING_DIRECTORY", getWorkingPath().toOSString() );

            new LaunchHelper().launch( config, ILaunchManager.RUN_MODE, monitor );
        }
        catch( CoreException e )
        {
            AlloyCore.logError( "Problem launching laut tool.", e );
        }
    }

    private String getExecPath()
    {
        return getWorkingPath().append( Platform.getOS().contains( "win" ) ? "run.bat" : "run.sh" ).toOSString();
    }

    private Path getWorkingPath()
    {
        return new Path( System.getProperty( LAUT_PATH ) );
    }

    public boolean hasUpdateAvailable()
    {
        return false;
    }
}
