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

package com.liferay.ide.project.ui.migration;

import com.liferay.blade.api.Problem;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/**
 * @author Lovett li
 */
public class MigratorComparator extends ViewerComparator {

	public MigratorComparator() {
		_columnIndex = 1;
		_direction = _DESCEDING;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		final Problem t1 = (Problem)e1;
		final Problem t2 = (Problem)e2;
		int flag = 0;

		switch (_columnIndex) {
			case 0:
				flag = (t1.getStatus() == Problem.STATUS_RESOLVED) ? 1 : -1;

				break;

			case 1:
				flag = (t1.getLineNumber() >= t2.getLineNumber()) ? 1 : -1;

				break;

			case 2:
				String title1 = t1.getTitle();

				if (title1.compareTo(t2.getTitle()) != 0) {
					flag = (title1.compareTo(t2.getTitle()) > 0) ? 1 : -1;
				}

				break;

			default:
				flag = 0;
		}

		if (_direction != _DESCEDING) {
			flag = -flag;
		}

		return flag;
	}

	public int getDirection() {
		if (_direction == 1) {
			return SWT.DOWN;
		}

		return SWT.UP;
	}

	public void setColumn(int column) {
		if (column == _columnIndex) {
			_direction = 1 - _direction;
		}
		else {
			_columnIndex = column;
			_direction = _DESCEDING;
		}
	}

	private static final int _DESCEDING = 1;

	private int _columnIndex;
	private int _direction = _DESCEDING;

}