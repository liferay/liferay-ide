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

import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Gregory Amerson
 */
public interface IModel extends IBaseModel {

	/**
	 * Returns a string found in the resource bundle associated with this model for
	 * the provided key.
	 *
	 * @param key
	 *            the name to use for bundle lookup
	 * @return the string for the key in the resource bundle, or the key itself if
	 *         not found
	 */
	public String getResourceString(String key);

	/**
	 * Returns the last modification time stamp. The model itself does not have the
	 * time stamp. It is 'borrowed' from the underlying physical object.
	 *
	 * @return the time stamp of the underlying physical object.
	 */
	public long getTimeStamp();

	/**
	 * Returns a workspace resource that this model is created from. Load/reload
	 * operations are not directly connected with the resource (although they can
	 * be). In some cases, models will load from a buffer (an editor document)
	 * rather than a resource. However, the buffer will eventually be synced up with
	 * this resource.
	 * <p>
	 * With the caveat of stepped loading, all other properties of the underlying
	 * resource could be used directly (path, project etc.).
	 *
	 * @return a workspace resource (file) that this model is associated with, or
	 *         <samp>null </samp> if the model is not created from a resource.
	 */
	public IResource getUnderlyingResource();

	/**
	 * Tests if this model is in sync with the storage object it was loaded from.
	 * Models loaded from resources are in sync if underlying resources are in sync.
	 * Models loaded from files on the file systems are in sync if the time stamp
	 * matches the model time stamp.
	 *
	 * @return <code>true</code> if the model is in sync with the file system.
	 */
	public boolean isInSync();

	/**
	 * Tests if this model is loaded and can be used.
	 *
	 * @return <code>true</code> if the model has been loaded
	 */
	public boolean isLoaded();

	/**
	 * Returns whether this model needs to react to changes in source and reconcile
	 * them. Only model instances used in editors need to perform this task.
	 *
	 * @return <code>true</code> if this is a reconciling model, <code>false</code>
	 *         otherwise.
	 */
	public boolean isReconcilingModel();

	/**
	 * Loads the model directly from an underlying resource. This method does
	 * nothing if this model has no underlying resource or if there is a buffer
	 * stage between the model and the resource.
	 *
	 * @throws CoreException
	 *             if errors are encountered during the loading.
	 */
	public void load() throws CoreException;

	/**
	 * Loads the model from the provided input stream. This method throws a
	 * CoreException if errors are encountered during the loading. Upon succesful
	 * load, 'isLoaded()' should return <samp>true </samp>.
	 *
	 * @param source
	 *            an input stream that should be parsed to load the model
	 * @param outOfSync
	 *            if true, time stamp will not be updated to maintain out-of-sync
	 *            state of the model.
	 * @throws CoreException
	 *             if errors are encountered during the loading.
	 */
	public void load(InputStream source, boolean outOfSync) throws CoreException;

	/**
	 * Reload is a version of 'load' operation that has the following steps:
	 * <ul>
	 * <li>Reset the model
	 * <li>Load the model
	 * <li>Fire "world changed" event
	 * </ul>
	 * Reload operation is used when a model that is already in use is invalidated
	 * by a change in the underlying buffer or resource. Since we don't know the
	 * extent of the change, the only safe thing to do is to reparse the buffer to
	 * sync up. The event that is subsequently fired should be used by listeners to
	 * discard all caches and/or fully refresh views that shows any portion of the
	 * model.
	 *
	 * @param source
	 *            an input stream that should be parsed to load the model.
	 * @param outOfSync
	 *            if true, the timestamp will not be updated to maintain out-of-sync
	 *            state of the model.
	 * @throws CoreException
	 *             if errors are encountered during the reloading.
	 */
	public void reload(InputStream source, boolean outOfSync) throws CoreException;

}