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

package com.liferay.ide.swtbot.liferay.ui.action;

import com.liferay.ide.swtbot.liferay.ui.UIAction;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesContinueDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.swtbot.ui.eclipse.page.PackageExplorerView;
import com.liferay.ide.swtbot.ui.eclipse.page.ProjectExplorerView;
import com.liferay.ide.swtbot.ui.eclipse.page.ServersView;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;

/**
 * @author Terry Jia
 */
public class ViewAction extends UIAction
{

    private DeleteResourcesContinueDialog continueDeleteResourcesDialog =
        new DeleteResourcesContinueDialog( bot );
    private DeleteResourcesDialog deleteResourcesDialog = new DeleteResourcesDialog( bot );
    private PackageExplorerView packageExplorerView = new PackageExplorerView( bot );
    private ProjectExplorerView projectExplorerView = new ProjectExplorerView( bot );
    private ServersView serversView = new ServersView( bot );

    public ViewAction( SWTWorkbenchBot bot )
    {
        super( bot );
    }

    public void deleteProject( String name )
    {
        getProjects().getTreeItem( name ).doAction( DELETE );

        deleteResourcesDialog.getDeleteFromDisk().select();

        deleteResourcesDialog.confirm();

        try
        {
            long origin = SWTBotPreferences.TIMEOUT;

            SWTBotPreferences.TIMEOUT = 1500;

            continueDeleteResourcesDialog.confirm();

            SWTBotPreferences.TIMEOUT = origin;
        }
        catch( Exception e )
        {
        }
    }

    public void deleteProject( String... nodes )
    {
        getProjects().expandNode( nodes ).doAction( DELETE );

        deleteResourcesDialog.getDeleteFromDisk().select();

        deleteResourcesDialog.confirm();

        try
        {
            long origin = SWTBotPreferences.TIMEOUT;

            SWTBotPreferences.TIMEOUT = 1500;

            continueDeleteResourcesDialog.confirm();

            SWTBotPreferences.TIMEOUT = origin;
        }
        catch( Exception e )
        {
        }
    }

    public void deleteProjects()
    {
        Tree projects = getProjects();

        String[] names = projects.getAllItems();

        for( String name : names )
        {
            deleteProject( name );
        }
    }

    public void deleteProjects( String[] names )
    {
        for( String name : names )
        {
            deleteProject( name );
        }
    }

    public void deleteProjectsExcludeNames( String... names )
    {
        String[] projectNames = getProjects().getAllItems();

        for( String projectName : projectNames )
        {
            boolean include = false;

            for( String name : names )
            {
                if( name.equals( projectName ) )
                {
                    include = true;

                    break;
                }

                getProjects().getTreeItem( projectName ).collapse();
            }

            if( !include )
            {
                deleteProject( projectName );
            }
        }
    }

    public TreeItem fetchProjectFile( String... files )
    {
        return getProjects().expandNode( files );
    }

    public TreeItem getProject( String name )
    {
        return getProjects().getTreeItem( name );
    }

    public Tree getProjects()
    {
        try
        {
            return projectExplorerView.getProjects();
        }
        catch( Exception e )
        {
            return packageExplorerView.getProjects();
        }
    }

    public void openAddAndRemoveDialog( String serverLabel )
    {
        serversView.getServers().getTreeItem( serverLabel ).contextMenu( ADD_AND_REMOVE );
    }

    public void openLiferayPortalHome( String serverLabel )
    {
        serversView.getServers().getTreeItem( serverLabel ).contextMenu( OPEN_LIFERAY_PORTAL_HOME );
    }

    public void openServerEditor( String serverLabel )
    {
        serversView.getServers().getTreeItem( serverLabel ).doubleClick();
    }

    public void serverDebug( String serverLabel )
    {
        serversView.getServers().getTreeItem( serverLabel ).select();

        serversView.clickDebugBtn();
    }

    public void serverStart( String serverLabel )
    {
        serversView.getServers().getTreeItem( serverLabel ).select();

        serversView.clickStartBtn();
    }

    public void serverStop( String serverLabel )
    {
        serversView.getServers().getTreeItem( serverLabel ).select();

        serversView.clickStopBtn();
    }

    public void showServersView()
    {
        ide.showServersView();
    }

}
