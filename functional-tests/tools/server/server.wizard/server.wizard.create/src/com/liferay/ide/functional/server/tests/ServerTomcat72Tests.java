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

package com.liferay.ide.functional.server.tests;

import com.liferay.ide.functional.liferay.support.server.PureTomcat72Support;
import com.liferay.ide.functional.liferay.support.server.ServerSupport;
import com.liferay.ide.functional.server.wizard.base.ServerTomcat7xBase;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Lily Li
 */
public class ServerTomcat72Tests extends ServerTomcat7xBase {

	@Override
	public ServerSupport getTestServer() {
		if ((testServer == null) || !(testServer instanceof PureTomcat72Support)) {
			testServer = new PureTomcat72Support(bot);
		}

		return testServer;
	}

	@Test
	public void testDefaultServerName() {
		super.testDefaultServerName();
	}

	@Rule
	public ServerSupport tomcat = getTestServer();

}