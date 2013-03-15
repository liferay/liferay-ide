/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

/**
 * @author Greg Amerson
 */
public class NewServerDropDownAction extends Action implements IMenuCreator, IWorkbenchWindowPulldownDelegate2
{
    protected Menu fMenu;
    protected Shell fWizardShell;

    public NewServerDropDownAction()
    {
        fMenu = null;
        setMenuCreator( this );
    }

    public void dispose()
    {
    }

    public Action getDefaultAction( Shell shell )
    {
        Action[] actions = getActions( shell );

        if( actions.length > 1 )
        {
            return actions[1];
        }

        return null;
    }

    protected Action[] getActions( Shell shell )
    {
        return new Action[] { new NewPluginsSDKAction( shell ), new NewServerAction( shell ) };
    }

    public Menu getMenu( Control parent )
    {
        if( fMenu == null )
        {
            fMenu = new Menu( parent );

            Action[] actions = getActions( parent.getShell() );

            for( Action action : actions )
            {
                ActionContributionItem item = new ActionContributionItem( action );
                item.fill( fMenu, -1 );
            }
        }

        return fMenu;
    }

    public Menu getMenu( Menu parent )
    {
        return null;
    }

    public void init( IWorkbenchWindow window )
    {
        fWizardShell = window.getShell();
    }

    public void run( IAction action )
    {
        getDefaultAction( fWizardShell ).run();
    }

    public void selectionChanged( IAction action, ISelection selection )
    {
    }

}
