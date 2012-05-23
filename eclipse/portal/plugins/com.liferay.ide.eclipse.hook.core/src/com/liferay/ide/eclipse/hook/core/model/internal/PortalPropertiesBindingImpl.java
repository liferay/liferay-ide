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
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.BindingImpl;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.ValueBindingImpl;


/**
 * @author Gregory Amerson
 */
public class PortalPropertiesBindingImpl extends HookListBindingImpl
{

    private List<NameValueObject> properties = new ArrayList<NameValueObject>();

	private class PortalPropertyResource extends Resource
	{
		private NameValueObject nameValue;

        public PortalPropertyResource( Resource parent, NameValueObject nameValue )
		{
			super( parent );

			this.nameValue = nameValue;
		}

        public NameValueObject getNameValue()
        {
            return this.nameValue;
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
			return this.nameValue.getName();
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

			this.nameValue.setName( name );
			setValue( oldValue );
		}

		public void setValue( Object value )
		{
            this.nameValue.setValue( value );
			if ( PortalPropertiesBindingImpl.this.portalPropertiesConfiguration != null )
			{
				PortalPropertiesBindingImpl.this.portalPropertiesConfiguration.setProperty( getName(), value );
				flushProperties();
			}
		}

	}

	private PropertiesConfiguration portalPropertiesConfiguration;

	@Override
	protected Object insertUnderlyingObject( ModelElementType type, int position )
	{
        Object retval = null;

		if ( type.equals( IPortalProperty.TYPE ) )
		{
            retval = new NameValueObject();
            this.properties.add((NameValueObject)retval);
		}

		return retval;
	}

	@Override
	protected Resource resource( Object obj )
	{
	    return new PortalPropertyResource( this.element().resource(), (NameValueObject)obj );
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

		Listener listener = new FilteredListener<PropertyContentEvent>()
        {
			@Override
			protected void handleTypedEvent( PropertyContentEvent event )
			{
				updateConfigurationForFile();
			}
		};

		hook().attach( listener, IHook.PROP_PORTAL_PROPERTIES_FILE.getName() + "/*" );

		updateConfigurationForFile();
	}

    @Override
	protected List<?> readUnderlyingList()
	{
		return properties;
	}

	@Override
	public void remove( Resource resource )
	{
		if ( resource instanceof PortalPropertyResource )
		{
			PortalPropertyResource ppResource = (PortalPropertyResource) resource;
            this.properties.remove( ppResource.getNameValue() );
			this.portalPropertiesConfiguration.clearProperty( ppResource.getName() );
			flushProperties();
		}

//		this.element().notifyPropertyChangeListeners( this.property() );
	}

//	@Override
//	@SuppressWarnings( "rawtypes" )
//	public void swap( Resource a, Resource b )
//	{
//		final PortalPropertyResource propA = a.adapt( PortalPropertyResource.class );
//		final PortalPropertyResource propB = b.adapt( PortalPropertyResource.class );
//
//		List<Object> keysList = new ArrayList<Object>();
//
//		Iterator keys = this.portalPropertiesConfiguration.getKeys();
//
//		while( keys.hasNext() )
//		{
//			keysList.add( keys.next() );
//		}
//
//		Map<Object, Object> properties = new HashMap<Object, Object>();
//
//		for( Object key : keysList )
//		{
//			properties.put( key, this.portalPropertiesConfiguration.getProperty( key.toString() ) );
//		}
//
//		Collections.sort( keysList, new Comparator<Object>()
//		{
//			public int compare( Object o1, Object o2 )
//			{
//				if( propA.name != null && propA.name.equals( o1 ) && propB.name != null && propB.name.equals( o2 ) )
//				{
//					return 1;
//				}
//				else if( propA.name != null && propA.name.equals( o2 ) && propB.name != null && propB.name.equals( o1 ) )
//				{
//					return 1;
//				}
//
//				return 0;
//			}
//		} );
//
//		this.portalPropertiesConfiguration.clear();
//
//		for( Object key : keysList )
//		{
//			this.portalPropertiesConfiguration.addProperty( key.toString(), properties.get( key.toString() ) );
//		}
//
//		flushProperties();
//	}

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

			Iterator<?> keys = this.portalPropertiesConfiguration.getKeys();

			while (keys.hasNext())
			{
                String key = keys.next().toString();
			    this.properties.add(new NameValueObject(key, this.portalPropertiesConfiguration.getProperty( key )));
			}
		}
	}

}
