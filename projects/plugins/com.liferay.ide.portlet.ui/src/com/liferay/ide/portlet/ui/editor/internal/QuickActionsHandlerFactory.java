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
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
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
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class QuickActionsHandlerFactory extends SapphireActionHandlerFactory
{

    private String[] modelProperties;

    @Override
    public void init( SapphireAction action, ActionHandlerFactoryDef def )
    {
        super.init( action, def );
        String strModelElementNames = def.getParam( "MODEL_PROPERTIES" );
        if( strModelElementNames != null )
        {
            this.modelProperties = strModelElementNames.split( "," );
        }
        else
        {
            throw new IllegalStateException( Resources.bind( Resources.message, "MODEL_PROPERTIES" ) );
        }

    }

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
            String modelProperty = this.modelProperties[i];

            if( modelProperty != null && "Portlets".equalsIgnoreCase( modelProperty ) && isPartInLiferayProject() )
            {
                SapphireActionHandler handler = new CreateLiferayPortletActionHandler();
                handler.init( this.getAction(), null );
                handler.addImage( Portlet.TYPE.image() );
                handler.setLabel( getActionLabel( "Portlets" ) );

                listOfHandlers.add( handler );
            }
            else
            {
                listOfHandlers.add( new Handler( modelProperty ) );
            }
        }

        // System.out.println( "QuickActionsHandlerFactory.created" + listOfHandlers.size() + " handlers " );
        return listOfHandlers;
    }

    private boolean isPartInLiferayProject()
    {
        SapphireEditor editor = this.getPart().nearest( SapphireEditor.class );

        return editor != null && ProjectUtil.isLiferayProject( editor.getProject() );
    }

    /**
     * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
     */
    private static final class Handler extends SapphireActionHandler
    {

        private final String strModelProperty;

        public Handler( String modelProperty )
        {
            this.strModelProperty = modelProperty;
        }

        @Override
        public void init( SapphireAction action, ActionHandlerDef def )
        {
            super.init( action, def );
            final IModelElement rootModel = action.getPart().getModelElement();
            final ModelProperty modelProperty = rootModel.type().property( this.strModelProperty );

            String labelText = modelProperty.getLabel( false, CapitalizationType.FIRST_WORD_ONLY, true );
            String actionLabel = getActionLabel( labelText );
            setLabel( actionLabel );

            ModelElementType propModelElementType = modelProperty.getType();
            addImage( propModelElementType.image() );
        }

        /*
         * (non-Javadoc)
         * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
         */
        @Override
        protected Object run( SapphireRenderingContext context )
        {

            final IModelElement rootModel = context.getPart().getModelElement();
            final ModelProperty modelProperty = rootModel.type().property( this.strModelProperty );
            Object obj = rootModel.read( modelProperty );
            IModelElement mElement = null;

            if( obj instanceof ModelElementList<?> )
            {
                // System.out.println( "QuickActionsHandlerFactory.Handler.run()" + obj.getClass() );
                ModelElementList<?> list = (ModelElementList<?>) obj;
                mElement = list.insert();
            }
            else
            {
                throw new UnsupportedOperationException( Resources.bind(
                    Resources.unsuportedOperation, this.strModelProperty ) );
            }

            // Select the ndoe
            final MasterDetailsEditorPagePart page = getPart().nearest( MasterDetailsEditorPagePart.class );
            final MasterDetailsContentNode root = page.outline().getRoot();
            final MasterDetailsContentNode node = root.findNodeByModelElement( mElement );
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
        if( labelText.endsWith( "s" ) )
        {
            labelText = labelText.substring( 0, labelText.lastIndexOf( "s" ) );
        }

        if( labelText.equals( "Portlet" ) )
        {
            labelText += "...";
        }

        return labelText;
    }

    private static final class Resources extends NLS
    {

        public static String message;
        public static String unsuportedOperation;
        static
        {
            initializeMessages( QuickActionsHandlerFactory.class.getName(), Resources.class );
        }
    }

}
