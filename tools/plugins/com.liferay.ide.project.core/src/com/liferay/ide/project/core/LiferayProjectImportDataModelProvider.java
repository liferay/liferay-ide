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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.datamodel.FacetProjectCreationDataModelProvider;
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
public class LiferayProjectImportDataModelProvider
	extends FacetProjectCreationDataModelProvider implements ILiferayProjectImportDataModelProperties {

	public LiferayProjectImportDataModelProvider() {
	}

	@Override
	public IDataModelOperation getDefaultOperation() {
		return new LiferayProjectImportOperation(model);
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (SDK_VERSION.equals(propertyName)) {

			// see if we have a sdk location and extract the version

			String projectLoc = getStringProperty(PROJECT_LOCATION);

			try {
				SDK sdk = SDKUtil.getSDKFromProjectDir(new File(projectLoc));

				if (sdk != null) {
					return new Version(sdk.getVersion()).toString();
				}
			}
			catch (Exception e) {
			}
		}
		else if (PLUGIN_TYPE.equals(propertyName)) {
			return ProjectUtil.getLiferayPluginType(getStringProperty(PROJECT_LOCATION));
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();

		propertyNames.add(PROJECT_LOCATION);
		propertyNames.add(PROJECT_RECORD);
		propertyNames.add(SDK_VERSION);
		propertyNames.add(PLUGIN_TYPE);

		return propertyNames;
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
		if (SDK_VERSION.equals(propertyName) || PLUGIN_TYPE.equals(propertyName)) {
			return false;
		}

		return super.isPropertyEnabled(propertyName);
	}

	@Override
	public boolean propertySet(String propertyName, Object propertyValue) {
		return super.propertySet(propertyName, propertyValue);
	}

	@Override
	public IStatus validate(String name) {
		if (PROJECT_LOCATION.equals(name)) {
			String projectLocation = getStringProperty(PROJECT_LOCATION);

			ProjectRecord record = ProjectUtil.getProjectRecordForDir(projectLocation);

			if (record != null) {
				String projectName = record.getProjectName();

				IProject existingProject = CoreUtil.getWorkspaceRoot().getProject(projectName);

				if (FileUtil.exists(existingProject)) {
					return ProjectCore.createErrorStatus(Msgs.projectNameExists);
				}

				File projectDir = record.getProjectLocation().toFile();

				// go up to the SDK level

				SDK sdk = SDKUtil.getSDKFromProjectDir(projectDir);

				if (sdk != null) {
					return Status.OK_STATUS;
				}
				else {
					return ProjectCore.createErrorStatus(Msgs.projectNotLocated);
				}
			}

			return ProjectCore.createErrorStatus(Msgs.invalidProjectLocation);
		}
		else if (SDK_VERSION.equals(name)) {
			IStatus locationStatus = validate(PROJECT_LOCATION);

			if (locationStatus.isOK()) {
				Version version = new Version(getStringProperty(SDK_VERSION));

				if (CoreUtil.compareVersions(version, SDKManager.getLeastValidVersion()) >= 0) {
					return Status.OK_STATUS;
				}
				else {
					return ProjectCore.createErrorStatus(
						Msgs.invalidPluginSDKVersion + SDKManager.getLeastValidVersion());
				}
			}
			else {
				return locationStatus;
			}
		}
		else if (PLUGIN_TYPE.equals(name)) {
			if (ProjectUtil.isLiferayPluginType(getStringProperty(PLUGIN_TYPE))) {
				return Status.OK_STATUS;
			}
			else {
				return ProjectCore.createErrorStatus(Msgs.invalidLiferayPluginType);
			}
		}
		else if (FACET_RUNTIME.equals(name)) {
			Object runtime = getProperty(FACET_RUNTIME);

			if (!(runtime instanceof BridgedRuntime)) {
				return ProjectCore.createErrorStatus(Msgs.validLiferayRuntimeSelected);
			}
			else {
				return Status.OK_STATUS;
			}
		}
		else if (FACET_PROJECT_NAME.equals(name)) {

			// no need to check this one

			return Status.OK_STATUS;
		}

		return super.validate(name);
	}

	protected IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy() {
		return (IFacetedProjectWorkingCopy)this.model.getProperty(FACETED_PROJECT_WORKING_COPY);
	}

	private static class Msgs extends NLS {

		public static String invalidLiferayPluginType;
		public static String invalidPluginSDKVersion;
		public static String invalidProjectLocation;
		public static String projectNameExists;
		public static String projectNotLocated;
		public static String validLiferayRuntimeSelected;

		static {
			initializeMessages(LiferayProjectImportDataModelProvider.class.getName(), Msgs.class);
		}

	}

}