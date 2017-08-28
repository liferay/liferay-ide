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
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;
import com.liferay.ide.workspace.ui.builder.LiferayModuleFragmentBuilder;
import com.liferay.ide.workspace.ui.util.FileUtil;
import com.liferay.ide.workspace.ui.util.LiferayWorkspaceUtil;
import com.liferay.ide.workspace.ui.util.ServerUtil;
import com.liferay.ide.workspace.ui.util.ZipUtil;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Terry Jia
 */
public class LiferayModuleFragmentWizardStep extends ModuleWizardStep {
    private JPanel mainPanel;
    private JPanel jspsPanel;
    private JComboBox osgiHost;
    private final LiferayModuleFragmentBuilder builder;
    private final Tree jspsTree;
    private WizardContext wizardContext;
    private File temp = new File(new File(System.getProperties().getProperty("user.home"), ".liferay-ide"), "bundles");

    public LiferayModuleFragmentWizardStep(WizardContext wizardContext, LiferayModuleFragmentBuilder builder) {
        this.wizardContext = wizardContext;
        this.builder = builder;
        jspsTree = new Tree();
        jspsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        temp.mkdirs();
        JScrollPane typesScrollPane = ScrollPaneFactory.createScrollPane(jspsTree);

        jspsPanel.add(typesScrollPane, "archetypes");
        jspsTree.setRootVisible(false);
        jspsTree.setShowsRootHandles(true);
        jspsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        final File liferayHomeDir = new File(wizardContext.getProject().getBasePath(), LiferayWorkspaceUtil.getHomeDir(wizardContext.getProject().getBasePath()));

        if (!liferayHomeDir.exists()) {
            osgiHost.addItem("unable to get liferay bundle");

            DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode("unable to get liferay bundle", true);

            root.add(node);

            final TreeModel model = new DefaultTreeModel(root);

            jspsTree.setModel(model);

            return;
        }

        List<String> bundles = ServerUtil.getModuleFileListFrom70Server(liferayHomeDir);

        for (String bundle : bundles) {
            osgiHost.addItem(bundle);
        }

        osgiHost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);

                ServerUtil.getModuleFileFrom70Server(liferayHomeDir, osgiHost.getSelectedItem().toString(), temp);

                File currentOsgiBundle = new File(temp, osgiHost.getSelectedItem().toString());

                if (currentOsgiBundle.exists()) {
                    try (JarFile jar = new JarFile(currentOsgiBundle)) {
                        Enumeration<JarEntry> enu = jar.entries();

                        while (enu.hasMoreElements()) {
                            JarEntry entry = enu.nextElement();
                            String name = entry.getName();

                            if ((name.startsWith("META-INF/resources/") &&
                                    (name.endsWith(".jsp") || name.endsWith(".jspf"))) ||
                                    name.equals("portlet.properties") || name.equals("resource-actions/default.xml")) {
                                DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(name, true);
                                root.add(node1);
                            }
                        }
                    } catch (IOException e1) {
                    }
                }

                jspsTree.setModel(new DefaultTreeModel(root));
            }
        });
    }


    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public void updateDataModel() {
        builder.setOsgiHost(getOsgiHost());
        builder.setFiles(getSelectedJsps());
        String[] bsnAndVerion = getBsnAndVersion(getOsgiHost());
        builder.setBsnName(bsnAndVerion[0]);
        builder.setVersion(bsnAndVerion[1]);
    }

    public String[] getSelectedJsps() {
        TreePath[] paths = jspsTree.getSelectionPaths();
        String[] jsps = new String[paths.length];

        for (int i = 0; i < paths.length; i++) {
            jsps[i] = paths[i].getLastPathComponent().toString();
        }
        return jsps;
    }

    public String getOsgiHost() {
        return osgiHost.getSelectedItem().toString();
    }

    public String[] getBsnAndVersion(String hostBundleName) {
        final File tempBundle = new File(temp, hostBundleName.substring(0, hostBundleName.lastIndexOf(".jar")));

        if (!tempBundle.exists()) {
            File hostBundle = new File(temp, hostBundleName);

            try {
                ZipUtil.unzip(hostBundle, tempBundle);
            } catch (IOException e) {
            }
        }

        String bundleSymbolicName = "";
        String version = "";

        if (tempBundle.exists()) {
            final File file = new File(new File(tempBundle, "META-INF"), "MANIFEST.MF");
            final String[] contents = FileUtil.readLinesFromFile(file);

            for (String content : contents) {
                if (content.contains("Bundle-SymbolicName:")) {
                    bundleSymbolicName = content.substring(
                            content.indexOf("Bundle-SymbolicName:") + "Bundle-SymbolicName:".length());
                }

                if (content.contains("Bundle-Version:")) {
                    version =
                            content.substring(content.indexOf("Bundle-Version:") + "Bundle-Version:".length()).trim();
                }
            }
        }

        return new String[]{bundleSymbolicName, version};
    }

}
