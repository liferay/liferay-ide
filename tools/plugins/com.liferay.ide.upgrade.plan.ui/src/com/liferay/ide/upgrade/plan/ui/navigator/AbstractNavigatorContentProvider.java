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

package com.liferay.ide.upgrade.plan.ui.navigator;

import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider2;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public abstract class AbstractNavigatorContentProvider implements IPipelinedTreeContentProvider2 {

	public Object[] getElements(Object inputElement) {
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public void getPipelinedChildren(Object aParent, Set theCurrentChildren) {
	}

	@SuppressWarnings("rawtypes")
	public void getPipelinedElements(Object anInput, Set theCurrentElements) {
	}

	public Object getPipelinedParent(Object anObject, Object aSuggestedParent) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return false;
	}

	public boolean hasPipelinedChildren(Object element, boolean currentHasChildren) {
		return false;
	}

	public void init(ICommonContentExtensionSite config) {
		_config = config;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public PipelinedShapeModification interceptAdd(PipelinedShapeModification anAddModification) {
		return null;
	}

	public boolean interceptRefresh(PipelinedViewerUpdate aRefreshSynchronization) {
		return false;
	}

	public PipelinedShapeModification interceptRemove(PipelinedShapeModification aRemoveModification) {
		return null;
	}

	public boolean interceptUpdate(PipelinedViewerUpdate anUpdateSynchronization) {
		return false;
	}

	public void restoreState(IMemento aMemento) {

		// do nothing

	}

	public void saveState(IMemento aMemento) {

		// do nothing

	}

	protected ICommonContentExtensionSite getConfig() {
		return _config;
	}

	private ICommonContentExtensionSite _config;

}