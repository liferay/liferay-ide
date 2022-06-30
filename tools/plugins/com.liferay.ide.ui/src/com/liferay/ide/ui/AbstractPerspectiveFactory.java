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

package com.liferay.ide.ui;

import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * @author Lovett Li
 */
@SuppressWarnings("deprecation")
public abstract class AbstractPerspectiveFactory implements IPerspectiveFactory {

	public static final String ANT_VIEW_ID = "org.eclipse.ant.ui.views.AntView";

	public static final String ID_CONSOLE_VIEW = "org.eclipse.ui.console.ConsoleView";

	public static final String ID_DATA_VIEW = "org.eclipse.datatools.connectivity.DataSourceExplorerNavigator";

	public static final String ID_GRADLE_EXECUTIONS_VIEW = "org.eclipse.buildship.ui.views.executionview";

	public static final String ID_GRADLE_TASK_VIEW = "org.eclipse.buildship.ui.views.taskview";

	public static final String ID_J2EE_HIERARCHY_VIEW = "org.eclipse.ui.navigator.ProjectExplorer";

	public static final String ID_JAVADOC_VIEW = "org.eclipse.jdt.ui.JavadocView";

	public static final String ID_LIFERAY_UPGRADE_VIEW = "com.liferay.ide.project.ui.upgradeView";

	public static final String ID_MARKERS_VIEW = "org.eclipse.ui.views.AllMarkersView";

	public static final String ID_NEW_COMPONENT_WIZARD = "com.liferay.ide.project.ui.modules.NewLiferayComponentWizard";

	public static final String ID_NEW_HOOK_WIZARD = "com.liferay.ide.eclipse.portlet.ui.wizard.hook";

	public static final String ID_NEW_JSF_MODULE_WIZARD = "com.liferay.ide.project.ui.newJsfModuleProjectWizard";

	public static final String ID_NEW_JSF_PORTLET_WIZARD = "com.liferay.ide.eclipse.portlet.jsf.ui.wizard.portlet";

	public static final String ID_NEW_LAYOUT_TEMPLATE_WIZARD =
		"com.liferay.ide.eclipse.layouttpl.ui.wizard.layouttemplate";

	public static final String ID_NEW_MODULE_FRAGMENT_FILES_WIZARD =
		"com.liferay.ide.project.ui.newModuleFragmentFilesWizard";

	public static final String ID_NEW_MODULE_FRAGMENT_PROJECT_WIZARD =
		"com.liferay.ide.project.ui.newModuleFragmentWizard";

	public static final String ID_NEW_MODULE_PROJECT_WIZARD = "com.liferay.ide.project.ui.newModuleProjectWizard";

	public static final String ID_NEW_PLUGIN_PROJECT_WIZARD = "com.liferay.ide.project.ui.newPluginProjectWizard";

	public static final String ID_NEW_PLUGIN_PROJECT_WIZARD_EXISTING_SOURCE =
		"com.liferay.ide.eclipse.project.ui.newProjectWizardExistingSource";

	public static final String ID_NEW_PORTLET_WIZARD = "com.liferay.ide.eclipse.portlet.ui.wizard.portlet";

	public static final String ID_NEW_SERVICE_BUILDER_WIZARD =
		"com.liferay.ide.eclipse.portlet.ui.wizard.servicebuilder";

	public static final String ID_NEW_VAADIN_PORTLET_WIZARD =
		"com.liferay.ide.eclipse.portlet.vaadin.ui.wizard.portlet";

	public static final String ID_NEW_WORKSPACE_PROJECT_WIZARD =
		"com.liferay.ide.project.ui.workspace.newLiferayWorkspaceWizard";

	public static final String ID_PACKAGE_EXPLORER_VIEW = "org.eclipse.jdt.ui.PackageExplorer";

	public static final String ID_PROJECT_EXPLORER_VIEW = "org.eclipse.ui.navigator.ProjectExplorer";

	public static final String ID_SEARCH_VIEW = "org.eclipse.search.ui.views.SearchView";

	public static final String ID_SERVERS_VIEW = "org.eclipse.wst.server.ui.ServersView";

	public static final String ID_WST_SNIPPETS_VIEW = "org.eclipse.wst.common.snippets.internal.ui.SnippetsView";

	public void addViewIfExist(IPageLayout page, IFolderLayout bottomRight, String viewId) {
		IViewRegistry viewRegistry = UIUtil.getViewRegistry();

		IViewDescriptor dbView = viewRegistry.find(viewId);

		if (dbView != null) {
			bottomRight.addView(viewId);
		}
	}

	protected void addShortcuts(IPageLayout layout) {
		layout.addPerspectiveShortcut("org.eclipse.jst.j2ee.J2EEPerspective");
		layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaPerspective");
		layout.addPerspectiveShortcut("org.eclipse.debug.ui.DebugPerspective");
		layout.addShowViewShortcut(ANT_VIEW_ID);

		IPerspectiveRegistry perspectiveRegistry = UIUtil.getPerspectiveRegistry();

		IPerspectiveDescriptor desc = perspectiveRegistry.findPerspectiveWithId(
			"org.eclipse.team.cvs.ui.cvsPerspective");

		if (desc != null) {
			layout.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective");
		}

		desc = perspectiveRegistry.findPerspectiveWithId("org.tigris.subversion.subclipse.ui.svnPerspective");

		if (desc != null) {
			layout.addPerspectiveShortcut("org.tigris.subversion.subclipse.ui.svnPerspective");
		}

		desc = perspectiveRegistry.findPerspectiveWithId("org.eclipse.team.svn.ui.repository.RepositoryPerspective");

		if (desc != null) {
			layout.addPerspectiveShortcut("org.eclipse.team.svn.ui.repository.RepositoryPerspective");
		}
	}

	protected void setupActions(IPageLayout layout) {
		layout.addActionSet("org.eclipse.jdt.ui.JavaActionSet");
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(IDebugUIConstants.DEBUG_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		//		layout.addActionSet("com.liferay.ide.eclipse.ui.shortcuts.actionSet");
		layout.addActionSet("org.eclipse.wst.server.ui.internal.webbrowser.actionSet");
		layout.addActionSet("org.eclipse.wst.ws.explorer.explorer");

		layout.addShowViewShortcut(ID_J2EE_HIERARCHY_VIEW);
		layout.addShowViewShortcut(ID_SERVERS_VIEW);
		layout.addShowViewShortcut(ID_DATA_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		layout.addShowViewShortcut(ID_WST_SNIPPETS_VIEW);
		layout.addShowViewShortcut(ID_MARKERS_VIEW);
		layout.addShowViewShortcut(ID_SEARCH_VIEW);
		layout.addShowViewShortcut(ID_CONSOLE_VIEW);

		layout.addShowInPart(ID_J2EE_HIERARCHY_VIEW);
	}

}