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

package com.liferay.ide.sdk.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.sdk.SDK;
import com.liferay.ide.sdk.SDKClasspathProvider;
import com.liferay.ide.sdk.SDKPlugin;
import com.liferay.ide.ui.util.LaunchHelper;

import java.util.Map;

import org.eclipse.ant.internal.ui.IAntUIConstants;
import org.eclipse.ant.launching.IAntLaunchConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.internal.debug.ui.classpath.ClasspathModel;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( { "restriction", "deprecation" } )
public class SDKHelper extends LaunchHelper
{

    public static final String ANT_CLASSPATH_PROVIDER = "org.eclipse.ant.ui.AntClasspathProvider"; //$NON-NLS-1$

    public static final String ANT_LAUNCH_CONFIG_TYPE_ID = IAntLaunchConstants.ID_ANT_LAUNCH_CONFIGURATION_TYPE;

    protected IPath currentBuildFile;

    protected String currentTargets;

    protected SDK sdk;

    private String[] additionalVMArgs;

    public SDKHelper( SDK sdk )
    {
        super( ANT_LAUNCH_CONFIG_TYPE_ID );

        this.sdk = sdk;

        setLaunchSync( true );
        setLaunchInBackground( true );
        setLaunchCaptureInConsole( true );
        setLaunchIsPrivate( true );
        // this.launchTimeout = 10000;
    }

    public ILaunchConfiguration createLaunchConfiguration(
        IPath buildFile, String targets, Map<String, String> properties, boolean separateJRE ) throws CoreException
    {

        ILaunchConfigurationWorkingCopy launchConfig = super.createLaunchConfiguration();

        launchConfig.setAttribute( IExternalToolConstants.ATTR_LOCATION, buildFile.toOSString() );

        launchConfig.setAttribute(
            IExternalToolConstants.ATTR_WORKING_DIRECTORY, buildFile.removeLastSegments( 1 ).toOSString() );

        launchConfig.setAttribute( IAntLaunchConstants.ATTR_ANT_TARGETS, targets );

        // set default for common settings
        CommonTab tab = new CommonTab();
        tab.setDefaults( launchConfig );
        tab.dispose();

        launchConfig.setAttribute(
            IDebugUIConstants.ATTR_CAPTURE_IN_FILE,
            SDKPlugin.getDefault().getStateLocation().append( "sdk.log" ).toOSString() ); //$NON-NLS-1$

        launchConfig.setAttribute(
            IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH_PROVIDER, "org.eclipse.ant.ui.AntClasspathProvider" ); //$NON-NLS-1$

        // launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
        // "org.eclipse.ant.internal.ui.antsupport.InternalAntRunner");

        launchConfig.setAttribute( DebugPlugin.ATTR_PROCESS_FACTORY_ID, IAntUIConstants.REMOTE_ANT_PROCESS_FACTORY_ID );

        // IVMInstall vmInstall = getDefaultVMInstall(launchConfig);
        // launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH,
        // vmInstall.getName());
        // launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH,
        // vmInstall.getVMInstallType().getId());

        launchConfig.setAttribute( IAntLaunchConstants.ATTR_ANT_PROPERTIES, properties );
        launchConfig.setAttribute( IAntLaunchConstants.ATTR_ANT_PROPERTY_FILES, (String) null );

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
                args.append( StringUtil.SPACE + vmArg );
            }
        }

        return args.toString();
    }

    public String getClasspathProviderAttributeValue()
    {
        // return ANT_CLASSPATH_PROVIDER;
        return SDKClasspathProvider.ID;
    }

    /**
     * Returns a unique name for a copy of the given launch configuration with the given targets. The name seed is the
     * same as the name for a new launch configuration with " [targetList]" appended to the end.
     * 
     * @param config
     * @param targetAttribute
     * @return
     */
    public String getNewLaunchConfigurationName()
    {
        StringBuffer buffer = new StringBuffer();

        if( this.sdk.getName() != null )
        {
            buffer.append( this.sdk.getName() );
            buffer.append( ' ' );
        }

        if( this.currentBuildFile != null )
        {
            buffer.append( this.currentBuildFile.lastSegment() );
        }

        if( this.currentTargets != null )
        {
            buffer.append( " [" ); //$NON-NLS-1$
            buffer.append( this.currentTargets );
            buffer.append( "]" ); //$NON-NLS-1$
        }

        return buffer.toString();
    }

    public void runTarget( IPath buildFile, String targets, Map<String, String> properties ) throws CoreException
    {

        runTarget( buildFile, targets, properties, false );
    }

    public void runTarget( IPath buildFile, String targets, Map<String, String> properties, boolean separateJRE )
        throws CoreException
    {

        if( isLaunchRunning() )
        {
            throw new IllegalStateException( "Existing launch in progress" ); //$NON-NLS-1$
        }

        this.currentBuildFile = buildFile;

        this.currentTargets = targets;

        ILaunchConfiguration launchConfig = createLaunchConfiguration( buildFile, targets, properties, separateJRE );

        launch( launchConfig, ILaunchManager.RUN_MODE, null );

        this.currentBuildFile = null;

        this.currentTargets = null;
    }

    protected void addUserEntries( ClasspathModel model ) throws CoreException
    {

        IPath[] antLibs = sdk.getAntLibraries();

        for( IPath antLib : antLibs )
        {
            if( antLib.toFile().exists() )
            {
                model.addEntry( ClasspathModel.USER, JavaRuntime.newArchiveRuntimeClasspathEntry( antLib ) );
            }
        }
    }

    public void setVMArgs( String[] vmargs )
    {
        this.additionalVMArgs = vmargs;
    }
}
