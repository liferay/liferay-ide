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

package com.liferay.ide.project.ui.workspace;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.core.commands.ExpressionContext;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.wst.web.internal.DelegateConfigurationElement;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.workspace.NewLiferayWorkspaceOp;
import com.liferay.ide.project.core.workspace.WorkspaceNameValidationService;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.wizard.WorkingSetCustomPart;
import com.liferay.ide.ui.LiferayWorkspacePerspectiveFactory;

/**
 * @author Andy Wu
 */
@SuppressWarnings( "restriction" )
public class NewLiferayWorkspaceWizard extends SapphireWizard<NewLiferayWorkspaceOp>
    implements IWorkbenchWizard, INewWizard
{

    private boolean firstErrorMessageRemoved = false;

    public NewLiferayWorkspaceWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewLiferayWorkspaceWizard.class ).wizard() );
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
                try
                {
                    if( LiferayWorkspaceUtil.hasLiferayWorkspace() )
                    {
                        wizardPage.setMessage(
                            WorkspaceNameValidationService.hasLiferayWorkspaceMsg, SapphireWizardPage.ERROR );
                    }
                    else
                    {
                        wizardPage.setMessage( "Please enter the workspace name.", SapphireWizardPage.NONE );
                    }
                }
                catch( CoreException e )
                {
                    wizardPage.setMessage( LiferayWorkspaceUtil.multiWorkspaceError, SapphireWizardPage.ERROR );
                }

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

        final NewLiferayWorkspaceOp op = element().nearest( NewLiferayWorkspaceOp.class );

        final IProject newProject = CoreUtil.getProject( op.getWorkspaceName().content() );

        try
        {
            addToWorkingSets( newProject );
        }
        catch( Exception ex )
        {
            ProjectUI.logError( "Unable to add project to working set", ex );
        }

        openLiferayPerspective( newProject );

        setProjectExplorerLayoutNestedEnabled();
    }

    private void setProjectExplorerLayoutNestedEnabled() {

        String commandId = "org.eclipse.ui.navigator.resources.nested.changeProjectPresentation";

        try
        {
            final ICommandService commandService =
                (ICommandService) PlatformUI.getWorkbench().getService( ICommandService.class );

            Command command = commandService.getCommand( commandId );

            IHandler hanlder = command.getHandler();

            if( hanlder != null )
            {
                Map<String, String> map = new HashMap<String, String>();

                map.put( "org.eclipse.ui.navigator.resources.nested.enabled", "true" );

                IEclipseContext eclipseContext =
                    (IEclipseContext) PlatformUI.getWorkbench().getService( IEclipseContext.class );

                ExpressionContext expressionContext = new ExpressionContext( eclipseContext );

                EvaluationContext context = new EvaluationContext( expressionContext, new Object() );

                ExecutionEvent event = new ExecutionEvent( command, map, null, context );

                hanlder.execute( event );
            }
            else
            {
                ProjectUI.logInfo( "didn't find " + commandId + " handler" );
            }
        }
        catch( ExecutionException e )
        {
            ProjectUI.logError( "execute " + commandId + " handler error", e );
        }
	}

	private static NewLiferayWorkspaceOp createDefaultOp()
    {
        return NewLiferayWorkspaceOp.TYPE.instantiate();
    }

}
