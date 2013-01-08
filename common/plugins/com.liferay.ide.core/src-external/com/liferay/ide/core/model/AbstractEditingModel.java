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

import com.liferay.ide.core.util.StringPool;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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

public abstract class AbstractEditingModel extends PlatformObject implements IEditingModel, IModelChangeProviderExtension {
	private ArrayList fListeners = new ArrayList();
	protected boolean fReconciling;
	protected boolean fInSync = true;
	protected boolean fLoaded = false;
	protected boolean fDisposed;
	protected long fTimestamp;
//	private transient NLResourceHelper fNLResourceHelper;
	private IDocument fDocument;
	private boolean fDirty;
	private String fCharset;
	private IResource fUnderlyingResource;
	private String fInstallLocation;
	private boolean fStale;

	public AbstractEditingModel(IDocument document, boolean isReconciling) {
		fDocument = document;
		fReconciling = isReconciling;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#dispose()
	 */
	public void dispose() {
//		if (fNLResourceHelper != null) {
//			fNLResourceHelper.dispose();
//			fNLResourceHelper = null;
//		}
		fDisposed = true;
		fListeners.clear();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#getResourceString(java.lang.String)
	 */
	public String getResourceString(String key) {
		if (key == null || key.length() == 0)
			return ""; //$NON-NLS-1$

//		return (getNLResourceHelper() == null) ? key : getNLResourceHelper().getResourceString(key);
		return StringPool.EMPTY;
	}

//	protected abstract NLResourceHelper createNLResourceHelper();
//
//	public NLResourceHelper getNLResourceHelper() {
//		if (fNLResourceHelper == null)
//			fNLResourceHelper = createNLResourceHelper();
//		return fNLResourceHelper;
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isDisposed()
	 */
	public boolean isDisposed() {
		return fDisposed;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isEditable()
	 */
	public boolean isEditable() {
		return fReconciling;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isLoaded()
	 */
	public boolean isLoaded() {
		return fLoaded;
	}

	/**
	 * @param loaded
	 */
	public void setLoaded(boolean loaded) {
		fLoaded = loaded;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isInSync()
	 */
	public boolean isInSync() {
		return fInSync;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isValid()
	 */
	public boolean isValid() {
		return isLoaded();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#getTimeStamp()
	 */
	public final long getTimeStamp() {
		return fTimestamp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#load()
	 */
	public final void load() throws CoreException {
		try {
			load(getInputStream(getDocument()), false);
		} catch (UnsupportedEncodingException e) {
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#reload(java.io.InputStream, boolean)
	 */
	public final void reload(InputStream source, boolean outOfSync) throws CoreException {
		load(source, outOfSync);
		fireModelChanged(new ModelChangedEvent(this, IModelChangedEvent.WORLD_CHANGED, new Object[] {this}, null));

	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#isReconcilingModel()
	 */
	public boolean isReconcilingModel() {
		return fReconciling;
	}

	public IDocument getDocument() {
		return fDocument;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.neweditor.text.IReconcilingParticipant#reconciled(org.eclipse.jface.text.IDocument)
	 */
	public final void reconciled(IDocument document) {
		if (isReconcilingModel()) {
			try {
				if (isStale()) {
					adjustOffsets(document);
					setStale(false);
				} else {
					reload(getInputStream(document), false);
				}
			} catch (UnsupportedEncodingException e) {
			} catch (CoreException e) {
			}
			if (isDirty())
				setDirty(false);
		}
	}

	public abstract void adjustOffsets(IDocument document) throws CoreException;

	protected InputStream getInputStream(IDocument document) throws UnsupportedEncodingException {
		return new BufferedInputStream(new ByteArrayInputStream(document.get().getBytes(getCharset())));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.text.IEditingModel#getCharset()
	 */
	public String getCharset() {
		return fCharset != null ? fCharset : "UTF-8"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.text.IEditingModel#setCharset(java.lang.String)
	 */
	public void setCharset(String charset) {
		fCharset = charset;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModelChangeProvider#addModelChangedListener(org.eclipse.pde.core.IModelChangedListener)
	 */
	public void addModelChangedListener(IModelChangedListener listener) {
		if (!fListeners.contains(listener))
			fListeners.add(listener);
	}

	public void transferListenersTo(IModelChangeProviderExtension target, IModelChangedListenerFilter filter) {
		List oldList = (List) fListeners.clone();
		for (int i = 0; i < oldList.size(); i++) {
			IModelChangedListener listener = (IModelChangedListener) oldList.get(i);
			if (filter == null || filter.accept(listener)) {
				// add the listener to the target
				target.addModelChangedListener(listener);
				// remove the listener from our list
				fListeners.remove(listener);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModelChangeProvider#fireModelChanged(org.eclipse.pde.core.IModelChangedEvent)
	 */
	public void fireModelChanged(IModelChangedEvent event) {
		if (event.getChangeType() == IModelChangedEvent.CHANGE && event.getOldValue() != null && event.getOldValue().equals(event.getNewValue()))
			return;
		setDirty(event.getChangeType() != IModelChangedEvent.WORLD_CHANGED);
		for (int i = 0; i < fListeners.size(); i++) {
			((IModelChangedListener) fListeners.get(i)).modelChanged(event);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModelChangeProvider#fireModelObjectChanged(java.lang.Object, java.lang.String, java.lang.Object, java.lang.Object)
	 */
	public void fireModelObjectChanged(Object object, String property, Object oldValue, Object newValue) {
		fireModelChanged(new ModelChangedEvent(this, object, property, oldValue, newValue));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModelChangeProvider#removeModelChangedListener(org.eclipse.pde.core.IModelChangedListener)
	 */
	public void removeModelChangedListener(IModelChangedListener listener) {
		fListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IEditable#isDirty()
	 */
	public boolean isDirty() {
		return fDirty;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IEditable#save(java.io.PrintWriter)
	 */
	public void save(PrintWriter writer) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IEditable#setDirty(boolean)
	 */
	public void setDirty(boolean dirty) {
		this.fDirty = dirty;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.model.IEditingModel#isStale()
	 */
	public boolean isStale() {
		return fStale;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.model.IEditingModel#setStale(boolean)
	 */
	public void setStale(boolean stale) {
		fStale = stale;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.IModel#getUnderlyingResource()
	 */
	public IResource getUnderlyingResource() {
		return fUnderlyingResource;
	}

	public void setUnderlyingResource(IResource resource) {
		fUnderlyingResource = resource;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.core.plugin.ISharedPluginModel#getInstallLocation()
	 */
	public String getInstallLocation() {
		if (fInstallLocation == null && fUnderlyingResource != null) {
			IPath path = fUnderlyingResource.getProject().getLocation();
			return path != null ? path.addTrailingSeparator().toString() : null;
		}
		return fInstallLocation;
	}

	public void setInstallLocation(String location) {
		fInstallLocation = location;
	}

	public IModelTextChangeListener getLastTextChangeListener() {
		for (int i = fListeners.size() - 1; i >= 0; i--) {
			Object obj = fListeners.get(i);
			if (obj instanceof IModelTextChangeListener)
				return (IModelTextChangeListener) obj;
		}
		return null;
	}

}