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

package com.liferay.ide.core.model;

/**
 * @author Gregory Amerson
 */
public class ModelChangedEvent implements IModelChangedEvent {

	public ModelChangedEvent(IModelChangeProvider provider, int type, Object[] objects, String changedProperty) {
		_provider = provider;
		_type = type;
		_changedObjects = objects;
		_changedProperty = changedProperty;
	}

	public ModelChangedEvent(
		IModelChangeProvider provider, Object object, String changedProperty, Object oldValue, Object newValue) {

		_type = CHANGE;
		_provider = provider;
		_changedObjects = new Object[] {object};
		_changedProperty = changedProperty;
		_oldValue = oldValue;
		_newValue = newValue;
	}

	public Object[] getChangedObjects() {
		if (_changedObjects == null) {
			return new Object[0];
		}

		return _changedObjects;
	}

	public String getChangedProperty() {
		return _changedProperty;
	}

	public IModelChangeProvider getChangeProvider() {
		return _provider;
	}

	public int getChangeType() {
		return _type;
	}

	public Object getNewValue() {
		return _newValue;
	}

	public Object getOldValue() {
		return _oldValue;
	}

	private Object[] _changedObjects;
	private String _changedProperty;
	private Object _newValue;
	private Object _oldValue;
	private IModelChangeProvider _provider;
	private int _type;

}