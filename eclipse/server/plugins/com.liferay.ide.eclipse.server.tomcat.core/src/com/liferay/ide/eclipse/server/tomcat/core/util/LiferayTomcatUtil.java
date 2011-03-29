/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.tomcat.core.util;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.core.util.FileListing;
import com.liferay.ide.eclipse.core.util.FileUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.server.core.LiferayServerCorePlugin;
import com.liferay.ide.eclipse.server.tomcat.core.ILiferayTomcatRuntime;
import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatPlugin;
import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatRuntime;
import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatServerBehavior;
import com.liferay.ide.eclipse.server.util.PortalSupportHelper;
import com.liferay.ide.eclipse.ui.util.UIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerEvent;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class LiferayTomcatUtil {

	public static void displayToggleMessage(String msg, String key) {
		UIUtil.postInfoWithToggle(
			"Liferay Tomcat Server", msg, "Do not show this message again", false,
			LiferayTomcatPlugin.getPreferenceStore(), key);
	}

	public static IPath[] getAllUserClasspathLibraries(IPath runtimeLocation, IPath portalDir) {
		List<IPath> libs = new ArrayList<IPath>();
		IPath libFolder = runtimeLocation.append("lib");
		IPath webinfLibFolder = portalDir.append("WEB-INF/lib");

		try {
			List<File> libFiles = FileListing.getFileListing(new File(libFolder.toOSString()));

			for (File lib : libFiles) {
				if (lib.exists() && lib.getName().endsWith(".jar")) {
					libs.add(new Path(lib.getPath()));
				}
			}

			libFiles = FileListing.getFileListing(new File(webinfLibFolder.toOSString()));

			for (File lib : libFiles) {
				if (lib.exists() && lib.getName().endsWith(".jar")) {
					libs.add(new Path(lib.getPath()));
				}
			}
		}
		catch (FileNotFoundException e) {
			LiferayTomcatPlugin.logError(e);
		}

		return libs.toArray(new IPath[0]);
	}

	public static Properties getCategories(IPath runtimeLocation, IPath portalDir) {
		Properties retval = null;

		File implJar = portalDir.append("WEB-INF/lib/portal-impl.jar").toFile();

		if (implJar.exists()) {
			try {
				JarFile jar = new JarFile(implJar);
				Properties categories = new Properties();
				Properties props = new Properties();
				props.load(jar.getInputStream(jar.getEntry("content/Language.properties")));
				Enumeration<?> names = props.propertyNames();

				while (names.hasMoreElements()) {
					String name = names.nextElement().toString();
					if (name.startsWith("category.")) {
						categories.put(name, props.getProperty(name));
					}
				}
				retval = categories;

			}
			catch (IOException e) {
				LiferayTomcatPlugin.logError(e);
			}
		}

		return retval;
	}

	public static ILiferayTomcatRuntime getLiferayTomcatRuntime(IRuntime runtime) {
		if (runtime != null) {
			return (ILiferayTomcatRuntime) runtime.createWorkingCopy().loadAdapter(ILiferayTomcatRuntime.class, null);
		}

		return null;
	}

	public static IPath getPortalDir(IPath appServerDir) {
		return appServerDir.append("/webapps/ROOT");
	}

	public static String[] getSupportedHookProperties(IPath runtimeLocation, IPath portalDir)
		throws IOException {
		
		IPath hookPropertiesPath =
			LiferayTomcatPlugin.getDefault().getStateLocation().append("hook_properties").append(
				runtimeLocation.toPortableString().replaceAll("\\/", "_") + "_hook_properties.txt");

		IPath errorPath = LiferayTomcatPlugin.getDefault().getStateLocation().append("hookError.log");

		File hookPropertiesFile = hookPropertiesPath.toFile();

		File errorFile = errorPath.toFile();

		if (!hookPropertiesFile.exists()) {
			loadHookPropertiesFile(runtimeLocation, portalDir, hookPropertiesFile, errorFile);
		}

		String[] hookProperties = FileUtil.readLinesFromFile(hookPropertiesFile);

		if (hookProperties.length == 0) {
			loadHookPropertiesFile(runtimeLocation, portalDir, hookPropertiesFile, errorFile);

			hookProperties = FileUtil.readLinesFromFile(hookPropertiesFile);
		}

		return hookProperties;
	}

	public static String getVersion(IPath location, IPath portalDir)
		throws IOException {
		
		IPath versionsInfoPath = LiferayTomcatPlugin.getDefault().getStateLocation().append("version.properties");

		String locationKey = location.toPortableString().replaceAll("\\/", "_");

		File versionInfoFile = versionsInfoPath.toFile();

		Properties properties = new Properties();

		if (versionInfoFile.exists()) {
			try {
				properties.load(new FileInputStream(versionInfoFile));
				String version = (String) properties.get(locationKey);

				if (!CoreUtil.isNullOrEmpty(version)) {
					return version;
				}
			}
			catch (Exception e) {
			}
		}

		File versionFile = LiferayTomcatPlugin.getDefault().getStateLocation().append("version.txt").toFile();

		if (versionFile.exists()) {
			FileUtil.clearContents(versionFile);
		}

		IPath errorPath = LiferayTomcatPlugin.getDefault().getStateLocation().append("versionError.log");

		File errorFile = errorPath.toFile();

		loadVersionInfoFile(location, portalDir, versionFile, errorFile);

		Version version = CoreUtil.readVersionFile(versionFile);

		if (version.equals(Version.emptyVersion)) {
			loadVersionInfoFile(location, portalDir, versionInfoFile, errorFile);

			version = CoreUtil.readVersionFile(versionInfoFile);
		}

		if (!version.equals(Version.emptyVersion)) {
			properties.put(locationKey, version.toString());
			try {
				properties.store(new FileOutputStream(versionInfoFile), "");
			}
			catch (Exception e) {
			}
		}

		return version.toString();
	}

	public static boolean isExtProjectContext(Context context) {

		return false;
	}

	public static boolean isLiferayModule(IModule module) {
		boolean retval = false;

		if (module != null) {
			IProject project = module.getProject();

			retval = ProjectUtil.isLiferayProject(project);
		}

		return retval;
	}

	public static Context loadContextFile(File contextFile) {
		FileInputStream fis = null;
		Context context = null;
		if (contextFile != null && contextFile.exists()) {
			try {
				Factory factory = new Factory();
				factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
				fis = new FileInputStream(contextFile);
				context = (Context)factory.loadDocument(fis);
				if (context != null) {
					String path = context.getPath();
					// If path attribute is not set, derive from file name
					if (path == null) {
						String fileName = contextFile.getName();
						path = fileName.substring(0, fileName.length() - ".xml".length());
						if ("ROOT".equals(path))
							path = "";
						context.setPath("/" + path);
					}
				}
			} catch (Exception e) {
				// may be a spurious xml file in the host dir?

			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return context;
	}

	public static void loadHookPropertiesFile(
		IPath runtimeLocation, IPath portalDir, File hookPropertiesFile, File errorFile)
		throws IOException {
		
		String portalSupportClass = "com.liferay.ide.eclipse.server.core.support.GetSupportedHookProperties";

		IPath[] libRoots = new IPath[] { runtimeLocation.append("lib"), runtimeLocation.append("lib/ext") };

		URL[] urls = new URL[] {
				FileLocator.toFileURL(LiferayServerCorePlugin.getDefault().getBundle().getEntry(
					"portal-support/portal-support.jar"))
		};

		PortalSupportHelper helper =
			new PortalSupportHelper(
				libRoots, portalDir, portalSupportClass, hookPropertiesFile, errorFile, urls, new String[] {});

		try {
			helper.launch(null);
		}
		catch (CoreException e) {
			LiferayTomcatPlugin.logError(e);
		}
	}

	public static void loadVersionInfoFile(IPath runtimeLocation, IPath portalDir, File versionInfoFile, File errorFile)
		throws IOException {
		
		String portalSupportClass = "com.liferay.ide.eclipse.server.core.support.ReleaseInfoGetVersion";

		IPath[] libRoots = new IPath[] { runtimeLocation.append("lib"), runtimeLocation.append("lib/ext") };

		URL[] urls = new URL[] {
				FileLocator.toFileURL(LiferayServerCorePlugin.getDefault().getBundle().getEntry(
					"portal-support/portal-support.jar"))
		};

		PortalSupportHelper helper =
			new PortalSupportHelper(
				libRoots, portalDir, portalSupportClass, versionInfoFile, errorFile, urls, new String[] {});

		try {
			helper.launch(null);
		}
		catch (CoreException e) {
			LiferayTomcatPlugin.logError(e);
		}
	}

	public static IPath modifyLocationForBundle(IPath currentLocation) {
		IPath modifiedLocation = null;
	
		if (currentLocation == null || CoreUtil.isNullOrEmpty(currentLocation.toOSString())) {
			return null;
		}
	
		File location = currentLocation.toFile();
	
		if (location.exists() && location.isDirectory()) {
			// check to see if this location contains 3 dirs
			// data, deploy, and tomcat-*
			File[] files = location.listFiles();
	
			boolean[] matches = new boolean[3];
	
			String[] patterns = new String[] {
				"data", "deploy", "^tomcat-.*"
			};
	
			File tomcatDir = null;
	
			for (File file : files) {
				for (int i = 0; i < patterns.length; i++) {
					if (file.isDirectory() && file.getName().matches(patterns[i])) {
						matches[i] = true;
	
						if (i == 2) { // tomcat
							tomcatDir = file;
						}
	
						break;
					}
				}
			}
	
			if (matches[0] && matches[1] && matches[2] && tomcatDir != null) {
				modifiedLocation = new Path(tomcatDir.getPath());
			}
		}
	
		return modifiedLocation;
	}

	public static void syncStopServer(final IServer server) {
		if (server.getServerState() != IServer.STATE_STARTED) {
			return;
		}

		final LiferayTomcatServerBehavior serverBehavior =
			(LiferayTomcatServerBehavior) server.loadAdapter(LiferayTomcatServerBehavior.class, null);

		Thread shutdownThread = new Thread() {

			@Override
			public void run() {
				serverBehavior.stop(true);

				synchronized (server) {
					try {
						server.wait(5000);
					}
					catch (InterruptedException e) {
					}
				}
			}

		};

		IServerListener shutdownListener = new IServerListener() {

			public void serverChanged(ServerEvent event) {
				if (event.getState() == IServer.STATE_STOPPED) {
					synchronized (server) {
						server.notifyAll();
					}
				}
			}
		};

		server.addServerListener(shutdownListener);

		try {
			shutdownThread.start();
			shutdownThread.join();
		}
		catch (InterruptedException e) {
		}

		server.removeServerListener(shutdownListener);
	}

	public static IStatus validateRuntimeStubLocation(IPath runtimeStubLocation) {
		try {
			IRuntimeWorkingCopy runtimeStub =
				ServerCore.findRuntimeType(LiferayTomcatRuntime.RUNTIME_TYPE_ID).createRuntime(null, null);
			runtimeStub.setLocation(runtimeStubLocation);
			runtimeStub.setStub(true);
			return runtimeStub.validate(null);
		}
		catch (Exception e) {
			return LiferayTomcatPlugin.createErrorStatus(e);
		}

	}
}
