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

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * @author Gregory Amerson
 */
public class ColumnFigure extends RoundedRectangle {

	public ColumnFigure() {
		setAntialias(SWT.ON);

		// setText("50%");

	}

	public String getText() {
		return text;
	}

	@Override
	public void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);

		if (getText() == null) {
			return;
		}

		if (!shouldDrawText()) {
			return;
		}

		correctFont();

		if (graphics.getFont() != null) {
			graphics.setTextAntialias(SWT.ON);
			graphics.setFont(getFont());

			Dimension extent = FigureUtilities.getTextExtents(getText(), graphics.getFont());

			graphics.drawString(
				getText(), bounds.x + (bounds.width / 2) - (extent.width / 2),
				bounds.y + (bounds.height / 2) - (extent.height / 2));
		}
	}

	public void setDrawText(boolean drawText) {
		this.drawText = drawText;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean shouldDrawText() {
		return drawText;
	}

	protected void correctFont() {
		Font initialFont = getFont();

		Device device = getFont().getDevice();

		if ((initialFont != null) && !getFont().isDisposed() && !device.isDisposed()) {
			FontData[] fontData = initialFont.getFontData();

			for (FontData data : fontData) {
				int height = 24;

				data.setHeight(height);

				int width = getFontWidth(data);

				while (width > (getPreferredSize().width() - 1)) {
					height--;

					data.setHeight(height);

					width = getFontWidth(data);
				}
			}

			Font correctedFont = new Font(getFont().getDevice(), fontData);

			if (!_compareFonts(initialFont, correctedFont)) {
				setFont(correctedFont);
			}
			else {
				correctedFont.dispose();
			}
		}
	}

	protected int getFontWidth(FontData fontData) {
		Font newFont = new Font(getFont().getDevice(), fontData);

		Dimension dimension = FigureUtilities.getTextExtents(getText(), newFont);

		int width = dimension.width();

		newFont.dispose();

		return width;
	}

	protected boolean drawText = true;
	protected String text = null;

	private boolean _compareFonts(Font font1, Font font2) {
		if ((font1 == null) || (font2 == null)) {
			return false;
		}

		Device font1Device = font1.getDevice();

		if (!font1Device.equals(font2.getDevice())) {
			return false;
		}

		FontData[] data1 = font1.getFontData();
		FontData[] data2 = font2.getFontData();

		if (!(data1.length == data2.length)) {
			return false;
		}

		for (int i = 0; i < data1.length; i++) {
			if (!data1[i].equals(data2[i])) {
				return false;
			}
		}

		return true;
	}

}