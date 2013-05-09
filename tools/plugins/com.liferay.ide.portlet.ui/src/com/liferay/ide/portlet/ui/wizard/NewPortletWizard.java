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

package com.liferay.ide.portlet.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.portlet.core.operation.NewPortletClassDataModelProvider;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.portlet.ui.template.PortletTemplateContextTypeIds;
import com.liferay.ide.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.project.ui.wizard.IPluginWizardFragment;
import com.liferay.ide.project.ui.wizard.ValidProjectChecker;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringExecutionStarter;
import org.eclipse.jdt.internal.ui.fix.ImportsCleanUp;
import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.ICleanUp;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.servlet.ui.internal.wizard.NewWebArtifactWizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class NewPortletWizard extends NewWebArtifactWizard
    implements IPluginWizardFragment, INewPortletClassDataModelProperties
{

    public static final String ID = "com.liferay.ide.eclipse.portlet.ui.wizard.portlet"; //$NON-NLS-1$

    protected boolean fragment;
    protected IWizardPage hostPage;
    protected IProject initialProject;

    public NewPortletWizard()
    {
        this( (IDataModel) null );
    }

    public NewPortletWizard( IDataModel model )
    {
        super( model );
        setDefaultPageImageDescriptor( getImage() );
    }

    public NewPortletWizard( IProject project )
    {
        this( (IDataModel) null );

        this.initialProject = project;
    }

    @Override
    protected void doAddPages()
    {
        addPage( new NewPortletClassWizardPage(
            getDataModel(), "pageOne", Msgs.createPortletClass, getDefaultPageTitle(), this.fragment, //$NON-NLS-1$
            ( initialProject != null ) ) );
        addPage( new NewPortletOptionsWizardPage(
            getDataModel(), "pageTwo", Msgs.specifyPortletDeployment, getDefaultPageTitle(), //$NON-NLS-1$
            this.fragment ) );
        addPage( new NewLiferayPortletWizardPage(
            getDataModel(), "pageThree", Msgs.specifyLiferayPortletDeployment, //$NON-NLS-1$
            getDefaultPageTitle(), this.fragment ) );
        addPage( new NewPortletClassOptionsWizardPage(
            getDataModel(), "pageFour", //$NON-NLS-1$
            Msgs.specifyModifiersInterfacesMethodStubs, getDefaultPageTitle(),
            this.fragment ) );
    }

    public IDataModelProvider getDataModelProvider()
    {
        return getDefaultProvider();
    }

    protected String getDefaultPageTitle()
    {
        return Msgs.createLiferayPortlet;
    }

    @Override
    protected IDataModelProvider getDefaultProvider()
    {
        final TemplateStore templateStore = PortletUIPlugin.getDefault().getTemplateStore();

        final TemplateContextType contextType =
            PortletUIPlugin.getDefault().getTemplateContextRegistry().getContextType( PortletTemplateContextTypeIds.NEW );

        return new NewPortletClassDataModelProvider( this.fragment, initialProject )
        {
            @Override
            public IDataModelOperation getDefaultOperation()
            {
                return new AddPortletOperation( this.model, templateStore, contextType );
            }
        };
    }

    protected ImageDescriptor getImage()
    {
        return PortletUIPlugin.imageDescriptorFromPlugin( PortletUIPlugin.PLUGIN_ID, "/icons/wizban/portlet_wiz.png" ); //$NON-NLS-1$
    }

    public String getTitle()
    {
        return Msgs.newLiferayPortlet;
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        super.init( workbench, selection );
        ValidProjectChecker checker = new ValidProjectChecker( ID );
        checker.checkValidProjectTypes();
    }

    public void initFragmentDataModel( IDataModel parentDataModel, String projectName )
    {
        getDataModel().setBooleanProperty( IPluginWizardFragmentProperties.REMOVE_EXISTING_ARTIFACTS, true );
        getDataModel().setProperty(
            IPluginWizardFragmentProperties.FACET_RUNTIME,
            parentDataModel.getProperty( IFacetProjectCreationDataModelProperties.FACET_RUNTIME ) );
        getDataModel().setStringProperty( IArtifactEditOperationDataModelProperties.PROJECT_NAME, projectName );
    }

    @Override
    protected void openJavaClass()
    {
        IProject project =
            ResourcesPlugin.getWorkspace().getRoot().getProject( getDataModel().getStringProperty( PROJECT_NAME ) );

        if( getDataModel().getBooleanProperty( USE_DEFAULT_PORTLET_CLASS ) )
        {
            try
            {
                String jspsFolder = getDataModel().getStringProperty( CREATE_JSPS_FOLDER );

                // IDE-110 IDE-648
                IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

                if( webappRoot != null )
                {
                    for( IContainer container : webappRoot.getUnderlyingFolders() )
                    {
                        IFile viewFile = container.getFile( new Path( jspsFolder + "/view.jsp" ) ); //$NON-NLS-1$

                        if( viewFile.exists() )
                        {
                            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                            IDE.openEditor( page, viewFile, true );

                            return;
                        }
                    }
                }
            }
            catch( Exception e )
            {
                // eat this exception this is just best effort
            }
        }
        else
        {
            Map<String, String> settings = new Hashtable<String, String>();
            settings.put( CleanUpConstants.ORGANIZE_IMPORTS, CleanUpOptions.TRUE );
            ImportsCleanUp importsCleanUp = new ImportsCleanUp( settings );

            ICleanUp[] cleanUps = new ICleanUp[] { importsCleanUp };

            IJavaProject javaProject = JavaCore.create( project );

            try
            {
                IType type =
                    javaProject.findType( getDataModel().getStringProperty(
                        INewJavaClassDataModelProperties.QUALIFIED_CLASS_NAME ) );

                ICompilationUnit cu = (ICompilationUnit) type.getParent();

                ICompilationUnit[] units = new ICompilationUnit[] { cu };

                RefactoringExecutionStarter.startCleanupRefactoring(
                    units, cleanUps, false, getShell(), false, "organize imports" ); //$NON-NLS-1$
            }
            catch( Exception e )
            {
            }

            super.openJavaClass();
        }
    }

    @Override
    protected void postPerformFinish() throws InvocationTargetException
    {
        openJavaClass();
    }

    @Override
    protected boolean prePerformFinish()
    {
        if( this.fragment )
        {
            // if this is added to plugin wizard as fragment we don't want this to execute performFinish
            return false;
        }
        else
        {
            return true;
        }
    }

    public void setFragment( boolean fragment )
    {
        this.fragment = fragment;
    }

    public void setHostPage( IWizardPage hostPage )
    {
        this.hostPage = hostPage;
    }

    private static class Msgs extends NLS
    {
        public static String createLiferayPortlet;
        public static String createPortletClass;
        public static String newLiferayPortlet;
        public static String specifyLiferayPortletDeployment;
        public static String specifyModifiersInterfacesMethodStubs;
        public static String specifyPortletDeployment;

        static
        {
            initializeMessages( NewPortletWizard.class.getName(), Msgs.class );
        }
    }
}
