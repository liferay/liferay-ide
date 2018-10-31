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

import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageActionListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageValidationListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Andy Wu
 * @author Simon Jiang
 */
public class GearControl
	extends AbstractCanvas implements PageNavigatorListener, PageActionListener, PageValidationListener {

	public GearControl(Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);

		init();

		scheduleRun();
	}

	public void addSelectionChangedListener(SelectionChangedListener listener) {
		_selectionChangedListeners.add(listener);
	}

	public final int getSelection() {
		return _selection;
	}

	@Override
	public void onPageAction(PageActionEvent event) {
		int targetPageIndex = event.getTargetPageIndex();

		_needRedraw = true;

		if (targetPageIndex != NONE) {
			setSelection(targetPageIndex);
		}
	}

	@Override
	public void onPageNavigate(PageNavigateEvent event) {
		setSelection(event.getTargetPage());
	}

	@Override
	public void onValidation(PageValidateEvent event) {
		String pageId = event.getPageId();

		_validationMessageMap.put(pageId, event);

		Page page = UpgradeView.getPage(_selection);

		if (pageId.equals(page.getPageId())) {
			_needRedraw = true;
		}
	}

	public void restart() {
		_angle = 0;
		_speed = 0;
	}

	public final void setSelection(int selection) {
		_hover = NONE;
		_oldHover = NONE;

		if (selection < 0) {
			selection = 0;
			_overflow = true;
		}
		else if (selection > (_gearMaxNumber - 1)) {
			selection = _gearMaxNumber - 1;
			_overflow = true;
		}

		if (_overflow) {
			_overflow = false;

			while (needRedraw()) {
			}

			_overflow = true;

			return;
		}

		_oldSelection = _selection;

		_selection = selection;

		for (SelectionChangedListener listener : _selectionChangedListeners) {
			listener.onSelectionChanged(selection);
		}

		restart();
	}

	protected void init() {
		super.init();

		_display = getDisplay();

		_errorImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
		_warningImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);

		_white = _display.getSystemColor(SWT.COLOR_WHITE);
		_gray = _display.getSystemColor(SWT.COLOR_GRAY);
		_darkGray = _display.getSystemColor(SWT.COLOR_DARK_GRAY);

		Font initialFont = getFont();

		FontData[] fontData = initialFont.getFontData();

		for (FontData data : fontData) {
			data.setHeight(16);
			data.setStyle(SWT.BOLD);
		}

		baseFont = new Font(_display, fontData);

		_numberFont = createFont(24);
		_tooltipFont = createFont(24);

		_radius = 32;

		setSize((int)(_gearMaxNumber * 2 * _radius), (int)(2 * _radius));

		// Not selected.

		_gearBackground[0] = createColor(169, 171, 202);
		_gearForeground[0] = createColor(140, 132, 171);

		// Selected.

		_gearBackground[1] = createColor(247, 148, 30);
		_gearForeground[1] = createColor(207, 108, 0);

		_tooltipColor = createColor(253, 232, 206);
	}

	@Override
	protected boolean needRedraw() {
		boolean retVal = false;

		if (_needRedraw) {
			_needRedraw = false;
			retVal = true;
		}

		if (_overflow) {
			_overflow = false;
			retVal = true;
		}

		if (_hover != _oldHover) {
			retVal = true;
		}

		if (_speed >= _teethAngle) {
			_startAnimation = 0;

			return retVal;
		}

		long now = currentTime;

		if (_startAnimation == 0) {
			_startAnimation = now;
		}

		long timeSinceStart = now - _startAnimation;

		_speed = timeSinceStart * _teethAngle / 400;

		_angle += _speed;

		return true;
	}

	@Override
	protected void onMouseDown(int x, int y) {
		if ((x != Integer.MIN_VALUE) && (y != Integer.MIN_VALUE)) {
			GC gc = new GC(this);

			for (int i = 0; i < _gearPaths.length; i++) {
				Path path = _gearPaths[i];

				if ((path != null) && path.contains(x, y, gc, false)) {
					if (i != getSelection()) {
						setSelection(i);
					}
				}
			}
		}
	}

	@Override
	protected void onMouseMove(int x, int y) {
		if ((x != Integer.MIN_VALUE) && (y != Integer.MIN_VALUE)) {
			GC gc = new GC(this);

			int number = UpgradeView.getPageNumber();

			for (int i = 0; i < number; i++) {
				Path path = _gearPaths[i];

				if ((path != null) && path.contains(x, y, gc, false)) {
					if (i != _hover) {
						_hover = i;
					}

					return;
				}
			}
		}

		_hover = NONE;
	}

	@Override
	protected void paint(GC gc) {
		gc.setFont(getBaseFont());
		gc.setLineWidth(3);
		gc.setAntialias(SWT.ON);

		int alpha = Math.min((int)(255 * _speed / _teethAngle), 255);

		int number = UpgradeView.getPageNumber();

		for (int i = 0; i < number; i++) {
			_tooltipPoints[i] = _paintGear(gc, i, alpha);
		}

		if ((_hover >= 0) && (_hover < _tooltipPoints.length)) {
			Point point = _tooltipPoints[_hover];

			Page page = UpgradeView.getPage(_hover);

			String title = page.getTitle();

			gc.setFont(_tooltipFont);
			gc.setForeground(_darkGray);
			gc.setBackground(_tooltipColor);

			Rectangle rectangle = drawText(gc, point.x, point.y + 14, title, 2);

			gc.setForeground(_gray);
			gc.setLineWidth(1);
			gc.drawRectangle(rectangle);
		}

		_paintValidationMessage(gc);

		_oldHover = _hover;
	}

	private Path _drawGear(
		GC gc, Display display, double cx, double cy, double outerR, double innerR, float angleOffset) {

		double radian2 = _teethAngle / 2 * _radian;
		double radian3 = .06;

		Path path = new Path(display);

		for (int i = 0; i < _teeth; i++) {
			double radian = (i * _teethAngle + angleOffset) * _radian;

			double x = cx + outerR * Math.cos(radian);
			double y = cy - outerR * Math.sin(radian);

			if (i == 0) {
				path.moveTo((int)x, (int)y);
			}

			double r1 = radian + radian3;

			double r3 = radian + radian2;

			double r2 = r3 - radian3;
			double r4 = r3 + radian2;

			x = cx + innerR * Math.cos(r1);
			y = cy - innerR * Math.sin(r1);

			path.lineTo((int)x, (int)y);

			x = cx + innerR * Math.cos(r2);
			y = cy - innerR * Math.sin(r2);

			path.lineTo((int)x, (int)y);

			x = cx + outerR * Math.cos(r3);
			y = cy - outerR * Math.sin(r3);

			path.lineTo((int)x, (int)y);

			x = cx + outerR * Math.cos(r4);
			y = cy - outerR * Math.sin(r4);

			path.lineTo((int)x, (int)y);
		}

		path.close();

		gc.fillPath(path);
		gc.drawPath(path);

		return path;
	}

	private Point _paintBadge(GC gc, double x, double y, double outerR, int i, int alpha) {
		if (_selection >= _gearMaxNumber) {
			gc.setAlpha(255 - alpha);
		}
		else if (_oldSelection >= _gearMaxNumber) {
			gc.setAlpha(alpha);
		}

		Image badgeImage = null;

		Page page = UpgradeView.getPage(i);

		PageAction pageAction = page.getSelectedAction();

		if (pageAction != null) {
			badgeImage = pageAction.getBageImage();
		}

		if (badgeImage != null) {
			Rectangle rectangle = badgeImage.getBounds();

			gc.drawImage(badgeImage, (int)(x - rectangle.width / 2), (int)(y - outerR - 12));

			gc.setAlpha(255);
		}

		return new Point((int)x, (int)(y + outerR));
	}

	private Point _paintGear(GC gc, int i, int alpha) {
		double offset = 2 * i * _radius;

		double x = _border + _radius + offset;

		double y = _border + _radius;
		double r2 = (double)_radius * .8F;
		double r3 = (double)_radius * .5F;

		int selected = 0;
		double factor = 1;

		if (i == _oldSelection) {
			if (_speed < (_teethAngle / 2)) {
				selected = 1;
			}
		}
		else if (i == _selection) {
			if (_speed >= (_teethAngle / 2)) {
				selected = 1;
				factor += (_teethAngle - _speed) * .02;
			}
			else {
				factor += _speed * .02;
			}
		}

		boolean hovered = false;

		if (i == _hover) {
			factor += .1;
			_oldHover = _hover;

			if (selected == 0) {
				hovered = true;
			}
		}

		double outerR = factor * _radius;
		double innerR = factor * r2;
		float angleOffset = (_angle + i * _teethAngle) * (i % 2 == 1 ? -1 : 1) / 7;

		gc.setForeground(hovered ? _darkGray : _gearForeground[selected]);
		gc.setBackground(hovered ? _gray : _gearBackground[selected]);

		if (outerR < 32.0) {
			outerR = 32.0D;
		}

		if (innerR < 25.0) {
			innerR = 25.600000381469727D;
		}

		Path path = _drawGear(gc, _display, x, y, outerR, innerR, angleOffset);

		if (_gearPaths[i] != null) {
			_gearPaths[i].dispose();
		}

		_gearPaths[i] = path;

		int ovalX = (int)(x - factor * r3);
		int ovalY = (int)(y - factor * r3);
		int ovalR = (int)(2 * factor * r3);
		gc.setBackground(_white);
		gc.fillOval(ovalX, ovalY, ovalR, ovalR);
		gc.drawOval(ovalX, ovalY, ovalR, ovalR);

		if (i < _gearMaxNumber) {
			String number = String.valueOf(i + 1);

			gc.setForeground(selected == 1 ? _gearForeground[1] : _gray);
			gc.setFont(_numberFont);

			drawText(gc, x, y - 1, number);
		}

		return _paintBadge(gc, x, y, outerR, i, alpha);
	}

	private void _paintValidationMessage(GC gc) {
		Page page = UpgradeView.getPage(_selection);

		String pageId = page.getPageId();

		PageValidateEvent event = _validationMessageMap.get(pageId);

		if (event != null) {
			String message = event.getMessage();

			if ((message != null) && !message.equals("ok")) {
				if (PageValidateEvent.error.equals(event.getType())) {
					drawImage(gc, _errorImage, 30, 130);
				}
				else {
					drawImage(gc, _warningImage, 30, 130);
				}

				gc.setBackground(_tooltipColor);
				gc.setForeground(_darkGray);
				gc.setLineWidth(1);

				Rectangle rectangle = drawTextNotCenter(gc, 40, 120, message, 2);

				gc.drawRectangle(rectangle);
			}
		}
	}

	private float _angle;
	private final int _border = 20;
	private Color _darkGray;
	private Display _display;
	private Image _errorImage;
	private final Color[] _gearBackground = new Color[2];
	private final Color[] _gearForeground = new Color[2];
	private int _gearMaxNumber = 10;
	private final Path[] _gearPaths = new Path[_gearMaxNumber];
	private Color _gray;
	private int _hover = NONE;
	private boolean _needRedraw = false;
	private Font _numberFont;
	private int _oldHover = NONE;
	private int _oldSelection = NONE;
	private boolean _overflow;
	private final double _radian = 2 * Math.PI / 360;
	private float _radius;
	private int _selection;
	private final List<SelectionChangedListener> _selectionChangedListeners = Collections.synchronizedList(
		new ArrayList<SelectionChangedListener>());
	private float _speed;
	private long _startAnimation;
	private final int _teeth = 8;
	private final float _teethAngle = 360 / _teeth;
	private Color _tooltipColor;
	private Font _tooltipFont;
	private final Point[] _tooltipPoints = new Point[_gearMaxNumber];
	private Map<String, PageValidateEvent> _validationMessageMap = new HashMap<>();
	private Image _warningImage;
	private Color _white;

}