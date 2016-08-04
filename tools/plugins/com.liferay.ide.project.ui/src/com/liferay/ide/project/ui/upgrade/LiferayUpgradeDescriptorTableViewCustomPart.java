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

package com.liferay.ide.project.ui.upgrade;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;
import com.liferay.ide.project.core.util.ProjectImportUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;
import com.liferay.ide.project.ui.wizard.ElementLabelProvider;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * @author Simon Jiang
 */
public class LiferayUpgradeDescriptorTableViewCustomPart extends FormComponentPart
{

    private final static String[][] DESCRIPTORS_AND_IMAGES = { { "liferay-portlet.xml", "/icons/e16/portlet.png" },
        { "liferay-display.xml", "/icons/e16/liferay_display_xml.png" }, { "service.xml", "/icons/e16/service.png" },
        { "liferay-hook.xml", "/icons/e16/hook.png" }, { "liferay-layout-templates.xml", "/icons/e16/layout.png" },
        { "liferay-look-and-feel.xml", "/icons/e16/theme.png" }, { "liferay-portlet-ext.xml", "/icons/e16/ext.png" } };

    private final static String PUBLICID_REGREX =
        "-\\//(?:[a-z][a-z]+)\\//(?:[a-z][a-z]+)[\\s+(?:[a-z][a-z0-9_]*)]*\\s+(\\d\\.\\d\\.\\d)\\//(?:[a-z][a-z]+)";

    private final static String SYSTEMID_REGREX =
        "^http://www.liferay.com/dtd/[-A-Za-z0-9+&@#/%?=~_()]*(\\d_\\d_\\d).dtd";

    private class LiferayDescriptorUpgradeElement
    {

        public String name;
        public String context;
        public final String location;
        public final String descriptorName;

        public LiferayDescriptorUpgradeElement( String name, String context, String location, String descriptorName )
        {
            this.context = context;
            this.name = name;
            this.location = location;
            this.descriptorName = descriptorName;
        }
    }

