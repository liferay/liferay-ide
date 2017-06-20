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

package com.liferay.ide.swtbot.ui.tests.eclipse.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import com.liferay.ide.swtbot.ui.tests.UIBase;
import com.liferay.ide.swtbot.ui.tests.page.DialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;
import com.liferay.ide.swtbot.ui.tests.page.ViewPO;

/**
 * @author Terry Jia
 */
public class PackageExplorerViewPO extends ViewPO implements UIBase
{

    private TreePO _projectsTree;
    private DeleteResourcesDialogPO _deleteResourcesDialog;
    private DialogPO _continueDeleteResourcesDialog;

    public PackageExplorerViewPO( SWTWorkbenchBot bot )
    {
        super( bot, LABEL_PACKAGE_EXPLORER );

        _projectsTree = new TreePO( bot );
        _deleteResourcesDialog = new DeleteResourcesDialogPO( bot );
        _continueDeleteResourcesDialog = new DialogPO( bot, TITLE_DELETE_RESOURCES, BUTTON_CANCEL, BUTTON_CONTINUE );
    }

    public void deleteProjectExcludeNames( String[] names, boolean deleteFromDisk )
    {
        if( !_projectsTree.hasItems() )
            return;

        String[] allItemNames = _projectsTree.getAllItems();

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
                _projectsTree.getTreeItem( itemName ).collapse();
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
        if( !_projectsTree.hasItems() )
            return;

        _projectsTree.getTreeItem( name ).doAction( BUTTON_DELETE );

        sleep( 1000 );

        if( deleteFromDisk )
        {
            _deleteResourcesDialog.confirmDeleteFromDisk();
        }

        _deleteResourcesDialog.confirm();

        sleep( 1000 );
        _continueDeleteResourcesDialog.closeIfOpen();
    }

    public boolean hasProjects()
    {
        return _projectsTree.hasItems();
    }

    public TreePO getProjectTree()
    {
        return _projectsTree;
    }

}
