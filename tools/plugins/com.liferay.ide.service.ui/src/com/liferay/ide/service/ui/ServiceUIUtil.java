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
package com.liferay.ide.service.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.SaveableHelper;

/**
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class ServiceUIUtil
{
    public static boolean shouldCreateServiceBuilderJob( IFile file )
    {
        IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();

        for( IWorkbenchWindow window  : windows )
        {
            IWorkbenchPage[] pages = window.getPages();

            for( IWorkbenchPage page : pages )
            {
                IEditorReference[] editorReferences = page.getEditorReferences();

                for( IEditorReference editorReference : editorReferences )
                {
                    if( file.getName().equals( editorReference.getName() ) )
                    {
                        IWorkbenchPart part = editorReference.getPart( true );

                        if( SaveableHelper.savePart( (ISaveablePart) part, part, window, true ) )
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                }
            }
        }

        return false;
    }

}
