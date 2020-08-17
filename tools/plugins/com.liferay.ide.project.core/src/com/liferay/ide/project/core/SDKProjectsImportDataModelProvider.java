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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.datamodel.FacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings({"unchecked", "restriction", "rawtypes"})
public class SDKProjectsImportDataModelProvider
	extends FacetProjectCreationDataModelProvider implements ISDKProjectsImportDataModelProperties {

	public SDKProjectsImportDataModelProvider() {
	}

	public IStatus createSelectedProjectsErrorStatus() {
		return ProjectCore.createErrorStatus(Msgs.selectOneLiferayProject);
	}

	@Override
	public IDataModelOperation getDefaultOperation() {
		return new SDKProjectsImportOperation(model);
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (SDK_LOCATION.equals(propertyName)) {
			String sdkName = getStringProperty(LIFERAY_SDK_NAME);

			SDKManager sdkManager = SDKManager.getInstance();

			SDK sdk = sdkManager.getSDK(sdkName);

			if (sdk != null) {
				return FileUtil.toOSString(sdk.getLocation());
			}
		}
		else if (SDK_VERSION.equals(propertyName)) {

			// see if we have a sdk location and extract the version

			String sdkLoc = getStringProperty(SDK_LOCATION);

			try {
				SDK sdk = SDKUtil.createSDKFromLocation(new Path(sdkLoc));

				if (sdk != null) {
					Version v = new Version(sdk.getVersion());

					return v.toString();
				}
			}
			catch (Exception e) {
			}
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();

		propertyNames.add(LIFERAY_SDK_NAME);
		propertyNames.add(SDK_LOCATION);
		propertyNames.add(SDK_VERSION);
		propertyNames.add(SELECTED_PROJECTS);

		return propertyNames;
	}

	@Override
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (LIFERAY_SDK_NAME.equals(propertyName)) {
			SDKManager sdkManager = SDKManager.getInstance();

			SDK[] validSDKs = sdkManager.getSDKs();

			String[] values = null;

			String[] descriptions = null;

			if (validSDKs.length == 0) {
				values = new String[] {IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE};
				descriptions = new String[] {IPluginFacetConstants.LIFERAY_SDK_NAME_DEFAULT_VALUE_DESCRIPTION};
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

		// set the project facets to get the runtime target dropdown to only show liferay runtimes

		IFacetedProjectWorkingCopy facetedProject = getFacetedProjectWorkingCopy();

		facetedProject.setSelectedPreset(IPluginFacetConstants.LIFERAY_PORTLET_PRESET);

		Set<IProjectFacet> fixedFacets = new HashSet<>();

		fixedFacets.add(ProjectFacetsManager.getProjectFacet(IPluginFacetConstants.LIFERAY_EXT_FACET_ID));

		facetedProject.setFixedProjectFacets(Collections.unmodifiableSet(fixedFacets));

		ProjectUtil.setDefaultRuntime(getDataModel());
	}

	@Override
	public boolean isPropertyEnabled(String propertyName) {
		if (SDK_VERSION.equals(propertyName) || SDK_LOCATION.equals(propertyName)) {
			return false;
		}

		return super.isPropertyEnabled(propertyName);
	}

	@Override
	public IStatus validate(String name) {
		if (SDK_LOCATION.equals(name)) {
			String sdkLocation = getStringProperty(SDK_LOCATION);

			if (SDKUtil.isValidSDKLocation(sdkLocation)) {
				return Status.OK_STATUS;
			}

			return ProjectCore.createErrorStatus(Msgs.invalidPluginSDKLocation);
		}
		else if (SDK_VERSION.equals(name)) {
			String sdkVersion = getStringProperty(SDK_VERSION);

			if (SDKUtil.isValidSDKVersion(sdkVersion, SDKManager.getLeastValidVersion())) {
				Object runtime = getProperty(FACET_RUNTIME);

				if (_compareSDKRuntimeVersion(sdkVersion, runtime)) {
					return Status.OK_STATUS;
				}

				return ProjectCore.createWarningStatus(Msgs.versionUnequal);
			}

			return ProjectCore.createErrorStatus(Msgs.invalidPluginSDKVersion + SDKManager.getLeastValidVersion());
		}
		else if (SELECTED_PROJECTS.equals(name)) {
			Object val = getProperty(SELECTED_PROJECTS);

			if (val instanceof Object[]) {
				Object[] selectedProjects = (Object[])val;

				if (selectedProjects.length >= 1) {
					for (Object project : selectedProjects) {
						if (project instanceof BinaryProjectRecord) {
							BinaryProjectRecord binaryProject = (BinaryProjectRecord)project;

							Version sdkVersion = Version.parseVersion(getStringProperty(SDK_VERSION));

							if (binaryProject.isWeb() &&
								(CoreUtil.compareVersions(sdkVersion, ILiferayConstants.V700) < 0)) {

								return ProjectCore.createErrorStatus(Msgs.unableSupportWebPluginType);
							}
						}
					}

					return Status.OK_STATUS;
				}
			}

			return createSelectedProjectsErrorStatus();
		}
		else if (FACET_RUNTIME.equals(name)) {
			Object runtime = getProperty(FACET_RUNTIME);

			if (!(runtime instanceof BridgedRuntime)) {
				return ProjectCore.createErrorStatus(Msgs.selectValidLiferayRuntime);
			}

			String sdkVersion = getStringProperty(SDK_VERSION);

			if (_compareSDKRuntimeVersion(sdkVersion, runtime)) {
				return Status.OK_STATUS;
			}

			return ProjectCore.createWarningStatus(Msgs.versionUnequal);
		}
		else if (FACET_PROJECT_NAME.equals(name)) {

			// no need to check this one

			return Status.OK_STATUS;
		}

		return super.validate(name);
	}

	public IStatus validateSuper(String name) {
		return super.validate(name);
	}

	protected IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy() {
		return (IFacetedProjectWorkingCopy)model.getProperty(FACETED_PROJECT_WORKING_COPY);
	}

	private boolean _compareSDKRuntimeVersion(String sdkVersion, Object runtime) {
		if ((sdkVersion == null) && !(runtime instanceof BridgedRuntime)) {
			return false;
		}

		try {
			Version liferaySdkVersion = new Version(sdkVersion);

			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime((BridgedRuntime)runtime);

			String runtimeVersion = liferayRuntime.getPortalVersion();

			Version liferayRuntimeVersion = new Version(runtimeVersion);

			if ((liferaySdkVersion.getMajor() == liferayRuntimeVersion.getMajor()) &&
				(liferaySdkVersion.getMinor() == liferayRuntimeVersion.getMinor())) {

				return true;
			}

			return false;
		}
		catch (Exception e) {
			LiferayCore.logError("invalid sdk or runtime version ", e);

			return false;
		}
	}

	private static class Msgs extends NLS {

		public static String invalidPluginSDKLocation;
		public static String invalidPluginSDKVersion;
		public static String selectOneLiferayProject;
		public static String selectValidLiferayRuntime;
		public static String unableSupportWebPluginType;
		public static String versionUnequal;

		static {
			initializeMessages(SDKProjectsImportDataModelProvider.class.getName(), Msgs.class);
		}

	}

}