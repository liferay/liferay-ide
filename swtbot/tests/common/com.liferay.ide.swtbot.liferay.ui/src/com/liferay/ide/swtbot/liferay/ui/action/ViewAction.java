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

    private final DeleteResourcesContinueDialog continueDeleteResourcesDialog =
        new DeleteResourcesContinueDialog( bot );
    private final DeleteResourcesDialog deleteResourcesDialog = new DeleteResourcesDialog( bot );
    private final PackageExplorerView packageExplorerView = new PackageExplorerView( bot );
    private final ProjectExplorerView projectExplorerView = new ProjectExplorerView( bot );
    private final ServersView serversView = new ServersView( bot );

    public ViewAction( final SWTWorkbenchBot bot )
    {
        super( bot );
    }

    public void deleteProject( final String name )
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

    public void deleteProject( final String... nodes )
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
        final Tree projects = getProjects();

        final String[] names = projects.getAllItems();

        for( final String name : names )
        {
            deleteProject( name );
        }
    }

    public void deleteProjects( final String[] names )
    {
        for( final String name : names )
        {
            deleteProject( name );
        }
    }

    public void deleteProjectsExcludeNames( final String... names )
    {
        final String[] projectNames = getProjects().getAllItems();

        for( final String projectName : projectNames )
        {
            boolean include = false;

            for( final String name : names )
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

    public TreeItem fetchProjectFile( final String... files )
    {
        return getProjects().expandNode( files );
    }

    public TreeItem getProject( final String name )
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

    public void openAddAndRemoveDialog( final String serverLabel )
    {
        serversView.getServers().getTreeItem( serverLabel ).contextMenu( ADD_AND_REMOVE );
    }

    public void openLiferayPortalHome( final String serverLabel )
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
