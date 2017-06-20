/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.swtbot.project.upgrade.ui.tests;

import org.junit.Test;

import com.liferay.ide.swtbot.ui.tests.SWTBotBase;
import com.liferay.ide.swtbot.ui.tests.eclipse.page.CodeUpgradeViewPO;

/**
 * @author Terry Jia
 */
public class CodeUpgradeToolTests extends SWTBotBase implements CodeUpgradeTool
{

    @Test
    public void testGear()
    {
        CodeUpgradeViewPO codeUpgrade = eclipse.showCodeUpgradeView();

        codeUpgrade.getGear().clickGear( 0 );
        keyPress.pressShortcut( ctrl, M );

        // the number of gears starts from 0, that means, the Import Page should be 1 or not 2
        codeUpgrade.getGear().clickGear( 1 );

        sleep( 1000 );
        codeUpgrade.getGear().clickGear( 0 );
        sleep( 1000 );
        codeUpgrade.showAllPagesWithConfirm();
        codeUpgrade.getGear().clickGear( 0 );
        codeUpgrade.getGear().clickGear( 1 );
        codeUpgrade.getGear().clickGear( 2 );
        codeUpgrade.getGear().clickGear( 3 );
        codeUpgrade.getGear().clickGear( 4 );
        codeUpgrade.getGear().clickGear( 5 );
        codeUpgrade.getGear().clickGear( 6 );
        codeUpgrade.getGear().clickGear( 7 );
        codeUpgrade.getGear().clickGear( 8 );
        codeUpgrade.getGear().clickGear( 9 );
        codeUpgrade.restartWithConfirm();
        codeUpgrade.getGear().clickGear( 0 );
        codeUpgrade.getGear().clickGear( 1 );
    }

}
