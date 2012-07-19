/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.core.model;

/**
 * Model change events are fired by the model when it is changed from the last
 * clean state. Model change listeners can use these events to update
 * accordingly.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.0
 */
public interface IModelChangedEvent {
	/**
	 * Indicates a change where one or more objects are added to the model.
	 */
	int INSERT = 1;
	/**
	 * Indicates a change where one or more objects are removed from the model.
	 */
	int REMOVE = 2;
	/**
	 * Indicates that the model has been reloaded and that listeners should
	 * perform full refresh.
	 */
	int WORLD_CHANGED = 99;
	/**
	 * indicates that a model object's property has been changed.
	 */
	int CHANGE = 3;

	/**
	 * Returns the provider that fired this event.
	 * 
	 * @return the event provider
	 */
	public IModelChangeProvider getChangeProvider();

	/**
	 * Returns an array of model objects that are affected by the change.
	 * 
	 * @return array of affected objects
	 */
	public Object[] getChangedObjects();

	/**
	 * Returns a name of the object's property that has been changed if change
	 * type is CHANGE.
	 * 
	 * @return property that has been changed in the model object, or <samp>null
	 *         </samp> if type is not CHANGE or if more than one property has
	 *         been changed.
	 */
	public String getChangedProperty();

	/**
	 * When model change is of type <samp>CHANGE</samp>, this method is used to
	 * obtain the old value of the property (before the change).
	 * 
	 * @return the old value of the changed property
	 */
	public Object getOldValue();

	/**
	 * When model change is of type <samp>CHANGE</samp>, this method is used to
	 * obtain the new value of the property (after the change).
	 * 
	 * @return the new value of the changed property.
	 */
	public Object getNewValue();

	/**
	 * Returns the type of change that occured in the model (one of <samp>INSERT</samp>,
	 * <samp>REMOVE</samp>, <samp>CHANGE</samp> or
	 * <samp>WORLD_CHANGED </samp>).
	 * 
	 * @return type of change
	 */
	public int getChangeType();
}
