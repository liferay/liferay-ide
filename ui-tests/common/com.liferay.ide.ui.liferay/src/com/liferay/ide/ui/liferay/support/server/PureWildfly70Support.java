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

package com.liferay.ide.ui.liferay.support.server;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * @author Rui Wang
 */
public class PureWildfly70Support extends ServerSupport {

	public static boolean isNot(ServerSupport server) {
		if ((server == null) || !(server instanceof PureWildfly70Support)) {
			return true;
		}

		return false;
	}

	public PureWildfly70Support(SWTWorkbenchBot bot) {
		super(bot, "wildfly", "7.0-ga7");
	}

}