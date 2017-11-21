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

package com.liferay.ide.kaleo.core.model.internal;

/**
 * @author Gregory Amerson
 */
public class Point {

	public Point() {
		this(-1, -1);
	}

	public Point(int x, int y) {
		_x = x;
		_y = y;
	}

	@Override
	public boolean equals(Object obj) {
		boolean xEqual = false;

		if (((Point)obj).getX() == _x) {
			xEqual = true;
		}

		boolean yEqual = false;

		if (((Point)obj).getY() == _y) {
			yEqual = true;
		}

		if (obj instanceof Point && xEqual && yEqual) {
			return true;
		}

		return false;
	}

	public int getX() {
		return _x;
	}

	public int getY() {
		return _y;
	}

	public void setX(int x) {
		_x = x;
	}

	public void setY(int y) {
		_y = y;
	}

	private int _x;
	private int _y;

}