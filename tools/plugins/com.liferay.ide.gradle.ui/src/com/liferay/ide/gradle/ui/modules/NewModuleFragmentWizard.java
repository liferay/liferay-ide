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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.modules.NewModuleFragmentOp;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.wizard.WorkingSetCustomPart;
import com.liferay.ide.ui.LiferayWorkspacePerspectiveFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.wst.web.internal.DelegateConfigurationElement;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class NewModuleFragmentWizard extends SapphireWizard<NewModuleFragmentOp> implements IWorkbenchWizard, INewWizard
{

    private boolean firstErrorMessageRemoved = false;

    public NewModuleFragmentWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewModuleFragmentWizard.class ).wizard() );
    }

    private void addToWorkingSets( IProject newProject ) throws Exception
    {
        if( newProject != null )
        {
            for( final FormComponentPart formPart : part().getPages().get( 0 ).children().all() )
            {
                if( formPart instanceof WorkingSetCustomPart )
                {
                    final WorkingSetCustomPart workingSetPart = (WorkingSetCustomPart) formPart;
                    final IWorkingSet[] workingSets = workingSetPart.getWorkingSets();

                    if( !CoreUtil.isNullOrEmpty( workingSets ) )
                    {
                        PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets( newProject, workingSets );
                    }
                }
            }
        }
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
    }

    private void openLiferayPerspective( IProject newProject )
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();

        IPerspectiveDescriptor perspective = workbench.getActiveWorkbenchWindow().getActivePage().getPerspective();

        if( perspective.getId().equals( LiferayWorkspacePerspectiveFactory.ID ) )
        {
            return;
        }

        // open the "final" perspective
        final IConfigurationElement element = new DelegateConfigurationElement( null )
        {

            @Override
            public String getAttribute( String aName )
            {
                if( aName.equals( "finalPerspective" ) )
                {
                    return LiferayWorkspacePerspectiveFactory.ID;
                }

                return super.getAttribute( aName );
            }
        };

        BasicNewProjectResourceWizard.updatePerspective( element );

        // select and reveal
        BasicNewResourceWizard.selectAndReveal( newProject, workbench.getActiveWorkbenchWindow() );
    }

    @Override
    protected void performPostFinish()
    {
        super.performPostFinish();

        final NewModuleFragmentOp op = element().nearest( NewModuleFragmentOp.class );

        final IProject project = CoreUtil.getProject( op.getProjectName().content() );

        try
        {
            addToWorkingSets( project );

        }
        catch( Exception ex )
        {
            ProjectUI.logError( "Unable to add project to working set", ex );
        }

        openLiferayPerspective( project );
    }

    private static NewModuleFragmentOp createDefaultOp()
    {
        return NewModuleFragmentOp.TYPE.instantiate();
    }

}
