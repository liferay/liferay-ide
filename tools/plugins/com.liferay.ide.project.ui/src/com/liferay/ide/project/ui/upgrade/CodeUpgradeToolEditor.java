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

import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.sapphire.Context;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.osgi.BundleBasedContext;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.def.EditorPageDef;
import org.eclipse.sapphire.ui.forms.swt.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import com.liferay.ide.project.ui.ProjectUI;

/**
 * @author Terry Jia
 * @author Lovett Li
 */
public class CodeUpgradeToolEditor extends SapphireEditor implements IExecutableExtension
{

    @Override
    protected IEditorPart createPage( Reference<EditorPageDef> definition )
    {
        IEditorPart part = super.createPage( definition );

        if( part instanceof MasterDetailsEditorPage )
        {
            MasterDetailsEditorPage mdPage = (MasterDetailsEditorPage) part;

            mdPage.getPart().state().getContentOutlineState().setVisible( false );

            mdPage.outline().setSelection( "Welcome" );
        }

        return part;
    }

    @Text( "Source" )
    private static LocalizableText sourcePageTitle;

    static
    {
        LocalizableText.init( CodeUpgradeToolEditor.class );
    }

    private ElementType type;
    private DefinitionLoader.Reference<EditorPageDef> definition;
    private StructuredTextEditor sourcePage;
    private Context context;
    private String sdef;
    private String pageName;
    private String pageDefinitionId;

    public CodeUpgradeToolEditor( final ElementType type, final DefinitionLoader.Reference<EditorPageDef> definition )
    {
        super();

        if( type == null )
        {
            throw new IllegalArgumentException();
        }

        this.type = type;
        this.definition = definition;
    }

    public CodeUpgradeToolEditor()
    {
    }

    @Override
    public void setInitializationData(
        final IConfigurationElement config, final String propertyName, final Object data )
    {
        super.setInitializationData( config, propertyName, data );

        if( this.definition == null )
        {
            final Map<?, ?> properties = (Map<?, ?>) data;

            this.context = BundleBasedContext.adapt( config.getContributor().getName() );
            this.sdef = (String) properties.get( "sdef" );
            this.pageName = (String) properties.get( "pageName" );
            this.pageDefinitionId = (String) properties.get( "pageDefinitionId" );
        }
    }

    @Override
    protected DefinitionLoader getDefinitionLoader()
    {
        return DefinitionLoader.context( this.context ).sdef( this.sdef );
    }

    @Override
    protected Reference<EditorPageDef> getDefinition( String id )
    {
        if( this.definition != null )
        {
            return this.definition;
        }
        else
        {
            return super.getDefinition( id );
        }
    }

    public final StructuredTextEditor getXmlEditor()
    {
        return this.sourcePage;
    }

    protected Element createModel()
    {
        ElementType type = this.type;

        if( type == null )
        {
            final EditorPageDef def = getDefinition( this.pageDefinitionId ).resolve();

            if( def == null )
            {
                throw new IllegalStateException();
            }

            final JavaType elementJavaType = def.getElementType().target();
            type = ElementType.read( (Class<?>) elementJavaType.artifact(), true );
        }

        final XmlEditorResourceStore store = createResourceStore( this.sourcePage );
        return type.instantiate( new RootXmlResource( store ) );
    }

    protected XmlEditorResourceStore createResourceStore( final StructuredTextEditor sourceEditor )
    {
        return new XmlEditorResourceStore( this, this.sourcePage );
    }

    @Override
    protected final void createSourcePages() throws PartInitException
    {
        this.sourcePage = new StructuredTextEditor();

        this.sourcePage.setEditorPart( this );

        final int index = addPage( this.sourcePage, getEditorInput() );

        setPageText( index, sourcePageTitle.text() );

        CTabItem item = ( (CTabFolder) getContainer() ).getItem( index );

        Control pageControl = item.getControl();

        item.dispose();

        if( pageControl != null )
        {
            pageControl.dispose();
        }
    }

    @Override
    protected void createFormPages() throws PartInitException
    {
        if( this.pageName == null )
        {
            final IEditorPart page = createPage( getDefinition( null ) );

            if( page instanceof IFormPage )
            {
                addPage( 0, (IFormPage) page );
            }
            else
            {
                addPage( 0, page, getEditorInput() );
            }
        }
        else
        {
            addDeferredPage( 0, this.pageName, this.pageDefinitionId );
        }
    }

    @Override
    public boolean isDirty()
    {
        return false;
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

    @Override
    public void dispose()
    {
        try
        {
            super.getModelElement().resource().save();
        }
        catch( ResourceStoreException e )
        {
            ProjectUI.logError( e );
        }

        super.dispose();

        this.type = null;
        this.definition = null;
        this.sourcePage = null;
    }

}
