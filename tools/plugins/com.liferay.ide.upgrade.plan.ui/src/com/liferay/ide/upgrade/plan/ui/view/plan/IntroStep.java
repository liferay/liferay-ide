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
import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Terry Jia
 */
public class IntroStep implements UpgradeTaskStep {

	public IntroStep(UpgradeTask task) {
		_properties = new HashMap<>();

		_properties.put("step.description", (String)task.getProperty("task.description"));
		_properties.put("step.md", (String)task.getProperty("task.md"));
		_properties.put("step.title", "Introduction");
		_properties.put("step.url", (String)task.getProperty("task.url"));
	}

	@Override
	public IStatus execute(IProgressMonitor progressMonitor) {
		return Status.OK_STATUS;
	}

	@Override
	public Object getProperty(String key) {
		return _properties.get(key);
	}

	private Map<String, String> _properties;

}