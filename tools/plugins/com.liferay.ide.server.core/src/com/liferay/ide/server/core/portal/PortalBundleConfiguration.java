/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.ide.server.core.portal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Document;

import com.liferay.ide.core.properties.PortalPropertiesConfiguration;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.LiferayServerCore;

/**
 * @author Simon Jiang
 */

public abstract class PortalBundleConfiguration implements IPortalBundleConfiguration
{

    protected final static String FRAMEWORK_OSGI_CONSOLE_NAME = "module.framework.properties.osgi.console";
    protected final static String PORTAL_EXT_PROPERTIES = "portal-ext.properties";
    protected PortalBundle bundle;
    public static final String NAME_PROPERTY = "name";
    public static final String PORT_PROPERTY = "port";
    public static final String MODIFY_PORT_PROPERTY = "modifyPort";
    protected IFolder configPath;
    protected boolean isServerDirty;
    protected Document configurationDocument;

    protected PortalPropertiesConfiguration portalExtProperties;

    // property change listeners
    private transient List<PropertyChangeListener> propertyListeners;

    public PortalBundleConfiguration( PortalBundle bundle )
    {
        this.bundle = bundle;
    }

    protected void firePropertyChangeEvent( String propertyName, Object oldValue, Object newValue )
    {
        if( propertyListeners == null )
        {
            return;
        }

        PropertyChangeEvent event = new PropertyChangeEvent( this, propertyName, oldValue, newValue );
        try
        {
            Iterator<PropertyChangeListener> iterator = propertyListeners.iterator();

            while( iterator.hasNext() )
            {
                try
                {
                    PropertyChangeListener listener = (PropertyChangeListener) iterator.next();
                    listener.propertyChange( event );
                }
                catch( Exception e )
                {
                }
            }
        }
        catch( Exception e )
        {
        }
    }

    /**
     * Adds a property change listener to this server.
     *
     * @param listener
     *            java.beans.PropertyChangeListener
     */
    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        if( propertyListeners == null )
        {
            propertyListeners = new ArrayList<PropertyChangeListener>();
        }
        propertyListeners.add( listener );
    }

    /**
     * Removes a property change listener from this server.
     *
     * @param listener
     *            java.beans.PropertyChangeListener
     */
    public void removePropertyChangeListener( PropertyChangeListener listener )
    {
        if( propertyListeners != null )
        {
            propertyListeners.remove( listener );
        }
    }

    public abstract void applyBundleChange( LiferayServerPort port ) throws CoreException;

    public abstract void loadBundleConfiguration( IProgressMonitor monitor ) throws CoreException;

    public abstract void saveBundleConfiguration( IProgressMonitor monitor ) throws CoreException;

    public void applyChange( LiferayServerPort port )
    {
        try
        {
            if( port.getStoreLocation().equals( LiferayServerPort.defayltStoreInProperties ) )
            {
                if( !portalExtProperties.containsKey( FRAMEWORK_OSGI_CONSOLE_NAME ) )
                {
                    portalExtProperties.addProperty( FRAMEWORK_OSGI_CONSOLE_NAME, "localhost:" + port.getPort() );
                }
                else
                {
                    portalExtProperties.setProperty( FRAMEWORK_OSGI_CONSOLE_NAME, "localhost:" + port.getPort() );
                }
            }
            applyBundleChange( port );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
    }

    public void save( IProgressMonitor monitor )
    {
        try
        {
            if( isServerDirty )
            {
                File portalExtPropertiesFile = bundle.getLiferayHome().append( PORTAL_EXT_PROPERTIES ).toFile();

                if( !portalExtPropertiesFile.exists() )
                {
                    portalExtPropertiesFile.createNewFile();
                }

                portalExtProperties.save( portalExtPropertiesFile );
            }

            saveBundleConfiguration( monitor );

            isServerDirty = false;
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
    }

	public void load( IProgressMonitor monitor )
    {
        try
        {
            IPath portalExtPropertiesPath = bundle.getLiferayHome().append( PORTAL_EXT_PROPERTIES );

            if( portalExtPropertiesPath.toFile().exists() )
            {
                try(FileInputStream sream = new FileInputStream( portalExtPropertiesPath.toFile() ))
                {
                    portalExtProperties = new PortalPropertiesConfiguration();
                    portalExtProperties.load( sream );
                }
            }
            else
            {
                portalExtProperties = new PortalPropertiesConfiguration();
            }

            loadBundleConfiguration( monitor );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
    }
	
    /**
     * Return a string representation of this object.
     *
     * @return java.lang.String
     */
    public String toString()
    {
        return getClass().getName().toString();
    }

    public void importFromPath( IProgressMonitor monitor ) throws CoreException
    {
        load( monitor );
    }

    /**
     * Modify the port with the given id.
     *
     * @param id
     *            java.lang.String
     * @param port
     *            int
     */
    public void modifyServerPort( String id, int port )
    {
        try
        {
            isServerDirty = true;
            firePropertyChangeEvent( MODIFY_PORT_PROPERTY, id, new Integer( port ) );
        }
        catch( Exception e )
        {
            LiferayServerCore.logError( e );
        }
    }

    public LiferayServerPort createLiferayServerPort( final String id, final String portName, final String portValue )
    {
        String retVal = null;

        if( !CoreUtil.empty( portValue ) )
        {
            if( portValue.lastIndexOf( ":" ) == -1 )
            {
                retVal = portValue;
            }
            else
            {
                retVal = portValue.substring( portValue.lastIndexOf( ":" ) + 1, portValue.length() - 1 );
            }
        }
        return new LiferayServerPort(
            id, StringUtils.capitalize( StringUtils.replace( portName, "-", " " ) ), Integer.parseInt( retVal ),
            StringUtils.capitalize( portName ) );
    }

    @Override
    public int getTelnetPort()
    {
        String retVal = "11311";

        if( portalExtProperties.containsKey( FRAMEWORK_OSGI_CONSOLE_NAME ) )
        {
            String telnetPort = ( (String) portalExtProperties.getProperty( FRAMEWORK_OSGI_CONSOLE_NAME ) ).replaceAll(
                "localhost:", "" ).trim();

            if( !CoreUtil.empty( telnetPort ) )
            {
                retVal = telnetPort;
            }
        }

        return Integer.parseInt( retVal );
    }
}
