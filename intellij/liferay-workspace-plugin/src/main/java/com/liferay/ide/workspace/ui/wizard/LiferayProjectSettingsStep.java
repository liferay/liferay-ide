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
    private JPanel settingsPanel;
    private JPanel expertPlaceholder;
    private JPanel expertPanel;
    private final HideableDecorator expertDecorator;
    private final LiferayNamePathComponent namePathComponent;
    private final ProjectFormatPanel formatPanel;
    private JPanel mainPanel;
    private LiferayModuleNameLocationComponent moduleNameLocationComponent;
    private final WizardContext wizardContext;
    @Nullable
    private ModuleWizardStep settingsStep;

    public LiferayProjectSettingsStep(WizardContext context) {
        wizardContext = context;
        formatPanel = new ProjectFormatPanel();
        namePathComponent = LiferayNamePathComponent.initNamePathComponent(context);
        namePathComponent.setShouldBeAbsolute(true);

        final JPanel modulePanel = getModulePanel();

        if (context.isCreatingNewProject()) {
            settingsPanel.add(namePathComponent, BorderLayout.NORTH);
            addExpertPanel(modulePanel);
        } else {
            settingsPanel.add(modulePanel, BorderLayout.NORTH);
        }

        moduleNameLocationComponent.bindModuleSettings(namePathComponent);
        expertDecorator = new HideableDecorator(expertPlaceholder, "Mor&e Settings", false);
        expertPanel.setBorder(IdeBorderFactory.createEmptyBorder(0, IdeBorderFactory.TITLED_BORDER_INDENT, 5, 0));
        expertDecorator.setContentComponent(expertPanel);

        if (wizardContext.isCreatingNewProject()) {
            addProjectFormat(modulePanel);
        }
    }

    private JPanel getModulePanel() {
        return moduleNameLocationComponent.getModulePanel();
    }

    private JTextField getNameComponent() {
        return wizardContext.isCreatingNewProject() ? namePathComponent.getNameComponent() : moduleNameLocationComponent.getModuleNameField();
    }

    private void addProjectFormat(JPanel panel) {
        addField("Project \u001bformat:", formatPanel.getStorageFormatComboBox(), panel);
    }

    @Override
    public String getHelpId() {
        return wizardContext.isCreatingNewProject() ? "New_Project_Main_Settings" : "Add_Module_Main_Settings";
    }

    private void setupPanels() {
        final ModuleBuilder moduleBuilder = (ModuleBuilder) wizardContext.getProjectBuilder();

        restorePanel(namePathComponent, 4);
        restorePanel(getModulePanel(), wizardContext.isCreatingNewProject() ? 8 : 6);
        restorePanel(expertPanel, wizardContext.isCreatingNewProject() ? 1 : 0);

        settingsStep = moduleBuilder == null ? null : moduleBuilder.modifySettingsStep(this);

        expertPlaceholder.setVisible(!(moduleBuilder instanceof TemplateModuleBuilder) && expertPanel.getComponentCount() > 0);

        for (int i = 0; i < 6; i++) {
            getModulePanel().getComponent(i).setVisible(!(moduleBuilder instanceof EmptyModuleBuilder));
        }

        settingsPanel.revalidate();
        settingsPanel.repaint();
    }

    private static void restorePanel(JPanel component, int i) {
        while (component.getComponentCount() > i) {
            component.remove(component.getComponentCount() - 1);
        }
    }

    @Override
    public void updateStep() {
        expertDecorator.setOn(SelectTemplateSettings.getInstance().EXPERT_MODE);

        setupPanels();
    }

    @Override
    public void onStepLeaving() {
        SelectTemplateSettings.getInstance().EXPERT_MODE = expertDecorator.isExpanded();
    }

    @Override
    public boolean validate() throws ConfigurationException {
        if (wizardContext.isCreatingNewProject()) {
            if (!namePathComponent.validateNameAndPath(wizardContext, formatPanel.isDefault())) return false;
        }

        if (!moduleNameLocationComponent.validate()) {
            return false;
        }

        if (settingsStep != null) {
            return settingsStep.validate();
        }

        return true;
    }

    @Override
    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return getNameComponent();
    }

    @Override
    public void updateDataModel() {
        wizardContext.setProjectName(namePathComponent.getNameValue());
        wizardContext.setProjectFileDirectory(namePathComponent.getPath());
        formatPanel.updateData(wizardContext);
        moduleNameLocationComponent.updateDataModel();

        final ProjectBuilder moduleBuilder = wizardContext.getProjectBuilder();

        if (moduleBuilder instanceof TemplateModuleBuilder) {
            wizardContext.setProjectStorageFormat(StorageScheme.DIRECTORY_BASED);
        }

        if (settingsStep != null) {
            settingsStep.updateDataModel();
        }
    }

    @Override
    public String getName() {
        return "Project Settings";
    }

    @Override
    public WizardContext getContext() {
        return wizardContext;
    }

    @Override
    public void addSettingsField(@NotNull String label, @NotNull JComponent field) {
        final JPanel panel = wizardContext.isCreatingNewProject() ? namePathComponent : getModulePanel();

        addField(label, field, panel);
    }

    static void addField(String label, JComponent field, JPanel panel) {
        final JLabel jLabel = new JBLabel(label);

        jLabel.setLabelFor(field);

        panel.add(jLabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, JBUI.insetsBottom(5), 4, 0));
        panel.add(field, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, JBUI.insetsBottom(5), 0, 0));
    }

    @Override
    public void addSettingsComponent(@NotNull JComponent component) {
        final JPanel panel = wizardContext.isCreatingNewProject() ? namePathComponent : getModulePanel();

        panel.add(component, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0));
    }

    @Override
    public void addExpertPanel(@NotNull JComponent panel) {
        expertPanel.add(panel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0));
    }

    @Override
    public void addExpertField(@NotNull String label, @NotNull JComponent field) {
        final JPanel panel = wizardContext.isCreatingNewProject() ? getModulePanel() : expertPanel;

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
        moduleNameLocationComponent = new LiferayModuleNameLocationComponent(wizardContext);
    }

}
