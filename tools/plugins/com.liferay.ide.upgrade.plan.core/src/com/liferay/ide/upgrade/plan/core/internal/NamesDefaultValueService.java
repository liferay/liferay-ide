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

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.upgrade.plan.core.IMemento;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DefaultValueService;

/**
 * @author Terry Jia
 */
public class NamesDefaultValueService extends DefaultValueService {

	@Override
	protected String compute() {
		String retval = "";

		UpgradePlanCorePlugin upgradePlanCorePlugin = UpgradePlanCorePlugin.getInstance();

		IPath stateLocation = upgradePlanCorePlugin.getStateLocation();

		IPath xmlFile = stateLocation.append("upgradePlanner.xml");

		File file = xmlFile.toFile();

		if (file.exists()) {
			try (InputStream inputStream = new FileInputStream(file)) {
				IMemento rootMemento = XMLMemento.loadMemento(inputStream);

				if (rootMemento != null) {
					List<String> names = Stream.of(
						rootMemento.getChildren("upgradePlan")
					).map(
						memento -> memento.getString("upgradePlanName")
					).collect(
						Collectors.toList()
					);

					if (!names.isEmpty()) {
						retval = names.get(0);
					}
				}
			}
			catch (IOException ioe) {
			}
		}

		return retval;
	}

}