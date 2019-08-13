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

package com.liferay.ide.functional.liferay.action;

import com.liferay.ide.functional.liferay.UIAction;
import com.liferay.ide.functional.swtbot.Keys;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class KeyboardAction extends UIAction implements Keys {

	public static KeyboardAction getInstance(SWTWorkbenchBot bot) {
		if (_keyboardAction == null) {
			_keyboardAction = new KeyboardAction(bot);
		}

		return _keyboardAction;
	}

	public void pressKeyEnter() {
		keyPress.pressShortcut(KeyStroke.getInstance(SWT.CR));
	}

	public void pressKeysPreferencesDialogMac() {
		keyPress.pressShortcut(SWT.COMMAND, ',');
	}

	public void pressKeysUpdateMavenProjectDialog() {

		// the latest one has no meaning but it has to have something

		keyPress.pressShortcut(SWT.ALT, SWT.F5, 'a');
	}

	private KeyboardAction(SWTWorkbenchBot bot) {
		super(bot);
	}

	private static KeyboardAction _keyboardAction;

}