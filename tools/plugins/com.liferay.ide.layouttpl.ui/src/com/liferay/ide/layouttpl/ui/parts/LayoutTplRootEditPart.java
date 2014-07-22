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

package com.liferay.ide.layouttpl.ui.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.SimpleRootEditPart;
import org.eclipse.gef.tools.MarqueeDragTracker;

/**
 * @author Gregory Amerson
 */
public class LayoutTplRootEditPart extends SimpleRootEditPart implements LayerConstants, LayerManager
{

    class FeedbackLayer extends Layer
    {

        FeedbackLayer()
        {
            setEnabled( false );
        }

        /**
         * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
         */
        public Dimension getPreferredSize( int wHint, int hHint )
        {
            Rectangle rect = new Rectangle();
            for( int i = 0; i < getChildren().size(); i++ )
            {
                rect.union( ( (IFigure) getChildren().get( i ) ).getBounds() );
            }
            return rect.getSize();
        }

    }

    private LayeredPane layers;

    public LayoutTplRootEditPart()
    {
        super();
    }

    protected IFigure createFigure()
    {
        layers = new LayeredPane();
        createLayers( layers );

        return layers;
    }

    protected void createLayers( LayeredPane layeredPane )
    {
        // layeredPane.add(getScaledLayers(), SCALABLE_LAYERS);
        // layeredPane.add(getPrintableLayers(), PRINTABLE_LAYERS);
        Layer layer = new Layer();
        layer.setLayoutManager( new StackLayout() );
        layeredPane.add( layer, PRIMARY_LAYER );

        layeredPane.add( new Layer()
        {

            public Dimension getPreferredSize( int wHint, int hHint )
            {
                return new Dimension();
            }
        }, HANDLE_LAYER );
        layeredPane.add( new FeedbackLayer(), FEEDBACK_LAYER );
    }

    public IFigure getContentPane()
    {
        return getLayer( PRIMARY_LAYER );
    }

    /**
     * Should not be called, but returns a MarqeeDragTracker for good measure.
     * 
     * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
     */
    public DragTracker getDragTracker( Request req )
    {
        /*
         * The root will only be asked for a drag tracker if for some reason the contents editpart says it is neither
         * selector nor opaque.
         */
        return new MarqueeDragTracker();
    }

    /**
     * Returns the layer indicated by the key. Searches all layered panes.
     * 
     * @see LayerManager#getLayer(Object)
     */
    public IFigure getLayer( Object key )
    {
        // if (innerLayers == null)
        // return null;
        // IFigure layer = printableLayers.getLayer(key);
        // if (layer != null)
        // return layer;

        return layers.getLayer( key );
    }

    /**
     * The root editpart does not have a real model. The LayerManager ID is returned so that this editpart gets
     * registered using that key.
     * 
     * @see org.eclipse.gef.EditPart#getModel()
     */
    public Object getModel()
    {
        return LayerManager.ID;
    }

}
