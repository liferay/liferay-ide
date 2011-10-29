/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.hook.ui.actions.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;

import com.liferay.ide.eclipse.core.util.FileUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class CreateFileActionHandler extends SapphirePropertyEditorActionHandler {

	IModelElement modelElement;
	ModelProperty modelProperty;
	IPath docRootFolder;
	ModelPropertyListener listener;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
	 */
	@Override
	public Object run( SapphireRenderingContext context ) {

		if ( modelProperty instanceof ValueProperty ) {
			ValueProperty valueProperty = (ValueProperty) modelProperty;
			final Value<Path> value = modelElement.read( valueProperty );
			final IPath filePath = docRootFolder.append( value.getText() );
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation( filePath );
			try {
				InputStream defaultContentStream = getPortalPropsDefaultContent();
				if ( !file.exists() ) {
					file.create( defaultContentStream, true, null );
					modelElement.refresh( true, true );
				}
			}
			catch ( Exception e ) {
				// LOG it
			}

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#init(org.eclipse.sapphire.ui.SapphireAction,
	 * org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef)
	 */
	@Override
	public void init( final SapphireAction action, ISapphireActionHandlerDef def ) {
		super.init( action, def );
		modelElement = getModelElement();
		modelProperty = getProperty();
		this.listener = new ModelPropertyListener() {

			@Override
			public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event ) {
				refreshEnablementState();
			}

		};

		modelElement.addListener( this.listener, modelProperty.getName() );

		attach( new Listener() {

			@Override
			public void handle( Event event ) {
				if ( event instanceof DisposeEvent ) {
					getModelElement().removeListener( listener, modelProperty.getName() );
				}
			}

		} );

		IProject project = modelElement.adapt( IProject.class );
		docRootFolder = project.getLocation().append( ISDKConstants.DEFAULT_WEBCONTENT_FOLDER );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#computeEnablementState()
	 */
	@Override
	protected final boolean computeEnablementState() {
		boolean isEnbled = super.computeEnablementState();
		if ( modelElement != null ) {
			boolean validationStatus = !modelElement.validate().ok();
			isEnbled = isEnbled && validationStatus;
		}
		return isEnbled;
	}

	/**
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private InputStream getPortalPropsDefaultContent() throws URISyntaxException, IOException {
		URL defaultContentUrl =
			new URL(
				"platform:/plugin/com.liferay.ide.eclipse.hook.ui/default-content-files/PortalPropertiesDefaultContent.properties" );
		InputStream in = defaultContentUrl.openStream();
		String buf = FileUtil.readContents( in );
		in.close();
		return new ByteArrayInputStream( buf.getBytes() );
	}
}
