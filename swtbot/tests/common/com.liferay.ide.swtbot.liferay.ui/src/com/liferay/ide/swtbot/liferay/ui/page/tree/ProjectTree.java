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

package com.liferay.ide.swtbot.liferay.ui.page.tree;

import com.liferay.ide.swtbot.ui.eclipse.page.DeleteResourcesDialog;
import com.liferay.ide.swtbot.ui.page.Tree;
import com.liferay.ide.swtbot.ui.page.TreeItem;

import org.eclipse.swtbot.swt.finder.SWTBot;

/**
 * @author Li Lu
 */
public class ProjectTree extends TreeItem
{

    DeleteResourcesDialog deleteDialog;

    public ProjectTree( SWTBot bot, String nodeText )
    {
        super( bot, new Tree( bot ), nodeText );

        deleteDialog = new DeleteResourcesDialog( bot );
    }

    public void deleteProject()
    {
        doAction( DELETE );

        deleteDialog.getDeleteFromDisk().select();

        deleteDialog.confirm();
    }

}
