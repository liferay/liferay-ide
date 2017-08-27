package com.liferay.ide.workspace.ui.bundle;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.execution.ui.DefaultJreSelector;
import com.intellij.execution.ui.JrePathEditor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.PanelWithAnchor;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LiferayBundleConfigurable extends SettingsEditor<LiferayBundleConfiguration> implements PanelWithAnchor {

    private LabeledComponent<ModulesComboBox> myModuleComponent;
    private JPanel mainPanel;
    private JrePathEditor myJrePathEditor;
    private JTextField vmParams;
    private JTextField liferayBundle;
    private final Project myProject;
    private JComponent myAnchor;

    public LiferayBundleConfigurable(final Project project) {
        myProject = project;

        ModulesComboBox modulesComboBox = myModuleComponent.getComponent();
        modulesComboBox.allowEmptySelection("<whole project>");
        modulesComboBox.fillModules(project);

        liferayBundle.setEditable(false);
        myJrePathEditor.setDefaultJreSelector(DefaultJreSelector.fromModuleDependencies(modulesComboBox, true));
    }

    public void applyEditorTo(@NotNull final LiferayBundleConfiguration configuration) throws ConfigurationException {
        configuration.setAlternativeJrePath(myJrePathEditor.getJrePathOrName());
        configuration.setAlternativeJrePathEnabled(myJrePathEditor.isAlternativeJreSelected());
        configuration.setModule(myModuleComponent.getComponent().getSelectedModule());
        configuration.setLiferayBundle(liferayBundle.getText());
        configuration.setVMParameters(vmParams.getText());
    }

    public void resetEditorFrom(@NotNull final LiferayBundleConfiguration configuration) {
        vmParams.setText(configuration.getVMParameters());
        liferayBundle.setText(configuration.getLiferayBundle());
        myJrePathEditor.setPathOrName(configuration.getAlternativeJrePath(), configuration.isAlternativeJrePathEnabled());
        myModuleComponent.getComponent().setSelectedModule(configuration.getModule());
    }

    @NotNull
    public JComponent createEditor() {
        return mainPanel;
    }

    @Override
    public JComponent getAnchor() {
        return myAnchor;
    }

    @Override
    public void setAnchor(@Nullable JComponent anchor) {
        myAnchor = anchor;
        myJrePathEditor.setAnchor(anchor);
    }

}
