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

package com.liferay.ide.server.util;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.properties.PortalPropertiesConfiguration;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalBundleFactory;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.remote.IRemoteServer;
import com.liferay.ide.server.remote.IServerManagerConnection;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.PropertiesConfiguration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.ServerPlugin;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Tao Tao
 * @author Kuo Zhang
 * @author Simon Jiang
 * @author Terry Jia
 */
@SuppressWarnings("restriction")
public class ServerUtil {

	public static IStatus addPortalRuntime() {
		return addPortalRuntime(null);
	}

	public static IStatus addPortalRuntime(String serverName) {
		IProject project = LiferayWorkspaceUtil.getWorkspaceProject();

		try {
			if (project == null) {
				return LiferayServerCore.createErrorStatus("Can not get a valid Liferay Workspace project.");
			}

			IPath bundlesLocation = LiferayWorkspaceUtil.getBundleHomePath(project);

			if (FileUtil.exists(bundlesLocation)) {
				PortalBundle bundle = LiferayServerCore.newPortalBundle(bundlesLocation);

				if (bundle == null) {
					return LiferayServerCore.createErrorStatus("Bundle can not be found in:" + bundlesLocation);
				}

				if (serverName == null) {
					serverName = bundle.getServerReleaseInfo();
				}

				IRuntime runtime = getRuntime(serverName);

				if ((runtime != null) && bundlesLocation.equals(runtime.getLocation())) {
					deleteRuntimeAndServer(PortalRuntime.ID, bundlesLocation.toFile());
				}

				addPortalRuntimeAndServer(serverName, bundlesLocation, new NullProgressMonitor());

				IProject pluginsSDK = CoreUtil.getProject(
					LiferayWorkspaceUtil.getPluginsSDKDir(FileUtil.toPortableString(project.getLocation())));

				if (FileUtil.exists(pluginsSDK)) {
					SDK sdk = SDKUtil.createSDKFromLocation(pluginsSDK.getLocation());

					if (sdk != null) {
						Map<String, String> appServerPropertiesMap = new HashMap<>();

						appServerPropertiesMap.put(
							"app.server.deploy.dir", FileUtil.toOSString(bundle.getAppServerDeployDir()));
						appServerPropertiesMap.put("app.server.dir", FileUtil.toOSString(bundle.getAppServerDir()));
						appServerPropertiesMap.put(
							"app.server.lib.global.dir", FileUtil.toOSString(bundle.getAppServerLibGlobalDir()));
						appServerPropertiesMap.put(
							"app.server.parent.dir", FileUtil.toOSString(bundle.getLiferayHome()));
						appServerPropertiesMap.put(
							"app.server.portal.dir", FileUtil.toOSString(bundle.getAppServerPortalDir()));
						appServerPropertiesMap.put("app.server.type", bundle.getType());

						sdk.addOrUpdateServerProperties(appServerPropertiesMap);

						pluginsSDK.refreshLocal(IResource.DEPTH_INFINITE, null);

						sdk.validate(true);
					}
				}
			}
		}
		catch (Exception e) {
			return LiferayServerCore.createErrorStatus("Add Liferay server failed", e);
		}

		return Status.OK_STATUS;
	}

	public static void addPortalRuntimeAndServer(String serverRuntimeName, IPath location, IProgressMonitor monitor)
		throws CoreException {

		IRuntimeType portalRuntimeType = ServerCore.findRuntimeType(PortalRuntime.ID);

		IRuntimeWorkingCopy runtimeWC = portalRuntimeType.createRuntime(null, monitor);

		serverRuntimeName = _setRuntimeName(runtimeWC, serverRuntimeName, -1);

		runtimeWC.setLocation(location);

		runtimeWC.save(true, monitor);

		IServerType serverType = ServerCore.findServerType(PortalServer.ID);

		IServerWorkingCopy serverWC = serverType.createServer(serverRuntimeName, null, runtimeWC, monitor);

		serverWC.setName(serverRuntimeName);
		serverWC.save(true, monitor);
	}

	public static Map<String, String> configureAppServerProperties(ILiferayRuntime liferayRuntime) {
		return getSDKRequiredProperties(liferayRuntime);
	}

	public static Map<String, String> configureAppServerProperties(IProject project) throws CoreException {
		try {
			return getSDKRequiredProperties(getLiferayRuntime(project));
		}
		catch (CoreException ce) {
			throw new CoreException(LiferayServerCore.createErrorStatus(ce));
		}
	}

