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

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
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
import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;

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
		final String defaultRBFileName =
			PortletUtil.convertJavaToIoFileName(
				resourceBundle.getText(), ResourceBundleRelativePathService.RB_FILE_EXTENSION );

		final IPath entryPath = getResourceBundleFolderLocation( project, defaultRBFileName );
		if ( getModelElement() instanceof IPortletApp ) {
			final IRunnableWithProgress rbCreationProc = new IRunnableWithProgress() {

				public void run( final IProgressMonitor monitor ) throws InvocationTargetException,
					InterruptedException {
					monitor.beginTask( "Creating resource bundle " + defaultRBFileName, 2 );

					final StringBuilder rbFileBuffer = new StringBuilder( "#Portlet Application Resource Bundle \n" );
					monitor.worked( 1 );

					try {
						final IFile rbFile = wroot.getFileForLocation( entryPath.append( defaultRBFileName ) );
						rbFile.create( new ByteArrayInputStream( rbFileBuffer.toString().getBytes() ), true, monitor );
						getModelElement().refresh( getProperty(), true );
						// TODO: Actually this needs to be automatically done using worksapce resource chnage listener
						setEnabled( false );
						monitor.worked( 1 );
					}
					catch ( CoreException e ) {
						PortletUIPlugin.logError( e );
					}

				}
			};

			try {
				( new ProgressMonitorDialog( context.getShell() ) ).run( false, false, rbCreationProc );
			}
			catch ( InvocationTargetException e ) {
				PortletUIPlugin.logError( e );
			}
			catch ( InterruptedException e ) {
				PortletUIPlugin.logError( e );
			}
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
