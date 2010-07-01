/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/
package com.liferay.ide.eclipse.layouttpl.ui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

public class PortletLayout extends ModelElement implements PropertyChangeListener {

	public static final String COLUMN_ADDED_PROP = "PortletLayout.ColumnAdded";

	public static final String COLUMN_REMOVED_PROP = "PortletLayout.ColumnRemoved";

	public static final String CHILD_COLUMN_WEIGHT_CHANGED_PROP = "PortletLayout.ChildColumnWeightChanged";

	protected List<ModelElement> columns = new ArrayList<ModelElement>();

	public PortletLayout() {
		super();
	}

	public boolean addColumn(PortletColumn newColumn) {
		return addColumn(newColumn, -1);
	}

	public boolean addColumn(PortletColumn newColumn, int index) {
		if (newColumn != null) {
			if (index < 0) {
				columns.add(newColumn);
			}
			else {
				columns.add(index, newColumn);
			}

			newColumn.addPropertyChangeListener(this);

			firePropertyChange(COLUMN_ADDED_PROP, null, newColumn);

			return true;
		}

		return false;
	}

	public List<ModelElement> getColumns() {
		return columns;
	}

	public Image getIcon() {
		// TODO Implement getIcon method on class PortletLayout
		System.out.println("PortletLayout.getIcon");
		return null;
	}

	@Override
	public void removeChild(ModelElement child) {
		if (columns.contains(child)) {
			removeColumn((PortletColumn) child);
		}
	}

	public boolean removeColumn(PortletColumn existingColumn) {
		if (existingColumn != null && columns.remove(existingColumn)) {
			firePropertyChange(COLUMN_REMOVED_PROP, null, existingColumn);
			return true;
		}

		return false;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();

		if (PortletColumn.WEIGHT_PROP.equals(prop)) {
			firePropertyChange(CHILD_COLUMN_WEIGHT_CHANGED_PROP, null, evt.getSource());
		}
	}

}
