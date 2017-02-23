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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.platform.PathBridge;
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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.jsf.NewLiferayJSFModuleProjectOp;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.wizard.WorkingSetCustomPart;
import com.liferay.ide.ui.LiferayWorkspacePerspectiveFactory;


/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class NewLiferayJSFModuleProjectWizard extends SapphireWizard<NewLiferayJSFModuleProjectOp>
    implements IWorkbenchWizard, INewWizard
{
    private boolean firstErrorMessageRemoved = false;

    public NewLiferayJSFModuleProjectWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewLiferayJSFModuleProjectWizard.class ).wizard() );
    }

    private void addToWorkingSets( IProject newProject ) throws Exception
    {
        if (newProject != null )    
        {
            for( final FormComponentPart formPart : part().getPages().get( 0 ).children().all() )
            {
                if( formPart instanceof WorkingSetCustomPart )
                {
                    final WorkingSetCustomPart workingSetPart = (WorkingSetCustomPart) formPart;
                    final IWorkingSet[] workingSets = workingSetPart.getWorkingSets();

                    if( ! CoreUtil.isNullOrEmpty( workingSets ) )
                    {
                        PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(newProject, workingSets);
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

            if( messageType == IMessageProvider.ERROR && ! CoreUtil.isNullOrEmpty( message ) )
            {
                wizardPage.setMessage( "Please enter a jsf project name.", SapphireWizardPage.NONE ); //$NON-NLS-1$
                firstErrorMessageRemoved = true;
            }
        }

        return wizardPages;
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        if( selection != null )
        {
            Object element = selection.getFirstElement();

            if( element instanceof IResource )
            {
                IResource resource = (IResource) element;

                final IPath location = resource.getProject().getLocation();

                if( location != null )
                {
                    element().setInitialSelectionPath( PathBridge.create( location ) );
                }
            }
        }
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

        final List<IProject> projects = new ArrayList<IProject>();

        final NewLiferayJSFModuleProjectOp op = element().nearest( NewLiferayJSFModuleProjectOp.class );

        ElementList<ProjectName> projectNames = op.getProjectNames();

        for( ProjectName projectName : projectNames )
        {
            final IProject newProject = CoreUtil.getProject( projectName.getName().content() );

            if( newProject != null )
            {
                projects.add( newProject );
            }
        }

        for( final IProject project : projects )
        {
            try
            {
                addToWorkingSets( project );

            }
            catch( Exception ex )
            {
                ProjectUI.logError( "Unable to add project to working set", ex );
            }
        }

        if ( projects.size() > 0 )
        {
            final IProject finalProject = projects.get(0);

            openLiferayPerspective( finalProject );
        }
     }

    private static NewLiferayJSFModuleProjectOp createDefaultOp()
    {
        return NewLiferayJSFModuleProjectOp.TYPE.instantiate();
    }

}
