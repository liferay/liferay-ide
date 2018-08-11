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

package com.liferay.ide.layouttpl.ui.parts;

import com.liferay.ide.layouttpl.core.model.CanAddPortletLayouts;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import java.util.List;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.sapphire.ElementList;

/**
 * @author Cindy Li
 */
public abstract class PortletRowLayoutEditPart extends BaseGraphicalEditPart {

	public static final int DEFAULT_COLUMN_HEIGHT = -1;

	public int getContainerWidth() {
		/*
		 * XXX to be continued, temporarily fix NullPointerException for parent
		 * column
		 */
		if (panel != null) {
			Dimension size = panel.getSize();

			return size.width - (getMargin() * 2);
		}

		return 0;
	}

	public abstract int getMargin();

	public int getPreferredColumnHeight() {
		int retval = DEFAULT_COLUMN_HEIGHT;

		int numRows = getRowPartsCount();

		if (numRows > 1) {
			Rectangle partBounds = getFigure().getBounds();

			if (partBounds.height > 0) {
				int partHeight = partBounds.height;

				int rowsHeight = partHeight - (getMargin() * 2);

				int totalColumnsHeight = rowsHeight - (getRowPartsCount() * PortletLayoutEditPart.COLUMN_SPACING * 2);

				int computedColumnHeight = totalColumnsHeight / numRows;

				retval = computedColumnHeight;
			}
		}

		return retval;
	}

	@Override
	public void refresh() {
		super.refresh();

		if (getChildren() != null) {
			refreshVisuals();
		}
	}

	protected void configurePanel(Panel panel) {
		GridLayout gridLayout = new GridLayout(1, false);

		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;

		panel.setLayoutManager(gridLayout);

		panel.setBorder(new MarginBorder(getMargin()));
	}

	@Override
	protected IFigure createFigure() {
		panel = createPanel();

		configurePanel(panel);

		return panel;
	}

	protected Panel createPanel() {
		return new Panel();
	}

	protected CanAddPortletLayouts getCastedModel() {
		return (CanAddPortletLayouts)getModel();
	}

	protected ElementList<PortletLayoutElement> getModelChildren() {
		return getCastedModel().getPortletLayouts(); // return a list of rows
	}

	protected int getRowPartsCount() {
		return getChildren().size();
	}

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();

		List<?> children = getChildren();

		for (Object child : children) {
			if (child instanceof AbstractEditPart) {
				((AbstractEditPart)child).refresh();
			}
		}
	}

	protected Panel panel;

}