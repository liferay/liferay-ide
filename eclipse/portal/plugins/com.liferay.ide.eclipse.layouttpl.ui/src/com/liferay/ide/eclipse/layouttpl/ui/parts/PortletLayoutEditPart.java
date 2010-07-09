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

import com.liferay.ide.eclipse.layouttpl.ui.draw2d.PortletLayoutPanel;
import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.eclipse.layouttpl.ui.policies.PortletLayoutLayoutEditPolicy;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;


public class PortletLayoutEditPart extends BaseGraphicalEditPart {

	public static final int LAYOUT_MARGIN = 10;
	public static final int COLUMN_SPACING = 10;

	protected PortletLayoutPanel layoutPanel;

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

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new PortletLayoutLayoutEditPolicy());
	}

	protected LayoutTplDiagram getParentModel() {
		return (LayoutTplDiagram) getParent().getModel();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();

		if (PortletLayout.COLUMN_ADDED_PROP.equals(prop) || PortletLayout.COLUMN_REMOVED_PROP.equals(prop) ||
			PortletLayout.CHILD_COLUMN_WEIGHT_CHANGED_PROP.equals(prop)) {
			refreshChildren();
			refreshVisuals();
		}
	}

	protected PortletLayout getCastedModel() {
		return (PortletLayout) getModel();
	}

	protected List<ModelElement> getModelChildren() {
		return getCastedModel().getColumns(); // return a list of columns
	}

	protected PortletLayoutPanel getCastedFigure() {
		return (PortletLayoutPanel) getFigure();
	}

	public static GridData createGridData() {
		return new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();

		GridData gd = createGridData();
		((GraphicalEditPart) getParent()).setLayoutConstraint(this, layoutPanel, gd);

		List rows = getParent().getChildren();

		if (rows.size() == 1) {
			layoutPanel.setTop(true);
			layoutPanel.setBottom(true);
		}
		else if (this.equals(rows.get(0))) {
			layoutPanel.setTop(true);
			layoutPanel.setBottom(false);
		}
		else if (this.equals(rows.get(rows.size() - 1))) {
			layoutPanel.setTop(false);
			layoutPanel.setBottom(true);
		}
		else {
			layoutPanel.setTop(false);
			layoutPanel.setBottom(false);
		}
		
		PortletLayoutPanel panel = getCastedFigure();
		GridLayout gridLayout = (GridLayout) panel.getLayoutManager();
		List columns = getChildren();

		int numColumns = columns.size();

		// need to rebuild column widths based on weight
		if (numColumns > 1) {
			// get width of our own part to calculate new width
			int rowWidth = this.getFigure().getSize().width - (PortletLayoutEditPart.LAYOUT_MARGIN * 2);

			for (Object col : columns) {
				PortletColumnEditPart portletColumnPart = (PortletColumnEditPart) col;
				PortletColumn column = (PortletColumn) portletColumnPart.getModel();
				// if (column.getWeight() == PortletColumn.DEFAULT_WEIGHT) {
				// column.setWeight(100);
				// }
				GridData rowData = portletColumnPart.createGridData();

				double percent = column.getWeight() / 100d;
				rowData.widthHint = (int) (percent * rowWidth) - (COLUMN_SPACING * 2);
				this.setLayoutConstraint(portletColumnPart, portletColumnPart.getFigure(), rowData);
			}
		}

		gridLayout.numColumns = numColumns;
		gridLayout.invalidate();

		this.getFigure().repaint();
	}

	public int getDefaultColumnHeight() {
		return getCastedParent().getPreferredColumnHeight();
	}

	private LayoutTplDiagramEditPart getCastedParent() {
		return (LayoutTplDiagramEditPart) getParent();
	}

	public Object getLayoutConstraint(PortletColumnEditPart columnPart, IFigure figure) {
		if (getChildren().contains(columnPart)) {
			return getFigure().getLayoutManager().getConstraint(figure);
		}

		return null;
	}

}
