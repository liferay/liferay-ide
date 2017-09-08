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

package com.liferay.ide.idea.server;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.execution.ui.DefaultJreSelector;
import com.intellij.execution.ui.JrePathEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.PanelWithAnchor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Terry Jia
 */
public class LiferayBundleConfigurable extends SettingsEditor<LiferayBundleConfiguration> implements PanelWithAnchor {

    private LabeledComponent<ModulesComboBox> modules;
    private JPanel mainPanel;
    private JrePathEditor jrePath;
    private JTextField vmParams;
    private JTextField liferayBundle;
    private JComponent anchor;

    public LiferayBundleConfigurable(final Project project) {
        ModulesComboBox modulesComboBox = modules.getComponent();
        modulesComboBox.allowEmptySelection("<whole project>");
        modulesComboBox.fillModules(project);

        liferayBundle.setEditable(false);
        liferayBundle.setEnabled(false);
        jrePath.setDefaultJreSelector(DefaultJreSelector.fromModuleDependencies(modulesComboBox, true));
    }

    public void applyEditorTo(@NotNull final LiferayBundleConfiguration configuration) throws ConfigurationException {
        configuration.setAlternativeJrePath(jrePath.getJrePathOrName());
        configuration.setAlternativeJrePathEnabled(jrePath.isAlternativeJreSelected());
        configuration.setModule(modules.getComponent().getSelectedModule());
        configuration.setLiferayBundle(liferayBundle.getText());
        configuration.setVMParameters(vmParams.getText());
    }

    public void resetEditorFrom(@NotNull final LiferayBundleConfiguration configuration) {
        vmParams.setText(configuration.getVMParameters());
        liferayBundle.setText(configuration.getLiferayBundle());
        jrePath.setPathOrName(configuration.getAlternativeJrePath(), configuration.isAlternativeJrePathEnabled());
        modules.getComponent().setSelectedModule(configuration.getModule());
    }

    @NotNull
    public JComponent createEditor() {
        return mainPanel;
    }

    @Override
    public JComponent getAnchor() {
        return anchor;
    }

    @Override
    public void setAnchor(@Nullable JComponent anchor) {
        this.anchor = anchor;
        jrePath.setAnchor(anchor);
    }

}
