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

package com.liferay.ide.project.ui.upgrade.animated;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.Version;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.IOUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.IWorkspaceProjectBuilder;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.BladeCLI;
import com.liferay.ide.project.core.modules.BladeCLIException;
import com.liferay.ide.project.core.modules.ImportLiferayModuleProjectOpMethods;
import com.liferay.ide.project.core.util.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.project.ui.IvyUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
@SuppressWarnings( "unused" )
public class InitConfigureProjectPage extends Page implements IServerLifecycleListener, SelectionChangedListener
{

    private class LiferayUpgradeValidationListener extends Listener
    {

        @Override
        public void handle( Event event )
        {
            if( !(event instanceof ValuePropertyContentEvent) )
            {
                return;
            }

            final Property property = ((ValuePropertyContentEvent) event).property();

            if( !(property.name().equals( "SdkLocation" )) )
            {
                startCheckThread();
                return;
            }

            org.eclipse.sapphire.modeling.Path path = dataModel.getSdkLocation().content();

            if( path == null || !sdkValidation.compute().ok() )
            {
                if( !layoutComb.isDisposed() )
                {
                    layoutComb.setEnabled( true );
                }

                startCheckThread();
                return;
            }

            if( isAlreadyImported( PathBridge.create(path) ) )
            {
            		disposeBundleCheckboxElement();
                disposeBundleElement();
                disposeServerEelment();
                disposeMigrateLayoutElement();

                importButton.setText("Continue");
                pageParent.layout();
            }
            else if( isMavenProject( path.toPortableString() ) )
            {
                disposeBundleCheckboxElement();
                disposeBundleElement();
                disposeServerEelment();
                disposeMigrateLayoutElement();
                pageParent.layout();
            }
            else
            {
                disposeMigrateLayoutElement();
                createMigrateLayoutElement();

                createBundleControl();
                pageParent.layout();
                final String version = SDKUtil.createSDKFromLocation( PathBridge.create( path ) ).getVersion();

                if( version != null && new Version( version ).compareTo( new Version( "7.0.0" ) ) >= 0 )
                {
                    UIUtil.async( new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            if( layoutComb.getSelectionIndex() != 0 )
                            {
                                layoutComb.select( 1 );
                            }

                            layoutComb.setEnabled( false );
                            dataModel.setLayout( layoutComb.getText() );
                        }
                    } );
                }
                else
                {
                    layoutComb.setEnabled( true );
                }
            }

