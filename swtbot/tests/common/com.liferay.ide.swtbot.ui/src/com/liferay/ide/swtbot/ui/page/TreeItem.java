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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Li Lu
 */
public class TreeItem extends AbstractWidget
{

    protected String[] nodeText;
    protected Tree tree;

    public TreeItem( final SWTWorkbenchBot bot )
    {
        super( bot );
    }

    public TreeItem( final SWTWorkbenchBot bot, final Tree tree, final String... nodeText )
    {
        super( bot );

        this.tree = tree;
        this.nodeText = nodeText;
    }

    public void collapse()
    {
        getWidget().collapse();
    }

    public void doAction( final String... actions )
    {
        SWTBotMenu goalMenu = getWidget().contextMenu( actions[0] ).click();

        for( int i = 1; i < actions.length; i++ )
        {
            goalMenu = goalMenu.menu( actions[i] ).click();
        }
    }

    public void doubleClick()
    {
        getWidget().doubleClick();
    }

    public void doubleClick( String node )
    {
        getWidget().getNode( node ).doubleClick();
    }

    public void expand()
    {
        getWidget().expand();
    }

    public void expandAll( final SWTBotTreeItem... node )
    {
        SWTBotTreeItem treeItem = getWidget().expand();

        SWTBotTreeItem[] subNodes = treeItem.getItems();

        if( subNodes != null )
        {
            for( SWTBotTreeItem subNode : subNodes )
            {
                if( subNode.getText().contains( "JRE" ) )
                    continue;

                treeItem = subNode.expand();

                expandAll( subNode );
            }
        }
    }

    public void expandNode( String... nodes )
    {
        getWidget().expandNode( nodes );
    }

    public String[] getAllItems()
    {
        expand();

        SWTBotTreeItem[] items = getWidget().getItems();
        String subNodes[] = new String[items.length];

        for( int i = 0; i < items.length; i++ )
        {
            subNodes[i] = items[i].getText();
        }

        return subNodes;
    }

    public TreeItem getTreeItem( String... items )
    {
        String[] fullNodeText = new String[nodeText.length + items.length];

        System.arraycopy( nodeText, 0, fullNodeText, 0, nodeText.length );
        System.arraycopy( items, 0, fullNodeText, nodeText.length, items.length );

        return new TreeItem( bot, tree, fullNodeText );
    }

    @Override
    protected SWTBotTreeItem getWidget()
    {
        SWTBotTreeItem treeItem = null;

        if( nodeText != null )
        {
            treeItem = tree.getWidget().getTreeItem( nodeText[0] );
        }

        for( int i = 1; i < nodeText.length; i++ )
        {
            treeItem.expand();

            treeItem = treeItem.getNode( nodeText[i] );
        }

        return treeItem;
    }

    public boolean isEnabled()
    {
        return getWidget().isEnabled();
    }

    public boolean isExpanded()
    {
        return getWidget().isExpanded();
    }

    public boolean isSelected()
    {
        return getWidget().isSelected();
    }

    public boolean isVisible()
    {
        return getWidget().isVisible();
    }

    public void select()
    {
        getWidget().select();
    }

    public void selectMulty( String... items )
    {
        getWidget().select( items );
    }

    public void selectTreeItem( String... items )
    {
        SWTBotTreeItem treeItem = getWidget();

        for( int i = 0; i < nodeText.length; i++ )
        {
            treeItem.expand();

            treeItem = treeItem.getNode( nodeText[i] );
        }

        treeItem.select();
    }

}
