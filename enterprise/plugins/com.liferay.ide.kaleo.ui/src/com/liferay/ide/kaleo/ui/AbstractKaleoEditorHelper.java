/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.ui.editor.HiddenFileEditorInput;
import com.liferay.ide.kaleo.ui.editor.ScriptPropertyEditorInput;
import com.liferay.ide.kaleo.core.model.Node;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractKaleoEditorHelper implements IKaleoEditorHelper
{
    private String contributorName;
    private String editorId;
    private String fileExtension;
    private String languageType;

    public IEditorPart createEditorPart( ScriptPropertyEditorInput editorInput, IEditorSite editorSite )
    {
        IEditorPart editorPart = null;

        try
        {
            editorPart = new TextEditor();
            editorPart.init( editorSite, editorInput );
        }
        catch( PartInitException e )
        {
            KaleoUI.logError( "Could not create default script editor.", e );
        }

        return editorPart;
    }

    public String getContributorName()
    {
        return contributorName;
    }

    public String getEditorId()
    {
        return editorId;
    }

    public String getFileExtension()
    {
        return fileExtension;
    }

    private HiddenFileEditorInput getHiddenFileEditorInput(
        IProject project, String name, String nodeName, String fileContents )
    {
        return new HiddenFileEditorInput( getTemporaryFile( project, name, nodeName, getFileExtension(), fileContents ) );
    }

    public String getLanguageType()
    {
        return this.languageType;
    }

    private IFile getTemporaryFile(
        IProject project, String name, String nodeName, String fileExtension, String fileContents )
    {
        if( empty( fileContents ) )
        {
            fileContents = "";
        }

        IPath tempScriptFilePath = getTempScriptFilePath( project, name, nodeName, fileExtension );

        IFile tempFile = ResourcesPlugin.getWorkspace().getRoot().getFile( tempScriptFilePath );

        try
        {
            final ByteArrayInputStream source = new ByteArrayInputStream( fileContents.getBytes() );

            if( tempFile.exists() )
            {
                tempFile.setContents( source, true, false, null );
            }
            else
            {
                tempFile.create( source, true, null );
            }
        }
        catch( CoreException e )
        {
            e.printStackTrace();
        }

        return tempFile;
    }

    private String getTempScriptFileName( String name, String nodeName, String fileExtension )
    {
        StringBuilder retval = new StringBuilder( /*"."*/ );

        if( !empty( nodeName ))
        {
            retval.append( nodeName.replaceAll( "![A-Za-z]+", "" ).replaceAll( "\\s+", "" ) );
        }
        else if (!empty( name ) )
        {
            retval.append( name.replaceAll( "![A-Za-z]+", "" ) );
        }

        retval.append( "." ).append( fileExtension );

        return retval.toString();
    }

    private IPath getTempScriptFilePath( IProject project, String name, String nodeName, String fileExtension )
    {
        IPath retval = null;

        IContainer folder = null;

        Path tempPath = new Path(KALEO_TEMP_PREFIX + System.currentTimeMillis() + "/" + getTempScriptFileName( name, nodeName, fileExtension ) );

        if( project != null && tempPath != null)
        {
            IFolder[] folders = CoreUtil.getSourceFolders( JavaCore.create( project ) ).toArray( new IFolder[0] );

            if( !empty( folders ) )
            {
                folder = folders[0];;

            }
            else
            {
                folder = project;
            }
        }

        if (folder != null)
        {
            // create a temporary folder that will contain the script
            final IFile tempFile = folder.getFile( tempPath );

            try
            {
                CoreUtil.makeFolders( (IFolder) tempFile.getParent() );
                retval = tempFile.getFullPath();
            }
            catch( CoreException e )
            {
            }
        }

        return retval;
    }

    public void handleDropFromPalette( IEditorPart activeEditor )
    {
        // default do nothing
    }

    public void openEditor( ISapphirePart sapphirePart, final Element modelElement, final ValueProperty valueProperty )
    {
        try
        {
            Object content = modelElement.property( valueProperty ).content();

            if( content == null )
            {
                content = "";
            }

            final IProject project = sapphirePart.adapt( IProject.class );

            final IEditorInput editorInput = modelElement.adapt( IEditorInput.class );

            final String name = editorInput.getName();

            final String nodeName = modelElement.nearest( Node.class ).getName().content();

            final HiddenFileEditorInput hiddenFileEditorInput =
                getHiddenFileEditorInput( project, name, nodeName, content.toString() );

            final IEditorSite editorSite = sapphirePart.adapt( IEditorSite.class );

            final IEditorPart editorPart =
                editorSite.getWorkbenchWindow().getActivePage().openEditor( hiddenFileEditorInput, this.editorId );

            final ITextEditor textEditor = (ITextEditor) editorPart.getAdapter( ITextEditor.class );

            textEditor.getDocumentProvider().getDocument( hiddenFileEditorInput ).addDocumentListener
            (
                new IDocumentListener()
                {
                    public void documentAboutToBeChanged( DocumentEvent event )
                    {
                    }

                    public void documentChanged( DocumentEvent event )
                    {
                        String contents = event.getDocument().get();

                        modelElement.property( valueProperty ).write( contents );
                    }
                }
            );

            editorPart.getSite().getPage().addPartListener
            (
                new IPartListener()
                {
                    public void partActivated( IWorkbenchPart part )
                    {
                    }

                    public void partBroughtToTop( IWorkbenchPart part )
                    {
                    }

                    public void partClosed( IWorkbenchPart part )
                    {
                        if( part != null && part.equals( editorPart ) )
                        {
                            new WorkspaceJob( "delete temp editor file" )
                            {
                                @Override
                                public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
                                {
                                    try
                                    {
                                        hiddenFileEditorInput.getFile().getParent().delete( true, null );
                                    }
                                    catch( CoreException e )
                                    {
                                    }

                                    return Status.OK_STATUS;
                                }
                            }.schedule( 100 );
                        }
                    }

                    public void partDeactivated( IWorkbenchPart part )
                    {
                    }

                    public void partOpened( IWorkbenchPart part )
                    {
                    }
                }
            );
        }
        catch( Exception e )
        {
            KaleoUI.logError( "Error opening editor.", e );
        }
    }

    public void setContributorName( String contributorName )
    {
        this.contributorName = contributorName;
    }

    public void setEditorId( String editorId )
    {
        this.editorId = editorId;
    }

    public void setFileExtension( String fileExtension )
    {
        this.fileExtension = fileExtension;
    }

    public void setLanguageType( String langauge )
    {
        this.languageType = langauge;
    }
}
