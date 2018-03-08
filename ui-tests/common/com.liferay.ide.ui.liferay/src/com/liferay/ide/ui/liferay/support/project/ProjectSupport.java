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

import com.liferay.ide.ui.liferay.support.SupportBase;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 * @author Ying Xu
 */
public class ProjectSupport extends SupportBase {

	public ProjectSupport(SWTWorkbenchBot bot) {
		super(bot);
	}

	public ProjectSupport(SWTWorkbenchBot bot, String name) {
		super(bot);

		this.name = name;
	}

	public String getName() {
		return name + timestamp;
	}

	public String getName(String suffix) {
		return name + timestamp + suffix;
	}

	public String getStartedLabel() {
		return getName() + "  [Started, Synchronized] (" + getName() + ")";
	}

	public String getStartedLabel(String suffix) {
		return getName() + "  [Started, Synchronized] (" + getName(suffix) + ")";
	}

	protected String name = "test";

}