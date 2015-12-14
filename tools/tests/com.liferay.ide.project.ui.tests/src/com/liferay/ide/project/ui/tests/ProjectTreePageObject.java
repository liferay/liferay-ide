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

package com.liferay.ide.project.ui.tests;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.ui.tests.swtbot.page.TreeItemPageObject;

/**
 * @author Li Lu
 */
public class ProjectTreePageObject<T extends SWTBot> extends TreeItemPageObject<T> implements ProjectBuildAction
{

    DeleteProjectDialogPageObject<SWTBot> deleteDialog;

    public ProjectTreePageObject( SWTBot bot, String nodeText )
    {
        super( bot, nodeText );

        deleteDialog = new DeleteProjectDialogPageObject<SWTBot>( bot );
    }

    public void deleteProject()
    {
        doAction( BUTTON_DELETE );

        deleteDialog.confirmDeleteFromDisk();
    }
}
