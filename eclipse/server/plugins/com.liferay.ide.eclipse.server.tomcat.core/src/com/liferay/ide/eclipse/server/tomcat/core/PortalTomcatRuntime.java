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
import com.liferay.ide.eclipse.core.util.FileUtil;
import com.liferay.ide.eclipse.server.core.IAppServer;
import com.liferay.ide.eclipse.server.core.IPortalConstants;
import com.liferay.ide.eclipse.server.core.PortalServerCorePlugin;
import com.liferay.ide.eclipse.server.tomcat.core.util.PortalTomcatUtil;
import com.liferay.ide.eclipse.server.util.JavaUtil;
import com.liferay.ide.eclipse.server.util.PortalSupportHelper;
import com.liferay.ide.eclipse.server.util.ReleaseHelper;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.FileLocator;
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
public class PortalTomcatRuntime extends TomcatRuntime implements IPortalTomcatRuntime, IAppServer {

	public static final String PROP_BUNDLE_SOURCE_LOCATION = "bundle-source-location";

	public static final String PROP_BUNDLE_ZIP_LOCATION = "bundle-zip-location";

	public static final String RUNTIME_TYPE_ID = "com.liferay.ide.eclipse.server.tomcat.runtime.60";

	private IStatus runtimeDelegateStatus;

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
		if (jrePath == null)
			return null;

		// make sure we don't have an existing JRE that has the same path
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
			if (!CoreUtil.isNullOrEmpty(getRuntime().getName())) {
				newVM.setName(getRuntime().getName() + " JRE");
			}
			else {
				newVM.setName("Liferay JRE");
			}

			// make sure the new VM name isn't the same as existing name
			boolean existingVMWithSameName = ServerUtil.isExistingVMName(newVM.getName());

			int num = 1;
			while (existingVMWithSameName) {
				newVM.setName(getRuntime().getName() + " JRE (" + (num++) + ")");
				existingVMWithSameName = ServerUtil.isExistingVMName(newVM.getName());
			}

