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

import com.liferay.ide.core.model.internal.ResourceBundleRelativePathService;
import com.liferay.ide.portlet.core.model.PortletApp;
import com.liferay.ide.portlet.core.util.PortletUtil;

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
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.PropertyEvent;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class CreatePortletAppResourceBundleActionHandler extends AbstractResourceBundleActionHandler
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#init(org.eclipse.sapphire.ui.SapphireAction,
     * org.eclipse.sapphire.ui.def.ActionHandlerDef)
     */
    @Override
    public void init( SapphireAction action, ActionHandlerDef def )
    {
        super.init( action, def );
        final IModelElement element = getModelElement();
        final ModelProperty property = getProperty();
        this.listener = new FilteredListener<PropertyEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyEvent event )
            {
                refreshEnablementState();
            }
        };

        element.attach( this.listener, property.getName() );

        attach
        ( 
            new Listener()
            {
                @Override
                public void handle( Event event )
                {
                    if( event instanceof DisposeEvent )
                    {
                        getModelElement().detach( listener, getProperty().getName() );
                    }
                }
            } 
        );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.ui.SapphireActionHandler#run(org.eclipse.sapphire.ui.SapphireRenderingContext)
     */
    @Override
    protected Object run( SapphireRenderingContext context )
    {
        final IModelElement element = getModelElement();
        final IProject project = element.adapt( IProject.class );
        final ModelProperty property = getProperty();
        final Value<Path> resourceBundle = element.read( (ValueProperty) property );
        final String resourceBundleText = resourceBundle.getText();

        int index = resourceBundleText.lastIndexOf( "." );

        if( index == -1 )
        {
            index = resourceBundleText.length();
        }

        final String packageName = resourceBundleText.substring( 0, index );

        final String defaultRBFileName =
            PortletUtil.convertJavaToIoFileName(
                resourceBundleText, ResourceBundleRelativePathService.RB_FILE_EXTENSION );

        final IFolder rbSourecFolder = getResourceBundleFolderLocation( project, defaultRBFileName );
        final IPath entryPath = rbSourecFolder.getLocation();
        if( getModelElement() instanceof PortletApp )
        {
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

}
