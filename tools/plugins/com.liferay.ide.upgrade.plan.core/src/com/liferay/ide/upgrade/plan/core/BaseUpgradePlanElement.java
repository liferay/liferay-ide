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

import com.liferay.ide.upgrade.plan.core.util.ServicesLookup;

import java.util.Dictionary;

import org.eclipse.core.runtime.Adapters;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public abstract class BaseUpgradePlanElement implements UpgradePlanElement {

	@Activate
	public void activate(ComponentContext componentContext) {
		Dictionary<String, Object> properties = componentContext.getProperties();

		_description = getStringProperty(properties, "description");
		_id = getStringProperty(properties, "id");
		_imagePath = getStringProperty(properties, "imagePath");
		_title = getStringProperty(properties, "title");
		_order = getDoubleProperty(properties, "order");

		_upgradePlanner = ServicesLookup.getSingleService(UpgradePlanner.class, null);

		if (_description == null) {
			_description = _title;
		}
	}

	@Override
	public boolean equals(Object object) {
		if ((object instanceof BaseUpgradePlanElement) == false) {
			return false;
		}

		BaseUpgradePlanElement upgradePlanElement = Adapters.adapt(object, BaseUpgradePlanElement.class);

		if (upgradePlanElement == null) {
			return false;
		}

		if (isEqualIgnoreCase(_description, upgradePlanElement.getDescription()) &&
			isEqualIgnoreCase(_id, upgradePlanElement.getId()) &&
			isEqualIgnoreCase(_imagePath, upgradePlanElement.getImagePath()) &&
			(_order == upgradePlanElement.getOrder()) && isEqualIgnoreCase(_title, upgradePlanElement.getTitle()) &&
			_upgradePlanElementStatus.equals(getStatus())) {

			return true;
		}

		return false;
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
	public double getOrder() {
		return _order;
	}

	@Override
	public UpgradePlanElementStatus getStatus() {
		return _upgradePlanElementStatus;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public int hashCode() {
		int hash = 31;

		Double orderDouble = Double.valueOf(_order);

		hash = 31 * hash + orderDouble.hashCode();
		hash = 31 * hash + (_description != null ? _description.hashCode() : 0);
		hash = 31 * hash + (_imagePath != null ? _imagePath.hashCode() : 0);
		hash = 31 * hash + (_title != null ? _title.hashCode() : 0);
		hash = 31 * hash + (_id != null ? _id.hashCode() : 0);
		hash = 31 * hash + (_upgradePlanElementStatus != null ? _upgradePlanElementStatus.hashCode() : 0);

		return hash;
	}

	public void setStatus(UpgradePlanElementStatus upgradePlanElementStatus) {
		UpgradePlanElementStatusChangedEvent upgradePlanElementStatusChangedEvent =
			new UpgradePlanElementStatusChangedEvent(this, _upgradePlanElementStatus, upgradePlanElementStatus);

		_upgradePlanElementStatus = upgradePlanElementStatus;

		_upgradePlanner.dispatch(upgradePlanElementStatusChangedEvent);
	}

	@Override
	public String toString() {
		return getId();
	}

	private String _description;
	private String _id;
	private String _imagePath;
	private double _order;
	private String _title;
	private UpgradePlanElementStatus _upgradePlanElementStatus = UpgradePlanElementStatus.INCOMPLETE;
	private UpgradePlanner _upgradePlanner;

}