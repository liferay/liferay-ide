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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Li Lu
 */
public class TreeItemPO extends AbstractWidgetPO
{

    protected String[] _nodeText;
    protected TreePO _tree;

    public TreeItemPO( SWTBot bot )
    {
        super( bot );
    }

    public TreeItemPO( SWTBot bot, TreePO tree, String... nodeText )
    {
        super( bot );

        _tree = tree;
        _nodeText = nodeText;
    }

    public void collapse()
    {
        getWidget().collapse();
    }

    public void doAction( String... action )
    {
        SWTBotMenu goalMenu = getWidget().contextMenu( action[0] ).click();

        for( int i = 1; i < action.length; i++ )
        {
            goalMenu = goalMenu.menu( action[i] ).click();
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
        sleep();
        getWidget().expand();
    }

    public void expandAll( SWTBotTreeItem... node )
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

    public TreeItemPO getTreeItem( String... items )
    {
        String[] fullNodeText = new String[_nodeText.length + items.length];

        System.arraycopy( _nodeText, 0, fullNodeText, 0, _nodeText.length );
        System.arraycopy( items, 0, fullNodeText, _nodeText.length, items.length );

        return new TreeItemPO( bot, _tree, fullNodeText );
    }

    @Override
    protected SWTBotTreeItem getWidget()
    {
        SWTBotTreeItem treeItem = null;

        if( _nodeText != null )
        {
            treeItem = _tree.getWidget().getTreeItem( _nodeText[0] );
        }

        for( int i = 1; i < _nodeText.length; i++ )
        {
            treeItem.expand();

            treeItem = treeItem.getNode( _nodeText[i] );
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

        for( int i = 0; i < _nodeText.length; i++ )
        {
            treeItem.expand();

            treeItem = treeItem.getNode( _nodeText[i] );
        }

        treeItem.select();

    }
}
