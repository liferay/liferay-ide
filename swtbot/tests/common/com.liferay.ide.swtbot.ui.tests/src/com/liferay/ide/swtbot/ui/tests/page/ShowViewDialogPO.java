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

import com.liferay.ide.swtbot.ui.tests.UIBase;

/**
 * @author Terry Jia
 */
public class ShowViewDialogPO extends DialogPO implements UIBase
{

    private TextPO _searchText;

    public ShowViewDialogPO( SWTBot bot )
    {
        super( bot, LABEL_SHOW_VIEW, BUTTON_CANCEL, BUTTON_OK );

        _searchText = new TextPO( bot );
    }

    public void setSearchText( String text )
    {
        _searchText.setText( text );
    }

}