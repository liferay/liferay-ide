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

package com.liferay.ide.upgrade.plan.ui.view.plan;

import com.liferay.ide.upgrade.plan.api.UpgradeTask;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Terry Jia
 */
public class TasksContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {

		// TODO need to get task list basing on one Upgrade Plan

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext context = bundle.getBundleContext();

		return _getUpgradeTasks(context).toArray();
	}

	private List<UpgradeTask> _getUpgradeTasks(BundleContext context) {
		List<UpgradeTask> upgradeTasks = new ArrayList<>();

		try {
			List<ServiceReference<UpgradeTask>> references = new ArrayList<>();

			references.addAll(context.getServiceReferences(UpgradeTask.class, null));

			for (ServiceReference<UpgradeTask> reference : references) {
				upgradeTasks.add(context.getService(reference));
			}

			upgradeTasks.sort(
				(task1, task2) -> {
					int priority1 = 0;

					try {
						priority1 = Integer.parseInt((String)task1.getProperty("task.priority"));
					}
					catch (NumberFormatException nfe) {
					}

					int priority2 = 0;

					try {
						priority2 = Integer.parseInt((String)task2.getProperty("task.priority"));
					}
					catch (NumberFormatException nfe) {
					}

					return priority2 - priority1;
				});
		}
		catch (InvalidSyntaxException ise) {
		}

		return upgradeTasks;
	}

}