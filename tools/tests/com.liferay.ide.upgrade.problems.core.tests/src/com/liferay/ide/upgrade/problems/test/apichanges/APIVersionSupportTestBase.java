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

package com.liferay.ide.upgrade.problems.test.apichanges;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Optional;
import java.util.stream.Collectors;

import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

import com.liferay.ide.upgrade.problems.core.FileMigrator;

/**
 * @author Seiphon Wang
 */
public abstract class APIVersionSupportTestBase extends APITestBase {

	@Override
	protected Filter getFilter() throws Exception {
		return context.createFilter("(component.name=" + getComponentName() + ")");
	}

	@Override
	protected ServiceReference<FileMigrator>[] filterForVersion(ServiceReference<FileMigrator>[] serviceReferences) {
		return Arrays.stream(
			serviceReferences
		).filter(
			ref -> {
				Dictionary<String, Object> serviceProperties = ref.getProperties();

				Version version = new Version(getVersion());

				return Optional.ofNullable(
					serviceProperties.get("version")
				).map(
					Object::toString
				).map(
					VersionRange::valueOf
				).filter(
					range -> range.includes(version)
				).isPresent();
			}
		).collect(
			Collectors.toList()
		).toArray(serviceReferences);
	}

	public abstract String getVersion();

}
