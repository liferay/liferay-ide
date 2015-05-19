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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.core.util.RuntimeClasspathModel;
import com.liferay.ide.core.util.StringPool;

import java.io.File;

import org.eclipse.ant.launching.IAntLaunchConstants;
import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class CreateHelper extends LaunchHelper
{

    public static final String ANT_PROGRAM_CONFIG_TYPE_ID = IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE;

    protected IPath currentCreateFile;

    protected SDK sdk;

    private String[] additionalVMArgs;

    private IProgressMonitor monitor;

    public CreateHelper( SDK sdk )
    {
        super( ANT_PROGRAM_CONFIG_TYPE_ID );

        this.sdk = sdk;

        setLaunchSync( true );
        setLaunchInBackground( true );
        setLaunchCaptureInConsole( true );
        setLaunchIsPrivate( true );
    }

    public CreateHelper( SDK sdk, IProgressMonitor monitor )
    {
        this( sdk );

        this.monitor = monitor;
    }

    public ILaunchConfiguration createLaunchConfiguration(
        IPath buildFile, String arguments, boolean separateJRE, String workingDir ) throws CoreException
    {
        ILaunchConfigurationWorkingCopy launchConfig = super.createLaunchConfiguration();

        launchConfig.setAttribute( IExternalToolConstants.ATTR_LOCATION, buildFile.toOSString() );
        
        launchConfig.setAttribute( IExternalToolConstants.ATTR_WORKING_DIRECTORY, workingDir );

        launchConfig.setAttribute( IExternalToolConstants.ATTR_TOOL_ARGUMENTS, arguments );

        launchConfig.setAttribute( DebugPlugin.ATTR_CAPTURE_OUTPUT, true);

        launchConfig.setAttribute( "org.eclipse.debug.ui.ATTR_CAPTURE_IN_FILE",
            SDKCorePlugin.getDefault().getStateLocation().append( "sdk.log" ).toOSString() );

        if( separateJRE )
        {
            launchConfig.setAttribute(
                IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, IAntLaunchConstants.MAIN_TYPE_NAME );
            launchConfig.setAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, getVMArgumentsAttr() );
        }

        return launchConfig;
    }

    private String getVMArgumentsAttr()
    {
        StringBuffer args = new StringBuffer( "-Xmx768m" ); //$NON-NLS-1$

        if( !CoreUtil.isNullOrEmpty( additionalVMArgs ) )
        {
            for( String vmArg : additionalVMArgs )
            {
                args.append( StringPool.SPACE + vmArg );
            }
        }

        return args.toString();
    }

    public void runTarget( IPath createFile, String arguments, boolean separateJRE, String workingDir )
        throws CoreException
    {
        if( isLaunchRunning() )
        {
            throw new IllegalStateException( "Existing launch in progress" );
        }

        this.currentCreateFile = createFile;

        this.currentCreateFile.toFile().setExecutable( true );

        ILaunchConfiguration launchConfig = createLaunchConfiguration( createFile, arguments, separateJRE, workingDir );

        launch( launchConfig, ILaunchManager.RUN_MODE, monitor );

        this.currentCreateFile = null;
    }

    protected void addUserEntries( RuntimeClasspathModel model ) throws CoreException
    {
        IPath[] antLibs = sdk.getAntLibraries();

        for( IPath antLib : antLibs )
        {
            if( antLib.toFile().exists() )
            {
                model.addEntry(
                    RuntimeClasspathModel.USER,
                    JavaRuntime.newArchiveRuntimeClasspathEntry( antLib.makeAbsolute() ) );
            }
        }

        try
        {
            File bundleFile = FileLocator.getBundleFile( JavaCore.getPlugin().getBundle() );

            if( bundleFile.exists() )
            {
                model.addEntry(
                    RuntimeClasspathModel.USER,
                    JavaRuntime.newArchiveRuntimeClasspathEntry( new Path( bundleFile.getAbsolutePath() ) ) );
            }
        }
        catch( Exception e )
        {
        }
    }

    public void setVMArgs( String[] vmargs )
    {
        this.additionalVMArgs = vmargs;
    }
}
