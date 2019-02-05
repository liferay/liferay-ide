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

package com.liferay.ide.upgrade.plan.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Gregory Amerson
 */
public abstract class BaseUpgradeTaskStep implements UpgradeTaskStep {

	@Activate
	public void activate(ComponentContext componentContext) {
		Dictionary<String, Object> properties = componentContext.getProperties();

		_description = getProperty(properties, "description");
		_id = getProperty(properties, "id");
		_imagePath = getProperty(properties, "imagePath");
		_requirement = getProperty(properties, "requirement");
		_taskId = getProperty(properties, "taskId");
		_title = getProperty(properties, "title");
		_url = getProperty(properties, "url");

		_lookupActions(componentContext);
	}

	@Override
	public List<UpgradeTaskStepAction> getActions() {
		return Collections.unmodifiableList(_upgradeTaskStepActions);
	}

	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String getImagePath() {
		return _imagePath;
	}

	@Override
	public UpgradeTaskStepRequirement getRequirement() {
		return UpgradeTaskStepRequirement.valueOf(UpgradeTaskStepRequirement.class, _requirement.toUpperCase());
	}

	@Override
	public UpgradeTaskStepStatus getStatus() {
		return _upgradeTaskStepStatus;
	}

	@Override
	public String getTaskId() {
		return _taskId;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public String getUrl() {
		return _url;
	}

	@Override
	public String toString() {
		return getId();
	}

	private void _lookupActions(ComponentContext componentContext) {
		BundleContext bundleContext = componentContext.getBundleContext();

		try {
			Collection<ServiceReference<UpgradeTaskStepAction>> upgradeTaskStepActionServiceReferences =
				bundleContext.getServiceReferences(UpgradeTaskStepAction.class, "(stepId=" + _id + ")");

			Stream<ServiceReference<UpgradeTaskStepAction>> stream = upgradeTaskStepActionServiceReferences.stream();

			_upgradeTaskStepActions = stream.map(
				bundleContext::getService
			).filter(
				Objects::nonNull
			).collect(
				Collectors.toList()
			);
		}
		catch (InvalidSyntaxException ise) {
		}
	}

	private String _description;
	private String _id;
	private String _imagePath;
	private String _requirement;
	private String _taskId;
	private String _title;
	private List<UpgradeTaskStepAction> _upgradeTaskStepActions;
	private UpgradeTaskStepStatus _upgradeTaskStepStatus = UpgradeTaskStepStatus.INCOMPLETE;
	private String _url;

}