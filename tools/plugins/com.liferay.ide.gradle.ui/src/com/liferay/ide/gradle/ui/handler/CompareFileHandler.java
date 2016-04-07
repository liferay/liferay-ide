package com.liferay.ide.gradle.ui.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.liferay.ide.gradle.core.GradleCore;

/**
 * @author Lovett Li
 */
public class CompareFileHandler extends AbstractHandler
{

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked( event );
        final ISelection selection = HandlerUtil.getActiveMenuSelection( event );

        IStatus retval = Status.OK_STATUS;
        IFile currentFile = null;

        if( selection instanceof ITreeSelection )
        {
            Object firstElement = ( (ITreeSelection) selection ).getFirstElement();

            if( firstElement instanceof IFile )
            {
                currentFile = (IFile) firstElement;
            }
        }
        else if( selection instanceof TextSelection )
        {
            IEditorPart editor = window.getActivePage().getActiveEditor();
            currentFile = editor.getEditorInput().getAdapter( IFile.class );
        }

        retval = openCompareEditor( window, currentFile );

        return retval;
    }

    private IStatus openCompareEditor( IWorkbenchWindow window, IFile currentFile )
    {
        final IWorkbenchPage workBenchPage = window.getActivePage();

        ITypedElement left = null;
        ITypedElement right = null;
        IStatus retval = Status.OK_STATUS;

        try
        {
            left = new CompareItem( getTemplateFile( currentFile ) );
            right = new CompareItem( currentFile.getLocation().toFile() );
            openInCompare( left, right, workBenchPage );
        }
        catch( Exception e )
        {
            retval = GradleCore.createErrorStatus( e );
        }
        return retval;
    }

    private void openInCompare( final ITypedElement left, final ITypedElement right, IWorkbenchPage workBenchPage )
    {
        final CompareConfiguration configuration = new CompareConfiguration();
        configuration.setLeftLabel( "Template" );
        configuration.setRightLabel( ( (CompareItem) right ).getFile().getAbsolutePath() );

        CompareUI.openCompareEditor( new CompareEditorInput( configuration)
        {

            @Override
            protected Object prepareInput( final IProgressMonitor monitor )
                throws InvocationTargetException, InterruptedException
            {
                DiffNode diffNode = new DiffNode( left, right );
                return diffNode;
            }

        } );

    }

    private File getTemplateFile( IFile currentFile ) throws Exception
    {
        final IProject currentProject = currentFile.getProject();
        final IFile bndfile = currentProject.getFile( "bnd.bnd" );

        File templateFile = null;

        final BufferedReader reader = new BufferedReader( new InputStreamReader( bndfile.getContents() ) );
        String fragName;

        while( ( fragName = reader.readLine() ) != null )
        {
            if( fragName.startsWith( "Fragment-Host:" ) )
            {
                fragName = fragName.substring( fragName.indexOf( ":" ) + 1, fragName.indexOf( ";" ) ).trim();
                break;
            }
        }

        final String hookfolder = currentFile.getFullPath().toOSString().substring(
            currentFile.getFullPath().toOSString().lastIndexOf( "META-INF" ) );
        final IPath templateLocation =
            GradleCore.getDefault().getStateLocation().append( fragName ).append( hookfolder );

        templateFile = new File( templateLocation.toOSString() );

        if( !templateFile.exists() )
        {
            throw new FileNotFoundException( "Template not find." );
        }

        return templateFile;
    }

    private class CompareItem implements ITypedElement,IStreamContentAccessor,IModificationDate,IEditableContent
    {
        private File file;

        public CompareItem( File file )
        {
            this.file = file;
        }

        public File getFile()
        {
            return file;
        }

        @Override
        public String getName()
        {
            return null;
        }

        @Override
        public Image getImage()
        {
            return null;
        }

        @Override
        public String getType()
        {
            return null;
        }

        @Override
        public long getModificationDate()
        {
            return 0;
        }

        @Override
        public InputStream getContents() throws CoreException
        {
            try
            {
                return new FileInputStream( file );
            }
            catch( FileNotFoundException e )
            {
            }
            return null;
        }

        @Override
        public boolean isEditable()
        {
            return false;
        }

        @Override
        public void setContent( byte[] newContent )
        {
        }

        @Override
        public ITypedElement replace( ITypedElement dest, ITypedElement src )
        {
            return null;
        }
    }

}
