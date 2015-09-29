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
package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.ui.IvyUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.ui.LiferayPerspectiveFactory;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ElementList;
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
 * @author Simon Jiang
 * @author Eric Min
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

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    private void openLiferayPerspective( IProject newProject )
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        // open the "final" perspective
        final IConfigurationElement element = new DelegateConfigurationElement( null )
        {
            @Override
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

        final List<IProject> projects = new ArrayList<IProject>();

        final NewLiferayPluginProjectOp op = element().nearest( NewLiferayPluginProjectOp.class );

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

        final IProject finalProject = projects.get(0);

        openLiferayPerspective( finalProject );

        showInAntView( finalProject );

        checkAndConfigureIvy( finalProject );

        // check if a new portlet wizard is needed, available for portlet projects.
        final boolean createNewPortlet = op.getCreateNewPortlet().content();

        if( createNewPortlet && PluginType.portlet.equals( op.getPluginType().content() ) )
        {
            final IPortletFramework portletFramework = op.getPortletFramework().content();
            String wizardId = null;

            if( ("mvc").equals( portletFramework.getShortName() ) )
            {
                wizardId = "com.liferay.ide.portlet.ui.newPortletWizard";
            }
            else if( ("jsf-2.x").equals( portletFramework.getShortName() ) )
            {
                wizardId = "com.liferay.ide.portlet.ui.newJSFPortletWizard";
            }
            else if( ("vaadin").equals( portletFramework.getShortName() ) )
            {
                wizardId = "com.liferay.ide.portlet.vaadin.ui.newVaadinPortletWizard";
            }

            if( wizardId != null )
            {
                openNewPortletWizard( wizardId, finalProject );
            }
        }
    }

    public static void checkAndConfigureIvy( final IProject project )
    {
        if( project != null && project.getFile( ISDKConstants.IVY_XML_FILE ).exists() )
        {
            new WorkspaceJob( "Configuring project with Ivy dependencies" ) //$NON-NLS-1$
            {
                @Override
                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                {
                    try
                    {
                        IvyUtil.configureIvyProject( project, monitor );
                    }
                    catch( CoreException e )
                    {
                        return ProjectCore.createErrorStatus(
                            ProjectCore.PLUGIN_ID, "Failed to configured ivy project.", e ); //$NON-NLS-1$
                    }

                    return Status.OK_STATUS;
                }
            }.schedule();
        }
    }

    private void openNewPortletWizard( String wizardId, final IProject project )
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();

        final IExtension extension = registry.getExtension( wizardId );

        final IConfigurationElement[] elements = extension.getConfigurationElements();

        for( final IConfigurationElement element : elements )
        {
            if( "wizard".equals( element.getName() ) )
            {
                UIUtil.async( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            final INewWizard wizard = (INewWizard) CoreUtility.createExtension( element, "class" );

                            final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

                            wizard.init( PlatformUI.getWorkbench(), new StructuredSelection( project ) );

                            WizardDialog dialog = new WizardDialog( shell, wizard );

                            dialog.create();

                            dialog.open();
                        }
                        catch( CoreException ex )
                        {
                            ProjectCore.createErrorStatus( ex );
                        }
                    }
                });
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

                @Override
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
