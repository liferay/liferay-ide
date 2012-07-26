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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.ui.HookUI;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.services.RelativePathService;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler;
import org.eclipse.sapphire.ui.SapphirePropertyEditorCondition;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;

/**
 * @author Gregory Amerson
 */
public class CreateDirectoryActionHandler extends SapphirePropertyEditorActionHandler
{

    public CreateDirectoryActionHandler()
    {
        super();
    }

    @Override
    public void init( SapphireAction action, ActionHandlerDef def )
    {
        super.init( action, def );

        final IModelElement element = getModelElement();
        final ValueProperty property = (ValueProperty) getProperty();

        final Listener listener = new FilteredListener<PropertyEvent>()
        {
            @Override
            public void handleTypedEvent( final PropertyEvent event )
            {
                refreshEnablementState();
            }
        };

        element.attach( listener, property.getName() );

        attach
        ( 
            new Listener()
            {
                @Override
                public void handle( final Event event )
                {
                    if( event instanceof DisposeEvent )
                    {
                        element.detach( listener, property.getName() );
                    }
                }
            }
        );
    }

    @Override
    protected boolean computeEnablementState()
    {
        boolean enabled = super.computeEnablementState();

        if( enabled )
        {
            @SuppressWarnings( "unchecked" )
            final Value<Path> value = (Value<Path>) getModelElement().read( getProperty() );
            final Path path = value.getContent();
            final Path absolutePath =
                getModelElement().service( getProperty(), RelativePathService.class ).convertToAbsolute( path );

            enabled = !absolutePath.toFile().exists();
        }

        return enabled;
    }

    @Override
    protected Object run( SapphireRenderingContext context )
    {
        try
        {
            final IModelElement element = getModelElement();
            final ModelProperty property = getProperty();
            final IProject project = element.adapt( IProject.class );

            final CustomJspDir customJspDir = (CustomJspDir) element;

            Path customJspDirValue = customJspDir.getValue().getContent( false );

            if( customJspDirValue == null )
            {
                customJspDirValue = customJspDir.getValue().getContent( true );
                customJspDir.setValue( customJspDirValue );
            }

            customJspDir.setValue( customJspDirValue );

            final Path absolutePath =
                element.service( property, RelativePathService.class ).convertToAbsolute( customJspDirValue );

            if( !absolutePath.toFile().exists() )
            {
                IFolder docroot = CoreUtil.getDocroot( project );

                IFolder customJspFolder =
                    docroot.getFolder( new org.eclipse.core.runtime.Path( customJspDirValue.toPortableString() ) );

                CoreUtil.makeFolders( customJspFolder );

                element.refresh();
                refreshEnablementState();
            }
        }
        catch( Exception e )
        {
            HookUI.logError( e );
        }

        return null;
    }

    public static class Condition extends SapphirePropertyEditorCondition
    {
        @Override
        protected final boolean evaluate( final PropertyEditorPart part )
        {
            final ModelProperty property = part.getProperty();
            final IModelElement element = part.getModelElement();

            if( property instanceof ValueProperty && element != null && property.isOfType( Path.class ) )
            {
                final ValidFileSystemResourceType typeAnnotation =
                    property.getAnnotation( ValidFileSystemResourceType.class );

                if( typeAnnotation != null && typeAnnotation.value() == FileSystemResourceType.FOLDER )
                {
                    return true;
                }
            }

            return false;
        }
    }
}
