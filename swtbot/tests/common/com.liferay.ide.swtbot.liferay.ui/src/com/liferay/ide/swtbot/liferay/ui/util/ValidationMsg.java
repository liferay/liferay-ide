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

package com.liferay.ide.swtbot.liferay.ui.util;

/**
 * @author Terry Jia
 */
public class ValidationMsg {

	public String getExpect() {
		return _expect;
	}

	public String getInput() {
		return _input;
	}

	public void setExpect(String expect) {
		_expect = expect;
	}

	public void setInput(String input) {
		_input = input;
	}

	private String _expect;
	private String _input;

}