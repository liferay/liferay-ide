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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 */
public class HookClasspathContainer extends PluginClasspathContainer {

	public static final String SEGMENT_PATH = "hook";

	public HookClasspathContainer(
		IPath containerPath, IJavaProject project, IPath portalDir, String javadocURL, IPath sourceURL) {

		super(containerPath, project, portalDir, javadocURL, sourceURL);
	}

	public String getDescription() {
		return Msgs.liferayHookPluginAPI;
	}

	@Override
	protected String[] getPortalJars() {
		return new String[] {
			"commons-logging.jar", "log4j.jar", "util-bridges.jar", "util-java.jar", "util-taglib.jar", "struts.jar",
			"struts-el.jar"
		};
	}

	private static class Msgs extends NLS {

		public static String liferayHookPluginAPI;

		static {
			initializeMessages(HookClasspathContainer.class.getName(), Msgs.class);
		}

	}

}