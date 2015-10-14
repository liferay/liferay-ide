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

package com.liferay.ide.project.ui.action;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.liferay.ide.project.ui.wizard.SDKProjectImportWizard;

/**
 * @author Simon Jiang
 */
public class ImportSDKProjectAction implements IObjectActionDelegate
{
    private ISelection fSelection;

    public ImportSDKProjectAction()
    {
    }

    @Override
    public void run( IAction action )
    {
        if( fSelection instanceof IStructuredSelection )
        {
            Object[] elems = ( (IStructuredSelection) fSelection ).toArray();

            IPath projectLocation = null;

            Object elem = elems[0];

            if( elem instanceof IFolder )
            {
                projectLocation = ( (IFolder) elem ).getLocation();
            }

            SDKProjectImportWizard wizard =
                new SDKProjectImportWizard(  projectLocation );

            final WizardDialog dialog = new WizardDialog( Display.getDefault().getActiveShell(), wizard );

            dialog.open();
       }
    }

    @Override
    public void selectionChanged( IAction action, ISelection selection )
    {
        fSelection = selection;
    }

    @Override
    public void setActivePart( IAction action, IWorkbenchPart targetPart )
    {
    }

}
