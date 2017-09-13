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

package com.liferay.ide.idea.server;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.execution.ui.DefaultJreSelector;
import com.intellij.execution.ui.JrePathEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.PanelWithAnchor;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Terry Jia
 */
public class LiferayBundleConfigurable extends SettingsEditor<LiferayBundleConfiguration> implements PanelWithAnchor {

	public LiferayBundleConfigurable(Project project) {
		ModulesComboBox modulesComboBox = _modules.getComponent();

		modulesComboBox.allowEmptySelection("<whole project>");
		modulesComboBox.fillModules(project);

		_liferayBundle.setEditable(false);
		_liferayBundle.setEnabled(false);
		_jrePath.setDefaultJreSelector(DefaultJreSelector.fromModuleDependencies(modulesComboBox, true));
	}

	public void applyEditorTo(@NotNull LiferayBundleConfiguration configuration) throws ConfigurationException {
		configuration.setAlternativeJrePath(_jrePath.getJrePathOrName());
		configuration.setAlternativeJrePathEnabled(_jrePath.isAlternativeJreSelected());
		configuration.setModule(_modules.getComponent().getSelectedModule());
		configuration.setLiferayBundle(_liferayBundle.getText());
		configuration.setVMParameters(_vmParams.getText());
	}

	@NotNull
	public JComponent createEditor() {
		return _mainPanel;
	}

	@Override
	public JComponent getAnchor() {
		return _anchor;
	}

	public void resetEditorFrom(@NotNull LiferayBundleConfiguration configuration) {
		_vmParams.setText(configuration.getVMParameters());
		_liferayBundle.setText(configuration.getLiferayBundle());
		_jrePath.setPathOrName(configuration.getAlternativeJrePath(), configuration.isAlternativeJrePathEnabled());
		_modules.getComponent().setSelectedModule(configuration.getModule());
	}

	@Override
	public void setAnchor(@Nullable JComponent anchor) {
		_anchor = anchor;
		_jrePath.setAnchor(anchor);
	}

	private JComponent _anchor;
	private JrePathEditor _jrePath;
	private JTextField _liferayBundle;
	private JPanel _mainPanel;
	private LabeledComponent<ModulesComboBox> _modules;
	private JTextField _vmParams;

}