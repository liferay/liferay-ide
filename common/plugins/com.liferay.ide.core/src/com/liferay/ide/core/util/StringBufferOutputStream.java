/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.core.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Greg Amerson
 */
public class StringBufferOutputStream extends OutputStream {

	protected StringBuffer buffer = new StringBuffer();

	public StringBufferOutputStream() {
		super();
	}

	/*
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int write)
		throws IOException {
		buffer.append((char) write);
	}

	public String toString() {
		return buffer.toString();
	}

	public void clear() {
		buffer.delete(0, buffer.length());
	}

}
