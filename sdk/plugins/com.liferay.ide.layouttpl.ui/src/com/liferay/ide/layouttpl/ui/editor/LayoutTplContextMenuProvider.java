/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Provides context menu actions for the LayoutTplEditor.
 * 
 * @author Gregory Amerson
 */
public class LayoutTplContextMenuProvider extends ContextMenuProvider
{

    /** The editor's action registry. */
    protected ActionRegistry actionRegistry;

    /**
     * Instantiate a new menu context provider for the specified EditPartViewer and ActionRegistry.
     * 
     * @param viewer
     *            the editor's graphical viewer
     * @param registry
     *            the editor's action registry
     * @throws IllegalArgumentException
     *             if registry is <tt>null</tt>.
     */
    public LayoutTplContextMenuProvider( EditPartViewer viewer, ActionRegistry registry )
    {
        super( viewer );

        if( registry == null )
        {
            throw new IllegalArgumentException();
        }

        actionRegistry = registry;
    }

    /**
     * Called when the context menu is about to show. Actions, whose state is enabled, will appear in the context menu.
     * 
     * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    public void buildContextMenu( IMenuManager menu )
    {
        // Add standard action groups to the menu
        GEFActionConstants.addStandardActionGroups( menu );

        // Add actions to the menu
        menu.appendToGroup( GEFActionConstants.GROUP_UNDO, getAction( ActionFactory.UNDO.getId() ) );
        menu.appendToGroup( GEFActionConstants.GROUP_UNDO, getAction( ActionFactory.REDO.getId() ) );
        menu.appendToGroup( GEFActionConstants.GROUP_EDIT, getAction( ActionFactory.DELETE.getId() ) );
    }

    protected IAction getAction( String actionId )
    {
        return actionRegistry.getAction( actionId );
    }

}
