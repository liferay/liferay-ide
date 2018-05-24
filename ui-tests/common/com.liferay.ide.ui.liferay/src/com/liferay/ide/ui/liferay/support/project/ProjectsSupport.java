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

package com.liferay.ide.ui.liferay.support.project;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.junit.rules.ExternalResource;

/**
 * @author Terry Jia
 * @author Ying Xu
 */
public class ProjectsSupport extends ExternalResource {

	public ProjectsSupport(SWTWorkbenchBot bot) {
		_timestamps = new String[_total];
	}

	public void before() {
		for (int i = 0; i < _timestamps.length; i++) {
			_timestamps[i] = String.valueOf(System.currentTimeMillis()) + i;

			Long timestamp = Long.parseLong(_timestamps[i].substring(6));

			_timestamps[i] = String.valueOf(timestamp);
		}
	}

	public String getName(int index) {
		return "test" + _timestamps[index];
	}

	private String[] _timestamps;
	private int _total = 20;

}