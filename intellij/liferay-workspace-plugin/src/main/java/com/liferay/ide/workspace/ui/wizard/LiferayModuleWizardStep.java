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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;
import com.liferay.ide.workspace.ui.builder.LiferayModuleBuilder;
import com.liferay.ide.workspace.ui.util.BladeCLI;
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

        typesTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                String type = e.getNewLeadSelectionPath().getLastPathComponent().toString();

                if (type.equals("activator") || type.equals("api") || type.equals("theme-contributor") ||
                        type.equals("portlet-provider") || type.equals("content-targeting-report") ||
                        type.equals("content-targeting-tracking-action")) {

                    packageName.setEditable(false);
                    className.setEditable(false);
                    packageName.setEnabled(false);
                    className.setEnabled(false);
                } else {
                    packageName.setEditable(true);
                    className.setEditable(true);
                    packageName.setEnabled(true);
                    className.setEnabled(true);
                }
            }
        });

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);

        for (String type : BladeCLI.getProjectTemplates()) {
            if (type.equals("fragment")) {
                continue;
            }

            DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(type, true);
            root.add(node1);
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
        // Need to add validation for className and PackageName

        return true;
    }

    @Nullable
    public String getSelectedType() {
        return typesTree.getLastSelectedPathComponent().toString();
    }

    public String getPackageName() {
        return packageName.getText();
    }

    public String getClassName() {
        return className.getText();
    }

}
