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

package com.liferay.ide.workspace.ui.wizard;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.ide.util.projectWizard.AbstractModuleBuilder;
import com.intellij.ide.util.projectWizard.ProjectWizardUtil;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.roots.ui.configuration.ProjectStructureConfigurable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.io.File;

/**
 * @author Terry Jia
 */
public class LiferayModuleNameLocationComponent {
    private final WizardContext wizardContext;
    private JTextField moduleName;
    private TextFieldWithBrowseButton moduleContentRoot;
    private TextFieldWithBrowseButton moduleFileLocation;
    private JPanel modulePanel;

    private boolean moduleNameChangedByUser = false;
    private boolean moduleNameDocListenerEnabled = true;

    private boolean contentRootChangedByUser = false;
    private boolean contentRootDocListenerEnabled = true;

    private boolean myImlLocationChangedByUser = false;
    private boolean myImlLocationDocListenerEnabled = true;

    private boolean myUpdatePathsWhenNameIsChanged;

    public LiferayModuleNameLocationComponent(@NotNull WizardContext wizardContext) {
        this.wizardContext = wizardContext;
    }

    @Nullable
    public AbstractModuleBuilder getModuleBuilder() {
        return ((AbstractModuleBuilder) wizardContext.getProjectBuilder());
    }

    public boolean validate() throws ConfigurationException {
        AbstractModuleBuilder builder = getModuleBuilder();

        if (builder != null && !builder.validateModuleName(getModuleName())) {
            return false;
        }

        if (!validateModulePaths()) {
            return false;
        }

        validateExistingModuleName();

        return true;
    }

    public void updateDataModel() {
        final AbstractModuleBuilder moduleBuilder = getModuleBuilder();

        if (moduleBuilder == null) {
            return;
        }

        final String moduleName = getModuleName();

        moduleBuilder.setName(moduleName);
        moduleBuilder.setModuleFilePath(
                FileUtil.toSystemIndependentName(moduleFileLocation.getText()) + "/" + moduleName + ModuleFileType.DOT_DEFAULT_EXTENSION);
        moduleBuilder.setContentEntryPath(FileUtil.toSystemIndependentName(getModuleContentRoot()));
    }

    public JPanel getModulePanel() {
        return modulePanel;
    }

