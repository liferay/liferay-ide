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

package com.liferay.ide.eclipse.project.core.facet;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.common.project.facet.IJavaFacetInstallDataModelProperties;
import org.eclipse.jst.common.project.facet.JavaFacetUtils;
import org.eclipse.jst.j2ee.internal.web.archive.operations.WebFacetProjectCreationDataModelProvider;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.web.project.facet.IWebFacetInstallDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( {
	"unchecked", "restriction"
})
public class PluginFacetProjectCreationDataModelProvider extends WebFacetProjectCreationDataModelProvider
	implements IPluginProjectDataModelProperties {

	public PluginFacetProjectCreationDataModelProvider() {
		super();
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (LIFERAY_SDK_NAME.equals(propertyName)) {
			SDK sdk = SDKManager.getDefaultSDK();
			if (sdk != null) {
				return sdk.getName();
			}
			else {
				return IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE;
			}
		}
		else if (LIFERAY_ADV_CONFIG.equals(propertyName)) {
			return false;
		}
		else if (LIFERAY_USE_SDK_LOCATION.equals(propertyName)) {
			return true;
		}
		else if (USE_DEFAULT_LOCATION.equals(propertyName)) {
			return false;
		}
		else if (LIFERAY_USE_CUSTOM_LOCATION.equals(propertyName)) {
			return false;
		}
		else if (PLUGIN_TYPE_PORTLET.equals(propertyName)) {
			return true;
		}
		else if (PORTLET_NAME.equals(propertyName)) {
			return getProperty(PROJECT_NAME);
		}
		else if (HOOK_NAME.equals(propertyName)) {
			return getProperty(PROJECT_NAME);
		}
		else if (EXT_NAME.equals(propertyName)) {
			return getProperty(PROJECT_NAME);
		}
		else if (THEME_NAME.equals(propertyName)) {
			return getProperty(PROJECT_NAME);
		}
		else if (LAYOUT_TEMPLATE_NAME.equals(propertyName)) {
			return getProperty(PROJECT_NAME);
		}
		else if (CREATE_PROJECT_OPERATION.equals(propertyName)) {
			return true;
		}
		return super.getDefaultProperty(propertyName);
	}

	@Override
	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		if (LIFERAY_SDK_NAME.equals(propertyName)) {
			Object val = getProperty(propertyName);
			if (IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE.equals(val) || "".equals(val)) {
				return new DataModelPropertyDescriptor(
					getProperty(propertyName), IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE_DESCRIPTION);
			}
			else {
				return new DataModelPropertyDescriptor(val, val.toString());
			}
		}
		return super.getPropertyDescriptor(propertyName);
	}

	@Override
	public Set getPropertyNames() {
		Set propNames = super.getPropertyNames();
		propNames.add(LIFERAY_SDK_NAME);
		propNames.add(LIFERAY_ADV_CONFIG);
		propNames.add(LIFERAY_USE_SDK_LOCATION);
		propNames.add(LIFERAY_USE_CUSTOM_LOCATION);
		propNames.add(PLUGIN_TYPE_PORTLET);
		propNames.add(PLUGIN_TYPE_HOOK);
		propNames.add(PLUGIN_TYPE_EXT);
		propNames.add(PLUGIN_TYPE_THEME);
		propNames.add(PLUGIN_TYPE_LAYOUT_TEMPLATE);
		// propNames.add(SETUP_PROJECT_FLAG);
		propNames.add(PORTLET_NAME);
		propNames.add(HOOK_NAME);
		propNames.add(EXT_NAME);
		propNames.add(THEME_NAME);
		propNames.add(LAYOUT_TEMPLATE_NAME);
		propNames.add(CREATE_PROJECT_OPERATION);
		return propNames;
	}

	@Override
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (LIFERAY_SDK_NAME.equals(propertyName)) {
			SDK[] validSDKs = SDKManager.getAllSDKs();
			String[] values = null;
			String[] descriptions = null;
			if (validSDKs.length == 0) {
				values = new String[] {
					IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE
				};
				descriptions = new String[] {
					IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE_DESCRIPTION
				};
			}
			else {
				values = new String[validSDKs.length];
				descriptions = new String[validSDKs.length];
				for (int i = 0; i < validSDKs.length; i++) {
					values[i] = validSDKs[i].getName();
					descriptions[i] = validSDKs[i].getName();
				}
			}
			return DataModelPropertyDescriptor.createDescriptors(values, descriptions);
		}
		return super.getValidPropertyDescriptors(propertyName);
	}

	@Override
	public void init() {
		super.init();

		getDataModel().setProperty(LIFERAY_USE_SDK_LOCATION, true);

		// Collection<IProjectFacet> existingRequiredFacets =
		// (Collection<IProjectFacet>) getProperty(REQUIRED_FACETS_COLLECTION);
		// Collection<IProjectFacet> requiredFacets = new
		// ArrayList<IProjectFacet>();
		// for (IProjectFacet f : existingRequiredFacets) {
		// requiredFacets.add(f);
		// }
		// requiredFacets.add(IPluginFacetConstants.LIFERAY_PLUGIN_FACET);
		// setProperty(REQUIRED_FACETS_COLLECTION, requiredFacets);

		getDataModel().setProperty(PLUGIN_TYPE_PORTLET, true);

		DataModelPropertyDescriptor[] validDescriptors = getDataModel().getValidPropertyDescriptors(FACET_RUNTIME);
		for (DataModelPropertyDescriptor desc : validDescriptors) {
			Object runtime = desc.getPropertyValue();
			if (runtime instanceof BridgedRuntime && ServerUtil.isPortalRuntime((BridgedRuntime) runtime)) {
				getDataModel().setProperty(FACET_RUNTIME, runtime);
				break;
			}
		}
	}

	@Override
	public boolean isPropertyEnabled(String propertyName) {
		if (PLUGIN_TYPE_THEME.equals(propertyName) || PLUGIN_TYPE_LAYOUT_TEMPLATE.equals(propertyName)) {
			return false;
		}
		return super.isPropertyEnabled(propertyName);
	}

	@Override
	public boolean propertySet(String propertyName, Object propertyValue) {
		// if (LIFERAY_USE_CUSTOM_LOCATION.equals(propertyName) &&
		// Boolean.TRUE.equals(propertyValue)) {
		// getNestedModel().setProperty(USER_DEFINED_BASE_LOCATION, null);
		// } else if (LIFERAY_USE_SDK_LOCATION.equals(propertyName) &&
		// Boolean.TRUE.equals(propertyValue)) {
		// getNestedModel().setProperty(USER_DEFINED_BASE_LOCATION,
		// getSDKLocation());
		// getNestedModel().setProperty(USE_DEFAULT_LOCATION, false);
		// } else if (LIFERAY_SDK_NAME.equals(propertyName)) {
		// if (getModel().getBooleanProperty(LIFERAY_USE_SDK_LOCATION)) {
		// getNestedModel().setProperty(USER_DEFINED_BASE_LOCATION,
		// getSDKLocation());
		// }
		// } else
		if (FACET_PROJECT_NAME.equals(propertyName) || LIFERAY_SDK_NAME.equals(propertyName)) {
			if (!(getProperty(LIFERAY_SDK_NAME).equals(IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE))) {
				getNestedModel().setProperty(USE_DEFAULT_LOCATION, false);
				getNestedModel().setProperty(USER_DEFINED_BASE_LOCATION, getProjectLocation());
			}
			// getNestedModel().setProperty(USER_DEFINED_LOCATION,
			// getProjectLocation() + File.separator + propertyValue.toString()
			// + getProjectSuffix());
			// getNestedModel().setProperty(PROJECT_LOCATION,
			// getProjectLocation() + File.separator + propertyValue.toString()
			// + getProjectSuffix());
			// getNestedModel().setProperty(PROJECT_NAME,
			// getProperty(FACET_PROJECT_NAME) + getProjectSuffix());
			// } else if (SETUP_PROJECT_FLAG.equals(propertyName)) {
		}
		else if (PLUGIN_TYPE_PORTLET.equals(propertyName) && getBooleanProperty(PLUGIN_TYPE_PORTLET)) {
			setupPortletProject();
		}
		else if (PLUGIN_TYPE_HOOK.equals(propertyName) && getBooleanProperty(PLUGIN_TYPE_HOOK)) {
			setupHookProject();
		}
		else if (PLUGIN_TYPE_EXT.equals(propertyName) && getBooleanProperty(PLUGIN_TYPE_EXT)) {
			setupExtProject();
		}
		else if (PLUGIN_TYPE_THEME.equals(propertyName) && getBooleanProperty(PLUGIN_TYPE_THEME)) {
			setupThemeProject();
		}
		else if (PLUGIN_TYPE_LAYOUT_TEMPLATE.equals(propertyName) && getBooleanProperty(PLUGIN_TYPE_LAYOUT_TEMPLATE)) {
			setupLayoutTemplateProject();
		}
		return super.propertySet(propertyName, propertyValue);
	}

	@Override
	public IStatus validate(String propertyName) {
		if (FACET_PROJECT_NAME.equals(propertyName)) {
			String facetProjectName = getStringProperty(propertyName);
			String testProjectName = facetProjectName + getProjectSuffix();
			if (ProjectUtil.getProject(testProjectName).exists()) {
				return ProjectCorePlugin.createErrorStatus("A project already exists with that name.");
			}
		}
		else if (LIFERAY_SDK_NAME.equals(propertyName)) {
			Object sdkVal = getModel().getProperty(LIFERAY_SDK_NAME);
			if (sdkVal instanceof String && IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE.equals(sdkVal)) {
				return ProjectCorePlugin.createErrorStatus("Liferay SDK must be configured.");
			}
			else if (!CoreUtil.isNullOrEmpty(sdkVal.toString())) {
				SDK sdk = SDKManager.getSDKByName(sdkVal.toString());
				return sdk != null ? Status.OK_STATUS : ProjectCorePlugin.createErrorStatus("Liferay SDK is invalid.");
			}
		}
		else if (FACET_RUNTIME.equals(propertyName)) {
			// validate the sdk first
			IStatus status = validate(LIFERAY_SDK_NAME);
			if (!status.isOK()) {
				return status;
			}
			Object facetRuntime = getProperty(FACET_RUNTIME);
			if (facetRuntime == null) {
				return ProjectCorePlugin.createErrorStatus("Liferay Portal runtime must be configured and selected.");
			}
			else if (facetRuntime instanceof BridgedRuntime) {
				if (ServerUtil.isPortalRuntime((BridgedRuntime) facetRuntime)) {
					return Status.OK_STATUS;
				}
				else {
					return ProjectCorePlugin.createErrorStatus("Must select a Liferay Portal runtime.");
				}
			}
		}
		else if (PLUGIN_TYPE_PORTLET.equals(propertyName) || PLUGIN_TYPE_HOOK.equals(propertyName) ||
			PLUGIN_TYPE_EXT.equals(propertyName) || PLUGIN_TYPE_THEME.equals(propertyName) ||
			PLUGIN_TYPE_LAYOUT_TEMPLATE.equals(propertyName)) {
			return validate(FACET_PROJECT_NAME);
		}
		return super.validate(propertyName);
	}

	protected IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy() {
		return (IFacetedProjectWorkingCopy) this.model.getProperty(FACETED_PROJECT_WORKING_COPY);
	}

	protected IDataModel getModel() {
		return this.model;
	}

	protected IDataModel getNestedModel() {
		return getDataModel().getNestedModel(NESTED_PROJECT_DM);
	}

	protected String getProjectLocation() {
		IPath sdkLoc = getSDKLocation();
		if (getBooleanProperty(PLUGIN_TYPE_PORTLET)) {
			return sdkLoc.append("portlets").toOSString();
		}
		else if (getBooleanProperty(PLUGIN_TYPE_HOOK)) {
			return sdkLoc.append("hooks").toOSString();
		}
		else if (getBooleanProperty(PLUGIN_TYPE_EXT)) {
			return sdkLoc.append("ext").toOSString();
		}
		else if (getBooleanProperty(PLUGIN_TYPE_THEME)) {
			return sdkLoc.append("themes").toOSString();
		}
		else if (getBooleanProperty(PLUGIN_TYPE_LAYOUT_TEMPLATE)) {
			return sdkLoc.append("layouttpl").toOSString();
		}
		return null;
	}

	protected String getProjectSuffix() {
		if (getBooleanProperty(PLUGIN_TYPE_PORTLET)) {
			return "-portlet";
		}
		else if (getBooleanProperty(PLUGIN_TYPE_HOOK)) {
			return "-hook";
		}
		else if (getBooleanProperty(PLUGIN_TYPE_EXT)) {
			return "-ext";
		}
		else if (getBooleanProperty(PLUGIN_TYPE_THEME)) {
			return "-theme";
		}
		else if (getBooleanProperty(PLUGIN_TYPE_LAYOUT_TEMPLATE)) {
			return "-layouttpl";
		}
		return null;
	}

	protected IPath getSDKLocation() {
		SDK sdk = SDKManager.getSDKByName((String) getProperty(LIFERAY_SDK_NAME));
		return sdk != null ? sdk.getLocation() : null;
	}

	protected void removeFacet(IFacetedProjectWorkingCopy facetedProject, IProjectFacet facet) {
		if (facetedProject == null || facet == null) {
			return;
		}

		Set<IProjectFacetVersion> newFacetSet = new HashSet<IProjectFacetVersion>();
		Set<IProjectFacetVersion> facets = facetedProject.getProjectFacets();
		for (IProjectFacetVersion fv : facets) {
			if (!(fv.getProjectFacet().equals(facet))) {
				newFacetSet.add(fv);
			}
		}
		facetedProject.setProjectFacets(newFacetSet);
	}

	protected void setupExtProject() {
		IFacetedProjectWorkingCopy facetedProject = getFacetedProjectWorkingCopy();
		removeFacet(facetedProject, PortletPluginFacetInstall.LIFERAY_PORTLET_PLUGIN_FACET);
		removeFacet(facetedProject, HookPluginFacetInstall.LIFERAY_HOOK_PLUGIN_FACET);
		IFacetedProjectTemplate template =
			ProjectFacetsManager.getTemplate(IPluginFacetConstants.LIFERAY_EXT_PLUGIN_FACET_TEMPLATE_ID);
		facetedProject.setFixedProjectFacets(Collections.unmodifiableSet(template.getFixedProjectFacets()));

		// Set<IProjectFacetVersion> facets =
		// ProjectUtil.getFacetsForPreset(IPluginFacetConstants.LIFERAY_EXT_PRESET);
		// facetedProject.setProjectFacets(Collections.unmodifiableSet(facets));

		ProjectUtil.setGenerateDD(getDataModel(), true);
		FacetDataModelMap map = (FacetDataModelMap) getProperty(FACET_DM_MAP);
		// IDataModel javaFacetModel = map.getFacetDataModel(
		// JavaFacetUtils.JAVA_FACET.getId() );
		IDataModel webFacetModel = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());
		webFacetModel.setStringProperty(
			IWebFacetInstallDataModelProperties.CONFIG_FOLDER, IPluginFacetConstants.EXT_PLUGIN_SDK_CONFIG_FOLDER);
	}

	protected void setupHookProject() {
		IFacetedProjectWorkingCopy facetedProject = getFacetedProjectWorkingCopy();
		// facetedProject.setFixedProjectFacets(template.getFixedProjectFacets());
		removeFacet(facetedProject, PortletPluginFacetInstall.LIFERAY_PORTLET_PLUGIN_FACET);
		removeFacet(facetedProject, ExtPluginFacetInstall.LIFERAY_EXT_PLUGIN_FACET);
		IFacetedProjectTemplate template =
			ProjectFacetsManager.getTemplate(IPluginFacetConstants.LIFERAY_HOOK_PLUGIN_FACET_TEMPLATE_ID);
		facetedProject.setFixedProjectFacets(Collections.unmodifiableSet(template.getFixedProjectFacets()));
		// Set<IProjectFacetVersion> facets =
		// ProjectUtil.getFacetsForPreset(IPluginFacetConstants.LIFERAY_HOOK_PRESET);
		// facetedProject.setProjectFacets(Collections.unmodifiableSet(facets));

		ProjectUtil.setGenerateDD(getDataModel(), false);
		FacetDataModelMap map = (FacetDataModelMap) getProperty(FACET_DM_MAP);
		IDataModel javaFacetModel = map.getFacetDataModel(JavaFacetUtils.JAVA_FACET.getId());
		IDataModel webFacetModel = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());
		webFacetModel.setStringProperty(
			IWebFacetInstallDataModelProperties.CONFIG_FOLDER, IPluginFacetConstants.HOOK_PLUGIN_SDK_CONFIG_FOLDER);
		webFacetModel.setStringProperty(
			IWebFacetInstallDataModelProperties.SOURCE_FOLDER, IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER);
		javaFacetModel.setStringProperty(
			IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
			IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER);
		javaFacetModel.setStringProperty(
			IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
			IPluginFacetConstants.HOOK_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER);
	}

	protected void setupLayoutTemplateProject() {

	}

	protected void setupPortletProject() {
		IFacetedProjectWorkingCopy facetedProject = getFacetedProjectWorkingCopy();

		removeFacet(facetedProject, HookPluginFacetInstall.LIFERAY_HOOK_PLUGIN_FACET);
		removeFacet(facetedProject, ExtPluginFacetInstall.LIFERAY_EXT_PLUGIN_FACET);
		IFacetedProjectTemplate template =
			ProjectFacetsManager.getTemplate(IPluginFacetConstants.LIFERAY_PORTLET_PLUGIN_FACET_TEMPLATE_ID);
		facetedProject.setFixedProjectFacets(Collections.unmodifiableSet(template.getFixedProjectFacets()));

		// Set<IProjectFacetVersion> facets =
		// ProjectUtil.getFacetsForPreset(IPluginFacetConstants.LIFERAY_PORTLET_PRESET);
		// facetedProject.setProjectFacets(Collections.unmodifiableSet(facets));

		ProjectUtil.setGenerateDD(getDataModel(), true);
		FacetDataModelMap map = (FacetDataModelMap) getProperty(FACET_DM_MAP);
		IDataModel webFacetModel = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());
		webFacetModel.setStringProperty(
			IWebFacetInstallDataModelProperties.CONFIG_FOLDER, IPluginFacetConstants.PORTLET_PLUGIN_SDK_CONFIG_FOLDER);
		webFacetModel.setStringProperty(
			IWebFacetInstallDataModelProperties.SOURCE_FOLDER, IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);
		IDataModel javaFacetModel = map.getFacetDataModel(JavaFacetUtils.JAVA_FACET.getId());
		javaFacetModel.setStringProperty(
			IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
			IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER);
		javaFacetModel.setStringProperty(
			IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
			IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER);
	}

	protected void setupThemeProject() {

	}
}
