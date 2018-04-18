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

package com.liferay.ide.project.core;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.remote.IRemoteServerPublisher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PluginsSDKBundleProject extends FlexibleProject implements IWebProject, IBundleProject {

	public PluginsSDKBundleProject(IProject project, PortalBundle portalBundle) {
		super(project);

		_portalBundle = portalBundle;
	}

	public <T> T adapt(Class<T> adapterType) {
		T adapter = super.adapt(adapterType);

		if (adapter != null) {
			return adapter;
		}

		if (IProjectBuilder.class.equals(adapterType)) {
			SDK sdk = getSDK();

			if (sdk != null) {
				IProjectBuilder projectBuilder = new SDKProjectBuilder(getProject(), sdk);

				return adapterType.cast(projectBuilder);
			}
		}
		else if (IRemoteServerPublisher.class.equals(adapterType)) {
			SDK sdk = getSDK();

			if (sdk != null) {
				IRemoteServerPublisher remotePublisher = new SDKProjectRemoteServerPublisher(getProject(), sdk);

				return adapterType.cast(remotePublisher);
			}
		}
		else if (ILiferayPortal.class.equals(adapterType)) {
			return adapterType.cast(_portalBundle);
		}

		return null;
	}

	@Override
	public boolean filterResource(IPath resourcePath) {
		if (filterResource(resourcePath, _IGNORE_PATHS)) {
			return true;
		}

		return false;
	}

	@Override
	public String getBundleShape() {
		return "war";
	}

	@Override
	public IPath getLibraryPath(String filename) {
		IPath[] libs = getUserLibs();

		if (ListUtil.isNotEmpty(libs)) {
			for (IPath lib : libs) {
				if (lib.lastSegment().startsWith(filename)) {
					return lib;
				}
			}
		}

		return null;
	}

	@Override
	public IPath getOutputBundle(boolean cleanBuild, IProgressMonitor monitor) throws CoreException {
		IPath retval = null;

		SDK sdk = getSDK();

		IStatus status = sdk.validate();

		if (!status.isOK()) {
			throw new CoreException(status);
		}

		IStatus warStatus = sdk.war(getProject(), null, true, new String[] {"-Duser.timezone=GMT"}, monitor);

		IPath distPath = sdk.getLocation().append("dist");

		File[] distFiles = _getSDKOutputFiles(distPath);

		if (warStatus.isOK() && ListUtil.isNotEmpty(distFiles)) {
			try {
				retval = new Path(distFiles[0].getCanonicalPath());
			}
			catch (IOException ioe) {
				throw new CoreException(ProjectCore.createErrorStatus(ioe));
			}
		}

		return retval;
	}

	@Override
	public IPath getOutputBundlePath() {
		IPath retval = null;

		SDK sdk = getSDK();

		IStatus status = sdk.validate();

		if (!status.isOK()) {
			return retval;
		}

		IPath distPath = sdk.getLocation().append("dist");

		File[] distFiles = _getSDKOutputFiles(distPath);

		if (ListUtil.isNotEmpty(distFiles)) {
			try {
				retval = new Path(distFiles[0].getCanonicalPath());
			}
			catch (IOException ioe) {
				ProjectCore.createErrorStatus(ioe);
			}
		}

		return retval;
	}

	public String getProperty(String key, String defaultValue) {
		String retval = defaultValue;

		if (("theme.type".equals(key) || "theme.parent".equals(key)) && ProjectUtil.isThemeProject(getProject())) {
			try {
				IFile buildXml = getProject().getFile("build.xml");

				Document buildXmlDoc = FileUtil.readXML(buildXml.getContents(), null, null);

				NodeList properties = buildXmlDoc.getElementsByTagName("property");

				for (int i = 0; i < properties.getLength(); i++) {
					Node item = properties.item(i);

					Node name = item.getAttributes().getNamedItem("name");

					if ((name != null) && key.equals(name.getNodeValue())) {
						Node value = item.getAttributes().getNamedItem("value");

						retval = value.getNodeValue();

						break;
					}
				}
			}
			catch (CoreException ce) {
				ProjectCore.logError("Unable to get property " + key, ce);
			}
		}

		return retval;
	}

	public SDK getSDK() {
		SDK retval = null;

		// try to determine SDK based on project location

		IPath rawLocation = getProject().getRawLocation();

		IPath sdkLocation = rawLocation.removeLastSegments(2);

		retval = SDKManager.getInstance().getSDK(sdkLocation);

		if (retval == null) {
			retval = SDKUtil.createSDKFromLocation(sdkLocation);

			SDKManager.getInstance().addSDK(retval);
		}

		return retval;
	}

	@Override
	public String getSymbolicName() throws CoreException {
		IPath path = getProject().getLocation();

		return path.lastSegment();
	}

	public IPath[] getUserLibs() {
		return _portalBundle.getUserLibs();
	}

	@Override
	public boolean isFragmentBundle() {
		return false;
	}

	private File[] _getSDKOutputFiles(IPath distPath) {

		File[] distFiles = null;

		if ( FileUtil.notExists(distPath)) {
			return null;
		}

		try {
			distFiles = distPath.toFile().listFiles(
				new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {

						if ( FileUtil.isNotDir(dir)) {
							return false;
						}

						IPath location = getProject().getLocation();

						return name.contains(location.lastSegment());
					}

				}
			);
		}
		catch(Exception e) {
		}

		return distFiles;
	}

	private static final String[] _IGNORE_PATHS = {"docroot/WEB-INF/classes"};

	private PortalBundle _portalBundle;

}