    protected class LiferayUpgradeTabeViewLabelProvider extends ElementLabelProvider
        implements IColorProvider, IStyledLabelProvider
    {

        private String GREY_COLOR;

        private final ColorRegistry COLOR_REGISTRY = JFaceResources.getColorRegistry();

        protected Styler GREYED_STYLER;

        public LiferayUpgradeTabeViewLabelProvider()
        {
            COLOR_REGISTRY.put( GREY_COLOR, new RGB( 128, 128, 128 ) );
            GREYED_STYLER = StyledString.createColorRegistryStyler( GREY_COLOR, null );
        }

        public LiferayUpgradeTabeViewLabelProvider( final String greyColorName )
        {
            this.GREY_COLOR = greyColorName;
        }

        @Override
        public Color getForeground( Object element )
        {
            if( element instanceof LiferayDescriptorUpgradeElement )
            {
                final String srcLableString = ( (LiferayDescriptorUpgradeElement) element ).context;

                if( srcLableString.contains( "Finished" ) )
                {
                    return Display.getCurrent().getSystemColor( SWT.COLOR_BLUE );
                }
            }

            return Display.getCurrent().getSystemColor( SWT.COLOR_BLACK );
        }

        @Override
        public Image getImage( Object element )
        {
            if( element instanceof LiferayDescriptorUpgradeElement )
            {
                final String descriptorName = ( (LiferayDescriptorUpgradeElement) element ).descriptorName;

                if( descriptorName != null )
                {
                    return this.getImageRegistry().get( descriptorName );
                }
            }

            return null;
        }

        @Override
        public StyledString getStyledText( Object element )
        {
            if( element instanceof LiferayDescriptorUpgradeElement )
            {
                final String srcLableString = ( (LiferayDescriptorUpgradeElement) element ).context;
                final String elementName = ( (LiferayDescriptorUpgradeElement) element ).name;
                final StyledString styled = new StyledString( elementName );
                return StyledCellLabelProvider.styleDecoratedString( srcLableString, GREYED_STYLER, styled );
            }

            return new StyledString( ( (LiferayDescriptorUpgradeElement) element ).context );
        }

        @Override
        protected void initalizeImageRegistry( ImageRegistry imageRegistry )
        {
            for( String[] descriptorsAndImages : DESCRIPTORS_AND_IMAGES )
            {
                final String descName = descriptorsAndImages[0];
                final String descImage = descriptorsAndImages[1];

                imageRegistry.put( descName, ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, descImage ) );
            }
        }
    }

    private class NIOSearchFilesVisitor extends SimpleFileVisitor<java.nio.file.Path>
    {

        File resources;

        String searchFileName = null;

        NIOSearchFilesVisitor( String searchFileName )
        {
            this.searchFileName = searchFileName;
        }

        public File getSearchFile()
        {
            return this.resources;
        }

        @Override
        public FileVisitResult visitFile( java.nio.file.Path path, BasicFileAttributes attrs ) throws IOException
        {
            if( path.endsWith( searchFileName ) )
            {
                resources = path.toFile();

                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }
    }

    protected class TableViewContentProvider implements IStructuredContentProvider
    {

        @Override
        public void dispose()
        {
        }

        @Override
        public Object[] getElements( Object inputElement )
        {
            if( inputElement instanceof LiferayDescriptorUpgradeElement[] )
            {
                return (LiferayDescriptorUpgradeElement[]) inputElement;
            }

            return new Object[] { inputElement };
        }

        @Override
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }

    }

    public static IPath getTempLocation( String prefix, String fileName )
    {
        return ProjectUI.getDefault().getStateLocation().append( "tmp" ).append(
            prefix + "/" + System.currentTimeMillis() +
                ( CoreUtil.isNullOrEmpty( fileName ) ? StringPool.EMPTY : "/" + fileName ) );
    }

    protected Status retval = Status.createOkStatus();

    protected TableViewer tableViewer;

    protected LiferayDescriptorUpgradeElement[] tableViewElements;

    protected void compare( IStructuredSelection selection )
    {
        final LiferayDescriptorUpgradeElement descriptorElement =
            (LiferayDescriptorUpgradeElement) selection.getFirstElement();

        final String projectName = descriptorElement.name;
        final String descriptorName = descriptorElement.descriptorName;
        final String srcFileLocation = descriptorElement.location;
        final IPath srcFileIPath = PathBridge.create( new Path( srcFileLocation ) );
        final String[] descriptorToken = descriptorName.split( "\\." );
        final IPath createPreviewerFile =
            createPreviewerFile( projectName, srcFileIPath, srcFileLocation, descriptorToken[1] );

        final LiferayDescriptorUpgradeCompre lifeayDescriptorUpgradeCompre =
            new LiferayDescriptorUpgradeCompre( srcFileIPath, createPreviewerFile, descriptorName );

        lifeayDescriptorUpgradeCompre.openCompareEditor();
    }

    @Override
    public FormComponentPresentation createPresentation( SwtPresentation parent, Composite composite )
    {
        return new FormComponentPresentation( this, parent, composite)
        {

            @Override
            public void render()
            {
                final Composite parent = SWTUtil.createComposite( composite(), 2, 2, GridData.FILL_BOTH );

                tableViewer = new TableViewer( parent );

                tableViewer.setContentProvider( new TableViewContentProvider() );

                tableViewer.setLabelProvider( new DelegatingStyledCellLabelProvider( getLableProvider() ) );

                tableViewer.addDoubleClickListener( new IDoubleClickListener()
                {

                    @Override
                    public void doubleClick( DoubleClickEvent event )
                    {
                        compare( (IStructuredSelection) event.getSelection() );
                    }
                } );

                final Table table = tableViewer.getTable();
                final GridData tableData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 4 );

                tableData.heightHint = 225;
                tableData.widthHint = 400;
                table.setLayoutData( tableData );

                final Button selectAllButton = new Button( parent, SWT.NONE );

                selectAllButton.setText( "Find..." );
                selectAllButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                selectAllButton.addListener( SWT.Selection, new Listener()
                {

                    @Override
                    public void handleEvent( Event event )
                    {
                        handleFindEvent();
                    }
                } );

                final Button upgradeButton = new Button( parent, SWT.NONE );
                upgradeButton.setText( "Upgrade..." );
                upgradeButton.setLayoutData( new GridData( SWT.FILL, SWT.TOP, false, false ) );
                upgradeButton.addListener( SWT.Selection, new Listener()
                {

                    @Override
                    public void handleEvent( Event event )
                    {
                        handleUpgradeEvent();
                    }
                } );

            }
        };
    }

    private IPath createPreviewerFile(
        final String projectName, final IPath srcFilePath, final String location, final String descriptorType )
    {
        final IPath templateLocation = getTempLocation( projectName, srcFilePath.lastSegment() );

        templateLocation.toFile().getParentFile().mkdirs();

        if( descriptorType.equals( "xml" ) )
        {
            try
            {
                updateXMLDescriptor( new File( location ), templateLocation.toFile() );
            }
            catch( JDOMException | IOException e )
            {
                ProjectCore.logError( e );
            }
        }

        return templateLocation;
    }

    private List<LiferayDescriptorUpgradeElement> getInitItemsList(
        List<IProject> projects, IProgressMonitor monitor )
    {
        final List<LiferayDescriptorUpgradeElement> tableViewElementList = new ArrayList<>();

        final Path sdkLocation = op().getNewLocation().content();

        String context = null;

        int count = projects.size();

        if( count <= 0 )
        {
            return tableViewElementList;
        }

        int unit = 100 / count;

        monitor.beginTask( "Find needed upgrade descriptor......", 100 );

        for( int i = 0; i < count; i++ )
        {
            monitor.worked( i + 1 * unit );

            if( i == count - 1 )
            {
                monitor.worked( 100 );
            }

            IProject project = projects.get( i );
            monitor.setTaskName( "Finding desciptors for " + project.getName() );

            Path[] descriptorFiles = getUpgradeDTDFiles( project.getLocationURI() );

            for( Path descriptorPath : descriptorFiles )
            {
                IPath filePath = PathBridge.create( descriptorPath );

                boolean needUpgrade = isNeedUpgrade( filePath.toFile() );

                if( needUpgrade )
                {
                    final String projectLocation = descriptorPath.makeRelativeTo( sdkLocation ).toPortableString();

                    context =
                        filePath.lastSegment() + " (" + project.getName() + " - Location: " + projectLocation + ")";

                    LiferayDescriptorUpgradeElement tableViewElement = new LiferayDescriptorUpgradeElement(
                        project.getName(), context, filePath.toPortableString(), filePath.lastSegment() );

                    tableViewElementList.add( tableViewElement );
                }
            }
        }

        return tableViewElementList;
    }

    private IStyledLabelProvider getLableProvider()
    {
        return new LiferayUpgradeTabeViewLabelProvider( "Upgrade Descriptors" );
    }

    private String getNewDoctTypeSetting( String doctypeSetting, String newValue, String regrex )
    {
        String newDoctTypeSetting = null;
        Pattern p = Pattern.compile( regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
        Matcher m = p.matcher( doctypeSetting );

        if( m.find() )
        {
            String oldVersionString = m.group( m.groupCount() );
            newDoctTypeSetting = doctypeSetting.replace( oldVersionString, newValue );
        }

        return newDoctTypeSetting;
    }

    private String getOldVersion( final String sourceDTDVersion, final String regrex )
    {

        if( sourceDTDVersion == null )
        {
            return null;
        }

        Pattern p = Pattern.compile( regrex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL );
        Matcher m = p.matcher( sourceDTDVersion );

        if( m.find() )
        {
            String oldVersionString = m.group( m.groupCount() );
            return oldVersionString;
        }

        return null;
    }

    private Path[] getUpgradeDTDFiles( URI fileUri )
    {
        List<Path> files = new ArrayList<Path>();

        for( String[] descriptors : DESCRIPTORS_AND_IMAGES )
        {
            final String searchName = descriptors[0];
            NIOSearchFilesVisitor searchFilesVisitor = new NIOSearchFilesVisitor( searchName );
            try
            {
                Files.walkFileTree( Paths.get( fileUri ), searchFilesVisitor );
            }
            catch( IOException e )
            {
                ProjectUI.logError( e );
            }

            if( searchFilesVisitor.getSearchFile() != null )
            {
                files.add( new Path( searchFilesVisitor.getSearchFile().getAbsolutePath() ) );
            }
        }

        return files.toArray( new Path[files.size()] );
    }

    private void handleFindEvent()
    {
        List<IProject> projects = new ArrayList<>();

        final JavaProjectSelectionDialog dialog =
            new JavaProjectSelectionDialog( Display.getCurrent().getActiveShell() );

        if( dialog.open() == Window.OK )
        {
            final Object[] selectedProjects = dialog.getResult();

            if( selectedProjects != null )
            {
                for( Object project : selectedProjects )
                {
                    if( project instanceof IJavaProject )
                    {
                        IJavaProject p = (IJavaProject) project;
                        projects.add( p.getProject() );
                    }
                }
            }
        }
        try
        {
            final WorkspaceJob workspaceJob = new WorkspaceJob( "Find needed upgrade DTD files......")
            {

                @Override
                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                {
                    final List<LiferayDescriptorUpgradeElement> tableViewElementList =
                        getInitItemsList( projects, monitor );

                    tableViewElements = tableViewElementList.toArray(
                        new LiferayDescriptorUpgradeElement[tableViewElementList.size()] );

                    UIUtil.async( new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            tableViewer.setInput( tableViewElements );

                            updateValidation();
                        }
                    } );

                    updateValidation();

                    return StatusBridge.create( Status.createOkStatus() );
                }
            };
            workspaceJob.setUser( true );
            workspaceJob.schedule();
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }

    }

    private void handleUpgradeEvent()
    {
        try
        {
            final WorkspaceJob workspaceJob = new WorkspaceJob( "Find needed upgrade DTD files......")
            {

                @Override
                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                {
                    int count = tableViewElements.length;

                    if( count <= 0 )
                    {
                        return StatusBridge.create( Status.createOkStatus() );
                    }

                    int unit = 100 / count;

                    monitor.beginTask( "Upgrade Liferay Plugin Projcet Descriptor", 100 );

                    for( int i = 0; i < count; i++ )
                    {
                        monitor.worked( i + 1 * unit );

                        if( i == count - 1 )
                        {
                            monitor.worked( 100 );
                        }

                        LiferayDescriptorUpgradeElement tableViewElement = tableViewElements[i];

                        final String descriptorName = tableViewElement.descriptorName;
                        final String srcFileLocation = tableViewElement.location;
                        final String projectName = tableViewElement.name;
                        final String[] descriptorToken = descriptorName.split( "\\." );
                        final String descriptorType = descriptorToken[1];

                        monitor.setTaskName( "Upgrading desciptors for " + projectName );

                        if( descriptorType.equals( "xml" ) )
                        {
                            try
                            {
                                updateXMLDescriptor( new File( srcFileLocation ), new File( srcFileLocation ) );

                                IProject project = ProjectUtil.getProject( projectName );

                                if( project != null )
                                {
                                    project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
                                }

                                final int loopNum = i;

                                UIUtil.async( new Runnable()
                                {

                                    @Override
                                    public void run()
                                    {
                                        tableViewElement.context = tableViewElement.context + "( Finished )";

                                        tableViewElements[loopNum] = tableViewElement;

                                        tableViewer.setInput( tableViewElements );

                                        tableViewer.refresh();

                                    }
                                } );
                            }
                            catch( Exception e )
                            {
                                ProjectCore.logError( "Error upgrade Liferay Plugin xml DTD Version. ", e );
                            }
                        }
                    }
                    return StatusBridge.create( Status.createOkStatus() );
                }

            };

            workspaceJob.setUser( true );
            workspaceJob.schedule();
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }

    private boolean isNeedUpgrade( File srcFile )
    {
        try
        {
            SAXBuilder builder = new SAXBuilder( false );
            builder.setValidation( false );
            Document doc = builder.build( new FileInputStream( srcFile ) );

            DocType docType = doc.getDocType();

            if( docType != null )
            {
                final String publicId = docType.getPublicID();
                String oldPublicIdVersion = getOldVersion( publicId, PUBLICID_REGREX );

                final String systemId = docType.getSystemID();
                String oldSystemIdVersion = getOldVersion( systemId, SYSTEMID_REGREX );

                if( ( publicId != null && !oldPublicIdVersion.equals( "7.0.0" ) ) ||
                    ( systemId != null && !oldSystemIdVersion.equals( "7_0_0" ) ) )
                {
                    return true;
                }
            }
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
        return false;
    }

    private CodeUpgradeOp op()
    {
        return getLocalModelElement().nearest( CodeUpgradeOp.class );
    }

    private void updateValidation()
    {
        retval = Status.createOkStatus();

        Value<Path> sdkPath = op().getNewLocation();

        if( sdkPath != null )
        {
            Path sdkLocation = sdkPath.content();

            if( sdkLocation != null )
            {
                IStatus status = ProjectImportUtil.validateSDKPath( sdkLocation.toPortableString() );

                retval = StatusBridge.create( status );
            }
        }

        refreshValidation();
    }

    private void updateXMLDescriptor( File srcFile, File templateFile )
        throws JDOMException, IOException, FileNotFoundException
    {
        SAXBuilder builder = new SAXBuilder( false );
        builder.setValidation( false );
        Document doc = builder.build( new FileInputStream( srcFile ) );
        DocType docType = doc.getDocType();

        if( docType != null )
        {
            final String publicId = docType.getPublicID();
            final String newPublicId = getNewDoctTypeSetting( publicId, "7.0.0", PUBLICID_REGREX );
            docType.setPublicID( newPublicId );

            final String systemId = docType.getSystemID();
            final String newSystemId = getNewDoctTypeSetting( systemId, "7_0_0", SYSTEMID_REGREX );
            docType.setSystemID( newSystemId );
        }

        XMLOutputter out = new XMLOutputter();

        if( templateFile.exists() )
        {
            templateFile.delete();
        }

        templateFile.createNewFile();

        try(FileOutputStream fos = new FileOutputStream( templateFile );)
        {
            out.output( doc, fos );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }
}
