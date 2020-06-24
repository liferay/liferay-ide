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
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;

/**
 * @author Simon Jiang
 */
public interface ConfigureWorkspaceProductOp extends ExecutableElement, ProductVersionElement {

	public ElementType TYPE = new ElementType(ConfigureWorkspaceProductOp.class);

	@DelegateImplementation(ConfigureWorkspaceProductOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public ValueProperty PROP_PRODUCT_VERSION = new ValueProperty(ProductVersionElement.TYPE, "ProductVersion");

}