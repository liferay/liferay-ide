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

import com.liferay.ide.core.util.LaunchHelper;

import java.util.ArrayList;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class CreateHelper extends LaunchHelper
{

    public static final String PROGRAM_CONFIG_TYPE_ID = IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE;

    protected IPath currentCreateFile;

    protected SDK sdk;

    private IProgressMonitor monitor;

    public CreateHelper( SDK sdk )
    {
        super( PROGRAM_CONFIG_TYPE_ID );

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
        IPath buildFile, ArrayList<String> arguments, String workingDir ) throws CoreException
    {
        StringBuffer sb = new StringBuffer();

        for( String argument : arguments )
        {
            sb.append( "\"" );
            sb.append( argument );
            sb.append( "\"" );
            sb.append( " " );
        }

        ILaunchConfigurationWorkingCopy launchConfig = super.createLaunchConfiguration();

        launchConfig.setAttribute( IExternalToolConstants.ATTR_LOCATION, buildFile.toOSString() );
        
        launchConfig.setAttribute( IExternalToolConstants.ATTR_WORKING_DIRECTORY, workingDir );

        launchConfig.setAttribute( IExternalToolConstants.ATTR_TOOL_ARGUMENTS, sb.toString().trim() );

        launchConfig.setAttribute( DebugPlugin.ATTR_CAPTURE_OUTPUT, true);

        launchConfig.setAttribute( "org.eclipse.debug.ui.ATTR_CAPTURE_IN_FILE",
            SDKCorePlugin.getDefault().getStateLocation().append( "sdk.log" ).toOSString() );

        return launchConfig;
    }

    public void runTarget( IPath createFile, ArrayList<String> arguments, String workingDir ) throws CoreException
    {
        if( isLaunchRunning() )
        {
            throw new IllegalStateException( "Existing launch in progress" );
        }

        this.currentCreateFile = createFile;

        this.currentCreateFile.toFile().setExecutable( true );

        ILaunchConfiguration launchConfig = createLaunchConfiguration( createFile, arguments, workingDir );

        launch( launchConfig, ILaunchManager.RUN_MODE, monitor );

        this.currentCreateFile = null;
    }

}
