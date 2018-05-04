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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.ProjectUI;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import org.osgi.framework.Bundle;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public abstract class AbstractCanvas extends Canvas {

	public static final int NONE = -1;

	public static Rectangle drawText(GC gc, double cX, double cY, String text) {
		return drawText(gc, cX, cY, text, 0);
	}

	public static Rectangle drawText(GC gc, double cX, double cY, String text, int box) {
		Point extent = gc.stringExtent(text);

		int x = (int)(cX - extent.x / 2);

		int y = (int)(cY - extent.y / 2);

		if (x < box) {
			x = box;
		}

		Rectangle rectangle = new Rectangle(x, y, extent.x, extent.y);

		if (box > 0) {
			rectangle.x -= box;
			rectangle.y -= box;
			rectangle.width += 2 * box;
			rectangle.height += 2 * box;

			gc.fillRectangle(rectangle);
		}

		gc.drawText(text, x, y, true);

		return rectangle;
	}

	public AbstractCanvas(Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);

		addFocusListener(
			new FocusListener() {

				public void focusGained(FocusEvent e) {
					redraw();
				}

				public void focusLost(FocusEvent e) {
					redraw();
				}

			});

		addDisposeListener(
			new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {
					dispose();
				}

			});

		addMouseTrackListener(
			new MouseTrackAdapter() {

				@Override
				public void mouseExit(MouseEvent e) {
					onMouseMove(Integer.MIN_VALUE, Integer.MIN_VALUE);
				}

			});

		addMouseMoveListener(
			new MouseMoveListener() {

				public void mouseMove(MouseEvent e) {
					onMouseMove(e.x, e.y);
				}

			});

		addMouseListener(
			new MouseAdapter() {

				@Override
				public void mouseDown(MouseEvent e) {

					// left button

					if (e.button == 1) {
						onMouseDown(e.x, e.y);
					}
				}

			});

		addPaintListener(
			new PaintListener() {

				@Override
				public void paintControl(PaintEvent e) {
					Image buffer = new Image(getDisplay(), getBounds());

					GC canvasGc = e.gc;

					GC bufferGC = new GC(buffer);

					bufferGC.setAdvanced(true);
					bufferGC.setBackground(canvasGc.getBackground());
					bufferGC.fillRectangle(buffer.getBounds());

					paint(bufferGC);

					canvasGc.drawImage(buffer, 0, 0);

					bufferGC.dispose();
					buffer.dispose();

					scheduleRun();
				}

			});
	}

	@Override
	public void dispose() {
		for (Resource resource : _resources) {
			if ((resource != null) && !resource.isDisposed()) {
				resource.dispose();
			}
		}
	}

	public Rectangle drawImage(GC gc, Image image, int cX, int cY) {
		Rectangle bounds = image.getBounds();

		cX -= bounds.width / 2;
		cY -= bounds.height / 2;

		gc.drawImage(image, cX, cY);

		return new Rectangle(cX, cY, bounds.width, bounds.height);
	}

	protected final Color createColor(int r, int g, int b) {
		Display display = getDisplay();

		Color color = new Color(display, r, g, b);

		_resources.add(color);

		return color;
	}

	protected final Font createFont(int pixelHeight) {
		return createFont(pixelHeight, 0);
	}

	protected final Font createFont(int pixelHeight, int pixelWidth, String... testStrings) {
		if (testStrings.length == 0) {
			pixelWidth = Integer.MAX_VALUE;
			testStrings = new String[] {"Ag"};
		}

		Display display = getDisplay();

		GC fontGC = new GC(display);

		try {
			FontData[] fontData = baseFont.getFontData();
			int fontSize = 40;

			while (fontSize > 0) {
				for (int i = 0; i < fontData.length; i++) {
					fontData[i].setHeight(fontSize);
					fontData[i].setStyle(SWT.BOLD);
				}

				Font font = new Font(display, fontData);

				fontGC.setFont(font);

				if (_isFontSmallEnough(pixelHeight, pixelWidth, fontGC, testStrings)) {
					_resources.add(font);

					return font;
				}

				font.dispose();

				--fontSize;
			}

			throw new RuntimeException("Could not create font: " + pixelHeight);
		}
		finally {
			fontGC.dispose();
		}
	}

	protected synchronized void doRun() {
		if (isDisposed()) {
			return;
		}

		currentTime += 20;

		boolean needsRedraw = needRedraw();

		if (needsRedraw) {
			redraw();
		}
		else {
			scheduleRun();
		}
	}

	protected Rectangle drawTextNotCenter(GC gc, int cX, int cY, String text, int box) {
		int maxWidth = getSize().x - 60;

		String line1 = null;
		String line2 = null;

		boolean multipleLine = false;

		Point extent = gc.stringExtent(text);

		int width = extent.x;
		int height = extent.y;

		if (extent.x > maxWidth) {
			line1 = text.substring(0, text.length() / 2);
			line2 = text.substring(text.length() / 2, text.length());

			Point line1Point = gc.stringExtent(line1);
			Point line2Point = gc.stringExtent(line2);

			width = Math.max(line1Point.x, line2Point.x);
			height = line1Point.y + line2Point.y;

			multipleLine = true;
		}

		int x = (int)(cX - extent.x / 2);

		if (x < box) {
			x = box;
		}

		int cyFinal = cY;

		if (multipleLine) {
			cyFinal -= extent.y;
		}

		Rectangle rectangle = new Rectangle(cX, cyFinal, width, height);

		if (box > 0) {
			rectangle.x -= box;
			rectangle.y -= box;
			rectangle.width += 2 * box;
			rectangle.height += 2 * box;

			gc.fillRectangle(rectangle);
		}

		if (multipleLine) {
			gc.drawText(line1, cX, cyFinal, true);
			gc.drawText(line2, cX, cY, true);
		}
		else {
			gc.drawText(text, cX, cY, true);
		}

		return rectangle;
	}

	protected final Font getBaseFont() {
		return baseFont;
	}

	protected void init() {
		Display display = getDisplay();

		Font initialFont = getFont();

		FontData[] fontData = initialFont.getFontData();

		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(16);
			fontData[i].setStyle(SWT.BOLD);
		}

		baseFont = new Font(display, fontData);
	}

	protected final Image loadImage(String name) {
		URL url = null;

		try {
			Bundle bundle = ProjectUI.getDefault().getBundle();

			url = bundle.getEntry("images/" + name);
		}
		catch (Exception e) {
		}

		ImageDescriptor imagedesc = ImageDescriptor.createFromURL(url);

		Image image = imagedesc.createImage();

		_resources.add(image);

		return image;
	}

	protected abstract boolean needRedraw();

	protected abstract void onMouseDown(int x, int y);

	protected abstract void onMouseMove(int x, int y);

	protected abstract void paint(GC bufferGC);

	protected void scheduleRun() {
		getDisplay().timerExec(_DEFAULT_TIMER_INTERVAL, _runnable);
	}

	protected Font baseFont;
	protected long currentTime = 0L;

	private boolean _isFontSmallEnough(int pixelHeight, int pixelWidth, GC fontGC, String[] testStrings) {
		for (String testString : testStrings) {
			Point extent = fontGC.stringExtent(testString);

			if ((extent.y > pixelHeight) || (extent.x > pixelWidth)) {
				return false;
			}
		}

		return true;
	}

	private static final int _DEFAULT_TIMER_INTERVAL = 10;

	private final List<Resource> _resources = new ArrayList<>();

	private final Runnable _runnable = new Runnable() {

		public void run() {
			doRun();
		}

	};

}