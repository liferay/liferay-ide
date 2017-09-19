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

package com.liferay.ide.swtbot.liferay.ui;

import com.liferay.ide.swtbot.ui.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Terry Jia
 */
public class Actions implements UI {

	public static List<String> getDelete() {
		List<String> actions = new ArrayList<>();

		actions.add(DELETE);

		return actions;
	}

	public static List<String> getLiferayInitializeServerBundle() {
		List<String> actions = new ArrayList<>();

		actions.add(LIFERAY);
		actions.add(INITIALIZE_SERVER_BUNDLE);

		return actions;
	}

}