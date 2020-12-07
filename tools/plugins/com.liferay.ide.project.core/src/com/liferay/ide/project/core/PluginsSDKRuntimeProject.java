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

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.remote.IRemoteServerPublisher;

import java.io.IOException;
import java.io.InputStream;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PluginsSDKRuntimeProject extends FlexibleProject implements IWebProject {

	public PluginsSDKRuntimeProject(IProject project, ILiferayRuntime liferayRuntime) {
		super(project);

		_liferayRuntime = liferayRuntime;
	}

	@Override
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
			ILiferayPortal portal = new PluginsSDKPortal(_liferayRuntime);

			return adapterType.cast(portal);
		}

		return null;
	}

	@Override
	public IPath getLibraryPath(String fileName) {
		IPath[] libs = getUserLibs();

		if (ListUtil.isEmpty(libs)) {
			return null;
		}

		for (IPath lib : libs) {
			if (StringUtil.startsWith(lib.lastSegment(), fileName)) {
				return lib;
			}
		}

		return null;
	}

	public Collection<IFile> getOutputs(boolean build, IProgressMonitor monitor) throws CoreException {
		Collection<IFile> outputs = new HashSet<>();

		if (build) {
			getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);

			SDK sdk = SDKUtil.getSDK(getProject());

			IStatus warStatus = sdk.war(getProject(), null, true, monitor);

			if (warStatus.isOK()) {
			}
		}

		return outputs;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		if (!ProjectUtil.isThemeProject(getProject())) {
			return defaultValue;
		}

		if (Objects.equals("theme.type", key) || Objects.equals("theme.parent", key)) {
			IFile buildXmlFile = getProject().getFile("build.xml");

			try (InputStream inputStream = buildXmlFile.getContents()) {
				Document buildXmlDoc = FileUtil.readXML(inputStream, null, null);

				NodeList properties = buildXmlDoc.getElementsByTagName("property");

				for (int i = 0; i < properties.getLength(); i++) {
					Node item = properties.item(i);

					NamedNodeMap namedNodeMap = item.getAttributes();

					Node name = namedNodeMap.getNamedItem("name");

					if ((name != null) && key.equals(name.getNodeValue())) {
						Node value = namedNodeMap.getNamedItem("value");

						return value.getNodeValue();
					}
				}
			}
			catch (CoreException | IOException e) {
				ProjectCore.logError("Unable to get property " + key, e);
			}
		}

		return defaultValue;
	}

	public IPath[] getUserLibs() {
		return _liferayRuntime.getUserLibs();
	}

	protected SDK getSDK() {

		// try to determine SDK based on project location

		IPath path = getProject().getRawLocation();

		IPath sdkLocation = path.removeLastSegments(2);

		SDKManager sdkManager = SDKManager.getInstance();

		SDK retval = sdkManager.getSDK(sdkLocation);

		if (retval == null) {
			retval = SDKUtil.createSDKFromLocation(sdkLocation);

			sdkManager.addSDK(retval);
		}

		return retval;
	}

	private ILiferayRuntime _liferayRuntime;

}