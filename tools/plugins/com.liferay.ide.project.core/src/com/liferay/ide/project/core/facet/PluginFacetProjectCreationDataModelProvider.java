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

package com.liferay.ide.project.core.facet;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.server.util.ServerUtil;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.j2ee.internal.web.archive.operations.WebFacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Terry Jia
 */
@SuppressWarnings({"unchecked", "restriction", "rawtypes"})
public class PluginFacetProjectCreationDataModelProvider
	extends WebFacetProjectCreationDataModelProvider implements IPluginProjectDataModelProperties {

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (LIFERAY_SDK_NAME.equals(propertyName)) {
			SDK sdk = SDKManager.getInstance().getDefaultSDK();

			if (sdk != null) {
				return sdk.getName();
			}
			else {
				return IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE;
			}
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
		else if (LIFERAY_USE_WORKSPACE_LOCATION.equals(propertyName)) {
			return false;
		}
		else if (PLUGIN_TYPE_PORTLET.equals(propertyName)) {
			return true;
		}
		else if (PORTLET_NAME.equals(propertyName)) {
			return getProperty(PROJECT_NAME);
		}
		else if (DISPLAY_NAME.equals(propertyName)) {
			return ProjectUtil.convertToDisplayName(getStringProperty(PROJECT_NAME));
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
		else if (LAYOUTTPL_NAME.equals(propertyName)) {
			return getProperty(PROJECT_NAME);
		}
		else if (CREATE_PROJECT_OPERATION.equals(propertyName)) {
			return true;
		}
		else if (PORTLET_FRAMEWORK_ID.equals(propertyName)) {
			return ProjectCore.getPortletFrameworks()[0].getId();
		}
		else if (THEME_PARENT.equals(propertyName)) {
			return "_styled";
		}
		else if (THEME_TEMPLATE_FRAMEWORK.equals(propertyName)) {
			return "Velocity";
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		if (LIFERAY_SDK_NAME.equals(propertyName)) {
			Object val = getProperty(propertyName);

			if (IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE.equals(val) || StringPool.EMPTY.equals(val)) {
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
		propNames.add(LIFERAY_USE_SDK_LOCATION);
		propNames.add(LIFERAY_USE_CUSTOM_LOCATION);
		propNames.add(PLUGIN_TYPE_PORTLET);
		propNames.add(PLUGIN_TYPE_HOOK);
		propNames.add(PLUGIN_TYPE_EXT);
		propNames.add(PLUGIN_TYPE_THEME);
		propNames.add(PLUGIN_TYPE_LAYOUTTPL);
		propNames.add(PLUGIN_TYPE_WEB);
		propNames.add(DISPLAY_NAME);
		propNames.add(PORTLET_NAME);
		propNames.add(HOOK_NAME);
		propNames.add(EXT_NAME);
		propNames.add(THEME_NAME);
		propNames.add(LAYOUTTPL_NAME);
		propNames.add(CREATE_PROJECT_OPERATION);
		propNames.add(PLUGIN_FRAGMENT_ENABLED);
		propNames.add(PLUGIN_FRAGMENT_DM);
		propNames.add(PORTLET_FRAMEWORK_ID);
		propNames.add(THEME_PARENT);
		propNames.add(THEME_TEMPLATE_FRAMEWORK);
		propNames.add(PROJECT_TEMP_PATH);
		propNames.add(LIFERAY_USE_WORKSPACE_LOCATION);

		return propNames;
	}

	@Override
	public void init() {
		super.init();

		getDataModel().setProperty(LIFERAY_USE_SDK_LOCATION, true);

		DataModelPropertyDescriptor[] validDescriptors = getDataModel().getValidPropertyDescriptors(FACET_RUNTIME);

		for (DataModelPropertyDescriptor desc : validDescriptors) {
			Object runtime = desc.getPropertyValue();

			if (runtime instanceof BridgedRuntime && ServerUtil.isLiferayRuntime((BridgedRuntime)runtime)) {
				getDataModel().setProperty(FACET_RUNTIME, runtime);
				break;
			}
		}

		ProjectCore.getPortletFrameworks();
	}

	@Override
	public boolean propertySet(final String propertyName, final Object propertyValue) {
		String portletFrameworkId = getStringProperty(PORTLET_FRAMEWORK_ID);

		IPortletFramework portletFramework = ProjectCore.getPortletFramework(portletFrameworkId);

		if (FACET_PROJECT_NAME.equals(propertyName) || LIFERAY_SDK_NAME.equals(propertyName) ||
			LIFERAY_USE_WORKSPACE_LOCATION.equals(propertyName) || LIFERAY_USE_CUSTOM_LOCATION.equals(propertyName)) {

			updateProjectLocation();
		}
		else if (DISPLAY_NAME.equals(propertyName)) {
			String displayName = ProjectUtil.removePluginSuffix(propertyValue.toString());

			return super.propertySet(DISPLAY_NAME, displayName);
		}
		else if (PORTLET_FRAMEWORK_ID.equals(propertyName) && (portletFramework != null)) {
			setupPortletFramework(portletFramework);
		}

		return super.propertySet(propertyName, propertyValue);
	}

	protected String getDefaultSDKProjectBaseLocation() {
		IPath sdkLoc = getSDKLocation();

		if (sdkLoc == null) {
			return null;
		}

		if (getBooleanProperty(PLUGIN_TYPE_PORTLET)) {
			return sdkLoc.append(ISDKConstants.PORTLET_PLUGIN_PROJECT_FOLDER).toOSString();
		}
		else if (getBooleanProperty(PLUGIN_TYPE_HOOK)) {
			return sdkLoc.append(ISDKConstants.HOOK_PLUGIN_PROJECT_FOLDER).toOSString();
		}
		else if (getBooleanProperty(PLUGIN_TYPE_EXT)) {
			return sdkLoc.append(ISDKConstants.EXT_PLUGIN_PROJECT_FOLDER).toOSString();
		}
		else if (getBooleanProperty(PLUGIN_TYPE_THEME)) {
			return sdkLoc.append(ISDKConstants.THEME_PLUGIN_PROJECT_FOLDER).toOSString();
		}
		else if (getBooleanProperty(PLUGIN_TYPE_LAYOUTTPL)) {
			return sdkLoc.append(ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_FOLDER).toOSString();
		}
		else if (getBooleanProperty(PLUGIN_TYPE_WEB)) {
			return sdkLoc.append(ISDKConstants.WEB_PLUGIN_PROJECT_FOLDER).toOSString();
		}

		return null;
	}

	protected IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy() {
		return (IFacetedProjectWorkingCopy)model.getProperty(FACETED_PROJECT_WORKING_COPY);
	}

	protected IDataModel getModel() {
		return model;
	}

	protected IDataModel getNestedModel() {
		return getDataModel().getNestedModel(NESTED_PROJECT_DM);
	}

	protected String getProjectSuffix() {
		if (getBooleanProperty(PLUGIN_TYPE_PORTLET)) {
			return ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
		}
		else if (getBooleanProperty(PLUGIN_TYPE_HOOK)) {
			return ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
		}
		else if (getBooleanProperty(PLUGIN_TYPE_EXT)) {
			return ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
		}
		else if (getBooleanProperty(PLUGIN_TYPE_THEME)) {
			return ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;
		}
		else if (getBooleanProperty(PLUGIN_TYPE_LAYOUTTPL)) {
			return ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
		}
		else if (getBooleanProperty(PLUGIN_TYPE_WEB)) {
			return ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX;
		}

		return null;
	}

	protected IPath getSDKLocation() {
		SDK sdk = SDKManager.getInstance().getSDK((String)getProperty(LIFERAY_SDK_NAME));

		if (sdk != null) {
			return sdk.getLocation();
		}

		return null;
	}

	protected void removeFacet(IFacetedProjectWorkingCopy facetedProject, IProjectFacet facet) {
		if ((facetedProject == null) || (facet == null)) {
			return;
		}

		Set<IProjectFacetVersion> newFacetSet = new HashSet<>();

		Set<IProjectFacetVersion> facets = facetedProject.getProjectFacets();

		for (IProjectFacetVersion fv : facets) {
			if (!(fv.getProjectFacet().equals(facet))) {
				newFacetSet.add(fv);
			}
		}

		facetedProject.setProjectFacets(newFacetSet);
	}

	protected void setupPortletFramework(IPortletFramework portletFramework) {
		IPortletFramework[] portletFrameworks = ProjectCore.getPortletFrameworks();

		for (IPortletFramework framework : portletFrameworks) {
			if (!framework.equals(portletFramework)) {
				IProjectFacet[] facets = framework.getFacets();

				for (IProjectFacet facet : facets) {
					removeFacet(getFacetedProjectWorkingCopy(), facet);
				}
			}
		}

		portletFramework.configureNewProject(getDataModel(), getFacetedProjectWorkingCopy());
	}

	protected void updateProjectLocation() {
		boolean useSdkLocation = getBooleanProperty(LIFERAY_USE_SDK_LOCATION);
		boolean useCustomLocation = getBooleanProperty(LIFERAY_USE_CUSTOM_LOCATION);

		final String projectBaseLocation = getDefaultSDKProjectBaseLocation();

		if (useSdkLocation) {
			getNestedModel().setProperty(USE_DEFAULT_LOCATION, false);
			getNestedModel().setProperty(USER_DEFINED_BASE_LOCATION, projectBaseLocation);
		}
		else if (useCustomLocation) {
			getNestedModel().setProperty(USE_DEFAULT_LOCATION, false);
			getNestedModel().setProperty(USER_DEFINED_BASE_LOCATION, projectBaseLocation);
		}
		else {
			getNestedModel().setProperty(USE_DEFAULT_LOCATION, true);
		}
	}

}