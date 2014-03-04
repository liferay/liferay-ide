/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.ui.IvyUtil;
import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.ui.LiferayPerspectiveFactory;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.ant.internal.ui.model.AntProjectNode;
import org.eclipse.ant.internal.ui.model.AntProjectNodeProxy;
import org.eclipse.ant.internal.ui.views.AntView;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.eclipse.wst.web.internal.DelegateConfigurationElement;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class NewLiferayPluginProjectWizard extends SapphireWizard<NewLiferayPluginProjectOp>
    implements IWorkbenchWizard, INewWizard
{
    private boolean firstErrorMessageRemoved = false;

    public NewLiferayPluginProjectWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewLiferayPluginProjectWizard.class ).wizard() );
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
                wizardPage.setMessage( "Please enter a project name.", SapphireWizardPage.NONE ); //$NON-NLS-1$
                firstErrorMessageRemoved = true;
            }
        }

        return wizardPages;
    }

    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    private void openLiferayPerspective( IProject newProject )
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        // open the "final" perspective
        final IConfigurationElement element = new DelegateConfigurationElement( null )
        {
            public String getAttribute( String aName )
            {
                if( aName.equals( "finalPerspective" ) )
                {
                    return LiferayPerspectiveFactory.ID;
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

        final NewLiferayPluginProjectOp op = element().nearest( NewLiferayPluginProjectOp.class );
        final IProject project = CoreUtil.getProject( op.getFinalProjectName().content() );

        try
        {
            addToWorkingSets( project );
        }
        catch( Exception ex )
        {
            ProjectUIPlugin.logError( "Unable to add project to working set", ex );
        }

        openLiferayPerspective( project );

        showInAntView( project );

        if( project != null && project.getFile( ISDKConstants.IVY_XML_FILE ).exists() )
        {
            new WorkspaceJob( "Configuring project with Ivy dependencies" ) //$NON-NLS-1$
            {
                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                {
                    try
                    {
                        IvyUtil.configureIvyProject( project, monitor );
                    }
                    catch( CoreException e )
                    {
                        return LiferayProjectCore.createErrorStatus(
                            LiferayProjectCore.PLUGIN_ID, "Failed to configured ivy project.", e ); //$NON-NLS-1$
                    }

                    return Status.OK_STATUS;
                }
            }.schedule();
        }

        // check if a new portlet wizard is needed, available for portlet projects.
        final boolean createNewPortlet = op.getCreateNewPortlet().content();

        if( createNewPortlet && PluginType.portlet.equals( op.getPluginType().content() ) )
        {
            openNewPortletWizard();
        }
    }

    private void openNewPortletWizard()
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();

        final String newPortletWizardExtensionId = "com.liferay.ide.portlet.ui.newPortletWizard";

        final IExtension extension = registry.getExtension( newPortletWizardExtensionId );

        final IConfigurationElement[] elements = extension.getConfigurationElements();

        for( final IConfigurationElement element : elements )
        {
            if( "wizard".equals( element.getName() ) )
            {
                UIUtil.async
                (
                    new Runnable()
                    {
                        public void run()
                        {
                            try
                            {
                                INewWizard wizard = (INewWizard) CoreUtility.createExtension( element, "class" );

                                final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

                                wizard.init( PlatformUI.getWorkbench(), null );

                                WizardDialog dialog = new WizardDialog( shell, wizard );

                                dialog.create();

                                dialog.open();
                            }
                            catch( CoreException ex )
                            {
                                LiferayProjectCore.createErrorStatus( ex );
                            }
                        }
                    }
                );
            }
        }
    }

    private void showInAntView( final IProject project )
    {
        Display.getDefault().asyncExec
        (
            new Runnable()
            {
                private void addBuildInAntView()
                {
                    if( project != null )
                    {
                        IFile buildXmlFile = project.getFile( "build.xml" ); //$NON-NLS-1$

                        if( buildXmlFile.exists() )
                        {
                            String buildFileName = buildXmlFile.getFullPath().toString();
                            final AntProjectNode antProject = new AntProjectNodeProxy( buildFileName );
                            project.getName();

                            IViewPart antView =
                                PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().findView(
                                    "org.eclipse.ant.ui.views.AntView" ); //$NON-NLS-1$

                            if( antView instanceof AntView )
                            {
                                ( (AntView) antView ).addProject( antProject );
                            }
                        }
                    }
                }

                private void refreshProjectExplorer()
                {
                    IViewPart view = null;

                    try
                    {
                        view =
                            PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().findView(
                                IPageLayout.ID_PROJECT_EXPLORER );
                    }
                    catch( Exception e )
                    {
                        // Just bail and return if there is no view
                    }

                    if( view == null )
                    {
                        return;
                    }

                    CommonViewer viewer = (CommonViewer) view.getAdapter( CommonViewer.class );

                    viewer.refresh( true );
                }

                public void run()
                {
                    refreshProjectExplorer();
                    addBuildInAntView();
                }
            }
        );
    }

    private static NewLiferayPluginProjectOp createDefaultOp()
    {
        return NewLiferayPluginProjectOp.TYPE.instantiate();
    }

}
