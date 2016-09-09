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
import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.dialog.CustomProjectSelectionDialog;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Andy Wu
 */
public class ConvertedProjectTreesPart extends FormComponentPart
{

    public class ConvertedProjectTreesPresentation extends FormComponentPresentation
    {

        private class CompareItem extends BufferedContent implements ITypedElement, IModificationDate, IEditableContent
        {

            private String fileName;
            private long time;

            public CompareItem( String fileName )
            {
                this.fileName = fileName;
                this.time = System.currentTimeMillis();
            }

            protected InputStream createStream() throws CoreException
            {
                try
                {
                    return new FileInputStream( new File( fileName ) );
                }
                catch( FileNotFoundException e )
                {
                    e.printStackTrace();
                }

                return new ByteArrayInputStream( new byte[0] );
            }

            public Image getImage()
            {
                return CompareUI.DESC_CTOOL_NEXT.createImage();
            }

            public long getModificationDate()
            {
                return time;
            }

            public String getName()
            {
                return fileName;
            }

            public String getType()
            {
                return ITypedElement.TEXT_TYPE;
            }

            public boolean isEditable()
            {
                return true;
            }

            public ITypedElement replace( ITypedElement dest, ITypedElement src )
            {
                return null;
            }

            public void writeFile()
            {
                this.writeFile( this.fileName, this.getContent() );
            }

            private void writeFile( String fileName, byte[] newContent )
            {
                FileOutputStream fos = null;
                try
                {
                    File file = new File( fileName );
                    if( file.exists() )
                    {
                        file.delete();
                    }

                    file.createNewFile();

                    fos = new FileOutputStream( file );
                    fos.write( newContent );
                    fos.flush();

                }
                catch( IOException e )
                {
                    e.printStackTrace();

                }
                finally
                {
                    try
                    {
                        fos.close();
                    }
                    catch( IOException e )
                    {
                        e.printStackTrace();
                    }

                    fos = null;
                }
            }
        }

        class LeftViewLabelProvider extends StyledCellLabelProvider
        {

            private String getFileName( File file )
            {
                String name = file.getName();

                File html = new File( file, "html" );

                if( html.exists() && html.isDirectory() )
                {
                    IPath location = Path.fromOSString( file.getAbsolutePath() );
                    IFile ifile = CoreUtil.getWorkspaceRoot().getFileForLocation( location );

                    return ifile.getProject().getName();
                }
                else
                {
                    return name.isEmpty() ? file.getPath() : name;
                }
            }

            @Override
            public void update( ViewerCell cell )
            {
                Object element = cell.getElement();
                StyledString text = new StyledString();

                File file = (File) element;

                if( file.isDirectory() )
                {
                    text.append( getFileName( file ) );

                    File html = new File( file, "html" );

                    if( html.exists() && html.isDirectory() )
                    {
                        cell.setImage( imageProject );
                    }
                    else
                    {
                        cell.setImage( imageFolder );
                    }

                    String[] files = file.list( new FilenameFilter()
                    {

                        @Override
                        public boolean accept( File dir, String name )
                        {
                            if( !name.startsWith( "." ) )
                            {
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }
                    } );

                    if( files != null )
                    {
                        text.append( " (" + files.length + ") ", StyledString.COUNTER_STYLER );
                    }
                }
                else
                {
                    cell.setImage( imageFile );

                    text.append( getFileName( file ) );

                    if( is62FileFound( file ) )
                    {
                        text.append( "(found)", StyledString.COUNTER_STYLER );
                    }
                    else
                    {
                        text.append( "(unfound)", StyledString.DECORATIONS_STYLER );
                    }
                }

                cell.setText( text.toString() );
                cell.setStyleRanges( text.getStyleRanges() );

                super.update( cell );
            }
        }

        class RightViewLabelProvider extends StyledCellLabelProvider
        {

