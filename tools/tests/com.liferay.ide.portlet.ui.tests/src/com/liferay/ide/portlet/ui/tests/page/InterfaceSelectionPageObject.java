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

package com.liferay.ide.portlet.ui.tests.page;

import com.liferay.ide.portlet.ui.tests.LiferayPortletWizard;
import com.liferay.ide.ui.tests.swtbot.page.AbstractSelectionPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TablePageObject;
import com.liferay.ide.ui.tests.swtbot.page.TextPageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Ashley Yuan
 */
public class InterfaceSelectionPageObject extends AbstractSelectionPageObject<SWTBot> implements LiferayPortletWizard
{

    TextPageObject<SWTBot> itemToOpenText;
    TablePageObject<SWTBot> matchItemsTable;

    public InterfaceSelectionPageObject( SWTBot bot, String title, int labelIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_OK, labelIndex );

        itemToOpenText = new TextPageObject<SWTBot>( bot );
        matchItemsTable = new TablePageObject<SWTBot>( bot, 0 );
    }

    public void clickMatchItem( int itemRow )
    {
        matchItemsTable.click( itemRow );
    }

    public void setItemToOpen( String itemToOpen )
    {
        this.itemToOpenText.setTextWithoutLabel( itemToOpen );
    }

}