	public static GogoBundleDeployer createBundleDeployer(PortalRuntime portalRuntime, IServer server)
		throws Exception {

		return new GogoBundleDeployer(server.getHost(), 11311);
	}

	public static IStatus createErrorStatus(String msg) {
		return new Status(IStatus.ERROR, LiferayServerCore.PLUGIN_ID, msg);
	}

	public static void deleteRuntimeAndServer(String runtimeType, File portalBundle) throws Exception {
		IRuntime[] runtimes = ServerCore.getRuntimes();

		IRuntime targetRuntime = null;

		for (IRuntime runtime : runtimes) {
			IRuntimeType rt = runtime.getRuntimeType();

			String runtimeId = rt.getId();

			if (runtimeId.equals(runtimeType)) {
				IPath runtimeLocation = runtime.getLocation();

				File runtimeFile = runtimeLocation.toFile();

				File cRuntimeFile = runtimeFile.getCanonicalFile();

				if (cRuntimeFile.equals(portalBundle)) {
					targetRuntime = runtime;
				}
			}
		}

		if (targetRuntime != null) {
			IServer[] servers = ServerCore.getServers();

			for (IServer server : servers) {
				IRuntime runtime = server.getRuntime();

				if ((runtime != null) && runtime.equals(targetRuntime)) {
					server.delete();
				}
			}

			targetRuntime.delete();
		}
	}

	public static void deleteWorkspaceServerAndRuntime(IProject project) {
		IWorkspaceProject liferayWorkpsaceProject = LiferayCore.create(IWorkspaceProject.class, project);

		String bundleHome = liferayWorkpsaceProject.getLiferayHome();

		if ((liferayWorkpsaceProject != null) && (bundleHome != null)) {
			Stream.of(
				ServerCore.getServers()
			).filter(
				server -> server != null
			).filter(
				server -> {
					IRuntime runtime = server.getRuntime();

					if (runtime != null) {
						IPath bundleHomePath = LiferayWorkspaceUtil.getBundleHomePath(project);

						if (Objects.isNull(bundleHomePath)) {
							return false;
						}

						return bundleHomePath.equals(runtime.getLocation());
					}

					return true;
				}
			).forEach(
				server -> {
					try {
						IRuntime runtime = server.getRuntime();

						server.delete();

						if (runtime != null) {
							runtime.delete();
						}
					}
					catch (Exception e) {
						LiferayServerCore.logError("Failed to delete server and runtime", e);
					}
				}
			);
		}
	}

	public static IProject findProjectByContextName(String contextName) {
		IProject retval = null;

		if (!CoreUtil.isNullOrEmpty(contextName)) {
			for (IProject project : CoreUtil.getAllProjects()) {
				IVirtualComponent c = ComponentCore.createComponent(project, true);

				if (c != null) {
					Properties metaProperties = c.getMetaProperties();

					if (metaProperties != null) {
						String contextRoot = metaProperties.getProperty("context-root");

						if (contextName.equals(contextRoot)) {
							retval = project;

							break;
						}
					}
				}
			}
		}

		return retval;
	}

	public static Properties getAllCategories(IPath portalDir) {
		Properties retval = null;

		IPath implJarPath = portalDir.append("WEB-INF/lib/portal-impl.jar");

		File implJar = implJarPath.toFile();

		if (FileUtil.exists(implJar)) {
			try (JarFile jar = new JarFile(implJar)) {
				Properties categories = new Properties();
				Properties props = new Properties();

				try (InputStream input = jar.getInputStream(jar.getEntry("content/Language.properties"))) {
					props.load(input);

					Enumeration<?> names = props.propertyNames();

					while (names.hasMoreElements()) {
						Object element = names.nextElement();

						String name = element.toString();

						if (name.startsWith("category.")) {
							categories.put(name, props.getProperty(name));
						}
					}

					retval = categories;
				}
			}
			catch (IOException ioe) {
				LiferayServerCore.logError(ioe);
			}
		}

		return retval;
	}

	public static IPath getAppServerDir(org.eclipse.wst.common.project.facet.core.runtime.IRuntime serverRuntime) {
		ILiferayRuntime runtime = (ILiferayRuntime)getRuntimeAdapter(serverRuntime, ILiferayRuntime.class);

		if (runtime != null) {
			return runtime.getAppServerDir();
		}

		return null;
	}

