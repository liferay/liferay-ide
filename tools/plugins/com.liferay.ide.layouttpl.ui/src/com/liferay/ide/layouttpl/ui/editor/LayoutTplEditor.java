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
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.editor;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.LayoutTplElementsFactory;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.sapphire.Context;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.osgi.BundleBasedContext;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.EditorPageDef;
import org.eclipse.sapphire.ui.forms.FormEditorPageDef;
import org.eclipse.sapphire.ui.forms.MasterDetailsEditorPageDef;
import org.eclipse.sapphire.ui.forms.swt.FormEditorPage;
import org.eclipse.sapphire.ui.forms.swt.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.forms.swt.SapphireEditorFormPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.osgi.framework.Version;


/**
 * @author Kuo Zhang
 * 
 */
@SuppressWarnings( "restriction" )
public class LayoutTplEditor extends SapphireEditor implements IExecutableExtension
{

    private static final int SOURCE_PAGE_INDEX = 0;
    private static final int PREVIEW_PAGE_INDEX = 1;
    private static final int DESIGN_PAGE_INDEX = 2;

    private static final String SOURCE_PAGE_TITLE = "Source";
    private static final String PREVIEW_PAGE_TITLE = "Preview";
    private static final String DESING_PAGE_TITLE = "Design";

    private StructuredTextEditor sourcePage;
    private SapphireEditorFormPage designPage;
    private LayoutTplPreviewEditor previewPage;

    private DefinitionLoader.Reference<EditorPageDef> definition;
    private IDOMModel sourceModel;

    private boolean isDesignPageChanged;
    private boolean isSourceModelChanged;
    private boolean isBootstrapStyle;

    public LayoutTplEditor()
    {
        this( LayoutTplElement.TYPE, DefinitionLoader.sdef( LayoutTplEditor.class ).page( "designPage" ) );
    }

    public LayoutTplEditor( final ElementType type, final DefinitionLoader.Reference<EditorPageDef> definition )
    {
        if( type == null )
        {
            throw new IllegalArgumentException();
        }

        if( definition == null )
        {
            throw new IllegalArgumentException();
        }

        this.definition = definition;
    }

    @Override
    protected void createDiagramPages() throws PartInitException
    {
        Element element = getModelElement();

        if( element instanceof LayoutTplElement )
        {
            this.previewPage = new LayoutTplPreviewEditor( (LayoutTplElement) element );
        }

        addPage( PREVIEW_PAGE_INDEX, this.previewPage, getEditorInput() );
        setPageText( PREVIEW_PAGE_INDEX, PREVIEW_PAGE_TITLE );
    }

    protected LayoutTplElement createEmptyDiagramModel()
    {
        LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( isBootstrapStyle() );
        layoutTpl.setClassName( getEditorInput().getName().replaceAll( "\\..*$", "" ) );

        return layoutTpl;
    }

    @Override
    protected void createFormPages() throws PartInitException
    {
        final EditorPageDef def = this.definition.resolve();

        if( def instanceof MasterDetailsEditorPageDef )
        {
            this.designPage = new MasterDetailsEditorPage( this, getModelElement(), this.definition );
        }
        else if( def instanceof FormEditorPageDef )
        {
            this.designPage = new FormEditorPage( this, getModelElement(), this.definition );
        }

        addPage( DESIGN_PAGE_INDEX, this.designPage );
        setPageText( DESIGN_PAGE_INDEX, DESING_PAGE_TITLE );
    }

    @Override
    protected Element createModel()
    {
        final IFile file = getFile();

        isBootstrapStyle = isBootstrapStyle();

        LayoutTplElement layoutTpl =
            LayoutTplElementsFactory.INSTANCE.newLayoutTplFromFile( file, isBootstrapStyle ); 

        if( layoutTpl == null )
        {
            layoutTpl = createEmptyDiagramModel();
        }

        layoutTpl.attach( new Listener()
        {
            @Override
            public void handle( Event event )
            {
                setDesignPageChanged( true );
                firePropertyChange( PROP_DIRTY );
            }

        }, "*" );

        return layoutTpl; 
    }

    @Override
    protected void createSourcePages() throws PartInitException
    {
        this.sourcePage = new StructuredTextEditor();
        this.sourcePage.setEditorPart( this );

        addPage( SOURCE_PAGE_INDEX, this.sourcePage, getEditorInput() );
        setPageText( SOURCE_PAGE_INDEX, SOURCE_PAGE_TITLE );

        initSourceModel();
    }

    @Override
    public void dispose()
    {
        super.dispose();

        this.definition = null;
        this.sourcePage = null;
        this.designPage = null;
        this.previewPage = null;

        if( this.sourceModel != null )
        {
            sourceModel.releaseFromEdit();
        }
    }

    @Override
    public void doSave( final IProgressMonitor monitor )
    {
        final int activePage = getActivePage();

        if( activePage == PREVIEW_PAGE_INDEX )
        {
            if( this.sourcePage.isDirty() )
            {
                this.sourcePage.doSave( monitor );
            }
        }
        else if( activePage == SOURCE_PAGE_INDEX )
        {
            if( this.sourcePage.isDirty() )
            {
                this.sourcePage.doSave( monitor );
                refreshDiagramModel();
            }
        }
        else if( activePage == DESIGN_PAGE_INDEX ) 
        {
            if( isDesignPageChanged )
            {
                refreshSourceModel();
                this.sourcePage.doSave( monitor );
            }
        }

        setSourceModelChanged( false );
        setDesignPageChanged( false );

        firePropertyChange( PROP_DIRTY );
    }

