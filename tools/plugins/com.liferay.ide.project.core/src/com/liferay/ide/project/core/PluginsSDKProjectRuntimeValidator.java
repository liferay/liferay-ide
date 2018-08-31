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

package com.liferay.ide.project.core;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectValidator;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;

/**
 * @author Kuo Zhang
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class PluginsSDKProjectRuntimeValidator implements IFacetedProjectValidator {

	public static final String ID_PLUGINS_SDK_NOT_SET = "plugins-sdk-not-set";

	public static final String ID_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME = "primary-runtime-not-liferay-runtime";

	public static final String ID_PRIMARY_RUNTIME_NOT_SET = "primary-runtime-not-set";

	public static final String LOCATION_TARGETED_RUNTIMES = "Targeted Runtimes";

	public static final String LOCATION_TARGETED_SDK = "Targeted SDK";

	public static final String MSG_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME = Msgs.primaryRuntimeNotLiferayRuntime;

	public static final String MSG_PRIMARY_RUNTIME_NOT_SET = Msgs.primaryRuntimeNotSet;

	public void validate(IFacetedProject fproj) throws CoreException {
		IProject project = fproj.getProject();

		if (!ProjectUtil.isLiferayFacetedProject(project)) {
			return;
		}

		_clearMarkers(project);

		if (SDKUtil.isSDKProject(fproj.getProject())) {
			IJavaProject javaProject = JavaCore.create(project);

			if (JavaProject.hasJavaNature(project)) {
				for (IClasspathEntry entry : javaProject.getRawClasspath()) {
					IPath path = entry.getPath();

					String segment = path.segment(0);

					if ((entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) &&
						segment.equals(SDKClasspathContainer.ID)) {

						return;
					}
				}
			}

			if (fproj.getPrimaryRuntime() == null) {
				_setMarker(
					project, ProjectCore.LIFERAY_PROJECT_MARKER_TYPE, IMarker.SEVERITY_ERROR,
					MSG_PRIMARY_RUNTIME_NOT_SET, LOCATION_TARGETED_RUNTIMES, ID_PRIMARY_RUNTIME_NOT_SET);
			}
			else {
				if (!ServerUtil.isLiferayRuntime((BridgedRuntime)fproj.getPrimaryRuntime())) {
					_setMarker(
						project, ProjectCore.LIFERAY_PROJECT_MARKER_TYPE, IMarker.SEVERITY_ERROR,
						MSG_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME, LOCATION_TARGETED_RUNTIMES,
						ID_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME);
				}
			}
		}
		else if (!ProjectUtil.isMavenProject(project)) {
			_setMarker(
				project, ProjectCore.LIFERAY_PROJECT_MARKER_TYPE, IMarker.SEVERITY_ERROR, Msgs.pluginSDKNotSet,
				LOCATION_TARGETED_SDK, ID_PLUGINS_SDK_NOT_SET);
		}
	}

	private void _clearMarkers(IProject project) {
		if (!project.isOpen()) {
			return;
		}

		try {
			IMarker[] markers = project.findMarkers(
				ProjectCore.LIFERAY_PROJECT_MARKER_TYPE, true, IResource.DEPTH_INFINITE);

			for (IMarker marker : markers) {
				for (String id : _getMarkerSourceIds()) {
					if (id.equals(marker.getAttribute(IMarker.SOURCE_ID))) {
						marker.delete();

						break;
					}
				}
			}
		}
		catch (CoreException ce) {
			ProjectCore.logError(ce);
		}
	}

	private String[] _getMarkerSourceIds() {
		return new String[] {
			ID_PRIMARY_RUNTIME_NOT_LIFERAY_RUNTIME, ID_PRIMARY_RUNTIME_NOT_SET, ID_PLUGINS_SDK_NOT_SET
		};
	}

	private void _setMarker(
			IProject proj, String markerType, int markerSeverity, String markerMsg, String markerLocation,
			String markerSourceId)
		throws CoreException {

		IMarker marker = proj.createMarker(markerType);

		marker.setAttribute(IMarker.SEVERITY, markerSeverity);
		marker.setAttribute(IMarker.MESSAGE, markerMsg);
		marker.setAttribute(IMarker.LOCATION, markerLocation);
		marker.setAttribute(IMarker.SOURCE_ID, markerSourceId);
	}

	private static class Msgs extends NLS {

		public static String pluginSDKNotSet;
		public static String primaryRuntimeNotLiferayRuntime;
		public static String primaryRuntimeNotSet;

		static {
			initializeMessages(PluginsSDKProjectRuntimeValidator.class.getName(), Msgs.class);
		}

	}

}