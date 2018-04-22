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

package com.liferay.ide.layouttpl.core.facet;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.project.core.PluginsSDKBundleProject;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.PluginFacetInstall;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Gregory Amerson
 * @author Kamesh Sampath
 * @author Simon Jiang
 */
public class LayoutTplPluginFacetInstall extends PluginFacetInstall {

	@Override
	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
		throws CoreException {

		super.execute(project, fv, config, monitor);

		IDataModel model = (IDataModel)config;

		IDataModel masterModel = (IDataModel)model.getProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM);

		if ((masterModel != null) && masterModel.getBooleanProperty(CREATE_PROJECT_OPERATION)) {
			String layoutTplName = this.masterModel.getStringProperty(LAYOUTTPL_NAME);

			IPath projectTempPath = (IPath)masterModel.getProperty(PROJECT_TEMP_PATH);

			processNewFiles(projectTempPath.append(layoutTplName + ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX));

			FileUtil.deleteDir(projectTempPath.toFile(), true);
		}
		else if (shouldSetupDefaultOutputLocation()) {
			setupDefaultOutputLocation();
		}

		removeUnneededClasspathEntries();

		IFolder folder = new PluginsSDKBundleProject(project, null).getDefaultDocrootFolder();

		if (FileUtil.exists(folder)) {
			IResource libRes = folder.findMember("WEB-INF/lib");

			if (FileUtil.exists(libRes)) {
				IFolder libFolder = (IFolder)libRes;

				IResource[] libFiles = libFolder.members(true);

				if (ListUtil.isEmpty(libFiles)) {
					libRes.delete(true, monitor);
				}
			}
		}

		try {
			this.project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch (Exception e) {
			LayoutTplCore.logError(e);
		}
	}

	@Override
	protected String getDefaultOutputLocation() {
		return IPluginFacetConstants.LAYOUTTPL_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER;
	}

	protected void removeUnneededClasspathEntries() {
		IFacetedProjectWorkingCopy facetedProject = getFacetedProject();

		IJavaProject javaProject = JavaCore.create(facetedProject.getProject());

		try {
			IClasspathEntry[] existingClasspath = javaProject.getRawClasspath();
			List<IClasspathEntry> newClasspath = new ArrayList<>();

			for (IClasspathEntry entry : existingClasspath) {
				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					continue;
				}
				else if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
					String path = entry.getPath().toPortableString();

					if (path.contains("org.eclipse.jdt.launching.JRE_CONTAINER") ||
						path.contains("org.eclipse.jst.j2ee.internal.web.container") ||
						path.contains("org.eclipse.jst.j2ee.internal.module.container")) {

						continue;
					}
				}

				newClasspath.add(entry);
			}

			javaProject.setRawClasspath(newClasspath.toArray(new IClasspathEntry[0]), null);

			IResource sourceFolder =
				javaProject.getProject().findMember(IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);

			if (FileUtil.exists(sourceFolder)) {
				sourceFolder.delete(true, null);
			}
		}
		catch (Exception e) {
		}
	}

	@Override
	protected boolean shouldInstallPluginLibraryDelegate() {
		if (SDKUtil.isSDKProject(project)) {
			return true;
		}
		else {
			return false;
		}
	}

}