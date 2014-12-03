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

import javax.management.openmbean.CompositeData;

import org.osgi.framework.Version;


/**
 * @author Gregory Amerson
 */
public class OsgiBundle
{
    private final String id;
    private final String symbolicName;
    private final String state;
    private final Version version;

    public OsgiBundle( String id, String symbolicName, String state, Version version )
    {
        this.id = id;
        this.symbolicName = symbolicName;
        this.state = state;
        this.version = version;
    }

    public String getState()
    {
        return this.state;
    }

    public Version getVersion()
    {
        return this.version == null ? new Version("0.0.0") : this.version;
    }

    public String getId()
    {
        return this.id;
    }


    public String getSymbolicName()
    {
        return this.symbolicName;
    }

    public static OsgiBundle newFromData( CompositeData cd )
    {
        return new OsgiBundle
        (
            cd.get( "Identifier" ).toString(),
            cd.get( "SymbolicName" ).toString(),
            cd.get( "State" ).toString(),
            new Version( cd.get( "Version" ).toString() )
        );
    }

}
