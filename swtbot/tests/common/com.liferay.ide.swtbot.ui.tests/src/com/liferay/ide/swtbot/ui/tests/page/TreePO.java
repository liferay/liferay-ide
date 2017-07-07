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
import org.eclipse.swtbot.swt.finder.utils.TableCollection;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Li Lu
 * @author Ashley Yuan
 */
public class TreePO extends AbstractWidgetPO
{

    protected int index = -1;

    protected TableCollection selection;

    public TreePO( SWTBot bot )
    {
        super( bot );
    }

    public TreePO( SWTBot bot, int index )
    {
        this( bot );
        this.index = index;
    }

    public boolean hasItems()
    {
        return getWidget().hasItems();
    }

    public boolean hasTreeItem( String... items )
    {
        try
        {
            SWTBotTreeItem treeItem = getWidget().getTreeItem( items[0] );

            for( int i = 1; i < items.length; i++ )
            {

                treeItem.expand();
                treeItem = treeItem.getNode( items[i] ).expand();
            }
            return true;
        }
        catch( Exception e )
        {
            return false;
        }
    }

    public String[] getAllItems()
    {
        SWTBotTreeItem[] items = getWidget().getAllItems();

        String subNodes[] = new String[items.length];

        for( int i = 0; i < items.length; i++ )
        {
            subNodes[i] = items[i].getText();
        }

        return subNodes;
    }

    public String[] getSelection()
    {
        selection = getWidget().selection();

        String[] elements = new String[selection.rowCount()];

        for( int i = 0; i < selection.rowCount(); i++ )
        {
            elements[i] = selection.get( i, 0 );
        }

        return elements;
    }

    protected SWTBotTree getWidget()
    {
        if( index >= 0 )
        {
            return bot.tree( index );
        }
        else
        {
            return bot.tree();
        }
    }

    public void selectMulty( final String... items )
    {
        getWidget().select( items );
    }

    public void selectTreeItem( String... items )
    {
        SWTBotTreeItem treeItem = getWidget().getTreeItem( items[0] );

        for( int i = 1; i < items.length; i++ )
        {
            treeItem.expand();
            treeItem = treeItem.getNode( items[i] ).expand();
        }

        treeItem.select();
    }

    public void unselect()
    {
        getWidget().unselect();
    }

    public TreeItemPO getTreeItem( String nodeText )
    {
        return new TreeItemPO( bot, this, nodeText );
    }

    public TreeItemPO expandNode( String... nodes )
    {
        getWidget().expandNode( nodes );

        return new TreeItemPO( bot, this, nodes );
    }

}
