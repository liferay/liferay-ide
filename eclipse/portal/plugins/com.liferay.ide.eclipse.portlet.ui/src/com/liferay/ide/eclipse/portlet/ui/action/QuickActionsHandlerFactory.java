/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt. Ltd., All rights reserved.
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
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerFactoryDef;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsContentNode;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPagePart;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class QuickActionsHandlerFactory extends SapphireActionHandlerFactory {

	private static final String ACTION_PREFIX = "Add new  ";
	private String[] modelProperties;

	@Override
	public void init( SapphireAction action, ISapphireActionHandlerFactoryDef def ) {
		super.init( action, def );
		String strModelElementNames = def.getParam( "MODEL_PROPERTIES" );
		if ( strModelElementNames != null ) {
			this.modelProperties = strModelElementNames.split( "," );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandlerFactory#create()
	 */
	@Override
	public List<SapphireActionHandler> create() {
		List<SapphireActionHandler> listOfHandlers = new ArrayList<SapphireActionHandler>();
		listOfHandlers.add( new CreateLiferayPortletActionHandler() );
		for ( int i = 0; i < modelProperties.length; i++ ) {
			String modelProperty = modelProperties[i];
			listOfHandlers.add( new Handler( modelProperty ) );
		}
		return listOfHandlers;
	}

	/**
	 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
	 */
	private static final class Handler extends SapphireActionHandler {

		private final String modelProperty;

		public Handler( String modelProperty ) {
			this.modelProperty = modelProperty;

		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
		 */
		@Override
		protected Object run( SapphireRenderingContext context ) {
			final IModelElement rootModel = context.getPart().getModelElement();
			final ModelProperty modelProperty = rootModel.getModelElementType().getProperty( this.modelProperty );
			ModelElementType elementType = modelProperty.getModelElementType();
			IModelElement mElement = elementType.instantiate();
			setLabel( ACTION_PREFIX + elementType.getLabel( false, CapitalizationType.FIRST_WORD_ONLY, true ) );
			// Select the node

			final MasterDetailsEditorPagePart page = getPart().nearest( MasterDetailsEditorPagePart.class );
			final MasterDetailsContentNode root = page.getContentOutline().getRoot();
			final MasterDetailsContentNode node = root.findNodeByModelElement( mElement );
			if ( node != null ) {
				node.select();
			}

			return mElement;
		}

	}

}
