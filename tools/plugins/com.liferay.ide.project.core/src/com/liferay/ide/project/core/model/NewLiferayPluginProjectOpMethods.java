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
package com.liferay.ide.project.core.model;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.LiferayProjectCore;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class NewLiferayPluginProjectOpMethods
{

    public static final Status execute( final NewLiferayPluginProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final ILiferayProjectProvider projectProvider = op.getProjectProvider().content( true );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            if ( status.isOK() )
            {
                updateProjectDefaultBuildType( op );
            }

            retval = StatusBridge.create( status );
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay plugin project."; //$NON-NLS-1$
            LiferayProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }
    
    public static String getPluginTypeSuffix( final PluginType pluginType )
    {
        String suffix = null;

        switch ( pluginType )
        {
            case portlet:
                suffix = "-portlet"; //$NON-NLS-1$
                break;
            case ext:
                suffix = "-ext"; //$NON-NLS-1$
                break;
            case hook:
                suffix = "-hook"; //$NON-NLS-1$
                break;
            case layouttpl:
                suffix = "-layouttpl"; //$NON-NLS-1$
                break;
            case theme:
                suffix = "-theme"; //$NON-NLS-1$
                break;
        }

        return suffix;
    }

    public static Set<String> getPossibleProfileIds( NewLiferayPluginProjectOp op, boolean includeNewProfiles )
    {
        final String activeProfilesValue = op.getActiveProfilesValue().content();

        final Path currentLocation = op.getLocation().content();

        final File param = currentLocation != null ? currentLocation.toFile() : null;

        final List<String> systemProfileIds =
            op.getProjectProvider().content().getData( "profileIds", String.class, param );

        final ElementList<NewLiferayProfile> newLiferayProfiles = op.getNewLiferayProfiles();

        final Set<String> possibleProfileIds = new HashSet<String>();

        if( ! CoreUtil.isNullOrEmpty( activeProfilesValue ) )
        {
            final String[] vals = activeProfilesValue.split( "," );

            if( ! CoreUtil.isNullOrEmpty( vals ) )
            {
                for( String val : vals )
                {
                    if( ! possibleProfileIds.contains( val ) )
                    {
                        possibleProfileIds.add( val );
                    }
                }
            }
        }

        if( ! CoreUtil.isNullOrEmpty( systemProfileIds ) )
        {
            for( Object systemProfileId : systemProfileIds )
            {
                if( systemProfileId != null )
                {
                    final String val = systemProfileId.toString();

                    if( ! possibleProfileIds.contains( val ) )
                    {
                       possibleProfileIds.add( val );
                    }
                }
            }
        }

        if( includeNewProfiles )
        {
            for( NewLiferayProfile newLiferayProfile : newLiferayProfiles )
            {
                final String newId = newLiferayProfile.getId().content();

                if( ( ! CoreUtil.isNullOrEmpty( newId ) ) && ( ! possibleProfileIds.contains( newId ) ) )
                {
                    possibleProfileIds.add( newId );
                }
            }
        }

        return possibleProfileIds;
    }

    public static void updateActiveProfilesValue( final NewLiferayPluginProjectOp op, final ElementList<Profile> profiles )
    {
        final StringBuilder sb = new StringBuilder();

        if( profiles.size() > 0 )
        {
            for( Profile profile : profiles )
            {
                if( ! profile.getId().empty() )
                {
                    sb.append( profile.getId().content() );
                    sb.append( ',' );
                }
            }
        }

        // remove trailing ','
        op.setActiveProfilesValue( sb.toString().replaceAll( "(.*),$", "$1" ) );
    }

    public static void updateLocation( final NewLiferayPluginProjectOp op, final Path baseLocation )
    {
        final String projectName = op.getProjectName().content();

        String suffix = null;

        if( "ant".equals( op.getProjectProvider().content( true ).getShortName() ) ) //$NON-NLS-1$
        {
            suffix = getPluginTypeSuffix( op.getPluginType().content( true ) );

            if( suffix != null )
            {
                // check if project name already contains suffix
                if( projectName.endsWith( suffix ) )
                {
                    suffix = null;
                }
            }
        }

        final String dirName = projectName + ( suffix == null ? StringPool.EMPTY : suffix );
        final Path newLocation = baseLocation.append( dirName );

        op.setLocation( newLocation );
    }

    private static void updateProjectDefaultBuildType( final NewLiferayPluginProjectOp op )
    {
        try
        {
            final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( LiferayProjectCore.PLUGIN_ID );
            prefs.put( LiferayProjectCore.PREF_DEFAULT_PROJECT_BUILD_TYPE_OPTION, op.getProjectProvider().text() );
            prefs.flush();
        }
        catch( Exception e )
        {
            final String msg = "Error update project default build type."; //$NON-NLS-1$
            LiferayProjectCore.logError( msg, e );
        }
    }
}
