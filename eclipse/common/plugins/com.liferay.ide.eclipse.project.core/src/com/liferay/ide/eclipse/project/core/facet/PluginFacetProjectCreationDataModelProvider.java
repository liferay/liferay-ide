/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
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
import com.liferay.ide.eclipse.project.core.IPortletFramework;
import com.liferay.ide.eclipse.project.core.IProjectDefinition;
import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jst.j2ee.internal.web.archive.operations.WebFacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( {
	"unchecked", "restriction", "rawtypes"
})
public class PluginFacetProjectCreationDataModelProvider extends WebFacetProjectCreationDataModelProvider
	implements IPluginProjectDataModelProperties {

	protected Map<String, IProjectDefinition> projectDefinitions = new HashMap<String, IProjectDefinition>();

	public PluginFacetProjectCreationDataModelProvider() {
		super();
		IProjectDefinition[] definitions = ProjectCorePlugin.getProjectDefinitions();

		for (IProjectDefinition definition : definitions) {
			if (definition.getFacetId() != null) {
				projectDefinitions.put(definition.getFacetId(), definition);
			}
		}
	}

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
		else if (PORTLET_FRAMEWORK.equals(propertyName)) {
			return ProjectCorePlugin.getPortletFrameworks()[0];
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
		propNames.add(LIFERAY_USE_SDK_LOCATION);
		propNames.add(LIFERAY_USE_CUSTOM_LOCATION);
		propNames.add(PLUGIN_TYPE_PORTLET);
		propNames.add(PLUGIN_TYPE_HOOK);
		propNames.add(PLUGIN_TYPE_EXT);
		propNames.add(PLUGIN_TYPE_THEME);
		propNames.add(PLUGIN_TYPE_LAYOUTTPL);
		propNames.add(DISPLAY_NAME);
		propNames.add(PORTLET_NAME);
		propNames.add(HOOK_NAME);
		propNames.add(EXT_NAME);
		propNames.add(THEME_NAME);
		propNames.add(LAYOUTTPL_NAME);
		propNames.add(CREATE_PROJECT_OPERATION);
		propNames.add(PLUGIN_FRAGMENT_ENABLED);
		propNames.add(PLUGIN_FRAGMENT_DM);
		propNames.add(PORTLET_FRAMEWORK);

		return propNames;
	}

	@Override
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (LIFERAY_SDK_NAME.equals(propertyName)) {
			SDK[] validSDKs = SDKManager.getInstance().getSDKs();
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

		DataModelPropertyDescriptor[] validDescriptors = getDataModel().getValidPropertyDescriptors(FACET_RUNTIME);

		for (DataModelPropertyDescriptor desc : validDescriptors) {
			Object runtime = desc.getPropertyValue();

			if (runtime instanceof BridgedRuntime && ServerUtil.isLiferayRuntime((BridgedRuntime) runtime)) {
				getDataModel().setProperty(FACET_RUNTIME, runtime);
				break;
			}
		}
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
			// if (!(getProperty(LIFERAY_SDK_NAME).equals(IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE))) {
			updateProjectLocation();
			// }
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
			setupProject(IPluginFacetConstants.LIFERAY_PORTLET_FACET_ID);
		}
		else if (PLUGIN_TYPE_HOOK.equals(propertyName) && getBooleanProperty(PLUGIN_TYPE_HOOK)) {
			setupProject(IPluginFacetConstants.LIFERAY_HOOK_FACET_ID);
		}
		else if (PLUGIN_TYPE_EXT.equals(propertyName) && getBooleanProperty(PLUGIN_TYPE_EXT)) {
			setupProject(IPluginFacetConstants.LIFERAY_EXT_FACET_ID);
		}
		else if (PLUGIN_TYPE_THEME.equals(propertyName) && getBooleanProperty(PLUGIN_TYPE_THEME)) {
			setupProject(IPluginFacetConstants.LIFERAY_THEME_FACET_ID);
		}
		else if (PLUGIN_TYPE_LAYOUTTPL.equals(propertyName) && getBooleanProperty(PLUGIN_TYPE_LAYOUTTPL)) {
			setupProject(IPluginFacetConstants.LIFERAY_LAYOUTTPL_FACET_ID);
		}
		else if (DISPLAY_NAME.equals(propertyName)) {
			String displayName = ProjectUtil.removePluginSuffix(propertyValue.toString());

			return super.propertySet(DISPLAY_NAME, displayName);
		}
		else if (PORTLET_FRAMEWORK.equals(propertyName)) {
			IPortletFramework portletFramework = (IPortletFramework) getProperty(PORTLET_FRAMEWORK);

			setupPortletFramework(portletFramework);
		}

		return super.propertySet(propertyName, propertyValue);
	}

	@Override
	public IStatus validate(String propertyName) {
		if (FACET_PROJECT_NAME.equals(propertyName)) {
			String projectName = getNestedModel().getStringProperty(PROJECT_NAME);
			
			if (CoreUtil.isNullOrEmpty(projectName)) {
				return super.validate(propertyName);
			}
			
			String testProjectName = projectName + getProjectSuffix();

			if (ProjectUtil.getProject(testProjectName).exists()) {
				return ProjectCorePlugin.createErrorStatus("A project already exists with that name.");
			}

			// before we do a basic java validation we need to strip "-" and "." characters

			String nameValidation = testProjectName.replaceAll( "-", "" ).replaceAll( "\\.", "" );

			IStatus status =
				JavaConventions.validateIdentifier(
					nameValidation, CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5);

			if (!status.isOK()) {
				return ProjectCorePlugin.createErrorStatus("Project name is invalid.");
			}
		}
		else if (LIFERAY_SDK_NAME.equals(propertyName)) {
			Object sdkVal = getModel().getProperty(LIFERAY_SDK_NAME);

			if (sdkVal instanceof String && IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE.equals(sdkVal)) {
				return ProjectCorePlugin.createErrorStatus("Plugin SDK must be configured.");
			}
			else if (!CoreUtil.isNullOrEmpty(sdkVal.toString())) {
				SDK sdk = SDKManager.getInstance().getSDK(sdkVal.toString());

				if (sdk == null || !sdk.isValid()) {
					return ProjectCorePlugin.createErrorStatus("Plugin SDK is invalid.");
				}
				else {
					return Status.OK_STATUS;
				}
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
				if (ServerUtil.isLiferayRuntime((BridgedRuntime) facetRuntime)) {
					return Status.OK_STATUS;
				}
				else {
					return ProjectCorePlugin.createErrorStatus("Must select a Liferay Portal runtime.");
				}
			}
		}
		else if (PLUGIN_TYPE_PORTLET.equals(propertyName) || PLUGIN_TYPE_HOOK.equals(propertyName) ||
			PLUGIN_TYPE_EXT.equals(propertyName) || PLUGIN_TYPE_THEME.equals(propertyName) ||
			PLUGIN_TYPE_LAYOUTTPL.equals(propertyName)) {

			return validate(FACET_PROJECT_NAME);
		}
		else if (PORTLET_FRAMEWORK.equals(propertyName) && getBooleanProperty(PLUGIN_TYPE_PORTLET)) {
			// check to make sure that the current SDK has the propery version
			String sdkName = getStringProperty(LIFERAY_SDK_NAME);
			SDK selectedSDK = SDKManager.getInstance().getSDK(sdkName);

			if (selectedSDK == null) {
				return ProjectCorePlugin.createErrorStatus("Unable to determine SDK version for " + sdkName);
			}

			Version sdkVersion = new Version(selectedSDK.getVersion());

			IPortletFramework framework = (IPortletFramework) getProperty(PORTLET_FRAMEWORK);

			Version requiredSDKVersion = new Version(framework.getRequiredSDKVersion());

			if (sdkVersion.compareTo(requiredSDKVersion) < 0) {
				return framework.getUnsupportedSDKErrorMsg();
			}
			else {
				return Status.OK_STATUS;
			}
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

		return null;
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
		return null;
	}

	protected IPath getSDKLocation() {
		SDK sdk = SDKManager.getInstance().getSDK((String) getProperty(LIFERAY_SDK_NAME));
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

	protected void setupPortletFramework(IPortletFramework portletFramework) {
		IPortletFramework[] portletFrameworks = ProjectCorePlugin.getPortletFrameworks();

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

	protected void setupProject(String facetId) {
		updateProjectLocation();

		IProjectDefinition projectDefinition = projectDefinitions.get(facetId);

		if (projectDefinition != null) {
			IFacetedProjectWorkingCopy facetedProject = getFacetedProjectWorkingCopy();

			for (IProjectDefinition def : projectDefinitions.values()) {
				removeFacet(facetedProject, def.getFacet());
			}

			IFacetedProjectTemplate template = projectDefinition.getFacetedProjectTemplate();

			if (template != null) {
				facetedProject.setFixedProjectFacets(Collections.unmodifiableSet(template.getFixedProjectFacets()));
			}

			projectDefinition.setupNewProject(getDataModel(), facetedProject);
		}
	}

	protected void updateProjectLocation() {
		String projectLocation = getProjectLocation();

		if (projectLocation == null) {
			getNestedModel().setProperty(USE_DEFAULT_LOCATION, true);
		}
		else {
			getNestedModel().setProperty(USE_DEFAULT_LOCATION, false);
			getNestedModel().setProperty(USER_DEFINED_BASE_LOCATION, projectLocation);
		}
	}
}
