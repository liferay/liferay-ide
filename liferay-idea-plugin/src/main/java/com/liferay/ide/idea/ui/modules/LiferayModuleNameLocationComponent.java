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

import java.io.File;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Terry Jia
 */
public class LiferayModuleNameLocationComponent {

	public LiferayModuleNameLocationComponent(@NotNull WizardContext context) {
		_context = context;
	}

	public void bindModuleSettings(LiferayNamePathComponent namePathComponent) {
		Document nameDocument = namePathComponent.getNameComponent().getDocument();

		nameDocument.addDocumentListener(
			new DocumentAdapter() {

				protected void textChanged(DocumentEvent e) {
					if (!_moduleNameChangedByUser) {
						setModuleName(namePathComponent.getNameValue());
					}
				}

			});

		_moduleContentRoot.addBrowseFolderListener(
			ProjectBundle.message("project.new.wizard.module.content.root.chooser.title"),
			ProjectBundle.message("project.new.wizard.module.content.root.chooser.description"), _context.getProject(),
			BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR);

		Document pathDocument = namePathComponent.getPathComponent().getDocument();

		pathDocument.addDocumentListener(
			new DocumentAdapter() {

				protected void textChanged(DocumentEvent e) {
					if (!_contentRootChangedByUser) {
						_setModuleContentRoot(namePathComponent.getPath());
					}
				}

			});

		_moduleName.getDocument().addDocumentListener(
			new DocumentAdapter() {

				protected void textChanged(DocumentEvent e) {
					if (_moduleNameDocListenerEnabled) {
						_moduleNameChangedByUser = true;
					}

					String path = _getDefaultBaseDir(_context, namePathComponent);
					String moduleName = _getModuleName();

					if ((path.length() > 0) && !Comparing.strEqual(moduleName, namePathComponent.getNameValue())) {
						path += "/" + _getTargetFolderName() + "/" + moduleName;
					}

					if (!_contentRootChangedByUser) {
						boolean f = _moduleNameChangedByUser;
						_moduleNameChangedByUser = true;
						_setModuleContentRoot(path);
						_moduleNameChangedByUser = f;
					}

					if (!_imlLocationChangedByUser) {
						_setImlFileLocation(path);
					}
				}

			});

		Document moduleContentRootDocument = _moduleContentRoot.getTextField().getDocument();

		moduleContentRootDocument.addDocumentListener(
			new DocumentAdapter() {

				protected void textChanged(DocumentEvent e) {
					if (_contentRootDocListenerEnabled) {
						_contentRootChangedByUser = true;
					}

					if (!_imlLocationChangedByUser) {
						_setImlFileLocation(_getModuleContentRoot());
					}

					if (!_moduleNameChangedByUser) {
						String path = FileUtil.toSystemIndependentName(_getModuleContentRoot());

						int idx = path.lastIndexOf("/");

						boolean f = _contentRootChangedByUser;
						_contentRootChangedByUser = true;

						boolean i = _imlLocationChangedByUser;
						_imlLocationChangedByUser = true;

						setModuleName(idx >= 0 ? path.substring(idx + 1) : "");

						_contentRootChangedByUser = f;
						_imlLocationChangedByUser = i;
					}
				}

			});

		_moduleFileLocation.addBrowseFolderListener(
			ProjectBundle.message("project.new.wizard.module.file.chooser.title"),
			ProjectBundle.message("project.new.wizard.module.file.description"), _context.getProject(),
			BrowseFilesListener.SINGLE_DIRECTORY_DESCRIPTOR);

		Document moduleFileLocationDocument = _moduleFileLocation.getTextField().getDocument();

		moduleFileLocationDocument.addDocumentListener(
			new DocumentAdapter() {

				protected void textChanged(DocumentEvent e) {
					if (_imlLocationDocListenerEnabled) {
						_imlLocationChangedByUser = true;
					}
				}

			});

		Document namePathDocument = namePathComponent.getPathComponent().getDocument();

		namePathDocument.addDocumentListener(
			new DocumentAdapter() {

				protected void textChanged(DocumentEvent e) {
					if (!_imlLocationChangedByUser) {
						_setImlFileLocation(namePathComponent.getPath());
					}
				}

			});
	}

	@Nullable
	public AbstractModuleBuilder getModuleBuilder() {
		return (AbstractModuleBuilder)_context.getProjectBuilder();
	}

	public JTextField getModuleNameField() {
		return _moduleName;
	}

	public JPanel getModulePanel() {
		return _modulePanel;
	}

	public void setModuleName(String moduleName) {
		_moduleNameDocListenerEnabled = false;
		_moduleName.setText(moduleName);
		_moduleNameDocListenerEnabled = true;
	}

	public void updateDataModel() {
		AbstractModuleBuilder moduleBuilder = getModuleBuilder();

		if (moduleBuilder == null) {
			return;
		}

		String moduleName = _getModuleName();

		moduleBuilder.setName(moduleName);
		moduleBuilder.setModuleFilePath(
			FileUtil.toSystemIndependentName(_moduleFileLocation.getText()) + "/" + moduleName +
				ModuleFileType.DOT_DEFAULT_EXTENSION);

		moduleBuilder.setContentEntryPath(FileUtil.toSystemIndependentName(_getModuleContentRoot()));
	}

