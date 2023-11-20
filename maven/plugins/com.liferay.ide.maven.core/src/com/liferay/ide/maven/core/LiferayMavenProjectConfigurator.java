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

package com.liferay.ide.maven.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.hook.core.dd.HookDescriptorHelper;
import com.liferay.ide.hook.core.util.HookUtil;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.markers.IMavenMarkerManager;
import org.eclipse.m2e.core.internal.markers.MavenProblemInfo;
import org.eclipse.m2e.core.internal.markers.SourceLocation;
import org.eclipse.m2e.core.internal.markers.SourceLocationHelper;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;
import org.eclipse.m2e.jdt.internal.MavenClasspathHelpers;
import org.eclipse.m2e.wtp.WTPProjectsUtil;
import org.eclipse.m2e.wtp.WarPluginConfiguration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.util.ComponentUtilities;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Kamesh Sampath
 */
@SuppressWarnings("restriction")
public class LiferayMavenProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator {

	public static IPath getThemeTargetFolder(MavenProject mavenProject, IProject project) {
		IPath m2eLiferayFolder = MavenUtil.getM2eLiferayFolder(mavenProject, project);

		return m2eLiferayFolder.append(ILiferayMavenConstants.THEME_RESOURCES_FOLDER);
	}

	public LiferayMavenProjectConfigurator() {
		MavenPluginActivator mavenPluginActivator = MavenPluginActivator.getDefault();

		_mavenMarkerManager = mavenPluginActivator.getMavenMarkerManager();
	}

	@Override
	public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		monitor.beginTask(NLS.bind(Msgs.configuringLiferayProject, MavenUtil.getProject(request)), 100);

		Plugin liferayMavenPlugin = MavenUtil.getPlugin(
			request.mavenProjectFacade(), ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, monitor);

		if (!_shouldConfigure(liferayMavenPlugin, request)) {
			monitor.done();

			return;
		}

		IProject project = MavenUtil.getProject(request);

		IFile pomFile = project.getFile(IMavenConstants.POM_FILE_NAME);
		IFacetedProject facetedProject = ProjectFacetsManager.create(project, false, monitor);

		_removeLiferayMavenMarkers(project);

		monitor.worked(25);

		MavenProject mavenProject = request.mavenProject();

		List<MavenProblemInfo> errors = _findLiferayMavenPluginProblems(request, monitor);

		if (ListUtil.isNotEmpty(errors)) {
			try {
				markerManager.addErrorMarkers(
					pomFile, ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_WARNING_ID, errors);
			}
			catch (CoreException ce) {

				// no need to log this error its just best effort

			}
		}

		monitor.worked(25);

		MavenProblemInfo installProblem = null;

		if (_shouldInstallNewLiferayFacet(facetedProject)) {
			installProblem = _installNewLiferayFacet(facetedProject, request, monitor);
		}

		if (_shouldAddLiferayNature(mavenProject, facetedProject)) {
			LiferayNature.addLiferayNature(project, monitor);
		}

		monitor.worked(25);

		if (installProblem != null) {
			SourceLocation sourceLocation = installProblem.getLocation();

			this.markerManager.addMarker(
				pomFile, ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_WARNING_ID,
				installProblem.getMessage(), sourceLocation.getLineNumber(), IMarker.SEVERITY_WARNING);
		}
		else {
			String pluginType = MavenUtil.getLiferayMavenPluginType(mavenProject);

			// IDE-817 we need to mak sure that on deployment it will have the correct
			// suffix for project name

			IVirtualComponent projectComponent = ComponentCore.createComponent(project);

			try {
				if (projectComponent != null) {
					String deployedName = projectComponent.getDeployedName();

					Matcher m = _versionPattern.matcher(deployedName);

					if (m.matches()) {
						deployedName = m.group(1);

						configureDeployedName(project, deployedName);
					}

					if (pluginType != null) {
						String pluginTypeSuffix = "-" + pluginType;

						String deployedFileName = project.getName() + pluginTypeSuffix;

						if ((deployedName == null) ||
							((deployedName != null) && !deployedName.endsWith(pluginTypeSuffix))) {

							configureDeployedName(project, deployedFileName);
						}

						String oldContextRoot = ComponentUtilities.getServerContextRoot(project);

						if ((oldContextRoot == null) ||
							((oldContextRoot != null) && !oldContextRoot.endsWith(pluginTypeSuffix))) {

							IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(LiferayMavenCore.PLUGIN_ID);

							boolean setMavenPluginSuffix = prefs.getBoolean(
								LiferayMavenCore.PREF_ADD_MAVEN_PLUGIN_SUFFIX, false);

							if (setMavenPluginSuffix) {
								ComponentUtilities.setServerContextRoot(project, deployedFileName);
							}
						}
					}
				}
			}
			catch (Exception e) {
				LiferayMavenCore.logError("Unable to configure deployed name for project " + project.getName(), e);
			}

			if (Objects.equals(ILiferayMavenConstants.THEME_PLUGIN_TYPE, pluginType)) {
				IVirtualComponent component = ComponentCore.createComponent(project, true);

				if (component != null) {

					// make sure to update the main deployment folder

					WarPluginConfiguration config = new WarPluginConfiguration(mavenProject, project);

					IFolder contentFolder = project.getFolder(config.getWarSourceDirectory());

					IPath warPath = _rootPath.append(contentFolder.getProjectRelativePath());

					IPath themeFolder = _rootPath.append(getThemeTargetFolder(mavenProject, project));

					// add a link to our m2e-liferay/theme-resources folder into deployment assembly

					WTPProjectsUtil.insertLinkBefore(project, themeFolder, warPath, _rootPath, monitor);
				}
			}
		}

