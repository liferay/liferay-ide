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

package com.liferay.ide.eclipse.portlet.ui.editor.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;

import com.liferay.ide.eclipse.portlet.core.model.IPortletApp;
import com.liferay.ide.eclipse.portlet.core.model.internal.ResourceBundleRelativePathService;
import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class CreatePortletAppResourceBundleActionHandler extends AbstractResourceBundleActionHandler {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#init(org.eclipse.sapphire.ui.SapphireAction,
	 * org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef)
	 */
	@Override
	public void init( SapphireAction action, ISapphireActionHandlerDef def ) {
		super.init( action, def );
		final IModelElement element = getModelElement();
		final ModelProperty property = getProperty();
		this.listener = new ModelPropertyListener() {

			@Override
			public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event ) {
				refreshEnablementState();
			}
		};

		element.addListener( this.listener, property.getName() );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
	 */
	@Override
	protected Object run( SapphireRenderingContext context ) {
		final IModelElement element = getModelElement();
		final IProject project = element.adapt( IProject.class );
		final ModelProperty property = getProperty();
		final Value<Path> resourceBundle = element.read( (ValueProperty) property );
		final String packageName = resourceBundle.getText().substring( 0, resourceBundle.getText().lastIndexOf( "." ) );
		final String defaultRBFileName =
			PortletUtil.convertJavaToIoFileName(
				resourceBundle.getText(), ResourceBundleRelativePathService.RB_FILE_EXTENSION );

		final IFolder rbSourecFolder = getResourceBundleFolderLocation( project, defaultRBFileName );
		final IPath entryPath = rbSourecFolder.getLocation();
		if ( getModelElement() instanceof IPortletApp ) {
			List<IFile> missingRBFiles = new ArrayList<IFile>();
			final StringBuilder rbFileBuffer = new StringBuilder( "#Portlet Application Resource Bundle \n" );
			final IFile rbFile = wroot.getFileForLocation( entryPath.append( defaultRBFileName ) );
			missingRBFiles.add( rbFile );
			createFiles( context, project, packageName, missingRBFiles, rbFileBuffer );
			setEnabled( false );
			getModelElement().refresh( getProperty(), true );
		}
		return null;
	}

	/*
	 * @Override(non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#dispose()
	 */
	@Override
	public void dispose() {
		getModelElement().removeListener( this.listener, getProperty().getName() );
		super.dispose();
	}

}
