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

package com.liferay.ide.layouttpl.ui.editor;

import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.ui.ILayoutTplUIPreferenceNames;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageEditorSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeIdForHTML;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings( { "restriction", "rawtypes" } )
public class LayoutTplMultiPageEditor extends MultiPageEditorPart implements ISelectionListener
{

    class PropertyListener implements IPropertyListener
    {
        public void propertyChanged( Object source, int propId )
        {
        }
    }

    class TextInputListener implements ITextInputListener
    {

        public void inputDocumentAboutToBeChanged( IDocument oldInput, IDocument newInput )
        {
            // do nothing
        }

        public void inputDocumentChanged( IDocument oldInput, IDocument newInput )
        {
            // if ((fDesignViewer != null) && (newInput != null)) {
            // fDesignViewer.setDocument(newInput);
            // }
        }
    }

    protected static final int SOURCE_PAGE_INDEX = 1;

    protected static final int VISUAL_PAGE_INDEX = 0;

    protected int lastPageIndex = -1;

    protected LayoutTplMultiOutlinePage multiOutlinePage;

    protected PropertyListener propertyListener;

    protected StructuredTextEditor sourceEditor;

    protected LayoutTplEditor visualEditor;

    public LayoutTplMultiPageEditor()
    {
        super();
    }

    @Override
    public void dispose()
    {
        super.dispose();

        getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener( this );
    }

    @Override
    public void doSave( IProgressMonitor monitor )
    {
        int activePage = getActivePage();

        if( activePage == VISUAL_PAGE_INDEX )
        {
            this.visualEditor.doSave( monitor );
        }

        sourceEditor.doSave( monitor );
    }

    @Override
    public void doSaveAs()
    {
        sourceEditor.doSaveAs();
    }

    @Override
    public Object getAdapter( Class adapter )
    {
        Object result = null;

        if( IContentOutlinePage.class.equals( adapter ) )
        {
            if( this.multiOutlinePage == null && sourceEditor != null && visualEditor != null )
            {
                // get outline pages for both source and visual editors
                IContentOutlinePage sourceOutlinePage = (IContentOutlinePage) sourceEditor.getAdapter( adapter );

                IContentOutlinePage visualOutlinePage = (IContentOutlinePage) visualEditor.getAdapter( adapter );

                if( sourceOutlinePage != null && visualOutlinePage != null )
                {
                    LayoutTplMultiOutlinePage outlinePage =
                                    new LayoutTplMultiOutlinePage( this, sourceOutlinePage, visualOutlinePage );
                    this.multiOutlinePage = outlinePage;
                }
            }

            return multiOutlinePage;
        }

        // we extend superclass, not override it, so allow it first
        // chance to satisfy request.
        result = super.getAdapter( adapter );

        if( result == null )
        {
            if( adapter.equals( IGotoMarker.class ) )
            {
                result = new IGotoMarker()
                {

                    public void gotoMarker( IMarker marker )
                    {
                        LayoutTplMultiPageEditor.this.gotoMarker( marker );
                    }
                };
            }
            else
            {
                /*
                 * DMW: I'm bullet-proofing this because its been reported (on very early versions) a null pointer
                 * sometimes happens here on startup, when an editor has been left open when workbench shutdown.
                 */
                if( sourceEditor != null )
                {
                    result = sourceEditor.getAdapter( adapter );
                }

                if( result == null && visualEditor != null )
                {
                    result = visualEditor.getAdapter( adapter );
                }
            }
        }
        return result;
    }

    public StructuredTextEditor getSourceEditor()
    {
        return sourceEditor;
    }

    public LayoutTplEditor getVisualEditor()
    {
        return visualEditor;
    }