		if ((project != null) && ProjectUtil.isHookProject(project)) {
			HookDescriptorHelper hookDescriptor = new HookDescriptorHelper(project);

			String customJSPFolder = hookDescriptor.getCustomJSPFolder(null);

			if (customJSPFolder != null) {
				IWebProject webproject = LiferayCore.create(IWebProject.class, project);

				if ((webproject != null) && (webproject.getDefaultDocrootFolder() != null)) {
					IFolder docFolder = webproject.getDefaultDocrootFolder();
					IPath newPath = Path.fromOSString(customJSPFolder);

					IPath fullPath = docFolder.getFullPath();

					IPath pathValue = fullPath.append(newPath);

					boolean disableCustomJspValidation = LiferayMavenCore.getPreferenceBoolean(
						LiferayMavenCore.PREF_DISABLE_CUSTOM_JSP_VALIDATION);

					if (disableCustomJspValidation) {
						HookUtil.configureJSPSyntaxValidationExclude(
							project, project.getFolder(pathValue.makeRelativeTo(project.getFullPath())), true);
					}
				}
			}
		}

		monitor.worked(25);
		monitor.done();
	}

	public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor)
		throws CoreException {
	}

	public void configureRawClasspath(
			ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor)
		throws CoreException {
		
		IMavenProjectFacade mavenProjectFacade = request.mavenProjectFacade();

		IClasspathEntry jreContainerEntry = MavenClasspathHelpers.getJREContainerEntry(
			JavaCore.create(mavenProjectFacade.getProject()));

		classpath.removeEntry(jreContainerEntry.getPath());

		IClasspathEntry defaultJREContainerEntry = JavaCore.newContainerEntry(
			JavaRuntime.newJREContainerPath(JavaRuntime.getDefaultVMInstall()));

		classpath.addEntry(defaultJREContainerEntry);
		
	}

	protected void configureDeployedName(IProject project, String deployedFileName) {

		// We need to remove the file extension from deployedFileName

		int extSeparatorPos = deployedFileName.lastIndexOf('.');

		String deployedName =
			(extSeparatorPos > -1) ? deployedFileName.substring(0, extSeparatorPos) : deployedFileName;

		// From jerr's patch in MNGECLIPSE-965

		IVirtualComponent projectComponent = ComponentCore.createComponent(project);

		if ((projectComponent != null) && !deployedName.equals(projectComponent.getDeployedName())) {

			// MNGECLIPSE-2331 :

			// Seems
			// projectComponent.getDeployedName()
			// can be null

			StructureEdit moduleCore = null;

			try {
				moduleCore = StructureEdit.getStructureEditForWrite(project);

				if (moduleCore != null) {
					WorkbenchComponent component = moduleCore.getComponent();

					if (component != null) {
						component.setName(deployedName);
						moduleCore.saveIfNecessary(null);
					}
				}
			}
			finally {
				if (moduleCore != null) {
					moduleCore.dispose();
				}
			}
		}
	}

	private MavenProblemInfo _checkValidConfigDir(Plugin liferayMavenPlugin, Xpp3Dom config, String configParam) {
		MavenProblemInfo retval = null;

		String message = null;

		if (configParam != null) {
			if (config == null) {
				message = NLS.bind(Msgs.missingConfigValue, configParam);
			}
			else {
				Xpp3Dom dirNode = config.getChild(configParam);

				if (dirNode == null) {
					message = NLS.bind(Msgs.missingConfigValue, configParam);
				}
				else {
					String value = dirNode.getValue();

					if (CoreUtil.isNullOrEmpty(value)) {
						message = NLS.bind(Msgs.emptyConfigValue, configParam);
					}
					else {
						File configDir = new File(value);

						if (FileUtil.notExists(configDir) || !configDir.isDirectory()) {
							message = NLS.bind(Msgs.unusableConfigValue, configParam, value);
						}
					}
				}
			}
		}

		if (message != null) {
			SourceLocation location = SourceLocationHelper.findLocation(
				liferayMavenPlugin, SourceLocationHelper.CONFIGURATION);

			retval = new MavenProblemInfo(message, IMarker.SEVERITY_WARNING, location);
		}

		return retval;
	}

	private MavenProblemInfo _checkValidVersion(Plugin plugin, Xpp3Dom config, String versionNodeName) {
		MavenProblemInfo retval = null;

		Version liferayVersion = null;

		String version = null;

		if (config != null) {

			// check for version config node

			Xpp3Dom versionNode = config.getChild(versionNodeName);

			if (versionNode != null) {
				version = versionNode.getValue();

				try {
					liferayVersion = new Version(MavenUtil.getVersion(version));
				}
				catch (IllegalArgumentException iae) {

					// bad version

				}
			}
		}

		if ((liferayVersion == null) || liferayVersion.equals(ILiferayConstants.EMPTY_VERSION)) {

			// could not get valid liferayVersion

			SourceLocation location = SourceLocationHelper.findLocation(plugin, SourceLocationHelper.CONFIGURATION);
			String problemMsg = NLS.bind(Msgs.unusableConfigValue, versionNodeName, version);

			retval = new MavenProblemInfo(problemMsg, IMarker.SEVERITY_WARNING, location);
		}

		return retval;
	}

	private List<MavenProblemInfo> _findLiferayMavenPluginProblems(
			ProjectConfigurationRequest request, IProgressMonitor monitor)
		throws CoreException {

		List<MavenProblemInfo> warnings = new ArrayList<>();

		// first check to make sure that the AppServer* properties are available and
		// pointed to valid location

		Plugin liferayMavenPlugin = MavenUtil.getPlugin(
			request.mavenProjectFacade(), ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, monitor);

		if (liferayMavenPlugin != null) {
			Xpp3Dom config = (Xpp3Dom)liferayMavenPlugin.getConfiguration();

			MavenProblemInfo validLiferayProblemInfo = _checkValidVersion(
				liferayMavenPlugin, config, ILiferayMavenConstants.PLUGIN_CONFIG_LIFERAY_VERSION);

			if (validLiferayProblemInfo != null) {
				warnings.add(validLiferayProblemInfo);
			}

			Version mavenPluginVersion = new Version(MavenUtil.getVersion(liferayMavenPlugin.getVersion()));

			if ((mavenPluginVersion == null) || mavenPluginVersion.equals(ILiferayConstants.EMPTY_VERSION)) {

				// could not get valid version for liferaymavenPlugin

				SourceLocation location = SourceLocationHelper.findLocation(liferayMavenPlugin, "version");
				String problemMsg = NLS.bind(
					Msgs.invalidVersion, "liferay-maven-plugin", liferayMavenPlugin.getVersion());

				MavenProblemInfo versionProblem = new MavenProblemInfo(problemMsg, IMarker.SEVERITY_WARNING, location);

				warnings.add(versionProblem);
			}

			String[] configDirParams = {ILiferayMavenConstants.PLUGIN_CONFIG_APP_SERVER_PORTAL_DIR};

			for (String configParam : configDirParams) {
				MavenProblemInfo configProblemInfo = _checkValidConfigDir(liferayMavenPlugin, config, configParam);

				if (configProblemInfo != null) {
					warnings.add(configProblemInfo);
				}
			}
		}

		return warnings;
	}

	private IProjectFacetVersion _getLiferayProjectFacet(IFacetedProject facetedProject) {
		IProjectFacetVersion retval = null;

		if (facetedProject != null) {
			for (IProjectFacetVersion fv : facetedProject.getProjectFacets()) {
				IProjectFacet projectFacet = fv.getProjectFacet();

				String id = projectFacet.getId();

				if (id.contains("liferay.")) {
					retval = fv;

					break;
				}
			}
		}

		return retval;
	}

	private IFacetedProject.Action _getNewLiferayFacetInstallAction(String pluginType) {
		IFacetedProject.Action retval = null;

		IProjectFacetVersion newFacet = null;

		IDataModelProvider dataModel = null;

		if (Objects.equals(ILiferayMavenConstants.PORTLET_PLUGIN_TYPE, pluginType)) {
			newFacet = IPluginFacetConstants.LIFERAY_PORTLET_PROJECT_FACET.getDefaultVersion();
			dataModel = new MavenPortletPluginFacetInstallProvider();
		}
		else if (Objects.equals(ILiferayMavenConstants.HOOK_PLUGIN_TYPE, pluginType)) {
			newFacet = IPluginFacetConstants.LIFERAY_HOOK_PROJECT_FACET.getDefaultVersion();
			dataModel = new MavenHookPluginFacetInstallProvider();
		}
		else if (Objects.equals(ILiferayMavenConstants.EXT_PLUGIN_TYPE, pluginType)) {
			newFacet = IPluginFacetConstants.LIFERAY_EXT_PROJECT_FACET.getDefaultVersion();
			dataModel = new MavenExtPluginFacetInstallProvider();
		}
		else if (Objects.equals(ILiferayMavenConstants.LAYOUTTPL_PLUGIN_TYPE, pluginType)) {
			newFacet = IPluginFacetConstants.LIFERAY_LAYOUTTPL_PROJECT_FACET.getDefaultVersion();
			dataModel = new MavenLayoutTplPluginFacetInstallProvider();
		}
		else if (Objects.equals(ILiferayMavenConstants.THEME_PLUGIN_TYPE, pluginType)) {
			newFacet = IPluginFacetConstants.LIFERAY_THEME_PROJECT_FACET.getDefaultVersion();
			dataModel = new MavenThemePluginFacetInstallProvider();
		}
		else if (Objects.equals(ILiferayMavenConstants.WEB_PLUGIN_TYPE, pluginType)) {
			newFacet = IPluginFacetConstants.LIFERAY_WEB_PROJECT_FACET.getDefaultVersion();
			dataModel = new MavenWebPluginFacetInstallProvider();
		}

		if (newFacet != null) {
			IDataModel config = DataModelFactory.createDataModel(dataModel);

			retval = new IFacetedProject.Action(IFacetedProject.Action.Type.INSTALL, newFacet, config);
		}

		return retval;
	}

	// Copied from
	// org.eclipse.m2e.wtp.AbstractProjectConfiguratorDelegate#configureDeployedName()

	private MavenProblemInfo _installNewLiferayFacet(
			IFacetedProject facetedProject, ProjectConfigurationRequest request, IProgressMonitor monitor)
		throws CoreException {

		MavenProblemInfo retval = null;

		String pluginType = MavenUtil.getLiferayMavenPluginType(request.mavenProject());

		if (pluginType == null) {
			pluginType = ILiferayMavenConstants.DEFAULT_PLUGIN_TYPE;
		}

		Plugin liferayMavenPlugin = MavenUtil.getPlugin(
			request.mavenProjectFacade(), ILiferayMavenConstants.LIFERAY_MAVEN_PLUGIN_KEY, monitor);
		IFacetedProject.Action action = _getNewLiferayFacetInstallAction(pluginType);

		if (action != null) {
			try {
				facetedProject.modify(Collections.singleton(action), monitor);
			}
			catch (Exception e) {
				try {
					SourceLocation location = SourceLocationHelper.findLocation(
						liferayMavenPlugin, SourceLocationHelper.CONFIGURATION);

					Throwable cause = e.getCause();

					String problemMsg = NLS.bind(
						Msgs.facetInstallError, pluginType, (cause != null) ? cause.getMessage() : e.getMessage());

					retval = new MavenProblemInfo(location, e);

					retval.setMessage(problemMsg);
				}
				catch (Exception e1) {
				}

				LiferayMavenCore.logError(
					"Unable to install liferay facet " + action.getProjectFacetVersion(), e.getCause());
			}
		}

		return retval;
	}

	private void _removeLiferayMavenMarkers(IProject project) throws CoreException {
		this._mavenMarkerManager.deleteMarkers(
			project, ILiferayMavenConstants.LIFERAY_MAVEN_MARKER_CONFIGURATION_WARNING_ID);
	}

	private boolean _shouldAddLiferayNature(MavenProject mavenProject, IFacetedProject facetedProject) {
		boolean layoutTemplateProject = false;

		IProject project = facetedProject.getProject();

		IFile templateFile = project.getFile("src/main/webapp/WEB-INF/liferay-layout-templates.xml");

		if (FileUtil.exists(project) && FileUtil.exists(templateFile)) {
			layoutTemplateProject = true;
		}

		if ((mavenProject.getPlugin(ILiferayMavenConstants.BND_MAVEN_PLUGIN_KEY) != null) ||
			(mavenProject.getPlugin(ILiferayMavenConstants.MAVEN_BUNDLE_PLUGIN_KEY) != null) ||
			(mavenProject.getPlugin(ILiferayMavenConstants.LIFERAY_THEME_BUILDER_PLUGIN_KEY) != null) ||
			(mavenProject.getPlugin(ILiferayMavenConstants.LIFERAY_CSS_BUILDER_PLUGIN_KEY) != null) ||
			layoutTemplateProject) {

			return true;
		}

		return false;
	}

	/**
	 * IDE-1489 when no liferay maven plugin is found the project will be scanned
	 * for liferay specific files
	 */
	private boolean _shouldConfigure(Plugin liferayMavenPlugin, ProjectConfigurationRequest request) {
		IProject project = MavenUtil.getProject(request);
		MavenProject mavenProject = request.mavenProject();

		boolean configureAsLiferayPlugin = false;

		if (liferayMavenPlugin != null) {
			configureAsLiferayPlugin = true;
		}

		IFolder warSourceDir = _warSourceDirectory(project, mavenProject);

		if (!configureAsLiferayPlugin && (warSourceDir != null)) {
			IPath baseDir = warSourceDir.getRawLocation();
			String[] includes = {"**/liferay*.xml", "**/liferay*.properties"};

			DirectoryScanner dirScanner = new DirectoryScanner();

			dirScanner.setBasedir(baseDir.toFile());
			dirScanner.setIncludes(includes);
			dirScanner.scan();

			String[] liferayProjectFiles = dirScanner.getIncludedFiles();

			configureAsLiferayPlugin = ListUtil.isNotEmpty(liferayProjectFiles);
		}

		return configureAsLiferayPlugin;
	}

	private boolean _shouldInstallNewLiferayFacet(IFacetedProject facetedProject) {
		if (_getLiferayProjectFacet(facetedProject) == null) {
			return true;
		}

		return false;
	}

	private IFolder _warSourceDirectory(IProject project, MavenProject mavenProject) {
		IFolder retval = null;

		Plugin plugin = mavenProject.getPlugin(_MAVEN_WAR_PLUGIN_KEY);

		Xpp3Dom warPluginConfiguration = (Xpp3Dom)plugin.getConfiguration();

		if (warPluginConfiguration != null) {
			Xpp3Dom[] warSourceDirs = warPluginConfiguration.getChildren("warSourceDirectory");

			if (ListUtil.isNotEmpty(warSourceDirs)) {
				String resourceLocation = warSourceDirs[0].getValue();

				retval = project.getFolder(resourceLocation);
			}
		}

		if (retval == null) {

			/**
			 * if no explicit warSourceDirectory set we assume the default warSource
			 * directory ${basedir}/src/main/webapp refer to
			 * http://maven.apache.org/plugins/maven-war-plugin/war-mojo.html for more
			 * information
			 */
			retval = project.getFolder(_WAR_SOURCE_FOLDER);
		}

		return retval;
	}

	private static final String _MAVEN_WAR_PLUGIN_KEY = "org.apache.maven.plugins:maven-war-plugin";

	private static final String _WAR_SOURCE_FOLDER = "/src/main/webapp";

	private static final IPath _rootPath = new Path("/");
	private static final Pattern _versionPattern = Pattern.compile(
		"^(.*)-([0-9]+((.[0-9]+)?(.[0-9]+)?)*)(?:-SNAPSHOT)?$");

	private IMavenMarkerManager _mavenMarkerManager;

	private static class Msgs extends NLS {

		public static String configuringLiferayProject;
		public static String emptyConfigValue;
		public static String facetInstallError;
		public static String invalidVersion;
		public static String missingConfigValue;
		public static String unusableConfigValue;

		static {
			initializeMessages(LiferayMavenProjectConfigurator.class.getName(), Msgs.class);
		}

	}

}