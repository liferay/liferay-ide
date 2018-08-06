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

package com.liferay.ide.maven.core.model;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.model.HasLiferayRuntime;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Validation;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;

/**
 * @author Gregory Amerson
 */
public interface NewLiferayProfileOp extends NewLiferayPluginProjectOp, HasLiferayRuntime {

	public ElementType TYPE = new ElementType(NewLiferayProfileOp.class);

	// we don't want to validated for missing project names

	@Validation(message = "", rule = "true")
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, "ProjectName");

	@DefaultValue(text = "maven")
	@Type(base = ILiferayProjectProvider.class)
	public ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty(TYPE, "ProjectProvider");

}