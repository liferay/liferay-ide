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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;

/**
 * @author Li Lu
 */
public class Table extends AbstractWidget
{

    public Table( final SWTWorkbenchBot bot )
    {
        super( bot );
    }

    public Table( final SWTWorkbenchBot bot, final String label )
    {
        super( bot, label );
    }

    public Table( final SWTWorkbenchBot bot, final int index )
    {
        super( bot, index );
    }

    protected SWTBotTable getWidget()
    {
        return isLabelNull() ? bot.table( index ) : bot.tableWithLabel( label, 0 );
    }

    public void click( final int row, final int column )
    {
        getWidget().click( row, column );
    }

    public void click( final String itemText )
    {
        getWidget().getTableItem( itemText ).click();
    }

    public void doubleClick( final int row, final int column )
    {
        getWidget().doubleClick( row, column );
    }

    public void click( final int row )
    {
        getWidget().getTableItem( row ).click();
    }

    public boolean containsItem( final String item )
    {
        return getWidget().containsItem( item );
    }

    public void setText( final int index, final String text )
    {
        bot.text( index ).setText( text );
    }

}
