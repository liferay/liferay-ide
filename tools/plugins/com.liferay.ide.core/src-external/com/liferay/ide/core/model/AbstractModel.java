/*******************************************************************************
 *  Copyright (c) 2000, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.core.model;

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

import com.liferay.ide.core.LiferayCore;

public abstract class AbstractModel extends PlatformObject implements IModel, IModelChangeProviderExtension, Serializable {

	private static final long serialVersionUID = 1L;

	private transient List fListeners;

	private boolean fLoaded;

	protected boolean fDisposed;

	private long fTimestamp;

	private Exception fException;

	public AbstractModel() {
		fListeners = Collections.synchronizedList(new ArrayList());
	}

	public void addModelChangedListener(IModelChangedListener listener) {
		fListeners.add(listener);
	}

	public void transferListenersTo(IModelChangeProviderExtension target, IModelChangedListenerFilter filter) {
		ArrayList removed = new ArrayList();
		for (int i = 0; i < fListeners.size(); i++) {
			IModelChangedListener listener = (IModelChangedListener) fListeners.get(i);
			if (filter == null || filter.accept(listener)) {
				target.addModelChangedListener(listener);
				removed.add(listener);
			}
		}
		fListeners.removeAll(removed);
	}

	public void dispose() {
		fDisposed = true;
	}

	public void fireModelChanged(IModelChangedEvent event) {
		IModelChangedListener[] list = (IModelChangedListener[]) fListeners.toArray(new IModelChangedListener[fListeners.size()]);
		for (int i = 0; i < list.length; i++) {
			IModelChangedListener listener = list[i];
			listener.modelChanged(event);
		}
	}

	public void fireModelObjectChanged(Object object, String property, Object oldValue, Object newValue) {
		fireModelChanged(new ModelChangedEvent(this, object, property, oldValue, newValue));
	}

	public String getResourceString(String key) {
		return key;
	}

	public IResource getUnderlyingResource() {
		return null;
	}

	protected boolean isInSync(File localFile) {
		return localFile.exists() && localFile.lastModified() == getTimeStamp();
	}

	public boolean isValid() {
		return !isDisposed() && isLoaded();
	}

	public final long getTimeStamp() {
		return fTimestamp;
	}

	protected abstract void updateTimeStamp();

	protected void updateTimeStamp(File localFile) {
		if (localFile.exists())
			fTimestamp = localFile.lastModified();
	}

	public boolean isDisposed() {
		return fDisposed;
	}

	public boolean isLoaded() {
		return fLoaded;
	}

	public void setLoaded(boolean loaded) {
		fLoaded = loaded;
	}

	public void setException(Exception e) {
		fException = e;
	}

	public Exception getException() {
		return fException;
	}

	public void removeModelChangedListener(IModelChangedListener listener) {
		fListeners.remove(listener);
	}

	public void throwParseErrorsException(Throwable e) throws CoreException {
		Status status = new Status(IStatus.ERROR, LiferayCore.PLUGIN_ID, IStatus.OK, "Error in the service file", //$NON-NLS-1$
				e);
		throw new CoreException(status);
	}

	protected SAXParser getSaxParser() throws ParserConfigurationException, SAXException, FactoryConfigurationError {
		return SAXParserFactory.newInstance().newSAXParser();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.core.IModel#isReconcilingModel()
	 */
	public boolean isReconcilingModel() {
		return false;
	}

}
