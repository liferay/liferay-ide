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

package com.liferay.ide.ui.tests.swtbot.page;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;

/**
 * @author Ashley Yuan
 */
public class LabelPageObject<T extends SWTBot> extends AbstractWidgetPageObject<SWTBot>
{

    public LabelPageObject( SWTBot bot, String label )
    {
        super( bot, label );
    }

    @Override
    protected AbstractSWTBot<?> getWidget()
    {
        return bot.label( label );
    }

    public boolean isVisible( String label )
    {
        long oldTimeOut = SWTBotPreferences.TIMEOUT;

        SWTBotPreferences.TIMEOUT = 1000;

        try
        {
            return getWidget().isVisible();
        }
        catch( Exception e )
        {
            if( e.getMessage().contains( label ) )
            {
                return false;
            }
            else
            {
                throw e;
            }
        }
        finally
        {
            SWTBotPreferences.TIMEOUT = oldTimeOut;
        }

    }

}