    public void bindModuleSettings(final LiferayNamePathComponent namePathComponent) {
        namePathComponent.getNameComponent().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (!moduleNameChangedByUser) {
                    setModuleName(namePathComponent.getNameValue());
                }
            }
        });

        moduleContentRoot.addBrowseFolderListener(ProjectBundle.message("project.new.wizard.module.content.root.chooser.title"),
                ProjectBundle.message("project.new.wizard.module.content.root.chooser.description"),
                wizardContext.getProject(), BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR);

        namePathComponent.getPathComponent().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (!contentRootChangedByUser) {
                    setModuleContentRoot(namePathComponent.getPath());
                }
            }
        });
        moduleName.getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (!myUpdatePathsWhenNameIsChanged) {
                    return;
                }

                if (moduleNameDocListenerEnabled) {
                    moduleNameChangedByUser = true;
                }
                String path = getDefaultBaseDir(wizardContext, namePathComponent);
                final String moduleName = getModuleName();
                if (path.length() > 0 && !Comparing.strEqual(moduleName, namePathComponent.getNameValue())) {
                    path += "/modules/" + moduleName;
                }
                if (!contentRootChangedByUser) {
                    final boolean f = moduleNameChangedByUser;
                    moduleNameChangedByUser = true;
                    setModuleContentRoot(path);
                    moduleNameChangedByUser = f;
                }
                if (!myImlLocationChangedByUser) {
                    setImlFileLocation(path);
                }
            }
        });
        moduleContentRoot.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (contentRootDocListenerEnabled) {
                    contentRootChangedByUser = true;
                }
                if (!myImlLocationChangedByUser) {
                    setImlFileLocation(getModuleContentRoot());
                }
                if (!moduleNameChangedByUser) {
                    final String path = FileUtil.toSystemIndependentName(getModuleContentRoot());
                    final int idx = path.lastIndexOf("/");

                    boolean f = contentRootChangedByUser;
                    contentRootChangedByUser = true;

                    boolean i = myImlLocationChangedByUser;
                    myImlLocationChangedByUser = true;

                    setModuleName(idx >= 0 ? path.substring(idx + 1) : "");

                    contentRootChangedByUser = f;
                    myImlLocationChangedByUser = i;
                }
            }
        });

        moduleFileLocation.addBrowseFolderListener(ProjectBundle.message("project.new.wizard.module.file.chooser.title"),
                ProjectBundle.message("project.new.wizard.module.file.description"),
                wizardContext.getProject(), BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR);
        moduleFileLocation.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (myImlLocationDocListenerEnabled) {
                    myImlLocationChangedByUser = true;
                }
            }
        });
        namePathComponent.getPathComponent().getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                if (!myImlLocationChangedByUser) {
                    setImlFileLocation(namePathComponent.getPath());
                }
            }
        });

        myUpdatePathsWhenNameIsChanged = true;

        if (wizardContext.isCreatingNewProject()) {
            setModuleName(namePathComponent.getNameValue());
            setModuleContentRoot(namePathComponent.getPath());
            setImlFileLocation(namePathComponent.getPath());
        } else {
            final Project project = wizardContext.getProject();
            assert project != null;
            final VirtualFile baseDir = project.getBaseDir();

            if (baseDir != null) { //e.g. was deleted
                final String baseDirPath = baseDir.getPath();
                String moduleName = ProjectWizardUtil.findNonExistingFileName(baseDirPath, "untitled", "");
                String contentRoot = baseDirPath + "/" + moduleName;
                if (!Comparing.strEqual(project.getName(), wizardContext.getProjectName()) &&
                        !wizardContext.isCreatingNewProject() &&
                        wizardContext.getProjectName() != null) {
                    moduleName =
                            ProjectWizardUtil.findNonExistingFileName(wizardContext.getProjectFileDirectory(), wizardContext.getProjectName(), "");
                    contentRoot = wizardContext.getProjectFileDirectory();
                    myUpdatePathsWhenNameIsChanged = !wizardContext.isProjectFileDirectorySetExplicitly();
                }
                setModuleName(moduleName);
                setModuleContentRoot(contentRoot);
                setImlFileLocation(contentRoot);

                this.moduleName.select(0, moduleName.length());
            }
        }
    }

    private void validateExistingModuleName() throws ConfigurationException {
        final Project project = wizardContext.getProject();

        if (project == null) {
            return;
        }

        final String moduleName = getModuleName();

        final Module module;

        final ProjectStructureConfigurable fromConfigurable = ProjectStructureConfigurable.getInstance(project);

        if (fromConfigurable != null) {
            module = fromConfigurable.getModulesConfig().getModule(moduleName);
        } else {
            module = ModuleManager.getInstance(project).findModuleByName(moduleName);
        }
        if (module != null) {
            throw new ConfigurationException("Module \'" + moduleName + "\' already exist in project. Please, specify another name.");
        }
    }

    private boolean validateModulePaths() throws ConfigurationException {
        final String moduleName = getModuleName();
        final String moduleFileDirectory = moduleFileLocation.getText();

        if (moduleFileDirectory.length() == 0) {
            throw new ConfigurationException("Enter module file location");
        }

        if (moduleName.length() == 0) {
            throw new ConfigurationException("Enter a module name");
        }

        if (!ProjectWizardUtil.createDirectoryIfNotExists(IdeBundle.message("directory.module.file"), moduleFileDirectory,
                myImlLocationChangedByUser)) {
            return false;
        }

        if (!ProjectWizardUtil.createDirectoryIfNotExists(IdeBundle.message("directory.module.content.root"), moduleContentRoot.getText(),
                contentRootChangedByUser)) {
            return false;
        }

        final File moduleFile = new File(moduleFileDirectory, moduleName + ModuleFileType.DOT_DEFAULT_EXTENSION);

        if (moduleFile.exists()) {
            int answer = Messages.showYesNoDialog(IdeBundle.message("prompt.overwrite.project.file", moduleFile.getAbsolutePath(),
                    IdeBundle.message("project.new.wizard.module.identification")),
                    IdeBundle.message("title.file.already.exists"), Messages.getQuestionIcon());
            if (answer != Messages.YES) {
                return false;
            }
        }

        return true;
    }

    private String getModuleContentRoot() {
        return moduleContentRoot.getText();
    }

    private static String getDefaultBaseDir(WizardContext wizardContext, LiferayNamePathComponent namePathComponent) {
        if (wizardContext.isCreatingNewProject()) {
            return namePathComponent.getPath();
        } else {
            final Project project = wizardContext.getProject();

            assert project != null;

            final VirtualFile baseDir = project.getBaseDir();

            if (baseDir != null) {
                return baseDir.getPath();
            }
            return "";
        }
    }

    private void setImlFileLocation(final String path) {
        myImlLocationDocListenerEnabled = false;
        moduleFileLocation.setText(FileUtil.toSystemDependentName(path));
        myImlLocationDocListenerEnabled = true;
    }

    private void setModuleContentRoot(final String path) {
        contentRootDocListenerEnabled = false;
        moduleContentRoot.setText(FileUtil.toSystemDependentName(path));
        contentRootDocListenerEnabled = true;
    }

    public void setModuleName(String moduleName) {
        moduleNameDocListenerEnabled = false;
        this.moduleName.setText(moduleName);
        moduleNameDocListenerEnabled = true;
    }

    public JTextField getModuleNameField() {
        return moduleName;
    }

    private String getModuleName() {
        return moduleName.getText().trim();
    }

}
