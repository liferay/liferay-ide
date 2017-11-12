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
public interface IModelChangedEvent {

	/**
	 * Returns an array of model objects that are affected by the change.
	 *
	 * @return array of affected objects
	 */
	public Object[] getChangedObjects();

	/**
	 * Returns a name of the object's property that has been changed if change type
	 * is CHANGE.
	 *
	 * @return property that has been changed in the model object, or <samp>null
	 *         </samp> if type is not CHANGE or if more than one property has been
	 *         changed.
	 */
	public String getChangedProperty();

	/**
	 * Returns the provider that fired this event.
	 *
	 * @return the event provider
	 */
	public IModelChangeProvider getChangeProvider();

	/**
	 * Returns the type of change that occured in the model (one of
	 * <samp>INSERT</samp>, <samp>REMOVE</samp>, <samp>CHANGE</samp> or
	 * <samp>WORLD_CHANGED </samp>).
	 *
	 * @return type of change
	 */
	public int getChangeType();

	/**
	 * When model change is of type <samp>CHANGE</samp>, this method is used to
	 * obtain the new value of the property (after the change).
	 *
	 * @return the new value of the changed property.
	 */
	public Object getNewValue();

	/**
	 * When model change is of type <samp>CHANGE</samp>, this method is used to
	 * obtain the old value of the property (before the change).
	 *
	 * @return the old value of the changed property
	 */
	public Object getOldValue();

	/**
	 * indicates that a model object's property has been changed.
	 */
	public int CHANGE = 3;

	/**
	 * Indicates a change where one or more objects are added to the model.
	 */
	public int INSERT = 1;

	/**
	 * Indicates a change where one or more objects are removed from the model.
	 */
	public int REMOVE = 2;

	/**
	 * Indicates that the model has been reloaded and that listeners should perform
	 * full refresh.
	 */
	public int WORLD_CHANGED = 99;

}