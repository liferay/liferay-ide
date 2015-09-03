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

package com.liferay.ide.project.ui.migration;

import blade.migrate.api.MigrationListener;
import blade.migrate.api.Problem;

import com.liferay.ide.project.ui.ProjectUI;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * @author Gregory Amerson
 */
public class WorkspaceMigrationImpl implements MigrationListener
{

    @Override
    public void problemsFound( List<Problem> problems )
    {
        final MigrationTask task = new MigrationTask();

        task.addProblems( problems );

        try
        {
            ProjectUI.getDefault().saveMigrationTask( task );
        }
        catch( CoreException e )
        {
            StatusManager.getManager().handle( e.getStatus(), StatusManager.SHOW );
        }
    }

}
