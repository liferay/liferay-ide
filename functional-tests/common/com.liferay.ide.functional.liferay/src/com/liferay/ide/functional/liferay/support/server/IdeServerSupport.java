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

package com.liferay.ide.functional.liferay.support.server;

import com.liferay.ide.functional.liferay.support.SupportBase;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class IdeServerSupport extends SupportBase {

	public IdeServerSupport(SWTWorkbenchBot bot, ServerSupport server) {
		super(bot);

		this.server = server;
	}

	@Override
	public void after() {
		dialogAction.deleteRuntimFromPreferences(0);

		super.after();
	}

	protected ServerSupport server;

}