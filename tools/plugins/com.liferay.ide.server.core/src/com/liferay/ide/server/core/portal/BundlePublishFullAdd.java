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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

import org.osgi.framework.dto.BundleDTO;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 */
public class BundlePublishFullAdd extends BundlePublishOperation {

	public BundlePublishFullAdd(IServer s, IModule[] modules) {
		super(s, modules);
	}

	@Override
	public void execute(IProgressMonitor monitor, IAdaptable info) throws CoreException {
		for (IModule module : modules) {
			IStatus retval = Status.OK_STATUS;

			IProject project = module.getProject();

			if (project == null) {
				continue;
			}

			IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

			if (bundleProject != null) {

				// TODO catch error in getOutputJar and show a popup notification instead

				monitor.subTask("Building " + module.getName() + " output bundle...");

				try {
					IPath outputJar = bundleProject.getOutputBundle(cleanBuildNeeded(), monitor);

					if (FileUtil.exists(outputJar)) {
						if (server.getServerState() == IServer.STATE_STARTED) {
							monitor.subTask(
								"Remotely deploying " + module.getName() + " to Liferay module framework...");

							retval = _remoteDeploy(bundleProject.getSymbolicName(), outputJar);
						}
						else {
							retval = _autoDeploy(outputJar);
						}

						portalServerBehavior.setModuleState2(new IModule[] {module}, IServer.STATE_STARTED);
					}
					else {
						retval = LiferayServerCore.error("Could not create output jar");
					}
				}
				catch (Exception e) {
					retval = LiferayServerCore.error("Deploy module project error", e);
				}
			}
			else {
				retval = LiferayServerCore.error("Unable to get bundle project for " + project.getName());
			}

			project.deleteMarkers(LiferayServerCore.BUNDLE_OUTPUT_ERROR_MARKER_TYPE, false, 0);
			project.deleteMarkers(LiferayServerCore.BUNDLE_OUTPUT_WARNING_MARKER_TYPE, false, 0);

			if (retval.isOK()) {
				portalServerBehavior.setModulePublishState2(new IModule[] {module}, IServer.PUBLISH_STATE_NONE);
			}
			else if (retval.getSeverity() == IStatus.WARNING) {
				portalServerBehavior.setModulePublishState2(new IModule[] {module}, IServer.PUBLISH_STATE_NONE);
				project.createMarker(LiferayServerCore.BUNDLE_OUTPUT_WARNING_MARKER_TYPE);
				LiferayServerCore.logError(retval);
			}
			else {
				portalServerBehavior.setModulePublishState2(new IModule[] {module}, IServer.PUBLISH_STATE_NONE);
				project.createMarker(LiferayServerCore.BUNDLE_OUTPUT_ERROR_MARKER_TYPE);
				LiferayServerCore.logError(retval);
			}
		}
	}

	protected boolean cleanBuildNeeded() {
		return false;
	}

	private IStatus _autoDeploy(IPath output) throws CoreException {
		IStatus retval = null;
		PortalBundle portalBundle = portalRuntime.getPortalBundle();

		IPath autoDeployPath = portalBundle.getAutoDeployPath();
		IPath modulesPath = portalBundle.getModulesPath();

		IPath statePath = modulesPath.append("state");

		if (FileUtil.exists(autoDeployPath)) {
			try (InputStream inputStream = Files.newInputStream(output.toFile().toPath())) {
				FileUtil.writeFileFromStream(autoDeployPath.append(output.lastSegment()).toFile(), inputStream);

				retval = Status.OK_STATUS;
			}
			catch (IOException ioe) {
				retval = LiferayServerCore.error("Unable to copy file to auto deploy folder", ioe);
			}
		}

		if (FileUtil.exists(statePath)) {
			FileUtil.deleteDir(statePath.toFile(), true);
		}

		return retval;
	}

	private String _getBundleUrl(File bundleFile, String bsn) throws MalformedURLException {
		String bundleUrl = null;
		Path bundlePath = bundleFile.toPath();

		String bp = bundlePath.toString();

		String bundlePathLowerCase = bp.toLowerCase();

		URI uri = bundleFile.toURI();

		URL url = uri.toURL();

		if (bundlePathLowerCase.endsWith(".war")) {
			bundleUrl = "webbundle:" + url.toExternalForm() + "?Web-ContextPath=/" + bsn;
		}
		else {
			bundleUrl = url.toExternalForm();
		}

		return bundleUrl;
	}

	private IStatus _remoteDeploy(String bsn, IPath output) {
		IStatus retval = null;

		if (FileUtil.exists(output)) {
			GogoBundleDeployer bundleDeployer = null;

			try {
				bundleDeployer = createBundleDeployer();

				BundleDTO deployed = bundleDeployer.deploy(bsn, output.toFile(), _getBundleUrl(output.toFile(), bsn));

				if (deployed instanceof BundleDTOWithStatus) {
					BundleDTOWithStatus withStatus = (BundleDTOWithStatus)deployed;

					retval = withStatus.status;
				}
				else {
					retval = new Status(IStatus.OK, LiferayServerCore.PLUGIN_ID, (int)deployed.id, null, null);
				}
			}
			catch (Exception e) {
				retval = LiferayServerCore.error(
					"Unable to deploy bundle remotely " +
						output.toPortableString(),
					e);
			}
		}
		else {
			retval = LiferayServerCore.error("Unable to deploy bundle remotely " + output.toPortableString());
		}

		return retval;
	}

}