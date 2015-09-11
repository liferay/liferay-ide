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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Gregory Amerson
 */
public class MigrationActionProvider extends CommonActionProvider
{

    private SelectionProviderAction _autoMigrateAction;
    private SelectionProviderAction _ignoreAction;
    private SelectionProviderAction _markDoneAction;
    private SelectionProviderAction _markUndoneAction;
    private SelectionProviderAction _openAction;
    private SelectionProviderAction _removeAction;

    public MigrationActionProvider()
    {
        super();
    }

    private void addListeners( CommonViewer cv )
    {
        cv.addOpenListener( new IOpenListener()
        {
            public void open( OpenEvent event )
            {
                try
                {
                    final IStructuredSelection sel = (IStructuredSelection) event.getSelection();
                    final Object data = sel.getFirstElement();

                    if( !( data instanceof IFile ) )
                    {
                        return;
                    }

                    _openAction.run();
                }
                catch( Exception e )
                {
                }
            }
        });
    }

    @Override
    public void fillContextMenu( IMenuManager menu )
    {
        menu.add( _openAction );
        menu.add( _autoMigrateAction );
        menu.add( new Separator() );
        menu.add( _markDoneAction );
        menu.add( _markUndoneAction );
        menu.add( _ignoreAction );
        menu.add( new Separator() );
        menu.add( _removeAction );
    }

    public void init( ICommonActionExtensionSite site )
    {
        super.init( site );

        final ICommonViewerSite viewerSite = site.getViewSite();

        if( viewerSite instanceof ICommonViewerWorkbenchSite )
        {
            StructuredViewer v = site.getStructuredViewer();

            if( v instanceof CommonViewer )
            {
                CommonViewer cv = (CommonViewer) v;
                ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite) viewerSite;

                makeActions( wsSite.getSelectionProvider() );

                addListeners( cv );
            }
        }
    }

    public MigrationActionProvider makeActions( ISelectionProvider provider )
    {
        // create the open action
        _openAction = new OpenAction( provider );
        _autoMigrateAction = new AutoMigrateAction( provider );
        _markDoneAction = new MarkDoneAction( provider );
        _markUndoneAction = new MarkUndoneAction( provider );
        _ignoreAction = new IgnoreAction( provider );
        _removeAction = new RemoveAction( provider );

        return this;
    }
}
