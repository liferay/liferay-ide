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

package com.liferay.ide.swtbot.ui.tests.eclipse.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.page.CanvasPO;

/**
 * @author Terry Jia
 */
public class GearPO extends CanvasPO
{

    int x = 52;
    int y = 52;
    int step = 64;

    public GearPO( SWTBot bot, int index )
    {
        super( bot, index );
    }

    public void clickGear( int i )
    {
        click( x + step * i, y );
        sleep();
    }

}
