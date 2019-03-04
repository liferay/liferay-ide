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

package com.liferay.ide.upgrade.problems.core.internal.tasks;

import com.liferay.ide.upgrade.plan.core.BaseUpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;
import com.liferay.ide.upgrade.problems.core.FileMigrator;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Terry Jia
 */
@Component(
	property = {
		"id=auto_correct_problems", "imagePath=icons/auto_correct_problems.png", "requirement=recommended", "order=2",
		"taskId=find_upgrade_problems", "title=Auto Correct Upgrade Problems"
	},
	scope = ServiceScope.PROTOTYPE, service = UpgradeTaskStep.class
)
public class AutoCorrectUpgradeProblemsStep extends BaseUpgradeTaskStep {

	@Override
	public String getDescription() {
		if (_description == null) {
			Bundle bundle = FrameworkUtil.getBundle(AutoCorrectUpgradeProblemsStep.class);

			BundleContext bundleContext = bundle.getBundleContext();

			List<FileMigrator> fileMigrators = ServicesLookup.getOrderedServices(
				bundleContext, FileMigrator.class, "(auto.correct=*)");

			StringBuffer sb = new StringBuffer();

			sb.append(
				"Performing this step will correct some easy upgrade problems automatically. You can edit the rest " +
					"problems manually according to the breaking changes provided.\n");

			sb.append("The following problems could be auto corrected.\n");

			for (FileMigrator fileMigrator : fileMigrators) {
				Class<?> clazz = fileMigrator.getClass();

				sb.append(clazz.getName());

				sb.append("\n");
			}

			_description = sb.toString();
		}

		return _description;
	}

	private String _description;

}