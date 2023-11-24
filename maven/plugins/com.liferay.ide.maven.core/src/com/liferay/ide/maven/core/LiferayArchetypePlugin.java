package com.liferay.ide.maven.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.common.ArchetypeArtifactManager;
import org.apache.maven.archetype.exception.UnknownArchetype;
import org.apache.maven.archetype.metadata.ArchetypeDescriptor;
import org.apache.maven.archetype.metadata.RequiredProperty;
import org.apache.maven.archetype.source.ArchetypeDataSource;
import org.apache.maven.archetype.source.ArchetypeDataSourceException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.IArchetype;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

@Component(service = LiferayArchetypePlugin.class)
@SuppressWarnings({ "restriction", "deprecation"})
public class LiferayArchetypePlugin {

	public static final String ARCHETYPE_PREFIX = "archetype";

	@Reference
	LiferayArchetypeGenerator archetypeGenerator;

	@Reference
	IMaven maven;

	public LiferayArchetypePlugin() {
	}

	public LiferayArchetypeGenerator getGenerator() {
		return archetypeGenerator;
	}

	/**
	 * Gets the required properties of an {@link IArchetype}.
	 *
	 * @param archetype the archetype possibly declaring required properties
	 * @param remoteArchetypeRepository the remote archetype repository, can be null.
	 * @param monitor the progress monitor, can be null.
	 * @return the required properties of the archetypes, null if none is found.
	 * @throws CoreException if no archetype can be resolved
	 */
	public List<RequiredProperty> getRequiredProperties(LiferayMavenArchetype archetype, IProgressMonitor monitor)
		throws CoreException {

		Assert.isNotNull(archetype, "Archetype can not be null");

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		final String groupId = archetype.getGroupId();
		final String artifactId = archetype.getArtifactId();
		final String version = archetype.getVersion();

		final List<ArtifactRepository> repositories = new ArrayList<>(maven.getArtifactRepositories());

		return maven.createExecutionContext(
		).execute(
			(context, monitor1) -> {
				ArtifactRepository localRepository = context.getLocalRepository();

				if (archetypeArtifactManager.isFileSetArchetype(
						groupId, artifactId, version, null, localRepository, repositories,
						context.newProjectBuildingRequest())) {

					ArchetypeDescriptor descriptor;

					try {
						descriptor = archetypeArtifactManager.getFileSetArchetypeDescriptor(
							groupId, artifactId, version, null, localRepository, repositories,
							context.newProjectBuildingRequest());
					}
					catch (UnknownArchetype ex) {
						throw new CoreException(Status.error("UnknownArchetype", ex));
					}

					return descriptor.getRequiredProperties();
				}

				return null;
			},
			monitor
		);
	}

	public void updateLocalCatalog(Archetype archetype) throws CoreException {
		maven.createExecutionContext(
		).execute(
			(ctx, m) -> {
				ProjectBuildingRequest request = ctx.newProjectBuildingRequest();

				try {
					ArchetypeDataSource source = archetypeDataSourceMap.get("catalog");

					source.updateCatalog(request, archetype);
				}
				catch (ArchetypeDataSourceException e) {
				}

				return null;
			},
			null
		);
	}

	@Activate
	void activate() throws ComponentLookupException, PlexusContainerException {
		final Module logginModule = new AbstractModule() {

			@Override
			protected void configure() {
				bind(
					ILoggerFactory.class
				).toInstance(
					LoggerFactory.getILoggerFactory()
				);
			}

		};
	    final ContainerConfiguration cc = new DefaultContainerConfiguration() //
	        .setClassWorld(new ClassWorld("plexus.core", ArchetypeArtifactManager.class.getClassLoader())) //$NON-NLS-1$
	        .setClassPathScanning(PlexusConstants.SCANNING_INDEX) //
	        .setAutoWiring(true) //
	        .setName("plexus"); //$NON-NLS-1$
		container = new DefaultPlexusContainer(cc, logginModule);
		archetypeArtifactManager = container.lookup(ArchetypeArtifactManager.class);
		archetypeDataSourceMap = container.lookupMap(ArchetypeDataSource.class);
	}

	@Deactivate
	void shutdown() throws IOException {
		container.dispose();
	}

	private ArchetypeArtifactManager archetypeArtifactManager;
	private Map<String, ArchetypeDataSource> archetypeDataSourceMap;
	private DefaultPlexusContainer container;

}