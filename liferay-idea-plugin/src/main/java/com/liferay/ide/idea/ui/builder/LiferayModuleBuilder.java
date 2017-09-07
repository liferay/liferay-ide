/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.idea.ui.builder;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.liferay.ide.idea.ui.LiferayIdeaUI;
import com.liferay.ide.idea.ui.wizard.LiferayModuleWizardStep;
import com.liferay.ide.idea.util.BladeCLI;
import com.liferay.ide.idea.util.CoreUtil;

import javax.swing.*;
import java.io.File;

/**
 * @author Terry Jia
 */
public class LiferayModuleBuilder extends ModuleBuilder {

    private String type;
    private String className;
    private String packageName;

    public void setType(String type) {
        this.type = type;
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

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    private VirtualFile createAndGetContentEntry() {
        final String path = FileUtil.toSystemIndependentName(getContentEntryPath());

        new File(path).mkdirs();

        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        final VirtualFile moduleDir = createAndGetContentEntry();

        StringBuilder sb = new StringBuilder();

        sb.append("create ");
        sb.append("-d \"" + moduleDir.getParent().getPath() + "\" ");
        sb.append("-t " + type + " ");

        if (!CoreUtil.isNullOrEmpty(className)) {
            sb.append("-c " + className + " ");
        }

        if (!CoreUtil.isNullOrEmpty(packageName)) {
            sb.append("-p " + packageName + " ");
        }

        sb.append("\"" + moduleDir.getName() + "\" ");

        BladeCLI.execute(sb.toString());

        rootModel.addContentEntry(moduleDir);

        if (myJdk != null) {
            rootModel.setSdk(myJdk);
        } else {
            rootModel.inheritSdk();
        }

    }

    public ModuleType getModuleType() {
        return StdModuleTypes.JAVA;
    }

    @Override
    public String getPresentableName() {
        return _LIFERAY_MODULES;
    }

    @Override
    public String getDescription() {
        return _LIFERAY_MODULES;
    }

    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new LiferayModuleWizardStep(this);
    }

    @Override
    public Icon getNodeIcon() {
        return LiferayIdeaUI.LIFERAY_ICON;
    }

    private final static String _LIFERAY_MODULES = "Liferay Modules";
}