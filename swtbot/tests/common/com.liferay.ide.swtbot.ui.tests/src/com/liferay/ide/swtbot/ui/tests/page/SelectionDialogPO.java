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

import com.liferay.ide.swtbot.ui.tests.UIBase;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class SelectionDialogPO extends DialogPO implements UIBase
{

    protected static int _index = 0;
    TreePO _selcetFile;

    public SelectionDialogPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_START );
    }

    public SelectionDialogPO( SWTBot bot, String title, int index )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK );

        _index = index;
        _selcetFile = new TreePO( bot );
    }

    public String getDialogLabel()
    {
        return getDialogLabel( _index );
    }

    public String getDialogLabel( int labelIndex )
    {
        return bot.label( labelIndex ).getText();
    }

    public TreePO getSelcetFileTree()
    {
        return _selcetFile;
    }

}
