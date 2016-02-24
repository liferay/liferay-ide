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

import com.liferay.ide.ui.tests.UIBase;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public abstract class AbstractSelectionPageObject<T extends SWTBot> extends DialogPageObject<T> implements UIBase
{

    protected static int labelIndex = 0;
    TreePageObject<SWTBot> selcetFileTree;

    public AbstractSelectionPageObject( T bot, String title )
    {
        this( bot, title, BUTTON_CANCEL, BUTTON_OK, labelIndex );
    }

    public AbstractSelectionPageObject( T bot, String title, int labelIndex )
    {
        this( bot, title, BUTTON_CANCEL, BUTTON_OK, labelIndex );
    }

    public AbstractSelectionPageObject(
        T bot, String title, String cancelButtonText, String confirmButtonText, int labelIndex )
    {
        super( bot, title, cancelButtonText, confirmButtonText );

        selcetFileTree = new TreePageObject<SWTBot>( bot );
    }

    public String getDialogLabel()
    {
        return getDialogLabel( labelIndex );
    }

    public String getDialogLabel( int labelIndex )
    {
        return bot.label( labelIndex ).getText();
    }

    public TreePageObject<SWTBot> getSelcetFileTree()
    {
        return selcetFileTree;
    }

}
