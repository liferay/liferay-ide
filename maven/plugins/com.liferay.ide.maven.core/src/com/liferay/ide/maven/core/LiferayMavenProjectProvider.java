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
package com.liferay.ide.maven.core;

import com.liferay.ide.core.AbstractLiferayProjectProvider;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.maven.core.aether.AetherUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.archetype.catalog.Archetype;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.sapphire.platform.PathBridge;


/**
 * @author Gregory Amerson
 */
public class LiferayMavenProjectProvider extends AbstractLiferayProjectProvider
{

    public LiferayMavenProjectProvider()
    {
        super( new Class<?>[] { IProject.class } );
    }

    public IStatus createNewProject( Object operation, IProgressMonitor monitor ) throws CoreException
    {
        if( ! (operation instanceof NewLiferayPluginProjectOp ) )
        {
            throw new IllegalArgumentException( "Operation must be of type NewLiferayPluginProjectOp" ); //$NON-NLS-1$
        }

        IStatus retval = null;

        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.class.cast( operation );

        final String groupId = op.getGroupId().content();
        final String artifactId = op.getProjectName().content();
        final String version = op.getVersion().content();
        final String javaPackage = op.getGroupId().content();
        final String profiles = op.getProfiles().content();

        IPath location = PathBridge.create( op.getLocation().content() );
        // for location we should use the parent location

        if( location.lastSegment().equals( artifactId ) )
        {
            // use parent dir since maven archetype will generate new dir under this location
            location = location.removeLastSegments( 1 );
        }

        final PluginType pluginType = op.getPluginType().content( true );
        final IPortletFramework portletFramework = op.getPortletFramework().content( true );
        final String archetypeGroupId = "com.liferay.maven.archetypes"; //$NON-NLS-1$

        String archetypeType = null;

        if( pluginType.equals( PluginType.portlet ) && portletFramework.isRequiresAdvanced() )
        {
            String frameworkName = op.getPortletFrameworkAdvanced().content( true ).getShortName();

            archetypeType = "portlet-" + frameworkName.replace( "_", "-" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        else
        {
            archetypeType = pluginType.name();
        }

        final String archetypeArtifactId = "liferay-" + archetypeType + "-archetype"; //$NON-NLS-1$ //$NON-NLS-2$

        final RepositorySystem system = AetherUtil.newRepositorySystem();

        final RepositorySystemSession session = AetherUtil.newRepositorySystemSession( system );

        // get latest liferay archetype
        monitor.beginTask( "Determining latest Liferay maven plugin archetype version.", IProgressMonitor.UNKNOWN ); //$NON-NLS-1$
        final String archetypeVersion = getLatestLiferayArchetype( archetypeArtifactId, system, session ).getVersion();

        Archetype archetype = new Archetype();

        archetype.setArtifactId( archetypeArtifactId );
        archetype.setGroupId( archetypeGroupId );
        archetype.setModelEncoding( "UTF-8" );
        archetype.setVersion( archetypeVersion );

        final Properties properties = new Properties();

        final ResolverConfiguration resolverConfig = new ResolverConfiguration();

        if( ! CoreUtil.isNullOrEmpty( profiles ) )
        {
            resolverConfig.setSelectedProfiles( profiles );
        }

        final ProjectImportConfiguration configuration = new ProjectImportConfiguration( resolverConfig );

        final List<IProject> newProjects =
            MavenPlugin.getProjectConfigurationManager().createArchetypeProjects(
                location, archetype, groupId, artifactId, version, javaPackage, properties, configuration, monitor );

        if( CoreUtil.isNullOrEmpty( newProjects ) )
        {
            retval = LiferayMavenCore.createErrorStatus( "New project was not created due to unknown error" );
        }
        else
        {
            if( op.getPluginType().content().equals( PluginType.portlet ) )
            {
                retval = op.getPortletFramework().content().postProjectCreated( newProjects.get( 0 ), monitor );
            }
        }

        if( retval == null )
        {
            retval = Status.OK_STATUS;
        }

        return retval;
    }

    private Artifact getLatestLiferayArchetype( String archetypeArtifactId, RepositorySystem system, RepositorySystemSession session )
    {
        final String groupId = "com.liferay.maven.archetypes";

        String latestVersion = MavenUtil.getLatestVersion( groupId, archetypeArtifactId, "6", system, session );

        Artifact artifact = new DefaultArtifact( "com.liferay.maven.archetypes:" + archetypeArtifactId + ":" + latestVersion );

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact( artifact );
        artifactRequest.addRepository( AetherUtil.newCentralRepository() );

        try
        {
            ArtifactResult artifactResult = system.resolveArtifact( session, artifactRequest );
            artifact = artifactResult.getArtifact();
        }
        catch( ArtifactResolutionException e )
        {
            e.printStackTrace();
        }

        return artifact;
    }

    @Override
    public String[] getPossibleVersions()
    {
        String[] retval = new String[0];

        List<String> possibleVersions = new ArrayList<String>();

        RepositorySystem system = AetherUtil.newRepositorySystem();

        RepositorySystemSession session = AetherUtil.newRepositorySystemSession( system );

        Artifact artifact = new DefaultArtifact( "com.liferay.portal:portal-service:[6,)" );

        RemoteRepository repo = AetherUtil.newCentralRepository();

        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact( artifact );
        rangeRequest.addRepository( repo );

        try
        {
            final VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );

            final List<Version> versions = rangeResult.getVersions();

            for( Version version : versions )
            {
                final String val = version.toString();

                if( ! "6.2.0".equals( val ) )
                {
                    possibleVersions.add( val );
                }
            }

            retval = possibleVersions.toArray( new String[0] );

//            Arrays.sort( retval );
        }
        catch( VersionRangeResolutionException e )
        {
        }

        return retval;
    }

    public ILiferayProject provide( Object type )
    {
        if( type instanceof IProject )
        {
            final IProject project = (IProject) type;

            try
            {
                if( MavenUtil.isMavenProject( project ) )
                {
                    return new LiferayMavenProject( project );
                }

            }
            catch( CoreException e )
            {
                LiferayMavenCore.logError(
                    "Unable to create ILiferayProject from maven project " + project.getName(), e ); //$NON-NLS-1$
            }
        }

        return null;
    }

}
