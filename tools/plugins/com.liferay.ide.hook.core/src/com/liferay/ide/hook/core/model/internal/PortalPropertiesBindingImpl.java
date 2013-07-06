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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.PortalProperty;
import com.liferay.ide.properties.core.PortalPropertiesConfiguration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyBinding;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.ValuePropertyBinding;

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
        protected PropertyBinding createBinding( Property property )
        {
            if( property.definition() == PortalProperty.PROP_NAME )
            {
                return new ValuePropertyBinding()
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
            else if( property.definition() == PortalProperty.PROP_VALUE )
            {
                return new ValuePropertyBinding()
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
            if( PortalPropertiesBindingImpl.this.portalPropertiesConfiguration != null )
            {
                return PortalPropertiesBindingImpl.this.portalPropertiesConfiguration.getProperty( getName() );
            }

            return null;
        }

        public void setName( String name )
        {
            Object oldValue = getValue();

            if( PortalPropertiesBindingImpl.this.portalPropertiesConfiguration != null )
            {
                PortalPropertiesBindingImpl.this.portalPropertiesConfiguration.clearProperty( getName() );
            }

            this.nameValue.setName( name );
            setValue( oldValue );
        }

        public void setValue( Object value )
        {
            this.nameValue.setValue( value );

            if( PortalPropertiesBindingImpl.this.portalPropertiesConfiguration != null )
            {
                PortalPropertiesBindingImpl.this.portalPropertiesConfiguration.setProperty( getName(), value );
                flushProperties();
            }
        }

    }

    private PropertiesConfiguration portalPropertiesConfiguration;

    @Override
    protected Object insertUnderlyingObject( ElementType type, int position )
    {
        Object retval = null;

        if( type.equals( PortalProperty.TYPE ) )
        {
            retval = new NameValueObject();
            this.properties.add( (NameValueObject) retval );
        }

        return retval;
    }

    @Override
    protected Resource resource( Object obj )
    {
        return new PortalPropertyResource( this.property().element().resource(), (NameValueObject) obj );
    }

    protected void flushProperties()
    {
        StringWriter output = new StringWriter();

        try
        {
            this.portalPropertiesConfiguration.save( output );

            IFile propsFile = HookMethods.getPortalPropertiesFile( hook(), false );

            if( propsFile != null )
            {
                ByteArrayInputStream contents = new ByteArrayInputStream( output.toString().getBytes() );

                if( propsFile.exists() )
                {
                    propsFile.setContents( contents, IResource.FORCE, null );
                }
                else
                {
                    propsFile.create( contents, true, null );
                }

                propsFile.refreshLocal( IResource.DEPTH_ONE, null );
            }
        }
        catch( Exception e )
        {
            HookCore.logError( e );
        }

    }

    @Override
    public void init( Property property )
    {
        super.init( property );

        Listener listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                updateConfigurationForFile();
            }
        };

        hook().attach( listener, Hook.PROP_PORTAL_PROPERTIES_FILE.name() + "/*" ); //$NON-NLS-1$

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
        if( resource instanceof PortalPropertyResource )
        {
            PortalPropertyResource ppResource = (PortalPropertyResource) resource;
            this.properties.remove( ppResource.getNameValue() );
            this.portalPropertiesConfiguration.clearProperty( ppResource.getName() );
            flushProperties();
        }

        // this.element().notifyPropertyChangeListeners( this.property() );
    }

    @Override
    public ElementType type( Resource resource )
    {
        return PortalProperty.TYPE;
    }

    protected void updateConfigurationForFile()
    {
        IFile portalPropertiesFile = HookMethods.getPortalPropertiesFile( hook() );

        if( portalPropertiesFile != null && portalPropertiesFile.exists() )
        {
            try
            {
                this.portalPropertiesConfiguration = new PortalPropertiesConfiguration();
                InputStream is = portalPropertiesFile.getContents();
                this.portalPropertiesConfiguration.load( is );
                is.close();
            }
            catch( Exception e )
            {
                HookCore.logError( e );
            }

            Iterator<?> keys = this.portalPropertiesConfiguration.getKeys();

            while( keys.hasNext() )
            {
                String key = keys.next().toString();
                this.properties.add( new NameValueObject( key, this.portalPropertiesConfiguration.getProperty( key ) ) );
            }
        }
    }

}
