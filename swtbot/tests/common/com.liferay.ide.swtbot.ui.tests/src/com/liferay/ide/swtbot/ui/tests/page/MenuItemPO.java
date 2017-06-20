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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

/**
 * @author Terry Jia
 */
public class MenuItemPO extends AbstractWidgetPO
{

    private ToolbarDropDownButtonPO _dropDownButton;

    public MenuItemPO( SWTBot bot, ToolbarDropDownButtonPO dropDownButton, String label )
    {
        super( bot, label );

        _dropDownButton = dropDownButton;
    }

    @Override
    protected SWTBotMenu getWidget()
    {
        return _dropDownButton.getWidget().menuItem( label );
    }

    public void click(){
        getWidget().click();
    }

}