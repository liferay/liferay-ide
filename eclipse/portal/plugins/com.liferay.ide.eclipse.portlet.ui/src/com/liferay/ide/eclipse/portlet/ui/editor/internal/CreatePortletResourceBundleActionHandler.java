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
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.editor.internal;

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.core.model.IPortletInfo;
import com.liferay.ide.eclipse.portlet.core.model.ISupportedLocales;
import com.liferay.ide.eclipse.portlet.core.model.internal.ResourceBundleRelativePathService;
import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.PropertyEvent;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class CreatePortletResourceBundleActionHandler extends AbstractResourceBundleActionHandler {

	Listener localePropListener;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#init(org.eclipse.sapphire.ui.SapphireAction,
	 * org.eclipse.sapphire.ui.def.ActionHandlerDef)
	 */
	@Override
	public void init( SapphireAction action, ActionHandlerDef def ) {
		super.init( action, def );
		final IModelElement element = getModelElement();

		this.listener = new FilteredListener<PropertyEvent>() {

			@Override
			protected void handleTypedEvent( final PropertyEvent event ) {

				refreshEnablementState();
			}
		};

		localePropListener = new FilteredListener<PropertyEvent>() {

			@Override
			protected void handleTypedEvent( final PropertyEvent event ) {
				refreshEnablementState();
			}
		};
		element.attach( listener, getProperty().getName() );
		element.attach( localePropListener, IPortlet.PROP_SUPPORTED_LOCALES.getName() );

		attach( new Listener() {

			@Override
			public void handle( Event event ) {
				if ( event instanceof DisposeEvent ) {
					getModelElement().detach( listener, getProperty().getName() );
					getModelElement().detach( localePropListener, IPortlet.PROP_SUPPORTED_LOCALES.getName() );
				}
			}

		} );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#computeEnablementState()
	 */
	@Override
	protected boolean computeEnablementState() {

		boolean isEnabled = super.computeEnablementState();
		final IModelElement element = getModelElement();
		IPortlet portlet = (IPortlet) element;
		if ( portlet.getSupportedLocales() != null && !portlet.getSupportedLocales().isEmpty() ) {
			isEnabled = !portlet.getSupportedLocales().validation().ok();
		}

		return isEnabled;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
	 */
	@Override
	protected Object run( SapphireRenderingContext context ) {
		final List<IFile> missingRBFiles = new ArrayList<IFile>();
		final IPortlet portlet = (IPortlet) getModelElement();
		final IProject project = portlet.adapt( IProject.class );
		Value<Path> resourceBundle = portlet.getResourceBundle();
		final String text = resourceBundle.getText();
        
        String defaultRBFileName =
			PortletUtil.convertJavaToIoFileName(
				text, ResourceBundleRelativePathService.RB_FILE_EXTENSION );

		int index = text.lastIndexOf( "." );
        
		if (index == -1)
		{
		    index = text.length();
		}
		
		final String packageName = text.substring( 0, index );

		final IFolder rbSourceFolder = getResourceBundleFolderLocation( project, defaultRBFileName );
		final IPath entryPath = rbSourceFolder.getLocation();

		IPortletInfo portletInfo = portlet.getPortletInfo();
		final StringBuilder rbFileBuffer = buildDefaultRBContent( portletInfo );

		// Create the default Resource Bundle if it does not exist
		if ( !getFileFromClasspath( project, defaultRBFileName ) ) {
			final IFile drbFile = wroot.getFileForLocation( entryPath.append( defaultRBFileName ) );
			missingRBFiles.add( drbFile );
		}

		// Create bundles for each supported locale for which the resource bundle is missing
		List<ISupportedLocales> supportedLocales = portlet.getSupportedLocales();

		for ( ISupportedLocales iSupportedLocale : supportedLocales ) {

			if ( iSupportedLocale != null ) {
				String locale = PortletUtil.localeString( iSupportedLocale.getSupportedLocale().getText() );
				final String localizedIOFileName =
					PortletUtil.convertJavaToIoFileName(
						text, ResourceBundleRelativePathService.RB_FILE_EXTENSION, locale );

				if ( !getFileFromClasspath( project, localizedIOFileName ) ) {
					final IFile rbFile = wroot.getFileForLocation( entryPath.append( localizedIOFileName ) );
					missingRBFiles.add( rbFile );
				}
			}
		}

		createFiles( context, project, packageName, missingRBFiles, rbFileBuffer );
		setEnabled( false );
		getModelElement().refresh( getProperty(), true );
		getModelElement().refresh( IPortlet.PROP_SUPPORTED_LOCALES, true, true );

		return null;
	}

	/**
	 * @param portletInfo
	 * @return
	 */
	private StringBuilder buildDefaultRBContent( IPortletInfo portletInfo ) {
		final StringBuilder rbFileBuffer = new StringBuilder();
		rbFileBuffer.append( "#Portlet Information\n" );
		rbFileBuffer.append( "javax.portlet.title" );
		rbFileBuffer.append( "=" );
		rbFileBuffer.append( ( portletInfo != null && portletInfo.getTitle() != null ) ? portletInfo.getTitle() : "" );
		rbFileBuffer.append( "\n" );
		rbFileBuffer.append( "javax.portlet.short-title" );
		rbFileBuffer.append( "=" );
		rbFileBuffer.append( ( portletInfo != null && portletInfo.getShortTitle() != null )
			? portletInfo.getShortTitle() : "" );
		rbFileBuffer.append( "\n" );
		rbFileBuffer.append( "javax.portlet.keywords" );
		rbFileBuffer.append( "=" );
		rbFileBuffer.append( ( portletInfo != null && portletInfo.getKeywords() != null )
			? portletInfo.getKeywords() : "" );
		rbFileBuffer.append( "\n" );
		rbFileBuffer.append( "#Other Properties" );
		rbFileBuffer.append( "\n" );
		return rbFileBuffer;
	}

}
