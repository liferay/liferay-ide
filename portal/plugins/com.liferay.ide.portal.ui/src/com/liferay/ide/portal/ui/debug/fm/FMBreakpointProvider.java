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

import com.liferay.ide.portal.core.debug.fm.FMLineBreakpoint;
import com.liferay.ide.portal.ui.debug.IDebugEditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.ISourceEditingTextTools;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IBreakpointProvider;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class FMBreakpointProvider implements IBreakpointProvider
{

    private ISourceEditingTextTools fSourceEditingTextTools;

    public FMBreakpointProvider()
    {
    }

    public IStatus addBreakpoint( IDocument document, IEditorInput input, int lineNumber, int offset )
        throws CoreException
    {
        IStatus retval = Status.OK_STATUS;

        if( fSourceEditingTextTools instanceof IDebugEditor )
        {
            IDebugEditor debugEditor = (IDebugEditor) fSourceEditingTextTools;

            IStatus status = debugEditor.validateBreakpointPosition( lineNumber, offset );

            if( status.isOK()  )
            {
                if( input instanceof IFileEditorInput )
                {
                    final IFileEditorInput fileEditorInput = (IFileEditorInput) input;
                    final IFile file = fileEditorInput.getFile();

                    final FMLineBreakpoint bp = new FMLineBreakpoint( file, lineNumber );

                    DebugPlugin.getDefault().getBreakpointManager().addBreakpoint( bp );
                }
            }
            else
            {
                retval = status;
            }
        }

        return retval;
    }

    public IResource getResource( IEditorInput input )
    {
        return null;
    }


    public ISourceEditingTextTools getfSourceEditingTextTools()
    {
        return this.fSourceEditingTextTools;
    }

    public void setSourceEditingTextTools( ISourceEditingTextTools sourceEditingTextTools )
    {
        fSourceEditingTextTools = sourceEditingTextTools;
    }

}
