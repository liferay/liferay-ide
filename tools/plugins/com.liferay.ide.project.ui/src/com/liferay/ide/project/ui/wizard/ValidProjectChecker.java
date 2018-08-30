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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.action.NewPluginProjectDropDownAction;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Cindy Li
 * @author Kuo Zhang
 */
public class ValidProjectChecker {

	public ValidProjectChecker(String wizardId) {
		this.wizardId = wizardId;

		init();
	}

	public void checkValidProjectTypes() {
		IProject[] projects = CoreUtil.getAllProjects();
		boolean hasValidProjectTypes = false;

		boolean hasJsfFacet = false;

		for (IProject project : projects) {
			if (ProjectUtil.isLiferayFacetedProject(project)) {
				Set<IProjectFacetVersion> facets = ProjectUtil.getFacetedProject(project).getProjectFacets();

				if ((validProjectTypes != null) && (facets != null)) {
					String[] validTypes = validProjectTypes.split(StringPool.COMMA);

					for (String validProjectType : validTypes) {
						for (IProjectFacetVersion facet : facets) {
							String id = facet.getProjectFacet().getId();

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
			final Shell activeShell = Display.getDefault().getActiveShell();

			Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
				activeShell, NLS.bind(Msgs.newElement, wizardName),
				NLS.bind(Msgs.noSuitableLiferayProjects, wizardName));

			if (openNewLiferayProjectWizard) {
				final Action defaultAction = NewPluginProjectDropDownAction.getPluginProjectAction();

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
		if ((wizardId != null) && wizardId.equals("com.liferay.ide.eclipse.portlet.jsf.ui.wizard.portlet")) {
			setJsfPortlet(true);
		}

		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
			PlatformUI.PLUGIN_ID, _TAG_NEW_WIZARDS);

		if (extensionPoint != null) {
			IConfigurationElement[] elements = extensionPoint.getConfigurationElements();

			for (IConfigurationElement element : elements) {
				if (element.getName().equals(_TAG_WIZARD) && element.getAttribute(_ATT_ID).equals(wizardId)) {

					// getValidProjectTypesFromConfig( element )!=null &&
					// isLiferayArtifactWizard(element,
					// "liferay_artifact")

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