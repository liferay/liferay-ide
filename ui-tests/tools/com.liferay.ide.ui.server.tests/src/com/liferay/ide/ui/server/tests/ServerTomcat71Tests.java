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

package com.liferay.ide.ui.server.tests;

import com.liferay.ide.ui.liferay.support.server.PureTomcat71Support;
import com.liferay.ide.ui.liferay.support.server.ServerSupport;
import com.liferay.ide.ui.server.tests.base.ServerTomcat7xBase;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Terry Jia
 * @author Vicky Wang
 * @author Ashley Yuan
 * @author Ying Xu
 */
public class ServerTomcat71Tests extends ServerTomcat7xBase {

	@Test
	public void addLiferay7RuntimeFromPreferences() {
		super.addLiferay7RuntimeFromPreferences();
	}

	@Test
	public void addLiferay7ServerFromMenu() {
		super.addLiferay7ServerFromMenu();
	}

	@Override
	public ServerSupport getTestServer() {
		if ((testServer == null) || !(testServer instanceof PureTomcat71Support)) {
			testServer = new PureTomcat71Support(bot);
		}

		return testServer;
	}

	@Test
	public void serverEditorCustomLaunchSettingsChange() {
		super.serverEditorCustomLaunchSettingsChange();
	}

	@Test
	public void serverEditorCustomLaunchSettingsChangeAndStart() {
		super.serverEditorCustomLaunchSettingsChangeAndStart();
	}

	@Ignore("To wait for IDE-3343")
	@Test
	public void serverEditorPortsChange() {
		super.serverEditorPortsChange();
	}

	@Ignore("To wait for IDE-3343")
	@Test
	public void serverEditorPortsChangeAndStart() {
		super.serverEditorPortsChangeAndStart();
	}

	@Test
	public void testLiferay7ServerDebug() {
		super.testLiferay7ServerDebug();
	}

	@Rule
	public ServerSupport tomcat = getTestServer();

}