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

import com.liferay.blade.api.MigrationConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.MarkerUtil;
import com.liferay.ide.project.core.upgrade.MigrationProblems;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Lovett Li
 */
public class RemoveAction extends SelectionProviderAction implements IAction
{

    public RemoveAction( ISelectionProvider provider )
    {
        super( provider, "Remove" );
    }

    @Override
    public void run()
    {
        final MigrationView mv = (MigrationView) UIUtil.showView( MigrationView.ID );
        final CommonViewer commonViewer = mv.getCommonViewer();

        Object selection = commonViewer.getStructuredSelection().getFirstElement();

        if( selection instanceof MigrationProblems )
        {
            MigrationProblems migrationProblem = (MigrationProblems) selection;

            MigrationUtil.removeMigrationProblem( migrationProblem );
            IResource project = MigrationUtil.getResourceFromMigrationProblems( migrationProblem );

            if( project != null )
            {
                MarkerUtil.clearMarkers( project, MigrationConstants.MARKER_TYPE, null );
            }
        }

        commonViewer.setInput( CoreUtil.getWorkspaceRoot() );

    }

    @Override
    public void selectionChanged( IStructuredSelection selection )
    {
        setEnabled( true );
    }

}
