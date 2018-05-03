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

import com.liferay.ide.core.util.StringPool;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.text.IDocument;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractEditingModel
	extends PlatformObject implements IEditingModel, IModelChangeProviderExtension {

	public AbstractEditingModel(IDocument document, boolean reconciling) {
		_fDocument = document;

		fReconciling = reconciling;
	}

	public void addModelChangedListener(IModelChangedListener listener) {
		if (!_fListeners.contains(listener)) {
			_fListeners.add(listener);
		}
	}

	public abstract void adjustOffsets(IDocument document) throws CoreException;

	public void dispose() {
		fDisposed = true;

		_fListeners.clear();
	}

	public void fireModelChanged(IModelChangedEvent event) {
		if ((event.getChangeType() == IModelChangedEvent.CHANGE) && (event.getOldValue() != null) &&
			event.getOldValue().equals(event.getNewValue())) {

			return;
		}

		setDirty(event.getChangeType() != IModelChangedEvent.WORLD_CHANGED);

		for (IModelChangedListener listener : _fListeners) {
			listener.modelChanged(event);
		}
	}

	public void fireModelObjectChanged(Object object, String property, Object oldValue, Object newValue) {
		fireModelChanged(new ModelChangedEvent(this, object, property, oldValue, newValue));
	}

	// private transient NLResourceHelper fNLResourceHelper;

	public String getCharset() {
		if (_fCharset != null) {
			return _fCharset;
		}

		return "UTF-8";
	}

	public IDocument getDocument() {
		return _fDocument;
	}

	public String getInstallLocation() {
		if ((_fInstallLocation == null) && (_fUnderlyingResource != null)) {
			IPath path = _fUnderlyingResource.getProject().getLocation();

			if (path != null) {
				return path.addTrailingSeparator().toString();
			}

			return null;
		}

		return _fInstallLocation;
	}

	public IModelTextChangeListener getLastTextChangeListener() {
		for (int i = _fListeners.size() - 1; i >= 0; i--) {
			Object obj = _fListeners.get(i);

			if (obj instanceof IModelTextChangeListener) {
				return (IModelTextChangeListener)obj;
			}
		}

		return null;
	}

	public String getResourceString(String key) {
		if ((key == null) || (key.length() == 0)) {
			return "";
		}

		return StringPool.EMPTY;
	}

	public final long getTimeStamp() {
		return fTimestamp;
	}

	public IResource getUnderlyingResource() {
		return _fUnderlyingResource;
	}

	public boolean isDirty() {
		return _fDirty;
	}

	public boolean isDisposed() {
		return fDisposed;
	}

	public boolean isEditable() {
		return fReconciling;
	}

	public boolean isInSync() {
		return fInSync;
	}

	public boolean isLoaded() {
		return fLoaded;
	}

	public boolean isReconcilingModel() {
		return fReconciling;
	}

	public boolean isStale() {
		return _fStale;
	}

	public boolean isValid() {
		return isLoaded();
	}

	public final void load() throws CoreException {
		try {
			load(getInputStream(getDocument()), false);
		}
		catch (UnsupportedEncodingException uee) {
		}
	}

	public final void reconciled(IDocument document) {
		if (!isReconcilingModel()) {
			return;
		}

		try {
			if (isStale()) {
				adjustOffsets(document);
				setStale(false);
			}
			else {
				reload(getInputStream(document), false);
			}
		}
		catch (UnsupportedEncodingException uee) {
		}
		catch (CoreException ce) {
		}

		if (isDirty()) {
			setDirty(false);
		}
	}

	public final void reload(InputStream source, boolean outOfSync) throws CoreException {
		load(source, outOfSync);

		ModelChangedEvent event = new ModelChangedEvent(
			this, IModelChangedEvent.WORLD_CHANGED, new Object[] {this}, null);

		fireModelChanged(event);
	}

	public void removeModelChangedListener(IModelChangedListener listener) {
		_fListeners.remove(listener);
	}

	public void save(PrintWriter writer) {
	}

	public void setCharset(String charset) {
		_fCharset = charset;
	}

	public void setDirty(boolean dirty) {
		_fDirty = dirty;
	}

	public void setInstallLocation(String location) {
		_fInstallLocation = location;
	}

	public void setLoaded(boolean loaded) {
		fLoaded = loaded;
	}

	public void setStale(boolean stale) {
		_fStale = stale;
	}

	public void setUnderlyingResource(IResource resource) {
		_fUnderlyingResource = resource;
	}

	public void transferListenersTo(IModelChangeProviderExtension target, IModelChangedListenerFilter filter) {
		List<IModelChangedListener> oldList = (List<IModelChangedListener>)_fListeners.clone();

		for (int i = 0; i < oldList.size(); i++) {
			IModelChangedListener listener = (IModelChangedListener)oldList.get(i);

			if ((filter == null) || filter.accept(listener)) {

				// add the listener to the target

				target.addModelChangedListener(listener);

				// remove the listener from our list

				_fListeners.remove(listener);
			}
		}
	}

	protected InputStream getInputStream(IDocument document) throws UnsupportedEncodingException {
		try (InputStream inputStream = new ByteArrayInputStream(document.get().getBytes(getCharset()))) {
			return new BufferedInputStream(inputStream);
		}
		catch (IOException ioe) {
			throw new UnsupportedEncodingException(ioe.getMessage());
		}
	}

	protected boolean fDisposed;
	protected boolean fInSync = true;
	protected boolean fLoaded = false;
	protected boolean fReconciling;
	protected long fTimestamp;

	private String _fCharset;
	private boolean _fDirty;
	private IDocument _fDocument;
	private String _fInstallLocation;
	private ArrayList<IModelChangedListener> _fListeners = new ArrayList<>();
	private boolean _fStale;
	private IResource _fUnderlyingResource;

}