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
 *    Gregory Amerson - IDE-355
 ******************************************************************************/

package com.liferay.ide.eclipse.hook.ui.action;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphirePropertyEditor;
import org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler;
import org.eclipse.sapphire.ui.SapphirePropertyEditorCondition;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class CreateSrcFileActionHandler extends SapphirePropertyEditorActionHandler {

	IModelElement modelElement;
	ModelProperty modelProperty;
	ModelPropertyListener listener;

	@Override
	public Object run( SapphireRenderingContext context ) {

		if ( modelProperty instanceof ValueProperty ) {
			ValueProperty valueProperty = (ValueProperty) modelProperty;
			final Value<Path> value = modelElement.read( valueProperty );
			final IPath filePath = getDefaultSrcFolderPath().append( value.getText() );
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile( filePath );
			try {
				InputStream defaultContentStream = new ByteArrayInputStream( "".getBytes() );
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

	private IPath getDefaultSrcFolderPath()
	{
		IProject project = this.modelElement.adapt( IProject.class );

		IFolder[] folders = ProjectUtil.getSourceFolders( project );

		if ( !CoreUtil.isNullOrEmpty( folders ) )
		{
			return folders[0].getFullPath();
		}

		return null;
	}

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


	public static class Condition extends SapphirePropertyEditorCondition
	{

		@Override
		protected final boolean evaluate( final SapphirePropertyEditor part )
		{
			final ModelProperty property = part.getProperty();
			final IModelElement element = part.getModelElement();

			if ( property instanceof ValueProperty && element != null && property.isOfType( Path.class ) )
			{
				final ValidFileSystemResourceType typeAnnotation =
					property.getAnnotation( ValidFileSystemResourceType.class );

				if ( typeAnnotation != null && typeAnnotation.value() == FileSystemResourceType.FILE )
				{
					return true;
				}
			}

			return false;
		}

	}
}
