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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class NavigatorControl extends AbstractCanvas implements SelectionChangedListener {

	public static final int BORDER = 30;

	public NavigatorControl(Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);

		init();

		scheduleRun();
	}

	public void addPageActionListener(PageActionListener listener) {
		this._actionListeners.add(listener);
	}

	public void addPageNavigateListener(PageNavigatorListener listener) {
		this._naviListeners.add(listener);
	}

	public final int getAction(int x, int y) {
		PageAction[] actions = _getSelectedPage().getActions();

		if ((actions == null) || (actions.length < 1)) {
			return NONE;
		}

		for (int i = 0; i < actions.length; i++) {
			Rectangle box = _actionBoxes[i];

			if ((box != null) && box.contains(x, y)) {
				return i;
			}
		}

		return NONE;
	}

	@Override
	public void onSelectionChanged(int targetSelection) {
		_select = targetSelection;

		_needRedraw = true;
	}

	public Rectangle paintAction(GC gc, int index, int x, int y, boolean hovered, boolean selected, PageAction action) {
		Image[] images = action.getImages();

		Image image = images[0];

		if (hovered) {
			image = images[2];
		}
		else if (selected) {
			image = images[1];
		}

		return drawImage(gc, image, x, y);
	}

	protected boolean actionOnMouseDown(int x, int y) {
		int i = getAction(x, y);

		if (i != NONE) {
			_action(i);

			return true;
		}

		return false;
	}

	protected int actionOnMouseMove(int x, int y) {
		int i = getAction(x, y);

		if (i != NONE) {

			// pageBufferUpdated = false;

			return _CHOICES - i;
		}

		return NONE;
	}

	protected void init() {
		super.init();

		_backImages[0] = loadImage("back.png");
		_backImages[1] = loadImage("back_hover.png");

		_nextImages[0] = loadImage("next.png");
		_nextImages[1] = loadImage("next_hover.png");

		Rectangle rectangle = _nextImages[0].getBounds();

		_buttonR = rectangle.height / 2;

		_answerY = 5 + _buttonR;

		_actionBoxes = new Rectangle[2];
	}

	@Override
	protected boolean needRedraw() {
		boolean retVal = false;

		if (_needRedraw) {
			_needRedraw = false;
			retVal = true;
		}

		if (_hover != _oldHover) {
			retVal = true;
		}

		return retVal;
	}

	@Override
	protected void onMouseDown(int x, int y) {
		boolean navigate = false;

		if ((x != Integer.MIN_VALUE) && (y != Integer.MIN_VALUE)) {
			Page page = _getSelectedPage();

			if (page != null) {
				PageNavigateEvent event = new PageNavigateEvent();

				if (page.showBackPage() && (_backBox != null) && _backBox.contains(x, y)) {
					event.setTargetPage(_select - 1);

					navigate = true;
				}

				if (page.showNextPage() && (_nextBox != null) && _nextBox.contains(x, y)) {
					event.setTargetPage(_select + 1);

					navigate = true;
				}

				if (navigate) {
					for (PageNavigatorListener listener : _naviListeners) {
						listener.onPageNavigate(event);
					}
				}

				actionOnMouseDown(x, y);
			}
		}
	}

	@Override
	protected void onMouseMove(int x, int y) {
		if ((x != Integer.MIN_VALUE) && (y != Integer.MIN_VALUE)) {
			Page page = _getSelectedPage();

			if (page != null) {
				if (page.showBackPage() && (_backBox != null) && _backBox.contains(x, y)) {
					_hover = _BUTTON_BACK;

					return;
				}

				if (page.showNextPage() && (_nextBox != null) && _nextBox.contains(x, y)) {
					_hover = _BUTTON_NEXT;

					return;
				}

				_hover = actionOnMouseMove(x, y);

				return;
			}
		}
		else {
			_hover = NONE;
		}
	}

	@Override
	protected void paint(GC gc) {
		gc.setFont(getBaseFont());
		gc.setLineWidth(3);
		gc.setAntialias(SWT.ON);

		Page page = _getSelectedPage();

		_backBox = null;
		_nextBox = null;

		if (page.showBackPage()) {
			_backBox = drawImage(
				gc, _backImages[_hover == _BUTTON_BACK ? 1 : 0], getBounds().width / 2 - 200, _answerY);
		}

		if (page.showNextPage()) {
			_nextBox = drawImage(
				gc, _nextImages[_hover == _BUTTON_NEXT ? 1 : 0], getBounds().width / 2 + 200, _answerY);
		}

		_paintActions(gc, page);

		_oldHover = _hover;
	}

	private void _action(int i) {
		Page page = _getSelectedPage();

		PageAction oldSelection = page.getSelectedAction();

		PageAction[] pageActions = page.getActions();

		PageAction targetAction = pageActions[i];

		if (targetAction.equals(oldSelection)) {
			targetAction = null;
		}

		page.setSelectedAction(targetAction);

		PageActionEvent event = new PageActionEvent();

		event.setAction(targetAction);
		event.setTargetPageIndex(NONE);

		if (page.showNextPage() && (targetAction != null)) {
			event.setTargetPageIndex(_select + 1);
		}

		for (PageActionListener listener : _actionListeners) {
			listener.onPageAction(event);
		}

		_needRedraw = true;
	}

	private Page _getSelectedPage() {
		return UpgradeView.getPage(_select);
	}

	private void _paintActions(GC gc, Page page) {
		PageAction[] actions = page.getActions();
		PageAction selectedAction = page.getSelectedAction();

		if (actions == null) {
			return;
		}

		boolean[] selecteds = new boolean[actions.length];
		boolean[] hovereds = new boolean[actions.length];

		Point[] sizes = new Point[actions.length];

		// int width = ( actions.length - 1 ) * BORDER;

		int height = 0;

		for (int i = 0; i < actions.length; i++) {
			selecteds[i] = actions[i].equals(selectedAction);

			if ((_CHOICES - i) == _hover) {

				// oldHover = hover;

				hovereds[i] = true;
			}

			sizes[i] = actions[i].getSize();

			// width += sizes[i].x;

			height = Math.max(height, sizes[i].y);
		}

		int x = getBounds().width / 2 - 40;

		int y = _answerY - _pageY;

		for (int i = 0; i < actions.length; i++) {
			PageAction action = actions[i];

			_actionBoxes[i] = paintAction(gc, i, x, y, hovereds[i], selecteds[i], action);

			x = getBounds().width / 2 + 40;
		}
	}

	private static final int _BUTTON_BACK = NONE - 1;

	private static final int _BUTTON_NEXT = _BUTTON_BACK - 1;

	private static final int _CHOICES = _BUTTON_NEXT - 1;

	private Rectangle[] _actionBoxes;
	private final List<PageActionListener> _actionListeners = Collections.synchronizedList(
		new ArrayList<PageActionListener>());
	private int _answerY;
	private Rectangle _backBox;
	private final Image[] _backImages = new Image[2];
	private int _buttonR;
	private int _hover = NONE;
	private final List<PageNavigatorListener> _naviListeners = Collections.synchronizedList(
		new ArrayList<PageNavigatorListener>());
	private boolean _needRedraw = false;
	private Rectangle _nextBox;
	private final Image[] _nextImages = new Image[2];
	private int _oldHover = NONE;
	private int _pageY;
	private int _select = 0;

}