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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
public interface ProductVersionElement extends Element {

	public ElementType TYPE = new ElementType(ProductVersionElement.class);

	public Value<String> getProductVersion();

	public Value<Boolean> getShowAllProductVersions();

	public void setProductVersion(String name);

	public void setShowAllProductVersions(Boolean value);

	@Label(standard = "Product Version")
	@Required
	@Service(impl = ProductVersionPossibleValuesService.class)
	@Service(impl = ProductVersionDefaultValueService.class)
	public ValueProperty PROP_PRODUCT_VERSION = new ValueProperty(TYPE, "ProductVersion");

	@DefaultValue(text = "false")
	@Label(standard = "Show All Product Versions")
	@Type(base = Boolean.class)
	public ValueProperty PROP_SHOW_ALL_PRODUCT_VERSIONS = new ValueProperty(TYPE, "ShowAllProductVersions");

}