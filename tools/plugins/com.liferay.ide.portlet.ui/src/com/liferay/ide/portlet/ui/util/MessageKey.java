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

package com.liferay.ide.portlet.ui.util;

import org.eclipse.core.resources.IFile;

/**
 * @author Gregory Amerson
 */
public class MessageKey {

	public MessageKey(IFile file, String key, int offset, int length, String value) {
		this.file = file;
		this.key = key;
		this.offset = offset;
		this.length = length;
		this.value = value;
	}

	public IFile file;
	public String key;
	public int length;
	public int offset;
	public String value;

}