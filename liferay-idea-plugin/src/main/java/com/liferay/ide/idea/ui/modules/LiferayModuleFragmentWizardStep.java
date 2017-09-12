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

package com.liferay.ide.idea.ui.modules;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;

import com.liferay.ide.idea.ui.LiferayIdeaUI;
import com.liferay.ide.idea.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.*;
import javax.swing.tree.*;

/**
 * @author Terry Jia
 */
public class LiferayModuleFragmentWizardStep extends ModuleWizardStep {

	public LiferayModuleFragmentWizardStep(WizardContext wizardContext, LiferayModuleFragmentBuilder builder) {
		this._builder = builder;
		_jspsTree = new Tree();
		_jspsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
		JScrollPane typesScrollPane = ScrollPaneFactory.createScrollPane(_jspsTree);

		_jspsPanel.add(typesScrollPane, "archetypes");
		_jspsTree.setRootVisible(false);
		_jspsTree.setShowsRootHandles(true);
		_jspsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		Project project = wizardContext.getProject();

		String homeDir = LiferayWorkspaceUtil.getHomeDir(project.getBasePath());

		File liferayHomeDir = new File(project.getBasePath(), homeDir);

		if (!liferayHomeDir.exists()) {
			_fragmentHost.addItem("unable to get liferay bundle");

			DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode("unable to get liferay bundle", true);

			root.add(node);

			TreeModel model = new DefaultTreeModel(root);

			_jspsTree.setModel(model);

			return;
		}

		List<String> bundles = ServerUtil.getModuleFileListFrom70Server(liferayHomeDir);

		for (String bundle : bundles) {
			_fragmentHost.addItem(bundle);
		}

		_fragmentHost.addActionListener(
			new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);

				ServerUtil.getModuleFileFrom70Server(
					liferayHomeDir, _fragmentHost.getSelectedItem().toString(), LiferayIdeaUI.USER_BUNDLES_DIR);

				File currentOsgiBundle = new File(
					LiferayIdeaUI.USER_BUNDLES_DIR, _fragmentHost.getSelectedItem().toString());

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
					}
					catch (IOException ioe) {
					}
				}

				_jspsTree.setModel(new DefaultTreeModel(root));
			}

		});
	}

	public String[] getBsnAndVersion(String hostBundleName) {
		String child = hostBundleName.substring(0, hostBundleName.lastIndexOf(".jar"));

		File tempBundle = new File(LiferayIdeaUI.USER_BUNDLES_DIR, child);

		if (!tempBundle.exists()) {
			File hostBundle = new File(LiferayIdeaUI.USER_BUNDLES_DIR, hostBundleName);

			try {
				ZipUtil.unzip(hostBundle, tempBundle);
			}
			catch (IOException ioe) {
			}
		}

		String bundleSymbolicName = "";
		String version = "";

		if (tempBundle.exists()) {
			File file = new File(new File(tempBundle, "META-INF"), "MANIFEST.MF");
			String[] contents = FileUtil.readLinesFromFile(file);

			for (String content : contents) {
				if (content.contains("Bundle-SymbolicName:")) {
					bundleSymbolicName = content.substring(
						content.indexOf("Bundle-SymbolicName:") + "Bundle-SymbolicName:".length());
				}

				if (content.contains("Bundle-Version:")) {
					version = content.substring(content.indexOf("Bundle-Version:") + "Bundle-Version:".length()).trim();
				}
			}
		}

		return new String[] {bundleSymbolicName, version};
	}

	public JComponent getComponent() {
		return _mainPanel;
	}

	public String getFragmentHost() {
		return _fragmentHost.getSelectedItem().toString();
	}

	public String[] getSelectedJsps() {
		TreePath[] paths = _jspsTree.getSelectionPaths();

		if (CoreUtil.isNullOrEmpty(paths)) {
			return null;
		}

		String[] jsps = new String[paths.length];

		for (int i = 0; i < paths.length; i++) {
			jsps[i] = paths[i].getLastPathComponent().toString();
		}

		return jsps;
	}

	@Override
	public void updateDataModel() {
		String[] bsnAndVerion = getBsnAndVersion(getFragmentHost());

		_builder.setBsnName(bsnAndVerion[0]);
		_builder.setFragmentHost(getFragmentHost());
		_builder.setOverrideFiles(getSelectedJsps());
		_builder.setVersion(bsnAndVerion[1]);
	}

	@Override
	public boolean validate() throws ConfigurationException {
		String validationTitle = "Validation Error";

		if (CoreUtil.isNullOrEmpty(getSelectedJsps())) {
			throw new ConfigurationException("At least select one jsp to override", validationTitle);
		}

		return true;
	}

	private LiferayModuleFragmentBuilder _builder;
	private JComboBox<String> _fragmentHost;
	private JPanel _jspsPanel;
	private Tree _jspsTree;
	private JPanel _mainPanel;

}