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

package com.liferay.ide.upgrade.plan.base;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.upgrade.plan.api.Summary;
import com.liferay.ide.upgrade.plan.api.UpgradeTask;
import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Terry Jia
 */
public abstract class AbstractUpgradeTask implements UpgradeTask, Summary {

	@Activate
	public void activate(Map<String, Object> properties) {
		_properties = properties;
	}

	@Override
	public String doDetail() {
		String url = (String)_properties.get("task.md");

		if (CoreUtil.isNotNullOrEmpty(url)) {
			return url;
		}
		else {
			return (String)_properties.get("task.description");
		}
	}

	@Override
	public String doLabel() {
		return (String)_properties.get("task.title");
	}

	public Object getProperty(String key) {
		return _properties.get(key);
	}

	public List<UpgradeTaskStep> getSteps() {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext context = bundle.getBundleContext();

		List<UpgradeTaskStep> steps = new ArrayList<>();

		try {

			// TODO also need to filter the steps by Upgrade Plan

			String taskTitle = (String)_properties.get("task.title");

			Collection<ServiceReference<UpgradeTaskStep>> references = context.getServiceReferences(
				UpgradeTaskStep.class, "(task.title=" + taskTitle + ")");

			for (ServiceReference<UpgradeTaskStep> reference : references) {
				UpgradeTaskStep step = context.getService(reference);

				steps.add(step);
			}

			steps.sort(
				(step1, step2) -> {
					int priority1 = 0;

					try {
						priority1 = Integer.parseInt((String)step1.getProperty("step.priority"));
					}
					catch (NumberFormatException nfe) {
					}

					int priority2 = 0;

					try {
						priority2 = Integer.parseInt((String)step2.getProperty("step.priority"));
					}
					catch (NumberFormatException nfe) {
					}

					return priority2 - priority1;
				});
		}
		catch (InvalidSyntaxException ise) {
		}

		return steps;
	}

	private Map<String, Object> _properties;

}