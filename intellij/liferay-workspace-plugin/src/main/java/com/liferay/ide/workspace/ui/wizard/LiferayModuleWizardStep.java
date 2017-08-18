package com.liferay.ide.workspace.ui.wizard;

import com.intellij.ide.projectWizard.NewProjectWizard;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ide.wizard.AbstractWizard;
import com.intellij.ide.wizard.StepAdapter;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.UIUtil;
import com.liferay.blade.cli.InitCommand;
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
import java.io.IOException;

/**
 * Created by wswdk on 2017/7/23.
 */
public class LiferayModuleWizardStep extends ModuleWizardStep {
    private JPanel mainPanel;
    private JPanel typesPanel;
    private JTextArea typeDescriptionField;
    private JBScrollPane typeDescriptionScrollPane;
    private JTextField packageName;
    private JTextField className;
    private final LiferayModuleBuilder builder;
    private final Tree typesTree;
    private WizardContext wizardContext;

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

            }
        });
        String s = InitCommand.DESCRIPTION;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);

        for (String type: BladeCLI.getProjectTemplates()){
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

    @Nullable
    public String getSelectedType() {
        return typesTree.getLastSelectedPathComponent().toString();
    }

    public String getPackageName(){
        return packageName.getText();
    }

    public String getClassName(){
        return className.getText();
    }

}
