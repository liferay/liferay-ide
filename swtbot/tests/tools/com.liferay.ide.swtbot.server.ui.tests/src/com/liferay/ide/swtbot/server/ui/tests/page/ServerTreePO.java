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

package com.liferay.ide.swtbot.server.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import com.liferay.ide.swtbot.server.ui.tests.ServerAction;
import com.liferay.ide.swtbot.ui.tests.page.ConfirmPO;
import com.liferay.ide.swtbot.ui.tests.page.ToolbarButtonWithTooltipPO;
import com.liferay.ide.swtbot.ui.tests.page.TreeItemPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Li Lu
 * @author Ying Xu
 */
public class ServerTreePO extends TreeItemPO implements ServerAction
{

    private AddAndRemoveProjectPO _addAndRemovePage;
    private String _serverName;
    private ToolbarButtonWithTooltipPO _start;
    private ToolbarButtonWithTooltipPO _stop;
    private ToolbarButtonWithTooltipPO _debug;

    int serverTreeIndex = 1;

    public ServerTreePO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public ServerTreePO( SWTBot bot, String serverName )
    {
        super( bot );

        _serverName = serverName;
        _addAndRemovePage = new AddAndRemoveProjectPO( bot );
        _start = new ToolbarButtonWithTooltipPO( bot, SERVER_START_BUTTON );
        _stop = new ToolbarButtonWithTooltipPO( bot, SERVER_STOP_BUTTON );
        _debug = new ToolbarButtonWithTooltipPO( bot, SERVER_DEBUG_BUTTON );
    }

    public void addALL()
    {
        doAction( MENU_ADD_AND_REMOVE );
        _addAndRemovePage.addAll();

        _addAndRemovePage.confirm();
    }

    public boolean checkConsoleHasMessage( String expectedMessage, int timeout )
    {
        long timeoutExpiredMs = System.currentTimeMillis() + timeout;

        while( true )
        {
            sleep();

            String content = bot.styledText().getText();

            if( content.contains( expectedMessage ) || content.matches( expectedMessage ) )
            {
                return true;
            }

            if( System.currentTimeMillis() >= timeoutExpiredMs )
            {
                return false;
            }
        }
    }

    public void debugServer()
    {
        doAction( MENU_DEBUG );
    }

    public void deleteServer()
    {
        doAction( BUTTON_DELETE );

        ConfirmPO deleteDialog = new ConfirmPO( bot, TITLE_DELETE_SERVER_WIZARD, BUTTON_OK );

        deleteDialog.confirm();
    }

    public void deployProject( String... projectItemNames )
    {
        doAction( MENU_ADD_AND_REMOVE );
        _addAndRemovePage.add( projectItemNames );
        _addAndRemovePage.confirm();
    }

    @Override
    protected SWTBotTreeItem getWidget()
    {
        TreePO tree = new TreePO( bot, serverTreeIndex );

        String[] servers = tree.getAllItems();

        for( String server : servers )
        {
            if( server.contains( _serverName ) || server.equals( _serverName ) )
            {
                index = serverTreeIndex;
                _nodeText = new String[] { server };

                return super.getWidget();
            }
        }

        return null;
    }

    public void removeALL()
    {
        doAction( MENU_ADD_AND_REMOVE );
        _addAndRemovePage.removeAll();
        _addAndRemovePage.confirm();
    }

    public void removeProject( String... projectItemNames )
    {
        doAction( MENU_ADD_AND_REMOVE );
        _addAndRemovePage.remove( projectItemNames );
        _addAndRemovePage.confirm();
    }

    public void startServer()
    {
        doAction( MENU_START );
    }

    public void stopServer()
    {
        doAction( MENU_STOP );
        checkConsoleHasMessage( MESAGE_SERVER_STOP, 10000 );
    }

    public ToolbarButtonWithTooltipPO getStart()
    {
        return _start;
    }

    public ToolbarButtonWithTooltipPO getStop()
    {
        return _stop;
    }

    public ToolbarButtonWithTooltipPO getDebug()
    {
        return _debug;
    }

}
