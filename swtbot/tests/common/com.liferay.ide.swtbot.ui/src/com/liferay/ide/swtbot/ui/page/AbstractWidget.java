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

package com.liferay.ide.swtbot.ui.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public abstract class AbstractWidget extends BasePageObject {

	public AbstractWidget(SWTWorkbenchBot bot) {
		super(bot);
	}

	public AbstractWidget(SWTWorkbenchBot bot, int index) {
		super(bot, index);
	}

	public AbstractWidget(SWTWorkbenchBot bot, String label) {
		super(bot, label);
	}

	public AbstractWidget(SWTWorkbenchBot bot, String label, int index) {
		super(bot, label, index);
	}

	public void click(int x, int y) {
		syncExec(
			new VoidResult() {

				@Override
				public void run() {
					_moveMouse(x, y);
					_mouseDown(x, y, 1);
					_mouseUp(x, y, 1);
				}

			});
	}

	public void contextMenu(String menu) {
		SWTBotMenu botMenu = getWidget().contextMenu(menu);

		botMenu.click();
	}

	public String getLabel() {
		return label;
	}

	public String getText() {
		return getWidget().getText();
	}

	public boolean isActive() {
		return getWidget().isActive();
	}

	public boolean isEnabled() {
		return getWidget().isEnabled();
	}

	public boolean isVisible() {
		return getWidget().isVisible();
	}

	public void rightClick(String menu) {
		contextMenu(menu);
	}

	public void setFocus() {
		getWidget().setFocus();
	}

	protected void asyncExec(VoidResult toExecute) {
		UIThreadRunnable.asyncExec(getWidget().display, toExecute);
	}

	protected Event createMouseEvent(int x, int y, int button, int stateMask, int count) {
		Event event = new Event();

		event.time = (int)System.currentTimeMillis();
		event.widget = getWidget().widget;
		event.display = getWidget().display;
		event.x = x;
		event.y = y;
		event.button = button;
		event.stateMask = stateMask;
		event.count = count;

		return event;
	}

	protected abstract AbstractSWTBot<?> getWidget();

	protected void syncExec(VoidResult toExecute) {
		UIThreadRunnable.syncExec(getWidget().display, toExecute);
	}

	private void _mouseDown(int x, int y, int button) {
		asyncExec(
			new VoidResult() {

				@Override
				public void run() {
					Event event = createMouseEvent(x, y, button, 0, 0);

					event.type = SWT.MouseDown;
					getWidget().display.post(event);
				}

			});
	}

	private void _mouseUp(int x, int y, int button) {
		asyncExec(
			new VoidResult() {

				@Override
				public void run() {
					Event event = createMouseEvent(x, y, button, 0, 0);

					event.type = SWT.MouseUp;
					getWidget().display.post(event);
				}

			});
	}

	private void _moveMouse(int x, int y) {
		asyncExec(
			new VoidResult() {

				@Override
				public void run() {
					Event event = createMouseEvent(x, y, 0, 0, 0);

					event.type = SWT.MouseMove;
					getWidget().display.post(event);
				}

			});
	}

}