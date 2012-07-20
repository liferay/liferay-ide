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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class CustomJspValidationService extends ValidationService
{

    private IPath portalDir;

    private IPath getPortalDir()
    {
        if( this.portalDir == null )
        {
            try
            {
                final IModelElement element = context().find( IModelElement.class );
                final IProject project = element.nearest( Hook.class ).adapt( IFile.class ).getProject();
                ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project );
                this.portalDir = liferayRuntime.getPortalDir();
            }
            catch( Exception e )
            {
                HookCore.logError( e );
            }
        }

        return this.portalDir;
    }

    private boolean isValidPortalJsp( Value<?> value )
    {
        String customJsp = value.getContent().toString();

        IPath customJspPath = getPortalDir().append( customJsp );

        if( customJspPath.toFile().exists() )
        {
            return true;
        }

        return false;
    }

    private boolean isValidProjectJsp( Value<?> value )
    {
        String customJsp = value.getContent().toString();

        IFolder customFolder = getCustomJspFolder();

        if( customFolder != null && customFolder.exists() )
        {
            IFile customJspFile = customFolder.getFile( customJsp );

            if( customJspFile.exists() )
            {
                return true;
            }
        }

        return false;
    }

    private IFolder getCustomJspFolder()
    {
        CustomJspDir element = this.hook().getCustomJspDir().element();
        IFolder docroot = CoreUtil.getDocroot( project() );

        if( element != null && docroot != null )
        {
            Path customJspDir = element.getValue().getContent();
            IFolder customJspFolder = docroot.getFolder( customJspDir.toPortableString() );
            return customJspFolder;
        }
        else
        {
            return null;
        }
    }

    protected Hook hook()
    {
        return this.context().find( Hook.class );
    }

    protected IProject project()
    {
        return this.hook().adapt( IProject.class );
    }

    private boolean isValueEmpty( Value<?> value )
    {
        return value.getContent( false ) == null;
    }

    @Override
    public Status validate()
    {
        final Value<?> value = (Value<?>) context( IModelElement.class ).read( context( ModelProperty.class ) );
        final ValueProperty property = value.getProperty();
        final String label = property.getLabel( true, CapitalizationType.NO_CAPS, false );

        if( isValueEmpty( value ) )
        {
            final String msg = NLS.bind( "Non-empty value for {0} required. ", label );
            return Status.createErrorStatus( msg );
        }
        else if( !isValidPortalJsp( value ) && !isValidProjectJsp( value ) )
        {
            final String msg = NLS.bind( "Invalid path {0} for custom jsp. Path does not exist in portal. ", label );
            return Status.createErrorStatus( msg );
        }

        return Status.createOkStatus();
    }

}
