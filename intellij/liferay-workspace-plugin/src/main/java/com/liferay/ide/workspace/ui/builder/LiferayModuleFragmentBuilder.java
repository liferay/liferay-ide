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

package com.liferay.ide.workspace.ui.builder;

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
import com.liferay.ide.workspace.ui.UI;
import com.liferay.ide.workspace.ui.util.BladeCLI;
import com.liferay.ide.workspace.ui.wizard.LiferayModuleFragmentWizardStep;

import javax.swing.*;
import java.io.File;
import java.nio.file.FileSystems;

/**
 * @author Terry Jia
 */
public class LiferayModuleFragmentBuilder extends ModuleBuilder {

    private String osgiHost;
    private String[] files;
    private String bsnName;
    private String version;
    private File temp = new File(new File(System.getProperties().getProperty("user.home"), ".liferay-ide"), "bundles");

    public void setOsgiHost(String osgiHost) {
        this.osgiHost = osgiHost;
    }

    public void setBsnName(String bsnName) {
        this.bsnName = bsnName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setFiles(String[] files) {
        this.files = files;
    }

    public String getOsgiHost() {
        return osgiHost;
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

        final VirtualFile root = createAndGetContentEntry(project);

        StringBuilder sb = new StringBuilder();

        sb.append("create ");
        sb.append("-d \"" + root.getParent().getPath() + "\" ");
        sb.append("-t " + "fragment" + " ");

        if (!bsnName.equals("")) {
            sb.append("-h " + bsnName + " ");
        }

        if (!version.equals("")) {
            sb.append("-H " + version + " ");
        }

        sb.append("\"" + root.getName() + "\" ");

        BladeCLI.execute(sb.toString());

        for (String file : files) {
            final File tempBundle = new File(temp, osgiHost.substring(0, osgiHost.lastIndexOf(".jar")));

            File fragmentFile = new File(tempBundle, file);

            if (fragmentFile.exists()) {
                File folder = null;

                if (fragmentFile.getName().equals("portlet.properties")) {
                    folder = FileSystems.getDefault().getPath(root.getPath(), "src", "main", "java").toFile();

                    com.liferay.ide.workspace.ui.util.FileUtil.copyFileToDir(fragmentFile, "portlet-ext.properties", folder);
                } else if (fragmentFile.getName().contains("default.xml")) {
                    folder = FileSystems.getDefault().getPath(root.getPath(), "src", "main", "resources", "resource-actions").toFile();

                    folder.mkdirs();

                    com.liferay.ide.workspace.ui.util.FileUtil.copyFileToDir(fragmentFile, "default-ext.xml", folder);

                    try {
                        File ext = FileSystems.getDefault().getPath(root.getPath(), "src", "main", "resources", "portlet-ext.properties").toFile();

                        ext.createNewFile();

                        String extFileContent =
                                "resource.actions.configs=resource-actions/default.xml,resource-actions/default-ext.xml";

                        com.liferay.ide.workspace.ui.util.FileUtil.writeFile(ext, extFileContent, null);
                    } catch (Exception e) {
                    }
                } else {
                    String parent = fragmentFile.getParentFile().getPath();
                    parent = parent.replaceAll("\\\\", "/");
                    String metaInfResources = "META-INF/resources";

                    parent = parent.substring(parent.indexOf(metaInfResources) + metaInfResources.length());

                    folder = FileSystems.getDefault().getPath(root.getPath(), "src", "main", "resources", "META-INF", "resources").toFile();

                    folder.mkdirs();

                    if (!parent.equals("resources") && !parent.equals("")) {
                        folder = new File(folder, parent);
                        folder.mkdirs();
                    }

                    com.liferay.ide.workspace.ui.util.FileUtil.copyFileToDir(fragmentFile, folder);
                }
            }
        }

        rootModel.addContentEntry(root);

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
        return UI.LIFERAY_FRAGMENT_MODULES;
    }

    @Override
    public String getDescription() {
        return UI.LIFERAY_FRAGMENT_MODULES;
    }

    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new LiferayModuleFragmentWizardStep(context, this);
    }

    @Override
    public Icon getNodeIcon() {
        return IconLoader.getIcon("/icons/liferay.png");
    }

}