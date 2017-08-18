package com.liferay.ide.workspace.ui.wizard;

import com.intellij.ide.projectWizard.ProjectTemplateList;
import com.intellij.ide.projectWizard.ProjectTypeStep;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.components.JBCheckBox;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LiferayChooseTemplateStep extends ModuleWizardStep {

    private final WizardContext myWizardContext;
    private final LiferayProjectTypeStep myProjectTypeStep;

    private JPanel myPanel;
    private LiferayProjectTemplateList myTemplateList;
    private JBCheckBox myCreateFromTemplateCheckBox;

    public LiferayChooseTemplateStep(WizardContext wizardContext, LiferayProjectTypeStep projectTypeStep) {
        myWizardContext = wizardContext;
        myProjectTypeStep = projectTypeStep;
        myCreateFromTemplateCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myTemplateList.setEnabled(myCreateFromTemplateCheckBox.isSelected());
                if (myCreateFromTemplateCheckBox.isSelected()) {
                    IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> {
                        IdeFocusManager.getGlobalInstance().requestFocus(myTemplateList.getList(), true);
                    });
                }
            }
        });
        myTemplateList.setEnabled(false);
    }

    @Override
    public boolean isStepVisible() {
        return myWizardContext.isCreatingNewProject() && !myProjectTypeStep.getAvailableTemplates().isEmpty();
    }

    @Override
    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    public void updateStep() {
        myTemplateList.setTemplates(new ArrayList<>(myProjectTypeStep.getAvailableTemplates()), false);
    }

    @Override
    public void updateDataModel() {
        myWizardContext.setProjectTemplate(myCreateFromTemplateCheckBox.isSelected() ? myTemplateList.getSelectedTemplate() : null);
    }

    public LiferayProjectTemplateList getTemplateList() {
        return myTemplateList;
    }

    @TestOnly
    public boolean setSelectedTemplate(String name) {
        myCreateFromTemplateCheckBox.setSelected(true);
        return myTemplateList.setSelectedTemplate(name);
    }
}
