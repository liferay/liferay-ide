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

package com.liferay.ide.ui.code.upgrade.tests;

import com.liferay.ide.ui.liferay.SwtbotBase;

import org.junit.Test;

/**
 * @author Terry Jia
 */
public class CodeUpgradeToolTests extends SwtbotBase {

	@Test
	public void testGear() {
		viewAction.showCodeUpgradeView();

		viewAction.switchGear(0);

		// the number of gears starts from 0, that means, the Import Page should be 1 or not 2

		viewAction.switchGear(1);

		viewAction.switchGear(0);

		viewAction.showAllPages();

		dialogAction.confirm(YES);

		viewAction.switchGear(0);

		viewAction.switchGear(1);

		viewAction.switchGear(2);

		viewAction.switchGear(3);

		viewAction.switchGear(4);

		viewAction.switchGear(5);

		viewAction.switchGear(6);

		viewAction.switchGear(7);

		viewAction.switchGear(8);

		viewAction.switchGear(9);

		viewAction.restartUpgrade();

		dialogAction.confirm(YES);

		viewAction.switchGear(0);

		viewAction.switchGear(1);
	}

}