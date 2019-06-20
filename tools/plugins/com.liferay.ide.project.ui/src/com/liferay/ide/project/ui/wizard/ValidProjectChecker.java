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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.action.NewPluginProjectDropDownAction;
import com.liferay.ide.ui.util.UIUtil;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Cindy Li
 * @author Kuo Zhang
 * @author Terry Jia
 */
public class ValidProjectChecker {

	public ValidProjectChecker(String wizardId) {
		this.wizardId = wizardId;

		init();
	}

	public void checkValidHookProjectTypes() {
		IProject[] projects = CoreUtil.getAllProjects();
		boolean hasValidProjectTypes = false;

		for (IProject project : projects) {
			IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

			if (bundleProject != null) {
				IFile liferayHookXml = project.getFile("src/main/webapp/WEB-INF/liferay-hook.xml");

				if (liferayHookXml.exists()) {
					hasValidProjectTypes = true;

					break;
				}
			}

			if (ProjectUtil.isLiferayFacetedProject(project)) {
				IFacetedProject facetedProject = ProjectUtil.getFacetedProject(project);

				Set<IProjectFacetVersion> facets = facetedProject.getProjectFacets();

				if ((validProjectTypes != null) && (facets != null)) {
					String[] validTypes = validProjectTypes.split(StringPool.COMMA);

					for (String validProjectType : validTypes) {
						for (IProjectFacetVersion facet : facets) {
							IProjectFacet projectFacet = facet.getProjectFacet();

							String id = projectFacet.getId();

							if (id.startsWith("liferay.") && id.equals("liferay." + validProjectType)) {
								hasValidProjectTypes = true;
							}
						}
					}
				}
			}
		}

		if (!hasValidProjectTypes) {
			Shell activeShell = UIUtil.getActiveShell();

			Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
				activeShell, NLS.bind(Msgs.newElement, wizardName),
				NLS.bind(Msgs.noSuitableLiferayProjects, wizardName));

			if (openNewLiferayProjectWizard) {
				Action defaultAction = null;

				if (LiferayWorkspaceUtil.hasWorkspace()) {
					defaultAction = NewPluginProjectDropDownAction.getDefaultAction();
				}
				else {
					defaultAction = NewPluginProjectDropDownAction.getPluginProjectAction();
				}

				if (defaultAction != null) {
					defaultAction.run();

					checkValidProjectTypes();
				}
			}
		}
	}

	public void checkValidProjectTypes() {
		IProject[] projects = CoreUtil.getAllProjects();
		boolean hasValidProjectTypes = false;

		boolean hasJsfFacet = false;

		for (IProject project : projects) {
			if (ProjectUtil.isLiferayFacetedProject(project)) {
				IFacetedProject facetedProject = ProjectUtil.getFacetedProject(project);

				Set<IProjectFacetVersion> facets = facetedProject.getProjectFacets();

				if ((validProjectTypes != null) && (facets != null)) {
					String[] validTypes = validProjectTypes.split(StringPool.COMMA);

					for (String validProjectType : validTypes) {
						for (IProjectFacetVersion facet : facets) {
							IProjectFacet projectFacet = facet.getProjectFacet();

							String id = projectFacet.getId();

							if (jsfPortlet && id.equals("jst.jsf")) {
								hasJsfFacet = true;
							}

							if (id.startsWith("liferay.") && id.equals("liferay." + validProjectType)) {
								hasValidProjectTypes = true;
							}
						}
					}
				}
			}
		}

		if (jsfPortlet) {
			hasValidProjectTypes = hasJsfFacet && hasValidProjectTypes;
		}

		if (!hasValidProjectTypes) {
			Shell activeShell = UIUtil.getActiveShell();

			Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
				activeShell, NLS.bind(Msgs.newElement, wizardName),
				NLS.bind(Msgs.noSuitableLiferayProjects, wizardName));

			if (openNewLiferayProjectWizard) {
				Action defaultAction = NewPluginProjectDropDownAction.getPluginProjectAction();

				if (defaultAction != null) {
					defaultAction.run();

					checkValidProjectTypes();
				}
			}
		}
	}

	public void setJsfPortlet(boolean jsfPortlet) {
		this.jsfPortlet = jsfPortlet;
	}

	public void setValidProjectTypes(String validProjectTypes) {
		this.validProjectTypes = validProjectTypes;
	}

	protected void init() {
		if ("com.liferay.ide.eclipse.portlet.jsf.ui.wizard.portlet".equals(wizardId)) {
			setJsfPortlet(true);
		}

		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

		IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint(PlatformUI.PLUGIN_ID, _TAG_NEW_WIZARDS);

		if (extensionPoint != null) {
			IConfigurationElement[] elements = extensionPoint.getConfigurationElements();

			for (IConfigurationElement element : elements) {
				if (_TAG_WIZARD.equals(element.getName()) && wizardId.equals(element.getAttribute(_ATT_ID))) {
					setValidProjectTypes(_getValidProjectTypesFromConfig(element));
					wizardName = element.getAttribute(_ATT_NAME);

					break;
				}
			}
		}
	}

	protected boolean jsfPortlet = false;
	protected String validProjectTypes = null;
	protected String wizardId = null;
	protected String wizardName = null;

	private String _getValidProjectTypesFromConfig(IConfigurationElement config) {
		IConfigurationElement[] classElements = config.getChildren();

		if (ListUtil.isNotEmpty(classElements)) {
			for (IConfigurationElement classElement : classElements) {
				IConfigurationElement[] paramElements = classElement.getChildren(_TAG_PARAMETER);

				for (IConfigurationElement paramElement : paramElements) {
					if (_ATT_VALID_PROJECT_TYPES.equals(paramElement.getAttribute(_ATT_NAME))) {
						return paramElement.getAttribute(_TAG_VALUE);
					}
				}
			}
		}

		return null;
	}

	private static final String _ATT_ID = "id";

	private static final String _ATT_NAME = "name";

	private static final String _ATT_VALID_PROJECT_TYPES = "validProjectTypes";

	private static final String _TAG_NEW_WIZARDS = "newWizards";

	private static final String _TAG_PARAMETER = "parameter";

	private static final String _TAG_VALUE = "value";

	private static final String _TAG_WIZARD = "wizard";

	private static class Msgs extends NLS {

		public static String newElement;
		public static String noSuitableLiferayProjects;

		static {
			initializeMessages(ValidProjectChecker.class.getName(), Msgs.class);
		}

	}

}