            startCheckThread();
        }

    }

	private boolean isAlreadyImported(IPath path) {
		IContainer[] containers =
			ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(path.toFile().toURI());

		return Stream.of(containers).filter(container -> container instanceof IProject).count() > 0;
	}

    private boolean isMavenProject( String path )
    {
        return ImportLiferayModuleProjectOpMethods.getBuildType( path ).getMessage().equals( "maven" );
    }

    private static Color GRAY;
    private Label dirLabel;
    private Text dirField;
    private Combo layoutComb;
    private Label layoutLabel;
    private String[] layoutNames = { "Upgrade to Liferay Workspace", "Upgrade to Liferay Plugins SDK 7" };
    private Label serverLabel;
    private Combo serverComb;
    private Button serverButton;
    private Button showAllPagesButton;
    private Label bundleNameLabel;
    private Label bundleUrlLabel;
    private Text bundleNameField;
    private Text bundleUrlField;
    private boolean validationResult;
    private Button importButton;
    private Button downloadBundleCheckbox;

    private Composite pageParent;
    private Composite blankComposite;

    private Control createHorizontalSpacer;
    private Control createSeparator;

    private ProjectLocationValidationService sdkValidation =
        dataModel.getSdkLocation().service( ProjectLocationValidationService.class );
    private BundleNameValidationService bundleNameValidation =
        dataModel.getBundleName().service( BundleNameValidationService.class );
    private BundleUrlValidationService bundleUrlValidation =
        dataModel.getBundleUrl().service( BundleUrlValidationService.class );

    public InitConfigureProjectPage( final Composite parent, int style, LiferayUpgradeDataModel dataModel )
    {
        super( parent, style, dataModel, INIT_CONFIGURE_PROJECT_PAGE_ID, false );

        dataModel.getSdkLocation().attach( new LiferayUpgradeValidationListener() );
        dataModel.getBundleName().attach( new LiferayUpgradeValidationListener() );
        dataModel.getBundleUrl().attach( new LiferayUpgradeValidationListener() );
        dataModel.getBackupLocation().attach( new LiferayUpgradeValidationListener() );

        ScrolledComposite scrolledComposite = new ScrolledComposite( this, SWT.V_SCROLL );
        GridData scrolledData = new GridData( SWT.FILL, SWT.FILL, true, true );
        scrolledData.widthHint = DEFAULT_PAGE_WIDTH;
        scrolledComposite.setLayoutData( scrolledData );
        pageParent = SWTUtil.createComposite( scrolledComposite, getGridLayoutCount(), 1, GridData.FILL_BOTH );
        scrolledComposite.setMinHeight( 300 );
        scrolledComposite.setExpandHorizontal( true );
        scrolledComposite.setExpandVertical( true );
        scrolledComposite.setContent( pageParent );

        dirLabel = createLabel( pageParent, "Plugins SDK or Maven Project Root Location:" );
        dirField = createTextField( pageParent, SWT.NONE );
        dirField.addModifyListener( new ModifyListener()
        {
            @Override
			public void modifyText( ModifyEvent e )
            {
                dataModel.setSdkLocation( dirField.getText() );

                if ( dirField.getText().isEmpty() )
                {
                    disposeMigrateLayoutElement();

                    disposeBundleCheckboxElement();

                    disposeBundleElement();

                    disposeServerEelment();

                    disposeImportElement();

                    createMigrateLayoutElement();

                    createDownloaBundleCheckboxElement();

                    createBundleElement();

                    createImportElement();

                    pageParent.layout();

                    startCheckThread();
                }
            }
        });

        SWTUtil.createButton( pageParent, "Browse..." ).addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                final DirectoryDialog dd = new DirectoryDialog( getShell() );
                dd.setMessage( "Plugins SDK top-level directory or Maven project root directory" );

                final String selectedDir = dd.open();

                if( selectedDir != null )
                {
                    dirField.setText( selectedDir );
                }
            }
        });

        createMigrateLayoutElement();

        createDownloaBundleCheckboxElement();

        createBundleElement();

        createImportElement();

        startCheckThread();
    }


    private void backup( IProgressMonitor monitor )
    {
        Boolean backup = dataModel.getBackupSdk().content();

        if ( backup == false )
        {
            return;
        }

        SubMonitor progress = SubMonitor.convert( monitor, 100 );
        try
        {
            progress.setTaskName( "Backup origial project folder into Eclipse workspace...");
            org.eclipse.sapphire.modeling.Path originalPath = dataModel.getSdkLocation().content();

            if( originalPath != null )
            {
                IPath backupLocation = PathBridge.create( dataModel.getBackupLocation().content() );

                backupLocation.toFile().mkdirs();

                progress.worked( 30 );

                ZipUtil.zip( originalPath.toFile(), backupLocation.append( "backup.zip" ).toFile() );

                progress.setWorkRemaining( 70 );
            }
        }
        catch( IOException e )
        {
            ProjectUI.logError( "Error to backup original project folder.", e );
        }
        finally
        {
            progress.done();
        }
    }

    public void checkAndConfigureIvy( final IProject project )
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

                    return StatusBridge.create( Status.createOkStatus() );
                }
            }.schedule();
        }
    }

    private void checkProjectType( IProject project )
    {
        if( ProjectUtil.isMavenProject( project ) )
        {
            dataModel.setHasMavenProject( true );
        }

        if( ProjectUtil.isPortletProject( project ) )
        {
            dataModel.setHasPortlet( true );
        }

        if( ProjectUtil.isHookProject( project ) )
        {
            dataModel.setHasHook( true );
        }

        List<IFile> searchFiles = new SearchFilesVisitor().searchFiles( project, "service.xml" );

        if( searchFiles.size() > 0 )
        {
            dataModel.setHasServiceBuilder( true );
        }

        if( ProjectUtil.isLayoutTplProject( project ) )
        {
            dataModel.setHasLayout( true );
        }

        if( ProjectUtil.isThemeProject( project ) )
        {
            dataModel.setHasTheme( true );
        }

        if( ProjectUtil.isExtProject( project ) )
        {
            dataModel.setHasExt( true );
        }

        if( ProjectUtil.isWebProject( project ) )
        {
            dataModel.setHasWeb( true );
        }
    }

    private void clearExistingProjects( IPath location, IProgressMonitor monitor ) throws CoreException
    {
        IProject sdkProject = SDKUtil.getWorkspaceSDKProject();

        if( sdkProject != null && sdkProject.getLocation().equals( location ) )
        {
            IProject[] projects = ProjectUtil.getAllPluginsSDKProjects();

            for( IProject project : projects )
            {
                project.delete( false, true, monitor );
            }

            sdkProject.delete( false, true, monitor );
        }

        IProject[] projects = CoreUtil.getAllProjects();

        for( IProject project : projects )
        {
            if( project.getLocation().toPortableString().startsWith( location.toPortableString() ) )
            {
                project.delete( false, true, monitor );
            }
        }
    }

    private void copyNewSDK( IPath targetSDKLocation, IProgressMonitor monitor ) throws CoreException
    {
        SubMonitor progress = SubMonitor.convert( monitor, 100 );
        try
        {
            progress.beginTask( "Copy new SDK to override target SDK.", 100 );
            final URL sdkZipUrl =
                Platform.getBundle( "com.liferay.ide.project.ui" ).getEntry( "resources/sdk70ga2.zip" );

            final File sdkZipFile = new File( FileLocator.toFileURL( sdkZipUrl ).getFile() );

            final IPath stateLocation = ProjectCore.getDefault().getStateLocation();

            File stateDir = stateLocation.toFile();

            progress.worked( 30 );

            ZipUtil.unzip( sdkZipFile, stateDir );

            progress.worked( 60 );

            IOUtil.copyDirToDir(
                new File( stateDir, "com.liferay.portal.plugins.sdk-7.0" ), targetSDKLocation.toFile() );

            progress.worked( 100 );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
            throw new CoreException( StatusBridge.create( Status.createErrorStatus( "Failed copy new SDK..", e ) ) );
        }
        finally
        {
            progress.done();
        }

    }

    @Override
    public void createSpecialDescriptor( Composite parent, int style )
    {
        Composite fillLayoutComposite = SWTUtil.createComposite( parent, 2, 2, GridData.FILL_HORIZONTAL );

        final String descriptor =
            "The initial step will be to upgrade to Liferay Workspace or Liferay Plugins SDK 7.0. " +
            "For more details, please see <a>dev.liferay.com</a>.";

        String url = "https://dev.liferay.com/develop/tutorials";

        SWTUtil.createHyperLink( fillLayoutComposite, SWT.WRAP, descriptor, 1, url );

        final String extensionDec =
            "The first step will help you convert a Liferay Plugins SDK 6.2 to Liferay Plugins SDK 7.0 or to Liferay Workspace.\n" +
                "Click the \"import\" button to import your project into Eclipse workspace" +
                "(this process maybe need 5-10 mins for bundle init).\n" + "Note:\n" +
                "       In order to save time, downloading 7.0 ivy cache locally could be a good choice to upgrade to liferay plugins sdk 7. \n" +
                "       Theme and ext projects will be ignored for that we do not support to upgrade them in this tool currently. \n";

        Label image = new Label( fillLayoutComposite, SWT.WRAP);
        image.setImage( loadImage("question.png")  );

        PopupDialog popupDialog = new PopupDialog( fillLayoutComposite.getShell(),
            PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, true, false, false, false, false, null, null )
        {
            private static final int CURSOR_SIZE = 15;

            @Override
			protected Point getInitialLocation( Point initialSize )
            {
                Display display = getShell().getDisplay();
                Point location = display.getCursorLocation();
                location.x += CURSOR_SIZE;
                location.y += CURSOR_SIZE;
                return location;
            }

            @Override
			protected Control createDialogArea( Composite parent )
            {
                Label label = new Label( parent, SWT.WRAP );
                label.setText( extensionDec );
                label.setFont( new Font( null, "Times New Roman", 11, SWT.NORMAL ) );
                GridData gd = new GridData( GridData.BEGINNING | GridData.FILL_BOTH );
                gd.horizontalIndent = PopupDialog.POPUP_HORIZONTALSPACING;
                gd.verticalIndent = PopupDialog.POPUP_VERTICALSPACING;
                label.setLayoutData( gd );
                return label;
            }
        };

        image.addListener(SWT.MouseHover, new org.eclipse.swt.widgets.Listener()
        {
            @Override
            public void handleEvent( org.eclipse.swt.widgets.Event event )
            {
                popupDialog.open();
            }
        });

        image.addListener(SWT.MouseExit, new org.eclipse.swt.widgets.Listener()
        {
            @Override
            public void handleEvent( org.eclipse.swt.widgets.Event event )
            {
                popupDialog.close();
            }
        });
    }

    private void createBundleControl()
    {
        disposeServerEelment();

        disposeImportElement();

        disposeBundleElement();

        createDownloaBundleCheckboxElement();

        createBundleElement();

        createImportElement();

        pageParent.layout();
    }

    private void createServerControl()
    {
        disposeServerEelment();

        disposeImportElement();

        disposeBundleCheckboxElement();

        disposeBundleElement();

        createServerElement();

        createImportElement();

        pageParent.layout();
    }

    private void createMigrateLayoutElement()
    {
        layoutLabel = createLabel( pageParent, "Select Migrate Layout:" );
        layoutComb = new Combo( pageParent, SWT.DROP_DOWN | SWT.READ_ONLY );
        layoutComb.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        layoutComb.setItems( layoutNames );
        layoutComb.select( 0 );
        layoutComb.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetDefaultSelected( SelectionEvent e )
            {
            }

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                int sel = layoutComb.getSelectionIndex();

                if( sel == 1 )
                {
                    createServerControl();

                    dataModel.setDownloadBundle( false );
                }
                else
                {
                    createBundleControl();
                }

                dataModel.setLayout( layoutComb.getText() );

                startCheckThread();
            }

        } );

        dataModel.setLayout( layoutComb.getText() );
    }

    private void createDownloaBundleCheckboxElement()
    {
        disposeBundleCheckboxElement();
        downloadBundleCheckbox = SWTUtil.createCheckButton( pageParent, "Download Liferay bundle (recommended)", null, true, 1 );
        GridDataFactory.generate( downloadBundleCheckbox, 2, 1 );
        downloadBundleCheckbox.addSelectionListener( new SelectionAdapter()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                dataModel.setDownloadBundle( downloadBundleCheckbox.getSelection() );

                if( dataModel.getDownloadBundle().content() )
                {
                    disposeImportElement();
                    createBundleElement();
                    createImportElement();
                    pageParent.layout();
                }
                else
                {
                    disposeBundleElement();
                    disposeImportElement();
                    createImportElement();
                    pageParent.layout();
                }

                startCheckThread();
            }
        } );
    }

    private void createBundleElement()
    {
        bundleNameLabel = createLabel( pageParent, "Server Name:" );
        bundleNameField = createTextField( pageParent, SWT.NONE );

        bundleNameField.addModifyListener( new ModifyListener()
        {

            @Override
			public void modifyText( ModifyEvent e )
            {
                dataModel.setBundleName( bundleNameField.getText() );
            }
        } );

        final String bundleName = dataModel.getBundleName().content();

        bundleNameField.setText( bundleName != null ? bundleName : "" );

        bundleUrlLabel = createLabel( pageParent, "Bundle URL:" );

        bundleUrlField = createTextField( pageParent, SWT.NONE );
        bundleUrlField.setForeground( pageParent.getDisplay().getSystemColor( SWT.COLOR_DARK_GRAY ) );
        bundleUrlField.setText( dataModel.getBundleUrl().content( true ) );
        bundleUrlField.addModifyListener( new ModifyListener()
        {

            @Override
			public void modifyText( ModifyEvent e )
            {
                dataModel.setBundleUrl( bundleUrlField.getText() );
            }
        } );

        bundleUrlField.addFocusListener( new FocusListener()
        {

            @Override
            public void focusGained( FocusEvent e )
            {
                String input = ( (Text) e.getSource() ).getText();

                if( input.equals( LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL ) )
                {
                    bundleUrlField.setText( "" );
                }
                bundleUrlField.setForeground( pageParent.getDisplay().getSystemColor( SWT.COLOR_BLACK ) );
            }

            @Override
            public void focusLost( FocusEvent e )
            {
                String input = ( (Text) e.getSource() ).getText();

                if( CoreUtil.isNullOrEmpty( input ) )
                {
                    bundleUrlField.setForeground( pageParent.getDisplay().getSystemColor( SWT.COLOR_DARK_GRAY ) );
                    bundleUrlField.setText( LiferayUpgradeDataModel.DEFAULT_BUNDLE_URL );
                }
            }
        } );
        dataModel.setBundleUrl( bundleUrlField.getText() );
    }

    private void createImportElement()
    {
        createHorizontalSpacer = createHorizontalSpacer( pageParent, 3 );
        createSeparator = createSeparator( pageParent, 3 );

        String backupFolderName = "Backup project into folder";

        importButton = SWTUtil.createButton( pageParent, "Import Projects" );
        importButton.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                try
                {
                    Boolean importFinished = dataModel.getImportFinished().content();

                    if ( isPageValidate() && !importFinished )
                    {
                        saveSettings();

                        importButton.setEnabled( false );

                        importProject();

                        UpgradeView.resetPages();

                        PageNavigateEvent event = new PageNavigateEvent();

                        if( UpgradeView.getPageNumber() < 3 )
                        {
                            Boolean showAllPages = MessageDialog.openQuestion(
                                UIUtil.getActiveShell(), "Show All Pages",
                                "There is no project need to be upgraded.\n" +
                                    "Do you want to show all the following steps?" );

                            if( showAllPages )
                            {
                                UpgradeView.showAllPages();
                            }
                            else
                            {
                                event.setTargetPage( 1 );
                            }
                        }
                        else
                        {
                            event.setTargetPage( 2 );
                        }

                        for( PageNavigatorListener listener : naviListeners )
                        {
                            listener.onPageNavigate( event );
                        }

                        setNextPage( true );

                        importButton.setEnabled( true );

                        setSelectedAction( getSelectedAction( "PageFinishAction" ) );
                    }
                }
                catch( CoreException ex )
                {
                    ProjectUI.logError( ex );

                    PageValidateEvent pe = new PageValidateEvent();
                    pe.setMessage( ex.getMessage()  );
                    pe.setType( PageValidateEvent.ERROR );

                    triggerValidationEvent( pe );
                }
            }
        } );
    }

    private void createInitBundle( IProgressMonitor monitor ) throws CoreException
    {
        SubMonitor progress = SubMonitor.convert( monitor, 100 );
        try
        {
            progress.beginTask( "Execute Liferay Worksapce Bundle Init Command...", 100 );
            String layout = dataModel.getLayout().content();

            if( layout.equals( layoutNames[0] ) )
            {
                IPath sdkLocation = PathBridge.create( dataModel.getSdkLocation().content() );

                IProject project = CoreUtil.getProject( sdkLocation.lastSegment() );

                final String bundleUrl = dataModel.getBundleUrl().content();

                final String bundleName = dataModel.getBundleName().content();

                IWorkspaceProjectBuilder projectBuilder = getWorkspaceProjectBuilder( project );

                progress.worked( 30 );

                if( bundleUrl != null && projectBuilder != null )
                {
                    projectBuilder.initBundle( project, bundleUrl, monitor );
                }

                if( sdkLocation.append( "bundles" ).toFile().exists() )
                {
                    progress.worked( 60 );

                    final IPath runtimeLocation = sdkLocation.append( LiferayWorkspaceUtil.getHomeDir( sdkLocation.toOSString() ) );

                    ServerUtil.addPortalRuntimeAndServer( bundleName, runtimeLocation, monitor );

                    IServer bundleServer = ServerCore.findServer( dataModel.getBundleName().content() );

                    if( bundleServer != null )
                    {
                        org.eclipse.sapphire.modeling.Path newPath = dataModel.getSdkLocation().content();
                        SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( newPath ).append( "plugins-sdk" ) );

                        IPath bundleLocation = bundleServer.getRuntime().getLocation();

                        sdk.addOrUpdateServerProperties( bundleLocation );
                    }
                    project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                }
            }
            progress.worked( 100 );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
            throw new CoreException(
                StatusBridge.create( Status.createErrorStatus( "Failed to execute Liferay Workspace Bundle Init Command...", e ) ) );
        }
        finally
        {
            progress.done();
        }
    }

    private void createLiferayWorkspace( IPath targetSDKLocation, IProgressMonitor monitor ) throws CoreException
    {
        SubMonitor progress = SubMonitor.convert( monitor, 100 );

        try
        {
            progress.beginTask( "Initializing Liferay Workspace...", 100 );

            StringBuilder sb = new StringBuilder();

            sb.append( "-b " );
            sb.append( "\"" + targetSDKLocation.toFile().getAbsolutePath() + "\" " );
            sb.append( "init -u" );

            progress.worked( 30 );
            BladeCLI.execute( sb.toString() );
            progress.worked( 100 );
        }
        catch( BladeCLIException e )
        {
            ProjectUI.logError( e );
            throw new CoreException(
                StatusBridge.create( Status.createErrorStatus( "Faild execute Liferay Workspace Init Command...", e ) ) );
        }
        finally
        {
            progress.done();
        }
    }

    private void createServerElement()
    {
        serverLabel = createLabel( pageParent, "Liferay Server Name:" );
        serverComb = new Combo( pageParent, SWT.DROP_DOWN | SWT.READ_ONLY );
        serverComb.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        serverButton = SWTUtil.createButton( pageParent, "Add Server..." );
        serverButton.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected( SelectionEvent e )
            {
                ServerUIUtil.showNewServerWizard( pageParent.getShell(), "liferay.bundle", null, "com.liferay." );
            }
        } );

        ServerCore.addServerLifecycleListener( this );

        IServer[] servers = ServerCore.getServers();
        List<String> serverNames = new ArrayList<>();

        if( !CoreUtil.isNullOrEmpty( servers ) )
        {
            for( IServer server : servers )
            {
                if( LiferayServerCore.newPortalBundle( server.getRuntime().getLocation() ) != null )
                {
                    serverNames.add( server.getName() );
                }
            }
        }

        serverComb.setItems( serverNames.toArray( new String[serverNames.size()] ) );
        serverComb.select( 0 );
    }

    private void deleteEclipseConfigFiles( File project )
    {
        for( File file : project.listFiles() )
        {
            if( file.getName().contentEquals( ".classpath" ) || file.getName().contentEquals( ".settings" ) ||
                file.getName().contentEquals( ".project" ) )
            {
                if( file.isDirectory() )
                {
                    FileUtil.deleteDir( file, true );
                }
                file.delete();
            }
        }
    }

    private void deleteSDKLegacyProjects( IPath sdkLocation )
    {
        String[] needDeletedPaths = new String[] { "shared/portal-http-service", "webs/resources-importer-web" };

        for( String path : needDeletedPaths )
        {
            File file = sdkLocation.append( path ).toFile();

            if( file.exists() )
            {
                FileUtil.deleteDir( file, true );
            }
        }
    }

    private void deleteServiceBuilderJarFile( IProject project, IProgressMonitor monitor )
    {
        try
        {
            IFolder docrootFolder = CoreUtil.getDefaultDocrootFolder( project );

            if( docrootFolder != null )
            {
                IFile serviceJarFile = docrootFolder.getFile( "WEB-INF/lib/" + project.getName() + "-service.jar" );

                if( serviceJarFile.exists() )
                {
                    serviceJarFile.delete( true, monitor );
                }
            }
        }
        catch( CoreException e )
        {
            ProjectUI.logError( e );
        }
    }

    private void disposeBundleCheckboxElement()
    {
        if( downloadBundleCheckbox != null && downloadBundleCheckbox != null )
        {
            downloadBundleCheckbox.dispose();
        }
    }

    private void disposeBundleElement()
    {
        if( bundleNameField != null && bundleUrlField != null )
        {
            bundleNameField.dispose();
            bundleNameLabel.dispose();
            bundleUrlField.dispose();
            bundleUrlLabel.dispose();
        }
    }

    private void disposeMigrateLayoutElement()
    {
        if( layoutLabel != null && layoutComb != null )
        {
            layoutLabel.dispose();
            layoutComb.dispose();
        }
    }

    private void disposeImportElement()
    {
        createSeparator.dispose();
        createHorizontalSpacer.dispose();
        importButton.dispose();
    }

    private void disposeServerEelment()
    {
        if( serverLabel != null && serverComb != null && serverButton != null )
        {
            serverLabel.dispose();
            serverComb.dispose();
            serverButton.dispose();
        }
    }

    @Override
	public int getGridLayoutCount()
    {
        return 2;
    }

    @Override
    public boolean getGridLayoutEqualWidth()
    {
        return false;
    }

    private void getLiferayBundle( IPath targetSDKLocation, IProgressMonitor monitor ) throws BladeCLIException
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "-b " );
        sb.append( "\"" + targetSDKLocation.toFile().getAbsolutePath() + "\" " );
        sb.append( "init" );

        BladeCLI.execute( sb.toString() );
    }

    @Override
    public String getPageTitle()
    {
        return "Select project(s) to upgrade";
    }

    private IWorkspaceProjectBuilder getWorkspaceProjectBuilder( IProject project ) throws CoreException
    {
        final ILiferayProject liferayProject = LiferayCore.create( project );

        if( liferayProject == null )
        {
            throw new CoreException( ProjectUI.createErrorStatus( "Can't find Liferay workspace project." ) );
        }

        final IWorkspaceProjectBuilder builder = liferayProject.adapt( IWorkspaceProjectBuilder.class );

        if( builder == null )
        {
            throw new CoreException( ProjectUI.createErrorStatus( "Can't find Liferay Gradle project builder." ) );
        }

        return builder;
    }

    protected void importProject() throws CoreException
    {
        String layout = dataModel.getLayout().content();

        IPath location = PathBridge.create( dataModel.getSdkLocation().content() );

        if (isAlreadyImported(location))
        {
        		Stream.of(CoreUtil.getAllProjects()).forEach(this::checkProjectType);

        		dataModel.setImportFinished(true);
        		return;
        }

        try
        {
            PlatformUI.getWorkbench().getProgressService().run( true, true, new IRunnableWithProgress()
            {

                @Override
				public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException
                {
                    try
                    {
                        String newPath = "";

                        backup( monitor );

                        clearExistingProjects( location, monitor );

                        deleteEclipseConfigFiles( location.toFile() );

                        if( isMavenProject( location.toPortableString() ) )
                        {
                            ILiferayProjectImporter importer = LiferayCore.getImporter( "maven" );

                            List<IProject> projects = importer.importProjects( location.toPortableString(), monitor );

                            for( IProject project : projects )
                            {
                                checkProjectType( project );
                            }
                        }
                        else
                        {
                            if( layout.equals( "Upgrade to Liferay Workspace" ) )
                            {
                                createLiferayWorkspace( location, monitor );

                                removeIvyPrivateSetting( location.append( "plugins-sdk" ) );

                                newPath = renameProjectFolder( location, monitor );

                                IPath sdkLocation = new Path( newPath ).append( "plugins-sdk" );

                                deleteSDKLegacyProjects( sdkLocation );

                                ILiferayProjectImporter importer = LiferayCore.getImporter( "gradle" );

                                importer.importProjects( newPath, monitor );

                                if( dataModel.getDownloadBundle().content() )
                                {
                                    createInitBundle( monitor );
                                }

                                importSDKProject( sdkLocation, monitor );

                                dataModel.setConvertLiferayWorkspace( true );
                            }
                            else
                            {
                                deleteEclipseConfigFiles( location.toFile() );
                                copyNewSDK( location, monitor );

                                removeIvyPrivateSetting( location );

                                deleteSDKLegacyProjects( location );

                                String serverName = dataModel.getLiferay70ServerName().content();

                                IServer server = ServerUtil.getServer( serverName );

                                newPath = renameProjectFolder( location, monitor );

                                SDK sdk = SDKUtil.createSDKFromLocation( new Path( newPath ) );

                                sdk.addOrUpdateServerProperties(
                                    ServerUtil.getLiferayRuntime( server ).getLiferayHome() );

                                SDKUtil.openAsProject( sdk, monitor );

                                importSDKProject( sdk.getLocation(), monitor );
                            }
                        }

                        dataModel.setImportFinished( true );
                    }
                    catch( Exception e )
                    {
                        ProjectUI.logError( e );
                        throw new InvocationTargetException( e, e.getMessage() );
                    }
                }
            } );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
            throw new CoreException( StatusBridge.create( Status.createErrorStatus( e.getMessage(), e ) ) );
        }
    }

    private void importSDKProject( IPath targetSDKLocation, IProgressMonitor monitor )
    {
        Collection<File> eclipseProjectFiles = new ArrayList<>();

        Collection<File> liferayProjectDirs = new ArrayList<>();

        if( ProjectUtil.collectSDKProjectsFromDirectory(
            eclipseProjectFiles, liferayProjectDirs, targetSDKLocation.toFile(), null, true, monitor ) )
        {
            for( File project : liferayProjectDirs )
            {
                try
                {
                    deleteEclipseConfigFiles( project );

                    IProject importProject =
                        ProjectImportUtil.importProject( new Path( project.getPath() ), monitor, null );

                    if( importProject != null && importProject.isAccessible() && importProject.isOpen() )
                    {
                        checkProjectType( importProject );

                        deleteServiceBuilderJarFile( importProject, monitor );
                    }

                    if( ProjectUtil.isExtProject( importProject ) || ProjectUtil.isThemeProject( importProject ) )
                    {
                        importProject.delete( false, true, monitor );
                    }

                    checkAndConfigureIvy( importProject );
                }
                catch( CoreException e )
                {
                }
            }

            for( File project : eclipseProjectFiles )
            {
                try
                {

                    deleteEclipseConfigFiles( project.getParentFile() );

                    IProject importProject =
                        ProjectImportUtil.importProject( new Path( project.getParent() ), monitor, null );

                    if( importProject != null && importProject.isAccessible() && importProject.isOpen() )
                    {
                        checkProjectType( importProject );

                        deleteServiceBuilderJarFile( importProject, monitor );
                    }

                    if( ProjectUtil.isExtProject( importProject ) || ProjectUtil.isThemeProject( importProject ) )
                    {
                        importProject.delete( false, true, monitor );
                    }

                    checkAndConfigureIvy( importProject );
                }
                catch( CoreException e )
                {
                }
            }
        }
    }

    @Override
    public void onSelectionChanged( int targetSelection )
    {
        if( targetSelection == 1 )
        {
            startCheckThread();
        }
    }

    @SuppressWarnings( "unchecked" )
    private void removeIvyPrivateSetting( IPath sdkLocation ) throws CoreException
    {

        IPath ivySettingPath = sdkLocation.append( "ivy-settings.xml" );
        File ivySettingFile = ivySettingPath.toFile();

        SAXBuilder builder = new SAXBuilder( false );
        builder.setValidation( false );
        builder.setFeature( "http://xml.org/sax/features/validation", false );
        builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false );
        builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );

        try( InputStream ivyInput = Files.newInputStream( ivySettingFile.toPath() ) )
        {
            if( ivySettingFile.exists() )
            {
                Document doc = builder.build( ivyInput );

                Element itemRem = null;
                Element elementRoot = doc.getRootElement();
                List<Element> resolversElements = elementRoot.getChildren( "resolvers" );

                for( Iterator<Element> resolversIterator = resolversElements.iterator(); resolversIterator.hasNext(); )
                {
                    Element resolversElement = resolversIterator.next();

                    List<Element> chainElements = resolversElement.getChildren( "chain" );

                    for( Iterator<Element> chainIterator = chainElements.iterator(); chainIterator.hasNext(); )
                    {
                        Element chainElement = chainIterator.next();
                        List<Element> resolverElements = chainElement.getChildren( "resolver" );

                        for( Iterator<Element> resolverIterator = resolverElements.iterator(); resolverIterator.hasNext(); )
                        {
                            Element resolverItem = resolverIterator.next();
                            String resolverRefItem = resolverItem.getAttributeValue( "ref" );

                            if (resolverRefItem.equals( "liferay-private" ))
                            {
                                resolverIterator.remove();
                                itemRem = resolverItem;
                            }
                        }
                    }
                    elementRoot.removeContent( itemRem );

                    List<Element> ibiblioElements = resolversElement.getChildren( "ibiblio" );

                    for( Iterator<Element> ibiblioIterator = ibiblioElements.iterator(); ibiblioIterator.hasNext(); )
                    {
                        Element ibiblioElement = ibiblioIterator.next();
                        String liferayPrivateName = ibiblioElement.getAttributeValue( "name" );

                        if (liferayPrivateName.equals( "liferay-private" ))
                        {
                            ibiblioIterator.remove();
                            itemRem = ibiblioElement;
                        }
                    }
                    elementRoot.removeContent( itemRem );
                }

                saveXML( ivySettingFile, doc );
            }
        }
        catch( IOException|JDOMException|CoreException e)
        {
            ProjectUI.logError( e );
            throw new CoreException(
                StatusBridge.create( Status.createErrorStatus( "Failed to remove Liferay private url configuration of ivy-settings.xml.", e ) ) );
        }
    }

    private String renameProjectFolder( IPath targetSDKLocation, IProgressMonitor monitor ) throws CoreException
    {
        // if( newName == null || newName.equals( "" ) )
        // {
        return targetSDKLocation.toString();
        // }
        // java.nio.file.Path newTargetPath;
        // File newFolder = targetSDKLocation.removeLastSegments( 1 ).append( newName ).toFile();
        // boolean renameStatus = targetSDKLocation.toFile().renameTo( newFolder );
        // try
        // {
        // newTargetPath = Files.move( targetSDKLocation.toFile().toPath(), newFolder.toPath(),
        // StandardCopyOption.REPLACE_EXISTING );
        // }
        // catch ( Exception e)
        // {
        // ProjectUI.logError( e );
        // throw new CoreException( StatusBridge.create( Status.createErrorStatus( "Failed to reanme target SDK folder
        // name.", e ) ) );
        // }
        // return newTargetPath.toAbsolutePath().toString();

        // if ( renameStatus == false )
        // {
        // throw new CoreException( StatusBridge.create( Status.createErrorStatus( "Failed to reanme target SDK folder
        // name." ) ) );
        // }
        // else
        // {
        // return newFolder.toPath().toString();
        // }
        //

    }

    private void saveSettings()
    {
        dataModel.setHasExt( false );
        dataModel.setHasHook( false );
        dataModel.setHasLayout( false );
        dataModel.setHasPortlet( false );
        dataModel.setHasServiceBuilder( false );
        dataModel.setHasTheme( false );
        dataModel.setHasWeb( false );

        if( bundleNameField != null && !bundleNameField.isDisposed() )
        {
            dataModel.setLiferay70ServerName( bundleNameField.getText() );
        }

        if( serverComb != null && !serverComb.isDisposed() )
        {
            dataModel.setLiferay70ServerName( serverComb.getText() );
        }

        SDK sdk = SDKUtil.createSDKFromLocation( new Path( dirField.getText() ) );

        try
        {
            if( sdk != null )
            {
                final String liferay62ServerLocation =
                    (String) ( sdk.getBuildProperties( true ).get( ISDKConstants.PROPERTY_APP_SERVER_PARENT_DIR ) );
                dataModel.setLiferay62ServerLocation( liferay62ServerLocation );
            }
        }
        catch( Exception xe )
        {
            ProjectUI.logError( xe );
        }
    }

    private void saveXML( File templateFile, Document doc ) throws CoreException
    {
        XMLOutputter out = new XMLOutputter();

        try(OutputStream fos = Files.newOutputStream( templateFile.toPath() );)
        {
            out.output( doc, fos );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
            throw new CoreException(
                StatusBridge.create( Status.createErrorStatus( "Failed to save change for ivy-settings.xml.", e ) ) );
        }
    }

    @Override
    public void serverAdded( IServer server )
    {
        UIUtil.async( new Runnable()
        {

            @Override
            public void run()
            {
                boolean serverExisted = false;

                if( serverComb != null && !serverComb.isDisposed() )
                {
                    String[] serverNames = serverComb.getItems();
                    List<String> serverList = new ArrayList<>( Arrays.asList( serverNames ) );

                    for( String serverName : serverList )
                    {
                        if( server.getName().equals( serverName ) )
                        {
                            serverExisted = true;
                        }
                    }
                    if( serverExisted == false )
                    {
                        serverList.add( server.getName() );
                        serverComb.setItems( serverList.toArray( new String[serverList.size()] ) );
                        serverComb.select( serverList.size() - 1 );
                    }

                    startCheckThread();

                }
            }
        } );
    }

    @Override
    public void serverChanged( IServer server )
    {
    }

    @Override
    public void serverRemoved( IServer server )
    {
        UIUtil.async( new Runnable()
        {

            @Override
            public void run()
            {
                if( serverComb != null && !serverComb.isDisposed() )
                {
                    String[] serverNames = serverComb.getItems();
                    List<String> serverList = new ArrayList<>( Arrays.asList( serverNames ) );

                    Iterator<String> serverNameiterator = serverList.iterator();
                    while( serverNameiterator.hasNext() )
                    {
                        String serverName = serverNameiterator.next();
                        if( server.getName().equals( serverName ) )
                        {
                            serverNameiterator.remove();
                        }
                    }
                    serverComb.setItems( serverList.toArray( new String[serverList.size()] ) );
                    serverComb.select( 0 );

                    startCheckThread();
                }
            }
        } );
    }

    private void startCheckThread()
    {
        final Thread t = new Thread()
        {
            @Override
            public void run()
            {
                validate();
            }
        };

        t.start();
    }

    private void validate()
    {
        UIUtil.async( new Runnable()
        {
            @Override
            public void run()
            {
                boolean inputValidation = true;
                boolean layoutValidation = true;
                boolean downloadBundle = dataModel.getDownloadBundle().content();
                String bundUrl = dataModel.getBundleUrl().content();

                String message = "ok";

                PageValidateEvent pe = new PageValidateEvent();
                pe.setType( PageValidateEvent.ERROR );

                if( !sdkValidation.compute().ok() )
                {
                    message = sdkValidation.compute().message();

                    inputValidation = false;
                }
                else if( !dataModel.getBackupLocation().validation().ok() )
                {
                    message = dataModel.getBackupLocation().validation().message();

                    inputValidation = false;
                }
                else
                {
                    inputValidation = true;
                }

                if( !layoutComb.isDisposed() )
                {
                    if( layoutComb.getSelectionIndex() == 1 )
                    {
                        final int itemCount = serverComb.getItemCount();

                        if( itemCount < 1 )
                        {
                            message = "You should add at least one Liferay 7 portal bundle.";

                            layoutValidation = false;
                        }
                    }
                    else if( layoutComb.getSelectionIndex() == 0 )
                    {
                        if( downloadBundle && !bundleNameValidation.compute().ok() )
                        {
                            message = bundleNameValidation.compute().message();

                            layoutValidation = false;
                        }
                        else if( downloadBundle && bundUrl != null && bundUrl.length() > 0 && !bundleUrlValidation.compute().ok() )
                        {
                            message = bundleUrlValidation.compute().message();

                            layoutValidation = false;
                        }
                        else
                        {
                            layoutValidation = true;
                        }
                    }
                }

                if( dataModel.getImportFinished().content() )
                {
                    message =
                        "Import has finished. If you want to reset, please click reset icon in view toolbar.";

                    pe.setType( PageValidateEvent.WARNING );

                    inputValidation = false;
                }

                pe.setMessage( message );

                triggerValidationEvent( pe );

                validationResult = layoutValidation && inputValidation;

                importButton.setEnabled( validationResult );
            }
        } );
    }

    private boolean isPageValidate()
    {
        return validationResult;
    }
}
