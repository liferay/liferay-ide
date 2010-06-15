/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/
package com.liferay.ide.eclipse.server.tomcat.core;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.core.util.FileListing;
import com.liferay.ide.eclipse.core.util.FileUtil;
import com.liferay.ide.eclipse.server.core.IPortalConstants;
import com.liferay.ide.eclipse.server.core.IPortalRuntime;
import com.liferay.ide.eclipse.server.util.JavaUtil;
import com.liferay.ide.eclipse.server.util.PortalSupportHelper;
import com.liferay.ide.eclipse.server.util.ReleaseHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.internal.launching.StandardVMType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatVersionHandler;
import org.eclipse.jst.server.tomcat.core.internal.TomcatRuntime;
import org.osgi.framework.Version;

@SuppressWarnings("restriction")
public class PortalTomcatRuntime extends TomcatRuntime implements IPortalRuntime {

	protected HashMap<IPath, ReleaseHelper> releaseHelpers;

	public PortalTomcatRuntime() {
		releaseHelpers = new HashMap<IPath, ReleaseHelper>();
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	public IVMInstall findPortalBundledJRE(boolean addVM) {
		IPath jrePath = findBundledJREPath(getRuntime().getLocation());
		if (jrePath == null) return null;
		
		//make sure we don't have an existing JRE that has the same path
		for (IVMInstallType vmInstallType : JavaRuntime.getVMInstallTypes()) {
			for (IVMInstall vmInstall : vmInstallType.getVMInstalls()) {
				if (vmInstall.getInstallLocation().equals(jrePath.toFile())) {
					return vmInstall;
				}
			}
		}
		
		if (addVM) {
			IVMInstallType installType = JavaRuntime.getVMInstallType(StandardVMType.ID_STANDARD_VM_TYPE);
			VMStandin newVM = new VMStandin(installType, JavaUtil.createUniqueId(installType));
			newVM.setInstallLocation(jrePath.toFile());
			if (getRuntime().getName()!= null && !getRuntime().getName().isEmpty()) {
				newVM.setName(getRuntime().getName() + " JRE");
			} else {
				newVM.setName("Liferay JRE");
			}
			
			//make sure the new VM name isn't the same as existing name 
			boolean existingVMWithSameName = existingVMName(newVM.getName());
			
			int num = 1;
			while (existingVMWithSameName) {
				newVM.setName(getRuntime().getName() + " JRE (" + (num++) + ")");
				existingVMWithSameName = existingVMName(newVM.getName());
			}
			
			return newVM.convertToRealVM();
		}
			
		return null;
	}
	
	public IPath[] getAllUserClasspathLibraries() {
		List<IPath> libs = new ArrayList<IPath>();
		IPath libFolder = getRuntime().getLocation().append("lib");
		IPath webinfLibFolder = getRoot().append("WEB-INF/lib");
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return libs.toArray(new IPath[0]);
	}

	public Properties getCategories() {
		Properties retval = null;
		File implJar = getRoot().append("WEB-INF/lib/portal-impl.jar").toFile();
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
			} catch (IOException e) {
				PortalTomcatPlugin.logError(e);
			}
		}
		return retval;
	}

	public IPath getRoot() {
		return getRuntime().getLocation().append("webapps/ROOT");
	}

	public String[] getSupportedHookProperties() {
		IPath location = getRuntime().getLocation();

		IPath hookPropertiesPath =
			PortalTomcatPlugin.getDefault().getStateLocation().append("hook_properties").append(
				location.toPortableString().replaceAll("\\/", "_") + "_hook_properties.txt");

		File hookPropertiesFile = hookPropertiesPath.toFile();

		if (!hookPropertiesFile.exists()) {
			loadHookPropertiesFile(location, hookPropertiesFile);
		}

		String[] hookProperties = FileUtil.readLinesFromFile(hookPropertiesFile);

		if (hookProperties.length == 0) {
			loadHookPropertiesFile(location, hookPropertiesFile);

			hookProperties = FileUtil.readLinesFromFile(hookPropertiesFile);
		}

		return hookProperties;
	}

	protected void loadHookPropertiesFile(IPath location, File hookPropertiesFile) {
		String portalSupportClass = "com.liferay.ide.eclipse.server.core.support.GetSupportedHookProperties";

		IPath libRoot = location.append("lib/ext");

		IPath portalRoot = getRoot();

		PortalSupportHelper helper =
			new PortalSupportHelper(libRoot, portalRoot, portalSupportClass, hookPropertiesFile, null);

		try {
			helper.launch(null);
		}
		catch (CoreException e) {
			PortalTomcatPlugin.logError(e);
		}
	}

	public String getVersion() {
		// check for existing release info
		IPath location = getRuntime().getLocation();

		IPath versionInfoPath =
			PortalTomcatPlugin.getDefault().getStateLocation().append("versions").append(
				location.toPortableString().replaceAll("\\/", "_") + "_version.txt");

		File versionInfoFile = versionInfoPath.toFile();

		if (!versionInfoFile.exists()) {
			loadVersionInfoFile(location, versionInfoFile);
		}

		Version version = CoreUtil.readVersionFile(versionInfoFile);

		if (version.equals(Version.emptyVersion)) {
			loadVersionInfoFile(location, versionInfoFile);

			version = CoreUtil.readVersionFile(versionInfoFile);
		}

		return version.toString();
	
		// IPath location = getRuntime().getLocation();
		//		
		// if (location != null) {
		// try {
		// ReleaseHelper helper =
		// getReleaseHelper(location.append("lib/ext").append("portal-service.jar"));
		//
		// retval = helper.getVersion();
		// }
		// catch (Exception e) {
		// PortalTomcatPlugin.logError(e);
		// }
		// }
	}

	protected void loadVersionInfoFile(IPath location, File versionInfoFile) {
		String portalSupportClass = "com.liferay.ide.eclipse.server.core.support.GetReleaseInfo";

		IPath libRoot = location.append("lib/ext");

		IPath portalRoot = getRoot();

		PortalSupportHelper helper =
			new PortalSupportHelper(libRoot, portalRoot, portalSupportClass, versionInfoFile, null);

		try {
			helper.launch(null);
		}
		catch (CoreException e) {
			PortalTomcatPlugin.logError(e);
		}
	}

	@Override
	public ITomcatVersionHandler getVersionHandler() {
		String id = getRuntime().getRuntimeType().getId();
		if (id.indexOf("runtime.60") > 0) {
			return new PortalTomcat60Handler();
		}

		return null;
	}

	public IVMInstall getVMInstall() {
		if (getVMInstallTypeId() == null) {
			IVMInstall vmInstall = findPortalBundledJRE(false);
			if (vmInstall != null) {
				setVMInstall(vmInstall);
				return vmInstall;
			} else {
				return JavaRuntime.getDefaultVMInstall();
			}
		}
		try {
			IVMInstallType vmInstallType = JavaRuntime.getVMInstallType(getVMInstallTypeId());
			IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();
			int size = vmInstalls.length;
			String id = getVMInstallId();
			for (int i = 0; i < size; i++) {
				if (id.equals(vmInstalls[i].getId()))
					return vmInstalls[i];
			}
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	@Override
	public IStatus validate() {
		IStatus status = super.validate();

		if (!status.isOK()) {
			return status;
		}

		String version = getVersion();

		Version portalVersion = Version.parseVersion(version);

		if (portalVersion != null && (portalVersion.compareTo(IPortalConstants.LEAST_SUPPORTED_VERSION) < 0)) {
			status =
				PortalTomcatPlugin.createErrorStatus("Portal version not supported.  Need at least " +
				IPortalConstants.LEAST_SUPPORTED_VERSION);
		}

		return status;
	}

	private IPath findBundledJREPath(IPath location) {
		if (Platform.getOS().equals(Platform.OS_WIN32) && location != null && location.toFile().exists()) {
			//look for jre dir
			File tomcat = location.toFile();
			String[] jre = tomcat.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.startsWith("jre");
				}
			});
			for (String dir : jre) {
				File javaw = new File(location.toFile(), dir + "/win/bin/javaw.exe");
				if (javaw.exists()) {
					return new Path(javaw.getPath()).removeLastSegments(2);
				}
			}
		}
		return null;
	}

	protected boolean existingVMName(String name) {
		for (IVMInstall vm : JavaRuntime.getVMInstallType(StandardVMType.ID_STANDARD_VM_TYPE).getVMInstalls()) {
			if (vm.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	protected ReleaseHelper getReleaseHelper(IPath serviceJar) {
		if (releaseHelpers == null) {
			releaseHelpers = new HashMap<IPath, ReleaseHelper>();
		}

		ReleaseHelper cachedHelper = releaseHelpers.get(serviceJar);

		if (cachedHelper != null) {
			return cachedHelper;
		}

		ReleaseHelper newHelper = new ReleaseHelper(serviceJar);

		releaseHelpers.put(serviceJar, newHelper);

		return newHelper;
	}

}
