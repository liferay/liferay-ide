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

package com.liferay.ide.swtbot.ui.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Terry Jia
 * @author Ashley Yuan
 */
public class SelectionDialog extends Dialog
{

    Tree selcetFiles;

    public SelectionDialog( SWTBot bot )
    {
        super( bot );
    }

    public SelectionDialog( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_START );
    }

    public SelectionDialog( SWTBot bot, String title, int index )
    {
        super( bot, title );

        selcetFiles = new Tree( bot );
    }

    public String getDialogLabel()
    {
        return getDialogLabel( index );
    }

    public String getDialogLabel( int labelIndex )
    {
        return bot.label( labelIndex ).getText();
    }

    public Tree getSelcetFileTree()
    {
        return selcetFiles;
    }

}
