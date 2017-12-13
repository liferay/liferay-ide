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

		viewAction.codeUpgrade.switchGear(0);

		// the number of gears starts from 0, that means, the Import Page should be 1 or not 2

		viewAction.codeUpgrade.switchGear(1);

		viewAction.codeUpgrade.switchGear(0);

		viewAction.codeUpgrade.showAllPages();

		dialogAction.confirm(YES);

		viewAction.codeUpgrade.switchGear(0);

		viewAction.codeUpgrade.switchGear(1);

		viewAction.codeUpgrade.switchGear(2);

		viewAction.codeUpgrade.switchGear(3);

		viewAction.codeUpgrade.switchGear(4);

		viewAction.codeUpgrade.switchGear(5);

		viewAction.codeUpgrade.switchGear(6);

		viewAction.codeUpgrade.switchGear(7);

		viewAction.codeUpgrade.switchGear(8);

		viewAction.codeUpgrade.switchGear(9);

		viewAction.codeUpgrade.restartUpgrade();

		dialogAction.confirm(YES);

		viewAction.codeUpgrade.switchGear(0);

		viewAction.codeUpgrade.switchGear(1);
	}

}