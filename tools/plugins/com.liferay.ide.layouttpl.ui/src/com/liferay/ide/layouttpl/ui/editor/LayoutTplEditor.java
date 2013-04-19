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
 * Contributors:
 *      Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.editor;

import com.liferay.ide.layouttpl.core.model.LayoutTplDiagramElement;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.layouttpl.ui.action.LayoutTplEditorSelectAllAction;
import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagramUIFactory;
import com.liferay.ide.layouttpl.ui.parts.LayoutTplEditPartFactory;
import com.liferay.ide.layouttpl.ui.parts.LayoutTplRootEditPart;

import java.util.EventObject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.SelectAllAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class LayoutTplEditor extends GraphicalEditorWithFlyoutPalette
{
    protected static PaletteRoot PALETTE_MODEL;
    protected LayoutTplDiagramElement diagram;
    protected StructuredTextEditor sourceEditor;

    public LayoutTplEditor()
    {
        super();
        setEditDomain( new DefaultEditDomain( this ) );
    }

    public LayoutTplEditor( StructuredTextEditor sourceEditor )
    {
        this();
        this.sourceEditor = sourceEditor;
    }

    public void commandStackChanged( EventObject event )
    {
        firePropertyChange( IEditorPart.PROP_DIRTY );

        super.commandStackChanged( event );
    }

    @Override
    protected void createActions()
    {
        super.createActions();

        getActionRegistry().removeAction( getActionRegistry().getAction( ActionFactory.SELECT_ALL.getId() ) );
        SelectAllAction action = new LayoutTplEditorSelectAllAction( (LayoutTplEditor) this );
        getActionRegistry().registerAction(action);
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public void doSave( IProgressMonitor monitor )
    {
        // IFile file = ((IFileEditorInput) getEditorInput()).getFile();
        // try {
        // diagram.saveToFile(file, monitor);
        // getCommandStack().markSaveLocation();
        // }
        // catch (Exception e) {
        // LayoutTplUI.logError(e);
        // }

        try
        {
            refreshSourceModel();
            getCommandStack().markSaveLocation();
        }
        catch( Exception e )
        {
            LayoutTplUI.logError( e );
        }
    }

    public void doSaveAs()
    {
    }

    @Override
    public ActionRegistry getActionRegistry()
    {
        return super.getActionRegistry();
    }

    @SuppressWarnings( "rawtypes" )
    public Object getAdapter( Class type )
    {
        if( type == IContentOutlinePage.class )
        {
            return new LayoutTplOutlinePage( this, new TreeViewer() );
        }

        return super.getAdapter( type );
    }

    public LayoutTplDiagramElement getDiagram()
    {
        return diagram;
    }

    @Override
    public DefaultEditDomain getEditDomain()
    {
        return super.getEditDomain();
    }

    @Override
    protected FlyoutPreferences getPalettePreferences()
    {
        return FlyoutPaletteComposite.createFlyoutPreferences( LayoutTplUI.getDefault().getPluginPreferences() );
    }

    @Override
    public SelectionSynchronizer getSelectionSynchronizer()
    {
        return super.getSelectionSynchronizer();
    }

    public boolean isSaveAsAllowed()
    {
        return false;
    }

    public void refreshSourceModel()
    {
        IDOMModel domModel = getSourceModel();
        domModel.aboutToChangeModel();
        String name = getSourceFileName();
        String templateSource = LayoutTplUtil.getTemplateSource( diagram, name );
        domModel.getStructuredDocument().setText( this, templateSource );
        domModel.changedModel();
        domModel.releaseFromEdit();
        getCommandStack().markSaveLocation();
    }

    public void refreshVisualModel()
    {
        IDOMModel domModel = getSourceModel( true );

        if( domModel != null )
        {
            diagram = LayoutTplDiagramElement.createFromModel( domModel, LayoutTplDiagramUIFactory.INSTANCE );
            domModel.releaseFromRead();
        }
        else
        {
            diagram = LayoutTplDiagram.createDefaultDiagram();
        }

        final GraphicalViewer viewer = getGraphicalViewer();
        if( viewer != null )
        {
            viewer.setContents( diagram );
            refreshViewer( viewer );
        }
    }

    public void updateActions()
    {
        updateActions( this.getSelectionActions() );
    }

    private void refreshViewer( final GraphicalViewer viewer )
    {
        viewer.getControl().addPaintListener
        (
            new PaintListener()
            {
                public void paintControl( PaintEvent e )
                {
                    getGraphicalViewer().getContents().refresh();// rebuild column heights if needed
                    viewer.getControl().removePaintListener( this );
                }
            }
        );
    }

    @Override
    protected void configureGraphicalViewer()
    {
        super.configureGraphicalViewer();

        GraphicalViewer viewer = getGraphicalViewer();

        viewer.setEditPartFactory( new LayoutTplEditPartFactory() );

        // viewer.setRootEditPart(new ScalableRootEditPart());
        viewer.setRootEditPart( new LayoutTplRootEditPart() );
        viewer.setKeyHandler( new GraphicalViewerKeyHandler( viewer ) );

        // configure the context menu provider
        ContextMenuProvider cmProvider = new LayoutTplContextMenuProvider( viewer, getActionRegistry() );
        viewer.setContextMenu( cmProvider );
        getSite().registerContextMenu( cmProvider, viewer );
    }

    protected void createGraphicalViewer( Composite parent )
    {
        // GraphicalViewer viewer = new ScrollingGraphicalViewer();
        GraphicalViewer viewer = new GraphicalViewerImpl();
        viewer.createControl( parent );
        setGraphicalViewer( viewer );
        configureGraphicalViewer();
        hookGraphicalViewer();
        initializeGraphicalViewer();
    }

    protected PaletteViewerProvider createPaletteViewerProvider()
    {
        return new PaletteViewerProvider( getEditDomain() )
        {
            protected void configurePaletteViewer( PaletteViewer viewer )
            {
                super.configurePaletteViewer( viewer );
                viewer.setPaletteViewerPreferences( new LayoutTplPaletteViewerPreferences() );
                // FlyoutPreferences preferences = getPalettePreferences();
                // preferences.setPaletteState(FlyoutPaletteComposite.STATE_PINNED_OPEN);
                // create a drag source listener for this palette viewer
                // together with an appropriate transfer drop target listener,
                // this will enable
                // model element creation by dragging a
                // CombinatedTemplateCreationEntries
                // from the palette into the editor
                viewer.addDragSourceListener( new TemplateTransferDragSourceListener( viewer ) );
            }
        };
    }

    /**
     * Create a transfer drop target listener. When using a CombinedTemplateCreationEntry tool in the palette, this will
     * enable model element creation by dragging from the palette.
     *
     * @see #createPaletteViewerProvider()
     */
    protected TransferDropTargetListener createTransferDropTargetListener()
    {
        return new TemplateTransferDropTargetListener( getGraphicalViewer() )
        {
            protected CreationFactory getFactory( Object template )
            {
                if( template instanceof PortletLayoutTemplate )
                {
                    PortletLayoutTemplate portletLayoutTemplate = (PortletLayoutTemplate) template;

                    return new PortletLayoutFactory(
                        portletLayoutTemplate.getNumCols(), portletLayoutTemplate.getWeights() );
                }

                return new SimpleFactory( (Class<?>) template );
            }
        };
    }

    protected PaletteRoot getPaletteRoot()
    {
        if( PALETTE_MODEL == null )
        {
            PALETTE_MODEL = LayoutTplEditorPaletteFactory.createPalette();
        }

        return PALETTE_MODEL;
    }

    protected String getSourceFileName()
    {
        IEditorInput editorInput = getEditorInput();

        IPath path = null;

        if( editorInput instanceof IFileEditorInput )
        {
            path = ( (IFileEditorInput) editorInput ).getFile().getFullPath();
        }
        else if( editorInput instanceof FileStoreEditorInput )
        {
            path = new Path( ( (FileStoreEditorInput) editorInput ).getURI().getPath() );
        }

        if( path == null )
        {
            return editorInput.getName();
        }

        return path.removeFileExtension().lastSegment();
    }

    protected IDOMModel getSourceModel()
    {
        return getSourceModel( false );
    }

    protected IDOMModel getSourceModel( boolean readOnly )
    {
        IDOMModel domModel = null;

        if( this.sourceEditor != null && this.sourceEditor.getDocumentProvider() != null )
        {
            IDocumentProvider documentProvider = this.sourceEditor.getDocumentProvider();
            IDocument doc = documentProvider.getDocument( getEditorInput() );
            IStructuredModel model;

            if( readOnly )
            {
                model = StructuredModelManager.getModelManager().getExistingModelForRead( doc );
            }
            else
            {
                model = StructuredModelManager.getModelManager().getExistingModelForEdit( doc );
            }

            if( model instanceof IDOMModel )
            {
                domModel = (IDOMModel) model;
            }
        }

        return domModel;
    }

    protected void initializeGraphicalViewer()
    {
        super.initializeGraphicalViewer();
        final GraphicalViewer viewer = getGraphicalViewer();
        viewer.setContents( getDiagram() ); // set the contents of this editor
        refreshViewer( viewer );

        // listen for dropped parts
        viewer.addDropTargetListener( createTransferDropTargetListener() );
    }

    protected void setInput( IEditorInput input )
    {
        super.setInput( input );

        refreshVisualModel();

        setPartName( input.getName() );

        // try {
        // IFile file = ((IFileEditorInput) input).getFile();
        // setPartName(file.getName());
        // diagram = LayoutTplDiagram.createFromFile(file);
        // }
        // catch (Exception e) {
        // LayoutTplUI.logError(e);
        // }
    }

}
