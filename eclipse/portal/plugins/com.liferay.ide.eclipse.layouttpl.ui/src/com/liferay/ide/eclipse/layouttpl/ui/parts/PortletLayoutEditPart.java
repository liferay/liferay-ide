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

	protected PortletLayoutPanel layoutPanel;

	@Override
	protected IFigure createFigure() {
		layoutPanel = new PortletLayoutPanel();
		layoutPanel.setOpaque(true);
		layoutPanel.setBorder(new MarginBorder(LAYOUT_MARGIN));
		layoutPanel.setBackgroundColor(new Color(null, 171, 171, 171));
		layoutPanel.setLayoutManager(new GridLayout(1, false));

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

		if (PortletLayout.COLUMN_ADDED_PROP.equals(prop) || PortletLayout.COLUMN_REMOVED_PROP.equals(prop)) {
			refreshChildren();
		}
	}

	protected PortletLayout getCastedModel() {
		return (PortletLayout) getModel();
	}

	protected List<ModelElement> getModelChildren() {
		return getCastedModel().getColumns(); // return a list of columns
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();

		((GraphicalEditPart) getParent()).setLayoutConstraint(this, layoutPanel, new GridData(
			SWT.FILL, SWT.FILL, true, false, 1, 1));

		List children = getParent().getChildren();

		if (children.size() == 1) {
			layoutPanel.setTop(true);
			layoutPanel.setBottom(true);
		}
		else if (this.equals(children.get(0))) {
			layoutPanel.setTop(true);
			layoutPanel.setBottom(false);
		}
		else if (this.equals(children.get(children.size() - 1))) {
			layoutPanel.setTop(false);
			layoutPanel.setBottom(true);
		}
		else {
			layoutPanel.setTop(false);
			layoutPanel.setBottom(false);
		}

		this.getFigure().repaint();
	}

}
