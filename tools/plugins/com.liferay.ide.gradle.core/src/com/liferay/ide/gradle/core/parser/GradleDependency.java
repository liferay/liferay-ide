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

package com.liferay.ide.gradle.core.parser;

import java.util.Map;

/**
 * @author Lovett Li
 */
public class GradleDependency
{

    private String group;
    private String name;
    private String version;

    public GradleDependency( Map<String, String> dep )
    {
        setGroup( dep.get( "group" ) );
        setName( dep.get( "name" ) );
        setVersion( dep.get( "version" ) );
    }

    public GradleDependency( String group, String name, String version )
    {
        this.group = group;
        this.name = name;
        this.version = version;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( group == null ) ? 0 : group.hashCode() );
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = prime * result + ( ( version == null ) ? 0 : version.hashCode() );

        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        GradleDependency other = (GradleDependency) obj;
        if( group == null )
        {
            if( other.group != null )
                return false;
        }
        else if( !group.equals( other.group ) )
            return false;
        if( name == null )
        {
            if( other.name != null )
                return false;
        }
        else if( !name.equals( other.name ) )
            return false;
        if( version == null )
        {
            if( other.version != null )
                return false;
        }
        else if( !version.equals( other.version ) )
            return false;
        return true;
    }
}
