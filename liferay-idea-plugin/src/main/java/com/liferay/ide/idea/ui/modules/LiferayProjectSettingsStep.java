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
 *
 */

package com.liferay.ide.idea.ui.modules;

import com.intellij.ide.util.newProjectWizard.SelectTemplateSettings;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.platform.templates.TemplateModuleBuilder;
import com.intellij.projectImport.ProjectFormatPanel;
import com.intellij.ui.HideableDecorator;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Terry Jia
 */
public class LiferayProjectSettingsStep extends ModuleWizardStep implements SettingsStep {

    public LiferayProjectSettingsStep(WizardContext context) {
        _context = context;
        _formatPanel = new ProjectFormatPanel();
        _namePathComponent = LiferayNamePathComponent.initNamePathComponent(context);
        _namePathComponent.setShouldBeAbsolute(true);

        JPanel modulePanel = getModulePanel();

        if (context.isCreatingNewProject()) {
            _settingsPanel.add(_namePathComponent, BorderLayout.NORTH);
            addExpertPanel(modulePanel);
        } else {
            _settingsPanel.add(modulePanel, BorderLayout.NORTH);
        }

        _moduleNameLocationComponent.bindModuleSettings(_namePathComponent);
        _expertDecorator = new HideableDecorator(_expertPlaceholder, "Mor&e Settings", false);
        _expertPanel.setBorder(IdeBorderFactory.createEmptyBorder(0, IdeBorderFactory.TITLED_BORDER_INDENT, 5, 0));
        _expertDecorator.setContentComponent(_expertPanel);

        if (_context.isCreatingNewProject()) {
            addProjectFormat(modulePanel);
        }
    }

    @Override
    public void _init() {
        _moduleNameLocationComponent.updateLocations();
    }

    private JPanel getModulePanel() {
        return _moduleNameLocationComponent.getModulePanel();
    }

    private JTextField getNameComponent() {
        return _context.isCreatingNewProject() ? _namePathComponent.getNameComponent() : _moduleNameLocationComponent.getModuleNameField();
    }

    private void addProjectFormat(JPanel panel) {
        addField("Project \u001bformat:", _formatPanel.getStorageFormatComboBox(), panel);
    }

    @Override
    public String getHelpId() {
        return _context.isCreatingNewProject() ? "New_Project_Main_Settings" : "Add_Module_Main_Settings";
    }

    private void setupPanels() {
        ModuleBuilder moduleBuilder = (ModuleBuilder) _context.getProjectBuilder();

        restorePanel(_namePathComponent, 4);
        restorePanel(getModulePanel(), _context.isCreatingNewProject() ? 8 : 6);
        restorePanel(_expertPanel, _context.isCreatingNewProject() ? 1 : 0);

        _settingsStep = moduleBuilder == null ? null : moduleBuilder.modifySettingsStep(this);

        _expertPlaceholder.setVisible(!(moduleBuilder instanceof TemplateModuleBuilder) && _expertPanel.getComponentCount() > 0);

        for (int i = 0; i < 6; i++) {
            getModulePanel().getComponent(i).setVisible(!(moduleBuilder instanceof EmptyModuleBuilder));
        }

        _settingsPanel.revalidate();
        _settingsPanel.repaint();
    }

    private static void restorePanel(JPanel component, int i) {
        while (component.getComponentCount() > i) {
            component.remove(component.getComponentCount() - 1);
        }
    }

    @Override
    public void updateStep() {
        _expertDecorator.setOn(SelectTemplateSettings.getInstance().EXPERT_MODE);

        setupPanels();
    }

    @Override
    public void onStepLeaving() {
        SelectTemplateSettings.getInstance().EXPERT_MODE = _expertDecorator.isExpanded();
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (_context.isCreatingNewProject()) {
            if (!_namePathComponent.validateNameAndPath(_context, _formatPanel.isDefault())) {
                return false;
            }
        }

        if (!_moduleNameLocationComponent.validate()) {
            return false;
        }

        if (_settingsStep != null) {
            return _settingsStep.validate();
        }

        return true;
    }

    @Override
    public JComponent getComponent() {
        return _mainPanel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return getNameComponent();
    }

    @Override
    public void updateDataModel() {
        _context.setProjectName(_namePathComponent.getNameValue());
        _context.setProjectFileDirectory(_namePathComponent.getPath());
        _formatPanel.updateData(_context);
        _moduleNameLocationComponent.updateDataModel();

        ProjectBuilder moduleBuilder = _context.getProjectBuilder();

        if (moduleBuilder instanceof TemplateModuleBuilder) {
            _context.setProjectStorageFormat(StorageScheme.DIRECTORY_BASED);
        }

        if (_settingsStep != null) {
            _settingsStep.updateDataModel();
        }
    }

    @Override
    public String getName() {
        return "Project Settings";
    }

    @Override
    public WizardContext getContext() {
        return _context;
    }

    @Override
    public void addSettingsField(@NotNull String label, @NotNull JComponent field) {
        JPanel panel = _context.isCreatingNewProject() ? _namePathComponent : getModulePanel();

        addField(label, field, panel);
    }

    static void addField(String label, JComponent field, JPanel panel) {
        JLabel jLabel = new JBLabel(label);

        jLabel.setLabelFor(field);

        panel.add(jLabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, JBUI.insetsBottom(5), 4, 0));
        panel.add(field, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, JBUI.insetsBottom(5), 0, 0));
    }

    @Override
    public void addSettingsComponent(@NotNull JComponent component) {
        JPanel panel = _context.isCreatingNewProject() ? _namePathComponent : getModulePanel();

        panel.add(component, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0));
    }

    @Override
    public void addExpertPanel(@NotNull JComponent panel) {
        _expertPanel.add(panel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0));
    }

    @Override
    public void addExpertField(@NotNull String label, @NotNull JComponent field) {
        JPanel panel = _context.isCreatingNewProject() ? getModulePanel() : _expertPanel;

        addField(label, field, panel);
    }

    @NotNull
    public JTextField getModuleNameField() {
        return getNameComponent();
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    private void createUIComponents() {
        _moduleNameLocationComponent = new LiferayModuleNameLocationComponent(_context);
    }

    private JPanel _settingsPanel;
    private JPanel _expertPlaceholder;
    private JPanel _expertPanel;
    private HideableDecorator _expertDecorator;
    private LiferayNamePathComponent _namePathComponent;
    private ProjectFormatPanel _formatPanel;
    private JPanel _mainPanel;
    private LiferayModuleNameLocationComponent _moduleNameLocationComponent;
    private WizardContext _context;
    @Nullable
    private ModuleWizardStep _settingsStep;

}
