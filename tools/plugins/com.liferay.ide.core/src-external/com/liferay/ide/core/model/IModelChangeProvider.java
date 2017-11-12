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
public interface IModelChangeProvider {

	/**
	 * Adds the listener to the list of listeners that will be notified on model
	 * changes.
	 *
	 * @param listener
	 *            a model change listener to be added
	 */
	public void addModelChangedListener(IModelChangedListener listener);

	/**
	 * Delivers change event to all the registered listeners.
	 *
	 * @param event
	 *            a change event that will be passed to all the listeners
	 */
	public void fireModelChanged(IModelChangedEvent event);

	/**
	 * Notifies listeners that a property of a model object changed. This is a
	 * utility method that will create a model event and fire it.
	 *
	 * @param object
	 *            an affected model object
	 * @param property
	 *            name of the property that has changed
	 * @param oldValue
	 *            the old value of the property
	 * @param newValue
	 *            the new value of the property
	 */
	public void fireModelObjectChanged(Object object, String property, Object oldValue, Object newValue);

	/**
	 * Takes the listener off the list of registered change listeners.
	 *
	 * @param listener
	 *            a model change listener to be removed
	 */
	public void removeModelChangedListener(IModelChangedListener listener);

}