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
import com.liferay.ide.project.core.LiferayProjectCore;

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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.m2e.core.internal.MavenPluginActivator;

/**
 * A helper to boot the repository system and a repository system session.
 */
@SuppressWarnings( "restriction" )
public class AetherUtil
{

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

    public static Artifact getLatestAvailableLiferayArtifact( final String groupId, final String artifactId )
    {
        Artifact retval = null;

        final RepositorySystem system = newRepositorySystem();
        final RepositorySystemSession session = newRepositorySystemSession( system );

        final String latestVersion = AetherUtil.getLatestVersion( groupId, artifactId, "6", "6.2.0-ga1", system, session );

        final Artifact defaultArtifact = new DefaultArtifact( groupId + ":" + artifactId + ":" + latestVersion );

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
        }


        if( retval == null )
        {
            retval = defaultArtifact;
        }

        return retval;
    }

    public static String getLatestVersion(
        String group, String artifactId, String startVersion, String defaultVersion, RepositorySystem system,
        RepositorySystemSession session )
    {
        String retval = null;

        final Artifact artifact = new DefaultArtifact( group + ":" + artifactId + ":[" + startVersion + ",)" );

        final VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact( artifact );
        rangeRequest.addRepository( newCentralRepository() );
//        rangeRequest.addRepository( newLiferayRepository() );

        try
        {
            final VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );
            final Version newestVersion = rangeResult.getHighestVersion();
            final List<Version> versions = rangeResult.getVersions();

            if( versions.size() > 1 && newestVersion.toString().endsWith( "-SNAPSHOT" ) &&
                shouldUseSnapshotVersions() == false )
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
            retval = defaultVersion;
        }

        return retval;
    }

    private static boolean shouldUseSnapshotVersions(){
        final IScopeContext[] prefContexts = { DefaultScope.INSTANCE, InstanceScope.INSTANCE };

        return  Platform.getPreferencesService().getBoolean(
                LiferayProjectCore.PLUGIN_ID, LiferayProjectCore.PREF_USE_SNAPSHOT_VERSION, false, prefContexts );
    }

}
