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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.upgrade.plan.core.IMemento;
import com.liferay.ide.upgrade.plan.core.NewUpgradePlanOp;
import com.liferay.ide.upgrade.plan.core.UpgradePlanCorePlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Terry Jia
 */
public class NameValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		UpgradePlanCorePlugin upgradePlanCorePlugin = UpgradePlanCorePlugin.getInstance();

		IPath stateLocation = upgradePlanCorePlugin.getStateLocation();

		IPath xmlFile = stateLocation.append("upgradePlanner.xml");

		File file = xmlFile.toFile();

		if (!file.exists()) {
			return retval;
		}

		try (InputStream inputStream = new FileInputStream(file)) {
			IMemento rootMemento = XMLMemento.loadMemento(inputStream);

			if (rootMemento == null) {
				return retval;
			}

			List<String> upgradePlanNames = Stream.of(
				rootMemento.getChildren("upgradePlan")
			).map(
				memento -> memento.getString("upgradePlanName")
			).collect(
				Collectors.toList()
			);

			NewUpgradePlanOp op = context(NewUpgradePlanOp.class);

			String name = get(op.getName());

			if (upgradePlanNames.contains(name)) {
				retval = Status.createErrorStatus("Upgrade plan name " + name + " already exist.");
			}
		}
		catch (IOException ioe) {
		}

		return retval;
	}

}