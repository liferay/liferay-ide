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

package com.liferay.ide.gradle.ui;

import java.util.stream.Stream;

import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;

/**
 * @author Charles Wu
 */
public class RemoveOldWatchConsoleListener implements IConsoleListener {

	public RemoveOldWatchConsoleListener(IConsoleManager consoleManager) {
		_consoleManager = consoleManager;
	}

	@Override
	public void consolesAdded(IConsole[] addConsoles) {
		IConsole[] allConsoles = _consoleManager.getConsoles();

		IConsole[] consoles = Stream.of(
			allConsoles
		).filter(
			console -> {
				//filter out the out of date consoles
				String consoleName = console.getName();

				//only one watch task will be launched in the same time

				return !consoleName.equals(addConsoles[0].getName());
			}
		).filter(
			console -> {
				Class<? extends IConsole> clazz = console.getClass();

				if ("GradleConsole".equals(clazz.getSimpleName())) {
					String consoleName = console.getName();

					if (consoleName.contains("watch")) {
						return true;
					}
				}

				return false;
			}
		).toArray(
			IConsole[]::new
		);

		_consoleManager.removeConsoles(consoles);
	}

	@Override
	public void consolesRemoved(IConsole[] consoles) {
	}

	private final IConsoleManager _consoleManager;

}