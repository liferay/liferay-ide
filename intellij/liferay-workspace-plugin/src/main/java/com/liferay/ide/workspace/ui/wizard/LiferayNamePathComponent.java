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
import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.ide.util.projectWizard.ProjectWizardUtil;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.FieldPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class LiferayNamePathComponent extends JPanel {
    private static final Logger LOG = Logger.getInstance("#com.liferay.ide.workspace.wizard.LiferayNamePathComponent");

    private JTextField name;
    private JTextField path;
    private boolean isNameChangedByUser = false;
    private boolean isPathChangedByUser = false;
    private boolean isPathNameSyncEnabled = true;
    private boolean isNamePathSyncEnabled = true;
    private boolean isSyncEnabled = true;
    private FieldPanel pathPanel;
    private JLabel nameLabel;
    private JLabel pathLabel;
    private boolean shouldBeAbsolute;

    public LiferayNamePathComponent(String nameLabelText,
                                    String pathLabelText,
                                    final String pathChooserTitle,
                                    final String pathChooserDescription,
                                    boolean hideIgnored,
                                    boolean bold) {
        super(new GridBagLayout());

        name = new JTextField();
        name.setDocument(new NameFieldDocument());
        name.setPreferredSize(new Dimension(200, name.getPreferredSize().height));

        path = new JTextField();
        path.setDocument(new PathFieldDocument());
        path.setPreferredSize(new Dimension(200, path.getPreferredSize().height));

        nameLabel = new JLabel(nameLabelText);

        if (bold) {
            nameLabel.setFont(UIUtil.getLabelFont().deriveFont(Font.BOLD));
        }

        nameLabel.setLabelFor(name);

        Insets insets = JBUI.insets(0, 0, 5, 4);

        this.add(nameLabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                insets, 0, 0));

        insets = JBUI.insets(0, 0, 5, 0);

        this.add(name, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        final FileChooserDescriptor chooserDescriptor = (FileChooserDescriptor) BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR.clone();

        chooserDescriptor.setHideIgnored(hideIgnored);

        final BrowseFilesListener browseButtonActionListener = new BrowseFilesListener(path, pathChooserTitle, pathChooserDescription, chooserDescriptor) {
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                isPathChangedByUser = true;
            }
        };

        pathPanel = new FieldPanel(path, null, null, browseButtonActionListener, null);
        pathLabel = new JLabel(pathLabelText);
        pathLabel.setLabelFor(path);

        if (bold) {
            pathLabel.setFont(UIUtil.getLabelFont().deriveFont(Font.BOLD));
        }

        insets = JBUI.insets(0, 0, 5, 4);

        this.add(pathLabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                insets, 0, 0));

        insets = JBUI.insets(0, 0, 5, 0);

        this.add(pathPanel, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));
    }

    public static LiferayNamePathComponent initNamePathComponent(WizardContext context) {
        final LiferayNamePathComponent component = new LiferayNamePathComponent(
                IdeBundle.message("label.project.name"),
                IdeBundle.message("label.project.files.location"),
                IdeBundle.message("title.select.project.file.directory", IdeBundle.message("project.new.wizard.project.identification")),
                IdeBundle.message("description.select.project.file.directory", StringUtil
                        .capitalize(IdeBundle.message("project.new.wizard.project.identification"))),
                true, false
        );

        final String baseDir = context.getProjectFileDirectory();
        final String projectName = context.getProjectName();
        final String initialProjectName = projectName != null ? projectName : ProjectWizardUtil.findNonExistingFileName(baseDir, "untitled", "");

        component.setPath(projectName == null ? (baseDir + File.separator + initialProjectName) : baseDir);
        component.setNameValue(initialProjectName);
        component.getNameComponent().select(0, initialProjectName.length());

        return component;
    }

    public boolean validateNameAndPath(WizardContext context, boolean defaultFormat) throws ConfigurationException {
        final String name = getNameValue();

        if (StringUtil.isEmptyOrSpaces(name)) {
            throw new ConfigurationException(IdeBundle.message("prompt.new.project.file.name", ApplicationInfo.getInstance().getVersionName(), context.getPresentationName()));
        }

        final String projectDirectory = getPath();

        if (StringUtil.isEmptyOrSpaces(projectDirectory)) {
            throw new ConfigurationException(IdeBundle.message("prompt.enter.project.file.location", context.getPresentationName()));
        }

        if (shouldBeAbsolute && !new File(projectDirectory).isAbsolute()) {
            throw new ConfigurationException(StringUtil.capitalize(IdeBundle.message("file.location.should.be.absolute", context.getPresentationName())));
        }

        String message = IdeBundle.message("directory.project.file.directory", context.getPresentationName());

        if (!ProjectWizardUtil.createDirectoryIfNotExists(message, projectDirectory, isPathChangedByUser())) {
            return false;
        }

        final File file = new File(projectDirectory);

        if (file.exists() && !file.canWrite()) {
            throw new ConfigurationException(String.format("Directory '%s' is not seem to be writable. Please consider another location.", projectDirectory));
        }

        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            if (ProjectUtil.isSameProject(projectDirectory, project)) {
                throw new ConfigurationException(String.format("Directory '%s' is already taken by the project '%s'. Please consider another location.", projectDirectory, project.getName()));
            }
        }

        boolean shouldContinue = true;
        final String fileName = defaultFormat ? name + ProjectFileType.DOT_DEFAULT_EXTENSION : Project.DIRECTORY_STORE_FOLDER;
        final File projectFile = new File(file, fileName);

        if (projectFile.exists()) {
            message = IdeBundle.message("prompt.overwrite.project.file", projectFile.getAbsolutePath(), context.getPresentationName());

            int answer = Messages.showYesNoDialog(message, IdeBundle.message("title.file.already.exists"), Messages.getQuestionIcon());

            shouldContinue = (answer == Messages.YES);
        }

        return shouldContinue;
    }

    public String getNameValue() {
        return name.getText().trim();
    }

    public void setNameValue(String name) {
        final boolean isNameChangedByUser = this.isNameChangedByUser;

        setNamePathSyncEnabled(false);

        try {
            this.name.setText(name);
        } finally {
            this.isNameChangedByUser = isNameChangedByUser;

            setNamePathSyncEnabled(true);
        }
    }

    public String getPath() {
        return FileUtil.expandUserHome(FileUtil.toSystemIndependentName(path.getText().trim()));
    }

    public void setPath(String path) {
        final boolean isPathChangedByUser = this.isPathChangedByUser;

        setPathNameSyncEnabled(false);

        try {
            this.path.setText(FileUtil.getLocationRelativeToUserHome(FileUtil.toSystemDependentName(path)));
        } finally {
            this.isPathChangedByUser = isPathChangedByUser;
            setPathNameSyncEnabled(true);
        }
    }

    public JTextField getNameComponent() {
        return name;
    }

    public JTextField getPathComponent() {
        return path;
    }

    public boolean isPathChangedByUser() {
        return isPathChangedByUser;
    }

    public boolean isSyncEnabled() {
        return isSyncEnabled;
    }

    private boolean isPathNameSyncEnabled() {
        if (!isSyncEnabled()) {
            return false;
        }
        return isPathNameSyncEnabled;
    }

    private void setPathNameSyncEnabled(boolean isPathNameSyncEnabled) {
        this.isPathNameSyncEnabled = isPathNameSyncEnabled;
    }

    private boolean isNamePathSyncEnabled() {
        return !isSyncEnabled() ? false : isNamePathSyncEnabled;
    }

    private void setNamePathSyncEnabled(boolean isNamePathSyncEnabled) {
        this.isNamePathSyncEnabled = isNamePathSyncEnabled;
    }

    public void setShouldBeAbsolute(boolean shouldBeAbsolute) {
        this.shouldBeAbsolute = shouldBeAbsolute;
    }

    private class NameFieldDocument extends PlainDocument {
        public NameFieldDocument() {
            addDocumentListener(new DocumentAdapter() {
                public void textChanged(DocumentEvent event) {
                    isNameChangedByUser = true;
                    syncNameAndPath();
                }
            });
        }

        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            boolean ok = true;
            for (int idx = 0; idx < str.length() && ok; idx++) {
                char ch = str.charAt(idx);
                ok = ch != File.separatorChar && ch != '\\' && ch != '/' && ch != '|' && ch != ':';
            }
            if (ok) {
                super.insertString(offs, str, a);
            }
        }

        private void syncNameAndPath() {
            if (isNamePathSyncEnabled() && !isPathChangedByUser) {
                try {
                    setPathNameSyncEnabled(false);
                    final String name = getText(0, getLength());
                    final String path = LiferayNamePathComponent.this.path.getText().trim();
                    final int lastSeparatorIndex = path.lastIndexOf(File.separator);
                    if (lastSeparatorIndex >= 0) {
                        setPath(path.substring(0, lastSeparatorIndex + 1) + name);
                    }
                } catch (BadLocationException e) {
                    LOG.error(e);
                } finally {
                    setPathNameSyncEnabled(true);
                }
            }
        }
    }

    private class PathFieldDocument extends PlainDocument {
        public PathFieldDocument() {
            addDocumentListener(new DocumentAdapter() {
                public void textChanged(DocumentEvent event) {
                    isPathChangedByUser = true;
                    syncPathAndName();
                }
            });
        }

        private void syncPathAndName() {
            if (isPathNameSyncEnabled() && !isNameChangedByUser) {
                try {
                    setNamePathSyncEnabled(false);
                    final String path = getText(0, getLength());
                    final int lastSeparatorIndex = path.lastIndexOf(File.separator);
                    if (lastSeparatorIndex >= 0 && (lastSeparatorIndex + 1) < path.length()) {
                        setNameValue(path.substring(lastSeparatorIndex + 1));
                    }
                } catch (BadLocationException e) {
                    LOG.error(e);
                } finally {
                    setNamePathSyncEnabled(true);
                }
            }
        }
    }

}
