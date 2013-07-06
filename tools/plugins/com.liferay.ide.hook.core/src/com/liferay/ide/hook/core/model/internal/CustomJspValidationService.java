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
 *******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.util.HookUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Status;
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
                final Element element = context().find( Element.class );
                final IProject project = element.nearest( Hook.class ).adapt( IFile.class ).getProject();
                final ILiferayProject liferayProject = LiferayCore.create( project );

                if( liferayProject != null )
                {
                    this.portalDir = liferayProject.getAppServerPortalDir();
                }
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
        String customJsp = value.content().toString();

        IPath customJspPath = getPortalDir().append( customJsp );

        if( customJspPath.toFile().exists() )
        {
            return true;
        }

        return false;
    }

    private boolean isValidProjectJsp( Value<?> value )
    {
        String customJsp = value.content().toString();

        IFolder customFolder = HookUtil.getCustomJspFolder( hook(), project() );

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
        return value.content( false ) == null;
    }

    @Override
    public Status validate()
    {
        final Value<?> value = (Value<?>) context( Element.class ).property( context( Property.class ).definition() );
        final ValueProperty property = value.definition();
        final String label = property.getLabel( true, CapitalizationType.NO_CAPS, false );

        if( isValueEmpty( value ) )
        {
            final String msg = NLS.bind( Msgs.nonEmptyValueRequired, label );
            return Status.createErrorStatus( msg );
        }
        else if( !isValidPortalJsp( value ) && !isValidProjectJsp( value ) )
        {
            final String msg = NLS.bind( Msgs.customJspInvalidPath, label );
            return Status.createErrorStatus( msg );
        }

        return Status.createOkStatus();
    }

    private static class Msgs extends NLS
    {
        public static String customJspInvalidPath;
        public static String nonEmptyValueRequired;

        static
        {
            initializeMessages( CustomJspValidationService.class.getName(), Msgs.class );
        }
    }
}
