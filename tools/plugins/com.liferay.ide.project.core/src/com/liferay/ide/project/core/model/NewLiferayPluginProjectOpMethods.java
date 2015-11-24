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
package com.liferay.ide.project.core.model;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.descriptor.RemoveSampleElementsOperation;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Tao Tao
 * @author Kuo Zhang
 * @author Terry Jia
 */
public class NewLiferayPluginProjectOpMethods
{

    public static boolean canUseCustomLocation( NewLiferayPluginProjectOp op )
    {
        boolean retval = false;

        if( op.getProjectProvider().content( true ).getShortName().equals( "maven" ) )
        {
            retval = true;
        }

        return retval;
    }

    public static final Status execute( final NewLiferayPluginProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final NewLiferayProjectProvider<NewLiferayPluginProjectOp> projectProvider =
                            op.getProjectProvider().content( true );

            //IDE-1306  If the user types too quickly all the model changes may not have propagated
            final Path projectLocation = op.getLocation().content();
            updateLocation( op, projectLocation );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            if ( status.isOK() )
            {
                updateProjectPrefs( op );

                removeSampleCodeAndFiles( op );
            }

            retval = StatusBridge.create( status );
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay plugin project."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

    public static String getFrameworkName( NewLiferayPluginProjectOp op )
    {
        final IPortletFramework portletFramework = op.getPortletFramework().content();

        String frameworkName = portletFramework.getShortName();

        if( portletFramework.isRequiresAdvanced() )
        {
            frameworkName = op.getPortletFrameworkAdvanced().content().getShortName();
        }

        return frameworkName;
    }

    public static String getMavenParentPomGroupId( NewLiferayPluginProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> groupId =
                op.getProjectProvider().content().getData( "parentGroupId", String.class, parentProjectDir );

            if( ! groupId.isEmpty() )
            {
                retval = groupId.get( 0 );
            }
        }

        return retval;
    }

    public static String getMavenParentPomVersion( NewLiferayPluginProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> version =
                op.getProjectProvider().content().getData( "parentVersion", String.class, parentProjectDir );

            if( !version.isEmpty() )
            {
                retval = version.get( 0 );
            }
        }

        return retval;
    }

    public static String getPluginTypeSuffix( final PluginType pluginType )
    {
        String suffix = null;

        switch ( pluginType )
        {
            case portlet:
            case servicebuilder:
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
            case web:
                suffix = "-web"; //$NON-NLS-1$
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
                    if( !possibleProfileIds.contains( val ) && !val.contains( StringPool.SPACE ) )
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

                    if( !possibleProfileIds.contains( val ) && !val.contains( StringPool.SPACE ) )
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

                if( ( !CoreUtil.isNullOrEmpty( newId ) ) && ( !possibleProfileIds.contains( newId ) ) &&
                    ( !newId.contains( StringPool.SPACE ) ) )
                {
                    possibleProfileIds.add( newId );
                }
            }
        }

        return possibleProfileIds;
    }

    public static String getProjectNameWithSuffix( final NewLiferayPluginProjectOp op )
    {
        final String projectName = op.getProjectName().content();

        String suffix = null;

        if( projectName != null )
        {
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
        }

        return ( projectName == null ? StringPool.EMPTY : projectName ) +
                        ( suffix == null ? StringPool.EMPTY : suffix );
    }

    private static IStatus removeSampleCodeAndFiles( NewLiferayPluginProjectOp op )
    {
        IStatus status = org.eclipse.core.runtime.Status.OK_STATUS;

        final boolean includeSampleCode = op.getIncludeSampleCode().content();

        if( ! includeSampleCode )
        {
            final IProject project = CoreUtil.getLiferayProject( op.getFinalProjectName().content() );

            if( project != null && project.exists() )
            {
                ProjectCore.operate( project, RemoveSampleElementsOperation.class );

                // delete sample files: view.jsp, main.css, main.js
                try
                {
                    final IWebProject webproject = LiferayCore.create( IWebProject.class, project );

                    if( webproject != null )
                    {
                        final IFolder docroot = webproject.getDefaultDocrootFolder();

                        final IFile[] sampleFiles =
                        {
                            docroot.getFile( "view.jsp" ),
                            docroot.getFile( "css/main.css" ),
                            docroot.getFile( "js/main.js" )
                        };

                        for( IFile file : sampleFiles )
                        {
                            if( file != null && file.exists() )
                            {
                                file.delete( true, new NullProgressMonitor() );

                                if( file.getParent().members().length == 0 )
                                {
                                    CoreUtil.deleteResource( file.getParent() );
                                }
                            }
                        }
                    }
                }
                catch( CoreException e )
                {
                    ProjectCore.logError( "Error deleting sample files.", e );
                }
           }
        }

        return status;
    }

    public static boolean supportsWebTypePlugin( NewLiferayPluginProjectOp op )
    {
        boolean retval = false;

        if( op.getProjectProvider().content( true ).getShortName().equals( "maven" ) )
        {
            retval = true;
        }
        else
        {
            SDK sdk = null;

            try
            {
                sdk = SDKUtil.getWorkspaceSDK();
            }
            catch( CoreException e )
            {
            }

            if ( sdk == null )
            {
                final Path sdkLocation = op.getSdkLocation().content();

                if ( sdkLocation != null )
                {
                    sdk = SDKUtil.createSDKFromLocation( PathBridge.create( sdkLocation ) );
                }
            }

            if( sdk != null )
            {
                final Version version = new Version( sdk.getVersion() );

                final boolean greaterThan700 = CoreUtil.compareVersions( version, ILiferayConstants.V700 ) >= 0;

                if( greaterThan700 )
                {
                    retval = true;
                }
            }
            else
            {
                retval = true;
            }
        }

        return retval;
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
        final String projectName = getProjectNameWithSuffix( op );

        if ( baseLocation == null)
        {
            return ;
        }

        final String lastSegment = baseLocation.lastSegment();

        if ( baseLocation!= null && baseLocation.segmentCount()>0)
        {
            if ( lastSegment.equals( projectName ))
            {
                return;
            }
        }

        final Path newLocation = baseLocation.append( projectName );

        op.setLocation( newLocation );
    }

    private static void updateProjectPrefs( final NewLiferayPluginProjectOp op )
    {
        try
        {
            final IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ProjectCore.PLUGIN_ID );

            prefs.put( ProjectCore.PREF_DEFAULT_PLUGIN_PROJECT_BUILD_TYPE_OPTION, op.getProjectProvider().text() );
            prefs.putBoolean( ProjectCore.PREF_INCLUDE_SAMPLE_CODE, op.getIncludeSampleCode().content() );
            prefs.putBoolean( ProjectCore.PREF_CREATE_NEW_PORLET, op.getCreateNewPortlet().content() );

            if( "maven".equalsIgnoreCase( op.getProjectProvider().text() ) )
            {
                prefs.put( ProjectCore.PREF_DEFAULT_PLUGIN_PROJECT_MAVEN_GROUPID, op.getGroupId().content() );
            }

            prefs.flush();
        }
        catch( Exception e )
        {
            final String msg = "Error updating default project build type."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );
        }
    }
}
