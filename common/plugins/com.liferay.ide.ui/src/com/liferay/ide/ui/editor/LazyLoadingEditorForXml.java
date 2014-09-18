package com.liferay.ide.ui.editor;

import com.liferay.ide.ui.LiferayUIPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.LoggingService;
import org.eclipse.sapphire.Sapphire;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.def.EditorPageDef;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;


/**
 * @author Gregory Amerson
 */
public class LazyLoadingEditorForXml extends SapphireEditorForXml
{
    private boolean loaded = false;
    private boolean loading;

    public LazyLoadingEditorForXml( ElementType type, Reference<EditorPageDef> page )
    {
        super( type, page );
    }

    public int addPage( final IEditorPart page, final IEditorInput input ) throws PartInitException
    {
        int index = super.addPage( page, input );
        setPageText( index, page.getTitle() );

        return index;
    }

    public void addPage( int index, IFormPage page ) throws PartInitException
    {
        index = getOffset();

        super.addPage( index, page.getPartControl() );
        configurePage( index, page );
    }

    @Override
    public void doSave( IProgressMonitor monitor )
    {
        if( loaded )
        {
            super.doSave( monitor );
        }
        else
        {
            getEditor( 0 ).doSave( monitor );
        }
    }

    @Override
    public void doSaveAs()
    {
        if( loaded )
        {
            super.doSaveAs();
        }
        else
        {
            getEditor( 0 ).doSaveAs();
        }
    }

    public int getOffset()
    {
        int retval = 1; // source page

        if( shouldCreateDiagramPages() )
        {
            retval++;
        }

        if( shouldCreateFormPages() )
        {
            retval++;
        }

        return retval;
    }

    @Override
    protected void addPages()
    {
        initActionBars();

        String error = checkFile();

        if( error == null )
        {
            try
            {
                createSourcePages();
            }
            catch( PartInitException e )
            {
                Sapphire.service( LoggingService.class ).log( e );
            }

            // setup dummy pages for diagram and form pages

            if( shouldCreateFormPages() )
            {
                final Composite comp = new Composite( getContainer(), SWT.NONE );
                setPageText( addPage( comp ), "Overview" );
            }

            if( shouldCreateDiagramPages() )
            {
                final Composite comp = new Composite( getContainer(), SWT.NONE );
                setPageText( addPage( comp ), "Diagram" );
            }

            setActivePage( getLastActivePage() );
        }
    }

    @Override
    protected void pageChange( int pageIndex )
    {
        if( pageIndex > 0 && !loaded && !loading )
        {
            loading = true;

            String error = setupModel();

            if( error == null )
            {
                try
                {
                    adaptModel( getModelElement() );

                    if( shouldCreateDiagramPages() )
                    {
                        createDiagramPages();

                        setActivePage( getOffset() );
                    }

                    if( shouldCreateFormPages() )
                    {
                        createFormPages();

                        setActivePage( getOffset() + ( shouldCreateDiagramPages() ? 1 : 0 ) );
                    }

                    if( shouldCreateDiagramPages() )
                    {
                        removePage( 1 );
                    }

                    if( shouldCreateFormPages() )
                    {
                        removePage( 1 );
                    }

                    createFileChangeListener();

                    setActivePage( pageIndex );
                }
                catch( Exception e )
                {
                    LiferayUIPlugin.logError( "Error loading additional editor pages", e );
                }
                finally
                {
                    loading = false;
                }
            }

            loading = false;
            loaded = true;
        }

        if( loading )
        {
            return;
        }

        super.pageChange( pageIndex );
    }

    protected boolean shouldCreateDiagramPages()
    {
        return false;
    }

    protected boolean shouldCreateFormPages()
    {
        return false;
    }

}