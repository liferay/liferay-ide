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

import org.eclipse.draw2d.Panel;
import org.eclipse.swt.graphics.Color;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 */
public class LayoutTplDiagramEditPart extends PortletRowLayoutEditPart {

	public static final int DIAGRAM_MARGIN = 5;

	public LayoutTplDiagramEditPart() {
	}

	@Override
	public int getMargin() {
		return DIAGRAM_MARGIN;
	}

	@Override
	protected void configurePanel(Panel panel) {
		super.configurePanel(panel);

		panel.setBackgroundColor(new Color(null, 10, 10, 10));
	}

	@Override
	protected void createEditPolicies() {
	}

}