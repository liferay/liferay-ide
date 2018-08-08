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

package com.liferay.ide.ui.preference.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;
import com.liferay.ide.ui.swtbot.page.Table;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Joye Luo
 */
public class AvailableSoftwareSitesTests extends SwtbotBase {

	@Test
	public void checkLiferayIdeSite() {
		dialogAction.openPreferencesDialog();

		dialogAction.preferences.openAvailableSoftwareSites();

		Table availableSites = dialogAction.availableSoftwareSites.getSites();

		Assert.assertTrue(availableSites.containsItem(LIFERAY_IDE_STABLE_RELEASES));
	}

}