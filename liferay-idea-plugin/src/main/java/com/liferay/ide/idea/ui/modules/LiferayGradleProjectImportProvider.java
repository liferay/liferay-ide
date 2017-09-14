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

import com.intellij.openapi.externalSystem.service.project.wizard.AbstractExternalProjectImportProvider;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.util.GradleConstants;

/**
 * @author Andy Wu
 */
public class LiferayGradleProjectImportProvider extends AbstractExternalProjectImportProvider {

	public LiferayGradleProjectImportProvider(LiferayGradleProjectImportBuilder builder) {
		super(builder, GradleConstants.SYSTEM_ID);
	}

	@Nullable
	@Override
	public String getFileSample() {
		return "<b>Gradle</b> build script (*.gradle)";
	}

	@Override
	protected boolean canImportFromFile(VirtualFile file) {
		return GradleConstants.EXTENSION.equals(file.getExtension());
	}

}