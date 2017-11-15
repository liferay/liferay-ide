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

package com.liferay.ide.portlet.core.dd;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Simon Jiang
 * @author Kuo Zhang
 */
public class PluginPackagesDescriptorHelper extends LiferayDescriptorHelper {

	public static final String DESCRIPTOR_FILE = ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE;

	public PluginPackagesDescriptorHelper() {
	}

	public PluginPackagesDescriptorHelper(IProject project) {
		super(project);
	}

	public IStatus addNewPortlet(IDataModel dataModel) {
		return Status.OK_STATUS;
	}

	public IFile getDescriptorFile() {
		return super.getDescriptorFile(DESCRIPTOR_FILE);
	}

	public IStatus removeAllPortlets() {
		return Status.OK_STATUS;
	}

	@Override
	protected void addDescriptorOperations() {

		// currently, no descriptor operations for this descriptor

	}

}