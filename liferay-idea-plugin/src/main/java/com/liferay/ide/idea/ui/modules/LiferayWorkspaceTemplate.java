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

package com.liferay.ide.idea.ui.modules;

import com.intellij.platform.templates.BuilderBasedTemplate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Joye Luo
 */
public class LiferayWorkspaceTemplate extends BuilderBasedTemplate {

	public LiferayWorkspaceTemplate(String name, String description, LiferayWorkspaceBuilder builder) {
		super(builder);

		_name = name;
		_description = description;
	}

	@Nullable
	@Override
	public String getDescription() {
		return _description;
	}

	@NotNull
	@Override
	public String getName() {
		return _name;
	}

	private final String _description;
	private final String _name;

}