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

package com.liferay.ide.swtbot.project.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.tests.eclipse.page.DeleteResourcesDialogPO;
import com.liferay.ide.swtbot.ui.tests.page.TreeItemPO;
import com.liferay.ide.swtbot.ui.tests.page.TreePO;

/**
 * @author Li Lu
 */
public class ProjectTreePO extends TreeItemPO implements ProjectBuildAction
{

    DeleteResourcesDialogPO _deleteDialog;

    public ProjectTreePO( SWTBot bot, String nodeText )
    {
        super( bot, new TreePO( bot ), nodeText );

        _deleteDialog = new DeleteResourcesDialogPO( bot );
    }

    public void deleteProject()
    {
        doAction( BUTTON_DELETE );

        _deleteDialog.confirmDeleteFromDisk();
        
        _deleteDialog.confirm();
    }

}