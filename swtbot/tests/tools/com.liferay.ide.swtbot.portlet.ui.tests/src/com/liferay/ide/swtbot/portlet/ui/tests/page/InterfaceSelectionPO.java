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

package com.liferay.ide.swtbot.portlet.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.swtbot.ui.tests.page.SelectionDialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TablePO;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Ashley Yuan
 */
public class InterfaceSelectionPO extends SelectionDialogPO implements LiferayPortletWizard
{

    private TextPO _itemToOpenText;
    private TablePO _matchItemsTable;

    public InterfaceSelectionPO( SWTBot bot, String title, int labelIndex )
    {
        super( bot, title, labelIndex );

        _itemToOpenText = new TextPO( bot );
        _matchItemsTable = new TablePO( bot, 0 );
    }
    
    public InterfaceSelectionPO( SWTBot bot, String title )
    {
        this( bot, title, INDEX_DEFAULT_START );
    }

    public void clickMatchItem( int itemRow )
    {
        _matchItemsTable.click( itemRow );
    }

    public void setItemToOpen( String itemToOpen )
    {
        _itemToOpenText.setTextWithoutLabel( itemToOpen );
    }

}
