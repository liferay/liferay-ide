/**
 * 
 */
package com.liferay.ide.maven.core;

import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.core.facet.PortletPluginFacetInstallDataModelProvider;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderFramework;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * @author kamesh
 * 
 */
public class LiferayPortletProjectConfigurator extends
		AbstractProjectConfigurator {

	public static final String LIFERAY_PORTLET_LIB_PROVIDER = "com.liferay.ide.eclipse.plugin.portlet.libraryProvider";
	protected static final IProjectFacet dynamicWebFacet;
	protected static final IProjectFacetVersion dynamicWebVersion;

	protected static final IProjectFacet liferayPortletFacet;
	protected static final IProjectFacetVersion liferayPortletProjectFacetVersion;

	/*
	 * Dependencies
	 */
	private static final String GROUP_ID_PORTLET_API = "javax.portlet";
	private static final String ARTIFACT_ID_PORTLET_API = "portlet-api";

	private static final String GROUP_ID_LIFERAY_PORTAL_SERVICE = "com.liferay.portal";
	private static final String ARTIFACT_ID_LIFERAY_PORTAL_SERVICE = "portal-service";

	static {
		dynamicWebFacet = ProjectFacetsManager.getProjectFacet("jst.web");
		dynamicWebVersion = dynamicWebFacet.getVersion("2.5");

		liferayPortletFacet = ProjectFacetsManager
				.getProjectFacet(IPluginFacetConstants.LIFERAY_PORTLET_FACET_ID);
		liferayPortletProjectFacetVersion = liferayPortletFacet
				.getVersion("6.0");

	}

	public LiferayPortletProjectConfigurator() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator
	 * #configure
	 * (org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void configure(ProjectConfigurationRequest request,
			IProgressMonitor monitor) throws CoreException {
		MavenProject mavenProject = request.getMavenProject();
		IProject project = request.getProject();
		try {
			configureFacets(mavenProject, project, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator
	 * #mavenProjectChanged
	 * (org.eclipse.m2e.core.project.MavenProjectChangedEvent,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void mavenProjectChanged(MavenProjectChangedEvent event,
			IProgressMonitor monitor) throws CoreException {
		IMavenProjectFacade facade = event.getMavenProject();
		if (facade != null) {
			IProject project = facade.getProject();
			if (isWTPProject(project)) {
				MavenProject mavenProject = facade.getMavenProject(monitor);
				configureFacets(mavenProject, project, monitor);
			}
		}
		super.mavenProjectChanged(event, monitor);
	}

	private boolean isWTPProject(IProject project) {
		return ModuleCoreNature.getModuleCoreNature(project) != null;
	}

	/**
	 * 
	 * @param mavenProject
	 * @param project
	 * @throws CoreException
	 */
	private void configureFacets(MavenProject mavenProject, IProject project,
			IProgressMonitor monito) throws CoreException {
		String packaging = mavenProject.getPackaging();
		String portletVersion = dependecyVersion(mavenProject,
				GROUP_ID_PORTLET_API, ARTIFACT_ID_PORTLET_API);

		String liferayPortalServiceVersion = dependecyVersion(mavenProject,
				GROUP_ID_LIFERAY_PORTAL_SERVICE,
				ARTIFACT_ID_LIFERAY_PORTAL_SERVICE);

		if (portletVersion != null && liferayPortalServiceVersion != null) {
			final IFacetedProject lfrProject = ProjectFacetsManager.create(
					project, true, monito);
			if (lfrProject != null && "war".equals(packaging)) {
				installLiferayPortletFacet(lfrProject, portletVersion,
						liferayPortalServiceVersion, monito);
			}
		}

	}

	private void installLiferayPortletFacet(IFacetedProject lfrProject,
			String portletVersion, String liferayPortalServiceVersion,
			IProgressMonitor monito) throws CoreException {

		IDataModel model = createLiferayPortletDataModel(lfrProject, monito);
		lfrProject.installProjectFacet(liferayPortletProjectFacetVersion,
				model, monito);

	}

	private IDataModel createLiferayPortletDataModel(
			IFacetedProject lfrProject, IProgressMonitor monito)
			throws CoreException {
		IDataModel config = (IDataModel) new PortletPluginFacetInstallDataModelProvider()
				.create();

		LibraryInstallDelegate libraryDelegate = new LibraryInstallDelegate(
				lfrProject, liferayPortletProjectFacetVersion);

		ILibraryProvider provider = LibraryProviderFramework
				.getProvider(LIFERAY_PORTLET_LIB_PROVIDER);

		libraryDelegate.setLibraryProvider(provider);

		config.setProperty(
				IPluginProjectDataModelProperties.LIFERAY_PLUGIN_LIBRARY_DELEGATE,
				libraryDelegate);

		return config;
	}

	/**
	 * 
	 * @param mavenProject
	 * @param groupID
	 * @param artifactID
	 * @return
	 */
	public String dependecyVersion(MavenProject mavenProject, String groupID,
			String artifactID) {
		List<Artifact> artifacts = new ArrayList<Artifact>();
		ArtifactFilter filter = new org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter(
				Artifact.SCOPE_TEST);
		for (Artifact artifact : mavenProject.getArtifacts()) {
			if (filter.include(artifact)) {
				artifacts.add(artifact);
			}
		}
		for (Artifact artifact : artifacts) {
			String groupId = artifact.getGroupId();
			if (groupId != null && (groupId.equals(groupID))) {
				String artifactId = artifact.getArtifactId();
				if (artifactId != null && artifactId.equals(artifactID)) {
					return artifact.getVersion();
				}
			}
		}
		return null;
	}

}
