/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.util.LaunchHelper;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.internal.debug.ui.classpath.ClasspathModel;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class PortalSupportHelper extends LaunchHelper
{

    protected File errorFile;

    protected IPath[] libRoots;

    protected File outputFile;

    protected String[] portalLibs = new String[] { "portal-impl.jar", "spring-aop.jar" };

    protected IPath portalDir;

    protected URL[] supportLibs;

    protected String[] userLibs;

    public PortalSupportHelper(
        IPath[] libRoots, IPath portalDir, String portalSupportClass, File outputFile, File errorFile,
        URL[] supportLibs, String[] userLibs )
    {

        this( libRoots, portalDir, portalSupportClass, outputFile, errorFile, supportLibs, userLibs, null );
    }

    public PortalSupportHelper(
        IPath[] libRoots, IPath portalDir, String portalSupportClass, File outputFile, File errorFile,
        URL[] supportLibs, String[] userLibs, String extraArg )
    {

        super( IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION );

        setLaunchSync( true );

        setLaunchInBackground( true );

        setLaunchCaptureInConsole( true );

        setLaunchIsPrivate( true );

        setMainClass( portalSupportClass );

        setOutputFile( outputFile );

        setErrorFile( errorFile );

        setLaunchArgs( new String[] { portalSupportClass, outputFile.getAbsolutePath(), errorFile.getAbsolutePath(),
            extraArg } );

        setMode( ILaunchManager.RUN_MODE );

        this.libRoots = libRoots;

        this.portalDir = portalDir;

        this.userLibs = userLibs;

        this.supportLibs = supportLibs;

        // this.launchTimeout = 2000;
    }

    @Override
    public ILaunchConfigurationWorkingCopy createLaunchConfiguration() throws CoreException
    {

        ILaunchConfigurationWorkingCopy config = super.createLaunchConfiguration();

        // set default for common settings
        CommonTab tab = new CommonTab();
        tab.setDefaults( config );
        tab.dispose();

        config.setAttribute( IDebugUIConstants.ATTR_CAPTURE_IN_CONSOLE, false );
        config.setAttribute( DebugPlugin.ATTR_CAPTURE_OUTPUT, false );
        config.setAttribute( IDebugUIConstants.ATTR_PRIVATE, true );

        return config;
    }

    public File getErrorFile()
    {
        return this.errorFile;
    }

    public File getOutputFile()
    {
        return outputFile;
    }

    public void setErrorFile( File errorFile )
    {
        this.errorFile = errorFile;
    }

    public void setOutputFile( File outputFile )
    {
        this.outputFile = outputFile;
    }

    @Override
    protected void addUserEntries( ClasspathModel model ) throws CoreException
    {

        if( supportLibs != null && supportLibs.length > 0 )
        {
            for( URL supportLib : supportLibs )
            {
                model.addEntry(
                    ClasspathModel.USER,
                    JavaRuntime.newStringVariableClasspathEntry( new Path( supportLib.getPath() ).toOSString() ) );
            }
        }

        for( IPath libRoot : libRoots )
        {
            File[] libFiles = libRoot.toFile().listFiles( new FilenameFilter()
            {

                public boolean accept( File dir, String name )
                {
                    return name.endsWith( ".jar" );
                }

            } );

            if( !CoreUtil.isNullOrEmpty( libFiles ) )
            {

                for( File libFile : libFiles )
                {
                    model.addEntry(
                        ClasspathModel.USER, JavaRuntime.newStringVariableClasspathEntry( libFile.getAbsolutePath() ) );
                }

            }
        }

        for( String portalLib : portalLibs )
        {
            model.addEntry(
                ClasspathModel.USER,
                JavaRuntime.newStringVariableClasspathEntry( portalDir.append( "WEB-INF/lib" ).append( portalLib ).toOSString() ) );
        }

        if( userLibs != null )
        {
            for( String userLib : userLibs )
            {
                model.addEntry(
                    ClasspathModel.USER,
                    JavaRuntime.newStringVariableClasspathEntry( portalDir.append( "WEB-INF/lib" ).append( userLib ).toOSString() ) );
            }
        }
        else
        {
            for( String jarFile : this.portalDir.append( "WEB-INF/lib" ).toFile().list() )
            {
                if( jarFile.endsWith( ".jar" ) )
                {
                    model.addEntry(
                        ClasspathModel.USER,
                        JavaRuntime.newStringVariableClasspathEntry( portalDir.append( "WEB-INF/lib" ).append( jarFile ).toOSString() ) );
                }
            }
        }
    }
}
