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

package com.liferay.ide.project.ui.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

/**
 * @author Gregory Amerson
 */
public interface IPluginWizardFragment {

	public static final String ID = "com.liferay.ide.project.ui.pluginWizardFragment";

	public void addPages();

	public IDataModelProvider getDataModelProvider();

	public IWizardPage getNextPage(IWizardPage page);

	public void initFragmentDataModel(IDataModel parentDataModel, String projectName);

	public void setDataModel(IDataModel model);

	public void setFragment(boolean fragment);

	public void setHostPage(IWizardPage firstPage);

}