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

import static java.util.Collections.list;

import aQute.bnd.osgi.Domain;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.Tree;

import com.liferay.ide.idea.ui.LiferayIdeaUI;
import com.liferay.ide.idea.util.CoreUtil;
import com.liferay.ide.idea.util.LiferayWorkspaceUtil;
import com.liferay.ide.idea.util.ServerUtil;
import com.liferay.ide.idea.util.ZipUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Terry Jia
 */
public class LiferayModuleFragmentWizardStep extends ModuleWizardStep {

	public LiferayModuleFragmentWizardStep(WizardContext wizardContext, LiferayModuleFragmentBuilder builder) {
		_builder = builder;

		_jspsTree = new Tree();

		_jspsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
		_jspsTree.setRootVisible(false);
		_jspsTree.setShowsRootHandles(true);

		TreeSelectionModel treeSelectionModel = _jspsTree.getSelectionModel();

		treeSelectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		JScrollPane typesScrollPane = ScrollPaneFactory.createScrollPane(_jspsTree);

		_jspsPanel.add(typesScrollPane, "archetypes");

		Project project = wizardContext.getProject();

		String homeDir = LiferayWorkspaceUtil.getHomeDir(project.getBasePath());

		File liferayHomeDir = new File(project.getBasePath(), homeDir);

		if (!liferayHomeDir.exists()) {
			_fragmentHost.addItem("Unable to get Liferay bundle");

			DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);
			DefaultMutableTreeNode node = new DefaultMutableTreeNode("Unable to get Liferay bundle", true);

			root.add(node);

			TreeModel model = new DefaultTreeModel(root);

			_jspsTree.setModel(model);

			return;
		}

		List<String> bundles = ServerUtil.getModuleFileListFrom70Server(liferayHomeDir);

		Stream<String> steam = bundles.stream();

		steam.forEach(b -> _fragmentHost.addItem(b));

		_fragmentHost.addActionListener(
			new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);

					Object selectedFragment = _fragmentHost.getSelectedItem();

					File currentOsgiBundle = ServerUtil.getModuleFileFrom70Server(
						liferayHomeDir, selectedFragment.toString(), LiferayIdeaUI.USER_BUNDLES_DIR);

					if (currentOsgiBundle.exists()) {
						try (JarFile jar = new JarFile(currentOsgiBundle)) {
							List jarEntries = Collections.list(jar.entries());

							Stream<JarEntry> stream = jarEntries.stream();

							stream.map(
								entry -> entry.getName()
							).filter(
								name -> _isInterestingName(name)
							).map(
								name -> new DefaultMutableTreeNode(name, true)
							).forEach(
								node -> root.add(node)
							);
						}
						catch (IOException ioe) {
						}
					}

					_jspsTree.setModel(new DefaultTreeModel(root));
				}

			});
	}

	public JComponent getComponent() {
		return _mainPanel;
	}

	public String getFragmentHost() {
		Object selectedFragment = _fragmentHost.getSelectedItem();

		return selectedFragment.toString();
	}

	public String[] getSelectedJsps() {
		TreePath[] paths = _jspsTree.getSelectionPaths();

		if (CoreUtil.isNullOrEmpty(paths)) {
			return null;
		}

		String[] jsps = new String[paths.length];

		for (int i = 0; i < paths.length; i++) {
			Object lastPathComponent = paths[i].getLastPathComponent();

			jsps[i] = lastPathComponent.toString();
		}

		return jsps;
	}

	@Override
	public void updateDataModel() {
		Domain domain = _getBsnAndVersion(getFragmentHost());

		_builder.setBsnName(domain.getBundleSymbolicName().getKey());
		_builder.setVersion(domain.getBundleVersion());

		_builder.setFragmentHost(getFragmentHost());
		_builder.setOverrideFiles(getSelectedJsps());
	}

	@Override
	public boolean validate() throws ConfigurationException {
		String validationTitle = "Validation Error";

		if (CoreUtil.isNullOrEmpty(getSelectedJsps())) {
			throw new ConfigurationException("At least select one jsp to override", validationTitle);
		}

		return true;
	}

	private Domain _getBsnAndVersion(String hostBundleName) {
		Domain retval = null;

		String child = hostBundleName.substring(0, hostBundleName.lastIndexOf(".jar"));

		File tempBundle = new File(LiferayIdeaUI.USER_BUNDLES_DIR, child);

		File hostBundle = new File(LiferayIdeaUI.USER_BUNDLES_DIR, hostBundleName);

		if (!tempBundle.exists()) {
			try {
				ZipUtil.unzip(hostBundle, tempBundle);
			}
			catch (IOException ioe) {
			}
		}

		if (tempBundle.exists()) {
			try {
				retval = Domain.domain(hostBundle);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return retval;
	}

	private boolean _isInterestingName(String name) {
		boolean jsp = false;

		if (name.endsWith(".jsp") || name.endsWith(".jspf")) {
			jsp = true;
		}

		if ((name.startsWith("META-INF/resources/") && jsp) || name.equals("portlet.properties") ||
			name.equals("resource-actions/default.xml")) {

			return true;
		}

		return false;
	}

	private LiferayModuleFragmentBuilder _builder;
	private JComboBox<String> _fragmentHost;
	private JPanel _jspsPanel;
	private Tree _jspsTree;
	private JPanel _mainPanel;

}