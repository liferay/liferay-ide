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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import com.liferay.ide.idea.core.LiferayProjectTypeService;
import com.liferay.ide.idea.ui.LiferayIdeaUI;
import com.liferay.ide.idea.util.BladeCLI;
import com.liferay.ide.idea.util.CoreUtil;

import java.io.File;

import javax.swing.Icon;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class LiferayModuleBuilder extends ModuleBuilder {

	@Override
	public String getBuilderId() {
		Class<?> clazz = getClass();

		return clazz.getName();
	}

	public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
		return new LiferayModuleWizardStep(this);
	}

	@Override
	public String getDescription() {
		return _LIFERAY_MODULES;
	}

	@SuppressWarnings("rawtypes")
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

	public String getServiceName() {
		return _serviceName;
	}

	public String getType() {
		return _type;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setPackageName(String packageName) {
		_packageName = packageName;
	}

	public void setServiceName(String serviceName) {
		_serviceName = serviceName;
	}

	public void setType(String type) {
		_type = type;
	}

	@Override
	public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
		Project project = rootModel.getProject();

		ProjectType liferayProjectType = LiferayProjectTypeService.getProjectType(project);

		VirtualFile moduleDir = _createAndGetContentEntry();

		VirtualFile moduleParentDir = moduleDir.getParent();

		StringBuilder sb = new StringBuilder();

		sb.append("create ");
		sb.append("-d \"");
		sb.append(moduleParentDir.getPath());
		sb.append("\" ");

		String typeId = liferayProjectType.getId();

		if ((liferayProjectType != null) && typeId.equals(LiferayProjectType.LIFERAY_MAVEN_WORKSPACE)) {
			sb.append("-b ");
			sb.append("maven ");
		}

		sb.append("-t ");
		sb.append(_type);
		sb.append(" ");

		if (!CoreUtil.isNullOrEmpty(_className)) {
			sb.append("-c ");
			sb.append(_className);
			sb.append(" ");
		}

		if (!CoreUtil.isNullOrEmpty(_packageName)) {
			sb.append("-p ");
			sb.append(_packageName);
			sb.append(" ");
		}

		if ((_type.equals("service") || _type.equals("service-wrapper")) && !CoreUtil.isNullOrEmpty(_serviceName)) {
			sb.append("-s ");
			sb.append(_serviceName);
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

	private VirtualFile _createAndGetContentEntry() {
		String path = FileUtil.toSystemIndependentName(getContentEntryPath());

		new File(path).mkdirs();

		LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

		return localFileSystem.refreshAndFindFileByPath(path);
	}

	private static final String _LIFERAY_MODULES = "Liferay Modules";

	private String _className;
	private String _packageName;
	private String _serviceName;
	private String _type;

}