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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author Terry Jia
 */
public class LiferayNamePathComponent extends JPanel {

	public static LiferayNamePathComponent initNamePathComponent(WizardContext context) {
		String nameLabel = IdeBundle.message("label.project.name");
		String filesLocationLabel = IdeBundle.message("label.project.files.location");
		String fileDirectoryTitle = IdeBundle.message(
			"title.select.project.file.directory", IdeBundle.message("project.new.wizard.project.identification"));
		String fileDirectoryDescription = IdeBundle.message(
			"description.select.project.file.directory",
			StringUtil.capitalize(IdeBundle.message("project.new.wizard.project.identification")));

		LiferayNamePathComponent component = new LiferayNamePathComponent(
			nameLabel, filesLocationLabel, fileDirectoryTitle, fileDirectoryDescription, true, false);

		String baseDir = context.getProjectFileDirectory();
		String projectName = context.getProjectName();
		String initialProjectName = ProjectWizardUtil.findNonExistingFileName(baseDir, "untitled", "");

		if (projectName != null) {
			initialProjectName = projectName;
		}

		component.setPath(projectName == null ? (baseDir + File.separator + initialProjectName) : baseDir);
		component.setNameValue(initialProjectName);

		JTextField jTextField = component.getNameComponent();

		jTextField.select(0, initialProjectName.length());

		return component;
	}

	public LiferayNamePathComponent(
		String nameLabelText, String pathLabelText, String pathChooserTitle, String pathChooserDescription,
		boolean hideIgnored, boolean bold) {

		super(new GridBagLayout());

		_name = new JTextField();

		_name.setDocument(new NameFieldDocument());

		Dimension namePreferredSize = _name.getPreferredSize();

		_name.setPreferredSize(new Dimension(200, namePreferredSize.height));

		_path = new JTextField();

		_path.setDocument(new PathFieldDocument());

		Dimension pathPreferredSize = _path.getPreferredSize();

		_path.setPreferredSize(new Dimension(200, pathPreferredSize.height));

		_nameLabel = new JLabel(nameLabelText);

		if (bold) {
			Font font = UIUtil.getLabelFont();

			_nameLabel.setFont(font.deriveFont(Font.BOLD));
		}

		_nameLabel.setLabelFor(_name);

		Insets insets = JBUI.insets(0, 0, 5, 4);

		GridBagConstraints gridBagConstraints = new GridBagConstraints(
			0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0,
			0);

		add(_nameLabel, gridBagConstraints);

		insets = JBUI.insets(0, 0, 5, 0);

		gridBagConstraints = new GridBagConstraints(
			1, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
			insets, 0, 0);

		add(_name, gridBagConstraints);

		FileChooserDescriptor chooserDescriptor =
			(FileChooserDescriptor)BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR.clone();

		chooserDescriptor.setHideIgnored(hideIgnored);

		BrowseFilesListener browseButtonActionListener =
			new BrowseFilesListener(_path, pathChooserTitle, pathChooserDescription, chooserDescriptor) {

				public void actionPerformed(ActionEvent event) {
					super.actionPerformed(event);

					_pathChangedByUser = true;
				}

			};

		_pathLabel = new JLabel(pathLabelText);

		_pathLabel.setLabelFor(_path);

		if (bold) {
			Font font = UIUtil.getLabelFont();

			_pathLabel.setFont(font.deriveFont(Font.BOLD));
		}

		insets = JBUI.insets(0, 0, 5, 4);

		_pathPanel = new FieldPanel(_path, null, null, browseButtonActionListener, null);

		add(
			_pathLabel,
			new GridBagConstraints(
				0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				insets, 0, 0));

		insets = JBUI.insets(0, 0, 5, 0);

		add(
			_pathPanel,
			new GridBagConstraints(
				1, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, insets, 0, 0));
	}

	public JTextField getNameComponent() {
		return _name;
	}

	public String getNameValue() {
		String name = _name.getText();

		return name.trim();
	}

	public String getPath() {
		String path = _path.getText();

		return FileUtil.expandUserHome(FileUtil.toSystemIndependentName(path.trim()));
	}

	public JTextField getPathComponent() {
		return _path;
	}

	public boolean isPathChangedByUser() {
		return _pathChangedByUser;
	}

	public boolean isSyncEnabled() {
		return _syncEnabled;
	}

	public void setNameValue(String name) {
		boolean nameChangedByUser = _nameChangedByUser;

		_setNamePathSyncEnabled(false);

		try {
			_name.setText(name);
		}
		finally {
			_nameChangedByUser = nameChangedByUser;

			_setNamePathSyncEnabled(true);
		}
	}

	public void setPath(String path) {
		boolean pathChangedByUser = _pathChangedByUser;

		_setPathNameSyncEnabled(false);

		try {
			_path.setText(FileUtil.getLocationRelativeToUserHome(FileUtil.toSystemDependentName(path)));
		}
		finally {
			_pathChangedByUser = pathChangedByUser;

			_setPathNameSyncEnabled(true);
		}
	}

	public void setShouldBeAbsolute(boolean shouldBeAbsolute) {
		_shouldBeAbsolute = shouldBeAbsolute;
	}

