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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Adapters;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public abstract class BaseUpgradeStep implements UpgradeStep, UpgradePlanAcessor {

	public void activate(UpgradePlanner upgradePlanner, ComponentContext componentContext) {
		_upgradePlanner = upgradePlanner;

		Dictionary<String, Object> properties = componentContext.getProperties();

		_description = getStringProperty(properties, "description");
		_id = getStringProperty(properties, "id");
		_imagePath = getStringProperty(properties, "imagePath", "icons/step_default.gif");
		_title = getStringProperty(properties, "title");
		_order = getDoubleProperty(properties, "order");
		_url = getStringProperty(properties, "url");
		_requirement = getStringProperty(properties, "requirement", "Optional");
		_categoryId = getStringProperty(properties, "categoryId");

		if (_description == null) {
			_description = _title;
		}

		_description = "<form>" + _description + "</form>";

		_lookupChildIds(componentContext);

		_parentId = getStringProperty(properties, "parentId");
	}

	@Override
	public boolean completed() {
		if (ListUtil.isNotEmpty(_childIds)) {
			for (String childId : _childIds) {
				UpgradeStep childUpgradeStep = ServicesLookup.getSingleService(
					UpgradeStep.class, "(id=" + childId + ")");

				if (!childUpgradeStep.completed()) {
					return false;
				}
			}

			return true;
		}

		if ((getStatus() == UpgradeStepStatus.COMPLETED) || (getStatus() == UpgradeStepStatus.SKIPPED)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean enabled() {
		if (_parentId == null) {
			return true;
		}

		UpgradeStep parentStep = getStep(_parentId);

		if (parentStep == null) {
			return true;
		}

		String[] siblingIds = parentStep.getChildIds();

		long count = Stream.of(
			siblingIds
		).map(
			this::getStep
		).filter(
			upgradeStep -> upgradeStep.getOrder() < getOrder()
		).filter(
			this::_isRequiredIncompleted
		).count();

		if ((count == 0) && parentStep.enabled()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean equals(Object object) {
		if ((object instanceof BaseUpgradeStep) == false) {
			return false;
		}

		BaseUpgradeStep baseUpgradeStep = Adapters.adapt(object, BaseUpgradeStep.class);

		if (baseUpgradeStep == null) {
			return false;
		}

		if (isEqualIgnoreCase(_description, baseUpgradeStep._description) &&
			isEqualIgnoreCase(_categoryId, baseUpgradeStep._categoryId) &&
			isEqualIgnoreCase(_requirement, baseUpgradeStep._requirement) &&
			isEqualIgnoreCase(_parentId, baseUpgradeStep._parentId) && isEqualIgnoreCase(_url, baseUpgradeStep._url) &&
			isEqual(_childIds, baseUpgradeStep._childIds) && isEqualIgnoreCase(_id, baseUpgradeStep._id) &&
			isEqualIgnoreCase(_imagePath, baseUpgradeStep._imagePath) && (_order == baseUpgradeStep._order) &&
			isEqualIgnoreCase(_title, baseUpgradeStep._title) && _status.equals(baseUpgradeStep._status)) {

			return true;
		}

		return false;
	}

	@Override
	public String getCategoryId() {
		return _categoryId;
	}

	public String[] getChildIds() {
		return _childIds;
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
		if (completed()) {
			return "icons/completed.gif";
		}
		else if (_status.equals(UpgradeStepStatus.FAILED)) {
			return "icons/failed.png";
		}

		return _imagePath;
	}

	@Override
	public double getOrder() {
		return _order;
	}

	public String getParentId() {
		return _parentId;
	}

	@Override
	public UpgradeStepRequirement getRequirement() {
		return UpgradeStepRequirement.valueOf(UpgradeStepRequirement.class, _requirement.toUpperCase());
	}

	@Override
	public UpgradeStepStatus getStatus() {
		return _status;
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
	public int hashCode() {
		int hash = 31;

		Double orderDouble = Double.valueOf(_order);

		hash = 31 * hash + orderDouble.hashCode();
		hash = 31 * hash + (_description != null ? _description.hashCode() : 0);
		hash = 31 * hash + (_categoryId != null ? _categoryId.hashCode() : 0);
		hash = 31 * hash + (_childIds != null ? Arrays.hashCode(_childIds) : 0);
		hash = 31 * hash + (_parentId != null ? _parentId.hashCode() : 0);
		hash = 31 * hash + (_requirement != null ? _requirement.hashCode() : 0);
		hash = 31 * hash + (_url != null ? _url.hashCode() : 0);
		hash = 31 * hash + (_imagePath != null ? _imagePath.hashCode() : 0);
		hash = 31 * hash + (_title != null ? _title.hashCode() : 0);
		hash = 31 * hash + (_id != null ? _id.hashCode() : 0);
		hash = 31 * hash + (_status != null ? _status.hashCode() : 0);

		return hash;
	}

	public void setStatus(UpgradeStepStatus status) {
		UpgradeStepStatusChangedEvent upgradeStepStatusChangedEvent = new UpgradeStepStatusChangedEvent(
			this, _status, status);

		_status = status;

		_upgradePlanner.dispatch(upgradeStepStatusChangedEvent);
	}

	@Override
	public String toString() {
		return getId();
	}

	private boolean _isRequiredIncompleted(UpgradeStep upgradeStep) {
		if (UpgradeStepRequirement.REQUIRED.equals(upgradeStep.getRequirement()) && !upgradeStep.completed()) {
			return true;
		}

		return false;
	}

	private void _lookupChildIds(ComponentContext componentContext) {
		if (_upgradePlanner == null) {
			_childIds = new String[0];

			return;
		}

		BundleContext bundleContext = componentContext.getBundleContext();

		String id = getId();

		List<UpgradeStep> children = ServicesLookup.getOrderedServices(
			bundleContext, UpgradeStep.class, "(parentId=" + id + ")");

		Stream<UpgradeStep> stream = children.stream();

		_childIds = stream.filter(
			child -> child.appliesTo(_upgradePlanner.getCurrentUpgradePlan())
		).map(
			child -> child.getId()
		).toArray(
			String[]::new
		);
	}

	private String _categoryId;
	private String[] _childIds;
	private String _description;
	private String _id;
	private String _imagePath;
	private double _order;
	private String _parentId;
	private String _requirement;
	private UpgradeStepStatus _status = UpgradeStepStatus.INCOMPLETE;
	private String _title;
	private UpgradePlanner _upgradePlanner;
	private String _url;

}