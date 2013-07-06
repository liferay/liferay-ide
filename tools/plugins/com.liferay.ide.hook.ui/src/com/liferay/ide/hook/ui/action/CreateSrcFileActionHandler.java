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
 *    Kamesh Sampath - initial implementation
 *    Gregory Amerson - initial implementation review and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.hook.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler;
import org.eclipse.sapphire.ui.SapphirePropertyEditorCondition;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class CreateSrcFileActionHandler extends SapphirePropertyEditorActionHandler
{
    public static class Condition extends SapphirePropertyEditorCondition
    {
        @Override
        protected final boolean evaluate( final PropertyEditorPart part )
        {
            final Property property = part.property();
            final Element element = part.getModelElement();

            if( property.definition() instanceof ValueProperty && element != null && property.definition().isOfType( Path.class ) )
            {
                final ValidFileSystemResourceType typeAnnotation =
                    property.definition().getAnnotation( ValidFileSystemResourceType.class );

                if( typeAnnotation != null && typeAnnotation.value() == FileSystemResourceType.FILE )
                {
                    return true;
                }
            }

            return false;
        }
    }

    private Listener listener;
    private Element modelElement;

    private Property _property;

    @Override
    protected final boolean computeEnablementState()
    {
        boolean isEnabled = super.computeEnablementState();

        if( modelElement != null && isEnabled )
        {
            // check for existence of the file
            IFile srcFile = getSrcFile();

            if( srcFile != null && srcFile.exists() )
            {
                isEnabled = false;
            }
        }

        return isEnabled;
    }

    private IPath getDefaultSrcFolderPath()
    {
        IProject project = this.modelElement.adapt( IProject.class );

        IFolder[] folders = ProjectUtil.getSourceFolders( project );

        if( !CoreUtil.isNullOrEmpty( folders ) )
        {
            return folders[0].getFullPath();
        }

        return null;
    }

    private IFile getSrcFile()
    {
        IFile retval = null;

        if( _property.definition() instanceof ValueProperty )
        {
            ValueProperty valueProperty = (ValueProperty) _property.definition();
            final Value<Path> value = modelElement.property( valueProperty );

            if( value != null && !CoreUtil.isNullOrEmpty( value.text() ) )
            {
                final IPath defaultSrcFolderPath = getDefaultSrcFolderPath();

                if( defaultSrcFolderPath != null )
                {
                    final IPath filePath = defaultSrcFolderPath.append( value.text() );
                    retval = ResourcesPlugin.getWorkspace().getRoot().getFile( filePath );
                }
            }
        }

        return retval;
    }

    @Override
    public void init( final SapphireAction action, ActionHandlerDef def )
    {
        super.init( action, def );
        modelElement = getModelElement();
        _property = property();

        this.listener = new FilteredListener<PropertyEvent>()
        {
            @Override
            public void handleTypedEvent( final PropertyEvent event )
            {
                refreshEnablementState();
            }

        };

        modelElement.attach( this.listener, _property.name() );

        attach
        (
            new Listener()
            {
                @Override
                public void handle( Event event )
                {
                    if( event instanceof DisposeEvent )
                    {
                        getModelElement().detach( listener, _property.name() );
                    }
                }
            }
        );
    }

    @Override
    public Object run( SapphireRenderingContext context )
    {
        if( _property.definition() instanceof ValueProperty )
        {
            final IFile file = getSrcFile();

            try
            {
                if( !file.exists() )
                {
                    InputStream defaultContentStream = new ByteArrayInputStream( StringPool.EMPTY.getBytes() );

                    file.create( defaultContentStream, true, null );

                    try
                    {
                        file.refreshLocal( IResource.DEPTH_INFINITE, null );
                    }
                    catch( Exception e )
                    {
                        HookUI.logError( e );
                    }

                    modelElement.refresh();
                    // write the property again with the same value to force
                    // modelElement.write( valueProperty, value.getContent() );
                    refreshEnablementState();
                }
            }
            catch( Exception e )
            {
            }
        }

        return null;
    }
}
