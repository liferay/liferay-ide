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

package com.liferay.ide.project.ui.modules;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.NewLiferayComponentOpMethods;
import com.liferay.ide.project.ui.ProjectUI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentWizard extends SapphireWizard<NewLiferayComponentOp>
    implements IWorkbenchWizard, INewWizard
{

    private boolean firstErrorMessageRemoved = false;
    private IProject initialProject;
    private IPackageFragment initialPackage;

    public NewLiferayComponentWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewLiferayComponentWizard.class ).wizard() );
    }

    @Override
    public IWizardPage[] getPages()
    {
        final IWizardPage[] wizardPages = super.getPages();

        if( !firstErrorMessageRemoved && wizardPages != null )
        {
            final SapphireWizardPage wizardPage = (SapphireWizardPage) wizardPages[0];

            final String message = wizardPage.getMessage();
            final int messageType = wizardPage.getMessageType();

            if( messageType == IMessageProvider.ERROR && !CoreUtil.isNullOrEmpty( message ) )
            {
                wizardPage.setMessage( "Please enter a project name.", SapphireWizardPage.NONE ); //$NON-NLS-1$
                firstErrorMessageRemoved = true;
            }
        }

        return wizardPages;
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
                initialPackage = ( (IPackageFragment) element );
                initialProject = ( (IJavaElement) element ).getResource().getProject();
            }
            else if( element instanceof IJavaElement )
            {
                initialProject = ( (IJavaElement) element ).getResource().getProject();
            }

            if( initialProject != null )
            {
                final IBundleProject bundleProject = LiferayCore.create( IBundleProject.class, initialProject );

                if( bundleProject != null && "jar".equals( bundleProject.getBundleShape() ) &&
                    !bundleProject.isFragmentBundle() )
                {
                    element().setProjectName( initialProject.getName() );

                    if( initialPackage != null )
                    {
                        element().setPackageName( initialPackage.getElementName() );
                    }
                }
            }
        }
    }

    private static NewLiferayComponentOp createDefaultOp()
    {
        return NewLiferayComponentOp.TYPE.instantiate();
    }

    @Override
    public void performPostFinish()
    {
        final NewLiferayComponentOp op = element().nearest( NewLiferayComponentOp.class );
        IProject currentProject = CoreUtil.getProject( op.getProjectName().content() );

        if( currentProject == null )
        {
            return;
        }

        IJavaProject javaProject = JavaCore.create( currentProject );

        if( javaProject == null )
        {
            return;
        }

        try
        {
            String componentClass = op.getComponentClassName().content();
            String packageName = op.getPackageName().text();

            final IType type = javaProject.findType( packageName, componentClass );

            if( type == null )
            {
                return;
            }

            final IFile classFile = (IFile) type.getResource();

            if( classFile != null )
            {
                Display.getCurrent().asyncExec( new Runnable()
                {

                    public void run()
                    {
                        try
                        {
                            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                            IDE.openEditor( page, classFile, true );
                        }
                        catch( Exception e )
                        {
                            ProjectUI.logError( e );
                        }
                    }
                } );
            }
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }
}