			return newVM.convertToRealVM();
		}

		return null;
	}

	public IPath[] getAllUserClasspathLibraries() {
		IPath runtimeLocation = getRuntime().getLocation();

		return PortalTomcatUtil.getAllUserClasspathLibraries(runtimeLocation);

	}

	public IPath getAppServerDir() {
		return getRuntime().getLocation();
	}

	public String getAppServerType() {
		return "tomcat";
	}

	public IPath getBundleZipLocation() {
		String zipLocation = getAttribute(PROP_BUNDLE_ZIP_LOCATION, (String) null);

		return zipLocation != null ? new Path(zipLocation) : null;
	}

	public Properties getCategories() {
		IPath runtimeLocation = getRuntime().getLocation();

		return PortalTomcatUtil.getCategories(runtimeLocation);
	}

	public IPath getDeployDir() {
		return getAppServerDir().append("/webapps");
	}

	public IPath getLibGlobalDir() {
		return getAppServerDir().append("/lib/ext");
	}

	public IPath getPortalDir() {
		return getAppServerDir().append("/webapps/ROOT");
	}

	public IPath getPortalSourceLocation() {
		String sourceLocation = getAttribute(PROP_BUNDLE_SOURCE_LOCATION, (String) null);

		return sourceLocation != null ? new Path(sourceLocation) : null;
	}

	public String getPortalVersion() {
		// check for existing release info
		IPath location = getRuntime().getLocation();

		try {
			return PortalTomcatUtil.getVersion(location);
		}
		catch (IOException e) {
			return "";
		}
	}

	public IPath getRoot() {
		return PortalTomcatUtil.getPortalRoot(getRuntime().getLocation());
	}

	public IPath getRuntimeLocation() {
		return getRuntime().getLocation();
	}

	public String getServerInfo() {
		// check for existing server info
		IPath location = getRuntime().getLocation();

		IPath serverInfosPath = PortalTomcatPlugin.getDefault().getStateLocation().append("serverInfos.properties");

		String locationKey = location.toPortableString().replaceAll("\\/", "_");

		File serverInfosFile = serverInfosPath.toFile();

		Properties properties = new Properties();

		if (serverInfosFile.exists()) {
			try {
				properties.load(new FileInputStream(serverInfosFile));
				String serverInfo = (String) properties.get(locationKey);

				if (!CoreUtil.isNullOrEmpty(serverInfo)) {
					return serverInfo;
				}
			}
			catch (Exception e) {
			}
		}

		File serverInfoFile = PortalTomcatPlugin.getDefault().getStateLocation().append("serverInfo.txt").toFile();

		if (serverInfoFile.exists()) {
			FileUtil.clearContents(serverInfoFile);
		}

		IPath errorPath = PortalTomcatPlugin.getDefault().getStateLocation().append("serverInfoError.log");

		File errorFile = errorPath.toFile();

		loadServerInfoFile(location, serverInfoFile, errorFile);

		String serverInfoString = FileUtil.readContents(serverInfoFile);

		if (!CoreUtil.isNullOrEmpty(serverInfoString)) {
			properties.put(locationKey, serverInfoString);
			try {
				properties.store(new FileOutputStream(serverInfosFile), "");
			}
			catch (Exception e) {
			}
		}

		return serverInfoString;
	}

	public String[] getSupportedHookProperties() {
		IPath location = getRuntime().getLocation();

		try {
			return PortalTomcatUtil.getSupportedHookProperties(location);
		}
		catch (IOException e) {
			return new String[0];
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
			}
			else {
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
		}
		catch (Exception e) {
			// ignore
		}
		return null;
	}

	public void setBundleZipLocation(IPath path) {
		if (path != null) {
			setAttribute(PROP_BUNDLE_ZIP_LOCATION, path.toPortableString());
		}
	}

	public void setPortalSourceLocation(IPath path) {
		if (path != null) {
			setAttribute(PROP_BUNDLE_SOURCE_LOCATION, path.toPortableString());
		}
	}

	@Override
	public IStatus validate() {
		// first validate that this runtime is
		if (runtimeDelegateStatus == null) {
			runtimeDelegateStatus = PortalServerCorePlugin.validateRuntimeDelegate(this);
		}

		if (!runtimeDelegateStatus.isOK()) {
			return runtimeDelegateStatus;
		}

		IStatus status = super.validate();

		if (!status.isOK()) {
			return status;
		}

		String version = getPortalVersion();

		Version portalVersion = Version.parseVersion(version);

		if (portalVersion != null && (portalVersion.compareTo(getLeastSupportedVersion()) < 0)) {
			status =
				PortalTomcatPlugin.createErrorStatus("Portal version not supported.  Need at least " +
					getLeastSupportedVersion());
		}

		if (!getRuntime().isStub()) {
			String serverInfo = getServerInfo();

			if (CoreUtil.isNullOrEmpty(serverInfo) || serverInfo.indexOf(getExpectedServerInfo()) < 0) {
				status =
					PortalTomcatPlugin.createErrorStatus("Portal server not supported.  Expecting " +
						getExpectedServerInfo());
			}
		}

		// need to check if this runtime is specifying a zip file for a bundle package, if so validate it
		IPath bundleZip = getBundleZipLocation();

		if (bundleZip != null && (!CoreUtil.isNullOrEmpty(bundleZip.toString()))) {
			String rootEntryName = null;

			try {
				ZipInputStream zis = new ZipInputStream(new FileInputStream(bundleZip.toFile()));

				ZipEntry rootEntry = zis.getNextEntry();
				rootEntryName = new Path(rootEntry.getName()).segment(0);

				if (rootEntryName.endsWith("/")) {
					rootEntryName = rootEntryName.substring(0, rootEntryName.length() - 1);
				}

				boolean foundTomcat = false;

				ZipEntry entry = zis.getNextEntry();

				while (entry != null && !foundTomcat) {
					String entryName = entry.getName();

					if (entryName.startsWith(rootEntryName + "/tomcat-")) {
						foundTomcat = true;
					}

					entry = zis.getNextEntry();
				};
			}
			catch (Exception e) {
				return PortalTomcatPlugin.createWarningStatus("Bundle zip location does not specify a valid Liferay Tomcat bundle.");
			}

			// if we get here then the user has specified a good zip installation so now we need to see if the
			// installation of the runtime will work for EXT plugins.
			IPath location = getRuntime().getLocation();
			String bundleDir = location.removeLastSegments(1).lastSegment();

			if (!bundleDir.equals(rootEntryName)) {
				return PortalTomcatPlugin.createWarningStatus("Runtime location directory layout does not match zip file.");
			}
		}

		return status;
	}

	private IPath findBundledJREPath(IPath location) {
		if (Platform.getOS().equals(Platform.OS_WIN32) && location != null && location.toFile().exists()) {
			// look for jre dir
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

	protected String getExpectedServerInfo() {
		return "Liferay Portal";
	}

	protected Version getLeastSupportedVersion() {
		return IPortalConstants.LEAST_SUPPORTED_VERSION;
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

	protected void loadServerInfoFile(IPath location, File versionInfoFile, File errorFile) {
		String portalSupportClass = "com.liferay.ide.eclipse.server.core.support.ReleaseInfoGetServerInfo";

		IPath[] libRoots = new IPath[] { location.append("lib"), location.append("lib/ext") };

		IPath portalRoot = getRoot();

		try {
			URL[] supportUrls =
				new URL[] { FileLocator.toFileURL(PortalServerCorePlugin.getDefault().getBundle().getEntry(
					"portal-support/portal-support.jar")) };

			PortalSupportHelper helper =
				new PortalSupportHelper(
					libRoots, portalRoot, portalSupportClass, versionInfoFile, errorFile, supportUrls, new String[] {});

			helper.launch(null);
		}
		catch (Exception e) {
			PortalTomcatPlugin.logError(e);
		}
	}

}
