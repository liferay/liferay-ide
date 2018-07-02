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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.ILiferayProjectProvider;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
public interface BaseLiferayWorkspaceOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(BaseLiferayWorkspaceOp.class);

	public Value<String> getBundleUrl();

	public Value<NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp>> getProjectProvider();

	public Value<Boolean> getProvisionLiferayBundle();

	public Value<String> getServerName();

	public void setBundleUrl(String value);

	public void setProjectProvider(NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> value);

	public void setProjectProvider(String value);

	public void setProvisionLiferayBundle(Boolean value);

	public void setProvisionLiferayBundle(String value);

	public void setServerName(String value);

	public final String LIFERAY_70_BUNDLE_URL =
		"https://releases-cdn.liferay.com/portal/7.0.6-ga7/liferay-ce-portal-tomcat-7.0-ga7-20180507111753223.zip";

	public final String LIFERAY_71_BUNDLE_URL =
		"https://releases-cdn.liferay.com/portal/7.1.0-b3/liferay-ce-portal-tomcat-7.1-b3-20180611140920623.zip";

	// BundleUrl

	@DefaultValue(text = LIFERAY_70_BUNDLE_URL)
	@Service(impl = BundleUrlValidationService.class)
	public ValueProperty PROP_BUNDLE_URL = new ValueProperty(TYPE, "bundleUrl");

	// ProjectProvider

	@Label(standard = "build type")
	@Service(impl = WorkspaceProjectProviderDefaultValueService.class)
	@Service(impl = WorkspaceProjectProviderPossibleValuesService.class)
	@Type(base = ILiferayProjectProvider.class)
	public ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty(TYPE, "ProjectProvider");

	// ProvisionLiferayBundle

	@DefaultValue(text = "false")
	@Label(standard = "Download Liferay bundle")
	@Type(base = Boolean.class)
	public ValueProperty PROP_PROVISION_LIFERAY_BUNDLE = new ValueProperty(TYPE, "provisionLiferayBundle");

	// ServerName

	@Service(impl = ServerNameValidationService.class)
	public ValueProperty PROP_SERVER_NAME = new ValueProperty(TYPE, "serverName");

}