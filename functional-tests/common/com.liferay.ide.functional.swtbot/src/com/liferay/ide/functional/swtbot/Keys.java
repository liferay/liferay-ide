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

package com.liferay.ide.functional.swtbot;

import java.awt.event.KeyEvent;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;

/**
 * @author Terry Jia
 */
public interface Keys {

	public final KeyStroke alt = KeyStroke.getInstance(SWT.ALT);
	public final KeyStroke ctrl = KeyStroke.getInstance(SWT.CTRL);
	public final KeyStroke enter = KeyStroke.getInstance(SWT.CR);
	public final KeyStroke f5 = KeyStroke.getInstance(SWT.F5);
	public final Keyboard keyPress = KeyboardFactory.getAWTKeyboard();
	public final KeyStroke page_down = KeyStroke.getInstance(SWT.PAGE_DOWN);
	public final KeyStroke slash = KeyStroke.getInstance('/');
	public final KeyStroke up = KeyStroke.getInstance(KeyEvent.VK_UP);

}