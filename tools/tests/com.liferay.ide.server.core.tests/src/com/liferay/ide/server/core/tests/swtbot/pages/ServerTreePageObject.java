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

package com.liferay.ide.server.core.tests.swtbot.pages;

import com.liferay.ide.server.core.tests.swtbot.ServerAction;
import com.liferay.ide.ui.tests.swtbot.page.ConfirmPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TreeItemPageObject;
import com.liferay.ide.ui.tests.swtbot.page.TreePageObject;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * @author Li Lu
 */
public class ServerTreePageObject<T extends SWTBot> extends TreeItemPageObject<SWTBot> implements ServerAction
{

    AddAndRemoveProjectPageObject<SWTBot> addAndRemovePage;
    String serverName;

    int serverTreeIndex = 1;

    public ServerTreePageObject( SWTBot bot, String serverName )
    {
        super( bot );

        this.serverName = serverName;
        addAndRemovePage = new AddAndRemoveProjectPageObject<SWTBot>( bot );
    }

    public void addALL()
    {
        doAction( ADD_AND_REMOVE );
        addAndRemovePage.addAll();

        addAndRemovePage.confirm();
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
        doAction( DEBUG );
    }

    public void deleteServer()
    {
        doAction( BUTTON_DELETE );

        ConfirmPageObject<SWTBot> deleteDialog =
            new ConfirmPageObject<SWTBot>( bot, DELETE_SERVER_WIZARD_TITLE, BUTTON_OK );

        deleteDialog.confirm();
    }

    public void deployProject( String... projectItemNames )
    {
        doAction( ADD_AND_REMOVE );
        addAndRemovePage.add( projectItemNames );
        addAndRemovePage.confirm();
    }

    @Override
    protected SWTBotTreeItem getWidget()
    {
        TreePageObject<SWTBot> tree = new TreePageObject<SWTBot>( bot, serverTreeIndex );

        String[] servers = tree.getAllItems();

        for( String server : servers )
        {
            if( server.contains( serverName ) || server.equals( serverName ) )
            {
                index = serverTreeIndex;
                nodeText = new String[] { server };

                return super.getWidget();
            }
        }

        return null;
    }

    public void removeALL()
    {
        doAction( ADD_AND_REMOVE );
        addAndRemovePage.removeAll();
        addAndRemovePage.confirm();
    }

    public void removeProject( String... projectItemNames )
    {
        doAction( ADD_AND_REMOVE );
        addAndRemovePage.remove( projectItemNames );
        addAndRemovePage.confirm();
    }

    public void startServer()
    {
        doAction( START );
    }

    public void stopServer()
    {
        doAction( STOP );
        checkConsoleHasMessage( SERVER_STOP_MESSAGE, 10000 );
    }

}