	public static String getAppServerPropertyKey(String propertyAppServerDeployDir, ILiferayRuntime runtime) {
		String retval = null;

		try {
			Version version = Version.parseVersion(runtime.getPortalVersion());
			String type = runtime.getAppServerType();

			if ((CoreUtil.compareVersions(version, ILiferayConstants.V6130) >= 0) ||
				((CoreUtil.compareVersions(version, ILiferayConstants.V612) >= 0) &&
				 (CoreUtil.compareVersions(version, ILiferayConstants.V6110) < 0))) {

				retval = MessageFormat.format(propertyAppServerDeployDir, "." + type + ".");
			}
		}
		catch (Exception e) {
		}
		finally {
			if (retval == null) {
				retval = MessageFormat.format(propertyAppServerDeployDir, ".");
			}
		}

		return retval;
	}

	public static Set<IRuntime> getAvailableLiferayRuntimes() {
		Set<IRuntime> retval = new HashSet<>();

		IRuntime[] runtimes = ServerCore.getRuntimes();

		for (IRuntime rt : runtimes) {
			if (isLiferayRuntime(rt)) {
				retval.add(rt);
			}
		}

		return retval;
	}

	public static String getBundleFragmentHostNameFromBND(IProject project) {
		String retVal = null;

		IFile bndFile = project.getFile("bnd.bnd");

		if (FileUtil.exists(bndFile)) {
			IPath bndFileLocation = bndFile.getLocation();

			Properties prop = PropertiesUtil.loadProperties(bndFileLocation.toFile());

			String fragmentHost = prop.getProperty(Constants.FRAGMENT_HOST);

			if (fragmentHost != null) {
				String[] fragmentNames = fragmentHost.split(";");

				if (ListUtil.isNotEmpty(fragmentNames)) {
					retVal = fragmentNames[0];
				}
			}
		}

		return retVal;
	}

	public static Properties getEntryCategories(IPath portalDir, String portalVersion) {
		Properties categories = getAllCategories(portalDir);

		Properties retval = new Properties();

		String myKey = "category.my";

		String categoryMy = categories.getProperty(myKey);

		if ((portalVersion == null) ||
			(CoreUtil.compareVersions(Version.parseVersion(portalVersion), ILiferayConstants.V620) < 0)) {

			String portalKey = "category.portal";
			String serverKey = "category.server";
			String keyContent = "category.content";

			retval.put(myKey, categoryMy + " Account Section");
			retval.put(portalKey, categories.getProperty(portalKey) + " Section");
			retval.put(serverKey, categories.getProperty(serverKey) + " Section");
			retval.put(keyContent, categories.getProperty(keyContent) + " Section");
		}
		else {
			String keyUsers = "category.users";
			String keyApps = "category.apps";
			String keyConfig = "category.configuration";
			String keySites = "category.sites";
			String keySiteConfig = "category.site_administration.configuration";
			String keySiteContent = "category.site_administration.content";
			String keySitePages = "category.site_administration.pages";
			String keySiteUsers = "category.site_administration.users";

			retval.put(myKey, categoryMy + " Account Administration");
			retval.put(keyUsers, "Control Panel - " + categories.getProperty(keyUsers));
			retval.put(keyApps, "Control Panel - " + categories.getProperty(keyApps));
			retval.put(keyConfig, "Control Panel - " + categories.getProperty(keyConfig));
			retval.put(keySites, "Control Panel - " + categories.getProperty(keySites));
			retval.put(keySiteConfig, "Site Administration - " + categories.getProperty(keySiteConfig));
			retval.put(keySiteContent, "Site Administration - " + categories.getProperty(keySiteContent));
			retval.put(keySitePages, "Site Administration - " + categories.getProperty(keySitePages));
			retval.put(keySiteUsers, "Site Administration - " + categories.getProperty(keySiteUsers));
		}

		return retval;
	}

	public static IFacetedProject getFacetedProject(IProject project) {
		try {
			return ProjectFacetsManager.create(project);
		}
		catch (CoreException ce) {
			return null;
		}
	}

	public static org.eclipse.wst.common.project.facet.core.runtime.IRuntime getFacetRuntime(IRuntime runtime) {
		return RuntimeManager.getRuntime(runtime.getName());
	}

