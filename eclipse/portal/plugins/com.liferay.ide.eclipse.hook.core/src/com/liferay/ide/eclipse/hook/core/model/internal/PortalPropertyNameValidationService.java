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

package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.HookCore;
import com.liferay.ide.eclipse.hook.core.model.IHook;
import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.util.NLS;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 */
public class PortalPropertyNameValidationService extends ValidationService
{

	private IPath portalDir;

	private IPath getPortalDir()
	{
		if ( this.portalDir == null )
		{
			try
			{
				final IModelElement element = context().find( IModelElement.class );
				final IProject project = element.nearest( IHook.class ).adapt( IFile.class ).getProject();
				ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project );
				this.portalDir = liferayRuntime.getPortalDir();
			}
			catch ( Exception e )
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

		if ( customJspPath.toFile().exists() )
		{
			return true;
		}

		return false;
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

		if ( isValueEmpty( value ) )
		{
			final String msg = NLS.bind( "Non-empty value for {0} required. ", label );
			return Status.createErrorStatus( msg );
		}
		else if ( !isValidPortalJsp( value ) )
		{
			final String msg = NLS.bind( "Invalid path {0} for custom jsp. Path does not exist in portal. ", label );
			return Status.createErrorStatus( msg );
		}

		return Status.createOkStatus();
	}

}
