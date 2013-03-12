/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.layouttpl.ui.model;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.layouttpl.ui.util.LayoutTplUtil;
import com.liferay.ide.templates.core.ITemplateContext;
import com.liferay.ide.templates.core.ITemplateOperation;
import com.liferay.ide.templates.core.TemplatesCore;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class LayoutTplDiagram extends ModelElement implements PropertyChangeListener
{
    public static final String ROW_ADDED_PROP = "LayoutTplDiagram.RowAdded"; //$NON-NLS-1$
    public static final String ROW_REMOVED_PROP = "LayoutTplDiagram.RowRemoved"; //$NON-NLS-1$

    public static LayoutTplDiagram createDefaultDiagram()
    {
        return new LayoutTplDiagram();
    }

    public static LayoutTplDiagram createFromFile( IFile file )
    {
        if( file == null || !( file.exists() ) )
        {
            return null;
        }

        LayoutTplDiagram model = null;

        try
        {
            IDOMModel domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( file );
            model = createFromModel( domModel );
        }
        catch( Exception e )
        {
            LayoutTplUI.logError( "Unable to read layout template file " + file.getName(), e ); //$NON-NLS-1$
            model = new LayoutTplDiagram();
        }

        return model;
    }

    public static LayoutTplDiagram createFromModel( IDOMModel model )
    {
        if( model == null )
        {
            return null;
        }

        // look for element that is a div with id of "main-content"
        LayoutTplDiagram newDiagram = null;
        IDOMDocument rootDocument = model.getDocument();
        IDOMElement mainContentElement = LayoutTplUtil.findMainContentElement( rootDocument );

        newDiagram = new LayoutTplDiagram();
        newDiagram.setId( "main-content" ); //$NON-NLS-1$

        if( mainContentElement != null )
        {
            newDiagram.setRole( LayoutTplUtil.getRoleValue( mainContentElement, "main" ) ); //$NON-NLS-1$

            IDOMElement[] portletLayoutElements =
                LayoutTplUtil.findChildElementsByClassName( mainContentElement, "div", "portlet-layout" ); //$NON-NLS-1$ //$NON-NLS-2$

            if( !CoreUtil.isNullOrEmpty( portletLayoutElements ) )
            {
                for( IDOMElement portletLayoutElement : portletLayoutElements )
                {
                    PortletLayout newPortletLayout = PortletLayout.createFromElement( portletLayoutElement );
                    newDiagram.addRow( newPortletLayout );
                }
            }
        }
        else
        {
            newDiagram.setRole( "main" ); //$NON-NLS-1$
        }

        return newDiagram;
    }

    protected String id;
    protected String role;
    protected List<ModelElement> rows = new ArrayList<ModelElement>();

    public LayoutTplDiagram()
    {
        super();

        this.id = "main-content"; //$NON-NLS-1$
        this.role = "main"; //$NON-NLS-1$
    }

    public void addRow( PortletLayout newRow )
    {
        addRow( newRow, -1 );
    }

    public boolean addRow( PortletLayout newRow, int index )
    {
        if( newRow != null )
        {
            if( index < 0 )
            {
                rows.add( newRow );
            }
            else
            {
                rows.add( index, newRow );
            }

            newRow.setParent( this );
            newRow.addPropertyChangeListener( this );

            this.updateColumns();
            this.firePropertyChange( ROW_ADDED_PROP, null, newRow );

            return true;
        }

        return false;
    }

    public String getId()
    {
        return id;
    }

    public String getRole()
    {
        return role;
    }

    public List<ModelElement> getRows()
    {
        return rows;
    }

    public void propertyChange( PropertyChangeEvent evt )
    {
        String prop = evt.getPropertyName();

        if( PortletLayout.COLUMN_ADDED_PROP.equals( prop ) || PortletLayout.COLUMN_REMOVED_PROP.equals( prop ) )
        {
            updateColumns();
        }
    }

    @Override
    public void removeChild( ModelElement child )
    {
        if( rows.contains( child ) )
        {
            removeRow( (PortletLayout) child );
        }
    }

    public boolean removeRow( PortletLayout existingRow )
    {
        if( existingRow != null && rows.remove( existingRow ) )
        {
            firePropertyChange( ROW_REMOVED_PROP, null, existingRow );

            return true;
        }

        return false;
    }

    public void saveToFile( IFile file, IProgressMonitor monitor )
    {
        ITemplateOperation templateOperation = TemplatesCore.getTemplateOperation( "layouttpl.tpl" ); //$NON-NLS-1$
        templateOperation.setOutputFile( file );

        try
        {
            ITemplateContext ctx = templateOperation.getContext();
            ctx.put( "root", this ); //$NON-NLS-1$
            String name = file.getFullPath().removeFileExtension().lastSegment();
            ctx.put( "templateName", name ); //$NON-NLS-1$
            templateOperation.execute( monitor );
        }
        catch( Exception e )
        {
            LayoutTplUI.logError( e );
        }
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public void setRole( String role )
    {
        this.role = role;
    }

    protected void updateColumns()
    {
        int numIdCount = 1;

        for( ModelElement row : rows )
        {
            List<ModelElement> cols = ( (PortletLayout) row ).getColumns();

            for( int i = 0; i < cols.size(); i++ )
            {
                PortletColumn col = ( (PortletColumn) cols.get( i ) );
                col.setNumId( numIdCount++ );

                if( i == 0 && cols.size() > 1 )
                {
                    col.setFirst( true );
                }
                else if( cols.size() > 1 && i == ( cols.size() - 1 ) )
                {
                    col.setLast( true );
                }
            }
        }
    }

    public String getTemplateSource( String templateName )
    {
        ITemplateOperation templateOperation = TemplatesCore.getTemplateOperation( "layouttpl.tpl" ); //$NON-NLS-1$
        StringBuffer buffer = new StringBuffer();
        templateOperation.setOutputBuffer( buffer );
        templateOperation.getContext().put( "root", this ); //$NON-NLS-1$
        templateOperation.getContext().put( "templateName", templateName ); //$NON-NLS-1$

        try
        {
            templateOperation.execute( new NullProgressMonitor() );
        }
        catch( Exception ex )
        {
            LayoutTplUI.logError( "Error getting template source.", ex ); //$NON-NLS-1$
        }

        return buffer.toString();
    }
}
