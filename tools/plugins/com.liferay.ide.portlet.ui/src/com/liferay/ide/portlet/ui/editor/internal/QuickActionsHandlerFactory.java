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
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.ui.editor.internal;

import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.def.ActionHandlerFactoryDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPagePart;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class QuickActionsHandlerFactory extends SapphireActionHandlerFactory
{

    private static final class Handler extends SapphireActionHandler
    {
        private final String strProperty;

        public Handler( String Property )
        {
            this.strProperty = Property;
        }

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            final Element rootModel = action.getPart().getModelElement();
            PropertyDef _property = rootModel.type().property( this.strProperty );

            String labelText = _property.getLabel( false, CapitalizationType.FIRST_WORD_ONLY, true );
            String actionLabel = getActionLabel( labelText );
            setLabel( actionLabel );

            ElementType propModelElementType = _property.getType();
            addImage( propModelElementType.image() );
        }

        /*
         * (non-Javadoc)
         * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
         */
        @Override
        protected Object run( SapphireRenderingContext context )
        {
            final Element rootModel = context.getPart().getModelElement();
            final PropertyDef _property = rootModel.type().property( this.strProperty );
            final Object obj = rootModel.property( _property );
            Element mElement = null;

            if( obj instanceof ElementList<?> )
            {
                ElementList<?> list = (ElementList<?>) obj;
                mElement = list.insert();
            }
            else
            {
                throw new UnsupportedOperationException( Msgs.bind( Msgs.unsuportedOperation, this.strProperty ) );
            }

            // Select the ndoe
            final MasterDetailsEditorPagePart page = getPart().nearest( MasterDetailsEditorPagePart.class );
            final MasterDetailsContentNode root = page.outline().getRoot();
            final MasterDetailsContentNode node = root.findNode( mElement );

            if( node != null )
            {
                node.select();
            }

            return mElement;
        }
    }

    /**
     * This is make a compact and singular label text
     */
    private static String getActionLabel( String labelText )
    {
        if( labelText.endsWith( "s" ) ) //$NON-NLS-1$
        {
            labelText = labelText.substring( 0, labelText.lastIndexOf( "s" ) ); //$NON-NLS-1$
        }

        if( labelText.equals( Msgs.portlet ) )
        {
            labelText += "..."; //$NON-NLS-1$
        }

        return labelText;
    }

    private String[] modelProperties;

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphireActionHandlerFactory#create()
     */
    @Override
    public List<SapphireActionHandler> create()
    {
        List<SapphireActionHandler> listOfHandlers = new ArrayList<SapphireActionHandler>();

        for( int i = 0; i < this.modelProperties.length; i++ )
        {
            String Property = this.modelProperties[i];

            if( Property != null && "Portlets".equalsIgnoreCase( Property ) && isPartInLiferayProject() ) //$NON-NLS-1$
            {
                SapphireActionHandler handler = new CreateLiferayPortletActionHandler();
                handler.init( this.getAction(), null );
                handler.addImage( Portlet.TYPE.image() );
                handler.setLabel( getActionLabel( Msgs.portlets ) );

                listOfHandlers.add( handler );
            }
            else
            {
                listOfHandlers.add( new Handler( Property ) );
            }
        }

        // System.out.println( "QuickActionsHandlerFactory.created" + listOfHandlers.size() + " handlers " );
        return listOfHandlers;
    }

    @Override
    public void init( SapphireAction action, ActionHandlerFactoryDef def )
    {
        super.init( action, def );

        String strModelElementNames = def.getParam( "MODEL_PROPERTIES" ); //$NON-NLS-1$

        if( strModelElementNames != null )
        {
            this.modelProperties = strModelElementNames.split( "," ); //$NON-NLS-1$
        }
        else
        {
            throw new IllegalStateException( NLS.bind( Msgs.message, "MODEL_PROPERTIES" ) ); //$NON-NLS-1$
        }

    }

    private boolean isPartInLiferayProject()
    {
        SapphireEditor editor = this.getPart().nearest( SapphireEditor.class );

        return editor != null && ProjectUtil.isLiferayFacetedProject( editor.getProject() );
    }

    private static class Msgs extends NLS
    {
        public static String message;
        public static String portlet;
        public static String portlets;
        public static String unsuportedOperation;

        static
        {
            initializeMessages( QuickActionsHandlerFactory.class.getName(), Msgs.class );
        }
    }
}
