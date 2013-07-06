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

import com.liferay.ide.core.model.internal.GenericResourceBundlePathService;
import com.liferay.ide.portlet.core.model.Portlet;
import com.liferay.ide.portlet.core.model.PortletInfo;
import com.liferay.ide.portlet.core.model.SupportedLocales;
import com.liferay.ide.portlet.core.util.PortletUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class CreatePortletResourceBundleActionHandler extends AbstractResourceBundleActionHandler
{

    Listener localePropListener;

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#init(org.eclipse.sapphire.ui.SapphireAction,
     * org.eclipse.sapphire.ui.def.ActionHandlerDef)
     */
    @Override
    public void init( SapphireAction action, ActionHandlerDef def )
    {
        super.init( action, def );
        final Element element = getModelElement();

        this.listener = new FilteredListener<PropertyEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyEvent event )
            {
                refreshEnablementState();
            }
        };

        localePropListener = new FilteredListener<PropertyEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyEvent event )
            {
                refreshEnablementState();
            }
        };

        element.attach( listener, property().name() );
        element.attach( localePropListener, Portlet.PROP_SUPPORTED_LOCALES.name() );

        attach
        (
            new Listener()
            {
                @Override
                public void handle( Event event )
                {
                    if( event instanceof DisposeEvent )
                    {
                        getModelElement().detach( listener, property().name() );
                        getModelElement().detach( localePropListener, Portlet.PROP_SUPPORTED_LOCALES.name() );
                    }
                }
            }
        );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#computeEnablementState()
     */
    @Override
    protected boolean computeEnablementState()
    {
        boolean isEnabled = super.computeEnablementState();
        final Element element = getModelElement();
        Portlet portlet = (Portlet) element;
        if( portlet.getSupportedLocales() != null && !portlet.getSupportedLocales().isEmpty() )
        {
            isEnabled = !portlet.getSupportedLocales().validation().ok();
        }

        return isEnabled;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
     */
    @Override
    protected Object run( SapphireRenderingContext context )
    {
        final List<IFile> missingRBFiles = new ArrayList<IFile>();
        final Portlet portlet = (Portlet) getModelElement();
        final IProject project = portlet.adapt( IProject.class );
        Value<Path> resourceBundle = portlet.getResourceBundle();
        final String text = resourceBundle.text();

        String defaultRBFileName =
            PortletUtil.convertJavaToIoFileName( text, GenericResourceBundlePathService.RB_FILE_EXTENSION );

        int index = text.lastIndexOf( "." ); //$NON-NLS-1$

        if( index == -1 )
        {
            index = text.length();
        }

        final String packageName = text.substring( 0, index );

        final IFolder rbSourceFolder = getResourceBundleFolderLocation( project, defaultRBFileName );
        final IPath entryPath = rbSourceFolder.getLocation();

        PortletInfo portletInfo = portlet.getPortletInfo();
        final StringBuilder rbFileBuffer = buildDefaultRBContent( portletInfo );

        // Create the default Resource Bundle if it does not exist
        if( !getFileFromClasspath( project, defaultRBFileName ) )
        {
            final IFile drbFile = wroot.getFileForLocation( entryPath.append( defaultRBFileName ) );
            missingRBFiles.add( drbFile );
        }

        // Create bundles for each supported locale for which the resource bundle is missing
        List<SupportedLocales> supportedLocales = portlet.getSupportedLocales();

        for( SupportedLocales iSupportedLocale : supportedLocales )
        {
            if( iSupportedLocale != null )
            {
                String locale = PortletUtil.localeString( iSupportedLocale.getSupportedLocale().text() );
                final String localizedIOFileName =
                    PortletUtil.convertJavaToIoFileName(
                        text, GenericResourceBundlePathService.RB_FILE_EXTENSION, locale );

                if( !getFileFromClasspath( project, localizedIOFileName ) )
                {
                    final IFile rbFile = wroot.getFileForLocation( entryPath.append( localizedIOFileName ) );
                    missingRBFiles.add( rbFile );
                }
            }
        }

        createFiles( context, project, packageName, missingRBFiles, rbFileBuffer );
        setEnabled( false );
        getModelElement().property( property().definition() ).refresh();
        getModelElement().property( Portlet.PROP_SUPPORTED_LOCALES ).refresh();

        return null;
    }

    /**
     * @param portletInfo
     * @return
     */
    private StringBuilder buildDefaultRBContent( PortletInfo portletInfo )
    {
        final StringBuilder rbFileBuffer = new StringBuilder();
        rbFileBuffer.append( "#Portlet Information\n" ); //$NON-NLS-1$
        rbFileBuffer.append( "javax.portlet.title" ); //$NON-NLS-1$
        rbFileBuffer.append( "=" ); //$NON-NLS-1$
        rbFileBuffer.append( ( portletInfo != null && portletInfo.getTitle() != null ) ? portletInfo.getTitle() : "" ); //$NON-NLS-1$
        rbFileBuffer.append( "\n" ); //$NON-NLS-1$
        rbFileBuffer.append( "javax.portlet.short-title" ); //$NON-NLS-1$
        rbFileBuffer.append( "=" ); //$NON-NLS-1$
        rbFileBuffer.append( ( portletInfo != null && portletInfo.getShortTitle() != null )
            ? portletInfo.getShortTitle() : "" ); //$NON-NLS-1$
        rbFileBuffer.append( "\n" ); //$NON-NLS-1$
        rbFileBuffer.append( "javax.portlet.keywords" ); //$NON-NLS-1$
        rbFileBuffer.append( "=" ); //$NON-NLS-1$
        rbFileBuffer.append( ( portletInfo != null && portletInfo.getKeywords() != null )
            ? portletInfo.getKeywords() : "" ); //$NON-NLS-1$
        rbFileBuffer.append( "\n" ); //$NON-NLS-1$
        rbFileBuffer.append( "#Other Properties" ); //$NON-NLS-1$
        rbFileBuffer.append( "\n" ); //$NON-NLS-1$
        return rbFileBuffer;
    }

}
