/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.ui;

import com.liferay.ide.project.ui.wizard.IPluginWizardFragment;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
public interface IPortletFrameworkDelegate
{

    String EXTENSION_ID = "com.liferay.ide.project.ui.portletFrameworkDelegates";

    String FRAMEWORK_ID = "frameworkId";

    String ICON = "icon";

    Composite createNewProjectOptionsComposite( Composite parent, IDataModel iDataModel );

    String getBundleId();

    String getFrameworkId();

    String getIconUrl();

    IPluginWizardFragment getWizardFragment();

    boolean isFragmentEnabled();

}
