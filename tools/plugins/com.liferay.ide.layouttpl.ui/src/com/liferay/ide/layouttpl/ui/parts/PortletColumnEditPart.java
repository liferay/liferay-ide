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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.ui.draw2d.ColumnFigure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.sapphire.Value;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
public class PortletColumnEditPart extends PortletRowLayoutEditPart {

	public static final int COLUMN_MARGIN = 5;

	public PortletColumnEditPart() {
	}

	public GridData createGridData() {
		GridData gd = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);

		gd.heightHint = getCastedParent().getDefaultColumnHeight();

		return gd;
	}

	@Override
	public PortletColumnElement getCastedModel() {
		return (PortletColumnElement)getModel();
	}

	public PortletLayoutEditPart getCastedParent() {
		return (PortletLayoutEditPart)getParent();
	}

	@Override
	public int getMargin() {
		return COLUMN_MARGIN;
	}

	@Override
	protected void createEditPolicies() {
	}

	protected IFigure createFigure() {
		IFigure f;

		if (ListUtil.isEmpty(getModelChildren())) {
			f = createFigureForModel();

			f.setOpaque(true);
		}
		else {
			f = super.createFigure();
		}

		f.setBackgroundColor(new Color(null, 232, 232, 232));

		return f;
	}

	protected Figure createFigureForModel() {
		if (getModel() instanceof PortletColumnElement) {
			RoundedRectangle rect = new ColumnFigure();

			rect.setCornerDimensions(new Dimension(10, 10));

			return rect;
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected Panel createPanel() {
		return new Panel() {

			@Override
			protected void paintFigure(Graphics graphics) {
				Rectangle r = Rectangle.SINGLETON.setBounds(getBounds());

				r.width -= 1;
				r.height -= 1;

				graphics.drawRoundRectangle(r, 10, 10);

				r.width -= 1;
				r.height -= 1;
				r.x += 1;
				r.y += 1;

				graphics.fillRoundRectangle(r, 10, 10);
			}

		};
	}

	protected void refreshVisuals() {
		super.refreshVisuals();

		IFigure parentFigure = ((GraphicalEditPart)getParent()).getFigure();

		LayoutManager layoutManager = parentFigure.getLayoutManager();

		Object constraint = layoutManager.getConstraint(getFigure());

		GridData gd = null;

		if (constraint instanceof GridData) {
			gd = (GridData)constraint;

			if (gd.heightHint == SWT.DEFAULT) {
				gd.heightHint = getCastedParent().getDefaultColumnHeight();
			}
		}
		else {
			gd = createGridData();
		}

		((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), gd);

		if (getFigure() instanceof ColumnFigure) {
			Value<Integer> weight = getCastedModel().getWeight();

			Integer weightValue = weight.content();

			((ColumnFigure)getFigure()).setText(weightValue.toString());
		}
	}

}