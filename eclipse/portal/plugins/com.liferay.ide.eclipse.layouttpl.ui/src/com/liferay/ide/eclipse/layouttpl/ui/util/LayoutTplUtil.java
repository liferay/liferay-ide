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
package com.liferay.ide.eclipse.layouttpl.ui.util;

import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.eclipse.layouttpl.ui.parts.LayoutTplDiagramEditPart;
import com.liferay.ide.eclipse.layouttpl.ui.parts.PortletLayoutEditPart;

import java.util.List;

import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateRequest;

public class LayoutTplUtil {

	public static int getRowIndex(PortletLayoutEditPart layoutEditPart) {
		if (layoutEditPart == null) {
			return -1;
		}

		LayoutTplDiagramEditPart diagramPart = (LayoutTplDiagramEditPart) layoutEditPart.getParent();
		Object[] rows = diagramPart.getChildren().toArray();

		for (int i = 0; i < rows.length; i++) {
			if (layoutEditPart.equals(rows[i])) {
				return i;
			}
		}

		return -1;
	}

	public static int getColumnIndex(PortletLayout currentParent, PortletColumn column) {
		if (currentParent == null || column == null) {
			return -1;
		}

		List<ModelElement> cols = currentParent.getColumns();

		for (int i = 0; i < cols.size(); i++) {
			if (column.equals(cols.get(i))) {
				return i;
			}
		}

		return -1;
	}


	public static boolean isCreateRequest(Class<?> class1, Request request) {
		if (!(request instanceof CreateRequest)) {
			return false;
		}

		if (!(((CreateRequest) request).getNewObject().getClass() == class1)) {
			return false;
		}

		return true;
	}

}
