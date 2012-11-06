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
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.editor;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Contributes actions to a toolbar. This class is tied to the editor in the definition of editor-extension (see
 * plugin.xml).
 * 
 * @author Gregory Amerson
 */
public class LayoutTplEditorActionBarContributor extends ActionBarContributor
{

    /**
     * Create actions managed by this contributor.
     * 
     * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
     */
    protected void buildActions()
    {
        addRetargetAction( new DeleteRetargetAction() );
        addRetargetAction( new UndoRetargetAction() );
        addRetargetAction( new RedoRetargetAction() );
    }

    /**
     * Add actions to the given toolbar.
     * 
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    public void contributeToToolBar( IToolBarManager toolBarManager )
    {
        toolBarManager.add( getAction( ActionFactory.UNDO.getId() ) );
        toolBarManager.add( getAction( ActionFactory.REDO.getId() ) );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
     */
    protected void declareGlobalActionKeys()
    {
        // currently none
    }

}