            private String getFileName( File file )
            {
                String name = file.getName();

                if( name.equals( "resources" ) )
                {
                    IPath location = Path.fromOSString( file.getAbsolutePath() );
                    IFile ifile = CoreUtil.getWorkspaceRoot().getFileForLocation( location );

                    return ifile.getProject().getName();
                }
                else
                {
                    return name.isEmpty() ? file.getPath() : name;
                }
            }

            @Override
            public void update( ViewerCell cell )
            {
                Object element = cell.getElement();
                StyledString text = new StyledString();

                File file = (File) element;

                if( file.isDirectory() )
                {
                    text.append( getFileName( file ) );

                    if( file.getName().endsWith( "resources" ) )
                    {
                        cell.setImage( imageProject );
                    }
                    else
                    {
                        cell.setImage( imageFolder );
                    }

                    String[] files = file.list( new FilenameFilter()
                    {

                        @Override
                        public boolean accept( File dir, String name )
                        {
                            if( !name.startsWith( "." ) )
                            {
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }
                    } );

                    if( files != null )
                    {
                        text.append( " (" + files.length + ") ", StyledString.COUNTER_STYLER );
                    }
                }
                else
                {
                    cell.setImage( imageFile );

                    text.append( getFileName( file ) );

                    if( is70FileFound( file ) )
                    {
                        text.append( "(found)", StyledString.COUNTER_STYLER );
                    }
                    else
                    {
                        text.append( "(unfound)", StyledString.DECORATIONS_STYLER );
                    }
                }

                cell.setText( text.toString() );
                cell.setStyleRanges( text.getStyleRanges() );

                super.update( cell );
            }
        }

        class ViewContentProvider implements ITreeContentProvider
        {

            @Override
            public void dispose()
            {
            }

            @Override
            public Object[] getChildren( Object parentElement )
            {
                File file = (File) parentElement;

                File[] files = file.listFiles( new FilenameFilter()
                {

                    @Override
                    public boolean accept( File dir, String name )
                    {
                        if( name.startsWith( "." ) )
                        {
                            return false;
                        }
                        return true;
                    }
                } );

                return files;
            }

            @Override
            public Object[] getElements( Object inputElement )
            {
                return (File[]) inputElement;
            }

            @Override
            public Object getParent( Object element )
            {
                File file = (File) element;

                return file.getParentFile();
            }

            @Override
            public boolean hasChildren( Object element )
            {
                File file = (File) element;

                if( file.isDirectory() )
                {
                    return true;
                }

                return false;
            }

            public void inputChanged( Viewer v, Object oldInput, Object newInput )
            {
            }
        }

        private String compareType70 = "7.x";
        private String compareType62 = "6.2";

        private Image imageFile;
        private Image imageFolder;
        private Image imageProject;

        private TreeViewer leftTreeViewer;
        private TreeViewer rightTreeViewer;

        private String staticPath = "/src/main/resources/META-INF/resources/";

        public ConvertedProjectTreesPresentation( FormComponentPart part, SwtPresentation parent, Composite composite )
        {
            super( part, parent, composite );
        }

        public void compare( final String originalFile, final String changedFile, final String compareType )
        {
            CompareConfiguration config = new CompareConfiguration();

            config.setLeftEditable( false );
            config.setLeftLabel( compareType + " original jsp" );

            config.setRightEditable( true );
            config.setRightLabel( "custom jsp" );

            CompareEditorInput editorInput = new CompareEditorInput( config )
            {

                CompareItem originalItem = new CompareItem( originalFile );
                CompareItem changedItem = new CompareItem( changedFile );

                @Override
                protected Object prepareInput( IProgressMonitor monitor )
                    throws InvocationTargetException, InterruptedException
                {
                    return new DiffNode( originalItem, changedItem );
                }

                @Override
                public void saveChanges( IProgressMonitor pm ) throws CoreException
                {
                    super.saveChanges( pm );

                    changedItem.writeFile();
                }
            };

            editorInput.setTitle( "Jsp File Compare" );

            CompareUI.openCompareEditor( editorInput );
        }

