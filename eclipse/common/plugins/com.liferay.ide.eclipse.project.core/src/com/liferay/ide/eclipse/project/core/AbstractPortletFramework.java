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

package com.liferay.ide.eclipse.project.core;

import java.net.URL;

/**
 * @author Greg Amerson
 */
public abstract class AbstractPortletFramework implements IPortletFramework {

	protected String bundleId;

	protected String description;

	protected String displayName;

	protected URL helpUrl;

	protected String id;

	protected boolean isDefault;

	protected String shortName;

	protected String templateZipPath;

	public String getBundleId() {
		return bundleId;
	}

	public String getDescription() {
		return description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public URL getHelpUrl() {
		return helpUrl;
	}

	public String getId() {
		return id;
	}

	public String getShortName() {
		return shortName;
	}

	public String getTemplateZipPath() {
		return templateZipPath;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public void setHelpUrl(URL helpUrl) {
		this.helpUrl = helpUrl;
	}



	public void setId(String id) {
		this.id = id;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public void setTemplateZipPath(String templateZipPath) {
		this.templateZipPath = templateZipPath;
	}

}
