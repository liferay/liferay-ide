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
package com.liferay.ide.portal.ui.debug.fm;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portal.core.debug.fm.FMValue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;


/**
 * @author Gregory Amerson
 */
public class FMModelPresentation extends LabelProvider implements IDebugModelPresentation
{

    public void computeDetail( IValue value, IValueDetailListener listener )
    {
        String detail = StringPool.EMPTY;

        if( value instanceof FMValue )
        {
            FMValue fmValue = (FMValue) value;
            detail = fmValue.getDetailString();
        }

        listener.detailComputed( value, detail );
    }

    public String getEditorId( IEditorInput input, Object element )
    {
        if( element instanceof IFile || element instanceof ILineBreakpoint )
        {
            return "com.liferay.ide.freemarker.editor.FreemarkerEditor";
        }

        return null;
    }

    public IEditorInput getEditorInput( Object element )
    {
        IEditorInput editorInput = null;

        if( element instanceof IFile )
        {
            editorInput = new FileEditorInput( (IFile) element );
        }
        else if( element instanceof ILineBreakpoint )
        {
            final IMarker marker = ( (ILineBreakpoint) element ).getMarker();

            IResource resource = marker.getResource();

            if( resource instanceof IFile )
            {
                editorInput = new FileEditorInput( (IFile) resource );
            }
        }

        return editorInput;
    }

    public Image getImage(Object element)
    {
        return null;
    }

    public String getText(Object element)
    {
        return null;
    }

    public void setAttribute( String attribute, Object value )
    {
    }

}
