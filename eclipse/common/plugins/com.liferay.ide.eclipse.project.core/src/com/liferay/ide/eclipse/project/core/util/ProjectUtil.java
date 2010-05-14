/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.project.core.util;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.facet.ExtPluginFacetInstall;
import com.liferay.ide.eclipse.project.core.facet.HookPluginFacetInstall;
import com.liferay.ide.eclipse.project.core.facet.PortletPluginFacetInstall;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;

import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetInstallDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FacetDataModelMap;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class ProjectUtil {

	public static IFacetedProject getFacetedProject(IProject project) {
		try {
			return ProjectFacetsManager.create(project);
		}
		catch (CoreException e) {
			return null;
		}
	}

	public static Set<IProjectFacetVersion> getFacetsForPreset(String presetId) {
		IPreset preset = ProjectFacetsManager.getPreset(presetId);
		return preset.getProjectFacets();
	}

	public static IProjectFacet getLiferayFacet(IFacetedProject facetedProject) {
		for (IProjectFacetVersion projectFacet : facetedProject.getProjectFacets()) {
			if (isLiferayFacet(projectFacet.getProjectFacet())) {
				return projectFacet.getProjectFacet();
			}
		}
		return null;
	}

	public static IProject getProject(IDataModel model) {
		if (model != null) {
			String projectName = model.getStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME);
			return CoreUtil.getProject(projectName);
		}
		return null;
	}

	public static IProject getProject(String testProjectName) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(testProjectName);
	}

	public static SDK getSDK(IProject project, IProjectFacet facet)
		throws BackingStoreException {
		IFacetedProject factedProject = getFacetedProject(project);
		Preferences prefs = factedProject.getPreferences(facet).node("liferay-plugin-project");
		String name = prefs.get(ISDKConstants.PROPERTY_NAME, null);
		return SDKManager.getSDKByName(name);
	}

	public static boolean hasFacet(IProject project, IProjectFacet checkProjectFacet) {
		boolean retval = false;
		if (project == null || checkProjectFacet == null) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject != null && checkProjectFacet != null) {
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					IProjectFacet projectFacet = facet.getProjectFacet();
					if (checkProjectFacet.equals(projectFacet)) {
						retval = true;
						break;
					}
				}
			}
		}
		catch (CoreException e) {
		}
		return retval;
	}

	public static boolean isDynamicWebFacet(IProjectFacet facet) {
		return facet != null && facet.getId().equals(IModuleConstants.JST_WEB_MODULE);
	}

	public static boolean isExtProject(IProject project) {
		return hasFacet(project, ExtPluginFacetInstall.LIFERAY_EXT_PLUGIN_FACET);
	}

	public static boolean isHookProject(IProject project) {
		return hasFacet(project, HookPluginFacetInstall.LIFERAY_HOOK_PLUGIN_FACET);
	}

	public static boolean isJavaFacet(IProjectFacet facet) {
		return facet != null && facet.getId().equals(IModuleConstants.JST_JAVA);
	}

	public static boolean isLiferayFacet(IProjectFacet projectFacet) {
		return PortletPluginFacetInstall.LIFERAY_PORTLET_PLUGIN_FACET.equals(projectFacet) ||
			HookPluginFacetInstall.LIFERAY_HOOK_PLUGIN_FACET.equals(projectFacet) ||
			ExtPluginFacetInstall.LIFERAY_EXT_PLUGIN_FACET.equals(projectFacet);
	}

	public static boolean isLiferayProject(IProject project) {
		boolean retval = false;
		if (project == null) {
			return retval;
		}

		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project);
			if (facetedProject != null) {
				for (IProjectFacetVersion facet : facetedProject.getProjectFacets()) {
					IProjectFacet projectFacet = facet.getProjectFacet();
					if (PortletPluginFacetInstall.LIFERAY_PORTLET_PLUGIN_FACET.equals(projectFacet) ||
						HookPluginFacetInstall.LIFERAY_HOOK_PLUGIN_FACET.equals(projectFacet) ||
						ExtPluginFacetInstall.LIFERAY_EXT_PLUGIN_FACET.equals(projectFacet)) {
						retval = true;
						break;
					}
				}
			}
		}
		catch (Exception e) {
		}
		return retval;
	}

	public static boolean isParent(IFolder folder, IResource resource) {
		if (folder == null || resource == null) {
			return false;
		}

		if (resource.getParent() != null && resource.getParent().equals(folder)) {
			return true;
		}
		else {
			boolean retval = isParent(folder, resource.getParent());
			if (retval == true) {
				return true;
			}
		}

		return false;
	}

	public static boolean isPortletProject(IProject project) {
		return hasFacet(project, PortletPluginFacetInstall.LIFERAY_PORTLET_PLUGIN_FACET);
	}

	public static void setGenerateDD(IDataModel model, boolean generateDD) {
		FacetDataModelMap map =
			(FacetDataModelMap) model.getProperty(IFacetProjectCreationDataModelProperties.FACET_DM_MAP);
		IDataModel webFacet = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());
		webFacet.setBooleanProperty(IJ2EEFacetInstallDataModelProperties.GENERATE_DD, generateDD);
	}

	public static void setSDK(IProject project, IProjectFacet facet, SDK sdk)
		throws BackingStoreException, CoreException {
		Preferences prefs = ProjectFacetsManager.create(project).getPreferences(facet).node("liferay-plugin-project");
		prefs.put(ISDKConstants.PROPERTY_NAME, sdk.getName());
		prefs.flush();
	}

}
