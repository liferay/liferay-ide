/*******************************************************************************
 * Copyright (c) 2010, 2013 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.maven.core.aether;

import com.liferay.ide.maven.core.LiferayMavenCore;
import com.liferay.ide.maven.core.MavenUtil;

import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.m2e.core.internal.MavenPluginActivator;

/**
 * A helper to boot the repository system and a repository system session.
 */
@SuppressWarnings( "restriction" )
public class AetherUtil
{

    public static Artifact getLatestAvailableArtifact( final String gavCoords )
    {
        Artifact retval = null;

        final RepositorySystem system = newRepositorySystem();
        final RepositorySystemSession session = newRepositorySystemSession( system );

        final String latestVersion = AetherUtil.getLatestVersion( gavCoords, system, session );

        final String[] gav = gavCoords.split( ":" );

        final Artifact defaultArtifact = new DefaultArtifact( gav[0] + ":" + gav[1] + ":" + latestVersion );

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact( defaultArtifact );
        artifactRequest.addRepository( newCentralRepository() );
//        artifactRequest.addRepository( newLiferayRepository() );

        try
        {
            ArtifactResult artifactResult = system.resolveArtifact( session, artifactRequest );
            retval = artifactResult.getArtifact();
        }
        catch( ArtifactResolutionException e )
        {
            LiferayMavenCore.logError( "Unable to get latest Liferay archetype", e );

            artifactRequest.setArtifact( new DefaultArtifact( gavCoords ) );

            try
            {
                retval = system.resolveArtifact( session, artifactRequest ).getArtifact();
            }
            catch( ArtifactResolutionException e1 )
            {
                LiferayMavenCore.logError( "Unable to get default Liferay archetype", e1 );
            }
        }

        if( retval == null )
        {
            retval = defaultArtifact;
        }

        return retval;
    }

    public static String getLatestVersion( String gavCoords, RepositorySystem system, RepositorySystemSession session )
    {
        String retval = null;

        final String[] gav = gavCoords.split( ":" );

        if( gav == null || gav.length != 3 )
        {
            throw new IllegalArgumentException( "gavCoords should be group:artifactId:version" );
        }

        final Artifact artifact = new DefaultArtifact( gav[0] + ":" + gav[1] + ":[" + gav[2] + ",)" );

        final VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact( artifact );
        rangeRequest.addRepository( newCentralRepository() );
//        rangeRequest.addRepository( newLiferayRepository() );

        try
        {
            final VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );
            final Version newestVersion = rangeResult.getHighestVersion();
            final List<Version> versions = rangeResult.getVersions();

            if( versions.size() > 1 && newestVersion.toString().endsWith( "-SNAPSHOT" ) )
            {
                retval = versions.get( versions.size() - 2 ).toString();
            }
            else if( newestVersion != null )
            {
                retval = newestVersion.toString();
            }
        }
        catch( VersionRangeResolutionException e )
        {
            LiferayMavenCore.logError( "Unable to get latest artifact version.", e );
        }

        if( retval == null )
        {
            retval = gav[2];
        }

        return retval;
    }

    public static RemoteRepository newCentralRepository()
    {
        return new RemoteRepository.Builder( "central", "default", "http://repo1.maven.org/maven2/" ).build();
    }

    public static RemoteRepository newLiferayRepository()
    {
        return new RemoteRepository.Builder(
            "liferay", "default", "https://repository.liferay.com/nexus/content/groups/public/" ).build();
    }

    public static RepositorySystem newRepositorySystem()
    {
        return MavenPluginActivator.getDefault().getRepositorySystem();
    }

    public static DefaultRepositorySystemSession newRepositorySystemSession( RepositorySystem system )
    {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository( MavenUtil.getLocalRepositoryDir() );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );

        session.setTransferListener( new ConsoleTransferListener() );
        session.setRepositoryListener( new ConsoleRepositoryListener() );

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );

        return session;
    }

    public static Artifact getMavenArchtype( final String groupId, final String artifactId, final String version )
    {
        Artifact retval = null;

        final Artifact defaultArtifact = new DefaultArtifact( groupId + ":" + artifactId + ":" + version );
        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact( defaultArtifact );
        artifactRequest.addRepository( newCentralRepository() );
        final RepositorySystem system = newRepositorySystem();
        final RepositorySystemSession session = newRepositorySystemSession( system );

        try
        {
            ArtifactResult artifactResult = system.resolveArtifact( session, artifactRequest );
            retval = artifactResult.getArtifact();
        }
        catch( Exception e )
        {
            LiferayMavenCore.logError( e );
        }

        return retval;
    }
}
