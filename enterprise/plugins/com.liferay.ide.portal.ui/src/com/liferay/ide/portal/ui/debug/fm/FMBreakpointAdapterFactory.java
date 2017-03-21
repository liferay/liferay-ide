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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "rawtypes" )
public class FMBreakpointAdapterFactory implements IAdapterFactory
{

    public Object getAdapter( Object adaptableObject, Class adapterType )
    {
        if( adaptableObject instanceof ITextEditor )
        {
            ITextEditor editorPart = (ITextEditor) adaptableObject;
            IResource resource = (IResource) editorPart.getEditorInput().getAdapter( IResource.class );

            if( resource != null )
            {
                String extension = resource.getFileExtension();

                if( extension != null && extension.equals( "ftl" ) )
                {
                    return new FMLineBreakpointAdapter();
                }
            }
        }

        return null;
    }

    public Class[] getAdapterList()
    {
        return new Class[] { IToggleBreakpointsTarget.class };
    }

}