	public boolean validateNameAndPath(WizardContext context, boolean defaultFormat) throws ConfigurationException {
		String name = getNameValue();

		if (StringUtil.isEmptyOrSpaces(name)) {
			ApplicationInfo applicationInfo = ApplicationInfo.getInstance();

			throw new ConfigurationException(
				IdeBundle.message(
					"prompt.new.project.file.name", applicationInfo.getVersionName(), context.getPresentationName()));
		}

		String projectDirectory = getPath();

		if (StringUtil.isEmptyOrSpaces(projectDirectory)) {
			throw new ConfigurationException(
				IdeBundle.message("prompt.enter.project.file.location", context.getPresentationName()));
		}

		if (_shouldBeAbsolute && !new File(projectDirectory).isAbsolute()) {
			throw new ConfigurationException(
				StringUtil.capitalize(
					IdeBundle.message("file.location.should.be.absolute", context.getPresentationName())));
		}

		String message = IdeBundle.message("directory.project.file.directory", context.getPresentationName());

		if (!ProjectWizardUtil.createDirectoryIfNotExists(message, projectDirectory, isPathChangedByUser())) {
			return false;
		}

		File file = new File(projectDirectory);

		if (file.exists() && !file.canWrite()) {
			String msg = String.format(
				"Directory '%s' is not seem to be writable. Please consider another location.", projectDirectory);

			throw new ConfigurationException(msg);
		}

		ProjectManager projectManager = ProjectManager.getInstance();

		for (Project project : projectManager.getOpenProjects()) {
			if (ProjectUtil.isSameProject(projectDirectory, project)) {
				String msg = String.format(
					"Directory '%s' is already taken by the project '%s'. Please consider another location.",
					projectDirectory, project.getName());

				throw new ConfigurationException(msg);
			}
		}

		boolean shouldContinue = true;
		String fileName = defaultFormat ? name + ProjectFileType.DOT_DEFAULT_EXTENSION : Project.DIRECTORY_STORE_FOLDER;

		File projectFile = new File(file, fileName);

		if (projectFile.exists()) {
			message = IdeBundle.message(
				"prompt.overwrite.project.file", projectFile.getAbsolutePath(), context.getPresentationName());

			int answer = Messages.showYesNoDialog(
				message, IdeBundle.message("title.file.already.exists"), Messages.getQuestionIcon());

			shouldContinue = answer == Messages.YES;
		}

		return shouldContinue;
	}

	private boolean _isNamePathSyncEnabled() {
		if (!isSyncEnabled()) {
			return false;
		}

		return _namePathSyncEnabled;
	}

	private boolean _isPathNameSyncEnabled() {
		if (!isSyncEnabled()) {
			return false;
		}

		return _pathNameSyncEnabled;
	}

	private void _setNamePathSyncEnabled(boolean namePathSyncEnabled) {
		_namePathSyncEnabled = namePathSyncEnabled;
	}

	private void _setPathNameSyncEnabled(boolean pathNameSyncEnabled) {
		_pathNameSyncEnabled = pathNameSyncEnabled;
	}

	private static final Logger _log = Logger.getInstance("#com.liferay.ide.idea.wizard.LiferayNamePathComponent");

	private static final long serialVersionUID = -6184786008797586234L;

	private JTextField _name;
	private boolean _nameChangedByUser = false;
	private JLabel _nameLabel;
	private boolean _namePathSyncEnabled = true;
	private JTextField _path;
	private boolean _pathChangedByUser = false;
	private JLabel _pathLabel;
	private boolean _pathNameSyncEnabled = true;
	private FieldPanel _pathPanel;
	private boolean _shouldBeAbsolute;
	private boolean _syncEnabled = true;

	private class NameFieldDocument extends PlainDocument {

		public NameFieldDocument() {
			addDocumentListener(
				new DocumentAdapter() {

					public void textChanged(DocumentEvent event) {
						_nameChangedByUser = true;
						_syncNameAndPath();
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

		private void _syncNameAndPath() {
			if (_isNamePathSyncEnabled() && !_pathChangedByUser) {
				try {
					_setPathNameSyncEnabled(false);

					String name = getText(0, getLength());
					String path = _path.getText();

					path = path.trim();

					int lastSeparatorIndex = path.lastIndexOf(File.separator);

					if (lastSeparatorIndex >= 0) {
						setPath(path.substring(0, lastSeparatorIndex + 1) + name);
					}
				}
				catch (BadLocationException ble) {
					_log.error(ble);
				}
				finally {
					_setPathNameSyncEnabled(true);
				}
			}
		}

		private static final long serialVersionUID = -5824446550196631956L;

	}

	private class PathFieldDocument extends PlainDocument {

		public PathFieldDocument() {
			addDocumentListener(
				new DocumentAdapter() {

					public void textChanged(DocumentEvent event) {
						_pathChangedByUser = true;
						_syncPathAndName();
					}

				});
		}

		private void _syncPathAndName() {
			if (_isPathNameSyncEnabled() && !_nameChangedByUser) {
				try {
					_setNamePathSyncEnabled(false);
					String path = getText(0, getLength());

					int lastSeparatorIndex = path.lastIndexOf(File.separator);

					if ((lastSeparatorIndex >= 0) && ((lastSeparatorIndex + 1) < path.length())) {
						setNameValue(path.substring(lastSeparatorIndex + 1));
					}
				}
				catch (BadLocationException ble) {
					_log.error(ble);
				}
				finally {
					_setNamePathSyncEnabled(true);
				}
			}
		}

		private static final long serialVersionUID = -1045916924224928928L;

	}

}