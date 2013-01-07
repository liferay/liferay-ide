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

package com.liferay.ide.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.core.AntCorePreferences;
import org.eclipse.ant.core.IAntClasspathEntry;
import org.eclipse.ant.internal.launching.AntLaunchingUtil;
import org.eclipse.ant.internal.launching.launchConfigurations.AntClasspathProvider;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

@SuppressWarnings( "restriction" )
public class SDKClasspathProvider extends AntClasspathProvider
{

    public static final String ID = "com.liferay.ide.sdk.SDKClasspathProvider"; //$NON-NLS-1$

    public SDKClasspathProvider()
    {
        super();
    }

    @Override
    public IRuntimeClasspathEntry[] computeUnresolvedClasspath( ILaunchConfiguration configuration )
        throws CoreException
    {

        IRuntimeClasspathEntry[] retval = null;

        IRuntimeClasspathEntry[] superEntries = super.computeUnresolvedClasspath( configuration );

        boolean separateVM = AntLaunchingUtil.isSeparateJREAntBuild( configuration );

        if( separateVM )
        {
            List<IRuntimeClasspathEntry> newEntries = new ArrayList<IRuntimeClasspathEntry>();

            Collections.addAll( newEntries, superEntries );

            AntCorePreferences prefs = AntCorePlugin.getPlugin().getPreferences();

            IAntClasspathEntry[] antClasspathEntries = prefs.getContributedClasspathEntries();

            for( int i = 0; i < antClasspathEntries.length; i++ )
            {
                newEntries.add( JavaRuntime.newStringVariableClasspathEntry( antClasspathEntries[i].getLabel() ) );
            }

            retval = newEntries.toArray( new IRuntimeClasspathEntry[0] );
        }
        else
        {
            retval = superEntries;
        }

        return retval;
    }

}
