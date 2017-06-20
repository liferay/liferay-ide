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

package com.liferay.ide.swtbot.ui.tests.page;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCTabItem;

/**
 * @author Ying Xu
 */
public class CTabItemPO extends AbstractWidgetPO
{

    public CTabItemPO( SWTBot bot, String lable )
    {
        super( bot, lable );

    }

    public void click()
    {
        Display.getDefault().syncExec( new Runnable()
        {

            public void run()
            {
                try
                {
                    getWidget().activate();
                }
                catch( Exception ex )
                {
                    ex.printStackTrace();
                }
            }
        } );

    }

    @Override
    protected SWTBotCTabItem getWidget()
    {
        return bot.cTabItem( label );
    }

}
