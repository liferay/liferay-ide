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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.portlet.core.model.PortletApp;

import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;

/**
 * @author Kamesh Sampath
 */
public class PortletXmlEditor extends SapphireEditorForXml
{

    public static final String ID = "com.liferay.ide.eclipse.portlet.ui.editor.PortletXmlEditor"; //$NON-NLS-1$

    private static final String EDITOR_DEFINITION_PATH =
        "com.liferay.ide.portlet.ui/com/liferay/ide/portlet/ui/editor/portlet-app.sdef/portlet-app.editor"; //$NON-NLS-1$

    /**
	 * 
	 */
    public PortletXmlEditor()
    {
        super( ID );
        initEditorSettings();
    }

    /**
     * this method will initialize the editor with its settings like model element, UI definition etc.,
     */
    private void initEditorSettings()
    {
        setEditorDefinitionPath( EDITOR_DEFINITION_PATH );
        setRootModelElementType( PortletApp.TYPE );

    }
}
