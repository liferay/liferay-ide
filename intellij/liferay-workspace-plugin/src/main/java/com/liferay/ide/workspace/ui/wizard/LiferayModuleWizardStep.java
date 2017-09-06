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

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiNameHelper;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;
import com.liferay.ide.workspace.ui.builder.LiferayModuleBuilder;
import com.liferay.ide.workspace.ui.util.BladeCLI;
import com.liferay.ide.workspace.ui.util.CoreUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Terry Jia
 */
public class LiferayModuleWizardStep extends ModuleWizardStep {
    private JPanel mainPanel;
    private JPanel typesPanel;
    private JTextField packageName;
    private JTextField className;
    private final LiferayModuleBuilder builder;
    private final Tree typesTree;

    public LiferayModuleWizardStep(LiferayModuleBuilder builder) {
        this.builder = builder;
        typesTree = new Tree();
        typesTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));

        JScrollPane typesScrollPane = ScrollPaneFactory.createScrollPane(typesTree);

        typesPanel.add(typesScrollPane, "archetypes");

        typesTree.setRootVisible(false);
        typesTree.setShowsRootHandles(true);
        typesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);

        for (String type : BladeCLI.getProjectTemplates()) {
            if (type.equals("fragment")) {
                continue;
            }

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(type, true);
            root.add(node);
        }

        TreeModel model = new DefaultTreeModel(root);

        typesTree.setModel(model);
    }


    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public void updateDataModel() {
        builder.setType(getSelectedType());
        builder.setClassName(getClassName());
        builder.setPackageName(getPackageName());
    }

    @Override
    public boolean validate() throws ConfigurationException {
        String validationTitle = "Validation Error";

        if (CoreUtil.isNullOrEmpty(getSelectedType())) {
            throw new ConfigurationException("Please click one of the items to select a template", validationTitle);
        }

        Project workspaceProject = ProjectManager.getInstance().getOpenProjects()[0];

        String packageNameValue = getPackageName();
        String classNameValue = getClassName();

        if (!CoreUtil.isNullOrEmpty(packageNameValue) && !PsiDirectoryFactory.getInstance(workspaceProject).isValidPackageName(packageNameValue)) {
            throw new ConfigurationException(packageNameValue + " is not a valid package name", validationTitle);
        }

        if (!CoreUtil.isNullOrEmpty(classNameValue) && !PsiNameHelper.getInstance(workspaceProject).isQualifiedName(classNameValue)) {
            throw new ConfigurationException(classNameValue + " is not a valid java class name", validationTitle);
        }

        return true;
    }

    @Nullable
    public String getSelectedType() {
        Object selectedType = typesTree.getLastSelectedPathComponent();
        if (selectedType != null) {
            return selectedType.toString();
        } else {
            return null;
        }
    }

    public String getPackageName() {
        return packageName.getText();
    }

    public String getClassName() {
        return className.getText();
    }

}