        private void createChildren( Composite container )
        {
            createImages();
            createLeftPart( container );
            createRightPart( container );
        }

        private Image createImage( String symbolicName )
        {
            Image image = PlatformUI.getWorkbench().getSharedImages().getImage( symbolicName );

            if( image.isDisposed() )
            {
                image = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( symbolicName ).createImage();
            }

            return image;
        }

        private void createImages()
        {
            this.imageProject = createImage( SharedImages.IMG_OBJ_PROJECT );
            this.imageFolder = createImage( ISharedImages.IMG_OBJ_FOLDER );
            this.imageFile = createImage( ISharedImages.IMG_OBJ_FILE );
        }

        private void createLeftPart( Composite parent )
        {
            ScrolledComposite leftContainer = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );

            Composite leftPart = SWTUtil.createComposite( leftContainer, 1, 0, GridData.FILL_BOTH );

            leftContainer.setLayout( new FillLayout() );
            leftContainer.setMinSize( 410, 200 );
            leftContainer.setExpandHorizontal( true );
            leftContainer.setExpandVertical( true );
            leftContainer.setContent( leftPart );

            Label leftLabel = new Label( leftPart, SWT.NONE );
            leftLabel.setText( "6.2 Custom JSPs (double-click to compare with original)" );

            leftTreeViewer = new TreeViewer( leftPart, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );

            GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );

            leftTreeViewer.getTree().setLayoutData( gd );

            leftTreeViewer.setContentProvider( new ViewContentProvider() );
            leftTreeViewer.setLabelProvider( new LeftViewLabelProvider() );

            leftTreeViewer.addDoubleClickListener( new IDoubleClickListener()
            {

                @Override
                public void doubleClick( DoubleClickEvent event )
                {
                    ISelection selection = event.getSelection();
                    File file = (File) ( (ITreeSelection) selection ).getFirstElement();

                    if( file.isDirectory() )
                    {
                        return;
                    }

                    if( is62FileFound( file ) )
                    {
                        String[] paths = get62FilePaths( file );

                        compare( paths[0], paths[1], compareType62 );
                    }
                    else
                    {
                        MessageDialog.openInformation( Display.getDefault().getActiveShell(), "file not found",
                            "there is no such file in liferay 62" );
                    }
                }
            } );

            leftTreeViewer.setSorter( new ViewerSorter()
            {

                @Override
                public int category( Object element )
                {
                    File file = (File) element;

                    if( file.isDirectory() )
                    {
                        return -1;
                    }
                    else
                    {
                        return super.category( element );
                    }
                }
            } );
        }

        private void createRightPart( Composite parent )
        {
            ScrolledComposite rightContainer = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );

            Composite rightPart = SWTUtil.createComposite( rightContainer, 1, 0, GridData.FILL_BOTH );

            rightContainer.setLayout( new FillLayout() );
            rightContainer.setMinSize( 410, 200 );
            rightContainer.setExpandHorizontal( true );
            rightContainer.setExpandVertical( true );
            rightContainer.setContent( rightPart );

            Label rightLabel = new Label( rightPart, SWT.NONE );
            rightLabel.setText( "New JSP (double-click to compare with original)" );

            rightTreeViewer = new TreeViewer( rightPart, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );

            GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );

            rightTreeViewer.getTree().setLayoutData( gd );

            rightTreeViewer.setContentProvider( new ViewContentProvider() );
            rightTreeViewer.setLabelProvider( new RightViewLabelProvider() );

            rightTreeViewer.addDoubleClickListener( new IDoubleClickListener()
            {

                @Override
                public void doubleClick( DoubleClickEvent event )
                {
                    ISelection selection = event.getSelection();
                    File file = (File) ( (ITreeSelection) selection ).getFirstElement();

                    if( file.isDirectory() )
                    {
                        return;
                    }

                    if( is70FileFound( file ) )
                    {
                        String[] paths = get70FilePaths( file );

                        compare( paths[0], paths[1], compareType70 );
                    }
                    else
                    {
                        MessageDialog.openInformation( Display.getDefault().getActiveShell(), "file not found",
                            "there is no such file in liferay 7" );
                    }
                }
            } );

