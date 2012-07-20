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
 *    Kamesh Sampath - initial implementation
 *    Gregory Amerson - initial implementation review and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.hook.ui.action;

import com.liferay.ide.core.util.CoreUtil;
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
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler;
import org.eclipse.sapphire.ui.SapphirePropertyEditorCondition;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class CreateSrcFileActionHandler extends SapphirePropertyEditorActionHandler
{
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

                if( typeAnnotation != null && typeAnnotation.value() == FileSystemResourceType.FILE )
                {
                    return true;
                }
            }

            return false;
        }
    }
    
    private Listener listener;
    private IModelElement modelElement;

    private ModelProperty modelProperty;

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

        if( modelProperty instanceof ValueProperty )
        {
            ValueProperty valueProperty = (ValueProperty) modelProperty;
            final Value<Path> value = modelElement.read( valueProperty );
            
            if( value != null && !CoreUtil.isNullOrEmpty( value.getText() ) )
            {
                final IPath defaultSrcFolderPath = getDefaultSrcFolderPath();
                
                if( defaultSrcFolderPath != null )
                {
                    final IPath filePath = defaultSrcFolderPath.append( value.getText() );
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
        modelProperty = getProperty();
        
        this.listener = new FilteredListener<PropertyEvent>()
        {
            @Override
            public void handleTypedEvent( final PropertyEvent event )
            {
                refreshEnablementState();
            }

        };

        modelElement.attach( this.listener, modelProperty.getName() );

        attach
        ( 
            new Listener()
            {
                @Override
                public void handle( Event event )
                {
                    if( event instanceof DisposeEvent )
                    {
                        getModelElement().detach( listener, modelProperty.getName() );
                    }
                }
            }
        );
    }

    @Override
    public Object run( SapphireRenderingContext context )
    {
        if( modelProperty instanceof ValueProperty )
        {
            final IFile file = getSrcFile();

            try
            {
                if( !file.exists() )
                {
                    InputStream defaultContentStream = new ByteArrayInputStream( "".getBytes() );

                    file.create( defaultContentStream, true, null );
                    file.refreshLocal( IResource.DEPTH_INFINITE, null );

                    modelElement.refresh( true, true );
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
