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

		_description = _getProperty(properties, "description");
		_id = _getProperty(properties, "id");
		_imagePath = _getProperty(properties, "imagePath");
		_requirement = _getProperty(properties, "requirement");
		_title = _getProperty(properties, "title");
		_url = _getProperty(properties, "url");

		_lookupCommands(componentContext);
	}

	@Override
	public List<UpgradeTaskStepCommand> getCommands() {
		return Collections.unmodifiableList(_upgradeTaskStepCommands);
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

	private String _getProperty(Dictionary<String, Object> properties, String key) {
		Object value = properties.get(key);

		if (value instanceof String) {
			return (String)value;
		}

		return null;
	}

	private void _lookupCommands(ComponentContext componentContext) {
		BundleContext bundleContext = componentContext.getBundleContext();

		try {
			Collection<ServiceReference<UpgradeTaskStepCommand>> upgradeTaskStepCommandServiceReferences =
				bundleContext.getServiceReferences(UpgradeTaskStepCommand.class, "(stepId=" + _id + ")");

			Stream<ServiceReference<UpgradeTaskStepCommand>> stream = upgradeTaskStepCommandServiceReferences.stream();

			_upgradeTaskStepCommands = stream.map(
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
	private String _title;
	private List<UpgradeTaskStepCommand> _upgradeTaskStepCommands;
	private UpgradeTaskStepStatus _upgradeTaskStepStatus = UpgradeTaskStepStatus.INCOMPLETE;
	private String _url;

}