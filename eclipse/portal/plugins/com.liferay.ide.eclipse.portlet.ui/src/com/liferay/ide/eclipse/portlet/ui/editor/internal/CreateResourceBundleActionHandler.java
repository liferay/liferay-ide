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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
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
import org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef;

import com.liferay.ide.eclipse.portlet.core.model.IPortlet;
import com.liferay.ide.eclipse.portlet.core.model.IPortletInfo;
import com.liferay.ide.eclipse.portlet.core.model.ISupportedLocales;
import com.liferay.ide.eclipse.portlet.core.model.internal.ResourceBundleRelativePathService;
import com.liferay.ide.eclipse.portlet.core.util.PortletUtil;
import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class CreateResourceBundleActionHandler extends SapphirePropertyEditorActionHandler {

	final IWorkspace workspace = ResourcesPlugin.getWorkspace();
	final IWorkspaceRoot wroot = workspace.getRoot();
	private ModelPropertyListener listener;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#init(org.eclipse.sapphire.ui.SapphireAction,
	 * org.eclipse.sapphire.ui.def.ISapphireActionHandlerDef)
	 */
	@Override
	public void init( SapphireAction action, ISapphireActionHandlerDef def ) {
		super.init( action, def );
		final IModelElement element = getModelElement();
		this.listener = new ModelPropertyListener() {

			@Override
			public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event ) {
				refreshEnablementState();
			}
		};

		element.addListener( this.listener, IPortlet.PROP_SUPPORTED_LOCALES.getName() );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#computeEnablementState()
	 */
	@Override
	protected boolean computeEnablementState() {
		boolean isEnabled = super.computeEnablementState();
		final IModelElement element = getModelElement();

		if ( element instanceof IPortlet ) {
			IPortlet portlet = (IPortlet) element;
			isEnabled = isEnabled && portlet.getSupportedLocales() != null && !portlet.getSupportedLocales().isEmpty();
			if ( portlet.getSupportedLocales() != null ) {

				isEnabled = isEnabled && !portlet.getSupportedLocales().validate().ok();
			}

		}
		return isEnabled;
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
		if ( getModelElement() instanceof IPortlet ) {
			final IPortlet portlet = (IPortlet) getModelElement();
			List<ISupportedLocales> supportedLocales = portlet.getSupportedLocales();
			for ( ISupportedLocales iSupportedLocale : supportedLocales ) {

				if ( iSupportedLocale != null ) {
					String locale = PortletUtil.localeString( iSupportedLocale.getSupportedLocale().getText() );
					final String localizedIOFileName =
						PortletUtil.convertJavaToIoFileName(
							resourceBundle.getText(), ResourceBundleRelativePathService.RB_FILE_EXTENSION, locale );

					if ( !getFileFromClasspath( project, localizedIOFileName ) ) {
						final IRunnableWithProgress rbCreationProc = new IRunnableWithProgress() {

							public void run( final IProgressMonitor monitor ) throws InvocationTargetException,
								InterruptedException {
								monitor.beginTask( "Creating resource bundle " + localizedIOFileName, 2 );
								IPortletInfo portletInfo = portlet.getPortletInfo();
								final StringBuilder rbFileBuffer = buildDefaultRBContent( portletInfo );
								monitor.worked( 1 );

								try {
									final IFile rbFile =
										wroot.getFileForLocation( entryPath.append( localizedIOFileName ) );
									rbFile.create(
										new ByteArrayInputStream( rbFileBuffer.toString().getBytes() ), true, monitor );
									getModelElement().nearest( IPortlet.class ).refresh(
										IPortlet.PROP_SUPPORTED_LOCALES, true, true );
									monitor.worked( 1 );
								}
								catch ( CoreException e ) {
									PortletUIPlugin.logError( e );
								}

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
								rbFileBuffer.append( ( portletInfo != null && portletInfo.getTitle() != null )
									? portletInfo.getTitle() : "" );
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
				}
			}
		}
		return null;
	}

	@Override
	public void dispose() {
		super.dispose();

		getModelElement().removeListener( this.listener, getProperty().getName() );
	}

	/**
	 * @param project
	 * @param ioFileName
	 * @return
	 */
	private boolean getFileFromClasspath( IProject project, String ioFileName ) {

		IClasspathEntry[] cpEntries = PortletUtil.getClasspathEntries( project );
		for ( IClasspathEntry iClasspathEntry : cpEntries ) {
			if ( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() ) {
				IPath entryPath = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
				entryPath = entryPath.append( ioFileName );
				IFile resourceBundleFile = wroot.getFileForLocation( entryPath );
				if ( resourceBundleFile != null && resourceBundleFile.exists() ) {
					return true;
				}
				else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * @param project
	 * @param ioFileName
	 * @return
	 */
	private IPath getResourceBundleFolderLocation( IProject project, String ioFileName ) {

		IClasspathEntry[] cpEntries = PortletUtil.getClasspathEntries( project );
		for ( IClasspathEntry iClasspathEntry : cpEntries ) {
			if ( IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind() ) {
				IPath srcFolder = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
				IPath entryPath = wroot.getFolder( iClasspathEntry.getPath() ).getLocation();
				entryPath = entryPath.append( ioFileName );
				IFile resourceBundleFile = wroot.getFileForLocation( entryPath );
				if ( resourceBundleFile != null ) {
					return srcFolder;
				}
			}
		}
		return null;
	}
}