	public static String getFragemtHostName(File bundleFile) {
		String fragmentHostName = null;

		try (InputStream input = Files.newInputStream(bundleFile.toPath());
			JarInputStream jarStream = new JarInputStream(input)) {

			Manifest manifest = jarStream.getManifest();

			Attributes manifestAttrib = manifest.getMainAttributes();

			fragmentHostName = manifestAttrib.getValue("Fragment-Host");

			if (fragmentHostName != null) {
				String tfragmentHostName = fragmentHostName.trim();

				fragmentHostName = tfragmentHostName.substring(0, fragmentHostName.indexOf(";bundle-version"));
			}
		}
		catch (Exception e) {
		}

		return fragmentHostName;
	}

	public static IProjectFacet getLiferayFacet(IFacetedProject facetedProject) {
		for (IProjectFacetVersion projectFacet : facetedProject.getProjectFacets()) {
			if (isLiferayFacet(projectFacet.getProjectFacet())) {
				return projectFacet.getProjectFacet();
			}
		}

		return null;
	}

	public static ILiferayRuntime getLiferayRuntime(BridgedRuntime bridgedRuntime) {
		if (bridgedRuntime != null) {
			String id = bridgedRuntime.getProperty("id");

			if (id != null) {
				IRuntime runtime = ServerCore.findRuntime(id);

				if (isLiferayRuntime(runtime)) {
					return getLiferayRuntime(runtime);
				}
			}
		}

		return null;
	}

	public static ILiferayRuntime getLiferayRuntime(IProject project) throws CoreException {
		if (project == null) {
			return null;
		}

		IFacetedProject facetedProject = ProjectFacetsManager.create(project);

		if (facetedProject != null) {
			return (ILiferayRuntime)getRuntimeAdapter(facetedProject.getPrimaryRuntime(), ILiferayRuntime.class);
		}

		return null;
	}

	public static ILiferayRuntime getLiferayRuntime(IRuntime runtime) {
		if (runtime != null) {
			IRuntimeWorkingCopy runtimeWorkingCopy = runtime.createWorkingCopy();

			return (ILiferayRuntime)runtimeWorkingCopy.loadAdapter(ILiferayRuntime.class, null);
		}

		return null;
	}

	public static ILiferayRuntime getLiferayRuntime(IRuntime runtime, IProgressMonitor monitor) {
		return (ILiferayRuntime)runtime.loadAdapter(ILiferayRuntime.class, monitor);
	}

	public static ILiferayRuntime getLiferayRuntime(IServer server) {
		if (server != null) {
			return getLiferayRuntime(server.getRuntime());
		}

		return null;
	}

	public static ILiferayRuntime getLiferayRuntime(String name) {
		return getLiferayRuntime(getRuntime(name));
	}

	public static ILiferayServer getLiferayServer(IServer server, IProgressMonitor monitor) {
		ILiferayServer retval = null;

		if (server != null) {
			try {
				retval = (ILiferayServer)server.loadAdapter(ILiferayServer.class, monitor);
			}
			catch (Exception e) {
			}
		}

		return retval;
	}

