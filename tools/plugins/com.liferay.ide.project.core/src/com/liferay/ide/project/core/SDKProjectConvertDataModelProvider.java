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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;

import java.net.URI;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.datamodel.FacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
@SuppressWarnings({"unchecked", "restriction", "rawtypes"})
public class SDKProjectConvertDataModelProvider
	extends FacetProjectCreationDataModelProvider implements ISDKProjectsImportDataModelProperties {

	public SDKProjectConvertDataModelProvider() {
		this(null);
	}

	public SDKProjectConvertDataModelProvider(IProject project) {
		_project = project;
	}

	@Override
	public IDataModelOperation getDefaultOperation() {
		return new SDKProjectConvertOperation(getDataModel());
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (SDK_LOCATION.equals(propertyName)) {
			IPath rawLocation = _project.getRawLocation();

			if (rawLocation == null) {
				URI absoluteUri = _project.getLocationURI();

				rawLocation = new Path(absoluteUri.getPath());
			}

			return rawLocation.removeLastSegments(2).toOSString();
		}
		else if (SDK_VERSION.equals(propertyName)) {

			// see if we have a sdk location and extract the version

			String sdkLoc = getStringProperty(SDK_LOCATION);

			try {
				boolean validSDKLocation = SDKUtil.isValidSDKLocation(sdkLoc);

				if (validSDKLocation) {
					SDK sdk = SDKUtil.createSDKFromLocation(new Path(sdkLoc));

					if (sdk != null) {
						String sdkVersionValue = sdk.getVersion();

						Version v = new Version(sdkVersionValue);

						return v.toString();
					}
				}
				else {
					return StringPool.EMPTY;
				}
			}
			catch (Exception e) {
			}
		}
		else if (SELECTED_PROJECTS.equals(propertyName)) {
			return new ProjectRecord[] {new ProjectRecord(_project)};
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();

		propertyNames.add(SDK_LOCATION);
		propertyNames.add(SDK_VERSION);
		propertyNames.add(SELECTED_PROJECTS);

		return propertyNames;
	}

	@Override
	public void init() {
		super.init();

		// set the project facets to get the runtime target dropdown to only show liferay runtimes

		IFacetedProjectWorkingCopy facetedProject = getFacetedProjectWorkingCopy();

		Set<IProjectFacetVersion> facets = ProjectUtil.getFacetsForPreset(IPluginFacetConstants.LIFERAY_PORTLET_PRESET);

		Set<IProjectFacet> fixedFacets = new HashSet<>();

		for (IProjectFacetVersion pfv : facets) {
			fixedFacets.add(pfv.getProjectFacet());
		}

		facetedProject.setFixedProjectFacets(Collections.unmodifiableSet(fixedFacets));
	}

	@Override
	public boolean isPropertyEnabled(String propertyName) {
		if (SDK_VERSION.equals(propertyName) || SDK_LOCATION.equals(propertyName) ||
			SELECTED_PROJECTS.equals(propertyName)) {

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
		if (SDK_LOCATION.equals(name)) {
			String sdkLocation = getStringProperty(SDK_LOCATION);

			if (SDKUtil.isValidSDKLocation(sdkLocation)) {
				return Status.OK_STATUS;
			}
			else {
				return ProjectCore.createErrorStatus(Msgs.projectNotLocated);
			}
		}
		else if (SDK_VERSION.equals(name)) {
			String sdkVersion = getStringProperty(SDK_VERSION);

			if (SDKUtil.isValidSDKVersion(sdkVersion, SDKManager.getLeastValidVersion())) {
				return Status.OK_STATUS;
			}
			else {
				return ProjectCore.createErrorStatus(Msgs.invalidPluginSDKVersion + SDKManager.getLeastValidVersion());
			}
		}
		else if (SELECTED_PROJECTS.equals(name)) {
			Object val = getProperty(SELECTED_PROJECTS);

			if (val instanceof Object[]) {
				Object[] selectedProjects = (Object[])val;

				if (ListUtil.isNotEmpty(selectedProjects)) {
					return Status.OK_STATUS;
				}
			}

			return ProjectCore.createErrorStatus(Msgs.selectOneLiferayProject);
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
		return (IFacetedProjectWorkingCopy)model.getProperty(FACETED_PROJECT_WORKING_COPY);
	}

	private IProject _project;

	private static class Msgs extends NLS {

		public static String invalidPluginSDKVersion;
		public static String projectNotLocated;
		public static String selectOneLiferayProject;
		public static String validLiferayRuntimeSelected;

		static {
			initializeMessages(SDKProjectConvertDataModelProvider.class.getName(), Msgs.class);
		}

	}

}