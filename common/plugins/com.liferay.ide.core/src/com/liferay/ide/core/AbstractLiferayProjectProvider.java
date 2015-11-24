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
package com.liferay.ide.core;

import java.util.Collections;
import java.util.List;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractLiferayProjectProvider
    implements ILiferayProjectProvider, Comparable<ILiferayProjectProvider>
{
    private Class<?>[] classTypes;
    private String displayName;
    private boolean isDefault;
    private int priority;
    private String shortName;
    private String projectType;

    public AbstractLiferayProjectProvider( Class<?>[] types )
    {
        this.classTypes = types;
    }

    public int compareTo( ILiferayProjectProvider provider )
    {
        if( provider != null )
        {
            return this.shortName.compareTo( provider.getShortName() );
        }

        return 0;
    }

    public <T> List<T> getData( String key, Class<T> type, Object... params )
    {
        return Collections.emptyList();
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public String getShortName()
    {
        return this.shortName;
    }

    public String getProjectType()
    {
        return this.projectType;
    }

    public boolean isDefault()
    {
        return this.isDefault;
    }

    public boolean provides( Class<?> type )
    {
        if( type != null )
        {
            for( Class<?> classType : classTypes )
            {
                if( classType.isAssignableFrom( type ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    public void setDefault( boolean isDefault )
    {
        this.isDefault = isDefault;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public void setPriority( int priority )
    {
        this.priority = priority;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public void setProjectType( String type )
    {
        this.projectType = type;
    }
}
