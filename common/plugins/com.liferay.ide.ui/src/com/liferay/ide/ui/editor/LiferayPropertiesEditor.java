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
package com.liferay.ide.ui.editor;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileEditor;
import org.eclipse.ui.IEditorInput;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LiferayPropertiesEditor extends PropertiesFileEditor
{

    @Override
    protected void doSetInput( IEditorInput input ) throws CoreException
    {
        super.doSetInput( input );

        File file = (File) input.getAdapter( File.class );

        ((LiferayPropertiesSourceViewerConfiguration) getSourceViewerConfiguration()).setPropertilesFile( file );
    }

    @Override
    protected void initializeEditor()
    {
        super.initializeEditor();

        setSourceViewerConfiguration( new LiferayPropertiesSourceViewerConfiguration( this ) );
    }
}
