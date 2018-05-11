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

package com.liferay.ide.installer.tests.model;

import com.liferay.ide.installer.tests.util.Constants;

/**
 * @author Terry Jia
 */
public abstract class Installer implements Constants {

	public Installer(String type) {
		_type = type;
	}

	public boolean isWindow() {
		return _type.equals(WINDOWS);
	}

	public boolean isLinux() {
		return _type.equals(LINUX_X64);
	}

	public boolean isMacos() {
		return _type.equals(OSX);
	}

	private String _type;
	public abstract String command();

}
