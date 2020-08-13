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

package com.liferay.ide.upgrade.problems.core;

/**
 * @author Gregory Amerson
 */
public class AutoFileMigratorException extends Exception {

	public AutoFileMigratorException(String message) {
		super(message);
	}

	public AutoFileMigratorException(String message, Exception exception) {
		super(message, exception);
	}

	private static final long serialVersionUID = 3776203562079756845L;

}