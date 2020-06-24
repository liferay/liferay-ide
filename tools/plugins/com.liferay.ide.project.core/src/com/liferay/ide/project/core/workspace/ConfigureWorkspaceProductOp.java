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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 */
public interface ConfigureWorkspaceProductOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(ConfigureWorkspaceProductOp.class);

	@DelegateImplementation(ConfigureWorkspaceProductOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getProductVersion();

	public Value<Boolean> getShowAllVersionProduct();

	public void setProductVersion(String name);

	public void setShowAllVersionProduct(Boolean value);

	@Label(standard = "Product Version")
	@Required
	@Service(impl = ProductVersionPossibleValuesService.class)
	@Service(impl = ProductVersionDefaultValueService.class)
	public ValueProperty PROP_PRODUCT_VERSION = new ValueProperty(TYPE, "ProductVersion");

	@DefaultValue(text = "false")
	@Label(standard = "Show All Version Product")
	@Type(base = Boolean.class)
	public ValueProperty PROP_SHOW_ALL_VERSION_PRODUCT = new ValueProperty(TYPE, "ShowAllVersionProduct");

}