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

package com.liferay.ide.layouttpl.ui.draw2d;

import org.eclipse.draw2d.AbstractBackground;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

/**
 * @author Gregory Amerson
 */
public class PortletLayoutPanel extends Panel {

	public PortletLayoutPanel() {
	}

	public PortletLayoutPanel(boolean top, boolean bottom) {
		this.top = top;
		this.bottom = bottom;
	}

	public boolean isBottom() {
		return bottom;
	}

	public boolean isTop() {
		return top;
	}

	public void setBottom(boolean bottom) {
		this.bottom = bottom;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	@Override
	protected void paintFigure(Graphics graphics) {
		if (isOpaque()) {
			graphics.setAntialias(SWT.ON);

			if (!top && !bottom) {
				graphics.fillRectangle(getBounds());
			}
			else {
				Rectangle normalFillRectangle = new Rectangle(getBounds());
				Rectangle roundedFillRectangle = new Rectangle(getBounds());
				int remainder = normalFillRectangle.height % 2;
				int halfHeight = normalFillRectangle.height / 2;

				if (top && !bottom) {
					roundedFillRectangle.height = (roundedFillRectangle.height / 2) + remainder;
					normalFillRectangle.y = normalFillRectangle.y + halfHeight - 10;
					normalFillRectangle.height = halfHeight + remainder + 10;

					graphics.fillRectangle(normalFillRectangle);
				}
				else if (!top && bottom) {
					roundedFillRectangle.height = (roundedFillRectangle.height / 2) + remainder;
					roundedFillRectangle.y = roundedFillRectangle.y + halfHeight;
					normalFillRectangle.height = halfHeight + remainder + 10;

					graphics.fillRectangle(normalFillRectangle);
				}

				graphics.fillRoundRectangle(roundedFillRectangle, 10, 10);
			}
		}

		if (getBorder() instanceof AbstractBackground) {
			AbstractBackground backgroundBorder = (AbstractBackground)getBorder();

			backgroundBorder.paintBackground(this, graphics, NO_INSETS);
		}
	}

	protected void setup() {
	}

	protected boolean bottom = false;
	protected boolean top = false;

}