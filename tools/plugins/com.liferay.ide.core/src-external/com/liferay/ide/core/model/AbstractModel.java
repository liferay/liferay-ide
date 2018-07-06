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

import com.liferay.ide.core.LiferayCore;

import java.io.File;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;

import org.xml.sax.SAXException;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractModel
	extends PlatformObject implements IModel, IModelChangeProviderExtension, Serializable {

	public static final long serialVersionUID = 1L;

	public AbstractModel() {
		_fListeners = Collections.synchronizedList(new ArrayList<IModelChangedListener>());
	}

	public void addModelChangedListener(IModelChangedListener listener) {
		_fListeners.add(listener);
	}

	public void dispose() {
		fDisposed = true;
	}

	public void fireModelChanged(IModelChangedEvent event) {
		IModelChangedListener[] list = (IModelChangedListener[])_fListeners.toArray(
			new IModelChangedListener[_fListeners.size()]);

		for (IModelChangedListener listener : list) {
			listener.modelChanged(event);
		}
	}

	public void fireModelObjectChanged(Object object, String property, Object oldValue, Object newValue) {
		fireModelChanged(new ModelChangedEvent(this, object, property, oldValue, newValue));
	}

	public Exception getException() {
		return _fException;
	}

	public String getResourceString(String key) {
		return key;
	}

	public final long getTimeStamp() {
		return _fTimestamp;
	}

	public IResource getUnderlyingResource() {
		return null;
	}

	public boolean isDisposed() {
		return fDisposed;
	}

	public boolean isLoaded() {
		return _fLoaded;
	}

	public boolean isReconcilingModel() {
		return false;
	}

	public boolean isValid() {
		if (!isDisposed() && isLoaded()) {
			return true;
		}

		return false;
	}

	public void removeModelChangedListener(IModelChangedListener listener) {
		_fListeners.remove(listener);
	}

	public void setException(Exception e) {
		_fException = e;
	}

	public void setLoaded(boolean loaded) {
		_fLoaded = loaded;
	}

	public void throwParseErrorsException(Throwable e) throws CoreException {
		Status status = new Status(IStatus.ERROR, LiferayCore.PLUGIN_ID, IStatus.OK, "Error in the service file", e);

		throw new CoreException(status);
	}

	public void transferListenersTo(IModelChangeProviderExtension target, IModelChangedListenerFilter filter) {
		ArrayList<IModelChangedListener> removed = new ArrayList<>();

		for (IModelChangedListener listener : _fListeners) {
			if ((filter == null) || filter.accept(listener)) {
				target.addModelChangedListener(listener);

				removed.add(listener);
			}
		}

		_fListeners.removeAll(removed);
	}

	protected SAXParser getSaxParser() throws FactoryConfigurationError, ParserConfigurationException, SAXException {
		return SAXParserFactory.newInstance().newSAXParser();
	}

	protected boolean isInSync(File localFile) {
		if (localFile.exists() && (localFile.lastModified() == getTimeStamp())) {
			return true;
		}

		return false;
	}

	protected abstract void updateTimeStamp();

	protected void updateTimeStamp(File localFile) {
		if (localFile.exists()) {
			_fTimestamp = localFile.lastModified();
		}
	}

	protected boolean fDisposed;

	private Exception _fException;
	private transient List<IModelChangedListener> _fListeners;
	private boolean _fLoaded;
	private long _fTimestamp;

}