	public static File[] getMarketplaceLpkgFiles(PortalBundle portalBundle) {
		IPath osgiBundlesDir = portalBundle.getOSGiBundlesDir();

		IPath marketplacePath = osgiBundlesDir.append("marketplace");

		File marketplace = marketplacePath.toFile();

		return marketplace.listFiles(
			new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.matches(".*\\.lpkg");
				}

			});
	}

	public static File getModuleFileFrom70Server(IRuntime runtime, String hostOsgiBundle, IPath temp) {
		File f = new File(temp.toFile(), hostOsgiBundle);

		if (FileUtil.exists(f)) {
			return f;
		}

		PortalBundle portalBundle = LiferayServerCore.newPortalBundle(runtime.getLocation());

		String[] dirs = {"core", "modules", "portal", "static"};

		for (String dir : dirs) {
			IPath osgiBundlesDir = portalBundle.getOSGiBundlesDir();

			IPath osgiBundleDir = osgiBundlesDir.append(dir);

			File portalModuleDir = osgiBundleDir.toFile();

			File moduleOsgiBundle = new File(portalModuleDir, hostOsgiBundle);

			if (FileUtil.exists(moduleOsgiBundle)) {
				return _copyModuleBundleJar(moduleOsgiBundle, temp);
			}

			int index = hostOsgiBundle.indexOf("-");

			if (index > 0) {
				String hostOsgiBundleWithoutVersion = hostOsgiBundle.substring(0, index) + ".jar";

				File moduleOsgiBundleWithoutVersion = new File(portalModuleDir, hostOsgiBundleWithoutVersion);

				if (FileUtil.exists(moduleOsgiBundleWithoutVersion)) {
					return _copyModuleBundleJar(moduleOsgiBundleWithoutVersion, temp);
				}
			}
		}

		File[] files = getMarketplaceLpkgFiles(portalBundle);

		InputStream in = null;

		try {
			boolean found = false;

			for (File file : files) {
				try (JarFile jar = new JarFile(file)) {
					Enumeration<JarEntry> enu = jar.entries();

					while (enu.hasMoreElements()) {
						JarEntry entry = enu.nextElement();

						String name = entry.getName();

						if (name.contains(hostOsgiBundle)) {
							in = jar.getInputStream(entry);
							found = true;

							FileUtil.writeFile(f, in);

							break;
						}
					}

					if (found) {
						break;
					}
				}
			}
		}
		catch (Exception e) {
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException ioe) {
				}
			}
		}

		return f;
	}

	public static List<String> getModuleFileListFrom70Server(IRuntime runtime) {
		List<String> bundles = new ArrayList<>();

		PortalBundle portalBundle = LiferayServerCore.newPortalBundle(runtime.getLocation());

		String[] dirs = {"core", "modules", "portal", "static"};

		if (portalBundle != null) {
			try {
				for (String dir : dirs) {
					IPath osgiBundlesDir = portalBundle.getOSGiBundlesDir();

					IPath osgiBundleDir = osgiBundlesDir.append(dir);

					File dirFile = osgiBundleDir.toFile();

					if (FileUtil.exists(dirFile)) {
						File[] files = dirFile.listFiles(
							new FilenameFilter() {

								@Override
								public boolean accept(File dir, String name) {
									return name.matches(".*\\.jar");
								}

							});

						if (ListUtil.isNotEmpty(files)) {
							for (File file : files) {
								bundles.add(file.getName());
							}
						}
					}
				}
			}
			catch (Exception e) {
				LiferayServerCore.logError("Could not determine possible files.", e);
			}

			File[] files = getMarketplaceLpkgFiles(portalBundle);

			for (File file : files) {
				try (JarFile jar = new JarFile(file)) {
					Enumeration<JarEntry> enu = jar.entries();

					while (enu.hasMoreElements()) {
						JarEntry entry = enu.nextElement();

						String name = entry.getName();

						if (name.endsWith(".jar")) {
							bundles.add(name);
						}
					}
				}
				catch (IOException ioe) {
				}
			}
		}

		return bundles;
	}

	public static PortalBundle getPortalBundle(IProject project) throws CoreException {
		IPath projectLocation = project.getLocation();

		SDK sdk = SDKUtil.getSDKFromProjectDir(projectLocation.toFile());

		if (sdk == null) {
			return null;
		}

		IStatus status = sdk.validate();

		if (!status.isOK()) {
			return null;
		}

		Map<String, Object> appServerProperties = sdk.getBuildProperties();

		String appServerType = (String)appServerProperties.get("app.server.type");

		PortalBundleFactory factory = LiferayServerCore.getPortalBundleFactories(appServerType);

		if (factory != null) {
			IPath path = factory.canCreateFromPath(appServerProperties);

			if (path != null) {
				return factory.create(path);
			}
		}

		return null;
	}

	public static IPath getPortalDir(IJavaProject project) {
		return getPortalDir(project.getProject());
	}

	public static IPath getPortalDir(IProject project) {
		IPath retval = null;

		ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

		if (liferayProject != null) {
			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			if (portal != null) {
				retval = portal.getAppServerPortalDir();
			}
		}

		return retval;
	}

	public static Properties getPortletCategories(IPath portalDir) {
		Properties props = getAllCategories(portalDir);
		Properties categories = new Properties();
		Enumeration<?> names = props.propertyNames();

		String[] controlPanelCategories = {
			"category.my", "category.users", "category.apps", "category.configuration", "category.sites",
			"category.site_administration.configuration", "category.site_administration.content",
			"category.site_administration.pages", "category.site_administration.users"
		};

		while (names.hasMoreElements()) {
			boolean controlPanelCategory = false;

			Object element = names.nextElement();

			String name = element.toString();

			for (String category : controlPanelCategories) {
				if (name.equals(category)) {
					controlPanelCategory = true;

					break;
				}
			}

			if (!controlPanelCategory) {
				categories.put(name, props.getProperty(name));
			}
		}

		return categories;
	}

	public static IRuntime getRuntime(IProject project) throws CoreException {
		IFacetedProject facetedProject = ProjectFacetsManager.create(project);

		return (IRuntime)getRuntimeAdapter(facetedProject.getPrimaryRuntime(), IRuntime.class);
	}

	public static IRuntime getRuntime(org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime) {
		return ServerCore.findRuntime(runtime.getProperty("id"));
	}

	public static IRuntime getRuntime(String runtimeName) {
		IRuntime retval = null;

		if (!CoreUtil.isNullOrEmpty(runtimeName)) {
			IRuntime[] runtimes = ServerCore.getRuntimes();

			if (runtimes != null) {
				for (IRuntime runtime : runtimes) {
					if (runtimeName.equals(runtime.getName())) {
						retval = runtime;

						break;
					}
				}
			}
		}

		return retval;
	}

	public static IRuntimeWorkingCopy getRuntime(String runtimeTypeId, IPath location) {
		IRuntimeType runtimeType = ServerCore.findRuntimeType(runtimeTypeId);

		try {
			IRuntime runtime = runtimeType.createRuntime("runtime", null);

			IRuntimeWorkingCopy runtimeWC = runtime.createWorkingCopy();

			runtimeWC.setName("Runtime");
			runtimeWC.setLocation(location);

			return runtimeWC;
		}
		catch (CoreException ce) {
			ce.printStackTrace();
		}

		return null;
	}

	public static Object getRuntimeAdapter(
		org.eclipse.wst.common.project.facet.core.runtime.IRuntime facetRuntime, Class<?> adapterClass) {

		if (facetRuntime != null) {
			String runtimeId = facetRuntime.getProperty("id");

			for (IRuntime runtime : ServerCore.getRuntimes()) {
				String rId = runtime.getId();

				if (rId.equals(runtimeId)) {
					if (IRuntime.class.equals(adapterClass)) {
						return runtime;
					}

					IRuntimeWorkingCopy runtimeWC = null;

					if (!runtime.isWorkingCopy()) {
						runtimeWC = runtime.createWorkingCopy();
					}
					else {
						runtimeWC = (IRuntimeWorkingCopy)runtime;
					}

					return runtimeWC.loadAdapter(adapterClass, null);
				}
			}
		}

		return null;
	}

	public static Version getRuntimeVersion(IProject project) {
		Version retval = Version.emptyVersion;

		if (project != null) {
			ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

			if (liferayProject != null) {
				ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

				if (portal != null) {
					retval = Version.parseVersion(portal.getVersion());
				}
			}
		}

		return retval;
	}

	public static Map<String, String> getSDKRequiredProperties(ILiferayRuntime appServer) {
		Map<String, String> properties = new HashMap<>();

		String type = appServer.getAppServerType();

		IPath dir = appServer.getAppServerDir();

		IPath deployDir = appServer.getAppServerDeployDir();

		IPath libGlobalDir = appServer.getAppServerLibGlobalDir();

		String parentDir = new File(
			dir.toOSString()
		).getParent();

		IPath portalDir = appServer.getAppServerPortalDir();

		properties.put(ISDKConstants.PROPERTY_APP_SERVER_TYPE, type);

		String appServerDirKey = getAppServerPropertyKey(ISDKConstants.PROPERTY_APP_SERVER_DIR, appServer);
		String appServerDeployDirKey = getAppServerPropertyKey(ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, appServer);
		String appServerLibGlobalDirKey = getAppServerPropertyKey(
			ISDKConstants.PROPERTY_APP_SERVER_LIB_GLOBAL_DIR, appServer);
		String appServerPortalDirKey = getAppServerPropertyKey(ISDKConstants.PROPERTY_APP_SERVER_PORTAL_DIR, appServer);

		properties.put(appServerDeployDirKey, deployDir.toOSString());
		properties.put(appServerDirKey, dir.toOSString());
		properties.put(appServerLibGlobalDirKey, libGlobalDir.toOSString());

		/**
		 * IDE-1268 need to always specify app.server.parent.dir, even though it
		 * is only useful in 6.1.2/6.2.0 or greater
		 */
		properties.put(appServerPortalDirKey, portalDir.toOSString());
		properties.put(ISDKConstants.PROPERTY_APP_SERVER_PARENT_DIR, parentDir);

		return properties;
	}

	public static IServer getServer(String name) {
		IServer[] servers = ServerCore.getServers();

		for (IServer server : servers) {
			String serverName = server.getName();

			if (serverName.equals(name)) {
				return server;
			}
		}

		return null;
	}

	public static IServerManagerConnection getServerManagerConnection(IServer server, IProgressMonitor monitor) {
		return LiferayServerCore.getRemoteConnection((IRemoteServer)server.loadAdapter(IRemoteServer.class, monitor));
	}

	public static IServer[] getServersForRuntime(IRuntime runtime) {
		List<IServer> serverList = new ArrayList<>();

		if (runtime != null) {
			IServer[] servers = ServerCore.getServers();

			if (ListUtil.isNotEmpty(servers)) {
				for (IServer server : servers) {
					if (runtime.equals(server.getRuntime())) {
						serverList.add(server);
					}
				}
			}
		}

		return serverList.toArray(new IServer[0]);
	}

	public static String[] getServletFilterNames(IPath portalDir) throws Exception {
		List<String> retval = new ArrayList<>();

		IPath filtersWebXmlPath = portalDir.append("WEB-INF/liferay-web.xml");

		File filtersWebXmlFile = filtersWebXmlPath.toFile();

		if (FileUtil.notExists(filtersWebXmlFile)) {
			IPath webXmlPath = portalDir.append("WEB-INF/web.xml");

			filtersWebXmlFile = webXmlPath.toFile();
		}

		if (FileUtil.exists(filtersWebXmlFile)) {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			Document document = documentBuilder.parse(filtersWebXmlFile);

			NodeList filterNameElements = document.getElementsByTagName("filter-name");

			for (int i = 0; i < filterNameElements.getLength(); i++) {
				Node filterNameElement = filterNameElements.item(i);

				String content = filterNameElement.getTextContent();

				if (!CoreUtil.isNullOrEmpty(content)) {
					retval.add(content.trim());
				}
			}
		}

		return retval.toArray(new String[0]);
	}

	public static boolean hasFacet(IProject project, IProjectFacet checkProjectFacet) {
		boolean retval = false;

		if ((project == null) || (checkProjectFacet == null)) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);

			if ((facetedProject != null) && (checkProjectFacet != null)) {
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					if (checkProjectFacet.equals(facet.getProjectFacet())) {
						retval = true;

						break;
					}
				}
			}
		}
		catch (CoreException ce) {
		}

		return retval;
	}

	public static boolean isExistingVMName(String name) {
		IVMInstallType vmInstallType = JavaRuntime.getVMInstallType(StandardVMType.ID_STANDARD_VM_TYPE);

		for (IVMInstall vm : vmInstallType.getVMInstalls()) {
			String vmName = vm.getName();

			if (vmName.equals(name)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isExtProject(IProject project) {
		return hasFacet(project, ProjectFacetsManager.getProjectFacet("liferay.ext"));
	}

	public static boolean isLiferayFacet(IProjectFacet projectFacet) {
		if (projectFacet != null) {
			String projectId = projectFacet.getId();

			if (projectId.startsWith("liferay")) {
				return true;
			}
		}

		return false;
	}

	public static boolean isLiferayRuntime(BridgedRuntime bridgedRuntime) {
		if (bridgedRuntime != null) {
			String id = bridgedRuntime.getProperty("id");

			if (id != null) {
				IRuntime runtime = ServerCore.findRuntime(id);

				return isLiferayRuntime(runtime);
			}
		}

		return false;
	}

	public static boolean isLiferayRuntime(IRuntime runtime) {
		if (getLiferayRuntime(runtime) != null) {
			return true;
		}

		return false;
	}

	public static boolean isLiferayRuntime(IServer server) {
		if (getLiferayRuntime(server) != null) {
			return true;
		}

		return false;
	}

	public static boolean isValidPropertiesFile(File file) {
		if (FileUtil.notExists(file)) {
			return false;
		}

		try {
			new PropertiesConfiguration(file);
		}
		catch (Exception e) {
			return false;
		}

		return true;
	}

	public static void setupPortalDevelopModeConfiguration(PortalRuntime portalRuntime, PortalServer portalServer) {
		boolean customLaunchSettings = portalServer.getCustomLaunchSettings();

		IPath liferayHome = portalRuntime.getLiferayHome();

		IPath portalExtPath = liferayHome.append("portal-ext.properties");

		File portalext = portalExtPath.toFile();

		try {
			PortalPropertiesConfiguration config = new PortalPropertiesConfiguration();

			if (customLaunchSettings && FileUtil.notExists(portalext)) {
				portalext.createNewFile();
			}

			if (FileUtil.exists(portalext) && portalext.canRead()) {
				try (InputStream in = Files.newInputStream(portalext.toPath())) {
					config.load(in);
				}
			}
			else {
				LiferayServerCore.logInfo("Can not read portal-ext.properties file.");
			}

			String[] includeAndOverrideProperties = config.getStringArray("include-and-override");
			boolean needAdd = true;
			boolean needRemove = false;

			if (customLaunchSettings && portalServer.getDeveloperMode()) {
				for (String prop : includeAndOverrideProperties) {
					if (prop.equals("portal-developer.properties")) {
						needAdd = false;

						break;
					}
				}

				needRemove = false;

				if (needAdd) {
					config.addProperty("include-and-override", "portal-developer.properties");
				}
			}
			else if (FileUtil.exists(portalext)) {
				config.clearProperty("include-and-override");

				for (String prop : includeAndOverrideProperties) {
					if (!prop.equals("portal-developer.properties")) {
						config.addProperty("include-and-override", prop);
					}
					else {
						needRemove = true;
					}
				}

				needAdd = false;
			}

			if (needAdd || needRemove) {
				if (portalext.canWrite()) {
					config.save(portalext);
				}
				else {
					LiferayServerCore.logInfo("Can not save change to portal-ext.properties file.");
				}
			}
		}
		catch (Exception e) {
			LiferayServerCore.logError(e);
		}
	}

	public static void terminateLaunchesForConfig(ILaunchConfigurationWorkingCopy config) throws DebugException {
		DebugPlugin debugPlugin = DebugPlugin.getDefault();

		ILaunchManager launchManager = debugPlugin.getLaunchManager();

		ILaunch[] launches = launchManager.getLaunches();

		for (ILaunch launch : launches) {
			ILaunchConfiguration launchConfig = launch.getLaunchConfiguration();

			if (launchConfig.equals(config)) {
				launch.terminate();
			}
		}
	}

	public static boolean verifyPath(String verifyPath) {
		if (verifyPath == null) {
			return false;
		}

		IPath verifyLocation = new Path(verifyPath);

		File verifyFile = verifyLocation.toFile();

		if (FileUtil.exists(verifyFile) && verifyFile.isDirectory()) {
			return true;
		}

		return false;
	}

	private static File _copyModuleBundleJar(File moduleOsgiBundle, IPath temp) {
		String[] bsnAndVersion = FileUtil.readMainFestProsFromJar(
			moduleOsgiBundle, "Bundle-SymbolicName", "Bundle-Version");

		String bsnAndVersionName = bsnAndVersion[0] + "-" + bsnAndVersion[1] + ".jar";

		File tempJar = new File(temp.toFile(), bsnAndVersionName);

		if (FileUtil.notExists(tempJar)) {
			FileUtil.copyFileToDir(moduleOsgiBundle, bsnAndVersionName, temp.toFile());

			return tempJar;
		}

		return moduleOsgiBundle;
	}

	private static String _formateRuntimeName(String runtimeName, int suffix) {
		if (suffix != -1) {
			return MessageFormat.format("{0}({1})", runtimeName, String.valueOf(suffix));
		}

		return MessageFormat.format("{0}", runtimeName);
	}

	private static String _setRuntimeName(IRuntimeWorkingCopy runtime, String runtimeName, int suffix) {
		if (runtime == null) {
			return null;
		}

		if (suffix == -1) {
			runtimeName = MessageFormat.format("{0}", runtimeName);
		}
		else {
			runtimeName = MessageFormat.format("{0}({1})", runtimeName, String.valueOf(suffix));
		}

		runtimeName = _verifyRuntimeName(runtime, runtimeName, suffix);

		runtime.setName(runtimeName);

		return runtimeName;
	}

	private static String _verifyRuntimeName(IRuntimeWorkingCopy runtime, String runtimeName, int suffix) {
		String name = null;

		if (ServerPlugin.isNameInUse(runtime.getOriginal(), runtimeName)) {
			if (suffix == -1) {

				// If the no suffix name is in use, the next suffix to try is 2

				suffix = 2;
			}
			else {
				suffix++;
			}

			name = _formateRuntimeName(runtimeName, suffix);

			while (ServerPlugin.isNameInUse(runtime.getOriginal(), name)) {
				suffix++;

				name = _formateRuntimeName(runtimeName, suffix);
			}
		}
		else {
			name = runtimeName;
		}

		return name;
	}

}