    @Override
    public IContentOutlinePage getContentOutline( final Object page )
    {
        if( page == this.sourcePage )
        {
            return (IContentOutlinePage) this.sourcePage.getAdapter( IContentOutlinePage.class );
        }

        return super.getContentOutline( page );
    }

    protected void initSourceModel()
    {
        if( sourceModel == null )
        {
            if( this.sourcePage != null && this.sourcePage.getDocumentProvider() != null )
            {
                final IDocumentProvider documentProvider = this.sourcePage.getDocumentProvider();
                final IDocument doc = documentProvider.getDocument( getEditorInput() );

                sourceModel  =(IDOMModel) StructuredModelManager.getModelManager().getExistingModelForEdit( doc );

                sourceModel.addModelStateListener( new IModelStateListener()
                {

                    public void modelAboutToBeChanged( IStructuredModel model ){}

                    public void modelAboutToBeReinitialized( IStructuredModel structuredModel ){}

                    public void modelChanged( IStructuredModel model )
                    {
                        setSourceModelChanged( true );
                    }

                    public void modelDirtyStateChanged( IStructuredModel model, boolean isDirty ){}

                    public void modelReinitialized( IStructuredModel structuredModel ){}

                    public void modelResourceDeleted( IStructuredModel model ){}

                    public void modelResourceMoved( IStructuredModel oldModel, IStructuredModel newModel ){}
                } );
            }
        }
    }

    private boolean isBootstrapStyle()
    {
        boolean retval = true;

        try
        {
            final Version version = new Version( LiferayCore.create( getFile().getProject() ).getPortalVersion() );

            if( CoreUtil.compareVersions( version,ILiferayConstants.V620 ) < 0 )
            {
                retval = false;
            }
        }
        catch( Exception e )
        {
        }

        return retval;
    }

    @Override
    public boolean isDirty()
    {
        return isDesignPageChanged || this.sourcePage.isDirty(); 
    }

    @Override
    protected void pageChange( int pageIndex )
    {
        int lastActivePage = getLastActivePage();

        if( lastActivePage == SOURCE_PAGE_INDEX && pageIndex == PREVIEW_PAGE_INDEX )
        {
            // if the source page is dirty, but the model didn't get changed,
            // then don't refresh the model element 
            if( this.sourcePage.isDirty() && isSourceModelChanged )
            {
                refreshDiagramModel();
            }

            refreshPreviewPage();
        }

        if( lastActivePage == SOURCE_PAGE_INDEX && pageIndex == DESIGN_PAGE_INDEX )
        {
            if( this.sourcePage.isDirty() && isSourceModelChanged )
            {
                refreshDiagramModel();
            }
        }

        if( lastActivePage == DESIGN_PAGE_INDEX && pageIndex == SOURCE_PAGE_INDEX )
        {
            if( isDesignPageChanged )
            {
                refreshSourceModel();
            }
        }

        if( lastActivePage == DESIGN_PAGE_INDEX && pageIndex == PREVIEW_PAGE_INDEX )
        {
            if( isDesignPageChanged )
            {
                refreshSourceModel();
            }

            refreshPreviewPage();
        }

        super.pageChange( pageIndex );
    }

    protected void refreshDiagramModel()
    {
        LayoutTplElement newElement = LayoutTplElementsFactory.INSTANCE.newLayoutTplFromFile( getFile(), isBootstrapStyle );

        if( newElement == null )
        {
            // create an empty model for diagram in memory, but not write to source
            newElement = createEmptyDiagramModel();
        }

        Element model = getModelElement();
        model.clear();
        model.copy( newElement );
    }

    protected void refreshPreviewPage()
    {
        this.previewPage.refreshVisualModel( (LayoutTplElement) getModelElement() );
    }

    protected void refreshSourceModel()
    {
        refreshSourceModel( (LayoutTplElement) getModelElement() );
    }

    protected void refreshSourceModel( LayoutTplElement modelElement )
    {
        if( this.sourceModel != null )
        {
            final String templateSource = LayoutTplUtil.getTemplateSource( modelElement );

            sourceModel.aboutToChangeModel();
            sourceModel.getStructuredDocument().setText( this, templateSource );
            sourceModel.changedModel();
        }

        setSourceModelChanged( false );
    }

    @Override
    protected void setActivePage( int pageIndex )
    {
        super.setActivePage( SOURCE_PAGE_INDEX );
    }

    protected void setDesignPageChanged( boolean changed )
    {
        isDesignPageChanged = changed;
    }

    @Override
    public void setInitializationData( final IConfigurationElement config,
                                       final String propertyName,
                                       final Object data )
    {
        super.setInitializationData( config, propertyName, data );

        if( this.definition == null )
        {
            final String bundleId = config.getContributor().getName();
            final Context context = BundleBasedContext.adapt( bundleId );
            final Map<?,?> properties = (Map<?,?>) data;

            final String sdef = (String) properties.get( "sdef" );
            this.definition = DefinitionLoader.context( context ).sdef( sdef ).page();
        }
    }

    protected void setSourceModelChanged( boolean changed )
    {
        this.isSourceModelChanged = changed; 
    }
}
