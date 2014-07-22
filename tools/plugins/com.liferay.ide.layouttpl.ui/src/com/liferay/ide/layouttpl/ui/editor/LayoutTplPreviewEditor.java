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
 * Contributors:
 *      Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.ui.editor;

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.ui.parts.LayoutTplEditPartFactory;
import com.liferay.ide.layouttpl.ui.parts.LayoutTplRootEditPart;

import java.util.EventObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;

/**
 * @author Greg Amerson
 * @author Cindy Li
 * @author Kuo Zhang
 * 
 */
public class LayoutTplPreviewEditor extends GraphicalEditor
{
    protected LayoutTplElement modelElement;

    public LayoutTplPreviewEditor( LayoutTplElement layoutTpl )
    {
        this.modelElement = layoutTpl;
        setEditDomain( new DefaultEditDomain( this ) );
    }

    @Override
    public void commandStackChanged( EventObject event )
    {
        // do nothing
    }

    @Override
    protected void configureGraphicalViewer()
    {
        super.configureGraphicalViewer();

        GraphicalViewer viewer = getGraphicalViewer();

        viewer.setEditPartFactory( new LayoutTplEditPartFactory() );
        viewer.setRootEditPart( new LayoutTplRootEditPart() );
        viewer.setKeyHandler( new GraphicalViewerKeyHandler( viewer ) );
    }

    @Override
    protected void createActions()
    {
        // no actions
    }

    protected void createGraphicalViewer( Composite parent )
    {
        GraphicalViewer viewer = new GraphicalViewerImpl();
        viewer.createControl( parent );
        setGraphicalViewer( viewer );
        configureGraphicalViewer();
        hookGraphicalViewer();
        initializeGraphicalViewer();
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public void doSave( IProgressMonitor monitor )
    {
        // do nothing
    }

    public void doSaveAs()
    {
        // do nothing
    }

    @Override
    public ActionRegistry getActionRegistry()
    {
        return super.getActionRegistry();
    }

    @Override
    public DefaultEditDomain getEditDomain()
    {
        return super.getEditDomain();
    }

    public LayoutTplElement getModelElement()
    {
        return modelElement;
    }

    @Override
    public SelectionSynchronizer getSelectionSynchronizer()
    {
        return super.getSelectionSynchronizer();
    }

    protected void initializeGraphicalViewer()
    {
        final GraphicalViewer viewer = getGraphicalViewer();
        viewer.setContents( getModelElement() ); // set the contents of this editor
        refreshViewer( viewer );
    }

    @Override
    public boolean isDirty()
    {
        return false;
    }

    public boolean isSaveAsAllowed()
    {
        return false;
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

    public void refreshVisualModel( LayoutTplElement layoutTpl )
    {
        final GraphicalViewer viewer = getGraphicalViewer();
        if( viewer != null )
        {
            viewer.setContents( layoutTpl );
            refreshViewer( viewer );
        }
    }

    protected void setInput( IEditorInput input )
    {
        super.setInput( input );

        setPartName( input.getName() );
    }

}