            rightTreeViewer.setSorter( new ViewerSorter()
            {

                @Override
                public int category( Object element )
                {
                    File file = (File) element;

                    if( file.isDirectory() )
                    {
                        return -1;
                    }
                    else
                    {
                        return super.category( element );
                    }
                }
            } );
        }

        private String[] get62FilePaths( File file )
        {
            String filePath = file.getAbsolutePath();
            IFile iFile = CoreUtil.getWorkspaceRoot().getFileForLocation( Path.fromOSString( filePath ) );

            IProject project = iFile.getProject();

            String projectPath = project.getLocation().toOSString();

            String customJsp = CustomJspConverter.getCustomJspPath( projectPath );

            java.nio.file.Path customJspPath =
                project.getFolder( "docroot/" + customJsp ).getLocation().toFile().toPath();

            java.nio.file.Path relativePath = customJspPath.relativize( file.toPath() );

            String[] paths = new String[2];

            paths[0] = null;
            paths[1] = filePath;

            File original62JspFile =
                new File( getLiferay62ServerLocation() + "/tomcat-7.0.42/webapps/ROOT/" + relativePath.toString() );

            if( original62JspFile.exists() )
            {
                paths[0] = original62JspFile.getAbsolutePath();
            }

            return paths;
        }

        private String[] get70FilePaths( File file )
        {
            String filePath = file.getAbsolutePath();

            IFile iFile = CoreUtil.getWorkspaceRoot().getFileForLocation( Path.fromOSString( filePath ) );

            IFolder resourceFolder = iFile.getProject().getFolder( staticPath );

            java.nio.file.Path resourcePath = resourceFolder.getLocation().toFile().toPath();

            java.nio.file.Path relativePath = resourcePath.relativize( file.toPath() );

            String[] paths = new String[2];

            paths[0] = null;
            paths[1] = filePath;

            IFile original70File = resourceFolder.getFile( "/.ignore/" + relativePath.toString() );

            if( original70File.exists() )
            {
                paths[0] = original70File.getLocation().toOSString();
            }

            return paths;
        }

        private List<IProject> getHookProjects()
        {
            List<IProject> results = new ArrayList<IProject>();
            IProject[] projects = CoreUtil.getAllProjects();

            for( IProject project : projects )
            {
                if( ProjectUtil.isHookProject( project ) )
                {
                    results.add( project );
                }
            }

            return results;
        }

        private File[] getLeftTreeInputs()
        {
            String[] results = CustomJspConverter.getConvertResult( CustomJspConverter.sourcePrefix );

            if( results == null )
            {
                return null;
            }

            int size = results.length;

            File[] files = new File[size];

            for( int i = 0; i < size; i++ )
            {
                String[] contents = results[i].split( ":" );

                IProject project = CoreUtil.getProject( contents[0] );

                String customJspPath = contents[1];

                files[i] = project.getFolder( "docroot/" + customJspPath ).getLocation().toFile();

            }

            return files;
        }

        private String getLiferay62ServerLocation()
        {
            return op().getLiferay62ServerLocation().content( true );
        }

        private IRuntime getLiferay70Runtime()
        {
            String serverName = op().getLiferayServerName().content();

            return ServerUtil.getServer( serverName ).getRuntime();
        }

        private File[] getRightTreeInputs()
        {
            String[] results = CustomJspConverter.getConvertResult( CustomJspConverter.resultPrefix );

            if( results == null )
            {
                return null;
            }

            int size = results.length;

            File[] files = new File[size];

            for( int i = 0; i < size; i++ )
            {
                File file = new File( results[i], staticPath );
                files[i] = file;
            }

            return files;
        }

        private boolean is62FileFound( File file )
        {
            String[] paths = get62FilePaths( file );

            if( paths[0] != null )
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        private boolean is70FileFound( File file )
        {
            String[] paths = get70FilePaths( file );

            if( paths[0] != null )
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        private CodeUpgradeOp op()
        {
            return getLocalModelElement().nearest( CodeUpgradeOp.class );
        }

        public void refreshTreeViews()
        {
            File[] leftInputs = getLeftTreeInputs();

            leftTreeViewer.setInput( leftInputs );

            File[] rightInputs = getRightTreeInputs();

            rightTreeViewer.setInput( rightInputs );
        }

        @Override
        public void render()
        {
            Composite container = new Composite( composite(), SWT.NONE );

            GridLayout gridLayout = new GridLayout( 1, true );

            container.setLayout( gridLayout );

            GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, true );
            layoutData.widthHint = 800;
            layoutData.heightHint = 600;

            container.setLayoutData( layoutData );

            Composite buttonContainer = new Composite( container, SWT.NONE );

            buttonContainer.setLayout( new GridLayout( 2, true ) );

            Button selectButton = SWTUtil.createButton( buttonContainer, "select projects" );

            GridData buttonGridData = new GridData( SWT.LEFT, SWT.LEFT, false, false );

            buttonGridData.widthHint = 130;
            buttonGridData.heightHint = 40;

            selectButton.setLayoutData( buttonGridData );

            Button clearButton = SWTUtil.createButton( buttonContainer, "clear results" );

            clearButton.setLayoutData( buttonGridData );

            selectButton.addSelectionListener( new SelectionAdapter()
            {

                @Override
                public void widgetSelected( SelectionEvent e )
                {
                    runConvertAction();
                }
            } );

            clearButton.addSelectionListener( new SelectionAdapter()
            {

                @Override
                public void widgetSelected( SelectionEvent e )
                {
                    CustomJspConverter.clearConvertResults();

                    refreshTreeViews();
                }
            } );

            SashForm sashForm = new SashForm( container, SWT.HORIZONTAL | SWT.H_SCROLL );

            sashForm.setLayout( new GridLayout( 1, false ) );

            GridData sashFormLayoutData = new GridData( GridData.FILL_BOTH );

            sashForm.setLayoutData( sashFormLayoutData );

            createChildren( sashForm );

            sashForm.setWeights( new int[] { 1, 1 } );

            refreshTreeViews();
        }

        private void runConvertAction()
        {
            CustomProjectSelectionDialog dialog = new CustomProjectSelectionDialog( UIUtil.getActiveShell() );

            dialog.setProjects( getHookProjects() );

            List<IProject> hookProjects = new ArrayList<>();

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
                            hookProjects.add( p.getProject() );
                        }
                    }
                }
            }

            int size = hookProjects.size();

            if( size < 1 )
            {
                return;
            }

            String[] sourcePaths = new String[size];

            for( int i = 0; i < size; i++ )
            {
                sourcePaths[i] = hookProjects.get( i ).getLocation().toOSString();
            }

            CustomJspConverter converter = new CustomJspConverter();

            IRuntime liferay70Runtime = getLiferay70Runtime();

            String liferay62ServerLocation = getLiferay62ServerLocation();

            if( liferay70Runtime == null || CoreUtil.isNullOrEmpty( liferay62ServerLocation ) )
            {
                MessageDialog.openError( Display.getDefault().getActiveShell(), "could not convert",
                    "countn't find liferay 70 or 62 server location" );

                return;
            }

            converter.setLiferay70Runtime( liferay70Runtime );
            //TODO 
            //converter.setUi( this );

            //converter.doExecute( sourcePaths );
        }
    }

    @Override
    public FormComponentPresentation createPresentation( SwtPresentation parent, Composite composite )
    {
        return new ConvertedProjectTreesPresentation( this, parent, composite );
    }
}
