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

package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.project.ui.ProjectUI;

import java.lang.reflect.Method;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.cheatsheets.Messages;
import org.eclipse.ui.internal.cheatsheets.views.CheatSheetExpandRestoreAction;
import org.eclipse.ui.internal.cheatsheets.views.CheatSheetViewer;
import org.eclipse.ui.part.ViewPart;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class UpgradeAssistantView extends ViewPart
{

    protected static final String ID = "com.liferay.ide.project.ui.upgradeView";
    private CheatSheetViewer _viewer;
    private boolean _actionBarContributed;
    private IMemento _memento;
    private CheatSheetExpandRestoreAction _expandRestoreAction;
    private Action _copyAction;

    void contributeToActionBars()
    {
        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager tbmanager = bars.getToolBarManager();

        _expandRestoreAction =
            new CheatSheetExpandRestoreAction( Messages.COLLAPSE_ALL_BUT_CURRENT_TOOLTIP, false, _viewer );

        _copyAction = new Action( "copy")
        {
            public void run()
            {
                _viewer.copy( );
            }
        };

        _copyAction.setEnabled( false );
        tbmanager.add( _expandRestoreAction );
        bars.setGlobalActionHandler( ActionFactory.COPY.getId(), _copyAction );

        try
        {
            final Method setExpandRestoreAction = CheatSheetViewer.class.getDeclaredMethod(
                "setExpandRestoreAction", CheatSheetExpandRestoreAction.class );
            setExpandRestoreAction.setAccessible( true );
            setExpandRestoreAction.invoke( _viewer, _expandRestoreAction );
        }
        catch( Exception e )
        {
        }

        _viewer.setCopyAction( _copyAction );
    }

    @Override
    public void createPartControl( Composite parent )
    {
        _viewer = new CheatSheetViewer( false );
        _viewer.createPartControl( parent );

        if( !_actionBarContributed )
        {
//            contributeToActionBars();
            _actionBarContributed = true;
        }

        if( _memento != null )
        {
            restoreState( _memento );
        }
        else
        {
            loadUpgradeSheet();
        }
    }

    private void loadUpgradeSheet()
    {
        _viewer.setInput( "liferay_7_upgrade", "Liferay 7.0 Upgrade",
            ProjectUI.getDefault().getBundle().getEntry( "cheatsheets/liferay_7_upgrade/liferay_7_upgrade.xml" ) );
    }

    private void restoreState( IMemento memento )
    {
        IMemento input = memento.getChild( "input" );

        if( input == null )
        {
            loadUpgradeSheet();
        }
    }

    @Override
    public void saveState( IMemento memento )
    {
        // TODO saveSate method
    }

    @Override
    public void init( IViewSite site, IMemento memento ) throws PartInitException
    {
        super.init( site );

        _memento = memento;
    }

    @Override
    public void setFocus()
    {
        if( _viewer != null )
        {
            _viewer.setFocus();
        }
    }

}
