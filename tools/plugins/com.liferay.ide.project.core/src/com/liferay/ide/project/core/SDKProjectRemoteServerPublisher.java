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

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.remote.AbstractRemoteServerPublisher;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 */
public class SDKProjectRemoteServerPublisher extends AbstractRemoteServerPublisher {

	public SDKProjectRemoteServerPublisher(IProject project, SDK sdk) {
		super(project);

		_sdk = sdk;
	}

	@Override
	public IPath publishModuleFull(IProgressMonitor monitor) throws CoreException {
		IPath deployPath = LiferayServerCore.getTempLocation("direct-deploy", StringPool.EMPTY);

		File warFile = FileUtil.getFile(deployPath.append(getProject().getName() + ".war"));

		File parent = warFile.getParentFile();

		parent.mkdirs();

		Map<String, String> properties = new HashMap<>();

		properties.put(ISDKConstants.PROPERTY_AUTO_DEPLOY_UNPACK_WAR, "false");

		ILiferayRuntime runtime = ServerUtil.getLiferayRuntime(getProject());

		String appServerDeployDirProp = ServerUtil.getAppServerPropertyKey(
			ISDKConstants.PROPERTY_APP_SERVER_DEPLOY_DIR, runtime);

		properties.put(appServerDeployDirProp, deployPath.toOSString());

		// IDE-1073 LPS-37923

		properties.put(ISDKConstants.PROPERTY_PLUGIN_FILE_DEFAULT, warFile.getAbsolutePath());

		properties.put(ISDKConstants.PROPERTY_PLUGIN_FILE, warFile.getAbsolutePath());

		String fileTimeStamp = System.currentTimeMillis() + "";

		// IDE-1491

		properties.put(ISDKConstants.PROPERTY_LP_VERSION, fileTimeStamp);

		properties.put(ISDKConstants.PROPERTY_LP_VERSION_SUFFIX, ".0");

		IStatus status = _sdk.validate();

		if (!status.isOK()) {
			throw new CoreException(status);
		}

		IStatus directDeployStatus = _sdk.war(
			getProject(), properties, true, new String[] {"-Duser.timezone=GMT"}, monitor);

		if (!directDeployStatus.isOK() || !warFile.exists()) {
			String pluginVersion = "1";

			IPath pluginPropertiesPath = new Path("WEB-INF/liferay-plugin-package.properties");

			IWebProject webproject = LiferayCore.create(IWebProject.class, getProject());

			if (webproject != null) {
				IResource propsRes = webproject.findDocrootResource(pluginPropertiesPath);

				if (propsRes instanceof IFile && propsRes.exists()) {
					try (InputStream is = ((IFile)propsRes).getContents()) {
						PropertiesConfiguration pluginPackageProperties = new PropertiesConfiguration();

						pluginPackageProperties.load(is);

						pluginVersion = pluginPackageProperties.getString("module-incremental-version");
					}
					catch (Exception e) {
						LiferayCore.logError("error reading module-incremtnal-version. ", e);
					}
				}

				IPath sdkLocation = _sdk.getLocation();

				IPath distPath = sdkLocation.append("dist");

				String fullName = getProject().getName() + "-" + fileTimeStamp + "." + pluginVersion + ".0.war";

				warFile = FileUtil.getFile(distPath.append(fullName));

				if (!warFile.exists()) {
					throw new CoreException(directDeployStatus);
				}
			}
		}

		return new Path(warFile.getAbsolutePath());
	}

	private SDK _sdk;

}