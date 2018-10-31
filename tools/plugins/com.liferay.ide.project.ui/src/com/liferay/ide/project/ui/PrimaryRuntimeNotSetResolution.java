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

package com.liferay.ide.project.ui;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jst.server.core.FacetUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class PrimaryRuntimeNotSetResolution implements IMarkerResolution {

	public static final String TARGETED_RUNTIMES_PROPERTY_PAGE_ID =
		"org.eclipse.wst.common.project.facet.ui.RuntimesPropertyPage";

	public String getLabel() {
		return Msgs.setPrimaryRuntimeForProject;
	}

	/**
	 * IDE-1179, Quick fix for the project of which primary runtime is not set.
	 */
	public void run(IMarker marker) {
		if (marker.getResource() instanceof IProject) {
			final IProject proj = (IProject)marker.getResource();

			final IFacetedProject fproj = ProjectUtil.getFacetedProject(proj);

			/**
			 * Let users set a Liferay server runtime when there is no available one.
			 */
			Set<org.eclipse.wst.server.core.IRuntime> runtimes = ServerUtil.getAvailableLiferayRuntimes();

			if (runtimes.isEmpty()) {
				boolean openNewRuntimeWizard = MessageDialog.openQuestion(null, null, Msgs.noLiferayRuntimeAvailable);

				if (openNewRuntimeWizard) {
					ServerUIUtil.showNewRuntimeWizard(null, null, null, "com.liferay.");
				}
			}

			/**
			 * Let users confirm when there is only one available Liferay runtime.
			 *
			 * If the previous judgment block is executed, the size of available targeted
			 * runtimes will increase to 1.
			 */
			if (runtimes.size() == 1) {
				final Set<IRuntime> availableFacetRuntimes = _convertToFacetRuntimes(
					ServerUtil.getAvailableLiferayRuntimes());

				String runtimeName = ((IRuntime)availableFacetRuntimes.toArray()[0]).getName();

				boolean setAsPrimary = MessageDialog.openQuestion(
					null, null, NLS.bind(Msgs.setOnlyRuntimeAsPrimary, runtimeName));

				if (setAsPrimary) {
					try {
						fproj.setTargetedRuntimes(availableFacetRuntimes, null);
						fproj.setPrimaryRuntime((IRuntime)availableFacetRuntimes.toArray()[0], null);
					}
					catch (CoreException ce) {
						ProjectUI.logError(ce);
					}
				}
			}

			/**
			 * Open the "Targeted Runtimes" property page and let users set a runtime as the
			 * primary one when there are multiple Liferay runtimes available.
			 */
			if (runtimes.size() > 1) {
				boolean openRuntimesProperty = MessageDialog.openQuestion(null, null, Msgs.multipleAvailableRuntimes);

				if (openRuntimesProperty) {
					PropertyDialog dialog = PropertyDialog.createDialogOn(
						null, TARGETED_RUNTIMES_PROPERTY_PAGE_ID, proj);

					dialog.open();
				}
			}
		}
	}

	private Set<IRuntime> _convertToFacetRuntimes(Set<org.eclipse.wst.server.core.IRuntime> serverRuntimes) {
		Set<IRuntime> facetRuntimes = new HashSet<>();

		Iterator<org.eclipse.wst.server.core.IRuntime> it = serverRuntimes.iterator();

		while (it.hasNext()) {
			facetRuntimes.add(FacetUtil.getRuntime(it.next()));
		}

		return facetRuntimes;
	}

	private static class Msgs extends NLS {

		public static String multipleAvailableRuntimes;
		public static String noLiferayRuntimeAvailable;
		public static String setOnlyRuntimeAsPrimary;
		public static String setPrimaryRuntimeForProject;

		static {
			initializeMessages(PrimaryRuntimeNotSetResolution.class.getName(), Msgs.class);
		}

	}

}