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

package com.liferay.ide.gradle.ui.modules;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.gradle.core.modules.NewModuleFragmentFilesOp;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Terry Jia
 */
public class NewModuleFragmentFilesWizard extends SapphireWizard<NewModuleFragmentFilesOp> implements IWorkbenchWizard, INewWizard
{

    private IProject initialProject;

    public NewModuleFragmentFilesWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewModuleFragmentFilesWizard.class ).wizard() );
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        if( selection != null && !selection.isEmpty() )
        {
            final Object element = selection.getFirstElement();

            if( element instanceof IResource )
            {
                initialProject = ( (IResource) element ).getProject();
            }
            else if( element instanceof IJavaProject )
            {
                initialProject = ( (IJavaProject) element ).getProject();
            }
            else if( element instanceof IPackageFragment )
            {
                initialProject = ( (IJavaElement) element ).getResource().getProject();
            }
            else if( element instanceof IJavaElement )
            {
                initialProject = ( (IJavaElement) element ).getResource().getProject();
            }

            if( initialProject != null )
            {
                final IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, initialProject );

                if( bundleProject != null && bundleProject.isFragmentBundle() )
                {
                    element().setProjectName( initialProject.getName() );
                }
            }
        }
    }
    private static NewModuleFragmentFilesOp createDefaultOp()
    {
        return NewModuleFragmentFilesOp.TYPE.instantiate();
    }

}
