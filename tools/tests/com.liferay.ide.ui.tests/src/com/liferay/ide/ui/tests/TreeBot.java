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

package com.liferay.ide.ui.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Terry Jia
 * @auther Ashley Yuan
 * @author Ying Xu
 */
public class TreeBot extends Bot
{

    public TreeBot( SWTWorkbenchBot bot )
    {
        super( bot );
    }

    public void click( String node, String context )
    {
        bot.tree().getTreeItem( node ).contextMenu( context ).click();
        sleep();
    }

    public void click( String node, String context, String menuName )
    {
        bot.tree().getTreeItem( node ).contextMenu( context ).menu( menuName ).click();
        sleep();
    }

    public void doubleClick( String node, String... nodes )
    {
        expandNode( nodes ).getNode( node ).doubleClick();
        sleep();
    }

    public SWTBotTreeItem expandNode( String... nodes )
    {
        SWTBotTreeItem treeItem = bot.tree().expandNode( nodes );
        sleep();
        return treeItem;
    }

    public SWTBotTreeItem[] getItems()
    {
        return bot.tree().getAllItems();
    }

    public SWTBotTreeItem getNode( String node )
    {
        return bot.tree().getTreeItem( node );
    }

    public boolean hasItems()
    {
        SWTBotTree tree;

        try
        {
            tree = bot.tree();
        }
        catch( Exception e )
        {
            return false;
        }

        return tree.hasItems();
    }

    public void select( String node )
    {
        bot.tree().getTreeItem( node ).select();
    }

    public SWTBotTreeItem getTreeItem( String name )
    {
        return bot.tree().getTreeItem( name );
    }

    public SWTBotTreeItem expandNode( String nodeText )
    {
        return bot.tree().expandNode( nodeText );
    }

}
