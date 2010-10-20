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

package com.liferay.ide.eclipse.project.ui;

import com.liferay.ide.eclipse.project.ui.wizard.IPluginWizardFragment;

/**
 * @author Greg Amerson
 */
public abstract class AbstractPortletFrameworkDelegate implements IPortletFrameworkDelegate {

	protected String bundleId = null;

	protected boolean fragmentEnabled = false;

	protected String frameworkId = null;

	protected String iconUrl = null;

	public String getBundleId() {
		return bundleId;
	}

	public String getFrameworkId() {
		return frameworkId;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public IPluginWizardFragment getWizardFragment() {
		return null;
	}

	public boolean isFragmentEnabled() {
		return fragmentEnabled;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public void setFragmentEnabled(boolean fragmentEnabled) {
		this.fragmentEnabled = fragmentEnabled;
	}

	public void setFrameworkId(String id) {
		this.frameworkId = id;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

}
