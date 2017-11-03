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

package com.liferay.ide.project.ui.upgrade.animated;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class PageActionEvent {

	public PageAction getAction() {
		return _action;
	}

	public int getTargetPageIndex() {
		return _targetPageIndex;
	}

	public void setAction(PageAction action) {
		_action = action;
	}

	public void setTargetPageIndex(int targetPageIndex) {
		_targetPageIndex = targetPageIndex;
	}

	private PageAction _action;
	private int _targetPageIndex;

}