	public void updateLocations() {
		Project project = _context.getProject();

		assert project != null;

		VirtualFile baseDir = project.getBaseDir();

		if (baseDir != null) {
			String baseDirPath = baseDir.getPath();

			String moduleName = ProjectWizardUtil.findNonExistingFileName(baseDirPath, "untitled", "");

			setModuleName(moduleName);

			String contentRoot = baseDirPath + "/" + _getTargetFolderName() + "/" + moduleName;

			_setModuleContentRoot(contentRoot);
			_setImlFileLocation(contentRoot);

			_moduleName.select(0, moduleName.length());
		}
	}

	public boolean validate() throws ConfigurationException {
		AbstractModuleBuilder builder = getModuleBuilder();

		if ((builder != null) && !builder.validateModuleName(_getModuleName())) {
			return false;
		}

		if (!_validateModulePaths()) {
			return false;
		}

		_validateExistingModuleName();

		return true;
	}

	private static String _getDefaultBaseDir(WizardContext wizardContext, LiferayNamePathComponent namePathComponent) {
		if (wizardContext.isCreatingNewProject()) {
			return namePathComponent.getPath();
		}
		else {
			Project project = wizardContext.getProject();

			assert project != null;

			VirtualFile baseDir = project.getBaseDir();

			if (baseDir != null) {
				return baseDir.getPath();
			}

			return "";
		}
	}

	private String _getModuleContentRoot() {
		return _moduleContentRoot.getText();
	}

	private String _getModuleName() {
		return _moduleName.getText().trim();
	}

	private String _getTargetFolderName() {
		AbstractModuleBuilder builder = getModuleBuilder();
		LiferayModuleBuilder liferayModuleBuilder = null;

		if (builder instanceof LiferayModuleBuilder) {
			liferayModuleBuilder = (LiferayModuleBuilder)builder;
		}

		String targetFolder = "modules";

		if (liferayModuleBuilder != null) {
			String templateType = liferayModuleBuilder.getType();

			if ("theme".equals(templateType) || "layout-template".equals(templateType) ||
				"spring-mvc-portlet".equals(templateType)) {

				targetFolder = "wars";
			}
		}

		return targetFolder;
	}

	private void _setImlFileLocation(String path) {
		_imlLocationDocListenerEnabled = false;
		_moduleFileLocation.setText(FileUtil.toSystemDependentName(path));
		_imlLocationDocListenerEnabled = true;
	}

	private void _setModuleContentRoot(String path) {
		_contentRootDocListenerEnabled = false;
		_moduleContentRoot.setText(FileUtil.toSystemDependentName(path));
		_contentRootDocListenerEnabled = true;
	}

	private void _validateExistingModuleName() throws ConfigurationException {
		Project project = _context.getProject();

		if (project == null) {
			return;
		}

		String moduleName = _getModuleName();

		Module module;

		ProjectStructureConfigurable fromConfigurable = ProjectStructureConfigurable.getInstance(project);

		if (fromConfigurable != null) {
			module = fromConfigurable.getModulesConfig().getModule(moduleName);
		}
		else {
			module = ModuleManager.getInstance(project).findModuleByName(moduleName);
		}

		if (module != null) {
			String msg = "Module \'" + moduleName + "\' already exists in project. Please specify another name.";

			throw new ConfigurationException(msg);
		}
	}

	private boolean _validateModulePaths() throws ConfigurationException {
		String moduleName = _getModuleName();
		String moduleFileDirectory = _moduleFileLocation.getText();

		if (moduleFileDirectory.length() == 0) {
			throw new ConfigurationException("Enter module file location");
		}

		if (moduleName.length() == 0) {
			throw new ConfigurationException("Enter a module name");
		}

		if (!ProjectWizardUtil.createDirectoryIfNotExists(
				IdeBundle.message("directory.module.file"), moduleFileDirectory, _imlLocationChangedByUser)) {

			return false;
		}

		if (!ProjectWizardUtil.createDirectoryIfNotExists(
				IdeBundle.message("directory.module.content.root"), _moduleContentRoot.getText(),
				_contentRootChangedByUser)) {

			return false;
		}

		File moduleFile = new File(moduleFileDirectory, moduleName + ModuleFileType.DOT_DEFAULT_EXTENSION);

		if (moduleFile.exists()) {
			String identification = IdeBundle.message("project.new.wizard.module.identification");
			String existsTitle = IdeBundle.message("title.file.already.exists");
			String filePrompt = IdeBundle.message(
				"prompt.overwrite.project.file", moduleFile.getAbsolutePath(), identification);

			int answer = Messages.showYesNoDialog(filePrompt, existsTitle, Messages.getQuestionIcon());

			if (answer != Messages.YES) {
				return false;
			}
		}

		return true;
	}

	private boolean _contentRootChangedByUser = false;
	private boolean _contentRootDocListenerEnabled = true;
	private WizardContext _context;
	private boolean _imlLocationChangedByUser = false;
	private boolean _imlLocationDocListenerEnabled = true;
	private TextFieldWithBrowseButton _moduleContentRoot;
	private TextFieldWithBrowseButton _moduleFileLocation;
	private JTextField _moduleName;
	private boolean _moduleNameChangedByUser = false;
	private boolean _moduleNameDocListenerEnabled = true;
	private JPanel _modulePanel;

}