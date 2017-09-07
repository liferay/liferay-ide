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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.liferay.ide.idea.ui.LiferayIdeaUI;
import com.liferay.ide.idea.ui.wizard.LiferayModuleFragmentWizardStep;
import com.liferay.ide.idea.util.BladeCLI;

import javax.swing.*;
import java.io.File;
import java.nio.file.FileSystems;

/**
 * @author Terry Jia
 */
public class LiferayModuleFragmentBuilder extends ModuleBuilder {

    private String _fragmentHost;
    private String[] _overrideFiles;
    private String _bsn;
    private String _version;
    private static final File _USER_BUNDLES_DIR = new File(new File(System.getProperty("user.home"), ".liferay-ide"), "bundles");

    public void setFragmentHost(String fragmentHost) {
        _fragmentHost = fragmentHost;
    }

    public void setBsnName(String bsn) {
        _bsn = bsn;
    }

    public void setVersion(String version) {
        _version = version;
    }

    public void setOverrideFiles(String[] overrideFiles) {
        _overrideFiles = overrideFiles;
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    private VirtualFile createAndGetContentEntry(Project project) {
        String path = FileUtil.toSystemIndependentName(getContentEntryPath());

        File file = new File(path);

        file.mkdirs();

        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) throws ConfigurationException {
        final Project project = rootModel.getProject();

        final VirtualFile projectRoot = createAndGetContentEntry(project);

        StringBuilder sb = new StringBuilder();

        sb.append("create ");
        sb.append("-d \"" + projectRoot.getParent().getPath() + "\" ");
        sb.append("-t " + "fragment" + " ");

        if (!_bsn.equals("")) {
            sb.append("-h " + _bsn + " ");
        }

        if (!_version.equals("")) {
            sb.append("-H " + _version + " ");
        }

        sb.append("\"" + projectRoot.getName() + "\" ");

        BladeCLI.execute(sb.toString());

        for (String file : _overrideFiles) {
            final File tempBundle = new File(_USER_BUNDLES_DIR, _fragmentHost.substring(0, _fragmentHost.lastIndexOf(".jar")));

            File fragmentFile = new File(tempBundle, file);

            if (fragmentFile.exists()) {
                File folder = null;

                if (fragmentFile.getName().equals("portlet.properties")) {
                    folder = FileSystems.getDefault().getPath(projectRoot.getPath(), "src", "main", "java").toFile();

                    com.liferay.ide.idea.util.FileUtil.copyFileToDir(fragmentFile, "portlet-ext.properties", folder);
                } else if (fragmentFile.getName().contains("default.xml")) {
                    folder = FileSystems.getDefault().getPath(projectRoot.getPath(), "src", "main", "resources", "resource-actions").toFile();

                    folder.mkdirs();

                    com.liferay.ide.idea.util.FileUtil.copyFileToDir(fragmentFile, "default-ext.xml", folder);

                    try {
                        File ext = FileSystems.getDefault().getPath(projectRoot.getPath(), "src", "main", "resources", "portlet-ext.properties").toFile();

                        ext.createNewFile();

                        String extFileContent =
                                "resource.actions.configs=resource-actions/default.xml,resource-actions/default-ext.xml";

                        com.liferay.ide.idea.util.FileUtil.writeFile(ext, extFileContent, null);
                    } catch (Exception e) {
                    }
                } else {
                    String parent = fragmentFile.getParentFile().getPath();
                    parent = parent.replaceAll("\\\\", "/");
                    String metaInfResources = "META-INF/resources";

                    parent = parent.substring(parent.indexOf(metaInfResources) + metaInfResources.length());

                    folder = FileSystems.getDefault().getPath(projectRoot.getPath(), "src", "main", "resources", "META-INF", "resources").toFile();

                    folder.mkdirs();

                    if (!parent.equals("resources") && !parent.equals("")) {
                        folder = new File(folder, parent);
                        folder.mkdirs();
                    }

                    com.liferay.ide.idea.util.FileUtil.copyFileToDir(fragmentFile, folder);
                }
            }
        }

        rootModel.addContentEntry(projectRoot);

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
        return _LIFERAY_FRAGMENT_MODULES;
    }

    @Override
    public String getDescription() {
        return _LIFERAY_FRAGMENT_MODULES;
    }

    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new LiferayModuleFragmentWizardStep(context, this);
    }

    @Override
    public Icon getNodeIcon() {
        return LiferayIdeaUI.LIFERAY_ICON;
    }
    
    private final static String _LIFERAY_FRAGMENT_MODULES = "Liferay Fragment Modules";

}