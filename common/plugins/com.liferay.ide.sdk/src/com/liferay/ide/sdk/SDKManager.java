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
 *******************************************************************************/

package com.liferay.ide.sdk;

import com.liferay.ide.core.CorePlugin;
import com.liferay.ide.core.util.CoreUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
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

        return sdkList.toArray( new SDK[sdkList.size()] );
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

    private ScopedPreferenceStore getPrefStore()
    {
        return (ScopedPreferenceStore) SDKPlugin.getDefault().getPreferenceStore();
    }

    private void initialize()
    {
        loadSDKs();

        initialized = true;
    }

    private void loadSDKs()
    {
        sdkList = new ArrayList<SDK>();

        IPreferenceStore prefs = getPrefStore();

        String sdksXmlString = prefs.getString( "sdks" );

        if( !CoreUtil.isNullOrEmpty( sdksXmlString ) )
        {
            try
            {
                XMLMemento root =
                    XMLMemento.createReadRoot( new InputStreamReader( new ByteArrayInputStream(
                        sdksXmlString.getBytes( "UTF-8" ) ) ) );

                String defaultSDKName = root.getString( "default" );

                IMemento[] children = root.getChildren( "sdk" );

                for( IMemento sdkElement : children )
                {
                    SDK sdk = new SDK();
                    sdk.loadFromMemento( sdkElement );

                    boolean defaultSDK = sdk.getName() != null && sdk.getName().equals( defaultSDKName );

                    sdk.setDefault( defaultSDK );

                    sdkList.add( sdk );
                }
            }
            catch( Exception e )
            {
                SDKPlugin.logError( e );
            }
        }
    }

    private void saveSDKs()
    {
        XMLMemento root = XMLMemento.createWriteRoot( "sdks" );

        for( SDK sdk : sdkList )
        {
            IMemento child = root.createChild( "sdk" );

            sdk.saveToMemento( child );

            if( sdk.isDefault() )
            {
                root.putString( "default", sdk.getName() );
            }
        }

        StringWriter writer = new StringWriter();
        try
        {
            root.save( writer );

            getPrefStore().setValue( "sdks", writer.toString() );
            getPrefStore().save();
        }
        catch( IOException e )
        {
            CorePlugin.logError( e );
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
