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

package com.liferay.ide.maven.ui;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.ILiferayProjectAdapter;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.maven.core.FacetedMavenProject;
import com.liferay.ide.maven.core.IMavenProject;
import com.liferay.ide.project.core.IProjectBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 *
 *         This class is only to override the default behavior of
 *         MavenProjectBuilder. If portal version is less than 6.2 then we need
 *         to do a launchConfig for building services
 */
public class MavenProjectAdapter implements ILiferayProjectAdapter {

	public <T> T adapt(ILiferayProject liferayProject, Class<T> adapterType) {
		if (liferayProject instanceof IMavenProject && IProjectBuilder.class.equals(adapterType)) {

			// only use this builder for versions of Liferay less than 6.2

			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			if (portal != null) {
				String version = portal.getVersion();

				if (!CoreUtil.isNullOrEmpty(version)) {

					// we only need to match the first 2 characters

					Matcher matcher = _majorMinor.matcher(version);

					String matchedVersion = null;

					if (matcher.find() && (matcher.groupCount() == 2)) {
						matchedVersion = matcher.group(1) + "." + matcher.group(2) + ".0";
					}

					Version portalVersion = Version.parseVersion((matchedVersion != null) ? matchedVersion : version);

					if (CoreUtil.compareVersions(portalVersion, ILiferayConstants.V620) < 0) {
						MavenUIProjectBuilder builder = new MavenUIProjectBuilder((FacetedMavenProject)liferayProject);

						return adapterType.cast(builder);
					}
				}
			}
		}

		return null;
	}

	private static Pattern _majorMinor = Pattern.compile("([0-9]+)\\.([0-9]+).*");

}