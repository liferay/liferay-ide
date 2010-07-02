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

import com.liferay.ide.eclipse.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.eclipse.layouttpl.ui.model.ModelElement;
import com.liferay.ide.eclipse.layouttpl.ui.policies.LayoutTplDiagramLayoutEditPolicy;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.swt.graphics.Color;

public class LayoutTplDiagramEditPart extends BaseGraphicalEditPart {

	public static final int DIAGRAM_MARGIN = 10;

	protected Panel diagramPanel;

	public int getContainerWidth() {
		return diagramPanel.getSize().width - (DIAGRAM_MARGIN * 2);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();

		if (LayoutTplDiagram.ROW_ADDED_PROP.equals(prop) || LayoutTplDiagram.ROW_REMOVED_PROP.equals(prop)) {
			refreshChildren();
			List rows = getChildren();

			if (rows.size() > 0) {
				for (Object row : rows) {
					AbstractEditPart rowPart = (AbstractEditPart) row;
					List cols = rowPart.getChildren();

					if (cols.size() > 0) {
						for (Object col : cols) {
							((AbstractEditPart) col).refresh();
						}
					}

					((AbstractEditPart) row).refresh();
				}
			}
			refreshVisuals();
		}
	}

	protected void createEditPolicies() {
		// disallows the removal of this edit part from its parent
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());

		// handles constraint changes (e.g. moving and/or resizing) of model
		// elements and creation of new model elements
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutTplDiagramLayoutEditPolicy());
	}

	protected IFigure createFigure() {
		diagramPanel = new Panel();
		configureDiagramPanel(diagramPanel);

		return diagramPanel;
	}

	protected void configureDiagramPanel(Panel panel) {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;

		panel.setLayoutManager(gridLayout);
		panel.setBackgroundColor(new Color(null, 10, 10, 10));
		panel.setBorder(new MarginBorder(DIAGRAM_MARGIN));
	}

	protected LayoutTplDiagram getCastedModel() {
		return (LayoutTplDiagram) getModel();
	}

	protected List<ModelElement> getModelChildren() {
		return getCastedModel().getRows(); // return a list of rows
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		List children = getChildren();

		for (Object child : children) {
			if (child instanceof AbstractEditPart) {
				((AbstractEditPart) child).refresh();
			}
		}
	}

}
