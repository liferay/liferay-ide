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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;

/**
 * @author Li Lu
 */
public class TablePO extends AbstractWidgetPO
{

    public TablePO( SWTBot bot )
    {
        super( bot );
    }

    public TablePO( SWTBot bot, String label )
    {
        super( bot, label );
    }

    public TablePO( SWTBot bot, int index )
    {
        super( bot, index );
    }

    protected SWTBotTable getWidget()
    {
        if( label != null )
        {
            return bot.tableWithLabel( label, 0 );
        }

        return bot.table( index );
    }

    public void click( int row, int column )
    {
        getWidget().click( row, column );
    }

    public void click( String itemText )
    {
        getWidget().getTableItem( itemText ).click();
    }

    public void doubleClick( int row, int column )
    {
        getWidget().doubleClick( row, column );
    }

    public void click( int row )
    {
        getWidget().getTableItem( row ).click();
    }

    public boolean containsItem( String item )
    {
        return getWidget().containsItem( item );
    }

    public void setText( int index, String text )
    {
        bot.text( index ).setText( text );
    }

}
