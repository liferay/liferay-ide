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

package com.liferay.ide.sdk.core;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.internal.core.XMLMemento;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public final class SDKManager
{

    private static SDKManager instance;

    public SDK getDefaultSDK()
    {
        for( SDK sdk : getSDKs() )
        {
            if( sdk.isDefault() )
            {
                return sdk;
            }
        }

        return null;
    }

    public static SDKManager getInstance()
    {
        if( instance == null )
        {
            instance = new SDKManager();
        }

        return instance;
    }

    public static Version getLeastValidVersion()
    {
        return ISDKConstants.LEAST_SUPPORTED_SDK_VERSION;
    }

    public SDK getSDK( IPath sdkLocation )
    {
        for( SDK sdk : getSDKs() )
        {
            if( sdk.getLocation().equals( sdkLocation ) )
            {
                return sdk;
            }
        }

        return null;
    }

    public void saveSDKs( SDK[] sdks )
    {
        setSDKs( sdks );
    }

    private boolean initialized = false;

    // current list of available sdks
    private ArrayList<SDK> sdkList;

    private SDKManager()
    {
        instance = this;
    }

    public void addSDK( SDK sdk )
    {
        if( !initialized )
        {
            initialize();
        }

        sdkList.add( sdk );

        if( sdkList.size() == 1 )
        {
            sdk.setDefault( true );
        }

        saveSDKs();
    }

    public SDK getSDK( String sdkName )
    {
        if( sdkName == null )
        {
            return null;
        }

        for( SDK sdk : getSDKs() )
        {
            if( sdkName.equals( sdk.getName() ) )
            {
                return sdk;
            }
        }

        return null;
    }

    public SDK[] getSDKs()
    {
        if( !initialized )
        {
            initialize();
        }

        return sdkList.toArray( new SDK[0] );
    }

    public void setSDKs( SDK[] sdks )
    {
        this.sdkList.clear();

        for( SDK sdk : sdks )
        {
            sdkList.add( sdk );
        }

        saveSDKs();
    }

    @SuppressWarnings( "deprecation" )
    private IEclipsePreferences getPrefs()
    {
        return new InstanceScope().getNode( SDKCorePlugin.PREFERENCE_ID );
    }

    private void initialize()
    {
        loadSDKs();

        initialized = true;
    }

    private void loadSDKs()
    {
        sdkList = new ArrayList<SDK>();

        IEclipsePreferences prefs = getPrefs();

        String sdksXmlString = prefs.get( "sdks" , null ); //$NON-NLS-1$

        if( !CoreUtil.isNullOrEmpty( sdksXmlString ) )
        {
            try
            {
                XMLMemento root =
                    XMLMemento.createReadRoot( new InputStreamReader( new ByteArrayInputStream(
                        sdksXmlString.getBytes( "UTF-8" ) ) ) ); //$NON-NLS-1$

                String defaultSDKName = root.getString( "default" ); //$NON-NLS-1$

                XMLMemento[] children = root.getChildren( "sdk" ); //$NON-NLS-1$

                SDK defaultSDK = null;

                for( XMLMemento sdkElement : children )
                {
                    SDK sdk = new SDK();
                    sdk.loadFromMemento( sdkElement );

                    boolean def = sdk.getName() != null && sdk.getName().equals( defaultSDKName ) && defaultSDK == null;

                    if( def )
                    {
                        sdk.setDefault( def );
                        defaultSDK = sdk;
                    }

                    sdkList.add( sdk );
                }
            }
            catch( Exception e )
            {
                SDKCorePlugin.logError( e );
            }
        }
    }

    private void saveSDKs()
    {
        XMLMemento root = XMLMemento.createWriteRoot( "sdks" ); //$NON-NLS-1$

        for( SDK sdk : sdkList )
        {
            XMLMemento child = root.createChild( "sdk" ); //$NON-NLS-1$

            sdk.saveToMemento( child );

            if( sdk.isDefault() )
            {
                root.putString( "default", sdk.getName() ); //$NON-NLS-1$
            }
        }

        try
        {
            StringWriter writer = new StringWriter();

            root.save( writer );

            getPrefs().put( "sdks", writer.toString() ); //$NON-NLS-1$
            getPrefs().flush();
        }
        catch( Exception e )
        {
            LiferayCore.logError( e );
        }
    }

    public boolean containsSDK( SDK theSDK )
    {
        if( theSDK != null && getSDKs().length > 0 )
        {
            for( SDK sdk : getSDKs() )
            {
                if( theSDK.equals( sdk ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

}
