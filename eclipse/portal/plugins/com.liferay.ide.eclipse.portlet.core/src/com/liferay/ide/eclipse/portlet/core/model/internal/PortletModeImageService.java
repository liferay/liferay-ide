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
 *               Kamesh Sampath - initial implementation
 *               Gregory Amerson - ongoing maintenance 
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model.internal;

import com.liferay.ide.eclipse.portlet.core.model.ICustomPortletMode;
import com.liferay.ide.eclipse.portlet.core.model.IPortletMode;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.services.ImageService;
import org.eclipse.sapphire.services.ImageServiceData;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class PortletModeImageService extends ImageService
{

	private static final ImageData IMG_DEFAULT = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/portlet.png" );

	private static final ImageData IMG_VIEW = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/view.png" );

	private static final ImageData IMG_EDIT = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/edit.png" );

	private static final ImageData IMG_HELP = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/help.png" );

	private ModelPropertyListener listener;

	@Override
	protected void initImageService()
	{
		this.listener = new ModelPropertyListener()
		{

			@Override
			public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
			{
				refresh();
			}
		};

		context( IModelElement.class ).addListener( this.listener, IPortletMode.PROP_PORTLET_MODE.getName() );
	}

	@Override
	protected ImageServiceData compute()
	{
		String portletMode = null;
		IModelElement element = context( IModelElement.class );
		ImageData imageData = null;

		if ( element instanceof ICustomPortletMode )
		{
			ICustomPortletMode iCustomPortletMode = (ICustomPortletMode) element;
			portletMode = String.valueOf( iCustomPortletMode.getPortletMode().getContent() );
		}
		else if ( element instanceof IPortletMode )
		{
			IPortletMode iPortletMode = (IPortletMode) element;
			portletMode = iPortletMode.getPortletMode().getContent();
		}

		if ( portletMode != null )
		{
			if ( "VIEW".equalsIgnoreCase( portletMode ) )
			{
				imageData = IMG_VIEW;
			}
			else if ( "EDIT".equalsIgnoreCase( portletMode ) )
			{
				imageData = IMG_EDIT;
			}
			else if ( "HELP".equalsIgnoreCase( portletMode ) )
			{
				imageData = IMG_HELP;
			}

		}

		if ( imageData == null )
		{
			imageData = IMG_DEFAULT;
		}

		return new ImageServiceData( imageData );
	}

	@Override
	public void dispose()
	{
		super.dispose();

		context( IModelElement.class ).removeListener( this.listener, IPortletMode.PROP_PORTLET_MODE.getName() );
	}

}
