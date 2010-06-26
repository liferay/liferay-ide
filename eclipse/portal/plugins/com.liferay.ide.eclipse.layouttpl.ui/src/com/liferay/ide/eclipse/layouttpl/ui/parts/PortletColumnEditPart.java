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

package com.liferay.ide.eclipse.layouttpl.ui.parts;

import com.liferay.ide.eclipse.layouttpl.ui.draw2d.ColumnFigure;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.policies.PortletColumnComponentEditPolicy;
import com.liferay.ide.eclipse.layouttpl.ui.policies.PortletColumnLayoutEditPolicy;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public class PortletColumnEditPart extends BaseGraphicalEditPart {

	private static final int DEFAULT_COLUMN_HEIGHT = 100;

	public PortletColumnEditPart() {
		super();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (PortletColumn.SIZE_PROP.equals(prop) || PortletColumn.LOCATION_PROP.equals(prop)) {
			refreshVisuals();
		}
	}

	protected void createEditPolicies() {
		// allow removal of the associated model element
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new PortletColumnComponentEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new PortletColumnLayoutEditPolicy());
	}

	protected IFigure createFigure() {
		IFigure f = createFigureForModel();
		f.setOpaque(true); // non-transparent figure
		f.setBackgroundColor(new Color(null, 232, 232, 232));

		return f;
	}

	protected IFigure createFigureForModel() {
		if (getModel() instanceof PortletColumn) {
			RoundedRectangle rect = new ColumnFigure();
			rect.setCornerDimensions(new Dimension(20, 20));

			return rect;
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	protected PortletColumn getCastedModel() {
		return (PortletColumn) getModel();
	}

	protected void refreshVisuals() {
		// ColumnFigure columnFigure = (ColumnFigure) getFigure();
		// columnFigure.setPreferredSize(150, 100);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd.heightHint = DEFAULT_COLUMN_HEIGHT;
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), gd);
	}

}