    @Override
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.init( site, input );

        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener( this );
    }

    @Override
    public boolean isSaveAsAllowed()
    {
        return ( sourceEditor != null ) && sourceEditor.isSaveAsAllowed();
    }

    @Override
    public boolean isSaveOnCloseNeeded()
    {
        if( isDirty() )
        {
            return true;
        }

        // overriding super class since it does a lowly isDirty!
        if( sourceEditor != null )
        {
            return sourceEditor.isSaveOnCloseNeeded();
        }

        return isDirty();
    }

    public void selectionChanged( IWorkbenchPart part, ISelection selection )
    {
        if( this.equals( getSite().getPage().getActiveEditor() ) )
        {
            visualEditor.updateActions();
        }
    }

    protected void addSourcePage() throws PartInitException
    {
        int index = addPage( sourceEditor, getEditorInput() );
        setPageText( index, Msgs.source );

        firePropertyChange( PROP_TITLE );

        // Changes to the Text Viewer's document instance should also
        // force an
        // input refresh
        sourceEditor.getTextViewer().addTextInputListener( new TextInputListener() );
    }

    protected void connectVisualPage()
    {
        this.visualEditor.setInput( getEditorInput() );
    }

    protected void createAndAddVisualPage() throws PartInitException
    {
        IEditorPart editor = createVisualEditor();
        int index = addPage( editor, getEditorInput() );
        setPageText( index, Msgs.visual );
    }

    @Override
    protected void createPages()
    {
        try
        {
            createSourcePage(); // must create source page first
            createAndAddVisualPage();
            addSourcePage();
            connectVisualPage();

            IEditorActionBarContributor contributor = getEditorSite().getActionBarContributor();

            if( contributor instanceof MultiPageEditorActionBarContributor )
            {
                ( (MultiPageEditorActionBarContributor) contributor ).setActiveEditor( this );
            }

            int activePageIndex = getPreferenceStore().getInt( ILayoutTplUIPreferenceNames.LAST_ACTIVE_PAGE );
            if( ( activePageIndex >= 0 ) && ( activePageIndex < getPageCount() ) )
            {
                setActivePage( activePageIndex );
            }
            else
            {
                setActivePage( 0 );
            }
        }
        catch( PartInitException ex )
        {
            LayoutTplCore.logError( ex );
            throw new RuntimeException( ex );
        }
    }

    protected IEditorSite createSite( IEditorPart editor )
    {
        IEditorSite site = null;
        if( editor == sourceEditor )
        {
            site = new MultiPageEditorSite( this, editor )
            {

                /**
                 * @see org.eclipse.ui.part.MultiPageEditorSite#getActionBarContributor()
                 */
                public IEditorActionBarContributor getActionBarContributor()
                {
                    IEditorActionBarContributor contributor = super.getActionBarContributor();
                    IEditorActionBarContributor multiContributor =
                        LayoutTplMultiPageEditor.this.getEditorSite().getActionBarContributor();
                    if( multiContributor instanceof LayoutTplMultiPageEditorActionBarContributor )
                    {
                        contributor =
                            ( (LayoutTplMultiPageEditorActionBarContributor) multiContributor ).sourceEditorContributor;
                    }
                    return contributor;
                }

                public String getId()
                {
                    // sets this id so nested editor is considered xml source
                    // page
                    return ContentTypeIdForHTML.ContentTypeID_HTML + ".source"; //$NON-NLS-1$;
                }
            };
        }
        else
        {
            site = super.createSite( editor );
        }
        return site;
    }

    protected void createSourcePage() throws PartInitException
    {
        sourceEditor = new StructuredTextEditor();
        sourceEditor.setEditorPart( this );

        if( this.propertyListener == null )
        {
            this.propertyListener = new PropertyListener();
        }

        sourceEditor.addPropertyListener( this.propertyListener );
    }

    protected LayoutTplEditor createVisualEditor()
    {
        this.visualEditor = new LayoutTplEditor( this.sourceEditor );

        return this.visualEditor;
    }

    protected IPreferenceStore getPreferenceStore()
    {
        return LayoutTplUI.getDefault().getPreferenceStore();
    }

    @Override
    protected void pageChange( int newPageIndex )
    {
        if( lastPageIndex == VISUAL_PAGE_INDEX && newPageIndex == SOURCE_PAGE_INDEX )
        {
            if( this.visualEditor.isDirty() )
            {
                this.visualEditor.refreshSourceModel();
            }
        }
        else if( lastPageIndex == SOURCE_PAGE_INDEX && newPageIndex == VISUAL_PAGE_INDEX )
        {
            this.visualEditor.refreshVisualModel();
        }

        super.pageChange( newPageIndex );

        if( multiOutlinePage != null )
        {
            multiOutlinePage.refreshOutline();
        }

        lastPageIndex = newPageIndex;
    }

    @Override
    protected void setInput( IEditorInput input )
    {
        super.setInput( input );

        IFile file = ( (IFileEditorInput) input ).getFile();
        setPartName( file.getName() );
    }

    void gotoMarker( IMarker marker )
    {
        setActivePage( 1 );
        IDE.gotoMarker( sourceEditor, marker );
    }

    private static class Msgs extends NLS
    {
        public static String source;
        public static String visual;

        static
        {
            initializeMessages( LayoutTplMultiPageEditor.class.getName(), Msgs.class );
        }
    }
}
