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

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import com.liferay.ide.idea.ui.LiferayIdeaUI;
import com.liferay.ide.idea.util.BladeCLI;
import com.liferay.ide.idea.util.CoreUtil;

import java.io.File;

import javax.swing.Icon;

/**
 * @author Terry Jia
 */
public class LiferayModuleBuilder extends ModuleBuilder {

	@Override
	public String getBuilderId() {
		return getClass().getName();
	}

	public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
		return new LiferayModuleWizardStep(this);
	}

	@Override
	public String getDescription() {
		return _LIFERAY_MODULES;
	}

	public ModuleType getModuleType() {
		return StdModuleTypes.JAVA;
	}

	@Override
	public Icon getNodeIcon() {
		return LiferayIdeaUI.LIFERAY_ICON;
	}

	@Override
	public String getPresentableName() {
		return _LIFERAY_MODULES;
	}

	public String getType() {
		return this.type;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
		final VirtualFile moduleDir = createAndGetContentEntry();

		StringBuilder sb = new StringBuilder();

		sb.append("create ");
		sb.append("-d \"");
		sb.append(moduleDir.getParent().getPath());
		sb.append("\" ");
		sb.append("-t ");
		sb.append(type);
		sb.append(" ");

		if (!CoreUtil.isNullOrEmpty(className)) {
			sb.append("-c ");
			sb.append(className);
			sb.append(" ");
		}

		if (!CoreUtil.isNullOrEmpty(packageName)) {
			sb.append("-p ");
			sb.append(packageName);
			sb.append(" ");
		}

		sb.append("\"");
		sb.append(moduleDir.getName());
		sb.append("\" ");

		BladeCLI.execute(sb.toString());

		rootModel.addContentEntry(moduleDir);

		if (myJdk != null) {
			rootModel.setSdk(myJdk);
		}
		else {
			rootModel.inheritSdk();
		}
	}

	private VirtualFile createAndGetContentEntry() {
		final String path = FileUtil.toSystemIndependentName(getContentEntryPath());

		new File(path).mkdirs();

		return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
	}

	private static final String _LIFERAY_MODULES = "Liferay Modules";

	private String className;
	private String packageName;
	private String type;

}