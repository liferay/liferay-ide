/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.swtbot.ui.tests;

import java.awt.event.KeyEvent;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;

/**
 * @author Terry Jia
 */
public interface Keys
{

    public Keyboard keyPress = KeyboardFactory.getAWTKeyboard();

    public KeyStroke ctrl = KeyStroke.getInstance( SWT.CTRL, 0 );
    public KeyStroke N = KeyStroke.getInstance( 'N' );
    public KeyStroke M = KeyStroke.getInstance( 'M' );
    public KeyStroke alt = KeyStroke.getInstance( SWT.ALT, 0 );
    public KeyStroke enter = KeyStroke.getInstance( KeyEvent.VK_ENTER );
    public KeyStroke up = KeyStroke.getInstance( KeyEvent.VK_UP );
    public KeyStroke S = KeyStroke.getInstance( 'S' );
    public KeyStroke slash = KeyStroke.getInstance( '/' );
    public KeyStroke page_down = KeyStroke.getInstance( SWT.PAGE_DOWN );

}
