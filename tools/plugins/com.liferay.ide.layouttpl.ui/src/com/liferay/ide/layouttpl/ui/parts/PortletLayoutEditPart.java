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

import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;
import com.liferay.ide.layouttpl.ui.draw2d.PortletLayoutPanel;

import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
public class PortletLayoutEditPart extends BaseGraphicalEditPart {

	public static final int COLUMN_SPACING = 5;

	public static final int LAYOUT_MARGIN = 5;

	public static GridData createGridData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
	}

	public int getDefaultColumnHeight() {
		return _getCastedParent().getPreferredColumnHeight();
	}

	public Object getLayoutConstraint(PortletColumnEditPart columnPart, IFigure figure) {
		if (getChildren().contains(columnPart)) {
			LayoutManager layoutManager = getFigure().getLayoutManager();

			return layoutManager.getConstraint(figure);
		}

		return null;
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected IFigure createFigure() {
		GridLayout gridLayout = new GridLayout(1, false);

		gridLayout.horizontalSpacing = COLUMN_SPACING;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;

		layoutPanel = new PortletLayoutPanel();

		layoutPanel.setOpaque(true);
		layoutPanel.setBorder(new MarginBorder(LAYOUT_MARGIN));
		layoutPanel.setBackgroundColor(new Color(null, 171, 171, 171));
		layoutPanel.setLayoutManager(gridLayout);

		return layoutPanel;
	}

	protected PortletLayoutPanel getCastedFigure() {
		return (PortletLayoutPanel)getFigure();
	}

	protected PortletLayoutElement getCastedModel() {
		return (PortletLayoutElement)getModel();
	}

	protected ElementList<PortletColumnElement> getModelChildren() {
		return getCastedModel().getPortletColumns();
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected void refreshVisuals() {
		super.refreshVisuals();

		GridData gd = createGridData();

		GraphicalEditPart editParent = (GraphicalEditPart)getParent();

		editParent.setLayoutConstraint(this, layoutPanel, gd);

		List rows = getParent().getChildren();

		if (rows.size() == 1) {
			layoutPanel.setTop(true);
			layoutPanel.setBottom(true);
		}
		else if (equals(rows.get(0))) {
			layoutPanel.setTop(true);
			layoutPanel.setBottom(false);
		}
		else if (equals(rows.get(rows.size() - 1))) {
			layoutPanel.setTop(false);
			layoutPanel.setBottom(true);
		}
		else {
			layoutPanel.setTop(false);
			layoutPanel.setBottom(false);
		}

		PortletLayoutPanel panel = getCastedFigure();

		GridLayout gridLayout = (GridLayout)panel.getLayoutManager();

		List columns = getChildren();

		int numColumns = columns.size();

		// need to rebuild column widths based on weight

		if (numColumns > 0) {

			/**
			 * get width of our own part to calculate new width this method is
			 * invoked recursively, so it's complicated to compute the very
			 * exact width, sometimes minus 2 times of margin causes sidelines
			 * cannot be shown, minus 3 times of margin looks better
			 */
			IFigure parentFigure = getFigure().getParent();

			Dimension size = parentFigure.getSize();

			int rowWidth = size.width - (PortletLayoutEditPart.LAYOUT_MARGIN * 3);

			if (rowWidth > 0) {
				for (Object col : columns) {
					PortletColumnEditPart portletColumnPart = (PortletColumnEditPart)col;

					PortletColumnElement column = (PortletColumnElement)portletColumnPart.getModel();
					GridData rowData = portletColumnPart.createGridData();

					Value<Integer> weight = column.getWeight();

					Integer weightValue = weight.content();

					Value<Integer> fullWeight = column.getFullWeight();

					Integer fullWeightValue = fullWeight.content();

					double percent = weightValue.doubleValue() / fullWeightValue.doubleValue();

					int standardRowWidth = (int)(percent * rowWidth);

					rowData.widthHint = standardRowWidth - COLUMN_SPACING * 2;

					IFigure columnFigure = portletColumnPart.getFigure();

					Dimension columnFigureSize = columnFigure.getSize();

					columnFigure.setSize(rowData.widthHint, columnFigureSize.height);

					/**
					 * this.setLayoutConstraint( portletColumnPart,
					 * columnFigure, rowData );
					 */
					portletColumnPart.refresh();
				}
			}

			gridLayout.numColumns = numColumns;
			getFigure().repaint();
		}
	}

	protected PortletLayoutPanel layoutPanel;

	private PortletRowLayoutEditPart _getCastedParent() {
		return (PortletRowLayoutEditPart)getParent();
	}

}