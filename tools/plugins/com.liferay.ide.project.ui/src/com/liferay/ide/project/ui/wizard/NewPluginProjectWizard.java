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

package com.liferay.ide.project.ui.wizard;

import static org.eclipse.jst.j2ee.classpathdep.ClasspathDependencyUtil.getDefaultRuntimePath;
import static org.eclipse.jst.j2ee.classpathdep.ClasspathDependencyUtil.modifyDependencyPath;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.core.facet.PluginFacetProjectCreationDataModelProvider;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.WebXMLDescriptorHelper;
import com.liferay.ide.project.ui.AbstractPortletFrameworkDelegate;
import com.liferay.ide.project.ui.IPortletFrameworkDelegate;
import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.LiferayPerspectiveFactory;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ivyde.eclipse.cp.ClasspathSetup;
import org.apache.ivyde.eclipse.cp.IvyClasspathContainer;
import org.apache.ivyde.eclipse.cp.IvyClasspathContainerConfiguration;
import org.apache.ivyde.eclipse.cp.IvyClasspathContainerHelper;
import org.apache.ivyde.eclipse.cp.SettingsSetup;
import org.eclipse.ant.internal.ui.model.AntProjectNode;
import org.eclipse.ant.internal.ui.model.AntProjectNodeProxy;
import org.eclipse.ant.internal.ui.views.AntView;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.internal.BridgedRuntime;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class NewPluginProjectWizard extends NewProjectDataModelFacetWizard
    implements INewWizard, IPluginProjectDataModelProperties
{
    protected NewPluginProjectFirstPage firstPage;
    protected ImageDescriptor liferayWizardImageDescriptor;
    protected IPortletFrameworkDelegate[] portletFrameworkDelegates;
    protected NewPortletPluginProjectPage portletPluginPage;

    protected NewThemePluginProjectPage themePluginPage;

    public NewPluginProjectWizard()
    {
        super();

        setupWizard();
    }

    public NewPluginProjectWizard( IDataModel model )
    {
        super( model );

        setupWizard();
    }

    private IvyClasspathContainer addIvyLibrary( IProject project, IProgressMonitor monitor )
    {
        final String projectName = project.getName();
        final IJavaProject javaProject = JavaCore.create( project );

        final IvyClasspathContainerConfiguration conf =
            new IvyClasspathContainerConfiguration( javaProject, ISDKConstants.IVY_XML_FILE, true );
        final ClasspathSetup classpathSetup = new ClasspathSetup();

        conf.setAdvancedProjectSpecific( false );
        conf.setClasspathSetup( classpathSetup );
        conf.setClassthProjectSpecific( false );
        conf.setConfs( Collections.singletonList( "*" ) ); //$NON-NLS-1$
        conf.setMappingProjectSpecific( false );
        conf.setSettingsProjectSpecific( true );

        SDK sdk = SDKUtil.getSDK( project );
        final SettingsSetup settingsSetup = new SettingsSetup();

        if( sdk.getLocation().append( ISDKConstants.IVY_SETTINGS_XML_FILE ).toFile().exists() )
        {
            StringBuilder builder = new StringBuilder();
            builder.append( "${" ); //$NON-NLS-1$
            builder.append( ISDKConstants.VAR_NAME_LIFERAY_SDK_DIR );
            builder.append( ":" ); //$NON-NLS-1$
            builder.append( projectName );
            builder.append( "}/" ); //$NON-NLS-1$
            builder.append( ISDKConstants.IVY_SETTINGS_XML_FILE );
            settingsSetup.setIvySettingsPath( builder.toString() );
        }

        StringBuilder builder = new StringBuilder();
        builder.append( "${" ); //$NON-NLS-1$
        builder.append( ISDKConstants.VAR_NAME_LIFERAY_SDK_DIR );
        builder.append( ":" ); //$NON-NLS-1$
        builder.append( projectName );
        builder.append( "}/.ivy" ); //$NON-NLS-1$

        settingsSetup.setIvyUserDir( builder.toString() );
        conf.setIvySettingsSetup( settingsSetup );

        final IPath path = conf.getPath();
        final IClasspathAttribute[] atts = conf.getAttributes();

        final IClasspathEntry ivyEntry = JavaCore.newContainerEntry(path, null, atts, false);

        final IVirtualComponent virtualComponent = ComponentCore.createComponent( project );

        try
        {
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>( Arrays.asList( entries ) );

            IPath runtimePath = getDefaultRuntimePath( virtualComponent, ivyEntry);

            // add the deployment assembly config to deploy ivy container to /WEB-INF/lib
            final IClasspathEntry cpeTagged = modifyDependencyPath( ivyEntry, runtimePath );

            newEntries.add( cpeTagged );
            entries = (IClasspathEntry[]) newEntries.toArray( new IClasspathEntry[newEntries.size()] );
            javaProject.setRawClasspath( entries, javaProject.getOutputLocation(), monitor );

            IvyClasspathContainer ivycp = IvyClasspathContainerHelper.getContainer( path, javaProject );

            return ivycp;
        }
        catch( JavaModelException e )
        {
            ProjectUIPlugin.logError( "Unable to add Ivy library container", e ); //$NON-NLS-1$
        }

        return null;
    }

    private void addIvyNature( IProject project, IProgressMonitor monitor ) throws CoreException
    {
        CoreUtil.addNaturesToProject( project, new String[] { "org.apache.ivyde.eclipse.ivynature" }, monitor ); //$NON-NLS-1$
    }

    @Override
    public boolean canFinish()
    {
        if( getContainer().getCurrentPage().equals( portletPluginPage ) && isPluginWizardFragmentEnabled() )
        {
            return false;
        }

        return super.canFinish();
    }

    private IStatus configureIvyProject( final IProject project, IProgressMonitor monitor ) throws CoreException
    {
        SDK sdk = SDKUtil.getSDK( project );

        // check for 6.1.2 and greater but not 6.1.10 which is older EE release
        // and match 6.2.0 and greater
        final Version version = new Version( sdk.getVersion() );

        if( ( CoreUtil.compareVersions( version, ILiferayConstants.V611 ) >= 0 &&
                        CoreUtil.compareVersions( version, ILiferayConstants.V6110 ) < 0 ) ||
                        CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 )
        {
            IFile ivyXmlFile = project.getFile( ISDKConstants.IVY_XML_FILE );

            if( ivyXmlFile.exists() )
            {
                // IDE-1044
                addIvyNature( project, monitor );

                IvyClasspathContainer ivycp = addIvyLibrary( project, monitor );

                if( ivycp != null )
                {
                    IStatus status = ivycp.launchResolve( false, monitor );

                    if( status.isOK() )
                    {
                        final IFolder webinfFolder = CoreUtil.getDefaultDocrootFolder( project ).getFolder( "WEB-INF" ); //$NON-NLS-1$

                        CoreUtil.validateFolder( webinfFolder, monitor );
                    }
                    else
                    {
                        return status;
                    }
                }
            }
        }

        return Status.OK_STATUS;
    }

    @Override
    protected IWizardPage[] createBeginingPages()
    {
        this.firstPage = createFirstPage();
        this.portletPluginPage = new NewPortletPluginProjectPage( this, model );
        this.themePluginPage = new NewThemePluginProjectPage( this, model );

        return new IWizardPage[] { firstPage, portletPluginPage };
    }

    protected IDataModel createDataModel()
    {
        try
        {
            return DataModelFactory.createDataModel( new PluginFacetProjectCreationDataModelProvider() );
        }
        catch( Exception e )
        {
            ProjectUIPlugin.logError( e );

            return null;
        }
    }

    @Override
    protected NewPluginProjectFirstPage createFirstPage()
    {
        return new NewPluginProjectFirstPage( this, model, "first.page" ); //$NON-NLS-1$
    }

    @Override
    protected ImageDescriptor getDefaultPageImageDescriptor()
    {
        if( liferayWizardImageDescriptor == null )
        {
            liferayWizardImageDescriptor =
                ImageDescriptor.createFromURL( ProjectUIPlugin.getDefault().getBundle().getEntry(
                    "/icons/wizban/plugin_project.png" ) ); //$NON-NLS-1$
        }

        return liferayWizardImageDescriptor;
    }

    protected String getFinalPerspectiveID()
    {
        return LiferayPerspectiveFactory.ID;
    }

    protected IDataModel getNestedModel()
    {
        return getDataModel().getNestedModel( NESTED_PROJECT_DM );
    }

    @Override
    public IWizardPage getNextPage( IWizardPage page )
    {
        if( this.portletPluginPage.equals( page ) )
        {
            if( isPluginWizardFragmentEnabled() )
            {
                IPortletFrameworkWizardProvider selectedFramework =
                    this.portletPluginPage.getSelectedPortletFramework();
                IPortletFrameworkDelegate delegate = getPortletFrameworkDelegate( selectedFramework.getId() );

                IPluginWizardFragment pluginFragment = delegate.getWizardFragment();
                IDataModel dm = DataModelFactory.createDataModel( pluginFragment.getDataModelProvider() );

                getDataModel().addNestedModel( PLUGIN_FRAGMENT_DM, dm );
                pluginFragment.setDataModel( dm );
                pluginFragment.initFragmentDataModel( getDataModel(), getProjectName() );
                pluginFragment.addPages();
                pluginFragment.setHostPage( this.portletPluginPage );
                return pluginFragment.getNextPage( page );
            }
            else
            {
                return null;
            }
        }

        if( this.firstPage.equals( page ) && model.getBooleanProperty( PLUGIN_TYPE_THEME ) )
        {
            return this.themePluginPage;
        }

        return super.getNextPage( page );
    }

    protected String getPluginFacetId()
    {
        IDataModel dm = getDataModel();

        if( dm.getBooleanProperty( PLUGIN_TYPE_PORTLET ) )
        {
            return IPluginFacetConstants.LIFERAY_PORTLET_FACET_ID;
        }
        else if( dm.getBooleanProperty( PLUGIN_TYPE_HOOK ) )
        {
            return IPluginFacetConstants.LIFERAY_HOOK_FACET_ID;
        }
        else if( dm.getBooleanProperty( PLUGIN_TYPE_EXT ) )
        {
            return IPluginFacetConstants.LIFERAY_EXT_FACET_ID;
        }
        else if( dm.getBooleanProperty( PLUGIN_TYPE_LAYOUTTPL ) )
        {
            return IPluginFacetConstants.LIFERAY_LAYOUTTPL_FACET_ID;
        }
        else if( dm.getBooleanProperty( PLUGIN_TYPE_THEME ) )
        {
            return IPluginFacetConstants.LIFERAY_THEME_FACET_ID;
        }
        else
        {
            return null;
        }
    }

    public IPortletFrameworkDelegate getPortletFrameworkDelegate( String frameworkId )
    {
        IPortletFrameworkDelegate[] delegates = getPortletFrameworkDelegates();

        if( CoreUtil.isNullOrEmpty( frameworkId ) || CoreUtil.isNullOrEmpty( delegates ) )
        {
            return null;
        }

        for( IPortletFrameworkDelegate delegate : delegates )
        {
            if( frameworkId.equals( delegate.getFrameworkId() ) )
            {
                return delegate;
            }
        }

        return null;
    }

    public IPortletFrameworkDelegate[] getPortletFrameworkDelegates()
    {
        if( portletFrameworkDelegates == null )
        {
            IConfigurationElement[] elements =
                Platform.getExtensionRegistry().getConfigurationElementsFor( IPortletFrameworkDelegate.EXTENSION_ID );

            if( !CoreUtil.isNullOrEmpty( elements ) )
            {
                List<IPortletFrameworkDelegate> delegates = new ArrayList<IPortletFrameworkDelegate>();

                for( IConfigurationElement element : elements )
                {
                    String frameworkId = element.getAttribute( IPortletFrameworkDelegate.FRAMEWORK_ID );
                    String iconUrl = element.getAttribute( IPortletFrameworkDelegate.ICON );

                    try
                    {
                        AbstractPortletFrameworkDelegate delegate =
                            (AbstractPortletFrameworkDelegate) element.createExecutableExtension( "class" ); //$NON-NLS-1$
                        delegate.setFrameworkId( frameworkId );
                        delegate.setIconUrl( iconUrl );
                        delegate.setBundleId( element.getContributor().getName() );

                        delegates.add( delegate );
                    }
                    catch( CoreException e )
                    {
                        ProjectUIPlugin.logError( "Could not create portlet plugin template delegate.", e ); //$NON-NLS-1$
                    }

                }

                portletFrameworkDelegates = delegates.toArray( new IPortletFrameworkDelegate[0] );
            }
        }

        return portletFrameworkDelegates;
    }

    protected String getProjectSuffix()
    {
        if( getDataModel().getBooleanProperty( PLUGIN_TYPE_PORTLET ) )
        {
            return ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
        }
        else if( getDataModel().getBooleanProperty( PLUGIN_TYPE_HOOK ) )
        {
            return ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
        }
        else if( getDataModel().getBooleanProperty( PLUGIN_TYPE_EXT ) )
        {
            return ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
        }
        else if( getDataModel().getBooleanProperty( PLUGIN_TYPE_THEME ) )
        {
            return ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;
        }
        else if( getDataModel().getBooleanProperty( PLUGIN_TYPE_LAYOUTTPL ) )
        {
            return ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
        }

        return null;
    }

    @Override
    protected IFacetedProjectTemplate getTemplate()
    {
        return ProjectFacetsManager.getTemplate( IPluginFacetConstants.LIFERAY_DEFAULT_FACET_TEMPLATE );
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        super.init( workbench, selection );

        getDataModel().setBooleanProperty( PLUGIN_TYPE_PORTLET, true );
    }

    protected boolean isPluginWizardFragmentEnabled()
    {
        IPortletFrameworkWizardProvider portletFramework = this.portletPluginPage.getSelectedPortletFramework();

        if( portletFramework != null )
        {
            IPortletFrameworkDelegate delegate = getPortletFrameworkDelegate( portletFramework.getId() );

            if( delegate != null && delegate.getWizardFragment() != null && delegate.isFragmentEnabled() )
            {

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean performFinish()
    {
        // String projectName = getFacetedProjectWorkingCopy().getProjectName();
        // getFacetedProjectWorkingCopy().setProjectName(projectName +
        // getProjectSuffix());
        // String projectName =
        // getDataModel().getStringProperty(FACET_PROJECT_NAME);
        // getDataModel().setStringProperty(FACET_PROJECT_NAME, projectName +
        // getProjectSuffix());

        firstPage.setShouldValidatePage( false );

        // model.setProperty(SETUP_PROJECT_FLAG, "");

        final IWorkspaceRunnable wr = new IWorkspaceRunnable()
        {
            public void run( final IProgressMonitor monitor ) throws CoreException
            {
                performSDKInstall( monitor );
            }
        };

        final IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
            {
                try
                {
                    final IWorkspace ws = ResourcesPlugin.getWorkspace();
                    ws.run( wr, ws.getRoot(), IWorkspace.AVOID_UPDATE, monitor );
                }
                catch( CoreException e )
                {
                    throw new InvocationTargetException( e );
                }
            }
        };

        try
        {
            getContainer().run( true, true, op );
        }
        catch( InvocationTargetException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch( InterruptedException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return super.performFinish();
    }

    protected void performSDKInstall(IProgressMonitor monitor)
    {
        String projectName = getNestedModel().getStringProperty( PROJECT_NAME );

        if( !projectName.endsWith( getProjectSuffix() ) )
        {
            getNestedModel().setStringProperty( PROJECT_NAME, projectName + getProjectSuffix() );
        }

        model.setProperty( PORTLET_NAME, projectName );
        model.setProperty( THEME_NAME, projectName );
        model.setProperty( LAYOUTTPL_NAME, projectName );

        // IDE-1122
        /*
         * Let the progress monitor display message about ivy downloading and resolving artifacts
         * (particularly the first time plugins-sdk is used it can take several minutes ),
         * in case users mistakenly think the IDE is blocked.
         * SDK creating projects is moved here from method execute() of different types of PluginFacetInstall.
         */
        monitor.beginTask( "", 10 ); //$NON-NLS-1$

        SDK sdk = SDKManager.getInstance().getSDK( model.getStringProperty( LIFERAY_SDK_NAME ) );

        ILiferayRuntime liferayRuntime =
            ServerUtil.getLiferayRuntime( (BridgedRuntime) model.getProperty( FACET_RUNTIME ));

        Map<String, String> appServerProperties = ServerUtil.configureAppServerProperties( liferayRuntime );

        String displayName = this.model.getStringProperty( DISPLAY_NAME );

        monitor.worked( 1 );

        if( model.getBooleanProperty( PLUGIN_TYPE_PORTLET ) )
        {
            String portletName = model.getStringProperty( PORTLET_NAME );

            if( portletName.endsWith( ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX ) )
            {
                portletName =
                    portletName.substring( 0, portletName.indexOf( ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX ) );

                model.setProperty( PORTLET_NAME, portletName );
            }

            String portletFrameworkId = this.model.getStringProperty( PORTLET_FRAMEWORK_ID );

            IPortletFrameworkWizardProvider portletFramework =
                LiferayProjectCore.getPortletFramework( portletFrameworkId );

            String frameworkName = portletFramework.getShortName();

            monitor.subTask( NLS.bind( Msgs.creatingNewProject, "portlet" ) ); //$NON-NLS-1$

            IPath portletProjectTempPath =
                sdk.createNewPortletProject( portletName, displayName, frameworkName, appServerProperties, monitor );

            model.setProperty( PROJECT_TEMP_PATH, portletProjectTempPath );
        }
        else if( model.getBooleanProperty( PLUGIN_TYPE_HOOK ) )
        {
            String hookName = this.model.getStringProperty( HOOK_NAME );

            if( hookName.endsWith( ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX ) )
            {
                hookName = hookName.substring( 0, hookName.indexOf( ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX ) );

                model.setProperty( HOOK_NAME, hookName );
            }

            monitor.subTask( NLS.bind( Msgs.creatingNewProject, "hook" ) ); //$NON-NLS-1$

            IPath hookProjectTempPath = sdk.createNewHookProject( hookName, displayName, monitor );

            model.setProperty( PROJECT_TEMP_PATH, hookProjectTempPath );
        }
        else if( model.getBooleanProperty( PLUGIN_TYPE_EXT ) )
        {
            String extName = this.model.getStringProperty( EXT_NAME );

            if( extName.endsWith( ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX ) )
            {
                extName = extName.substring( 0, extName.indexOf( ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX ) );

                model.setProperty( EXT_NAME, extName );
            }

            monitor.subTask( NLS.bind( Msgs.creatingNewProject, "ext" ) ); //$NON-NLS-1$

            IPath extProjectTempPath = sdk.createNewExtProject( extName, displayName, appServerProperties, monitor );

            model.setProperty( PROJECT_TEMP_PATH, extProjectTempPath );
        }
        else if( model.getBooleanProperty( PLUGIN_TYPE_THEME ) )
        {
            String themeName = this.model.getStringProperty( THEME_NAME );

            if( themeName.endsWith( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) )
            {
                themeName = themeName.substring( 0, themeName.indexOf( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) );

                model.setProperty( THEME_NAME, themeName );
            }

            monitor.subTask( NLS.bind( Msgs.creatingNewProject, "theme" ) ); //$NON-NLS-1$

            IPath themeProjectTempPath = sdk.createNewThemeProject( themeName, displayName, monitor );

            model.setProperty( PROJECT_TEMP_PATH, themeProjectTempPath );
        }
        else if( model.getBooleanProperty( PLUGIN_TYPE_LAYOUTTPL ) )
        {
            String layoutTplName = model.getStringProperty( LAYOUTTPL_NAME );

            if( layoutTplName.endsWith( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX ) )
            {
                layoutTplName =
                    layoutTplName.substring( 0, layoutTplName.indexOf( ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX ) );

                model.setProperty( LAYOUTTPL_NAME, layoutTplName );
            }

            monitor.subTask( NLS.bind( Msgs.creatingNewProject, "layout" ) ); //$NON-NLS-1$

            IPath layoutTplPorjectTempPath =
                sdk.createNewLayoutTplProject( layoutTplName, displayName, appServerProperties, monitor );

            model.setProperty( PROJECT_TEMP_PATH, layoutTplPorjectTempPath );
        }
    }

    @Override
    protected void postPerformFinish() throws InvocationTargetException
    {
        postPerformFinish( isPluginWizardFragmentEnabled() );
    }

    protected void postPerformFinish(final boolean pluginFragmentEnabled ) throws InvocationTargetException
    {
        try
        {
            PlatformUI.getWorkbench().getProgressService().run
            (
                true, false,
                new IRunnableWithProgress()
                {
                    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                    {
                        try
                        {
                            postProjectCreated( monitor, pluginFragmentEnabled );

                            UIUtil.sync
                            (
                                new Runnable()
                                {
                                    public void run()
                                    {
                                        try
                                        {
                                            NewPluginProjectWizard.super.postPerformFinish();
                                        }
                                        catch( InvocationTargetException e )
                                        {
                                            ProjectUIPlugin.logError( e );
                                        }
                                    }
                                }
                            );
                        }
                        catch( ExecutionException e )
                        {
                            e.printStackTrace();
                        }
                    }
                }
            );
        }
        catch( InterruptedException e1 )
        {
            throw new InvocationTargetException( e1 );
        }
    }

    protected void postProjectCreated( IProgressMonitor monitor, boolean pluginFragmentEnabled ) throws ExecutionException
    {
        if( getDataModel().getBooleanProperty( PLUGIN_TYPE_PORTLET ) )
        {
            String portletFrameworkId = getDataModel().getStringProperty( PORTLET_FRAMEWORK_ID );

            IPortletFrameworkWizardProvider portletFramework =
                LiferayProjectCore.getPortletFramework( portletFrameworkId );

            portletFramework.postProjectCreated( getDataModel(), getFacetedProject(), monitor );
        }
        else if( getDataModel().getBooleanProperty( PLUGIN_TYPE_THEME ) )
        {
            try
            {
                Map<String, String> args = new HashMap<String, String>();

                args.put( "force", "true" ); //$NON-NLS-1$ //$NON-NLS-2$

                getFacetedProject().getProject().build(
                    IncrementalProjectBuilder.FULL_BUILD, "com.liferay.ide.eclipse.theme.core.cssBuilder", args, null ); //$NON-NLS-1$
            }
            catch( CoreException e )
            {
                ProjectUIPlugin.logError( e );
            }
        }

        // if we have a wizard fragment execute its operation after project is created
        if( pluginFragmentEnabled )
        {
            final IDataModel fragmentModel = getDataModel().getNestedModel( PLUGIN_FRAGMENT_DM );
            fragmentModel.setStringProperty( IArtifactEditOperationDataModelProperties.PROJECT_NAME, getProjectName() );

            fragmentModel.getDefaultOperation().execute( monitor, null );
        }

        final IProject project = getFacetedProject().getProject();

        if( project != null && ProjectUtil.isPortletProject( project ) )
        {
            WebXMLDescriptorHelper webXmlHelper = new WebXMLDescriptorHelper( project );

            try
            {
                webXmlHelper.deleteWelcomeFileListElements();
            }
            catch( Exception e )
            {
                LiferayProjectCore.logError( "Failed to delete welcome file list elements", e ); //$NON-NLS-1$
            }
        }

        if( project != null && project.getFile( ISDKConstants.IVY_XML_FILE ).exists() )
        {
            try
            {
                configureIvyProject( project, monitor );
            }
            catch( CoreException e )
            {
                LiferayProjectCore.logError( "Failed to configured ivy project.", e ); //$NON-NLS-1$
            }
        }

        Display.getDefault().asyncExec( new Runnable()
        {
            private void addBuildInAntView()
            {
                IProject project = getFacetedProject().getProject();

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
        } );
    }

    protected void setupWizard()
    {
        setWindowTitle( Msgs.newLiferayProject );
        setShowFacetsSelectionPage( false );
        setNeedsProgressMonitor( true );
    }

    private static class Msgs extends NLS
    {
        public static String newLiferayProject;
        public static String creatingNewProject;

        static
        {
            initializeMessages( NewPluginProjectWizard.class.getName(), Msgs.class );
        }
    }
}
