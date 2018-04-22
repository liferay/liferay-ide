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

package com.liferay.ide.portlet.ui.action;

import com.liferay.ide.portlet.ui.PortletUIPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerComparator;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
public class SortAction extends Action {

	/**
	 * @param viewer
	 * @param tooltipText
	 * @param sorter
	 * @param defaultSorter
	 * @param listener
	 * @param useMiniImage
	 */
	public SortAction(
		StructuredViewer viewer, String tooltipText, ViewerComparator sorter, ViewerComparator defaultSorter,
		IPropertyChangeListener listener) {

		super(tooltipText, IAction.AS_CHECK_BOX);

		// Set the tooltip

		setToolTipText(tooltipText);

		// Set the image

		Bundle bundle = PortletUIPlugin.getDefault().getBundle();

		setImageDescriptor(ImageDescriptor.createFromURL(bundle.getEntry("/icons/e16/alphab_sort_co.gif")));

		// Set the default comparator

		_fDefaultComparator = defaultSorter;

		// Set the viewer

		_fViewer = viewer;

		// Set the comparator
		// If one was not specified, use the default

		if (sorter == null) {
			_fComparator = new ViewerComparator();
		}
		else {
			_fComparator = sorter;
		}

		// Determine if the viewer is already sorted
		// Note: Most likely the default comparator is null

		if (viewer.getComparator() == _fDefaultComparator) {
			_fSorted = false;
		}
		else {
			_fSorted = true;
		}

		// Set the status of this action depending on whether it is sorted or
		// not

		setChecked(_fSorted);

		// If a listener was specified, use it

		if (listener != null) {
			addListenerObject(listener);
		}
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see Action#run()
	 */
	public void run() {

		// Toggle sorting on/off

		if (_fSorted) {

			// Sorting is on
			// Turn it off

			_fViewer.setComparator(_fDefaultComparator);
			_fSorted = false;
		}
		else {

			// Sorting is off
			// Turn it on

			_fViewer.setComparator(_fComparator);
			_fSorted = true;
		}

		notifyResult(true);
	}

	private ViewerComparator _fComparator;
	private ViewerComparator _fDefaultComparator;
	private boolean _fSorted;
	private StructuredViewer _fViewer;

}