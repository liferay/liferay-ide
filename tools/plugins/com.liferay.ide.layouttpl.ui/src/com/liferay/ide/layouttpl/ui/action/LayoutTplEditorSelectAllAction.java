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
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.action;

import com.liferay.ide.layouttpl.ui.editor.LayoutTplEditor;
import com.liferay.ide.layouttpl.ui.parts.PortletColumnEditPart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.SelectAllAction;


/**
 * @author Cindy Li
 */
public class LayoutTplEditorSelectAllAction extends SelectAllAction
{

    private LayoutTplEditor editor;

    public LayoutTplEditorSelectAllAction( LayoutTplEditor editor )
    {
        super( editor );
        this.editor = editor;
    }

    @Override
    public void run()
    {
        GraphicalViewer viewer = (GraphicalViewer) this.editor
                        .getAdapter(GraphicalViewer.class);
        if( viewer != null )
        {
            for (Object obj : viewer.getEditPartRegistry().values())
            {
                if (obj instanceof PortletColumnEditPart )
                {
                    viewer.appendSelection((EditPart)obj);
                }
            }
        }
    }

}
