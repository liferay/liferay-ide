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
package com.liferay.ide.adt.ui.handlers;

import com.liferay.ide.adt.ui.wizard.GenerateCustomServicesWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * @author Gregory Amerson
 */
public class GenerateCustomServicesHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final ISelection selection = HandlerUtil.getCurrentSelection( event );

        if( selection instanceof IStructuredSelection )
        {
            final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

            Object selected = structuredSelection.getFirstElement();

            IJavaProject project = null;

            if( selected instanceof IResource )
            {
                project = JavaCore.create( ( (IResource) selected ).getProject() );
            }
            else if (selected instanceof IJavaProject )
            {
                project = (IJavaProject) selected;
            }

            if( project != null )
            {
                final GenerateCustomServicesWizard wizard = new GenerateCustomServicesWizard( project );

                new WizardDialog( HandlerUtil.getActiveShellChecked( event ), wizard ).open();
            }
        }

        return null;
    }


}
