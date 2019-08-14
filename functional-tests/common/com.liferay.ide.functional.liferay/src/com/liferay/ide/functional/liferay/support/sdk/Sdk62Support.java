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

package com.liferay.ide.functional.liferay.support.sdk;

import com.liferay.ide.functional.liferay.support.server.ServerSupport;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Terry Jia
 */
public class Sdk62Support extends SdkSupport {

	public Sdk62Support(SWTWorkbenchBot bot, ServerSupport server) {
		super(bot, "6.2-ce-ga6", server);
	}

	@Override
	public void after() {
		if (!envAction.internal()) {
			return;
		}

		super.after();
	}

	@Override
	public void before() {
		if (!envAction.internal()) {
			return;
		}

		super.before();
	}

}