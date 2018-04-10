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

package com.liferay.ide.portlet.vaadin.core.dd;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.IPluginPackageModel;
import com.liferay.ide.portlet.core.PluginPropertiesConfiguration;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.dd.PluginPackagesDescriptorHelper;
import com.liferay.ide.project.core.descriptor.AddNewPortletOperation;

import java.io.File;
import java.io.FileWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Kuo Zhang
 * @author Andy Wu
 */
public class VaadinPluginPackageDescriptorHelper extends PluginPackagesDescriptorHelper {

	public VaadinPluginPackageDescriptorHelper() {
	}

	public VaadinPluginPackageDescriptorHelper(IProject project) {
		super(project);
	}

	public IStatus addPortalDependency(String propertyName, String value) {
		if (CoreUtil.isNullOrEmpty(value)) {
			return Status.OK_STATUS;
		}

		try {
			IFile pluginPackageFile = getDescriptorFile();

			if (FileUtil.notExists(pluginPackageFile)) {
				IStatus warning = PortletCore.createWarningStatus(
					"No " + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE + " file in the project.");

				ILog log = PortletCore.getDefault().getLog();

				log.log(warning);

				return Status.OK_STATUS;
			}

			File osfile = new File(pluginPackageFile.getLocation().toOSString());

			PluginPropertiesConfiguration pluginPackageProperties = new PluginPropertiesConfiguration();

			pluginPackageProperties.load(osfile);

			String existingDeps = pluginPackageProperties.getString(propertyName, StringPool.EMPTY);

			String[] existingValues = existingDeps.split(",");

			for (String existingValue : existingValues) {
				if (value.equals(existingValue)) {
					return Status.OK_STATUS;
				}
			}

			String newPortalDeps = null;

			if (CoreUtil.isNullOrEmpty(existingDeps)) {
				newPortalDeps = value;
			}
			else {
				newPortalDeps = existingDeps + "," + value;
			}

			pluginPackageProperties.setProperty(propertyName, newPortalDeps);

			try(FileWriter output = new FileWriter(osfile)) {
				pluginPackageProperties.save(output);
			}

			// refresh file

			pluginPackageFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
		}
		catch (Exception e) {
			PortletCore.logError(e);

			return PortletCore.createErrorStatus(
				"Could not add dependency in " + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE);
		}

		return Status.OK_STATUS;
	}

	@Override
	protected void addDescriptorOperations() {
		AddNewPortletOperation addNewPortletOperation = new AddNewPortletOperation() {

			@Override
			public IStatus addNewPortlet(IDataModel model) {

				// When a vaadin portlet is added, the liferay-plugin-package.properties won't
				// add an element called
				// "portlet",
				// it needs add a line "portal-dependency-jars=vaadin.jar"

				if (_canAddNewPortlet(model)) {
					return addPortalDependency(IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_JARS, "vaadin.jar");
				}

				return Status.OK_STATUS;
			}

		};

		addDescriptorOperation(addNewPortletOperation);
	}

	private boolean _canAddNewPortlet(IDataModel dataModel) {
		return dataModel.getID().contains("NewVaadinPortlet");
	}

}