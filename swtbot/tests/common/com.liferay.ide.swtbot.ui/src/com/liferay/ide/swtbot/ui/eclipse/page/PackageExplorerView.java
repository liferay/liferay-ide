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

package com.liferay.ide.swtbot.ui.eclipse.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import com.liferay.ide.swtbot.ui.UI;
import com.liferay.ide.swtbot.ui.page.Dialog;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.View;

/**
 * @author Terry Jia
 */
public class PackageExplorerView extends View implements UI
{

    private Tree projects;
    private DeleteResourcesDialog deleteResourcesDialog;
    private Dialog continueDeleteResourcesDialog;

    public PackageExplorerView( SWTWorkbenchBot bot )
    {
        super( bot, LABEL_PACKAGE_EXPLORER );

        projects = new Tree( bot );
        deleteResourcesDialog = new DeleteResourcesDialog( bot );
        continueDeleteResourcesDialog = new Dialog( bot, TITLE_DELETE_RESOURCES, CANCEL, CONTINUE );
    }

    public void deleteProjectExcludeNames( String[] names, boolean deleteFromDisk )
    {
        if( !projects.hasItems() )
            return;

        String[] allItemNames = projects.getAllItems();

        for( String itemName : allItemNames )
        {
            boolean include = false;

            if( names != null )
            {

                for( String name : names )
                {
                    if( name.equals( itemName ) )
                    {
                        include = true;

                        break;
                    }

                }
                projects.getTreeItem( itemName ).collapse();
            }

            if( !include )
            {

                deleteResouceByName( itemName, deleteFromDisk );
            }
        }
    }

    public void deleteResoucesByNames( String[] names, boolean deleteFromDisk )
    {
        for( String name : names )
        {
            deleteResouceByName( name, deleteFromDisk );
        }
    }

    public void deleteResouceByName( String name, boolean deleteFromDisk )
    {
        if( !projects.hasItems() )
            return;

        projects.getTreeItem( name ).doAction( DELETE );

        sleep( 1000 );

        if( deleteFromDisk )
        {
            deleteResourcesDialog.getDeleteFromDisk().select();
        }

        deleteResourcesDialog.confirm();

        sleep( 1000 );
        continueDeleteResourcesDialog.closeIfOpen();
    }

    public boolean hasProjects()
    {
        return projects.hasItems();
    }

    public Tree getProjectTree()
    {
        return projects;
    }

}
