/**
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
 */

package com.liferay.ide.maven.core.aether;

import com.liferay.ide.core.util.StringUtil;
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
 * @author Gregory Amerson
 * @author Seiphon Wang
 */
@SuppressWarnings("restriction")
public class AetherUtil {

	public static Artifact getAvailableArtifact(String gavCoords) {
		Artifact retval = null;

		RepositorySystem system = newRepositorySystem();

		RepositorySystemSession session = newRepositorySystemSession(system);

		Artifact defaultArtifact = new DefaultArtifact(gavCoords);

		ArtifactRequest artifactRequest = new ArtifactRequest();

		artifactRequest.setArtifact(defaultArtifact);
		artifactRequest.addRepository(newCentralRepository());

		// artifactRequest.addRepository( newLiferayRepository() );

		try {
			ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);

			retval = artifactResult.getArtifact();
		}
		catch (ArtifactResolutionException e) {
			LiferayMavenCore.logError("Unable to get latest Liferay archetype", e);

			artifactRequest.setArtifact(new DefaultArtifact(gavCoords));

			try {
				ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);

				retval = artifactResult.getArtifact();
			}
			catch (ArtifactResolutionException e1) {
				LiferayMavenCore.logError("Unable to get default Liferay archetype", e1);
			}
		}

		if (retval == null) {
			retval = defaultArtifact;
		}

		return retval;
	}

	public static Artifact getLatestAvailableArtifact(String gavCoords) {
		RepositorySystem system = newRepositorySystem();

		RepositorySystemSession session = newRepositorySystemSession(system);

		String latestVersion = getLatestVersion(gavCoords, system, session);

		String[] gav = gavCoords.split(":");

		return getAvailableArtifact(gav[0] + ":" + gav[1] + ":" + latestVersion);
	}

	public static String getLatestVersion(String gavCoords, RepositorySystem system, RepositorySystemSession session) {
		String retval = null;

		String[] gav = gavCoords.split(":");

		if ((gav == null) || (gav.length != 3)) {
			throw new IllegalArgumentException("gavCoords should be group:artifactId:version");
		}

		Artifact artifact = new DefaultArtifact(gav[0] + ":" + gav[1] + ":[" + gav[2] + ",)");

		VersionRangeRequest rangeRequest = new VersionRangeRequest();

		rangeRequest.setArtifact(artifact);
		rangeRequest.addRepository(newCentralRepository());

		// rangeRequest.addRepository( newLiferayRepository() );

		try {
			VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);

			Version newestVersion = rangeResult.getHighestVersion();
			List<Version> versions = rangeResult.getVersions();

			if ((versions.size() > 1) && StringUtil.endsWith(newestVersion, "-SNAPSHOT")) {
				Version version = versions.get(versions.size() - 2);

				retval = version.toString();
			}
			else if (newestVersion != null) {
				retval = newestVersion.toString();
			}
		}
		catch (VersionRangeResolutionException vrre) {
			LiferayMavenCore.logError("Unable to get latest artifact version.", vrre);
		}

		if (retval == null) {
			retval = gav[2];
		}

		return retval;
	}

	public static RemoteRepository newCentralRepository() {
		return new RemoteRepository.Builder(
			"central", "default", "https://repo1.maven.org/maven2/"
		).build();
	}

	public static RemoteRepository newLiferayRepository() {
		return new RemoteRepository.Builder(
			"liferay", "default", "https://repository.liferay.com/nexus/content/groups/public/"
		).build();
	}

	public static RepositorySystem newRepositorySystem() {
		MavenPluginActivator activator = MavenPluginActivator.getDefault();

		return activator.getRepositorySystem();
	}

	public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system) {
		DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

		LocalRepository localRepo = new LocalRepository(MavenUtil.getLocalRepositoryDir());

		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

		session.setTransferListener(new ConsoleTransferListener());
		session.setRepositoryListener(new ConsoleRepositoryListener());

		// uncomment to generate dirty trees
		// session.setDependencyGraphTransformer( null );

		return session;
	}

}