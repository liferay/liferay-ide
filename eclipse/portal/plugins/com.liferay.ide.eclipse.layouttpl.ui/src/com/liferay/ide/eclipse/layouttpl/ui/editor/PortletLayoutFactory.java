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
package com.liferay.ide.eclipse.layouttpl.ui.editor;

import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.requests.CreationFactory;


public class PortletLayoutFactory implements CreationFactory {

	private int numColumns;

	public PortletLayoutFactory(int numCols) {
		if (numCols < 1) {
			throw new IllegalArgumentException("Number of columns must be greater than 0");
		}

		this.numColumns = numCols;
	}

	public Object getNewObject() {
		PortletLayout newRow = new PortletLayout();
		int colWeight = (int) (100 / numColumns);

		for (int i = 0; i < numColumns; i++) {
			PortletColumn newColumn = new PortletColumn();
			newColumn.setWeight(colWeight);
			newRow.addColumn(newColumn);
		}

		return newRow;
	}

	public Object getObjectType() {
		return PortletLayout.class;
	}

}
