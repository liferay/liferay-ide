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

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public abstract class AbstractPortletFramework implements IPortletFramework {

	public String getBundleId() {
		return _bundleId;
	}

	public String getDescription() {
		return _description;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public IProjectFacet[] getFacets() {
		return new IProjectFacet[0];
	}

	public URL getHelpUrl() {
		return _helpUrl;
	}

	public String getId() {
		return _id;
	}

	public String getRequiredSDKVersion() {
		return _requiredSDKVersion;
	}

	public String getShortName() {
		return _shortName;
	}

	public boolean isAdvanced() {
		return _advanced;
	}

	public boolean isDefault() {
		return _isDefault;
	}

	public boolean isRequiresAdvanced() {
		return _requiresAdvanced;
	}

	public IStatus postProjectCreated(
		IProject project, String frameworkName, String portletName, IProgressMonitor monitor) {

		// by default do nothing;

		return Status.OK_STATUS;
	}

	public void setAdvanced(boolean adv) {
		_advanced = adv;
	}

	public void setBundleId(String bundleId) {
		_bundleId = bundleId;
	}

	public void setDefault(boolean isDefault) {
		_isDefault = isDefault;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setDisplayName(String displayName) {
		_displayName = displayName;
	}

	public void setHelpUrl(URL helpUrl) {
		_helpUrl = helpUrl;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setRequiredSDKVersion(String version) {
		_requiredSDKVersion = version;
	}

	public void setRequiresAdvanced(boolean adv) {
		_requiresAdvanced = adv;
	}

	public void setShortName(String shortName) {
		_shortName = shortName;
	}

	@Override
	public String toString() {
		return getShortName();
	}

	private boolean _advanced;
	private String _bundleId;
	private String _description;
	private String _displayName;
	private URL _helpUrl;
	private String _id;
	private boolean _isDefault;
	private String _requiredSDKVersion;
	private boolean _requiresAdvanced;
	private String _shortName;

}