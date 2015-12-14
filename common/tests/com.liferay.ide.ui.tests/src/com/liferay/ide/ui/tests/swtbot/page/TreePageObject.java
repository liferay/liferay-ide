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
import org.eclipse.swtbot.swt.finder.utils.TableCollection;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Li Lu
 */
public class TreePageObject<T extends SWTBot> extends AbstractWidgetPageObject<SWTBot>
{

    protected TableCollection selection;

    public TreePageObject( SWTBot bot )
    {
        super( bot );
    }

    public String[] getAllItems()
    {
        SWTBotTreeItem[] items = getTree().getAllItems();

        String subNodes[] = new String[items.length];

        for( int i = 0; i < items.length; i++ )
        {
            subNodes[i] = items[i].getText();

        }

        return subNodes;
    }

    public SWTBotTreeItem getItem( String nodeText )
    {
        return getTree().getTreeItem( nodeText );
    }

    public String[] getSelection()
    {
        selection = getTree().selection();

        String[] elements = new String[selection.rowCount()];

        for( int i = 0; i < selection.rowCount(); i++ )
        {
            elements[i] = selection.get( i, 0 );
        }

        return elements;
    }

    protected SWTBotTree getTree()
    {
        return (SWTBotTree) getWidget();
    }

    protected AbstractSWTBot<?> getWidget()
    {
        return bot.tree();
    }

    public void select( final String... items )
    {
        getTree().select( items );
    }

    public void unSelect()
    {
        getTree().unselect();
    }
}
