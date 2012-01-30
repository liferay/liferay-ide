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

package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.HookCore;
import com.liferay.ide.eclipse.hook.core.model.IHook;
import com.liferay.ide.eclipse.hook.core.model.IPortalPropertiesFile;
import com.liferay.ide.eclipse.hook.core.model.IPortalProperty;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.properties.core.PortalPropertiesConfiguration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.sapphire.modeling.BindingImpl;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.ValueBindingImpl;


/**
 * @author Gregory Amerson
 */
public class PortalPropertiesBindingImpl extends HookListBindingImpl
{

	private class PortalPropertyResource extends Resource
	{
		private String name;

		public PortalPropertyResource( Resource parent )
		{
			super( parent );
		}

		@Override
		protected BindingImpl createBinding( ModelProperty property )
		{
			if ( property == IPortalProperty.PROP_NAME )
			{
				return new ValueBindingImpl()
				{

					@Override
					public String read()
					{
						return getName();
					}

					@Override
					public void write( final String value )
					{
						setName( value );
					}
				};
			}
			else if ( property == IPortalProperty.PROP_VALUE )
			{
				return new ValueBindingImpl()
				{

					@Override
					public String read()
					{
						return getValue() != null ? getValue().toString() : null;
					}

					@Override
					public void write( final String value )
					{
						setValue( value );
					}
				};
			}

			return null;
		}

		public String getName()
		{
			return name;
		}

		public Object getValue()
		{
			if ( PortalPropertiesBindingImpl.this.portalPropertiesConfiguration != null )
			{
				return PortalPropertiesBindingImpl.this.portalPropertiesConfiguration.getProperty( getName() );
			}

			return null;
		}

		public void setName( String name )
		{
			Object oldValue = getValue();

			if ( PortalPropertiesBindingImpl.this.portalPropertiesConfiguration != null )
			{
				PortalPropertiesBindingImpl.this.portalPropertiesConfiguration.clearProperty( getName() );
			}

			this.name = name;
			setValue( oldValue );
		}

		public void setValue( Object value )
		{
			if ( PortalPropertiesBindingImpl.this.portalPropertiesConfiguration != null )
			{
				PortalPropertiesBindingImpl.this.portalPropertiesConfiguration.setProperty( getName(), value );
				flushProperties();
			}
		}

	}

	protected PropertiesConfiguration portalPropertiesConfiguration;

	@Override
	public Resource add( ModelElementType type )
	{
		if ( type.equals( IPortalProperty.TYPE ) )
		{
			PortalPropertyResource newResource = createResource();

			newResource.setName( "" );
			newResource.setValue( "" );

			this.element().notifyPropertyChangeListeners( this.property() );

			return newResource;
		}
		else
		{
			return null;
		}
	}

	protected PortalPropertyResource createResource()
	{
		return new PortalPropertyResource( this.element().resource() );
	}

	protected void flushProperties()
	{
		StringWriter output = new StringWriter();

		try
		{
			this.portalPropertiesConfiguration.save( output );
			IFile propsFile = getPortalPropertiesFile();

			if ( propsFile != null && propsFile.exists() )
			{
				propsFile.setContents( new ByteArrayInputStream( output.toString().getBytes() ), IResource.FORCE, null );
			}
		}
		catch ( Exception e )
		{
			HookCore.logError( e );
		}

	}

	protected IFile getPortalPropertiesFile()
	{
		IPortalPropertiesFile portalPropertiesFileElement = getPortalPropertiesFileElement();

		if ( portalPropertiesFileElement != null )
		{
			Path filePath = portalPropertiesFileElement.getValue().getContent();

			for ( IFolder folder : ProjectUtil.getSourceFolders( project() ) )
			{
				IFile file = folder.getFile( filePath.toPortableString() );

				if ( file.exists() )
				{
					return file;
				}
			}
		}

		return null;
	}

	protected IPortalPropertiesFile getPortalPropertiesFileElement()
	{
		return hook().getPortalPropertiesFile().element();
	}

	@Override
	public void init( IModelElement element, ModelProperty property, String[] params )
	{
		super.init( element, property, params );

		ModelPropertyListener listener = new ModelPropertyListener()
		{

			@Override
			public void handlePropertyChangedEvent( ModelPropertyChangeEvent event )
			{
				updateConfigurationForFile();
			}
		};

		hook().addListener( listener, IHook.PROP_PORTAL_PROPERTIES_FILE.getName() );

		updateConfigurationForFile();
	}

	@SuppressWarnings( "rawtypes" )
	@Override
	public List<Resource> read()
	{
		List<Resource> properties = new ArrayList<Resource>();

		if ( this.portalPropertiesConfiguration != null && !this.portalPropertiesConfiguration.isEmpty() )
		{
			Iterator keys = this.portalPropertiesConfiguration.getKeys();

			while ( keys.hasNext() )
			{
				String key = keys.next().toString();

				Object val = this.portalPropertiesConfiguration.getString( key );
				PortalPropertyResource resource = this.createResource();

				resource.setName( key );
				resource.setValue( val );

				properties.add( resource );
			}
		}

		return properties;
	}

	@Override
	public void remove( Resource resource )
	{
		if ( resource instanceof PortalPropertyResource )
		{
			PortalPropertyResource ppResource = (PortalPropertyResource) resource;
			this.portalPropertiesConfiguration.clearProperty( ppResource.getName() );
			flushProperties();
		}

		this.element().notifyPropertyChangeListeners( this.property() );
	}

	@Override
	public ModelElementType type( Resource resource )
	{
		return IPortalProperty.TYPE;
	}

	protected void updateConfigurationForFile()
	{
		IFile portalPropertiesFile = getPortalPropertiesFile();

		if ( portalPropertiesFile != null && portalPropertiesFile.exists() )
		{
			try
			{
				this.portalPropertiesConfiguration = new PortalPropertiesConfiguration();
				InputStream is = portalPropertiesFile.getContents();
				this.portalPropertiesConfiguration.load( is );
				is.close();
			}
			catch ( Exception e )
			{
				HookCore.logError( e );
			}
		}
	}

}
