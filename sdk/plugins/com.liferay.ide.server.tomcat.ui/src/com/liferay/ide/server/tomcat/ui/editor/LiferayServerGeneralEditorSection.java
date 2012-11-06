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

package com.liferay.ide.server.tomcat.ui.editor;

import org.eclipse.jst.server.tomcat.ui.internal.editor.ServerGeneralEditorSection;

@SuppressWarnings( "restriction" )
public class LiferayServerGeneralEditorSection extends ServerGeneralEditorSection
{

    public LiferayServerGeneralEditorSection()
    {
        super();
    }

    @Override
    protected void initialize()
    {
        super.initialize();

        if( this.noPublish != null )
        {
            this.noPublish.setEnabled( false );
        }

        if( this.separateContextFiles != null )
        {
            this.separateContextFiles.setEnabled( false );
        }

        if( this.secure != null )
        {
            this.secure.setEnabled( false );
        }
    }

}
